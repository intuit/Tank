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
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.vmManager.VMKillRequest;

/**
 * The class <code>VMKillRequestCpTest</code> contains tests for the class <code>{@link VMKillRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class VMKillRequestCpTest {
    /**
     * Run the VMKillRequest() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testVMKillRequest_1()
            throws Exception {

        VMKillRequest result = new VMKillRequest();

        assertNotNull(result);
        assertEquals(null, result.getInstances());
        assertEquals(null, result.getProvider());
    }

    /**
     * Run the VMKillRequest(VMProvider,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testVMKillRequest_2()
            throws Exception {
        VMProvider provider = VMProvider.Amazon;
        String instanceId = "";

        VMKillRequest result = new VMKillRequest(provider, instanceId);

        assertNotNull(result);
    }

    /**
     * Run the VMKillRequest(VMProvider,List<String>) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testVMKillRequest_3()
            throws Exception {
        VMProvider provider = VMProvider.Amazon;
        List<String> instanceIds = new LinkedList<String>();

        VMKillRequest result = new VMKillRequest(provider, instanceIds);

        assertNotNull(result);
    }

    /**
     * Run the List<String> getInstances() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetInstances_1()
            throws Exception {
        VMKillRequest fixture = new VMKillRequest();
        fixture.items = new HashMap<String,Object>();

        List<String> result = fixture.getInstances();

        assertEquals(null, result);
    }

    /**
     * Run the void setInstances(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetInstances_1()
            throws Exception {
        VMKillRequest fixture = new VMKillRequest();
        fixture.items = new HashMap<String,Object>();
        List<String> data = new LinkedList<String>();

        fixture.setInstances(data);

    }
}