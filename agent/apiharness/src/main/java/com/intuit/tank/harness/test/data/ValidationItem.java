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

public class ValidationItem extends DataItem {

    static Logger logger = LogManager.getLogger(ValidationItem.class);

    private String condition = null;

    /**
     * Constructor
     * 
     * @param name
     *            The name of the item to validate
     * @param value
     *            The value of the item to validate against
     * @param type
     *            The validation condition
     */
    public ValidationItem(String name, String value, String condition) {
        super(name, value);
        this.condition = condition.toUpperCase();
    }

    /**
     * Get the validation condition
     * 
     * @return
     */
    public String getCondition() {
        return this.condition;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return "Validation: " + getName() + " " + condition + " " + getValue();
    }

}
