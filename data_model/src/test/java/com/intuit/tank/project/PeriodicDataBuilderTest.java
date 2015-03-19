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

import com.intuit.tank.project.PeriodicData;
import com.intuit.tank.project.PeriodicDataBuilder;

/**
 * The class <code>PeriodicDataBuilderTest</code> contains tests for the class <code>{@link PeriodicDataBuilder}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class PeriodicDataBuilderTest {
    /**
     * Run the PeriodicDataBuilder() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testPeriodicDataBuilder_1()
        throws Exception {

        PeriodicDataBuilder result = new PeriodicDataBuilder();

        assertNotNull(result);
    }

    /**
     * Run the PeriodicData build() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuild_1()
        throws Exception {
        PeriodicDataBuilder fixture = new PeriodicDataBuilder();

        PeriodicData result = fixture.build();

        assertNotNull(result);
        assertEquals(null, result.getTimestamp());
        assertEquals(null, result.getPageId());
        assertEquals(0.0, result.getMax(), 1.0);
        assertEquals(0.0, result.getMean(), 1.0);
        assertEquals(0, result.getJobId());
        assertEquals(0, result.getSampleSize());
        assertEquals(0.0, result.getMin(), 1.0);
        assertEquals(0, result.getPeriod());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the PeriodicDataBuilder periodicData() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testPeriodicData_1()
        throws Exception {

        PeriodicDataBuilder result = PeriodicDataBuilder.periodicData();

        assertNotNull(result);
    }
}