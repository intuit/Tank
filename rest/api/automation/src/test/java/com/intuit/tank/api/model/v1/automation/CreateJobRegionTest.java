package com.intuit.tank.api.model.v1.automation;


import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The class <code>CreateJobRegionTest</code> contains tests for the class <code>{@link CreateJobRegion}</code>.
 *
 * @author msreekakula
 */
public class CreateJobRegionTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCreateJobRegion() throws JAXBException {
        CreateJobRegion createJobRegion = new CreateJobRegion();
        assertNull(createJobRegion.getRegion());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCreateJobRegionWithUsers() throws JAXBException {
        CreateJobRegion createJobRegion = new CreateJobRegion("regionA", "users");
        assertEquals(createJobRegion.getRegion(), "regionA");
        assertEquals(createJobRegion.getUsers(), "users");
    }

}
