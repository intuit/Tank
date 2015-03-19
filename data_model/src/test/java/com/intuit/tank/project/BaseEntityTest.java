package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.JobConfiguration;

/**
 * The class <code>BaseEntityTest</code> contains tests for the class <code>{@link BaseEntity}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class BaseEntityTest {
    /**
     * Run the Date getCreated() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetCreated_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();
        fixture.initializeDates();
        Date result = fixture.getCreated();
        assertNotNull(result);
    }

    /**
     * Run the int getId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetId_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();

        int result = fixture.getId();
        assertEquals(0, result);
    }

    /**
     * Run the Date getModified() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetModified_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();

        fixture.initializeDates();
        Date result = fixture.getModified();
        assertNotNull(result);
    }

    /**
     * Run the void initializeDates() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testInitializeDates_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();

        fixture.initializeDates();

    }

    /**
     * Run the void initializeDates() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testInitializeDates_2()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();

        fixture.initializeDates();

    }

    /**
     * Run the String reflectionToString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testReflectionToString_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();

        String result = fixture.reflectionToString();
        assertNotNull(result);
    }

    /**
     * Run the void setCreated(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetCreated_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();
        Date creater = new Date();

        fixture.setCreated(creater);

    }

    /**
     * Run the void setForceCreateDate(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetForceCreateDate_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();
        Date forceCreateDate = new Date();

        fixture.setForceCreateDate(forceCreateDate);

    }

    /**
     * Run the void setId(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetId_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();
        int id = 1;

        fixture.setId(id);

    }

    /**
     * Run the void setModified(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetModified_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();
        Date modified = new Date();

        fixture.setModified(modified);

    }

    /**
     * Run the void updateModified() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testUpdateModified_1()
            throws Exception {
        BaseEntity fixture = new JobConfiguration();

        fixture.updateModified();
    }
}