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

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.jexl3.JexlContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.CheckedKillScriptException;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ExpressionContextVisitor;

public class JexlIOFunctions implements ExpressionContextVisitor {

    private static Logger LOG = LogManager.getLogger(JexlIOFunctions.class);

    private static Map<Long, String[]> csvLineMap = new ConcurrentHashMap<Long, String[]>();
    private static Map<String, String[]> fileLineMap = new ConcurrentHashMap<String, String[]>();
    private static Map<String, byte[]> fileDataMap = new ConcurrentHashMap<String, byte[]>();

    public static void resetStatics() {
        csvLineMap.clear();
        fileDataMap.clear();
        fileLineMap.clear();
        CSVReader.reset();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void visit(JexlContext context) {
        context.set("ioFunctions", this);
    }

    /**
     * 
     * @return
     * @throws CheckedKillScriptException
     */
    public String getCSVData() throws CheckedKillScriptException {
        return getCSVData(0, false);
    }

    /**
     * 
     * @param colIndex
     * @return
     * @throws CheckedKillScriptException
     */
    public String getCSVData(int colIndex) throws CheckedKillScriptException {
        return getCSVData(colIndex, false);
    }

    /**
     * 
     * @param ocolIndex
     * @param loop
     * @return
     * @throws CheckedKillScriptException
     */
    public String getCSVData(Object ocolIndex, boolean loop) throws CheckedKillScriptException {
        String ret = null;
        int colIndex = FunctionHandler.getInt(ocolIndex);
        String[] currentLine = csvLineMap.get(Thread.currentThread().getId());
        if (currentLine == null || colIndex >= currentLine.length || currentLine[colIndex] == null) {
            currentLine = CSVReader.getInstance(TankConstants.DEFAULT_CSV_FILE_NAME).getNextLine(loop);
            if (currentLine != null) {
                csvLineMap.put(Thread.currentThread().getId(), currentLine);
            } else {
                csvLineMap.remove(Thread.currentThread().getId());
            }
        }

        if (null == currentLine) {
            LOG.debug("Next line in CSV file is null; returning empty string...");
            throw new CheckedKillScriptException("Next line in CSV file is null");
        } else if (colIndex < currentLine.length) {
            LOG.debug("Next item retrieved from csv file: " + currentLine[colIndex]);
            ret = currentLine[colIndex];
            currentLine[colIndex] = null;
        } else {
            LOG.debug("Next line in index file has " + currentLine.length + " elements; tried to retrieve index "
                    + colIndex);
        }
        return ret != null ? ret : "";
    }

    /**
     * 
     * @param fileName
     * @return
     * @throws CheckedKillScriptException
     */
    public String getCSVData(String fileName) throws CheckedKillScriptException {
        return getCSVData(fileName, 0, false);
    }

    /**
     * 
     * @param fileName
     * @param index
     * @return
     * @throws CheckedKillScriptException
     */
    public String getCSVData(String fileName, Object index) throws CheckedKillScriptException {
        return getCSVData(fileName, index, false);
    }

    /**
     * 
     * @param fileName
     * @param index
     * @param loop
     * @return
     * @throws CheckedKillScriptException
     */
    public String getCSVData(String fileName, Object oindex, boolean loop) throws CheckedKillScriptException {
        String ret = null;
        int index = FunctionHandler.getInt(oindex);
        String[] currentLine = fileLineMap.get(Thread.currentThread().getId() + "-" + fileName);
        if (currentLine == null || index >= currentLine.length || currentLine[index] == null) {
            currentLine = CSVReader.getInstance(fileName).getNextLine(loop);
            if (currentLine != null) {
                fileLineMap.put(Thread.currentThread().getId() + "-" + fileName, currentLine);
            } else {
                fileLineMap.remove(Thread.currentThread().getId() + "-" + fileName);
            }
        }

        if (null == currentLine) {
            LOG.debug("Next line in CSV file is null; returning empty string...");
            throw new CheckedKillScriptException("Next line in CSV file is null");
        } else if (index < currentLine.length) {
            LOG.debug("Next item retrieved from csv file: " + currentLine[index]);
            ret = currentLine[index];
            currentLine[index] = null;
        } else {
            LOG.debug("Next line in index file has " + currentLine.length + " elements; tried to retrieve index "
                    + index);
        }
        return ret != null ? ret : "";
    }

    /**
     * 
     * @param fileName
     * @return
     */
    public String getFileData(String fileName) {
        byte[] ret = fileDataMap.get(fileName);
        if (ret == null) {
            String datafileDir = "src/test/resources";
            try {
                datafileDir = APITestHarness.getInstance().getTankConfig().getAgentConfig()
                        .getAgentDataFileStorageDir();
            } catch (Exception e) {
                LOG.warn(LogUtil.getLogMessage("Cannot read config. Using datafileDir of " + datafileDir,
                        LogEventType.System));
            }
            File f = new File(datafileDir, fileName);
            try {
                if (f.exists()) {
                    ret = FileUtils.readFileToByteArray(f);
                }
            } catch (Exception e) {
                LOG.warn(LogUtil.getLogMessage("Cannot read file " + f.getAbsolutePath() + ": " + e,
                        LogEventType.System));
            }
            if (ret == null) {
                ret = new byte[0];
            }
            fileDataMap.put(fileName, ret);
        }

        return new String(ret);
    }

    /**
     * Reads the file contents to byte[] can only be used as input to other function that returns a string.
     * 
     * @param fileName
     * @return
     */
    public byte[] getFileBytes(String fileName) {
        byte[] ret = fileDataMap.get(fileName);
        if (ret == null) {
            String datafileDir = "src/test/resources";
            try {
                datafileDir = APITestHarness.getInstance().getTankConfig().getAgentConfig()
                        .getAgentDataFileStorageDir();
            } catch (Exception e) {
                LOG.warn(LogUtil.getLogMessage("Cannot read config. Using datafileDir of " + datafileDir,
                        LogEventType.System));
            }
            File f = new File(datafileDir, fileName);
            try {
                if (f.exists()) {
                    ret = FileUtils.readFileToByteArray(f);
                }
            } catch (Exception e) {
                LOG.warn(LogUtil.getLogMessage("Cannot read file " + f.getAbsolutePath() + ": " + e,
                        LogEventType.System));
            }
            if (ret == null) {
                ret = new byte[0];
            }
            fileDataMap.put(fileName, ret);
        }

        return ret;
    }

}