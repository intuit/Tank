package com.intuit.tank.api.model.v1.job;

/*
 * #%L
 * Job Rest Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.DateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.job.JobTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>JobTOTest</code> contains tests for the class <code>{@link JobTO}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:07 PM
 */
public class JobTOTest {
    /**
     * Run the JobTO() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testJobTO_1()
            throws Exception {
        JobTO result = new JobTO();
        assertNotNull(result);
    }

    /**
     * Run the Date getEndTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetEndTime_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Date result = fixture.getEndTime();

        assertNotNull(result);
    }

    /**
     * Run the Integer getId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetId_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Integer result = fixture.getId();

        assertNotNull(result);
        assertEquals("1", result.toString());
        assertEquals((byte) 1, result.byteValue());
        assertEquals((short) 1, result.shortValue());
        assertEquals(1, result.intValue());
        assertEquals(1L, result.longValue());
        assertEquals(1.0f, result.floatValue(), 1.0f);
        assertEquals(1.0, result.doubleValue(), 1.0);
    }

    /**
     * Run the String getLocation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetLocation_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        String result = fixture.getLocation();

        assertEquals("", result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the int getNumUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetNumUsers_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        int result = fixture.getNumUsers();

        assertEquals(1, result);
    }

    /**
     * Run the long getRampTimeMilis() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetRampTimeMilis_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        long result = fixture.getRampTimeMilis();

        assertEquals(1L, result);
    }

    /**
     * Run the long getSimulationTimeMilis() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetSimulationTimeMilis_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        long result = fixture.getSimulationTimeMilis();

        assertEquals(1L, result);
    }

    /**
     * Run the Date getStartTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetStartTime_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Date result = fixture.getStartTime();

        assertNotNull(result);
    }

    /**
     * Run the String getStatus() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetStatus_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        String result = fixture.getStatus();

        assertEquals("", result);
    }

    /**
     * Run the Date getSteadyStateEndTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetSteadyStateEndTime_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(0L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Date result = fixture.getSteadyStateEndTime();

        assertNotNull(result);
    }

    /**
     * Run the Date getSteadyStateEndTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetSteadyStateEndTime_2()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(0L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime((Date) null);
        fixture.setStatus("");

        Date result = fixture.getSteadyStateEndTime();

        assertEquals(null, result);
    }

    /**
     * Run the Date getSteadyStateEndTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetSteadyStateEndTime_3()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Date result = fixture.getSteadyStateEndTime();

        assertNotNull(result);
    }

    /**
     * Run the Date getSteadyStateEndTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetSteadyStateEndTime_4()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime((Date) null);
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Date result = fixture.getSteadyStateEndTime();

        assertEquals(null, result);
    }

    /**
     * Run the Date getSteadyStateStartTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetSteadyStateStartTime_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Date result = fixture.getSteadyStateStartTime();

        assertNotNull(result);
    }

    /**
     * Run the Date getSteadyStateStartTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testGetSteadyStateStartTime_2()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime((Date) null);
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        Date result = fixture.getSteadyStateStartTime();

        assertEquals(null, result);
    }

    /**
     * Run the void setEndTime(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetEndTime_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        Date endTime = new Date();

        fixture.setEndTime(endTime);

    }

    /**
     * Run the void setId(Integer) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetId_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        Integer id = new Integer(1);

        fixture.setId(id);

    }

    /**
     * Run the void setLocation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetLocation_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        String location = "";

        fixture.setLocation(location);

    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setNumUsers(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetNumUsers_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        int numUsers = 1;

        fixture.setNumUsers(numUsers);

    }

    /**
     * Run the void setRampTimeMilis(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetRampTimeMilis_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        long rampTimeMilis = 1L;

        fixture.setRampTimeMilis(rampTimeMilis);

    }

    /**
     * Run the void setSimulationTimeMilis(long) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetSimulationTimeMilis_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        long simulationTimeMilis = 1L;

        fixture.setSimulationTimeMilis(simulationTimeMilis);

    }

    /**
     * Run the void setStartTime(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetStartTime_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        Date startTime = new Date();

        fixture.setStartTime(startTime);

    }

    /**
     * Run the void setStatus(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testSetStatus_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");
        String status = "";

        fixture.setStatus(status);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:07 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        JobTO fixture = new JobTO();
        fixture.setSimulationTimeMilis(1L);
        fixture.setStartTime(new Date());
        fixture.setName("");
        fixture.setId(new Integer(1));
        fixture.setLocation("");
        fixture.setRampTimeMilis(1L);
        fixture.setNumUsers(1);
        fixture.setEndTime(new Date());
        fixture.setStatus("");

        String result = fixture.toString();

    }
}