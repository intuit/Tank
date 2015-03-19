package com.intuit.tank.api.model.v1.filter;

/*
 * #%L
 * Filter Rest Api
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

import com.intuit.tank.api.model.v1.filter.FilterTO;

import static org.junit.Assert.*;

/**
 * The class <code>FilterTOTest</code> contains tests for the class <code>{@link FilterTO}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:06 PM
 */
public class FilterTOTest {
    /**
     * Run the FilterTO() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testFilterTO_1()
            throws Exception {
        FilterTO result = new FilterTO();
        assertNotNull(result);
    }

    /**
     * Run the Integer getId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testGetId_1()
            throws Exception {
        FilterTO fixture = new FilterTO();
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(new Integer(1));

        Integer result = fixture.getId();

        assertNotNull(result);
        assertEquals("1", result.toString());
        assertEquals((byte) 1, result.byteValue());
        assertEquals((short) 1, result.shortValue());
        assertEquals(1, result.intValue());
        assertEquals(1L, result.longValue());
        assertEquals(1.0f, result.floatValue(), 1.0f);
        assertEquals(1.0, result.doubleValue(), 1.0);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        FilterTO fixture = new FilterTO();
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(new Integer(1));

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getProductName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testGetProductName_1()
            throws Exception {
        FilterTO fixture = new FilterTO();
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(new Integer(1));

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the void setId(Integer) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testSetId_1()
            throws Exception {
        FilterTO fixture = new FilterTO();
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(new Integer(1));
        Integer id = new Integer(1);

        fixture.setId(id);

    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        FilterTO fixture = new FilterTO();
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(new Integer(1));
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setProductName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testSetProductName_1()
            throws Exception {
        FilterTO fixture = new FilterTO();
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(new Integer(1));
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:06 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        FilterTO fixture = new FilterTO();
        fixture.setName("");
        fixture.setProductName("");
        fixture.setId(new Integer(1));

        String result = fixture.toString();

    }
}