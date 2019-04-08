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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.common.util.ExpressionContextVisitor;

/**
 * 
 * JexlStringFunctions functions useful for String info. Functions start with #stringFunctions.functionName(parameters).
 * 
 * @author dangleton
 * @author rchalmela
 * 
 */

public class JexlStringFunctions implements ExpressionContextVisitor {

    private static final Logger LOG = LogManager.getLogger(JexlStringFunctions.class);

    /**
     * Preset values
     */
    static final String[] alphaUpper = { "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z" };
    static final String[] alphaLower = { "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z" };
    static final String[] numeric = { "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9" };
    static final String[] special = { "!", "@", "#", "$", "%", "^", "*", "{",
            "[", "}", "]", ":", ";", ",", ".", "?", "~", "`" };
    static final String[] unicode = { "\u00a1", "\u00a2", "\u00a3", "\u00a4",
            "\u00a5", "\u00A9", "\u00ae", "\u00bc", "\u00bd", "\u00be",
            "\u00bf", "\u00e0", "\u00e1", "\u00e2", "\u00e3", "\u00e4",
            "\u00e5", "\u00e6" };

    static Random rnd = new Random();
    static private Hashtable<String, Stack<Integer>> stackMap = new Hashtable<String, Stack<Integer>>();

    /**
     * @inheritDoc
     */
    @Override
    public void visit(JexlContext context) {
        context.set("stringFunctions", this);
    }

    public static void resetStatics() {
        stackMap.clear();
    }

    /**
     * Get a random, positive whole number
     *
     * @param values
     * @param olength
     *            The length of the full numbers
     * @return A random whole number
     */
    private String randomString(String[] values, Object olength) {
        int length = FunctionHandler.getInt(olength);
        return IntStream.range(0, length).mapToObj(i -> values[rnd.nextInt(values.length)]).collect(Collectors.joining());
    }

    /**
     * Generate a random user date consisting of a prefix and a date
     * 
     * @param prefixLength
     *            The length of the random prefix
     * @param dateFormat
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @return The random user id
     */
    public String userIdFromDate(Object prefixLength, String dateFormat) {
        return userIdDate(prefixLength, dateFormat);
    }

    /**
     * Generate a random user date consisting of a prefix and a date
     * 
     * @param oprefixLength
     *            The length of the random prefix
     * @param dateFormat
     *            The format of the response (MM-dd-yyyy, MMddyyyy, etc)
     * @return The random user id
     */
    public String userIdDate(Object oprefixLength, String dateFormat) {
        int prefixLength = FunctionHandler.getInt(oprefixLength);
        String prefix = randomString(alphaMixedNumeric(), prefixLength);
        String date = new SimpleDateFormat(dateFormat)
                .format(new java.util.Date());
        return prefix + date;
    }

    /**
     * Generate a random user id in the range provided
     * 
     * @param ominId
     *            The length of the random prefix
     * @param omaxId
     *            The format for the date
     * @return The random user id
     */
    public String userIdFromRange(Object ominId, Object omaxId) {
        int minId = FunctionHandler.getInt(ominId);
        int maxId = FunctionHandler.getInt(omaxId);
        Stack<Integer> stack = getStack(minId, maxId);
        if (stack.size() > 0) {
            return Integer.toString(stack.pop());
        }
        throw new IllegalArgumentException(
                "Exhausted random User Ids. Range not large enough for the number of calls.");
    }

    /**
     * @param minId
     * @param maxId
     * @return
     */
    private synchronized Stack<Integer> getStack(Integer minId, Integer maxId) {
        String key = getStackKey(minId, maxId);
        Stack<Integer> stack = stackMap.get(key);
        if (stack == null) {
            int blockSize = (maxId - minId) / APITestHarness.getInstance().getAgentRunData().getTotalAgents();
            int offset = APITestHarness.getInstance().getAgentRunData().getAgentInstanceNum() * blockSize;
            LOG.info(LogUtil.getLogMessage("Creating userId Block starting at " + offset
                    + " and containing  " + blockSize + " entries.", LogEventType.System));
            List<Integer> list = IntStream.range(0, blockSize).map(i -> i + minId + offset).boxed().collect(Collectors.toList());

            Collections.shuffle(list);
            // Collections.reverse(list);
            stack = new Stack<Integer>();
            stack.addAll(list);
            stackMap.put(key, stack);
        }
        return stack;
    }

    private String getStackKey(Object minId, Object maxId) {
        return minId + "-" + maxId;
    }

    /**
     * Generates a string that is a concatenation of the strings, 0-n, passed in. Evaluates strings for variables and
     * will concat the variable value, not name, to string.
     * 
     * @param values
     *            List of Strings to concat starting at index 3 in array
     * @return Concatenation of strings
     */
    public String concat(String... values) {
        return StringUtils.join(values);
    }

    /**
     * Get a substring from a string
     * 
     * @param subject
     *            The original string
     * @param start
     *            The location to start at
     * @return
     */
    public String substring(String subject, int start) {
        return substring(subject, start, -1);
    }

    /**
     * <p>
     * Gets the String that is nested in between two Strings.
     * 
     * Only the first match is returned.
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>. A <code>null</code> open/close returns
     * <code>null</code> (no match). An empty ("") open and close returns an empty string.
     * </p>
     * 
     * <pre>
     * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween(*, null, *)          = null
     * StringUtils.substringBetween(*, *, null)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "]")         = null
     * StringUtils.substringBetween("", "[", "]")        = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     * 
     * @param subject
     *            the String containing the substring, may be null
     * @param open
     *            the String before the substring, may be null
     * @param close
     *            the String after the substring, may be null
     * @return the substring, <code>null</code> if no match
     */
    public String substringBetween(String subject, String open, String close) {
        return substringBetween(subject, open, close, 0);
    }

    /**
     * <p>
     * Gets the nth String that is nested in between two Strings.
     * 
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>. A <code>null</code> open/close returns
     * <code>null</code> (no match). An empty ("") open and close returns an empty string.
     * </p>
     * 
     * @param subject
     *            the String containing the substring, may be null
     * @param open
     *            the String before the substring, may be null
     * @param close
     *            the String after the substring, may be null
     * @param index
     *            the zero based index of the string to return.
     * @return the substring, <code>null</code> if no match
     */
    public String substringBetween(String subject, String open, String close, int index) {
        String[] strings = internalSubstringsBetween(subject, open, close);
        if (index >= 0 && index < strings.length) {
            return strings[index];
        }
        return null;
    }

    private String[] internalSubstringsBetween(String subject, String open, String close) {
        String[] ret = null;
        if (subject != null && open == null && close != null) {
            ret = new String[] { StringUtils.substringBefore(subject, close) };
        } else if (subject != null && open != null && close == null) {
            ret = new String[] { StringUtils.substringAfterLast(subject, open) };
        } else {
            ret = StringUtils.substringsBetween(subject, open, close);
        }
        return ret != null ? ret : new String[0];
    }

    public String replaceBetween(String subject, String open, String close, String replace, boolean encodeData) {
        if (subject == null) {
            return subject;
        }
        if (encodeData) {
            // decode the string
            subject = fromBase64(subject);
        }
        int indexStart = 0;
        int indexEnd = subject.length();
        if (open != null) {
            indexStart = subject.indexOf(open);
        }
        if (close != null) {
            indexEnd = subject.indexOf(close, indexStart + open.length());
        }

        if (indexStart == -1 || indexEnd == -1) {
            // do nothing
        } else {
            String s1 = open != null ? subject.substring(0, (indexStart + open.length())) : "";
            String s2 = close != null ? subject.substring(indexEnd) : "";
            subject = s1 + replace + s2;
        }
        if (encodeData) {// encode it again
            subject = toBase64(subject);
        }
        return subject;
    }

    public static void main(String[] args) {
        String test = "Test";
        int indexOf = test.indexOf("s", -1);
        System.out.println(
                indexOf);
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
    public String substring(String subject, int start, int stop) {
        if (stop == -1 || stop >= subject.length()) {
            return subject.substring(start);
        }
        return subject.substring(start, stop);
    }

    /**
     * Combine the alpha lower and alpha upper collections into one
     * 
     * @return The combined collection
     */
    private String[] alphaMixed() {
        return combineStringArrays(JexlStringFunctions.alphaLower,
                JexlStringFunctions.alphaUpper);
    }

    /**
     * Combine the alpha mixed and numeric collections into one
     * 
     * @return The combined collection
     */
    private String[] alphaMixedNumeric() {
        return combineStringArrays(alphaMixed(), JexlStringFunctions.numeric);
    }

    /**
     * Combine the alpha mixed and special collections into one
     * 
     * @return The combined collection
     */
    private String[] alphaMixedSpecial() {
        return combineStringArrays(alphaMixed(), JexlStringFunctions.special);
    }

    /**
     * Combine the alpha mixed, numeric and special collections into one
     * 
     * @return The combined collection
     */
    private String[] alphaMixedNumericSpecial() {
        return combineStringArrays(alphaMixedNumeric(),
                JexlStringFunctions.special);
    }

    /**
     * Combine two String arrays into one
     * 
     * @param value1
     *            String[] One
     * @param value2
     *            String[] Two
     * @return One String array containing both sets of data
     */
    private String[] combineStringArrays(String[] value1, String[] value2) {
        String[] output = new String[value1.length + value2.length];
        int counter = 0;
        for (String aValue1 : value1) {
            output[counter] = aValue1;
            ++counter;
        }
        for (String aValue2 : value2) {
            output[counter] = aValue2;
            ++counter;
        }
        return output;
    }

    /**
     * Get a random string of lower case letters of given length
     * 
     * @param length
     * @return random string
     */
    public String randomAlphaLower(int length) {
        return randomString(JexlStringFunctions.alphaLower, length);
    }

    /**
     * Get a random string of upper case letters of given length
     * 
     * @param length
     * @return random string
     */
    public String randomAlphaUpper(int length) {
        return randomString(JexlStringFunctions.alphaUpper, length);
    }

    /**
     * Get a random string of lower and upper case letters of given length
     * 
     * @param length
     * @return random string
     */
    public String randomAlphaMixed(int length) {
        return randomString(alphaMixed(), length);
    }

    /**
     * Get a random string of numerals
     * 
     * @param length
     * @return random string
     */
    public String randomNumeric(int length) {
        return randomString(JexlStringFunctions.numeric, length);
    }

    /**
     * Get a random string of special characters of given length
     * 
     * @param length
     * @return random string
     */
    public String randomSpecial(int length) {
        return randomString(JexlStringFunctions.special, length);
    }

    /**
     * Get a random string of lower and upper case letters and numerals of given length
     * 
     * @param length
     * @return random string
     */
    public String randomAlphaMixedNumeric(int length) {
        return randomString(alphaMixedNumeric(), length);
    }

    /**
     * Get a random string of lower and upper case letters and special characters of given length
     * 
     * @param length
     * @return random string
     */
    public String randomAlphaMixedSpecial(int length) {
        return randomString(alphaMixedSpecial(), length);
    }

    /**
     * Get a random string of lower and upper case letters, numerals and special characters of given length
     * 
     * @param length
     * @return random string
     */
    public String randomAlphaMixedNumericSpecial(int length) {
        return randomString(alphaMixedNumericSpecial(), length);
    }

    /**
     * Returns a string's base64 encoding
     * 
     * @param toEncode
     * @return base64 string
     */
    public String toBase64(String toEncode) {
        try {
            byte[] bytes = toEncode.getBytes();
            return new String(Base64.encodeBase64(bytes), Charset.forName("utf-8")).trim();
        } catch (Exception e) {
            LOG.error("Error base64 encoding " + toEncode + ": " + e);
        }
        return toEncode;
    }

    /**
     * Returns a string's decoded from base 64
     * 
     * @param toDecode
     *            the string to decode
     * @return base64 string
     */
    public String fromBase64(String toDecode) {
        try {
            byte[] bytes = Base64.decodeBase64(toDecode.trim());
            return new String(bytes, Charset.forName("utf-8"));
        } catch (Exception e) {
            LOG.error("Error base64 decoding " + toDecode + ": " + e);
        }
        return toDecode;
    }

    /**
     * Get a byte array's base64 endcoding
     * 
     * @param bytes
     * @return a base64 string
     */
    public String byteArrayToBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * Encodes a String using a URL encoder
     * 
     * @param toEncode
     *            the String to encode
     * @return the encoded String
     */
    public String urlEncode(String toEncode) {
        try {
            return URLEncoder.encode(toEncode, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            LOG.error("Error url encoding " + toEncode + ": " + e);
        }
        return toEncode;
    }

    /**
     * URL Decodes the given string
     * 
     * @param toDecode
     *            the String to decode
     * @return the decoded string
     */
    public String urlDecode(String toDecode) {
        try {
            return URLDecoder.decode(toDecode, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            LOG.error("Error url decoding " + toDecode + ": " + e);
        }
        return toDecode;
    }
    
    /**
     * UUID random generator
     * 
     * @return the new UUID string
     */
    public String getUUID(){
        //generate random UUIDs
        return UUID.randomUUID().toString();
      }
     
      private static void log(Object aObject){
        System.out.println( String.valueOf(aObject) );
      }
}