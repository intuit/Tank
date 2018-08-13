package com.intuit.tank.rest;

/*
 * #%L
 * Rest Client Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.rest.RestServiceException;
import com.intuit.tank.test.TestGroups;

import org.testng.Assert;

import org.testng.annotations.Test;

/**
 * Summary
 * 
 * @author wlee5
 */
public class RestServiceExceptionTest {
    @Test(groups = TestGroups.FUNCTIONAL)
    public void testGetStatusCode() throws Exception {
        Assert.assertTrue(new RestServiceException("hi", 1).getStatusCode() == 1);
    }
}
