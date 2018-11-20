package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDValidation;
import com.intuit.tank.harness.data.ValidationData;

/**
 * The class <code>HDValidationTest</code> contains tests for the class <code>{@link HDValidation}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDValidationTest {
    /**
     * Run the HDValidation() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDValidation_1()
            throws Exception {
        HDValidation result = new HDValidation();
        assertNotNull(result);
    }

    /**
     * Run the List<ValidationData> getBodyValidation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetBodyValidation_1()
            throws Exception {
        HDValidation fixture = new HDValidation();

        List<ValidationData> result = fixture.getBodyValidation();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ValidationData> getCookieValidation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetCookieValidation_1()
            throws Exception {
        HDValidation fixture = new HDValidation();

        List<ValidationData> result = fixture.getCookieValidation();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ValidationData> getHeaderValidation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetHeaderValidation_1()
            throws Exception {
        HDValidation fixture = new HDValidation();

        List<ValidationData> result = fixture.getHeaderValidation();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}