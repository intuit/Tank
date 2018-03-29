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

import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;

import com.intuit.tank.harness.test.data.Variables;

/**
 * 
 * NumericFunctions
 * 
 * @author dangleton
 * 
 */
class NumericFunctions {

    static Random rnd = new Random();

    /**
     * Is this a valid Numeric function request
     * 
     * @param values
     *            The command
     * @return TRUE if valid format; FALSE otherwise
     */
    static public boolean isValid(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("randompositivewhole")
                    || values[2].equalsIgnoreCase("randomnegativewhole")) {
                if (NumberUtils.isDigits(values[3])) {
                    return true;
                }
            }
            if (values[2].equalsIgnoreCase("randompositivefloat") || values[2].equalsIgnoreCase("randomnegativefloat")
                    || values[2].equalsIgnoreCase("mod")) {
                if (NumberUtils.isDigits(values[3]) && NumberUtils.isDigits(values[4])) {
                    return true;
                }
            }
            if (values[2].equalsIgnoreCase("add")
                    || values[2].equalsIgnoreCase("subtract")) {
                if (NumberUtils.isCreatable(values[3]) && NumberUtils.isCreatable(values[4])) {
                    for (int i = 5; i < values.length; i++) {
                        if (values[i] != null) {
                            if (!NumberUtils.isCreatable(values[i])) {
                                return false;
                            }
                        } else {
                            return true;
                        }
                    }
                }
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
     * @param variables
     * @return The requested value; "" if there was an error
     */
    static public String executeFunction(String[] values, Variables variables) {
        try {
            if (values[2].equalsIgnoreCase("mod")) {
                return NumericFunctions.mod(NumberUtils.toInt(values[3]), NumberUtils.toInt(values[4]));
            } else if (values[2].equalsIgnoreCase("randompositivewhole")) {
                return NumericFunctions.randomPositiveWhole(NumberUtils.toInt(values[3]));
            } else if (values[2].equalsIgnoreCase("randomnegativewhole")) {
                return NumericFunctions.randomNegativeWhole(NumberUtils.toInt(values[3]));
            } else if (values[2].equalsIgnoreCase("randompositivefloat")) {
                return NumericFunctions.randomPositiveFloat(NumberUtils.toInt(values[3]), NumberUtils.toInt(values[4]));
            } else if (values[2].equalsIgnoreCase("randomnegativefloat")) {
                return NumericFunctions.randomNegativeFloat(NumberUtils.toInt(values[3]), NumberUtils.toInt(values[4]));
            } else if (values[2].equalsIgnoreCase("add")) {
                return NumericFunctions.add(values, variables);
            } else if (values[2].equalsIgnoreCase("subtract")) {
                return NumericFunctions.subtract(values, variables);
            }
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * @param valueOf
     * @return
     */
    private static String mod(int num, int mod) {
        return Integer.toString(num % mod);
    }

    private static String add(String[] values, Variables variables) {
        double result = 0;
        for (int i = 3; i < values.length; i++) {
            if (values[i] != null) {
                result += NumberUtils.toDouble(values[i]);
            } else {
                break;
            }
        }
        return Double.toString(result);
    }

    private static String subtract(String[] values, Variables variables) {
        Double result = null;
        for (int i = 3; i < values.length; i++) {
            if (values[i] != null) {
                if (result == null) {
                    result = NumberUtils.toDouble(values[i]);
                } else {
                    result -= NumberUtils.toDouble(values[i]);
                }
            } else {
                break;
            }
        }
        return Double.toString(result);
    }

    /**
     * Get a random, positive whole number
     * 
     * @param length
     *            The length of the full numbers
     * @return A random whole number
     */
    static public String randomPositiveWhole(int length) {
        if (length == 0) {
            return "";
        }
        StringBuilder output = new StringBuilder(length);
        output.append(rnd.nextInt(8) + 1);
        for (int i = 0; i < length - 1; i++) {
            output.append(rnd.nextInt(9));
        }
        return output.toString();
    }

    /**
     * Get a random, negative whole number
     * 
     * @param length
     *            The length of the full numbers
     * @return A random whole number
     */
    static public String randomNegativeWhole(int length) {
        return "-" + NumericFunctions.randomPositiveWhole(length);
    }

    /**
     * Get a random, positive float
     * 
     * @param whole
     *            The number of whole digits
     * @param decimal
     *            The number of decimal digits
     * @return A random float
     */
    static public String randomPositiveFloat(int whole, int decimal) {
        StringBuilder output = new StringBuilder();
        output.append(rnd.nextInt(8) + 1);
        for (int i = 0; i < whole - 1; i++) {
            output.append(rnd.nextInt(9));
        }
        output.append(".");
        for (int i = 0; i < decimal; i++) {
            output.append(rnd.nextInt(9));
        }
        return output.toString();
    }

    /**
     * Get a random, negative float
     * 
     * @param whole
     *            The number of whole digits
     * @param decimal
     *            The number of decimal digits
     * @return A random float
     */
    static public String randomNegativeFloat(int whole, int decimal) {
        return "-" + NumericFunctions.randomPositiveFloat(whole, decimal);
    }
}