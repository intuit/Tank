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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;

import org.apache.commons.codec.binary.Base64;

/**
 * PasswordEncoder
 * 
 * @author dangleton
 * 
 */
public class PasswordEncoder {

    public static final String DEFAULT_ALGORITHM = "SHA-1";

    /**
     * private no-arg constructor to enforce util pattern
     */
    private PasswordEncoder() {

    }

    /**
     * Encodes the password using SHA-1 algorithm.
     * 
     * @param password
     *            the password to encode
     * @return a base64 encoded has of the password.
     */
    public static final String encodePassword(String password) {
        try {
            byte[] digest = MessageDigest.getInstance(DEFAULT_ALGORITHM).digest(password.getBytes());
            return new String(Base64.encodeBase64(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates the password against the stored hashed password.
     * 
     * @param raw
     *            the raw (plain text) password
     * @param encoded
     *            the stored hash of the password
     * @return true if the passwords match
     */
    public static final boolean validatePassword(@Nonnull String raw, @Nonnull String encoded) {
        boolean result = false;
        try {
            String encodedRaw = encodePassword(raw);
            result = encodedRaw.equals(encoded);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
