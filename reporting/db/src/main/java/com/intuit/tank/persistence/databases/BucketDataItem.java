/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
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
import java.util.Date;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * BucketDataItem
 * 
 * @author dangleton
 * 
 */
public class BucketDataItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private int period = 15;
    private Date startTime;
    private DescriptiveStatistics stats;

    /**
     * @param period
     * @param startTime
     * @param stats
     */
    public BucketDataItem(int period, Date startTime, DescriptiveStatistics stats) {
        super();
        this.period = period;
        this.startTime = startTime;
        this.stats = stats;
    }

    /**
     * @return the period
     */
    public int getPeriod() {
        return period;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the stats
     */
    public DescriptiveStatistics getStats() {
        return stats;
    }

}
