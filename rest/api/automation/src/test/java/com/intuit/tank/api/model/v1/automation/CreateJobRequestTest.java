package com.intuit.tank.api.model.v1.automation;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>CreateJobRequestTest</code> contains tests for the class <code>{@link CreateJobRequest}</code>.
 * 
 * @author msreekakula
 */
public class CreateJobRequestTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCreateJobRequest() throws JAXBException {
        CreateJobRequest createJobRequest = new CreateJobRequest();
        assertNull(createJobRequest.getProjectName());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCreateJobRequestWithProjectName() throws JAXBException {
        CreateJobRequest createJobRequest = new CreateJobRequest("test");
        assertEquals(createJobRequest.getProjectName(),"test");
        assertNull(createJobRequest.getJobInstanceName());
        assertNull(createJobRequest.getRampTime());
        assertNull(createJobRequest.getSimulationTime());
        assertEquals(createJobRequest.getUserIntervalIncrement(), 0);
        assertTrue(createJobRequest.getJobRegions().isEmpty());
        assertEquals(createJobRequest.getStopBehavior(), StopBehavior.END_OF_TEST.name());
        assertNull(createJobRequest.getVmInstance());
        assertEquals(createJobRequest.getNumUsersPerAgent(), 0);
    }

}
