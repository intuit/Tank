/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.vm.common.PasswordEncoder;

/**
 * PasswordEncoderTest
 * 
 * @author dangleton
 * 
 */
public class PasswordEncoderTest {

    @Test(groups = { "functional" })
    public void testEncoding() {
        String encodePassword = PasswordEncoder.encodePassword("admin");
        Assert.assertNotNull(encodePassword);
        Assert.assertTrue(PasswordEncoder.validatePassword("admin", encodePassword));
    }
}
