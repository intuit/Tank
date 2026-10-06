package com.intuit.tank.vmManager.environment.amazon;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.InstanceDescription;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static com.intuit.tank.vmManager.environment.amazon.AmazonInstance.INSUFFICIENT_INSTANCE_CAPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmazonInstanceTest {

    @Mock
    private Ec2AsyncClient _mockEc2AsyncClient;

    AmazonInstance amazonInstance;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        amazonInstance = new AmazonInstance(_mockEc2AsyncClient, VMRegion.STANDALONE);
    }

    @Test
    public void constructorTest() {
        AmazonInstance amazonInstance = new AmazonInstance(VMRegion.STANDALONE);
    }

    @Test
    public void createTest_SINGLE() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().state(InstanceState.builder().build()).build()).build()));

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(1, vmInfo.size());
        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(1)).runInstances(argumentCaptor.capture());
        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:1]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }

    @Test
    public void createTest_SUCCESS() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 8, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(List.of(
                                Instance.builder().state(InstanceState.builder().build()).build(),
                                Instance.builder().state(InstanceState.builder().build()).build(),
                                Instance.builder().state(InstanceState.builder().build()).build())).build()));

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(9, vmInfo.size());
        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(3)).runInstances(argumentCaptor.capture());

        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:3, m8g.xlarge:3, m8g.xlarge:2]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }

    @Test
    public void createTest_ALTERNATIVE() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 8, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.failedFuture(new CompletionException(Ec2Exception.builder()
                        .awsErrorDetails(AwsErrorDetails.builder().errorCode(INSUFFICIENT_INSTANCE_CAPACITY).build())
                        .build())))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(List.of(
                                Instance.builder().state(InstanceState.builder().build()).build(),
                                Instance.builder().state(InstanceState.builder().build()).build(),
                                Instance.builder().state(InstanceState.builder().build()).build())).build()));

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(9, vmInfo.size());
        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(4)).runInstances(argumentCaptor.capture());
        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:3, m7g.xlarge:3, m8g.xlarge:3, m8g.xlarge:2]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }

    @Test
    public void createTest_PARTIAL() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 8, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().state(InstanceState.builder().build()).build()).build()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(List.of(
                                Instance.builder().state(InstanceState.builder().build()).build(),
                                Instance.builder().state(InstanceState.builder().build()).build(),
                                Instance.builder().state(InstanceState.builder().build()).build())).build()));

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(10, vmInfo.size());
        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(4)).runInstances(argumentCaptor.capture());
        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:3, m7g.xlarge:2, m8g.xlarge:3, m8g.xlarge:2]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }

    @Test
    public void createTest_PARTIAL_keepsInstancesFromFirstResponse() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 8, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().instanceId("i-partial").state(InstanceState.builder().build()).build()).build()))
                .thenAnswer(invocation -> {
                    RunInstancesRequest request = invocation.getArgument(0);
                    List<Instance> instances = java.util.stream.IntStream.range(0, request.maxCount())
                            .mapToObj(i -> Instance.builder().state(InstanceState.builder().build()).build())
                            .toList();
                    return CompletableFuture.completedFuture(RunInstancesResponse.builder().instances(instances).build());
                });

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(8, vmInfo.size());
        assertEquals(1, vmInfo.stream().filter(info -> "i-partial".equals(info.getInstanceId())).count());
    }

    @Test
    public void findRunningAgents_queriesByControllerTagAndCarriesJobIdAndLaunchTime() {
        java.time.Instant launchTime = java.time.Instant.parse("2026-10-05T16:00:00Z");
        Instance instance = Instance.builder()
                .instanceId("i-agent")
                .privateIpAddress("10.0.0.5")
                .launchTime(launchTime)
                .state(InstanceState.builder().name(InstanceStateName.RUNNING).build())
                .tags(Tag.builder().key("Controller").value("tank-prod").build(),
                        Tag.builder().key("JobId").value("16156").build())
                .build();
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(DescribeInstancesResponse.builder()
                        .reservations(Reservation.builder().instances(instance).build()).build()));

        List<VMInformation> agents = amazonInstance.findRunningAgents("tank-prod");

        assertEquals(1, agents.size());
        assertEquals("i-agent", agents.get(0).getInstanceId());
        assertEquals("16156", agents.get(0).getJobId());
        assertEquals("10.0.0.5", agents.get(0).getPrivateIp());
        assertEquals(launchTime.toEpochMilli(), agents.get(0).getLaunchTime().getTimeInMillis());

        ArgumentCaptor<DescribeInstancesRequest> captor = ArgumentCaptor.forClass(DescribeInstancesRequest.class);
        verify(_mockEc2AsyncClient).describeInstances(captor.capture());
        assertEquals("[tag:Controller=[tank-prod], tag-key=[JobId], instance-state-name=[running]]",
                captor.getValue().filters().stream().map(f -> f.name() + "=" + f.values()).toList().toString());
    }

    @Test
    public void createTest_ICE_FAILURE() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 23, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.failedFuture(new CompletionException(Ec2Exception.builder()
                        .awsErrorDetails(AwsErrorDetails.builder().errorCode(INSUFFICIENT_INSTANCE_CAPACITY).build())
                        .build())));

        AWSXRay.beginSegment("TEST");
        assertThrows(RuntimeException.class, () -> amazonInstance.create(vmRequest));
        AWSXRay.endSegment();

        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(6)).runInstances(argumentCaptor.capture());
        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:8, m7g.xlarge:8, m8g.xlarge:8, m7g.xlarge:8, m8g.xlarge:7, m7g.xlarge:7]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }

    @Test
    public void createTest_OTHER_FAILURE() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 23, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.failedFuture(new CompletionException(Ec2Exception.builder().build())));

        AWSXRay.beginSegment("TEST");
        assertThrows(RuntimeException.class, () -> amazonInstance.create(vmRequest));
        AWSXRay.endSegment();

        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(3)).runInstances(argumentCaptor.capture());
        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:8, m8g.xlarge:8, m8g.xlarge:7]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }
}
