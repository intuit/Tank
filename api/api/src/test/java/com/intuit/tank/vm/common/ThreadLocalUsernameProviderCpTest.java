package com.intuit.tank.vm.common;

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

import com.intuit.tank.vm.common.ThreadLocalUsernameProvider;
import com.intuit.tank.vm.common.UsernameProvider;

import static org.junit.Assert.*;

/**
 * The class <code>ThreadLocalUsernameProviderCpTest</code> contains tests for the class
 * <code>{@link ThreadLocalUsernameProvider}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class ThreadLocalUsernameProviderCpTest {
    /**
     * Run the ThreadLocalUsernameProvider() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testThreadLocalUsernameProvider_1()
            throws Exception {
        ThreadLocalUsernameProvider result = new ThreadLocalUsernameProvider();
        assertNotNull(result);
    }

    /**
     * Run the UsernameProvider getUsernameProvider() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetUsernameProvider_1()
            throws Exception {

        UsernameProvider result = ThreadLocalUsernameProvider.getUsernameProvider();

        assertNotNull(result);
        assertEquals("system", result.getUserName());
    }

    /**
     * Run the UsernameProvider initialValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testInitialValue_1()
            throws Exception {
        ThreadLocalUsernameProvider fixture = new ThreadLocalUsernameProvider();

        UsernameProvider result = fixture.initialValue();

        assertNotNull(result);
        assertEquals("system", result.getUserName());
    }
}