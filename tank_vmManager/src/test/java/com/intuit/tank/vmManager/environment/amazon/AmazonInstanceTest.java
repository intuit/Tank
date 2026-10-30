package com.intuit.tank.vmManager.environment.amazon;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Entity;
import com.amazonaws.xray.entities.Subsegment;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.InstanceDescription;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.SsmClientBuilder;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.intuit.tank.vmManager.environment.amazon.AmazonInstance.INSUFFICIENT_INSTANCE_CAPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
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

    @Test
    public void createTest_doesNotWaitToDescribeInstances() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().instanceId("i-1").state(InstanceState.builder().build()).build()).build()));

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(1, vmInfo.size());
        verify(_mockEc2AsyncClient, never()).describeInstances(any(DescribeInstancesRequest.class));
    }

    @Test
    public void describeInstances_requestsOnlyTheGivenIds() {
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(DescribeInstancesResponse.builder()
                        .reservations(Reservation.builder().instances(Instance.builder().instanceId("i-1")
                                .publicIpAddress("54.0.0.1").state(InstanceState.builder().build()).build()).build()).build()));

        List<VMInformation> vmInfo = amazonInstance.describeInstances(List.of("i-1"));

        assertEquals(1, vmInfo.size());
        assertEquals("54.0.0.1", vmInfo.getFirst().getPublicIp());
        ArgumentCaptor<DescribeInstancesRequest> captor = ArgumentCaptor.forClass(DescribeInstancesRequest.class);
        verify(_mockEc2AsyncClient).describeInstances(captor.capture());
        assertEquals(List.of("i-1"), captor.getValue().instanceIds());
    }

    @Test
    public void createTest_ALTERNATIVE_keepsTraceEntityOnFallbackRequest() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, false, "testZone", instanceDescription);
        List<Entity> traceEntities = new CopyOnWriteArrayList<>();
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenAnswer(invocation -> {
                    traceEntities.add(AWSXRay.getTraceEntity());
                    // Fail on another thread, the way the SDK completes futures, so the fallback runs off the caller thread
                    return CompletableFuture.supplyAsync(() -> {
                        throw new CompletionException(Ec2Exception.builder()
                                .awsErrorDetails(AwsErrorDetails.builder().errorCode(INSUFFICIENT_INSTANCE_CAPACITY).build())
                                .build());
                    }, CompletableFuture.delayedExecutor(50, java.util.concurrent.TimeUnit.MILLISECONDS));
                })
                .thenAnswer(invocation -> {
                    traceEntities.add(AWSXRay.getTraceEntity());
                    return CompletableFuture.completedFuture(RunInstancesResponse.builder()
                            .instances(Instance.builder().state(InstanceState.builder().build()).build()).build());
                });

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(1, vmInfo.size());
        assertEquals(2, traceEntities.size());
        assertSame(traceEntities.get(0), traceEntities.get(1));
    }

    @Test
    public void associateAddress_withLatch_runsInCallersSubsegment() throws Exception {
        AtomicReference<Entity> workerEntity = new AtomicReference<>();
        stubAssociatedDescribe("54.0.0.1", workerEntity, null);
        CountDownLatch latch = new CountDownLatch(1);

        AWSXRay.beginSegment("TEST");
        try (Subsegment caller = AWSXRay.beginSubsegment("Request.Agents.test")) {
            amazonInstance.associateAddress("i-1", Address.builder().publicIp("54.0.0.1").build(), latch);
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            assertSame(caller, workerEntity.get());
        } finally {
            AWSXRay.endSegment();
        }
    }

    @Test
    public void associateAddress_withoutLatch_usesItsOwnSegment() throws Exception {
        AtomicReference<Entity> workerEntity = new AtomicReference<>();
        CountDownLatch described = new CountDownLatch(1);
        stubAssociatedDescribe("54.0.0.1", workerEntity, described);

        Entity caller;
        AWSXRay.beginSegment("TEST");
        try {
            caller = AWSXRay.getTraceEntity();
            amazonInstance.associateAddress("i-1", Address.builder().publicIp("54.0.0.1").build(), null);
        } finally {
            AWSXRay.endSegment();  // the caller does not wait for the worker
        }

        assertTrue(described.await(10, TimeUnit.SECONDS));
        assertNotNull(workerEntity.get());
        assertNotSame(caller, workerEntity.get());
        assertNotSame(caller, workerEntity.get().getParentSegment());
    }

    @Test
    public void createTest_allOptions_buildsRequestFromDescriptionAndRequest() {
        InstanceDescription description = description("ami-raw");
        when(description.getSSMAmi()).thenReturn("/tank/agent/ami");
        when(description.getSecurityGroupIds()).thenReturn(List.of("sg-1"));
        when(description.getIamRole()).thenReturn("tank-agent-role");
        when(description.getZone()).thenReturn("us-west-2a");
        when(description.getTenancy()).thenReturn("dedicated");
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, false, "testZone", description);
        vmRequest.setJobId("42");
        vmRequest.setReportingMode("TPS");
        vmRequest.setLoggingProfile("VERBOSE");
        vmRequest.setStopBehavior("END_OF_TEST");
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().instanceId("i-1").state(InstanceState.builder().build()).build()).build()));
        SsmClient ssmClient = mock(SsmClient.class);
        SsmClientBuilder ssmClientBuilder = mock(SsmClientBuilder.class);
        when(ssmClientBuilder.build()).thenReturn(ssmClient);
        when(ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(GetParameterResponse.builder()
                .parameter(Parameter.builder().value("ami-ssm").build()).build());
        JobInstance jobInstance = new JobInstance();
        jobInstance.setName("Nightly Load");

        System.setProperty(TankConstants.KEY_AWS_SECRET_KEY_ID, "test-key-id");
        System.setProperty(TankConstants.KEY_AWS_SECRET_KEY, "test-key");
        try (MockedStatic<SsmClient> ssm = mockStatic(SsmClient.class);
             MockedConstruction<JobInstanceDao> dao = mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.findById(42)).thenReturn(jobInstance))) {
            ssm.when(SsmClient::builder).thenReturn(ssmClientBuilder);

            AWSXRay.beginSegment("TEST");
            List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
            AWSXRay.endSegment();

            assertEquals(1, vmInfo.size());
        } finally {
            System.clearProperty(TankConstants.KEY_AWS_SECRET_KEY_ID);
            System.clearProperty(TankConstants.KEY_AWS_SECRET_KEY);
        }

        ArgumentCaptor<RunInstancesRequest> captor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient).runInstances(captor.capture());
        RunInstancesRequest request = captor.getValue();
        assertEquals("ami-ssm", request.imageId());
        assertEquals(List.of("sg-1"), request.securityGroupIds());
        assertEquals("tank-agent-role", request.iamInstanceProfile().name());
        assertEquals("us-west-2a", request.placement().availabilityZone());
        assertEquals("subnet-1", request.subnetId());
        assertTrue(request.tagSpecifications().getFirst().tags().stream()
                .anyMatch(tag -> "JobName".equals(tag.key()) && "Nightly Load".equals(tag.value())));
        String userData = new String(Base64.getDecoder().decode(request.userData()));
        assertTrue(userData.contains("\"reporting.mode\":\"TPS\""));
        assertTrue(userData.contains("\"loggingProfile\":\"VERBOSE\""));
        assertTrue(userData.contains("\"stopBehavior\":\"END_OF_TEST\""));
        assertTrue(userData.contains("\"AWS_SECRET_KEY_ID\":\"test-key-id\""));
    }

    @Test
    public void createTest_ssmFailure_fallsBackToConfiguredAmi() {
        InstanceDescription description = description("ami-raw");
        when(description.getSSMAmi()).thenReturn("/tank/agent/ami");
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, false, "testZone", description);
        vmRequest.setReportingMode(null);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().state(InstanceState.builder().build()).build()).build()));

        try (MockedStatic<SsmClient> ssm = mockStatic(SsmClient.class)) {
            ssm.when(SsmClient::builder).thenThrow(new IllegalStateException("no SSM"));

            AWSXRay.beginSegment("TEST");
            amazonInstance.create(vmRequest);
            AWSXRay.endSegment();
        }

        ArgumentCaptor<RunInstancesRequest> captor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient).runInstances(captor.capture());
        assertEquals("ami-raw", captor.getValue().imageId());
        assertEquals(List.of("security_group"), captor.getValue().securityGroups());
    }

    @Test
    public void createTest_reusesStoppedInstance() {
        AmazonInstance westInstance = new AmazonInstance(_mockEc2AsyncClient, VMRegion.US_WEST_2);
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, true, "testZone", null);
        when(_mockEc2AsyncClient.describeInstances()).thenReturn(CompletableFuture.completedFuture(
                DescribeInstancesResponse.builder().reservations(Reservation.builder().instances(
                        agentInstance("i-running", "[AMI-ID]", InstanceStateName.RUNNING),
                        agentInstance("i-other-ami", "ami-other", InstanceStateName.STOPPED),
                        agentInstance("i-stopped", "[AMI-ID]", InstanceStateName.STOPPED)).build()).build()));
        when(_mockEc2AsyncClient.startInstances(any(StartInstancesRequest.class))).thenReturn(CompletableFuture.completedFuture(
                StartInstancesResponse.builder().startingInstances(InstanceStateChange.builder().instanceId("i-stopped")
                        .currentState(InstanceState.builder().name(InstanceStateName.PENDING).build()).build()).build()));

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = westInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(1, vmInfo.size());
        assertEquals("i-stopped", vmInfo.getFirst().getInstanceId());
        assertEquals("pending", vmInfo.getFirst().getState());
        ArgumentCaptor<StartInstancesRequest> captor = ArgumentCaptor.forClass(StartInstancesRequest.class);
        verify(_mockEc2AsyncClient).startInstances(captor.capture());
        assertEquals(List.of("i-stopped"), captor.getValue().instanceIds());
        verify(_mockEc2AsyncClient, never()).runInstances((RunInstancesRequest) any());
    }

    @Test
    public void createTest_reuseStoppedInstance_describeFailureIsWrapped() {
        AmazonInstance westInstance = new AmazonInstance(_mockEc2AsyncClient, VMRegion.US_WEST_2);
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, true, "testZone", null);
        when(_mockEc2AsyncClient.describeInstances())
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("describe failed")));

        AWSXRay.beginSegment("TEST");
        assertThrows(RuntimeException.class, () -> westInstance.create(vmRequest));
        AWSXRay.endSegment();
    }

    @Test
    public void createTest_useEips_associatesAvailableVpcAddress() {
        InstanceDescription description = description("ami-raw");
        when(description.isVPC()).thenReturn(true);
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 2, false, "testZone", description);
        vmRequest.setUserEips(true);
        // two agents but only one free address: the second agent goes without
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().instanceId("i-1").state(InstanceState.builder().build()).build(),
                                Instance.builder().instanceId("i-2").state(InstanceState.builder().build()).build()).build()));
        when(_mockEc2AsyncClient.describeAddresses(any(DescribeAddressesRequest.class))).thenReturn(CompletableFuture.completedFuture(
                DescribeAddressesResponse.builder().addresses(
                        Address.builder().publicIp("54.0.0.1").allocationId("eipalloc-1").domain(DomainType.VPC).build(),
                        Address.builder().publicIp("54.0.0.2").domain(DomainType.STANDARD).build(),
                        Address.builder().publicIp("54.0.0.3").domain(DomainType.VPC).instanceId("i-busy").build()).build()));
        stubAssociatedDescribe("54.0.0.1", new AtomicReference<>(), null);

        AWSXRay.beginSegment("TEST");
        List<VMInformation> vmInfo = amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertEquals(2, vmInfo.size());
        ArgumentCaptor<AssociateAddressRequest> captor = ArgumentCaptor.forClass(AssociateAddressRequest.class);
        verify(_mockEc2AsyncClient).associateAddress(captor.capture());
        assertEquals("i-1", captor.getValue().instanceId());
        assertEquals("eipalloc-1", captor.getValue().allocationId());
    }

    @Test
    public void createTest_useEips_sdkExceptionIsWrapped() {
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, false, "testZone", description("ami-raw"));
        vmRequest.setUserEips(true);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().state(InstanceState.builder().build()).build()).build()));
        SdkClientException sdkException = SdkClientException.create("no addresses");
        when(_mockEc2AsyncClient.describeAddresses(any(DescribeAddressesRequest.class))).thenThrow(sdkException);

        AWSXRay.beginSegment("TEST");
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> amazonInstance.create(vmRequest));
        AWSXRay.endSegment();

        assertSame(sdkException, thrown.getCause());
    }

    @Test
    public void createTest_publicIp_bindsSingleInstance() throws Exception {
        InstanceDescription description = description("ami-raw");
        when(description.getPublicIp()).thenReturn("54.0.0.9");
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 1, false, "testZone", description);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder()
                        .instances(Instance.builder().instanceId("i-1").state(InstanceState.builder().build()).build()).build()));
        CountDownLatch described = new CountDownLatch(1);
        stubAssociatedDescribe("54.0.0.9", new AtomicReference<>(), described);

        AWSXRay.beginSegment("TEST");
        amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        assertTrue(described.await(10, TimeUnit.SECONDS));
        ArgumentCaptor<AssociateAddressRequest> captor = ArgumentCaptor.forClass(AssociateAddressRequest.class);
        verify(_mockEc2AsyncClient).associateAddress(captor.capture());
        assertEquals("54.0.0.9", captor.getValue().publicIp());
    }

    @Test
    public void createTest_INVALID_AMI_doesNotFallBack() {
        VMInstanceRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 2, false, "testZone", description("ami-raw"));
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.failedFuture(new CompletionException(Ec2Exception.builder()
                        .awsErrorDetails(AwsErrorDetails.builder().errorCode(AmazonInstance.INVALID_AMI_ID_UNAVAILABLE).build())
                        .build())));

        AWSXRay.beginSegment("TEST");
        assertThrows(RuntimeException.class, () -> amazonInstance.create(vmRequest));
        AWSXRay.endSegment();

        verify(_mockEc2AsyncClient, times(1)).runInstances((RunInstancesRequest) any());
    }

    @Test
    public void associateAddress_retriesAfterError() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        when(_mockEc2AsyncClient.associateAddress(any(AssociateAddressRequest.class)))
                .thenThrow(SdkClientException.create("not ready"))
                .thenReturn(CompletableFuture.completedFuture(AssociateAddressResponse.builder().build()));
        stubAssociatedDescribe("54.0.0.1", new AtomicReference<>(), null);

        AWSXRay.beginSegment("TEST");
        try {
            amazonInstance.associateAddress("i-1", Address.builder().publicIp("54.0.0.1").build(), latch);
            assertTrue(latch.await(10, TimeUnit.SECONDS));
        } finally {
            AWSXRay.endSegment();
        }

        verify(_mockEc2AsyncClient, times(2)).associateAddress(any(AssociateAddressRequest.class));
    }

    @Test
    public void describeInstances_failureReturnsEmpty() {
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("unknown instance")));

        assertTrue(amazonInstance.describeInstances(List.of("i-1")).isEmpty());
    }

    @Test
    public void describeInstances_interruptedReturnsEmptyAndKeepsInterruptFlag() {
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class)))
                .thenReturn(new CompletableFuture<>());

        Thread.currentThread().interrupt();
        try {
            assertTrue(amazonInstance.describeInstances(List.of("i-1")).isEmpty());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void findRunningAgents_followsNextTokenAndSkipsMissingTags() {
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(DescribeInstancesResponse.builder()
                        .reservations(Reservation.builder().instances(Instance.builder().instanceId("i-1")
                                .state(InstanceState.builder().build()).build()).build())
                        .nextToken("page-2").build()))
                .thenReturn(CompletableFuture.completedFuture(DescribeInstancesResponse.builder()
                        .reservations(Reservation.builder().instances(Instance.builder().instanceId("i-2")
                                .state(InstanceState.builder().build()).build()).build()).build()));

        List<VMInformation> agents = amazonInstance.findRunningAgents("tank-prod");

        assertEquals(List.of("i-1", "i-2"), agents.stream().map(VMInformation::getInstanceId).toList());
        assertEquals(null, agents.getFirst().getJobId());
        assertEquals(null, agents.getFirst().getLaunchTime());
        ArgumentCaptor<DescribeInstancesRequest> captor = ArgumentCaptor.forClass(DescribeInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(2)).describeInstances(captor.capture());
        assertEquals("page-2", captor.getAllValues().get(1).nextToken());
    }

    @Test
    public void findRunningAgents_failureReturnsEmpty() {
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("throttled")));

        assertTrue(amazonInstance.findRunningAgents("tank-prod").isEmpty());
    }

    @Test
    public void findRunningAgents_interruptedReturnsEmpty() {
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class)))
                .thenReturn(new CompletableFuture<>());

        Thread.currentThread().interrupt();
        try {
            assertTrue(amazonInstance.findRunningAgents("tank-prod").isEmpty());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void findInstancesOfType_returnsRunningAndPendingWithMatchingAmi() {
        when(_mockEc2AsyncClient.describeInstances()).thenReturn(CompletableFuture.completedFuture(
                DescribeInstancesResponse.builder().reservations(Reservation.builder().instances(
                        agentInstance("i-running", "[AMI-ID]", InstanceStateName.RUNNING),
                        agentInstance("i-pending", "[AMI-ID]", InstanceStateName.PENDING),
                        agentInstance("i-stopped", "[AMI-ID]", InstanceStateName.STOPPED),
                        agentInstance("i-other-ami", "ami-other", InstanceStateName.RUNNING)).build()).build()));

        List<VMInformation> found = amazonInstance.findInstancesOfType(VMRegion.US_WEST_2, VMImageType.AGENT);

        assertEquals(List.of("i-running", "i-pending"), found.stream().map(VMInformation::getInstanceId).toList());
        assertEquals(VMRegion.US_WEST_2, found.getFirst().getRegion());
    }

    @Test
    public void findInstancesOfType_failureIsWrapped() {
        when(_mockEc2AsyncClient.describeInstances())
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("describe failed")));

        assertThrows(RuntimeException.class, () -> amazonInstance.findInstancesOfType(VMRegion.US_WEST_2, VMImageType.AGENT));
    }

    @Test
    public void findDNSName_prefersPublicThenPrivateName() {
        when(_mockEc2AsyncClient.describeInstances()).thenReturn(CompletableFuture.completedFuture(
                DescribeInstancesResponse.builder().reservations(Reservation.builder().instances(
                        Instance.builder().instanceId("i-public").publicDnsName("ec2-public").privateDnsName("ip-public").build(),
                        Instance.builder().instanceId("i-private").publicDnsName("").privateDnsName("ip-private").build()).build()).build()));

        assertEquals("ec2-public", amazonInstance.findDNSName("i-public").orElseThrow());
        assertEquals("ip-private", amazonInstance.findDNSName("i-private").orElseThrow());
        assertTrue(amazonInstance.findDNSName("i-missing").isEmpty());
    }

    @Test
    public void findDNSName_failureReturnsEmpty() {
        when(_mockEc2AsyncClient.describeInstances())
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("describe failed")));

        assertTrue(amazonInstance.findDNSName("i-1").isEmpty());
    }

    @Test
    public void kill_terminatesRequestedInstances() {
        TerminateInstancesResponse response = mock(TerminateInstancesResponse.class);
        when(response.terminatingInstances()).thenReturn(List.of(InstanceStateChange.builder().instanceId("i-1").build()));
        when(_mockEc2AsyncClient.terminateInstances(any(TerminateInstancesRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        amazonInstance.kill(new VMKillRequest(VMProvider.Amazon, List.of("i-1")));

        // isEmpty() check plus the forEach both read the list
        verify(response, timeout(5000).times(2)).terminatingInstances();
        ArgumentCaptor<TerminateInstancesRequest> captor = ArgumentCaptor.forClass(TerminateInstancesRequest.class);
        verify(_mockEc2AsyncClient).terminateInstances(captor.capture());
        assertEquals(List.of("i-1"), captor.getValue().instanceIds());
    }

    @Test
    public void kill_failureAndEmptyResponseAreOnlyLogged() {
        TerminateInstancesResponse empty = mock(TerminateInstancesResponse.class);
        when(empty.terminatingInstances()).thenReturn(List.of());
        when(_mockEc2AsyncClient.terminateInstances(any(TerminateInstancesRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("terminate failed")))
                .thenReturn(CompletableFuture.completedFuture(null))
                .thenReturn(CompletableFuture.completedFuture(empty));

        amazonInstance.kill(new VMKillRequest(VMProvider.Amazon, List.of("i-1")));
        amazonInstance.kill(new VMKillRequest(VMProvider.Amazon, List.of("i-1")));
        amazonInstance.kill(new VMKillRequest(VMProvider.Amazon, List.of("i-1")));

        verify(empty, timeout(5000)).terminatingInstances();
        awaitAsyncCallbacks();
        verify(_mockEc2AsyncClient, times(3)).terminateInstances(any(TerminateInstancesRequest.class));
    }

    @Test
    public void killInstances_terminatesOnlyInstancesInRegion() {
        when(_mockEc2AsyncClient.describeInstances()).thenReturn(CompletableFuture.completedFuture(
                DescribeInstancesResponse.builder().reservations(Reservation.builder().instances(
                        Instance.builder().instanceId("i-1").build(),
                        Instance.builder().instanceId("i-unrelated").build()).build()).build()));

        amazonInstance.killInstances(List.of("i-1", "i-elsewhere"));

        ArgumentCaptor<TerminateInstancesRequest> captor = ArgumentCaptor.forClass(TerminateInstancesRequest.class);
        verify(_mockEc2AsyncClient, timeout(5000)).terminateInstances(captor.capture());
        assertEquals(List.of("i-1"), captor.getValue().instanceIds());
    }

    @Test
    public void killInstances_nothingToTerminate() {
        DescribeInstancesResponse noMatch = spyResponse(Instance.builder().instanceId("i-unrelated").build());
        DescribeInstancesResponse noReservations = spyResponse();
        when(_mockEc2AsyncClient.describeInstances())
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("describe failed")))
                .thenReturn(CompletableFuture.completedFuture(null))
                .thenReturn(CompletableFuture.completedFuture(noReservations))
                .thenReturn(CompletableFuture.completedFuture(noMatch));

        for (int i = 0; i < 4; i++) {
            amazonInstance.killInstances(List.of("i-1"));
        }

        verify(noReservations, timeout(5000)).reservations();
        verify(noMatch, timeout(5000).times(2)).reservations();
        awaitAsyncCallbacks();
        verify(_mockEc2AsyncClient, never()).terminateInstances(any(TerminateInstancesRequest.class));
    }

    @Test
    public void stopInstances_stopsOnlyInstancesInRegion() {
        when(_mockEc2AsyncClient.describeInstances()).thenReturn(CompletableFuture.completedFuture(
                DescribeInstancesResponse.builder().reservations(Reservation.builder().instances(
                        Instance.builder().instanceId("i-1").build(),
                        Instance.builder().instanceId("i-unrelated").build()).build()).build()));

        amazonInstance.stopInstances(List.of("i-1", "i-elsewhere"));

        ArgumentCaptor<StopInstancesRequest> captor = ArgumentCaptor.forClass(StopInstancesRequest.class);
        verify(_mockEc2AsyncClient, timeout(5000)).stopInstances(captor.capture());
        assertEquals(List.of("i-1"), captor.getValue().instanceIds());
    }

    @Test
    public void stopInstances_nothingToStop() {
        DescribeInstancesResponse noMatch = spyResponse(Instance.builder().instanceId("i-unrelated").build());
        DescribeInstancesResponse noReservations = spyResponse();
        when(_mockEc2AsyncClient.describeInstances())
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("describe failed")))
                .thenReturn(CompletableFuture.completedFuture(null))
                .thenReturn(CompletableFuture.completedFuture(noReservations))
                .thenReturn(CompletableFuture.completedFuture(noMatch));

        for (int i = 0; i < 4; i++) {
            amazonInstance.stopInstances(List.of("i-1"));
        }

        verify(noReservations, timeout(5000)).reservations();
        verify(noMatch, timeout(5000).times(2)).reservations();
        awaitAsyncCallbacks();
        verify(_mockEc2AsyncClient, never()).stopInstances(any(StopInstancesRequest.class));
    }

    @Test
    public void reboot_rebootsAllInstanceIds() {
        VMInformation first = new VMInformation();
        first.setInstanceId("i-1");
        VMInformation second = new VMInformation();
        second.setInstanceId("i-2");

        amazonInstance.reboot(List.of(first, second));

        ArgumentCaptor<RebootInstancesRequest> captor = ArgumentCaptor.forClass(RebootInstancesRequest.class);
        verify(_mockEc2AsyncClient).rebootInstances(captor.capture());
        assertEquals(List.of("i-1", "i-2"), captor.getValue().instanceIds());
    }

    @Test
    public void attachVolume_attachesThenReboots() {
        amazonInstance.attachVolume("vol-1", "i-1", "/dev/sdf");

        ArgumentCaptor<AttachVolumeRequest> captor = ArgumentCaptor.forClass(AttachVolumeRequest.class);
        verify(_mockEc2AsyncClient).attachVolume(captor.capture());
        assertEquals("vol-1", captor.getValue().volumeId());
        assertEquals("i-1", captor.getValue().instanceId());
        assertEquals("/dev/sdf", captor.getValue().device());
        verify(_mockEc2AsyncClient).rebootInstances(any(RebootInstancesRequest.class));
    }

    /**
     * A single-subnet agent description; individual tests override the getters they exercise.
     */
    private static InstanceDescription description(String ami) {
        InstanceDescription description = mock(InstanceDescription.class, withSettings().strictness(Strictness.LENIENT));
        when(description.getAmi()).thenReturn(ami);
        when(description.getKeypair()).thenReturn("tank-key");
        when(description.getSecurityGroup()).thenReturn("security_group");
        when(description.getSecurityGroupIds()).thenReturn(List.of());
        when(description.getSubnetIds()).thenAnswer(invocation -> new ArrayList<>(List.of("subnet-1")));
        when(description.isVPC()).thenReturn(false);
        return description;
    }

    private static Instance agentInstance(String instanceId, String imageId, InstanceStateName state) {
        return Instance.builder().instanceId(instanceId).imageId(imageId)
                .state(InstanceState.builder().name(state).build()).build();
    }

    private static DescribeInstancesResponse spyResponse(Instance... instances) {
        DescribeInstancesResponse.Builder builder = DescribeInstancesResponse.builder();
        if (instances.length > 0) {
            builder.reservations(Reservation.builder().instances(instances).build());
        }
        return spy(builder.build());
    }

    /**
     * kill/killInstances/stopInstances finish on the common pool; wait for those callbacks to run.
     */
    private static void awaitAsyncCallbacks() {
        ForkJoinPool.commonPool().awaitQuiescence(5, TimeUnit.SECONDS);
    }

    private void stubAssociatedDescribe(String publicIp, AtomicReference<Entity> workerEntity, CountDownLatch described) {
        when(_mockEc2AsyncClient.describeInstances(any(DescribeInstancesRequest.class))).thenAnswer(invocation -> {
            workerEntity.set(AWSXRay.getTraceEntity());
            if (described != null) {
                described.countDown();
            }
            return CompletableFuture.completedFuture(DescribeInstancesResponse.builder()
                    .reservations(Reservation.builder().instances(Instance.builder().instanceId("i-1")
                            .publicIpAddress(publicIp).build()).build()).build());
        });
    }
}
