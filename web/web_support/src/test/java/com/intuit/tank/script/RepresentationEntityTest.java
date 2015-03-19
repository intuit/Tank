package com.intuit.tank.script;

/*
 * #%L
 * JSF Support Beans
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

import com.intuit.tank.script.RepresentationEntity;

import static org.junit.Assert.*;

/**
 * The class <code>RepresentationEntityTest</code> contains tests for the class <code>{@link RepresentationEntity}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class RepresentationEntityTest {
    /**
     * Run the RepresentationEntity(String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRepresentationEntity_1()
        throws Exception {
        String representation = "";
        String value = "";

        RepresentationEntity result = new RepresentationEntity(representation, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RepresentationEntity.<init>(RepresentationEntity.java:11)
        assertNotNull(result);
    }

    /**
     * Run the String getRepresentation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRepresentation_1()
        throws Exception {
        RepresentationEntity fixture = new RepresentationEntity("", "");

        String result = fixture.getRepresentation();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RepresentationEntity.<init>(RepresentationEntity.java:11)
        assertNotNull(result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        RepresentationEntity fixture = new RepresentationEntity("", "");

        String result = fixture.getValue();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RepresentationEntity.<init>(RepresentationEntity.java:11)
        assertNotNull(result);
    }

    /**
     * Run the void setRepresentation(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetRepresentation_1()
        throws Exception {
        RepresentationEntity fixture = new RepresentationEntity("", "");
        String representation = "";

        fixture.setRepresentation(representation);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RepresentationEntity.<init>(RepresentationEntity.java:11)
    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        RepresentationEntity fixture = new RepresentationEntity("", "");
        String value = "";

        fixture.setValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RepresentationEntity.<init>(RepresentationEntity.java:11)
    }
}