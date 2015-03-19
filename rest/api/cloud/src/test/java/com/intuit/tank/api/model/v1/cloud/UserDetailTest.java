package com.intuit.tank.api.model.v1.cloud;

/*
 * #%L
 * Cloud Rest API
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

import org.junit.*;

import com.intuit.tank.api.model.v1.cloud.UserDetail;

import static org.junit.Assert.*;

/**
 * The class <code>UserDetailTest</code> contains tests for the class <code>{@link UserDetail}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:57 PM
 */
public class UserDetailTest {
    /**
     * Run the UserDetail() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testUserDetail_1()
        throws Exception {

        UserDetail result = new UserDetail();

        assertNotNull(result);
        assertEquals(null, result.getScript());
        assertEquals(null, result.getCreateTime());
        assertEquals(null, result.getUsers());
    }

    /**
     * Run the UserDetail(String,Integer) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testUserDetail_2()
        throws Exception {
        String script = "";
        Integer users = new Integer(1);

        UserDetail result = new UserDetail(script, users);

        assertNotNull(result);
        assertEquals("", result.getScript());
        assertEquals(new Integer(1), result.getUsers());
    }

    /**
     * Run the int compareTo(UserDetail) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        UserDetail fixture = new UserDetail("", new Integer(1));
        UserDetail o = new UserDetail("", new Integer(1));

        int result = fixture.compareTo(o);

        assertEquals(0, result);
    }

    /**
     * Run the Date getCreateTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetCreateTime_1()
        throws Exception {
        UserDetail fixture = new UserDetail("", new Integer(1));

        Date result = fixture.getCreateTime();

        assertNotNull(result);
    }

    /**
     * Run the String getScript() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetScript_1()
        throws Exception {
        UserDetail fixture = new UserDetail("", new Integer(1));

        String result = fixture.getScript();

        assertEquals("", result);
    }

    /**
     * Run the Integer getUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testGetUsers_1()
        throws Exception {
        UserDetail fixture = new UserDetail("", new Integer(1));

        Integer result = fixture.getUsers();

        assertNotNull(result);
        assertEquals("1", result.toString());
        assertEquals((byte) 1, result.byteValue());
        assertEquals((short) 1, result.shortValue());
        assertEquals(1, result.intValue());
        assertEquals(1L, result.longValue());
        assertEquals(1.0f, result.floatValue(), 1.0f);
        assertEquals(1.0, result.doubleValue(), 1.0);
    }
}