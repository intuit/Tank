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

import java.util.Collection;
import java.util.LinkedList;

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.vmManager.JobUtil;
import com.intuit.tank.vm.vmManager.RegionRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>JobUtilCpTest</code> contains tests for the class <code>{@link JobUtil}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class JobUtilCpTest {
    /**
     * Run the int calculateTotalVirtualUsers(Collection<? extends RegionRequest>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testCalculateTotalVirtualUsers_1()
            throws Exception {
        Collection<? extends RegionRequest> jobRegions = new LinkedList();

        int result = JobUtil.calculateTotalVirtualUsers(jobRegions);

        assertEquals(0, result);
    }

    /**
     * Run the int calculateTotalVirtualUsers(Collection<? extends RegionRequest>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testCalculateTotalVirtualUsers_2()
            throws Exception {
        Collection<? extends RegionRequest> jobRegions = new LinkedList();

        int result = JobUtil.calculateTotalVirtualUsers(jobRegions);

        assertEquals(0, result);
    }

    /**
     * Run the int parseUserString(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testParseUserString_1()
            throws Exception {
        String users = "";

        int result = JobUtil.parseUserString(users);

        assertEquals(0, result);
    }
}