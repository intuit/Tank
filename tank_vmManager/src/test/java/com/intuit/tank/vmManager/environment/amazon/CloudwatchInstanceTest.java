package com.intuit.tank.vmManager.environment.amazon;

import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CloudwatchInstanceTest
 *
 * @author Srujana Cheruvu
 *
 */
public class CloudwatchInstanceTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCloudwatchInstance() {
        CloudwatchInstance cloudwatchInstance = new CloudwatchInstance(VMRegion.STANDALONE);
        assertNotNull(cloudwatchInstance);
    }

}