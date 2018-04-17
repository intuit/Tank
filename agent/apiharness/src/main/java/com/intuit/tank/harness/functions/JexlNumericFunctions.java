/*
 * Copyright 2012 Intuit Inc. All Rights Reserved
 */

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
import java.util.Random;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.lang3.math.NumberUtils;

import com.intuit.tank.vm.common.util.ExpressionContextVisitor;

/**
 * 
 * JexlNumericFunctions numerical based functions like add, subtract and modulo. Also contains random number generators
 * of different lengths
 * 
 * @author rchalmela
 * 
 */
public class JexlNumericFunctions implements ExpressionContextVisitor {

    static Random rnd = new Random();

    /**
     * 
     * @inheritDoc
     */
    @Override
    public void visit(JexlContext context) {
        context.set("numericFunctions", this);
    }

    /**
     * finds first number modulo second number
     * 
     * @param num
     * @param mod
     * @return result as a string
     */
    public String mod(Object num, Object mod) {
        return Integer.toString(FunctionHandler.getInt(num) % FunctionHandler.getInt(mod));
    }

    /**
     * Adds all values together
     * 
     * @param values
     * @return summation
     */
    public String add(Object... values) {
        double result = Arrays.stream(values).mapToDouble(FunctionHandler::getDouble).sum();
        return Double.toString(result);
    }

    /**
     * Subtracts all values from the first value
     * 
     * @param values
     * @return subtraction of values
     */
    public String subtract(Object... values) {
        double result = FunctionHandler.getDouble(values[0]);
        for (int i = 1; i < values.length; i++) {
            result -= FunctionHandler.getDouble(values[i]);
        }
        return Double.toString(result);
    }

    /**
     * Returns a random number between min(inclusive) and max(exclusive)
     * 
     * @param min
     *            the min number
     * @param max
     *            the maximum number
     * @return the value
     */
    public String random(Object omin, Object omax) {
        int min = FunctionHandler.getInt(omin);
        int max = FunctionHandler.getInt(omax);
        if (max > min) {
            return Integer.toString(rnd.nextInt(max - min) + min);
        }
        return "0";
    }

    /**
     * Returns a random number between 0(inclusive) and max(exclusive)
     * 
     * @param max
     *            the maximum number
     * @return the value
     */
    public String random(Object omax) {
        return random(0, omax);
    }

    /**
     * Get a random, positive whole number
     * 
     * @param length
     *            The length of the full numbers
     * @return A random whole number
     */
    public String randomPositiveWhole(Object olength) {
        int length = FunctionHandler.getInt(olength);
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
    public String randomNegativeWhole(Object olength) {
        return "-" + randomPositiveWhole(olength);
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
    public String randomPositiveFloat(Object owhole, Object odecimal) {
        int whole = FunctionHandler.getInt(owhole);
        int decimal = FunctionHandler.getInt(odecimal);
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
    public String randomNegativeFloat(Object owhole, Object odecimal) {
        return "-" + randomPositiveFloat(owhole, odecimal);
    }

    /**
     * converts the Double to an Int
     * 
     * @param num
     * @return
     */
    public String toInt(String num) {
        return Integer.toString(FunctionHandler.getInt(num));
    }



}