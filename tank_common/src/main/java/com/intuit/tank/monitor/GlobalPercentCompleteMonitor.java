/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.monitor;

/*
 * #%L
 * Common
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import com.intuit.tank.vm.common.PercentCompleteMonitor;

/**
 * PercentCompleteMonitor
 * 
 * @author dangleton
 * 
 */
@Named
@Dependent
public class GlobalPercentCompleteMonitor implements PercentCompleteMonitor, Serializable {

    private final Map<Integer, Integer> statusMap = new ConcurrentHashMap<Integer, Integer>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSavingStarted(int id) {
        statusMap.put(id, 101);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProcessingComplete(int id) {
        statusMap.put(id, 200);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setError(int id, int errorCode) {
        statusMap.put(id, errorCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPerctComplete(int id) {
        Integer perctComplete = statusMap.get(id);
        return perctComplete != null ? perctComplete : 0;
    }
}
