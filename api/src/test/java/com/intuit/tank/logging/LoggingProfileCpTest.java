package com.intuit.tank.logging;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.logging.LogFields;
import com.intuit.tank.logging.LoggingProfile;

import static org.junit.Assert.*;

/**
 * The class <code>LoggingProfileCpTest</code> contains tests for the class <code>{@link LoggingProfile}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class LoggingProfileCpTest {
    /**
     * Run the LoggingProfile fromString(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testFromString_1()
            throws Exception {
        String s = "";

        LoggingProfile result = LoggingProfile.fromString(s);

        assertNotNull(result);
        assertEquals("STANDARD", result.name());
        assertEquals("STANDARD", result.toString());
    }

    /**
     * Run the LoggingProfile fromString(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testFromString_2()
            throws Exception {
        String s = "";

        LoggingProfile result = LoggingProfile.fromString(s);

        assertNotNull(result);
        assertEquals("STANDARD", result.name());
        assertEquals("STANDARD", result.toString());
    }

    /**
     * Run the String getDescription() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDescription_1()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;

        String result = fixture.getDescription();
        assertNotNull(result);

    }

    /**
     * Run the String getDisplayName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDisplayName_1()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;

        String result = fixture.getDisplayName();

        assertNotNull(result);
    }

    /**
     * Run the LogFields[] getFieldsToLog() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetFieldsToLog_1()
    {
        LoggingProfile fixture = LoggingProfile.STANDARD;

        LogFields[] result = fixture.getFieldsToLog();

        assertNotNull(result);
    }

    /**
     * Run the boolean isFieldLogged(LogFields) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsFieldLogged_1()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;
        LogFields field = LogFields.EventType;

        boolean result = fixture.isFieldLogged(field);

    }

    /**
     * Run the boolean isFieldLogged(LogFields) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsFieldLogged_2()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;
        LogFields field = LogFields.EventType;

        boolean result = fixture.isFieldLogged(field);

    }

    /**
     * Run the boolean isFieldLogged(LogFields) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testIsFieldLogged_3()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;
        LogFields field = LogFields.EventType;

        boolean result = fixture.isFieldLogged(field);

    }

    /**
     * Run the void setDescription(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetDescription_1()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;
        String description = "";

        fixture.setDescription(description);

    }

    /**
     * Run the void setDisplayName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetDisplayName_1()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;
        String displayName = "";

        fixture.setDisplayName(displayName);

    }

    /**
     * Run the void setFieldsToLog(LogFields[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetFieldsToLog_1()
            throws Exception {
        LoggingProfile fixture = LoggingProfile.STANDARD;
        LogFields[] fieldsToLog = new LogFields[] {};

        fixture.setFieldsToLog(fieldsToLog);

    }
}