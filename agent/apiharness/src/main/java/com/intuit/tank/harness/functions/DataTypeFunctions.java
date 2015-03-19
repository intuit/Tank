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

public class DataTypeFunctions {

    /**
     * Is this a valid function
     * 
     * @param values
     *            The parameter string
     * @return TRUE if it is a valid function; FALSE otherwise
     */
    static public boolean isValid(String[] values) {
        try {
            if (values[3] == "")
                return false;
            if (values[2].equalsIgnoreCase("BYTE_Max") || values[2].equalsIgnoreCase("BYTE_Min")
                    || values[2].equalsIgnoreCase("BYTE_Max_Plus") || values[2].equalsIgnoreCase("BYTE_Min_Minus")
                    || values[2].equalsIgnoreCase("SHORT_Max") || values[2].equalsIgnoreCase("SHORT_Min")
                    || values[2].equalsIgnoreCase("SHORT_Max_Plus") || values[2].equalsIgnoreCase("SHORT_Min_Minus")
                    || values[2].equalsIgnoreCase("INT_Max") || values[2].equalsIgnoreCase("INT_Min")
                    || values[2].equalsIgnoreCase("INT_Max_Plus") || values[2].equalsIgnoreCase("INT_Min_Minus")
                    || values[2].equalsIgnoreCase("LONG_Max") || values[2].equalsIgnoreCase("LONG_Min")
                    || values[2].equalsIgnoreCase("LONG_Max_Plus") || values[2].equalsIgnoreCase("LONG_Min_Minus"))
                return true;
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Execute the function
     * 
     * @param values
     *            The parameter string
     * @return The result of the function; "" if there was an issue
     */
    static public String executeFunction(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("BYTE_Max"))
                return DataTypeFunctions.BYTE_Max();
            else if (values[2].equalsIgnoreCase("BYTE_Min"))
                return DataTypeFunctions.BYTE_Min();
            else if (values[2].equalsIgnoreCase("BYTE_Max_Plus"))
                return DataTypeFunctions.BYTE_Max_Plus();
            else if (values[2].equalsIgnoreCase("BYTE_Min_Minus"))
                return DataTypeFunctions.BYTE_Min_Minus();
            else if (values[2].equalsIgnoreCase("SHORT_Max"))
                return DataTypeFunctions.SHORT_Max();
            else if (values[2].equalsIgnoreCase("SHORT_Min"))
                return DataTypeFunctions.SHORT_Min();
            else if (values[2].equalsIgnoreCase("SHORT_Max_Plus"))
                return DataTypeFunctions.SHORT_Max_Plus();
            else if (values[2].equalsIgnoreCase("SHORT_Min_Minus"))
                return DataTypeFunctions.SHORT_Min_Minus();
            else if (values[2].equalsIgnoreCase("INT_Max"))
                return DataTypeFunctions.INT_Max();
            else if (values[2].equalsIgnoreCase("INT_Min"))
                return DataTypeFunctions.INT_Min();
            else if (values[2].equalsIgnoreCase("INT_Max_Plus"))
                return DataTypeFunctions.INT_Max_Plus();
            else if (values[2].equalsIgnoreCase("INT_Min_Minus"))
                return DataTypeFunctions.INT_Min_Minus();
            else if (values[2].equalsIgnoreCase("LONG_Max"))
                return DataTypeFunctions.LONG_Max();
            else if (values[2].equalsIgnoreCase("LONG_Min"))
                return DataTypeFunctions.LONG_Min();
            else if (values[2].equalsIgnoreCase("LONG_Max_Plus"))
                return DataTypeFunctions.LONG_Max_Plus();
            else if (values[2].equalsIgnoreCase("LONG_Min_Minus"))
                return DataTypeFunctions.LONG_Min_Minus();
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Get the max value for a byte
     * 
     * @return The max value of a byte
     */
    static private String BYTE_Max() {
        byte tempValue = Byte.MAX_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the min value for a byte
     * 
     * @return The min value of a byte
     */
    static private String BYTE_Min() {
        byte tempValue = Byte.MIN_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the max value plus one for a byte
     * 
     * @return The max value plus one of a byte
     */
    static private String BYTE_Max_Plus() {
        long tempValue = Byte.MAX_VALUE + 1;
        return String.valueOf(tempValue);
    }

    /**
     * Get the min value minus one for a byte
     * 
     * @return The min value minus one of a byte
     */
    static private String BYTE_Min_Minus() {
        long tempValue = Byte.MIN_VALUE - 1;
        return String.valueOf(tempValue);
    }

    /**
     * Get the max value for a short
     * 
     * @return The max value of a short
     */
    static private String SHORT_Max() {
        short tempValue = Short.MAX_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the min value for a short
     * 
     * @return The min value of a short
     */
    static private String SHORT_Min() {
        short tempValue = Short.MIN_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the max value plus one for a short
     * 
     * @return The max value plus one of a short
     */
    static private String SHORT_Max_Plus() {
        long tempValue = Short.MAX_VALUE + 1;
        return String.valueOf(tempValue);
    }

    /**
     * Get the min value minus one for a short
     * 
     * @return The min value minus one of a short
     */
    static private String SHORT_Min_Minus() {
        long tempValue = Short.MIN_VALUE - 1;
        return String.valueOf(tempValue);
    }

    /**
     * Get the max value for an int
     * 
     * @return The max value of an int
     */
    static private String INT_Max() {
        int tempValue = Integer.MAX_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the min value for a int
     * 
     * @return The min value of a int
     */
    static private String INT_Min() {
        int tempValue = Integer.MIN_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the max value plus one for an int
     * 
     * @return The max value plus one of an int
     */
    static private String INT_Max_Plus() {
        long tempValue = Integer.MAX_VALUE + 1;
        return String.valueOf(tempValue);
    }

    /**
     * Get the min value minus one for a int
     * 
     * @return The min value minus one of a int
     */
    static private String INT_Min_Minus() {
        long tempValue = Integer.MIN_VALUE - 1;
        return String.valueOf(tempValue);
    }

    /**
     * Get the max value for a long
     * 
     * @return The max value of a long
     */
    static private String LONG_Max() {
        long tempValue = Long.MAX_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the min value for a long
     * 
     * @return The min value of a long
     */
    static private String LONG_Min() {
        long tempValue = Long.MIN_VALUE;
        return String.valueOf(tempValue);
    }

    /**
     * Get the max value plus one for a long
     * 
     * @return The max value plus one of a long
     */
    static private String LONG_Max_Plus() {
        return "9223372036854775808";
    }

    /**
     * Get the min value minus one for a long
     * 
     * @return The min value minus one of a long
     */
    static private String LONG_Min_Minus() {
        return "-9223372036854775809";
    }
}
