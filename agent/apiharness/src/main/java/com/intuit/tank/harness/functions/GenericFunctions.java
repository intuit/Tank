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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.AgentRunData;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.vm.common.TankConstants;

class GenericFunctions {

    static Random rnd = new Random();
    static Logger logger = LogManager.getLogger(GenericFunctions.class);

    private static Map<Long, String[]> csvLineMap = new ConcurrentHashMap<Long, String[]>();
    private static Map<String, String[]> fileLineMap = new ConcurrentHashMap<String, String[]>();
    private static final int MAX_SSN = 899999999;
    // the next ssn to try in this agent's share of the range, -1 until the first request; ssnBlockEnd is exclusive
    private static int nextSsn = -1;
    private static int ssnBlockEnd;

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
        String[] currentLine = csvLineMap.get(Thread.currentThread().threadId());
        if (currentLine == null || index >= currentLine.length || currentLine[index] == null) {
            currentLine = CSVReader.getInstance(TankConstants.DEFAULT_CSV_FILE_NAME).getNextLine(false);
            putOrRemove(csvLineMap, Thread.currentThread().threadId(), currentLine);
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
        // FunctionHandler pads the arguments with nulls, so the column index is the last one given
        int last = values.length - 1;
        while (last > 4 && values[last] == null) {
            last--;
        }
        int index = Integer.parseInt(values[last]);
        String file = values[3];
        for (int i = 4; i < last; i++) {
            file += "." + values[i];
        }
        String[] currentLine = fileLineMap.get(Thread.currentThread().threadId() + "-" + file);
        if (currentLine == null || index >= currentLine.length || currentLine[index] == null) {
            currentLine = CSVReader.getInstance(file).getNextLine(false);
            putOrRemove(fileLineMap, Thread.currentThread().threadId() + "-" + file, currentLine);
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

    /** A ConcurrentHashMap cannot hold null, which is what the reader returns once the file runs out. */
    private static <K> void putOrRemove(Map<K, String[]> lines, K key, String[] line) {
        if (line == null) {
            lines.remove(key);
        } else {
            lines.put(key, line);
        }
    }

    /**
     * 
     * @param val
     * @return
     */
    public static synchronized String getSsn(String val) {
        if (nextSsn < 0) {
            int startSsn = 100000000;
            if (NumberUtils.isDigits(val)) {
                startSsn = Integer.parseInt(val);
            }
            // each agent hands out its own share of the range, in order, so every user gets a unique ssn
            AgentRunData agentRunData = APITestHarness.getInstance().getAgentRunData();
            int blockSize = (MAX_SSN - startSsn) / agentRunData.getTotalAgents();
            nextSsn = startSsn + agentRunData.getAgentInstanceNum() * blockSize;
            ssnBlockEnd = nextSsn + blockSize;
        }
        while (nextSsn < ssnBlockEnd && !isValidSsn(nextSsn)) {
            nextSsn++;
        }
        if (nextSsn >= ssnBlockEnd) {
            throw new IllegalStateException("No social security numbers left in this agent's range");
        }

        String ssnString = StringUtils.leftPad(Integer.toString(nextSsn++), 9, '0');
        return ssnString.substring(0, 3) + "-" + ssnString.substring(3, 5) + "-" + ssnString.substring(5, 9);
    }

    private static boolean isValidSsn(int ssn) {

        return (((ssn >= 1010001) && (ssn <= 699999999)) ||
                ((ssn >= 700010001) && (ssn <= 733999999)) ||
                ((ssn >= 750010001) && (ssn <= 763999999)) ||
                ((ssn >= 764010001) && (ssn <= 899999999)));
    }
}