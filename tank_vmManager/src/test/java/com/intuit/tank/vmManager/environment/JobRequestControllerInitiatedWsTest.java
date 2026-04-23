package com.intuit.tank.vmManager.environment;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vmManager.VMTrackerImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobRequestControllerInitiatedWsTest {

    @Test
    public void testSelectHostForEastPrefersPrivateAddress() throws Exception {
        JobRequest jobRequest = createFixture();
        VMInformation vmInfo = buildVmInfo();

        Method method = JobRequest.class.getDeclaredMethod("selectHostForRegion", VMRegion.class, VMInformation.class);
        method.setAccessible(true);
        String host = (String) method.invoke(jobRequest, VMRegion.US_EAST_2, vmInfo);

        assertEquals("10.0.0.5", host);
    }

    @Test
    public void testSelectHostForWestPrefersPublicAddress() throws Exception {
        JobRequest jobRequest = createFixture();
        VMInformation vmInfo = buildVmInfo();

        Method method = JobRequest.class.getDeclaredMethod("selectHostForRegion", VMRegion.class, VMInformation.class);
        method.setAccessible(true);
        String host = (String) method.invoke(jobRequest, VMRegion.US_WEST_2, vmInfo);

        assertEquals("ec2-public.example.com", host);
    }

    private JobRequest createFixture() {
        VMJobRequest vmJobRequest = new VMJobRequest("job-1", "none", "STANDARD", 1,
                VMRegion.US_EAST_2, IncrementStrategy.increasing, "END_OF_SCRIPT", "m.xlarge", 4000);
        return new JobRequest(vmJobRequest, new VMTrackerImpl());
    }

    private VMInformation buildVmInfo() {
        VMInformation info = new VMInformation();
        info.setPrivateIp("10.0.0.5");
        info.setPrivateDNS("ip-10-0-0-5.ec2.internal");
        info.setPublicIp("54.12.34.56");
        info.setPublicDNS("ec2-public.example.com");
        return info;
    }
}
