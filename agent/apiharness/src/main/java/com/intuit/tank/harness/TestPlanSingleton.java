package com.intuit.tank.harness;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.parsers.WorkloadParser;
import com.intuit.tank.logging.LogEventType;

public class TestPlanSingleton {
    private static final Logger LOG = LogManager.getLogger(TestPlanSingleton.class);

    static private TestPlanSingleton instance = null;
    private List<HDWorkload> workloads;

    public static TestPlanSingleton getInstance() {
        if (null == instance) {
            instance = new TestPlanSingleton();
        }
        return instance;
    }

    private TestPlanSingleton() {
        workloads = new ArrayList<HDWorkload>();
    }

    public void setTestPlans(String plans) {

        String[] testPlanLists = plans.split(",");

        for (int t = 0; t < testPlanLists.length; t++) {
            try {
                File xmlFile = new File(testPlanLists[t]);
                if (!xmlFile.exists()) {
                    throw new Exception("File not found");
                }

                WorkloadParser parser = new WorkloadParser(xmlFile);
                HDWorkload workload = parser.getWorkload();
                if (workload != null) {
                    workloads.add(workload);
                }
            } catch (Exception e) {
                LOG.error(LogUtil.getLogMessage(e.getMessage(), LogEventType.System), e);
                throw new RuntimeException(e);
            }
        }
    }

    public void setTestPlans(List<String> testPlanXmls) {
        workloads.clear();
        for (String xml : testPlanXmls) {
            WorkloadParser parser = new WorkloadParser(xml);
            HDWorkload workload = parser.getWorkload();
            if (workload != null) {
                workloads.add(workload);
            }
        }
    }

    public List<HDWorkload> getTestPlans() {
        return workloads;
    }
}
