package com.intuit.tank.harness.functions;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.lang3.math.NumberUtils;

class MonetaryFunctions {

    /**
     * Is this a valid Monetary function request
     * 
     * @param values
     *            The command
     * @return TRUE if valid format; FALSE otherwise
     */
    public static boolean isValid(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("randompositive")
                    || values[2].equalsIgnoreCase("randomnegative")
                    && NumberUtils.isDigits(values[3])) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Process the numeric request
     * 
     * @param values
     *            The command line
     * @return The requested value; "" if there was an error
     */
    public static String executeFunction(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("randompositive"))
                return MonetaryFunctions.randomPositiveMonetary(Integer.valueOf(values[3]));
            else if (values[2].equalsIgnoreCase("randomnegative"))
                return MonetaryFunctions.randomNegativeMonetary(Integer.valueOf(values[3]));
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Get a random, positive monetary value
     * 
     * @param length
     *            The length of the full numbers
     * @return A random monetary value
     */
    private static String randomPositiveMonetary(int length) {
        return NumericFunctions.randomPositiveFloat(length, 2);
    }

    /**
     * Get a random, negative monetary value
     * 
     * @param length
     *            The length of the full numbers
     * @return A random monetary value
     */
    private static String randomNegativeMonetary(int length) {
        return "-" + MonetaryFunctions.randomPositiveMonetary(length);
    }
}