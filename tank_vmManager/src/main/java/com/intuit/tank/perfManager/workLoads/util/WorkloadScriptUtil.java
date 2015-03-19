/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.perfManager.workLoads.util;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.BaseJob;
import com.intuit.tank.project.Workload;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

/**
 * WorkloadScriptUtil
 * 
 * @author dangleton
 * 
 */
public final class WorkloadScriptUtil {

    private WorkloadScriptUtil() {
        // empty
    }

    /**
     * @param workload
     * @param scriptsXml
     */
    public static String getScriptForWorkload(Workload workload, BaseJob job) {
        HDWorkload hdWorkload = ConverterUtil.convertWorkload(workload, job);
        return ConverterUtil.getWorkloadXML(hdWorkload);
    }
}
