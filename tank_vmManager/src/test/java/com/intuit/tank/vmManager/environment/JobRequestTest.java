package com.intuit.tank.vmManager.environment;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobRequestTest {

    private final IEnvironmentInstance environment = mock(IEnvironmentInstance.class);

    @Test
    public void refreshPublicAddress_picksUpPublicIpForRemoteRegion() {
        VMInformation launched = vm("pending", null);
        VMInformation described = vm("running", "54.0.0.1");
        when(environment.describeInstances(List.of("i-1"))).thenReturn(List.of(described));

        VMInformation refreshed = JobRequest.refreshPublicAddress(launched, environment);

        assertSame(described, refreshed);
        assertEquals("54.0.0.1", JobRequest.resolveWsHost(VMRegion.US_WEST_2, refreshed));
    }

    @Test
    public void refreshPublicAddress_picksUpPublicIpInControllerRegion() {
        VMInformation launched = vm("pending", null);
        VMInformation described = vm("running", "3.0.0.1");
        when(environment.describeInstances(List.of("i-1"))).thenReturn(List.of(described));

        VMInformation refreshed = JobRequest.refreshPublicAddress(launched, environment);

        assertEquals("3.0.0.1", refreshed.getPublicIp());
        // The controller still reaches same-region agents on their private IP
        assertEquals("10.0.0.1", JobRequest.resolveWsHost(VMRegion.US_EAST_2, refreshed));
    }

    @Test
    public void refreshPublicAddress_skipsWhenPublicIpKnown() {
        VMInformation launched = vm("pending", "54.0.0.1");

        assertSame(launched, JobRequest.refreshPublicAddress(launched, environment));
        verify(environment, never()).describeInstances(any());
    }

    @Test
    public void refreshPublicAddress_stopsOnceRunningWithoutPublicIp() {
        VMInformation privateOnly = vm("running", null);

        assertSame(privateOnly, JobRequest.refreshPublicAddress(privateOnly, environment));
        verify(environment, never()).describeInstances(any());
        assertEquals("10.0.0.1", JobRequest.resolveWsHost(VMRegion.US_WEST_2, privateOnly));
    }

    @Test
    public void refreshPublicAddress_keepsLaunchInfoWhenDescribeFindsNothing() {
        VMInformation launched = vm("pending", null);
        when(environment.describeInstances(List.of("i-1"))).thenReturn(List.of());

        assertSame(launched, JobRequest.refreshPublicAddress(launched, environment));
    }

    private static VMInformation vm(String state, String publicIp) {
        VMInformation info = new VMInformation();
        info.setInstanceId("i-1");
        info.setState(state);
        info.setPrivateIp("10.0.0.1");
        info.setPublicIp(publicIp);
        return info;
    }
}
