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

import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.vm.common.TankConstants;

class GenericFunctions {

    static Random rnd = new Random();
    static Logger logger = Logger.getLogger(GenericFunctions.class);

    private static Map<Long, String[]> csvLineMap = new ConcurrentHashMap<Long, String[]>();
    private static Map<String, String[]> fileLineMap = new ConcurrentHashMap<String, String[]>();
    private static int lastSSN = -1;

    /**
     * Is this a valid Generic function request
     * 
     * @param values
     *            The command
     * @return TRUE if valid format; FALSE otherwise
     */
    public static boolean isValid(String[] values) {
        try {
            if (values[2].equalsIgnoreCase("getcsv")) {
                if (values[3] != "")
                    return true;
            } else if (values[2].equalsIgnoreCase("getfiledata")) {
                if (values[3] != "")
                    return true;
            } else if (values[2].equalsIgnoreCase("getssn")) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Process the date request
     * 
     * @param values
     *            The command line
     * @return The requested value; "" if there was an error
     */
    public static String executeFunction(String[] values, Variables variables) {
        try {
            if (values[2].equalsIgnoreCase("getcsv")) {
                return GenericFunctions.getCSVData(Integer.parseInt(values[3]));
            } else if (values[2].equalsIgnoreCase("getfiledata")) {
                return GenericFunctions.getCSVData(values, variables);
            } else if (values[2].equalsIgnoreCase("getssn")) {
                return GenericFunctions.getSsn(values[3]);
            }

            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getCSVData(int index) {
        String ret = null;
        String[] currentLine = csvLineMap.get(Thread.currentThread().getId());
        if (currentLine == null || index >= currentLine.length || currentLine[index] == null) {
            currentLine = CSVReader.getInstance(TankConstants.DEFAULT_CSV_FILE_NAME).getNextLine(false);
            csvLineMap.put(Thread.currentThread().getId(), currentLine);
        }

        if (null == currentLine) {
            logger.debug("Next line in CSV file is null; returning empty string...");
        } else if (index < currentLine.length) {
            logger.debug("Next item retrieved from csv file: " + currentLine[index]);
            ret = currentLine[index];
            currentLine[index] = null;
        } else {
            logger.debug("Next line in index file has " + currentLine.length + " elements; tried to retrieve index "
                    + index);
        }
        return ret != null ? ret : "";
    }

    public static String getCSVData(String[] values, Variables variables) {
        String ret = null;
        int index = Integer.parseInt(values[values.length - 1]);
        String file = values[3];
        for (int i = 4; i < values.length - 1; i++) {
            file += "." + values[i];
        }
        String[] currentLine = fileLineMap.get(Thread.currentThread().getId() + "-" + file);
        if (currentLine == null || index >= currentLine.length || currentLine[index] == null) {
            currentLine = CSVReader.getInstance(file).getNextLine(false);
            fileLineMap.put(Thread.currentThread().getId() + "-" + file, currentLine);
        }

        if (null == currentLine) {
            logger.debug("Next line in CSV file is null; returning empty string...");
        } else if (index < currentLine.length) {
            logger.debug("Next item retrieved from csv file: " + currentLine[index]);
            ret = currentLine[index];
            currentLine[index] = null;
        } else {
            logger.debug("Next line in index file has " + currentLine.length + " elements; tried to retrieve index "
                    + index);
        }
        return ret != null ? ret : "";
    }

    /**
     * 
     * @param startSsn
     * @return
     */
    public static String getSsn(String val) {
        int ret = 0;
        if (lastSSN < 0) {
            int startSsn = 100000000;
            if (NumberUtils.isDigits(val)) {
                startSsn = Integer.parseInt(val);
            }
            lastSSN = startSsn;
        }
        Stack<Integer> stack = StringFunctions.getStack(lastSSN, 899999999, null, false);
        do {
            ret = stack.pop();
        } while (!isValidSsn(ret));

        String ssnString = Integer.toString(ret);
        StringUtils.leftPad(ssnString, 9, '0');

        ssnString = ssnString.substring(0, 3) + "-" + ssnString.substring(3, 5) + "-" + ssnString.substring(5, 9);
        return ssnString;
    }

    private static boolean isValidSsn(int ssn) {

        if (((ssn >= 1010001) && (ssn <= 699999999)) ||
                ((ssn >= 700010001) && (ssn <= 733999999)) ||
                ((ssn >= 750010001) && (ssn <= 763999999)) ||
                ((ssn >= 764010001) && (ssn <= 899999999))) {
            return true;
        }
        return false;
    }
}