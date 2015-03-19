/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common.util;

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

import org.apache.commons.lang.StringUtils;

/**
 * ValidationUtil
 * 
 * @author dangleton
 * 
 */
public class ValidationUtil {
    public static char identifierChar = '@';
    public static final String functionIdentifier = "#function";

    /**
     * Is this formatted as a variable
     * 
     * @param key
     *            The variable name
     * @return TRUE if it is formatted as a variable; FALSE otherwise
     */
    public static boolean isVariable(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }

        if (key.charAt(0) == identifierChar) {
            return true;
        }

        return false;
    }

    /**
     * Is this formatted as a variable
     * 
     * @param key
     *            The variable name
     * @return TRUE if it is formatted as a variable; FALSE otherwise
     */
    public static boolean isAnyVariable(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }

        if (key.charAt(0) == identifierChar) {
            return true;
        }
        if (key.indexOf("#{") == 0 && key.indexOf("}") == key.length() - 1) {
            return true;
        }

        return false;
    }

    /**
     * Is the string specified in the proper function format
     * 
     * @param function
     *            True if it starts with the function identifier; FALSE otherwise
     * @return
     */
    public static boolean isFunction(String function) {
        if (StringUtils.isEmpty(function)) {
            return false;
        }
        if (function.startsWith(functionIdentifier)) {
            return true;
        }
        return false;
    }

    public static final String removeVariableIdentifier(String key) {
        return removeIdentifier(key, identifierChar);
    }

    public static final String removeAllVariableIdentifier(String key) {
        key = removeIdentifier(key, identifierChar);
        key = StringUtils.removeStart(key, "#");
        key = StringUtils.removeStart(key, "{");
        return StringUtils.removeEnd(key, "}");
    }

    private static final String removeIdentifier(String key, char c) {
        if (key.charAt(0) == c) {
            key = key.replaceFirst(Character.toString(c), "");
        }
        return key;
    }
}
