package com.intuit.tank.vm.api.enumerated;

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

import com.intuit.tank.vm.api.enumerated.VMProvider;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>VMProviderCpTest</code> contains tests for the class <code>{@link VMProvider}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class VMProviderCpTest {
    /**
     * Run the VMProvider getProvider(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetProvider_1()
            throws Exception {
        String providerString = "Amazon";

        VMProvider result = VMProvider.getProvider(providerString);

        assertNotNull(result);
        assertEquals("Amazon", result.name());
        assertEquals("Amazon", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the VMProvider getProvider(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetProvider_2()
            throws Exception {
        String providerString = "Pharos";

        VMProvider result = VMProvider.getProvider(providerString);

        assertNotNull(result);
        assertEquals("Pharos", result.name());
        assertEquals("Pharos", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the VMProvider getProvider(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test()
    public void testGetProvider_3()
            throws Exception {
        String providerString = "";

        assertThrows(java.lang.IllegalArgumentException.class, () -> VMProvider.getProvider(providerString));
    }
}