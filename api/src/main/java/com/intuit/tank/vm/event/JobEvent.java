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

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;

/**
 * JobEvent
 * 
 * @author dangleton
 * 
 */
public class JobEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String jobId;
    private String message;
    private JobLifecycleEvent event;
    private Map<String, String> extraContext = new HashMap<String, String>();

    /**
     * @param jobId
     * @param message
     * @param event
     */
    public JobEvent(String jobId, String message, JobLifecycleEvent event) {
        super();
        this.jobId = jobId;
        this.message = message;
        this.event = event;
    }

    /**
     * @return the extraContext
     */
    public Map<String, String> getExtraContext() {
        return extraContext;
    }

    public JobEvent addContextEntry(String key, String value) {
        this.extraContext.put(key, value);
        return this;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the event
     */
    public JobLifecycleEvent getEvent() {
        return event;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
