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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.OwnableEntity;

/**
 * The class <code>OwnableEntityTest</code> contains tests for the class <code>{@link OwnableEntity}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class OwnableEntityTest {
    /**
     * Run the String getCreator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetCreator_1()
        throws Exception {
        OwnableEntity fixture = new ColumnPreferences();

        String result = fixture.getCreator();

        assertEquals(null, result);
    }

    /**
     * Run the void setCreator(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetCreator_1()
        throws Exception {
        OwnableEntity fixture = new ColumnPreferences();
        String creator = "";

        fixture.setCreator(creator);

    }
}