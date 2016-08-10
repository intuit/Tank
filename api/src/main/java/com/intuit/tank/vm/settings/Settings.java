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

import java.io.*;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Settings extends PropertiesFile {
    static Logger logger = LogManager.getLogger(Settings.class);

    private static Settings instance = null;

    private HashMap<String, String> elements = null;

    /**
     * Get the single instance of the class
     * 
     * @return The single instance of the class
     */
    public synchronized static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    /**
     * Constructor
     */
    private Settings() {
        super();
        try {
            this.loadPropertiesFile(this.getFileName());
            this.elements = this.getItems();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            Settings.instance = null;
        }
    }

    /**
     * Add a setting
     * 
     * @param key
     *            The setting key
     * @param value
     *            The setting value
     */
    public void setValue(String key, String value) {
        this.elements.put(key, value);
    }

    /**
     * Get a value from the settings
     * 
     * @param key
     *            The key
     * @return The key value
     */
    public String getValue(String key) {
        return this.elements.get(key);
    }

    /**
     * Gets a value.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the value to return if no value defined
     * @return the value or the default value if value cannot be found
     */
    public String getValue(String key, String defaultValue) {
        String ret = getValue(key);

        return ret != null ? ret : defaultValue;
    }

    /**
     * Locate the settings file
     * 
     * @return The absolute file path
     */
    public String getFileName() {
        try {
            File file = new File("settings.properties");
            logger.info("loading settings from file " + file.getAbsolutePath());
            if (file.exists()) {
                return file.getCanonicalPath();
            } else if (System.getenv("WATS_PROPERTIES") != null) {
                return System.getenv("WATS_PROPERTIES") + "/settings.properties";
            } else if (System.getProperty("WATS_PROPERTIES") != null) {
                return System.getProperty("WATS_PROPERTIES") + "/settings.properties";
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    /**
     * @param string
     * @param i
     * @return
     */
    public int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    /**
     * @param string
     * @param i
     * @return
     */
    public int getIntValue(String key, int defaultValue) {
        int ret = defaultValue;
        String s = getValue(key);
        if (s != null) {
            try {
                ret = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                logger.warn("Error parsing int value for key " + key + " with value " + s);
            }
        }
        return ret;
    }

}