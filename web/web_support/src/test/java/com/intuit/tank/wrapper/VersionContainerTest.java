package com.intuit.tank.wrapper;

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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import com.intuit.tank.wrapper.VersionContainer;

import static org.junit.Assert.*;

/**
 * The class <code>VersionContainerTest</code> contains tests for the class <code>{@link VersionContainer}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class VersionContainerTest {
    /**
     * Run the VersionContainer(List<T>,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testVersionContainer_1()
            throws Exception {
        List<Serializable> entities = new LinkedList();
        int version = 1;

        VersionContainer result = new VersionContainer(entities, version);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.wrapper.VersionContainer.<init>(VersionContainer.java:25)
        assertNotNull(result);
    }

    /**
     * Run the List<Serializable> getEntities() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetEntities_1()
            throws Exception {
        VersionContainer fixture = new VersionContainer(new LinkedList(), 1);

        List<Serializable> result = fixture.getEntities();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.wrapper.VersionContainer.<init>(VersionContainer.java:25)
        assertNotNull(result);
    }

    /**
     * Run the int getVersion() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetVersion_1()
            throws Exception {
        VersionContainer fixture = new VersionContainer(new LinkedList(), 1);

        int result = fixture.getVersion();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.wrapper.VersionContainer.<init>(VersionContainer.java:25)
        assertEquals(1, result);
    }
}