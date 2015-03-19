package com.intuit.tank.project;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Date;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartModel;

public class TrackingCartesianChartModel extends LineChartModel {

    private static final long serialVersionUID = 1L;
    private Date minDate;
    private Date maxDate;

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    public long getMin() {
        return minDate != null ? minDate.getTime() : 0;
    }

    public long getMax() {
        return maxDate != null ? maxDate.getTime() : 0;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    public void addDate(Date d) {
        if (this.minDate == null || d.before(this.minDate)) {
            this.minDate = d;
        }
        if (this.maxDate == null || d.after(this.maxDate)) {
            this.maxDate = d;
        }

    }

}
