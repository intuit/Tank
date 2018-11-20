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

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.common.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>PasswordEncoderCpTest</code> contains tests for the class <code>{@link PasswordEncoder}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class PasswordEncoderCpTest {
    /**
     * Run the String encodePassword(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testEncodePassword_1()
            throws Exception {
        String password = "";

        String result = PasswordEncoder.encodePassword(password);

        assertEquals("2jmj7l5rSw0yVb/vlWAYkK/YBwk=", result);
    }

    /**
     * Run the String encodePassword(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testEncodePassword_2()
            throws Exception {
        String password = "";

        String result = PasswordEncoder.encodePassword(password);

        assertEquals("2jmj7l5rSw0yVb/vlWAYkK/YBwk=", result);
    }

    /**
     * Run the boolean validatePassword(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testValidatePassword_1()
            throws Exception {
        String raw = "";
        String encoded = "";

        boolean result = PasswordEncoder.validatePassword(raw, encoded);

        assertEquals(false, result);
    }

    /**
     * Run the boolean validatePassword(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testValidatePassword_2()
            throws Exception {
        String raw = "";
        String encoded = "";

        boolean result = PasswordEncoder.validatePassword(raw, encoded);

        assertEquals(false, result);
    }
}