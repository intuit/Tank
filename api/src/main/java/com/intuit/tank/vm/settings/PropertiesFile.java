package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PropertiesFile {

    static Logger logger = LogManager.getLogger(PropertiesFile.class);

    protected Properties properties = null;
    protected long loadUntilTime;
    private static final long TOTAL_TIME_TO_LOAD = 1000 * 60 * 5;// five minutes

    private static final int SLEEP_MULTIPLIER = 5000;// five seconds
    private int loadCount;

    /**
     * Constructor
     */
    public PropertiesFile() {
    }

    public void loadPropertiesFile(String fileName) {
        loadCount = 0;
        loadUntilTime = System.currentTimeMillis() + TOTAL_TIME_TO_LOAD;
        do {
            try {
                this.properties = new Properties();
                this.properties.load(new FileInputStream(fileName));
            } catch (IOException ex) {
                logger.error(ex.toString());
                this.properties = null;
            }
        } while (properties == null && attemptLoadBackoff());
    }

    /**
     * @return true if we should keep attempting to load settings
     */
    private boolean attemptLoadBackoff() {
        loadCount++;
        boolean result = false;
        long sleepTime = loadCount * SLEEP_MULTIPLIER;
        if (System.currentTimeMillis() + sleepTime <= loadUntilTime) {
            try {
                logger.warn("Load Failed. Sleeping " + sleepTime + "ms. before trying again...");
                Thread.sleep(sleepTime);
                result = true;
            } catch (InterruptedException e) {
                logger.error(e);
            }
        } else {
            logger.warn("Load failed with backoff of " + TOTAL_TIME_TO_LOAD + " ms.");
        }
        return result;
    }

    public Properties getPropertiesFile() {
        return this.properties;
    }

    @SuppressWarnings("rawtypes")
    public HashMap<String, String> getItems() {
        HashMap<String, String> output = new HashMap<String, String>();
        if (this.properties != null) {
            for (Enumeration e = this.properties.keys(); e.hasMoreElements(); /**/) {
                String key = (String) e.nextElement();
                String value = this.properties.getProperty(key);
                output.put(key, value);
            }
        }
        return output;
    }

    /**
     * Get a specified setting
     * 
     * @param key
     *            The setting requested
     * @return The setting value; NULL if not found
     */
    public String getSetting(String key) {
        if (properties.containsKey(key))
            return properties.getProperty(key);
        return null;
    }

    /**
     * Get the config directory
     * 
     * @return The config directory
     */
    public String getConfigDirectory() {
        StringBuilder output = new StringBuilder();
        output.append(this.getDevelopmentPath());
        output.append("config");
        output.append(File.separatorChar);
        File directory = new File(output.toString());
        if (directory.exists())
            return output.toString();
        return "." + File.separatorChar + "config" + File.separatorChar;
    }

    /**
     * Get the testPlans directory
     * 
     * @return
     */
    public String getTemplatesDirectory() {
        return Settings.getInstance().getSetting("Directory.Templates") + File.separatorChar;
    }

    /**
     * Get the development path
     * 
     * @return The development path
     */
    private String getDevelopmentPath() {
        StringBuilder output = new StringBuilder();
        output.append(".");
        output.append(File.separatorChar);
        output.append("src");
        output.append(File.separatorChar);
        output.append("main");
        output.append(File.separatorChar);
        return output.toString();
    }

    /**
     * Locate the settings file
     * 
     * @return The absolute file path
     */
    protected String setFileName(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists())
                return file.getCanonicalPath();
            return null;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }
}