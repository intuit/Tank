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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.Group;

/**
 * The class <code>GroupTest</code> contains tests for the class <code>{@link Group}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class GroupTest {
    /**
     * Run the Group() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGroup_1()
        throws Exception {

        Group result = new Group();

        assertNotNull(result);
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the Group(String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGroup_2()
        throws Exception {
        String name = "";

        Group result = new Group(name);

        assertNotNull(result);
        assertEquals("", result.toString());
        assertEquals("", result.getName());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        Group fixture = new Group("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        Group fixture = new Group("");
        Object obj = new Group();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        Group fixture = new Group("");
        Object obj = new Group();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        Group fixture = new Group("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        Group fixture = new Group("");

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        Group fixture = new Group("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        Group fixture = new Group("");

        String result = fixture.toString();

        assertEquals("", result);
    }
}