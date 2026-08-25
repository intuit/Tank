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
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.vm.common.util.ValidationUtil;

/**
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

    private static final String ESCAPE_DELIM = "-dot-";
    static final String delimeters = "[.]";
    private static final Pattern DELIM_PATTERN = Pattern.compile(delimeters);
    private static final Logger logger = LogManager.getLogger(FunctionHandler.class);

    /**
     * Is the string a valid function
     *
     * @param function
     *            The function string to evaluate
     * @return TRUE if that is an actual function; FALSE otherwise
     */
    static public boolean validFunction(String function) {
        try {
            return isValidFunction(getValues(function));
        } catch (Exception ex) {
            logger.error(LogUtil.getLogMessage(ex.toString()), ex);
            return false;
        }
    }

    private static boolean isValidFunction(String[] values) {
        if (!values[0].equalsIgnoreCase(ValidationUtil.functionIdentifier)) {
            return false;
        }
        return switch (values[1].toLowerCase()) {
            case "date" -> DateFunctions.isValid(values);
            case "monetary" -> MonetaryFunctions.isValid(values);
            case "numeric" -> NumericFunctions.isValid(values);
            case "generic" -> GenericFunctions.isValid(values);
            case "string" -> StringFunctions.isValid(values);
            case "datatype" -> DataTypeFunctions.isValid(values);
            case "tax" -> TaxFunctions.isValid(values);
            default -> false;
        };
    }

    private static String[] getValues(String function) {
        String[] split = DELIM_PATTERN.split(function);
        for (int i = 0; i < split.length; i++) {
            split[i] = cleanArg(split[i]);
        }
        if (split.length < 10) {
            split = Arrays.copyOf(split, 10);
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
            String[] values = getValues(function);
            if (!isValidFunction(values))
                return null;

            substituteVariables(values, variables);
            return switch (values[1].toLowerCase()) {
                case "date" -> DateFunctions.executeFunction(values);
                case "monetary" -> MonetaryFunctions.executeFunction(values);
                case "numeric" -> NumericFunctions.executeFunction(values, variables);
                case "string" -> StringFunctions.executeFunction(values, variables, addtlString);
                case "generic" -> GenericFunctions.executeFunction(values, variables);
                case "datatype" -> DataTypeFunctions.executeFunction(values);
                case "tax" -> TaxFunctions.executeFunction(values, variables);
                default -> null;
            };
        } catch (Exception ex) {
            logger.error(LogUtil.getLogMessage(ex.toString()), ex);
            return null;
        }
    }

    private static String cleanArg(String arg) {
        return (arg == null || arg.isEmpty()) ? arg : arg.replace(ESCAPE_DELIM, ".");
    }

    private static void substituteVariables(String[] values, Variables variables) {
        for (int i = 0; i < values.length; i++) {
            if (ValidationUtil.isVariable(values[i])) {
                values[i] = variables.getVariable(values[i]);
            }
        }
    }
    
    public static int getInt(Object o) {
        return getNumber(o).intValue();
    }

    public static double getDouble(Object o) {
        return getNumber(o).doubleValue();
    }

    public static long getLong(Object o) {
        return getNumber(o).longValue();
    }

    public static Number getNumber(Object o) {
        return (o instanceof Number number) ? number : NumberUtils.createNumber(o.toString());
    }
}