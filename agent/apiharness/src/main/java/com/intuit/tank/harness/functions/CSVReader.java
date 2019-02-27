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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;

public class CSVReader {

    private static Logger LOG = LogManager.getLogger(CSVReader.class);
    private static Hashtable<String, CSVReader> instance = new Hashtable<String, CSVReader>();

    private List<String[]> lines = null;
    private int pointer = 0;

    private CSVReader(String fileName) {
        readCSV(fileName);
    }

    public static void reset() {
        instance.clear();
    }

    public static synchronized CSVReader getInstance(String fileName) {
        if (instance.get(fileName) == null)
            if (instance.get(fileName) == null) {
                instance.put(fileName, new CSVReader(fileName));
            }

        return instance.get(fileName);
    }

    private void readCSV(String fileName) {

        lines = new ArrayList<String[]>();
        // Open the file that is the first
        // command line parameter
        String datafileDir = "src/test/resources";
        try {
            datafileDir = APITestHarness.getInstance().getTankConfig().getAgentConfig()
                    .getAgentDataFileStorageDir();
        } catch (Exception e) {
            LOG.warn("Cannot read config. Using datafileDir of " + datafileDir);
        }
        File csvFile = new File(datafileDir, fileName);
        LOG.debug(LogUtil.getLogMessage("READING CSV FILE: " + csvFile.getAbsolutePath()));
        try ( BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new DataInputStream(
                                new FileInputStream(csvFile)))) ) {

            String strLine;
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                lines.add(strLine.split(","));
            }
            LOG.debug(LogUtil.getLogMessage("CSV file " + csvFile.getAbsolutePath() + " has " + lines.size()
                    + " lines."));

        } catch (IOException e) {// Catch exception if any
            LOG.error(LogUtil.getLogMessage("Error reading CSV file: " + e.toString()), e);
        }

    }

    public synchronized String[] getNextLine(boolean loop) {
        String[] ret = null;
        if (pointer >= lines.size()) {
            if (loop) {
                pointer = 0;
            } else {
                return null;
            }
        }
        ret = lines.get(pointer);
        pointer++;
        return Arrays.copyOf(ret, ret.length);
    }
}
