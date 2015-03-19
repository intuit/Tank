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

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.vm.common.util.ValidationUtil;

/**
 * 
 * FunctionHandler handles functions in strings. Functions start with #function and use dot notation to pass parameters.
 * The first parameter is the class of function. Valid values are:
 * <ul>
 * <li>date - @see DateFunctions</li>
 * <li>monetary - @see MonetaryFunctions</li>
 * <li>numeric - @see NumericFunctions</li>
 * <li>generic - @see GenericFunctions</li>
 * <li>string - @see StringFunctions</li>
 * <li>datatype - @see DataTypeFunctions</li>
 * <li>tax - @see TaxFunctions</li>
 * </ul>
 * 
 * e.g. #function.date...
 * 
 * @author dangleton
 * 
 */
public class FunctionHandler {

    /**
     * 
     */
    private static final String ESCAPE_DELIM = "-dot-";
    static final String delimeters = "[.]";
    static private Logger logger = Logger.getLogger(FunctionHandler.class);

    /**
     * Is the string a valid function
     * 
     * @param function
     *            The function string to evaluate
     * @return TRUE if that is an actual function; FALSE otherwise
     */
    static public boolean validFunction(String function) {
        try {
            String[] values = getValues(function);
            if (values[0].equalsIgnoreCase(ValidationUtil.functionIdentifier)) {
                if (values[1].equalsIgnoreCase("date"))
                    return DateFunctions.isValid(values);
                else if (values[1].equalsIgnoreCase("monetary"))
                    return MonetaryFunctions.isValid(values);
                else if (values[1].equalsIgnoreCase("numeric"))
                    return NumericFunctions.isValid(values);
                else if (values[1].equalsIgnoreCase("generic"))
                    return GenericFunctions.isValid(values);
                else if (values[1].equalsIgnoreCase("string"))
                    return StringFunctions.isValid(values);
                else if (values[1].equalsIgnoreCase("datatype"))
                    return DataTypeFunctions.isValid(values);
                else if (values[1].equalsIgnoreCase("tax"))
                    return TaxFunctions.isValid(values);
                else
                    return false;
            } else
                return false;
        } catch (Exception ex) {
            logger.error(LogUtil.getLogMessage(ex.toString()), ex);
            return false;
        }
    }

    /**
     * @param function
     * @return
     */
    private static String[] getValues(String function) {
        String[] split = function.split(delimeters);
        for (int i = 0; i < split.length; i++) {
            split[i] = cleanArg(split[i]);
        }
        if (split.length < 10) {
            split = Arrays.<String> copyOf(split, 10);
        }
        return split;
    }

    /**
     * Execute a function
     * 
     * @param function
     *            The function string to execute
     * @return The response value from the function
     */
    static public String executeFunction(String function, Variables variables) {
        return executeFunction(function, variables, "");
    }

    /**
     * Execute a function
     * 
     * @param function
     *            The function string to execute
     * @return The response value from the function
     */
    static public String executeFunction(String function, Variables variables, String addtlString) {
        try {
            if (!FunctionHandler.validFunction(function))
                return null;

            String[] values = getValues(function);
            substituteVariables(values, variables);
            if (values[1].equalsIgnoreCase("date"))
                return DateFunctions.executeFunction(values);
            else if (values[1].equalsIgnoreCase("monetary"))
                return MonetaryFunctions.executeFunction(values);
            else if (values[1].equalsIgnoreCase("numeric"))
                return NumericFunctions.executeFunction(values, variables);
            else if (values[1].equalsIgnoreCase("string"))
                return StringFunctions.executeFunction(values, variables, addtlString);
            else if (values[1].equalsIgnoreCase("generic"))
                return GenericFunctions.executeFunction(values, variables);
            else if (values[1].equalsIgnoreCase("datatype"))
                return DataTypeFunctions.executeFunction(values);
            else if (values[1].equalsIgnoreCase("tax"))
                return TaxFunctions.executeFunction(values, variables);
            return null;
        } catch (Exception ex) {
            logger.error(LogUtil.getLogMessage(ex.toString()), ex);
            return null;
        }
    }

    private static final String cleanArg(String arg) {
        if (!StringUtils.isEmpty(arg)) {
            arg = arg.replaceAll(FunctionHandler.ESCAPE_DELIM, ".");
        }
        return arg;
    }

    /**
     * @param values
     * @param variables
     */
    private static void substituteVariables(String[] values, Variables variables) {
        for (int i = 0; i < values.length; i++) {
            if (ValidationUtil.isVariable(values[i])) {
                values[i] = variables.getVariable(values[i]);
            }
        }

    }
    
    /**
     * 
     * @param o
     * @return
     */
    public static final int getInt(Object o) {
        return getNumber(o).intValue();
    }

    public static final double getDouble(Object o) {
        return getNumber(o).doubleValue();
    }
    public static final long getLong(Object o) {
        return getNumber(o).longValue();
    }

    public static final Number getNumber(Object o) {
        Number ret = null;
        if (o instanceof Number) {
            ret = ((Number) o);
        } else {
            ret = NumberUtils.createNumber(o.toString());
        }
        return ret;
    }
}