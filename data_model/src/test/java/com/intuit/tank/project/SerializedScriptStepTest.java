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

import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import org.junit.Test;

import com.intuit.tank.project.SerializedScriptStep;

/**
 * The class <code>SerializedScriptStepTest</code> contains tests for the class <code>{@link SerializedScriptStep}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class SerializedScriptStepTest {
    /**
     * Run the SerializedScriptStep() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSerializedScriptStep_1()
        throws Exception {

        SerializedScriptStep result = new SerializedScriptStep();

        assertNotNull(result);
        assertEquals(null, result.getBytes());
        assertEquals(null, result.getSerialzedBlob());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the SerializedScriptStep(byte[]) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSerializedScriptStep_2()
        throws Exception {
        byte[] serialzedData = new byte[] {};

        SerializedScriptStep result = new SerializedScriptStep(serialzedData);

        assertNotNull(result);
        assertEquals(null, result.getSerialzedBlob());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the byte[] getBytes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetBytes_1()
        throws Exception {
        SerializedScriptStep fixture = new SerializedScriptStep(new byte[] {});
        fixture.setSerialzedData(new SerialBlob(new byte[] {}));

        byte[] result = fixture.getBytes();

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Run the Blob getSerialzedBlob() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSerialzedBlob_1()
        throws Exception {
        SerializedScriptStep fixture = new SerializedScriptStep(new byte[] {});
        fixture.setSerialzedData(new SerialBlob(new byte[] {}));

        Blob result = fixture.getSerialzedBlob();

        assertNotNull(result);
        assertEquals(0L, result.length());
    }

    /**
     * Run the void setSerialzedData(Blob) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSerialzedData_1()
        throws Exception {
        SerializedScriptStep fixture = new SerializedScriptStep(new byte[] {});
        fixture.setSerialzedData(new SerialBlob(new byte[] {}));
        Blob serialzedData = new SerialBlob(new byte[] {});

        fixture.setSerialzedData(serialzedData);

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
        SerializedScriptStep fixture = new SerializedScriptStep(new byte[] {});
        fixture.setSerialzedData(new SerialBlob(new byte[] {}));

        String result = fixture.toString();

    }
}