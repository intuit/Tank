/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.api.enumerated;

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

import com.intuit.tank.vm.common.TankConstants;

/**
 * ReportingMode
 * 
 * @author dangleton
 * 
 */
public enum ReportingMode {

    Wily(TankConstants.RESULTS_PERF),
    None(TankConstants.RESULTS_NONE),
    Database(TankConstants.RESULTS_DB);

    private String value;

    /**
     * @param value
     */
    private ReportingMode(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public static ReportingMode fromValue(String s) {
        ReportingMode ret = ReportingMode.None;
        if (TankConstants.RESULTS_PERF.equals(s)) {
            ret = ReportingMode.Wily;
        } else if (TankConstants.RESULTS_DB.equals(s)) {
            ret = ReportingMode.Database;
        }
        return ret;
    }

}
