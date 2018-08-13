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

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.settings.SelectableItem;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>SelectableItemCpTest</code> contains tests for the class <code>{@link SelectableItem}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class SelectableItemCpTest {
    /**
     * Run the SelectableItem(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSelectableItem_1()
            throws Exception {
        String displayName = "";
        String value = "";

        SelectableItem result = new SelectableItem(displayName, value);

        assertNotNull(result);
        assertEquals("", result.getValue());
        assertEquals("", result.getDisplayName());
    }

    /**
     * Run the int compareTo(SelectableItem) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testCompareTo_1()
            throws Exception {
        SelectableItem fixture = new SelectableItem("", "");
        SelectableItem o = new SelectableItem("", "");

        int result = fixture.compareTo(o);

        assertEquals(0, result);
    }

    /**
     * Run the String getDisplayName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetDisplayName_1()
            throws Exception {
        SelectableItem fixture = new SelectableItem("", "");

        String result = fixture.getDisplayName();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        SelectableItem fixture = new SelectableItem("", "");

        String result = fixture.getValue();

        assertEquals("", result);
    }
}