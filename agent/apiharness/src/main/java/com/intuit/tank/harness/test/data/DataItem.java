package com.intuit.tank.harness.test.data;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataItem {

    static Logger logger = LogManager.getLogger(DataItem.class);

    private String name = null;
    private String value = null;
    private String action = null;

    /**
     * Constructor
     * 
     * @param name
     *            The name of the data item
     * @param value
     *            The value of the data item
     */
    public DataItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor
     * 
     * @param name
     *            The name of the data item
     * @param value
     *            The value of the data item
     * @param action
     *            The action
     */
    public DataItem(String name, String value, String action) {
        this.name = name;
        this.value = value;
        this.action = action;
    }

    /**
     * Get the name of the data item
     * 
     * @return The name of the data item
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the value of the data item
     * 
     * @return The value of the data item
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Get the action to perform
     * 
     * @return The action (add, remove)
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Set the value of the data item
     * 
     * @param newValue
     *            The value for the data item
     */
    public void setValue(String newValue) {
        this.value = newValue;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return "(" + action + ") " + name + "=" + value;
    }
}
