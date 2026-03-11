package com.intuit.tank.harness.test.data;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.functions.FunctionHandler;
import com.intuit.tank.harness.functions.JexlDateFunctions;
import com.intuit.tank.harness.functions.JexlIOFunctions;
import com.intuit.tank.harness.functions.JexlMonetaryFunctions;
import com.intuit.tank.harness.functions.JexlNumericFunctions;
import com.intuit.tank.harness.functions.JexlStringFunctions;
import com.intuit.tank.harness.functions.JexlTaxFunctions;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.KillScriptException;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ValidationUtil;
import com.intuit.tank.vm.settings.TankConfig;

public class Variables {

    public static final String RETRY_KEY = "_retry_enabled_";

    private static final Logger LOG = LogManager.getLogger(Variables.class);

    private HashMap<String, VariableValue> variables = null;
    private boolean doLog = false;
    private static final JexlEngine jexl = new JexlBuilder()
            .permissions(JexlPermissions.parse("com.intuit.tank.harness.*"))
            .cache(1024).silent(true).strict(false)
            .create();

    private static final Pattern p = Pattern.compile(TankConstants.EXPRESSION_REGEX);

    // Cache for compiled JEXL expressions to avoid repeated parsing
    private static final Map<String, JexlExpression> expressionCache = new ConcurrentHashMap<>();

    private JexlContext context;

    /**
     * Get the single instance of the variables class
     *
     * @return The variable class instance
     *
     *         static public Variables getInstance(){ if (Variables.instance == null) Variables.instance = new
     *         Variables(); return Variables.instance; }
     *
     *         /** Constructor
     */
    public Variables() {
        this.variables = new HashMap<String, VariableValue>();
        try {
            doLog = new TankConfig().getAgentConfig().getLogVariables();
        } catch (Exception e) {
            // eat this one
        }

        context = new MapContext();
        new JexlStringFunctions().visit(context);
        new JexlIOFunctions().visit(context);
        new JexlDateFunctions().visit(context);
        new JexlMonetaryFunctions().visit(context);
        new JexlNumericFunctions().visit(context);
        new JexlTaxFunctions().visit(context);
        // get from config??
    }

    /**
     * @return the context
     */
    public JexlContext getContext() {
        return context;
    }

    public String evaluate(String s) {
        if (StringUtils.isEmpty(s) || s.indexOf('#') == -1) {
            return s;  // Fast path: no expressions to evaluate
        }

        Matcher m = p.matcher(s);
        if (!m.find()) {
            return s;  // No matches found
        }

        // Use StringBuilder with appendReplacement for efficient string building
        StringBuilder result = new StringBuilder(s.length() + 64);
        do {
            String group = m.group(1);

            // Get or create cached expression
            JexlExpression expression = expressionCache.computeIfAbsent(group, jexl::createExpression);

            String evalResult = (String) expression.evaluate(context);
            if (evalResult == null && (group.contains("getCSVData") || group.contains("getFile"))) {
                APITestHarness.getInstance().addKill();
                LOG.error(LogUtil.getLogMessage("CSV file (" + group + ") has no more data.",
                        LogEventType.Validation, LoggingProfile.USER_VARIABLE));
                throw new KillScriptException("CSV file (" + group + ") has no more data.");
            }

            // Use appendReplacement to build result efficiently
            m.appendReplacement(result, Matcher.quoteReplacement(evalResult != null ? evalResult : ""));
        } while (m.find());

        m.appendTail(result);
        return result.toString();
    }

    public Map<String, String> getVariableValues() {
        return variables.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getValue()));
    }

    /**
     * Does the variable exist in the list
     * 
     * @param key
     *            The variable name
     * @return TRUE if the variable is in the list; FALSE otherwise
     */
    public boolean variableExists(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        key = ValidationUtil.removeVariableIdentifier(key);
        return this.variables.containsKey(key);
    }

    /**
     * Add a variable to the map
     * 
     * @param key
     *            The key name
     * @param value
     *            The key value
     */
    public void addVariable(String key, String value) {
        addVariable(key, value, true);
    }

    /**
     * Add a variable to the map
     * 
     * @param key
     *            The key name
     * @param value
     *            The key value
     * @param allowOverride
     *            true to allow override of variable
     */
    public void addVariable(String key, String value, boolean allowOverride) {
        String result = ValidationUtil.isFunction(value)
                ? processFunction(key, value)
                : processString(key, value);

        VariableValue variableValue = variables.get(key);
        if (variableValue == null || variableValue.allowOverride) {
            variables.put(key, new VariableValue(result, allowOverride));
            context.set(key, result);
        }
    }

    /**
     * Get the value for a variable
     * 
     * @param key
     *            The key to look up
     * @return The value of the key; NULL if not present
     */
    public String getVariable(String key) {
        key = ValidationUtil.removeVariableIdentifier(key);
        VariableValue variableValue = this.variables.get(key);
        return variableValue != null ? variableValue.getValue() : "";

    }

    /**
     * casts the value to a number
     * 
     * @param key
     * @return the double or null if it is not a number
     */
    public Double getDoubleValue(String key) {
        String variable = getVariable(key);
        try {
            if (variable != null) {
                return Double.valueOf(variable);
            }
        } catch (NumberFormatException e) {
            LOG.error("{} is not a Double.", variable);
        }
        return null;
    }

    /**
     * casts the value to a number
     * 
     * @param key
     * @return the double or null if it is not a number
     */
    public Integer getIntegerValue(String key) {
        String variable = getVariable(key);
        try {
            if (variable != null) {
                return Integer.valueOf(variable);
            }
        } catch (NumberFormatException e) {
            LOG.error("{} is not a Integer.", variable);
        }
        return null;
    }

    /**
     * Handle functions
     * 
     * @param key
     *            The variable name
     * @param value
     *            The function to execute
     */
    private String processFunction(String key, String value) {
        String result = "";
        if (ValidationUtil.isFunction(value)) {
            result = FunctionHandler.executeFunction(value, this);
        }
        logVariable(key, value + " ==> " + result);
        return result;
    }

    /**
     * Handle a string
     * 
     * @param key
     *            The variable name
     * @param value
     *            The string value
     */
    private String processString(String key, String value) {
        value = evaluate(value);
        logVariable(key, value);
        return value;
    }

    public void removeVariable(String key) {
        if (doLog) {
            LOG.info(LogUtil.getLogMessage("Removing variable " + key));
        }
        this.variables.remove(key);
    }

    private void logVariable(String key, String value) {
        if (doLog && !key.equalsIgnoreCase("_startTime")) {
            LOG.info(LogUtil.getLogMessage("Setting variable " + key + " = " + value));
        }
    }

    private static class VariableValue {
        private String value;
        private boolean allowOverride;

        /**
         * @param value
         * @param allowOverride
         */
        private VariableValue(String value, boolean allowOverride) {
            super();
            this.value = value;
            this.allowOverride = allowOverride;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return value;
        }

    }

}
