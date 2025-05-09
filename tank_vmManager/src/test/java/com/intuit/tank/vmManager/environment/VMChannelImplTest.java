package com.intuit.tank.vmManager.environment;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.settings.InstanceDescription;
import com.intuit.tank.vm.settings.VmManagerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VMChannelImplTest {
    private VMChannelImpl vmChannel;

    private MockedConstruction<AmazonInstance> amazonInstanceMockedConstruction;
    private MockedConstruction<TankConfig> tankConfigMockedConstruction;

    @Mock
    private VmManagerConfig mockVmManagerConfig;
    @Mock
    private InstanceDescription mockInstanceDescription;

    @BeforeEach
    void setUp() {
        vmChannel = new VMChannelImpl();
    }

    @AfterEach
    void tearDown() {
        if (amazonInstanceMockedConstruction != null) {
            amazonInstanceMockedConstruction.close();
        }
        if (tankConfigMockedConstruction != null) {
            tankConfigMockedConstruction.close();
        }
    }

    private void setupTankConfigMock(Set<VMRegion> regionsToReturn) {
        tankConfigMockedConstruction = Mockito.mockConstruction(TankConfig.class,
                (mockConfig, contextConfig) -> {
                    when(mockConfig.getVmManagerConfig()).thenReturn(mockVmManagerConfig);
                    when(mockVmManagerConfig.getRegions()).thenReturn(regionsToReturn);
                });
    }

    @Test
    void findInstancesOfType_Success() {
        VMRegion region = VMRegion.US_WEST_2;
        VMImageType type = VMImageType.AGENT;

        VMInformation info1 = new VMInformation();
        info1.setInstanceId("id1");
        info1.setProvider(VMProvider.Amazon);
        info1.setRegion(region);
        info1.setPublicDNS("dns1");
        info1.setPrivateDNS("private_dns1");

        VMInformation info2 = new VMInformation();
        info2.setInstanceId("id2");
        info2.setProvider(VMProvider.Amazon);
        info2.setRegion(region);
        info2.setPublicDNS("dns2");
        info2.setPrivateDNS("private_dns2");

        List<VMInformation> expectedInstances = List.of(info1, info2);

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(region, context.arguments().get(0));
                    when(mock.findInstancesOfType(eq(region), eq(type))).thenReturn(expectedInstances);
                });

        List<VMInformation> actualInstances = vmChannel.findInstancesOfType(region, type);

        assertEquals(expectedInstances, actualInstances);
        assertEquals(1, amazonInstanceMockedConstruction.constructed().size());
        AmazonInstance constructedMock = amazonInstanceMockedConstruction.constructed().get(0);
        verify(constructedMock).findInstancesOfType(region, type);
    }

    @Test
    void findInstancesOfType_AmazonInstanceThrowsException() {
        VMRegion region = VMRegion.US_EAST_2;
        VMImageType type = VMImageType.CONTROLLER;
        RuntimeException simulatedException = new RuntimeException("Simulated AWS Error during findInstancesOfType");

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(region, context.arguments().get(0));
                    when(mock.findInstancesOfType(eq(region), eq(type))).thenThrow(simulatedException);
                });

        List<VMInformation> actualInstances = assertDoesNotThrow(
                () -> vmChannel.findInstancesOfType(region, type),
                "VMChannelImpl should catch the exception and not re-throw it"
        );

        assertNotNull(actualInstances, "Returned list should not be null even on error");
        assertTrue(actualInstances.isEmpty(), "Returned list should be empty when an exception occurs");

        assertEquals(1, amazonInstanceMockedConstruction.constructed().size(), "Exactly one AmazonInstance should be constructed");
        AmazonInstance constructedMock = amazonInstanceMockedConstruction.constructed().get(0);
        verify(constructedMock).findInstancesOfType(region, type);
    }

    @Test
    void findInstancesOfType_ReturnsEmptyList() {
        VMRegion region = VMRegion.US_WEST_2;
        VMImageType type = VMImageType.AGENT;
        List<VMInformation> expectedEmptyList = Collections.emptyList();

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(region, context.arguments().get(0));
                    when(mock.findInstancesOfType(eq(region), eq(type))).thenReturn(expectedEmptyList);
                });

        List<VMInformation> actualInstances = vmChannel.findInstancesOfType(region, type);

        assertNotNull(actualInstances, "Returned list should not be null");
        assertTrue(actualInstances.isEmpty(), "Returned list should be empty as specified by the mock");
        assertEquals(expectedEmptyList, actualInstances, "Returned list should be the empty list provided by the mock");

        assertEquals(1, amazonInstanceMockedConstruction.constructed().size(), "Exactly one AmazonInstance should be constructed");
        AmazonInstance constructedMock = amazonInstanceMockedConstruction.constructed().get(0);
        verify(constructedMock).findInstancesOfType(region, type);
    }

    @Test
    void terminateInstances_Success_SingleRegion() {
        List<String> instanceIds = List.of("i-12345");
        VMRegion region = VMRegion.US_EAST_2;
        Set<VMRegion> regions = Set.of(region);

        setupTankConfigMock(regions);

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(region, context.arguments().get(0), "Constructor called with correct region");
                });

        vmChannel.terminateInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size(), "TankConfig should be constructed once");
        TankConfig constructedConfig = tankConfigMockedConstruction.constructed().get(0);
        verify(constructedConfig).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();

        assertEquals(1, amazonInstanceMockedConstruction.constructed().size(), "AmazonInstance should be constructed once for the single region");
        AmazonInstance constructedAmzInstance = amazonInstanceMockedConstruction.constructed().get(0);

        verify(constructedAmzInstance).killInstances(eq(instanceIds));
    }

    @Test
    void terminateInstances_Success_MultipleRegions() {
        List<String> instanceIds = List.of("i-123", "i-456");
        VMRegion region1 = VMRegion.US_WEST_2;
        VMRegion region2 = VMRegion.US_EAST_2;
        Set<VMRegion> regions = Set.of(region1, region2);

        List<VMRegion> capturedRegions = new java.util.ArrayList<>();

        setupTankConfigMock(regions);

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    capturedRegions.add((VMRegion) context.arguments().get(0));
                });

        vmChannel.terminateInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();

        List<AmazonInstance> constructedInstances = amazonInstanceMockedConstruction.constructed();
        assertEquals(regions.size(), constructedInstances.size(), "AmazonInstance should be constructed once per region");

        assertTrue(capturedRegions.containsAll(regions) && regions.containsAll(capturedRegions),
                "AmazonInstance should be constructed for all configured regions. Actual captured: " + capturedRegions);

        for (AmazonInstance mockInstance : constructedInstances) {
            verify(mockInstance).killInstances(eq(instanceIds));
        }
    }

    @Test
    void terminateInstances_Success_NoRegions() {
        List<String> instanceIds = List.of("i-789");
        Set<VMRegion> regions = Collections.emptySet();

        setupTankConfigMock(regions);
        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class);

        vmChannel.terminateInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();

        assertEquals(0, amazonInstanceMockedConstruction.constructed().size(), "AmazonInstance should NOT be constructed if no regions are configured");
    }

    @Test
    void terminateInstances_Success_EmptyInstanceIdList() {
        List<String> instanceIds = Collections.emptyList();
        VMRegion region = VMRegion.US_WEST_2;
        Set<VMRegion> regions = Set.of(region);

        setupTankConfigMock(regions);
        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(region, context.arguments().get(0), "Constructor called with correct region");
                });

        vmChannel.terminateInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        assertEquals(1, amazonInstanceMockedConstruction.constructed().size());
        AmazonInstance constructedAmzInstance = amazonInstanceMockedConstruction.constructed().get(0);

        verify(constructedAmzInstance).killInstances(eq(instanceIds));
        verify(constructedAmzInstance).killInstances(eq(Collections.emptyList()));
    }


    @Test
    void terminateInstances_ExceptionDuringConfig() {
        List<String> instanceIds = List.of("i-abc");
        RuntimeException configException = new RuntimeException("Failed to load config");

        tankConfigMockedConstruction = Mockito.mockConstruction(TankConfig.class,
                (mockConfig, contextConfig) -> {
                    when(mockConfig.getVmManagerConfig()).thenThrow(configException);
                });

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class);

        assertDoesNotThrow(
                () -> vmChannel.terminateInstances(instanceIds),
                "VMChannelImpl should catch exceptions during config loading"
        );

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig, never()).getRegions();

        assertEquals(0, amazonInstanceMockedConstruction.constructed().size());
    }

    @Test
    void terminateInstances_ExceptionDuringKillInstances() {
        List<String> instanceIds = List.of("i-def");
        VMRegion region1 = VMRegion.US_EAST_2;
        VMRegion region2 = VMRegion.US_WEST_2;
        Set<VMRegion> regions = Set.of(region1, region2);
        RuntimeException killException = new RuntimeException("AWS kill instance failed");

        setupTankConfigMock(regions);

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    doThrow(killException).when(mock).killInstances(eq(instanceIds));
                });

        assertDoesNotThrow(
                () -> vmChannel.terminateInstances(instanceIds),
                "VMChannelImpl should catch exceptions during killInstances"
        );

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();


        List<AmazonInstance> constructedMocks = amazonInstanceMockedConstruction.constructed();
        assertFalse(constructedMocks.isEmpty(), "At least one AmazonInstance should have been constructed before the exception");
        verify(constructedMocks.get(0)).killInstances(eq(instanceIds));

        assertEquals(1, constructedMocks.size(), "Expected loop to terminate after first exception, constructing only one AmazonInstance");
    }

    @Test
    void stopInstances_Success_SingleRegion() {
        List<String> instanceIds = List.of("i-stop-1");
        VMRegion region = VMRegion.US_EAST_2;
        Set<VMRegion> regions = Set.of(region);

        setupTankConfigMock(regions);

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(region, context.arguments().get(0), "Constructor called with correct region");
                });

        vmChannel.stopInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();

        assertEquals(1, amazonInstanceMockedConstruction.constructed().size());
        AmazonInstance constructedAmzInstance = amazonInstanceMockedConstruction.constructed().get(0);

        verify(constructedAmzInstance).stopInstances(eq(instanceIds));
    }

    @Test
    void stopInstances_Success_MultipleRegions() {
        List<String> instanceIds = List.of("i-stop-2", "i-stop-3");
        VMRegion region1 = VMRegion.US_WEST_1;
        VMRegion region2 = VMRegion.US_EAST_2;

        Set<VMRegion> regions = Set.of(region1, region2);
        List<VMRegion> capturedRegions = new ArrayList<>();

        setupTankConfigMock(regions);
        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    capturedRegions.add((VMRegion) context.arguments().get(0));
                });

        vmChannel.stopInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();

        List<AmazonInstance> constructedInstances = amazonInstanceMockedConstruction.constructed();
        assertEquals(regions.size(), constructedInstances.size());

        assertTrue(capturedRegions.containsAll(regions) && regions.containsAll(capturedRegions),
                "Constructor called for all regions. Actual captured: " + capturedRegions);

        for (AmazonInstance mockInstance : constructedInstances) {
            verify(mockInstance).stopInstances(eq(instanceIds));
        }
    }

    @Test
    void stopInstances_Success_NoRegions() {
        List<String> instanceIds = List.of("i-stop-4");
        Set<VMRegion> regions = Collections.emptySet();

        setupTankConfigMock(regions);
        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class);

        vmChannel.stopInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();

        assertEquals(0, amazonInstanceMockedConstruction.constructed().size(), "AmazonInstance should not be constructed");
    }

    @Test
    void stopInstances_Success_EmptyInstanceIdList() {
        List<String> instanceIds = Collections.emptyList();
        VMRegion region = VMRegion.US_EAST_2;

        Set<VMRegion> regions = Set.of(region);

        setupTankConfigMock(regions);
        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(region, context.arguments().get(0), "Constructor called with correct region");
                });

        vmChannel.stopInstances(instanceIds);

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        assertEquals(1, amazonInstanceMockedConstruction.constructed().size());
        AmazonInstance constructedAmzInstance = amazonInstanceMockedConstruction.constructed().get(0);

        verify(constructedAmzInstance).stopInstances(eq(instanceIds));
        verify(constructedAmzInstance).stopInstances(eq(Collections.emptyList()));
    }

    @Test
    void stopInstances_ExceptionDuringConfig() {
        List<String> instanceIds = List.of("i-stop-5");
        RuntimeException configException = new RuntimeException("Failed to load config for stop");

        tankConfigMockedConstruction = Mockito.mockConstruction(TankConfig.class,
                (mockConfig, contextConfig) -> {
                    when(mockConfig.getVmManagerConfig()).thenThrow(configException);
                });
        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class);

        assertDoesNotThrow(
                () -> vmChannel.stopInstances(instanceIds),
                "Should catch exceptions during config loading"
        );

        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig, never()).getRegions();
        assertEquals(0, amazonInstanceMockedConstruction.constructed().size());
    }

    @Test
    void stopInstances_ExceptionDuringStopInstances() {
        List<String> instanceIds = List.of("i-stop-6");
        VMRegion region1 = VMRegion.US_EAST_2;
        VMRegion region2 = VMRegion.US_WEST_2;

        Set<VMRegion> regions = Set.of(region1, region2);
        RuntimeException stopException = new RuntimeException("AWS stop instance failed");

        setupTankConfigMock(regions);

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    doThrow(stopException).when(mock).stopInstances(eq(instanceIds));
                });

        assertDoesNotThrow(
                () -> vmChannel.stopInstances(instanceIds),
                "Should catch exceptions during stopInstances call"
        );


        assertEquals(1, tankConfigMockedConstruction.constructed().size());
        verify(tankConfigMockedConstruction.constructed().get(0)).getVmManagerConfig();
        verify(mockVmManagerConfig).getRegions();

        List<AmazonInstance> constructedMocks = amazonInstanceMockedConstruction.constructed();
        assertFalse(constructedMocks.isEmpty(), "At least one AmazonInstance should have been constructed before the exception");
        verify(constructedMocks.get(0)).stopInstances(eq(instanceIds));

        assertEquals(1, constructedMocks.size(), "Expected loop to terminate after first exception, constructing only one AmazonInstance");
    }

    @Test
    void startInstances_Success() {
        VMRegion targetRegion = VMRegion.US_EAST_2;
        VMInstanceRequest request = new VMInstanceRequest(VMProvider.Amazon, targetRegion, "m5.large",
                VMImageType.AGENT, 2, false, "testZone", mockInstanceDescription);

        VMInformation info1 = new VMInformation();
        info1.setInstanceId("id-start-1");
        info1.setProvider(VMProvider.Amazon);
        info1.setRegion(targetRegion);
        info1.setPublicDNS("dns-start-1");

        VMInformation info2 = new VMInformation();
        info2.setInstanceId("id-start-2");
        info2.setProvider(VMProvider.Amazon);
        info2.setRegion(targetRegion);
        info2.setPublicDNS("dns-start-2");

        List<VMInformation> expectedInstances = List.of(info1, info2);

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(targetRegion, context.arguments().get(0));
                    when(mock.create(eq(request))).thenReturn(expectedInstances);
                });

        List<VMInformation> actualInstances = vmChannel.startInstances(request);

        assertNotNull(actualInstances);
        assertEquals(expectedInstances, actualInstances);

        assertEquals(1, amazonInstanceMockedConstruction.constructed().size());
        AmazonInstance constructedMock = amazonInstanceMockedConstruction.constructed().get(0);
        verify(constructedMock).create(eq(request));
    }

    @Test
    void startInstances_ReturnsEmptyList() {
        VMRegion targetRegion = VMRegion.US_WEST_2;
        VMInstanceRequest request = new VMInstanceRequest(VMProvider.Amazon, targetRegion, "t3.micro",
                VMImageType.AGENT, 1, false, "testZone", mockInstanceDescription);

        List<VMInformation> expectedEmptyList = Collections.emptyList();

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(targetRegion, context.arguments().get(0));
                    when(mock.create(eq(request))).thenReturn(expectedEmptyList);
                });

        List<VMInformation> actualInstances = vmChannel.startInstances(request);

        assertNotNull(actualInstances);
        assertTrue(actualInstances.isEmpty());
        assertEquals(expectedEmptyList, actualInstances);

        assertEquals(1, amazonInstanceMockedConstruction.constructed().size());
        AmazonInstance constructedMock = amazonInstanceMockedConstruction.constructed().get(0);
        verify(constructedMock).create(eq(request));
    }

    @Test
    void startInstances_ExceptionDuringConstruction() {
        VMRegion targetRegion = VMRegion.US_EAST_2;
        VMInstanceRequest request = new VMInstanceRequest(VMProvider.Amazon, targetRegion, "c5.large",
                VMImageType.CONTROLLER, 1, false, "testZone", mockInstanceDescription);
        RuntimeException constructorException = new RuntimeException("Failed to init AmazonInstance");

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(targetRegion, context.arguments().get(0));
                    throw constructorException;
                });

        List<VMInformation> actualInstances = assertDoesNotThrow(
                () -> vmChannel.startInstances(request),
                "Should catch exception during AmazonInstance construction"
        );

        assertNotNull(actualInstances);
        assertTrue(actualInstances.isEmpty());
        assertEquals(Collections.emptyList(), actualInstances);

        assertEquals(0, amazonInstanceMockedConstruction.constructed().size(),
                "Construction failed, so no mocks should be in the constructed list");
    }

    @Test
    void startInstances_ExceptionDuringCreateCall() {
        VMRegion targetRegion = VMRegion.US_WEST_2;
        VMInstanceRequest request = new VMInstanceRequest(VMProvider.Amazon, targetRegion, "r5.large",
                VMImageType.AGENT, 1, false, "testZone", mockInstanceDescription);
        RuntimeException createException = new RuntimeException("AWS create call failed");

        amazonInstanceMockedConstruction = Mockito.mockConstruction(AmazonInstance.class,
                (mock, context) -> {
                    assertEquals(targetRegion, context.arguments().get(0));
                    when(mock.create(eq(request))).thenThrow(createException);
                });

        List<VMInformation> actualInstances = assertDoesNotThrow(
                () -> vmChannel.startInstances(request),
                "Should catch exception during amazonInstance.create()"
        );

        assertNotNull(actualInstances);
        assertTrue(actualInstances.isEmpty());
        assertEquals(Collections.emptyList(), actualInstances);

        assertEquals(1, amazonInstanceMockedConstruction.constructed().size());
        AmazonInstance constructedMock = amazonInstanceMockedConstruction.constructed().get(0);
        verify(constructedMock).create(eq(request));
    }

}

