package com.intuit.tank.vm.settings;

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

import com.intuit.tank.vm.settings.Range;

import static org.junit.Assert.*;

/**
 * The class <code>RangeCpTest</code> contains tests for the class <code>{@link Range}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class RangeCpTest {
    /**
     * Run the Range(long,long) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testRange_1()
            throws Exception {
        long min = 1L;
        long max = 1L;

        Range result = new Range(min, max);

        assertNotNull(result);
        assertEquals(1L, result.getMin());
        assertEquals(1L, result.getMax());
        assertEquals(1L, result.getRandomValueWithin());
    }

    /**
     * Run the long getMax() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetMax_1()
            throws Exception {
        Range fixture = new Range(1L, 1L);

        long result = fixture.getMax();

        assertEquals(1L, result);
    }

    /**
     * Run the long getMin() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetMin_1()
            throws Exception {
        Range fixture = new Range(1L, 1L);

        long result = fixture.getMin();

        assertEquals(1L, result);
    }

    /**
     * Run the long getRandomValueWithin() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRandomValueWithin_1()
            throws Exception {
        Range fixture = new Range(1L, 1L);

        long result = fixture.getRandomValueWithin();

        assertEquals(1L, result);
    }

    /**
     * Run the long getRandomValueWithin() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRandomValueWithin_2()
            throws Exception {
        Range fixture = new Range(1L, 1L);

        long result = fixture.getRandomValueWithin();

        assertEquals(1L, result);
    }
}