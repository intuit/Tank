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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.common.util.ValidationUtil;

/**
 * 
 * StringFunctions functions useful for String info. Functions start with #function and use dot notation to pass
 * parameters. The first parameter is the class of function. string in this case. The next value is the function name
 * Valid values are:
 * <ul>
 * <li>userid - @see {@link StringFunctions#userIdDate(int, String)}</li>
 * <li>alphalower - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>alphaupper - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>alphamixed - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>numeric - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>special - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>unicode - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>alphamixednumeric - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>alphamixedspecial - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>alphamixedunicode - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>alphamixednumericspecial - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>alphamixednumericspecialunicode - @see {@link StringFunctions#randomString(String[], int)}</li>
 * <li>concat - @see {@link StringFunctions#concat(String[], Variables)}</li>
 * <li>useridFromRange - @see {@link StringFunctions#userIdFromRange(String[])}</li>
 * <li>substring - @see {@link StringFunctions#getSubstring(String, int, int)}</li>
 * </ul>
 * 
 * 
 * @author dangleton
 * 
 */
class StringFunctions {

    private static final Logger LOG = LogManager.getLogger(StringFunctions.class);

    /**
     * Preset values
     */
    static final String[] alphaUpper = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    static final String[] alphaLower = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
    static final String[] numeric = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    static final String[] special = { "!", "@", "#", "$", "%", "^", "*", "{", "[", "}", "]", ":", ";", ",", ".", "?",
            "~", "`" };
    static final String[] unicode = { "\u00a1", "\u00a2", "\u00a3", "\u00a4", "\u00a5", "\u00A9", "\u00ae", "\u00bc",
            "\u00bd", "\u00be", "\u00bf", "\u00e0", "\u00e1", "\u00e2", "\u00e3", "\u00e4", "\u00e5", "\u00e6" };

    static Random rnd = new Random();
    static private Hashtable<String, Stack<Integer>> stackMap = new Hashtable<String, Stack<Integer>>();

    /**
     * Is this a valid String function request
     * 
     * @param values
     *            The command
     * @return TRUE if valid format; FALSE otherwise
     */
    static public boolean isValid(String[] values) {
        try {
            if (values[3] == "")
                return false;
            if (values[2].equalsIgnoreCase("userid")
                    || values[2].equalsIgnoreCase("randomalphalower")
                    || values[2].equalsIgnoreCase("randomalphaupper")
                    || values[2].equalsIgnoreCase("randomalphamixed")
                    || values[2].equalsIgnoreCase("randomnumeric")
                    || values[2].equalsIgnoreCase("randomspecial")
                    || values[2].equalsIgnoreCase("randomalphamixednumeric")
                    || values[2].equalsIgnoreCase("randomalphamixedspecial")
                    || values[2].equalsIgnoreCase("randomalphamixednumericspecial")
                    || values[2].equalsIgnoreCase("concat")
                    || values[2].equalsIgnoreCase("useridFromRange")
                    || values[2].equalsIgnoreCase("useridFromRangeExclude")
                    || values[2].equalsIgnoreCase("useridFromRangeInclude")
                    || values[2].equalsIgnoreCase("useridFromRangeWithMod")
                    || values[2].equalsIgnoreCase("useridFromRangeWithModExclude")
                    || values[2].equalsIgnoreCase("useridFromRangeWithModInclude")
                    || values[2].equalsIgnoreCase("substring"))
                return true;
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
    static public String executeFunction(String[] values, Variables variables, String addtlString) {
        try {
            if (values[2].equalsIgnoreCase("userid")) {
                return StringFunctions.userIdDate(Integer.valueOf(values[3]), values[4]);
            } else if (values[2].equalsIgnoreCase("randomalphalower")) {
                return StringFunctions.randomString(StringFunctions.alphaLower, Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("randomalphaupper")) {
                return StringFunctions.randomString(StringFunctions.alphaUpper, Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("randomalphamixed")) {
                return StringFunctions.randomString(StringFunctions.alphaMixed(), Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("randomnumeric")) {
                return StringFunctions.randomString(StringFunctions.numeric, Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("randomspecial")) {
                return StringFunctions.randomString(StringFunctions.special, Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("randomalphamixednumeric")) {
                return StringFunctions.randomString(StringFunctions.alphaMixedNumeric(), Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("randomalphamixedspecial")) {
                return StringFunctions.randomString(StringFunctions.alphaMixedSpecial(), Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("randomalphaMixedNumericSpecial")) {
                return StringFunctions.randomString(StringFunctions.alphaMixedNumericSpecial(),
                        Integer.valueOf(values[3]));
            } else if (values[2].equalsIgnoreCase("concat")) {
                return StringFunctions.concat(values, variables);
            } else if (values[2].equalsIgnoreCase("useridFromRange")) {
                return StringFunctions.userIdFromRange(values, false);
            } else if (values[2].equalsIgnoreCase("useridFromRangeInclude")) {
                return StringFunctions.userIdFromRange(values, true);
            } else if (values[2].equalsIgnoreCase("useridFromRangeExclude")) {
                return StringFunctions.userIdFromRange(values, false);
            } else if (values[2].equalsIgnoreCase("useridFromRangeWithMod")) {
                return StringFunctions.userIdFromRangeWithMod(values, true);
            } else if (values[2].equalsIgnoreCase("useridFromRangeWithModInclude")) {
                return StringFunctions.userIdFromRangeWithMod(values, true);
            } else if (values[2].equalsIgnoreCase("useridFromRangeWithModExclude")) {
                return StringFunctions.userIdFromRangeWithMod(values, false);
            } else if (values[2].equalsIgnoreCase("substring")) {
                if (values.length == 5) {
                    return StringFunctions.getSubstring(values[3], Integer.valueOf(values[4]), -1);
                } else if (values.length == 6)
                    try {
                        return StringFunctions.getSubstring(values[3], Integer.valueOf(values[4]),
                                Integer.valueOf(values[5]));
                    } catch (NumberFormatException nfe) {
                        return StringFunctions.getSubstring(addtlString, values[3], values[4]);
                    }
            }
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    private static String getSubstring(String string, String start,
            String end) {

        if (string == null)
            return "";

        int beginIndex = string.indexOf(start);
        if (beginIndex < 0)
            return "";

        String newString = string.substring(beginIndex + start.length());

        if (!end.equals("")) {
            int endIndex = newString.indexOf(end);
            if (endIndex > 0)
                newString = newString.substring(0, endIndex);
        }

        return newString;
    }

    /**
     * Combine the alpha lower and alpha upper collections into one
     * 
     * @return The combined collection
     */
    static private String[] alphaMixed() {
        return StringFunctions.combineStringArrays(StringFunctions.alphaLower, StringFunctions.alphaUpper);
    }

    /**
     * Combine the alpha mixed and numeric collections into one
     * 
     * @return The combined collection
     */
    static private String[] alphaMixedNumeric() {
        return StringFunctions.combineStringArrays(StringFunctions.alphaMixed(), StringFunctions.numeric);
    }

    /**
     * Combine the alpha mixed and special collections into one
     * 
     * @return The combined collection
     */
    static private String[] alphaMixedSpecial() {
        return StringFunctions.combineStringArrays(StringFunctions.alphaMixed(), StringFunctions.special);
    }

    // /**
    // * Combine the alpha mixed and unicode collections into one
    // *
    // * @return The combined collection
    // */
    // static private String[] alphaMixedUnicode() {
    // return StringFunctions.combineStringArrays(StringFunctions.alphaMixed(), StringFunctions.unicode);
    // }

    /**
     * Combine the alpha mixed, numeric and special collections into one
     * 
     * @return The combined collection
     */
    static private String[] alphaMixedNumericSpecial() {
        String[] step1 = StringFunctions.combineStringArrays(StringFunctions.alphaMixed(), StringFunctions.numeric);
        return StringFunctions.combineStringArrays(step1, StringFunctions.special);
    }

    //
    // /**
    // * Combine the alpha mixed, numeric, special and unicode collections into one
    // *
    // * @return The combined collection
    // */
    // static private String[] alphaMixedNumericSpecialUnicode() {
    // String[] step1 = StringFunctions.combineStringArrays(StringFunctions.alphaMixed(), StringFunctions.numeric);
    // String[] step2 = StringFunctions.combineStringArrays(step1, StringFunctions.special);
    // return StringFunctions.combineStringArrays(step2, StringFunctions.unicode);
    // }

    /**
     * Combine two String arrays into one
     * 
     * @param value1
     *            String[] One
     * @param value2
     *            String[] Two
     * @return One String array containing both sets of data
     */
    static private String[] combineStringArrays(String[] value1, String[] value2) {
        String[] output = new String[value1.length + value2.length];
        int counter = 0;
        for (int i = 0; i < value1.length; i++) {
            output[counter] = value1[i];
            ++counter;
        }
        for (int i = 0; i < value2.length; i++) {
            output[counter] = value2[i];
            ++counter;
        }
        return output;
    }

    /**
     * Get a random, positive whole number
     * 
     * 
     * 
     * @param length
     *            The length of the full numbers
     * @return A random whole number
     */
    static private String randomString(String[] values, int length) {
        StringBuilder output = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            output.append(values[rnd.nextInt(values.length)]);
        return output.toString();
    }

    /**
     * Generate a random user date consisting of a prefix and a date
     * 
     * e.g. #function.string.userid.2.06-01-2011
     * 
     * @param prefixLength
     *            The length of the random prefix
     * @param dateFormat
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @return The random user id
     */
    static private String userIdDate(int prefixLength, String dateFormat) {
        String[] dateRequest = { "#function", "date", "currentDate", dateFormat };
        String prefix = StringFunctions.randomString(StringFunctions.alphaMixedNumeric(), prefixLength);
        String date = DateFunctions.executeFunction(dateRequest);
        return prefix + date;
    }

    /**
     * Generate a random user id as an integer.
     * 
     * e.g. #function.string.useridFromRange.25.100
     * 
     * @param values
     *            Array values - [3] = minId, [4] = maxId, [5] = inclusions/inclusions list comma seperated regex to
     *            test the value
     * @param include
     *            The format for the date
     * @return The random user id
     */
    static private String userIdFromRange(String[] values, boolean include) {
        int minId = Integer.parseInt(values[3]);
        int maxId = Integer.parseInt(values[4]);
        String exclusions = values.length > 5 ? values[5] : null;
        Stack<Integer> stack = getStack(minId, maxId, exclusions, include);
        if (stack.size() > 0) {
            return Integer.toString(stack.pop());
        }
        throw new IllegalArgumentException("Exhausted random User Ids. Range not large enough for the number of calls.");
    }

    /**
     * Generate a random user id in the range provided
     * 
     * e.g. #function.string.useridFromRange.25.100
     * 
     * @param prefixLength
     *            The length of the random prefix
     * @param dateFormat
     *            The format for the date
     * @return The random user id
     */
    static private String userIdFromRangeWithMod(String[] values, boolean include) {
        int minId = Integer.parseInt(values[3]);
        int maxId = Integer.parseInt(values[4]);
        int mod = Integer.parseInt(values[5]);
        Stack<Integer> stack = getStackWithMods(minId, maxId, mod, include);
        if (stack.size() > 0) {
            return Integer.toString(stack.pop());
        }
        throw new IllegalArgumentException("Exhausted random User Ids. Range not large enough for the number of calls.");
    }

    /**
     * @param minId
     * @param maxId
     * @return
     */
    public synchronized static Stack<Integer> getStack(Integer minId, Integer maxId, String exclusions, boolean include) {
        String key = getStackKey(minId, maxId, exclusions, include);
        Stack<Integer> stack = stackMap.get(key);
        if (stack == null) {
            int blockSize = (maxId - minId) / APITestHarness.getInstance().getAgentRunData().getTotalAgents();
            int offset = APITestHarness.getInstance().getAgentRunData().getAgentInstanceNum() * blockSize;
            LOG.info(LogUtil.getLogMessage("Creating userId Block starting at " + (offset + minId)
                    + " and containing  " + blockSize
                    + " entries with " + (include ? "inclusion" : "exclusion") + " pattern(s) of " + exclusions,
                    LogEventType.System));
            List<Integer> list = new ArrayList<Integer>();
            List<String> exclusionList = parseExclusions(exclusions);

            for (int i = 0; i < blockSize; i++) {
                int nextNum = i + minId + offset;
                if (nextNum < maxId) {
                    if (shouldInclude(Integer.toString(nextNum), exclusionList, include)) {
                        list.add(nextNum);
                    }
                }
            }
            Collections.shuffle(list);
            // Collections.reverse(list);
            stack = new Stack<Integer>();
            stack.addAll(list);
            stackMap.put(key, stack);
        }
        return stack;
    }

    /**
     * @param minId
     * @param maxId
     * @return
     */
    private synchronized static Stack<Integer> getStackWithMods(Integer minId, Integer maxId, Integer mod,
            boolean include) {
        String key = getStackKey(minId, maxId, mod, include);
        Stack<Integer> stack = stackMap.get(key);
        if (stack == null) {
            int blockSize = (maxId - minId) / APITestHarness.getInstance().getAgentRunData().getTotalAgents();
            int offset = APITestHarness.getInstance().getAgentRunData().getAgentInstanceNum() * blockSize;
            LOG.info(LogUtil.getLogMessage("Creating userId Block starting at " + offset + " and containing  "
                    + blockSize
                    + " entries.", LogEventType.System));
            List<Integer> list = new ArrayList<Integer>();

            for (int i = 0; i < blockSize; i++) {
                int nextNum = i + minId + offset;
                if (include && nextNum < maxId && nextNum % mod == 0) {
                    list.add(nextNum);
                } else if (!include && nextNum < maxId && nextNum % mod != 0) {
                    list.add(nextNum);
                }
            }
            Collections.shuffle(list);
            // Collections.reverse(list);
            stack = new Stack<Integer>();
            stack.addAll(list);
            stackMap.put(key, stack);
        }
        return stack;
    }

    private static String getStackKey(Object minId, Object maxId, Object mod, boolean include) {
        return minId + "-" + maxId + "%" + mod + "-"
                + (include ? "include" : "exclude");
    }

    private static List<String> parseExclusions(String exclusions) {
        List<String> ret = new ArrayList<String>();
        if (!StringUtils.isBlank(exclusions)) {
            String[] terms = exclusions.split(",");
            for (String term : terms) {
                ret.add(term.trim());
            }
        }
        return ret;
    }

    public static boolean shouldInclude(String value, List<String> expressions, boolean include) {
        boolean isMatch = false;
        for (String exp : expressions) {
            isMatch = isMatch | value.matches(exp);
        }
        return include ? isMatch : !isMatch;
    }

    /**
     * Generates a string that is a concatenation of the strings, 0-n, passed in. Evaluates strings for variables and
     * will concat the variable value, not name, to string.
     * 
     * @param values
     *            List of Strings to concat starting at index 3 in array
     * @return Concatenation of strings
     */
    static private String concat(String[] values, Variables variables) {

        String generatedStr = "";

        for (int str = 3; str < values.length; str++) {
            String strToAdd = values[str];
            if (strToAdd != null) {
                // check if string is variable
                if (ValidationUtil.isVariable(strToAdd)) {
                    strToAdd = variables.getVariable(strToAdd);
                }
                generatedStr = generatedStr.concat(strToAdd);
            }
        }

        return generatedStr;
    }

    /**
     * Get a substring from a string
     * 
     * @param subject
     *            The original string
     * @param start
     *            The location to start at
     * @param stop
     *            The location to end at; -1 if end of string
     * @return
     */
    static private String getSubstring(String subject, int start, int stop) {
        if (stop == -1 || stop >= subject.length()) {
            return subject.substring(start);
        }
        return subject.substring(start, stop);
    }

}