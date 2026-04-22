package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.jobs.models.JobTO;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JobServiceUtilTest {

    @Test
    void jobToTO_convertsAllFields() {
        JobInstance job = new JobInstance();
        job.setId(42);
        job.setName("LoadTest1");
        job.setLocation("us-west-2");
        job.setStatus(JobQueueStatus.Running);
        job.setTotalVirtualUsers(1000);
        job.setRampTime(60000L);
        job.setSimulationTime(300000L);
        job.setStartTime(new Date());
        job.setEndTime(new Date());

        JobTO to = JobServiceUtil.jobToTO(job);

        assertEquals(42, to.getId());
        assertEquals("LoadTest1", to.getName());
        assertEquals("us-west-2", to.getLocation());
        assertEquals("Running", to.getStatus());
        assertEquals(1000, to.getNumUsers());
        assertEquals(60000L, to.getRampTimeMilis());
        assertEquals(300000L, to.getSimulationTimeMilis());
    }
}
