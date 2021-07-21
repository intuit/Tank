/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.event;

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * NotificationContext
 * 
 * @author dangleton
 * 
 */
public class NotificationContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> context = new HashMap<String, String>();

    /**
     * @return the context
     */
    public Map<String, String> getContext() {
        return context;
    }

    public NotificationContext addContextEntry(String key, String value) {
        this.context.put(key, StringUtils.isBlank(value) ? "N/A" : value);
        return this;
    }

    public String replaceValues(String content) {
        for (Entry<String, String> entry : context.entrySet()) {
            content = content.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return context.toString();
    }
}
