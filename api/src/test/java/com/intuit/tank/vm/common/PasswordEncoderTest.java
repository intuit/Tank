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

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PasswordEncoderTest
 * 
 * @author dangleton
 * 
 */
public class PasswordEncoderTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testEncoding() {
        String encodePassword = PasswordEncoder.encodePassword("admin");
        assertNotNull(encodePassword);
        assertTrue(PasswordEncoder.validatePassword("admin", encodePassword));
    }
}
