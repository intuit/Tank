package com.intuit.tank.vm.vmManager;

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

import java.util.HashMap;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMRequest;

/**
 * The class <code>VMRequestCpTest</code> contains tests for the class <code>{@link VMRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class VMRequestCpTest {
    /**
     * Run the VMProvider getProvider() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetProvider_1()
            throws Exception {
        VMInstanceRequest fixture = new VMInstanceRequest();
        fixture.items = new HashMap();

        VMProvider result = fixture.getProvider();

        assertEquals(null, result);
    }

    /**
     * Run the void setProvider(VMProvider) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetProvider_1()
            throws Exception {
        VMInstanceRequest fixture = new VMInstanceRequest();
        fixture.items = new HashMap();
        VMProvider data = VMProvider.Amazon;

        fixture.setProvider(data);

    }
}