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

import org.junit.Test;

import com.intuit.tank.project.JobRecipient;
import com.intuit.tank.vm.api.enumerated.RecipientType;

/**
 * The class <code>JobRecipientTest</code> contains tests for the class <code>{@link JobRecipient}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class JobRecipientTest {
    /**
     * Run the JobRecipient() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobRecipient_1()
        throws Exception {

        JobRecipient result = new JobRecipient();

        assertNotNull(result);
        assertEquals(null, result.getAddress());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the JobRecipient(String,RecipientType) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobRecipient_2()
        throws Exception {
        String address = "";
        RecipientType type = RecipientType.email;

        JobRecipient result = new JobRecipient(address, type);

        assertNotNull(result);
        assertEquals("", result.getAddress());
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
        JobRecipient fixture = new JobRecipient("", RecipientType.email);
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
        JobRecipient fixture = new JobRecipient("", RecipientType.email);
        Object obj = new JobRecipient();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        JobRecipient fixture = new JobRecipient("", RecipientType.email);
        Object obj = new JobRecipient();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getAddress() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetAddress_1()
        throws Exception {
        JobRecipient fixture = new JobRecipient("", RecipientType.email);

        String result = fixture.getAddress();

        assertEquals("", result);
    }

    /**
     * Run the RecipientType getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        JobRecipient fixture = new JobRecipient("", RecipientType.email);

        RecipientType result = fixture.getType();

        assertNotNull(result);
        assertEquals("email", result.name());
        assertEquals("email", result.toString());
        assertEquals(0, result.ordinal());
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
        JobRecipient fixture = new JobRecipient("", RecipientType.email);

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void setAddress(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetAddress_1()
        throws Exception {
        JobRecipient fixture = new JobRecipient("", RecipientType.email);
        String address = "";

        fixture.setAddress(address);

    }

    /**
     * Run the void setType(RecipientType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetType_1()
        throws Exception {
        JobRecipient fixture = new JobRecipient("", RecipientType.email);
        RecipientType type = RecipientType.email;

        fixture.setType(type);

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
        JobRecipient fixture = new JobRecipient("", RecipientType.email);

        String result = fixture.toString();

    }
}