/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.cloud;

/*
 * #%L
 * Cloud Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.enterprise.context.ApplicationScoped;

/**
 * CostingCache
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
public class CostingCache {

    // private Map<UsageKey, ServiceUsage> cache = new HashMap<CostingCache.UsageKey, ServiceUsage>();
    //
    // public ServiceUsage getUsageForType(TimePeriodSelectChoice timePeriod) {
    // ServiceUsage ret = null;
    // return ret;
    // }
    //
    // private static class UsageKey {
    // private String timePeriod;
    // private Calendar startDate;
    // private Calendar endDate;
    // /**
    // * @param timePeriod
    // * @param startDate
    // * @param endDate
    // */
    // private UsageKey(String timePeriod, Calendar startDate, Calendar endDate) {
    // super();
    // this.timePeriod = timePeriod;
    // this.startDate = startDate;
    // this.endDate = endDate;
    // }
    //
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean equals(Object obj) {
    // if (!(obj instanceof UsageKey)) {
    // return false;
    // }
    // UsageKey o = (UsageKey) obj;
    // return new EqualsBuilder().append(o.timePeriod, timePeriod).append(o.startDate, startDate).append(o.endDate,
    // endDate).isEquals();
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public int hashCode() {
    // return new HashCodeBuilder(21, 55).append(timePeriod).append(startDate).append(endDate).toHashCode();
    // }
    // }
    //
    // private static class UsageValue {
    // private static final long VALID_FOR_TIME = 1000 * 60 * 60 * 12;//twelve hours
    // private ServiceUsage usage;
    // private String timePeriod;
    // private long timestamp = System.currentTimeMillis();
    //
    // /**
    // * @param usage
    // */
    // private UsageValue(ServiceUsage usage, String timePeriod) {
    // super();
    // this.usage = usage;
    // }
    //
    // /**
    // * @return the usage
    // */
    // public ServiceUsage getUsage() {
    // return usage;
    // }
    //
    // public boolean isValid() {
    // long currTime = System.currentTimeMillis();
    // return currTime < timestamp + VALID_FOR_TIME;
    // }
    // }
}
