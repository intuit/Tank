package com.intuit.tank.harness;

import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class APIMonitorTest {

    @BeforeEach
    public void init() {
        APITestHarness instance = APITestHarness.getInstance();
    }

    @AfterEach
    public void cleanup() {
        APITestHarness.destroyCurrentInstance();
    }

    @Test
    public void testAPIMonitorTest_1() {
        CloudVmStatus status = new CloudVmStatus(null, null, "unknown", "wats-dev", JobStatus.Unknown,
                VMImageType.AGENT, VMRegion.US_EAST, VMStatus.running,
                new ValidationStatus(), 0, 0, new Date(), null);
        APIMonitor apiMonitor = new APIMonitor(false, status);
        assertNotNull(apiMonitor);
    }
}
