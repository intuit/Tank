package com.intuit.tank.util;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.util.TestParameterContainer.Builder;
import com.intuit.tank.vm.settings.TimeUtil;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;

/**
 * 
 * TestParamUtil utility class for evaluating test parameters in the syntax of 2RT + 10m.
 * 
 * @author dangleton
 * 
 */
public final class TestParamUtil {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestParamUtil.class);

    private static final String REGEX = "(\\(*[\\.,\\d]+\\)*)(\\(*[e,r,s]t\\)*)";
    private static final String ALT_REGEX = "(\\(*[e,r,s]t\\)*)(\\(*[\\.,\\d]+\\)*)";

    private static final String ESTIMATED_RUN_TIME = "et";
    private static final String RAMP_TIME = "rt";
    private static final String SIMULATION_TIME = "st";
    private static final long TEST_VALUE = 30000L;

    /**
     * private constructor to implement util pattern
     */
    private TestParamUtil() {

    }

    /**
     * extracts the ramp and simulation time from the given values.
     * 
     * @param executionTime
     *            the calculated execution time.
     * @param rampTimeString
     *            the expression for the ramp time
     * @param simulationTimeString
     *            the expresssion for the simulation time
     * @return {@link TestParameterContainer} contianing the values.
     * @throws IllegalArgumentException
     *             if there is an issue evaluating the expressions or there is a cyclic dependency.
     */
    public static TestParameterContainer evaluateTestTimes(long executionTime, String rampTimeString,
            String simulationTimeString) throws IllegalArgumentException {
        boolean rampComplete = false;
        long rampTime = 0;
        long simTime = 0;
        Builder builder = TestParameterContainer.builder();
        if (StringUtils.isNotBlank(rampTimeString)) {
            if (!rampTimeString.toLowerCase().contains(SIMULATION_TIME)) {
                rampTime = evaluateExpression(rampTimeString, executionTime, simTime, rampTime);
                builder.withRampTime(rampTime);
                rampComplete = true;
            }
        }
        if (StringUtils.isNotBlank(simulationTimeString)) {
            if (rampTimeString.toLowerCase().contains(RAMP_TIME) && !rampComplete) {
                throw new IllegalArgumentException("cyclic dependeny of ramp time and simulation time.");
            }
            simTime = evaluateExpression(simulationTimeString, executionTime, simTime, rampTime);
            builder.withSimulationTime(simTime);
        }
        if (!rampComplete && StringUtils.isNotBlank(rampTimeString)) {
            rampTime = evaluateExpression(rampTimeString, executionTime, simTime, rampTime);
            builder.withRampTime(rampTime);
        }
        return builder.build();
    }

    /**
     * Evaluates the expression provided.
     * 
     * @param value
     *            the value to evaluate
     * @param executionTime
     *            the execution time
     * @param simulationTime
     *            the simulationTime
     * @param rampTime
     *            the rampTime
     * @return the long value of evaluating the expression.
     * @throws IllegalArgumentException
     *             if there is an issue evaluating the expression.
     */
    public static long evaluateExpression(String value, long executionTime, long simulationTime, long rampTime)
            throws IllegalArgumentException {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Expression is either null or empty.");
        }
        String expression = normalizeExpression(value);
        try {
            Calculable calc = new ExpressionBuilder(expression)
                    .withVariable(ESTIMATED_RUN_TIME, executionTime)
                    .withVariable(SIMULATION_TIME, simulationTime)
                    .withVariable(RAMP_TIME, rampTime)
                    .build();
            long result = Math.round(calc.calculate());
            LOG.debug(value + " --> " + expression + " = " + Long.toString(result));
            return result;
        } catch (Exception e) {
            LOG.warn(value + " is not a valid expression.");
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * checks to see if the expressin is valid.
     * 
     * @param value
     *            the expression to evaluate
     * @return true if the expression can be evaluated.
     */
    public static final boolean isValidExpression(@Nonnull String value) {
        boolean ret = StringUtils.isNotBlank(value);
        if (ret) {
            try {
                evaluateExpression(value, TEST_VALUE, TEST_VALUE, TEST_VALUE);
            } catch (Exception e) {
                ret = false;
            }
        }
        return ret;
    }

    private static String normalizeExpression(String expression) {
        String ret = expression;
        if (StringUtils.isNotBlank(ret)) {
            String[] args = StringUtils.split(expression);
            StringBuilder timeArg = new StringBuilder();
            StringBuilder result = new StringBuilder();
            for (String arg : args) {
                if (arg.toLowerCase().endsWith("m") || arg.toLowerCase().endsWith("s")
                        || arg.toLowerCase().endsWith("h") || arg.toLowerCase().endsWith("d")) {
                    // its a time string
                    addArgToBuilder(timeArg, arg);
                } else {
                    if (timeArg.length() != 0) {
                        long time = TimeUtil.parseTimeString(timeArg.toString());
                        addArgToBuilder(result, Long.toString(time));
                        timeArg = new StringBuilder();
                    }
                    addArgToBuilder(result, normalizeArg(arg.toLowerCase()));
                }
            }
            if (timeArg.length() != 0) {
                long time = TimeUtil.parseTimeString(timeArg.toString());
                addArgToBuilder(result, Long.toString(time));
                timeArg = new StringBuilder();
            }
            ret = result.toString();
        }
        return ret;
    }

    private static String normalizeArg(String s) {
        Pattern p = Pattern.compile(REGEX);
        Matcher matcher = p.matcher(s);
        Pattern p1 = Pattern.compile(ALT_REGEX);
        Matcher matcher1 = p1.matcher(s);
        if (matcher.matches()) {
            s = matcher.group(1) + " * " + matcher.group(2);
        } else if (matcher1.matches()) {
            s = matcher1.group(1) + " * " + matcher1.group(2);
        }
        if (s.startsWith(".")) {
            s = "0" + s;
        }
        return s;
    }

    private static void addArgToBuilder(StringBuilder sb, String s) {
        if (sb.length() != 0) {
            sb.append(" ");
        }
        sb.append(s);
    }

}
