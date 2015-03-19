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

import com.intuit.tank.project.ColumnPreferences;

/**
 * The class <code>ColumnPreferencesTest</code> contains tests for the class <code>{@link ColumnPreferences}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ColumnPreferencesTest {
    /**
     * Run the ColumnPreferences() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testColumnPreferences_1()
        throws Exception {

        ColumnPreferences result = new ColumnPreferences();

        assertNotNull(result);
        assertEquals(0, result.getSize());
        assertEquals(null, result.getDisplayName());
        assertEquals(true, result.isHideable());
        assertEquals(null, result.getColName());
        assertEquals(true, result.isVisible());
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the ColumnPreferences(String,String,int,Visibility,Hidability) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testColumnPreferences_2()
        throws Exception {
        String colName = "";
        String displayName = "";
        int size = 1;
        ColumnPreferences.Visibility visible = ColumnPreferences.Visibility.HIDDEN;
        ColumnPreferences.Hidability hidable = ColumnPreferences.Hidability.HIDABLE;

        ColumnPreferences result = new ColumnPreferences(colName, displayName, size, visible, hidable);

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("", result.getDisplayName());
        assertEquals(true, result.isHideable());
        assertEquals("", result.getColName());
        assertEquals(false, result.isVisible());
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the ColumnPreferences(String,String,int,Visibility,Hidability) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testColumnPreferences_3()
        throws Exception {
        String colName = "";
        String displayName = "";
        int size = 1;
        ColumnPreferences.Visibility visible = ColumnPreferences.Visibility.HIDDEN;
        ColumnPreferences.Hidability hidable = ColumnPreferences.Hidability.HIDABLE;

        ColumnPreferences result = new ColumnPreferences(colName, displayName, size, visible, hidable);

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("", result.getDisplayName());
        assertEquals(true, result.isHideable());
        assertEquals("", result.getColName());
        assertEquals(false, result.isVisible());
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the ColumnPreferences(String,String,int,Visibility,Hidability) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testColumnPreferences_4()
        throws Exception {
        String colName = "";
        String displayName = "";
        int size = 1;
        ColumnPreferences.Visibility visible = ColumnPreferences.Visibility.HIDDEN;
        ColumnPreferences.Hidability hidable = ColumnPreferences.Hidability.HIDABLE;

        ColumnPreferences result = new ColumnPreferences(colName, displayName, size, visible, hidable);

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("", result.getDisplayName());
        assertEquals(true, result.isHideable());
        assertEquals("", result.getColName());
        assertEquals(false, result.isVisible());
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the ColumnPreferences(String,String,int,Visibility,Hidability) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testColumnPreferences_5()
        throws Exception {
        String colName = "";
        String displayName = "";
        int size = 1;
        ColumnPreferences.Visibility visible = ColumnPreferences.Visibility.HIDDEN;
        ColumnPreferences.Hidability hidable = ColumnPreferences.Hidability.HIDABLE;

        ColumnPreferences result = new ColumnPreferences(colName, displayName, size, visible, hidable);

        assertNotNull(result);
        assertEquals(1, result.getSize());
        assertEquals("", result.getDisplayName());
        assertEquals(true, result.isHideable());
        assertEquals("", result.getColName());
        assertEquals(false, result.isVisible());
        assertEquals(null, result.getCreator());
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
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);
        Object obj = null;

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
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);
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
    public void testEquals_3()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);
        Object obj = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);

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
    public void testEquals_4()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);
        Object obj = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getColName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetColName_1()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        String result = fixture.getColName();

        assertEquals("", result);
    }

    /**
     * Run the String getDisplayName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetDisplayName_1()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", (String) null, 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        String result = fixture.getDisplayName();

        assertEquals("", result);
    }

    /**
     * Run the String getDisplayName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetDisplayName_2()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        String result = fixture.getDisplayName();

        assertEquals("", result);
    }

    /**
     * Run the int getSize() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSize_1()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        int result = fixture.getSize();

        assertEquals(1, result);
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
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        int result = fixture.hashCode();

        assertEquals(629, result);
    }

    /**
     * Run the boolean isHideable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHideable_1()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        boolean result = fixture.isHideable();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isHideable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsHideable_2()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        boolean result = fixture.isHideable();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isVisible() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsVisible_1()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        boolean result = fixture.isVisible();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isVisible() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsVisible_2()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(false);

        boolean result = fixture.isVisible();

        assertEquals(false, result);
    }

    /**
     * Run the void setSize(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSize_1()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);
        int size = 1;

        fixture.setSize(size);

    }

    /**
     * Run the void setVisible(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVisible_1()
        throws Exception {
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);
        boolean visible = true;

        fixture.setVisible(visible);

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
        ColumnPreferences fixture = new ColumnPreferences("", "", 1, ColumnPreferences.Visibility.HIDDEN, ColumnPreferences.Hidability.HIDABLE);
        fixture.setVisible(true);

        String result = fixture.toString();

    }
}