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

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Summary
 * 
 * @author wlee5
 */
public class RestServiceExceptionTest {
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetStatusCode() throws Exception {
        assertTrue(new RestServiceException("hi", 1).getStatusCode() == 1);
    }
}
