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
import com.intuit.tank.vm.vmManager.VMUpdateStateRequest;

/**
 * The class <code>VMUpdateStateRequestCpTest</code> contains tests for the class
 * <code>{@link VMUpdateStateRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class VMUpdateStateRequestCpTest {
    /**
     * Run the VMUpdateStateRequest() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testVMUpdateStateRequest_1()
            throws Exception {

        VMUpdateStateRequest result = new VMUpdateStateRequest();

        assertNotNull(result);
        assertEquals(null, result.getInstances());
        assertEquals(null, result.getProvider());
    }

    /**
     * Run the VMUpdateStateRequest(VMProvider,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testVMUpdateStateRequest_2()
            throws Exception {
        VMProvider provider = VMProvider.Amazon;
        String instanceId = "";

        VMUpdateStateRequest result = new VMUpdateStateRequest(provider, instanceId);

        assertNotNull(result);
    }

    /**
     * Run the VMUpdateStateRequest(VMProvider,String[]) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testVMUpdateStateRequest_3()
            throws Exception {
        VMProvider provider = VMProvider.Amazon;

        VMUpdateStateRequest result = new VMUpdateStateRequest(provider);

        assertNotNull(result);
    }

    /**
     * Run the String[] getInstances() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetInstances_1()
            throws Exception {
        VMUpdateStateRequest fixture = new VMUpdateStateRequest();
        fixture.items = new HashMap();

        String[] result = fixture.getInstances();

        assertEquals(null, result);
    }

    /**
     * Run the void setInstances(String[]) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetInstances_1()
            throws Exception {
        VMUpdateStateRequest fixture = new VMUpdateStateRequest();
        fixture.items = new HashMap();

        fixture.setInstances();

    }
}