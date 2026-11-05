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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

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

        return key.charAt(0) == identifierChar;
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
        return key.indexOf("#{") == 0 && key.indexOf("}") == key.length() - 1;
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
        return function.startsWith(functionIdentifier);
    }

    public static String removeVariableIdentifier(String key) {
        return removeIdentifier(key, identifierChar);
    }

    public static String removeAllVariableIdentifier(String key) {
        key = removeIdentifier(key, identifierChar);
        key = Strings.CS.removeStart(key, "#");
        key = Strings.CS.removeStart(key, "{");
        return Strings.CS.removeEnd(key, "}");
    }

    private static String removeIdentifier(String key, char c) {
        return (key.charAt(0) == c) ? key.replaceFirst(Character.toString(c), "") : key;
    }
}
