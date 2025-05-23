/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.models;

/*
 * #%L
 * Cloud Rest API
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * TPSInfoContainer
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "TPSInfoContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TPSInfoContainerType", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "minTime",
        "maxTime",
        "period",
        "totalTps",
        "tpsInfos"
})
public class TPSInfoContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "minTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date minTime;

    @XmlElement(name = "maxTime", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date maxTime;

    @XmlElement(name = "period", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int period;

    @XmlElement(name = "totalTps", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int totalTps;

    @XmlElementWrapper(name = "tpsInfos", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "tps", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private List<TPSInfo> tpsInfos = new ArrayList<TPSInfo>();

    /**
     * @FrameworkUseOnly
     */
    public TPSInfoContainer() {
    }

    public TPSInfoContainer(Date minTime, Date maxTime, int period, List<TPSInfo> tpsInfos) {
        super();
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.tpsInfos = tpsInfos;
        this.period = period;
        if (tpsInfos != null) {
            Map<Date, Integer> transactionMap = new HashMap<Date, Integer>();
            for (TPSInfo info : tpsInfos) {
                Integer tps = transactionMap.get(info.getTimestamp());
                if (tps == null) {
                    tps = 0;
                }
                tps += info.getTransactions();
                transactionMap.put(info.getTimestamp(), tps);
            }
            if (!transactionMap.isEmpty()) {
                ArrayList<Date> dates = new ArrayList<Date>(transactionMap.keySet());
                Collections.sort(dates);
                Date date = dates.get(dates.size() - 1);
                totalTps = transactionMap.get(date) / period;
            }
        }
    }

    /**
     * @return the period
     */
    public int getPeriod() {
        return period;
    }

    /**
     * 
     * @return the total TPS
     */
    public int getTotalTps() {
        return totalTps;
    }

    /**
     * @return the tpsInfos
     */
    public List<TPSInfo> getTpsInfos() {
        return tpsInfos;
    }

    /**
     * @return the minTime
     */
    public Date getMinTime() {
        return minTime;
    }

    /**
     * @return the maxTime
     */
    public Date getMaxTime() {
        return maxTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("period", period).append("minTime", minTime).append("maxTime", maxTime)
                // .append(tpsInfos)
                .toString();
    }

}
