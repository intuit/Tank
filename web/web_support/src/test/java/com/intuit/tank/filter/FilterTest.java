package com.intuit.tank.filter;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.filter.Filter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>FilterTest</code> contains tests for the class <code>{@link Filter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class FilterTest {
    /**
     * Run the Filter(int,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testFilter_1()
        throws Exception {
        int id = 1;
        String name = "";
        String product = "";

        Filter result = new Filter(id, name, product);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
        assertNotNull(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");
        Object obj = new Filter(1, "", "");

        boolean result = fixture.equals(obj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        Filter fixture = new Filter(1, "", "");
        Object obj = null;

        boolean result = fixture.equals(obj);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_4()
        throws Exception {
        Filter fixture = new Filter(1, "", "");
        Object obj = new Filter(1, "", "");

        boolean result = fixture.equals(obj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEquals_5()
        throws Exception {
        Filter fixture = new Filter(1, "", "");
        Object obj = new Filter(1, "", "");

        boolean result = fixture.equals(obj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
        assertTrue(result);
    }

    /**
     * Run the int getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");

        int result = fixture.getId();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
        assertEquals(1, result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");

        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
        assertNotNull(result);
    }

    /**
     * Run the String getProduct() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetProduct_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");

        String result = fixture.getProduct();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
        assertNotNull(result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");

        int result = fixture.hashCode();
    }

    /**
     * Run the void setId(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetId_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");
        int id = 1;

        fixture.setId(id);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");
        String name = "";

        fixture.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
    }

    /**
     * Run the void setProduct(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetProduct_1()
        throws Exception {
        Filter fixture = new Filter(1, "", "");
        String product = "";

        fixture.setProduct(product);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.filter.Filter.<init>(Filter.java:10)
    }
}