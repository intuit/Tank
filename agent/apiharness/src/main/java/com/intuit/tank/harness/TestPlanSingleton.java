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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        List<String> testPlanLists = Arrays.asList(plans.split(","));

        workloads = testPlanLists.stream()
                .filter(xmlFile -> {
                    if (new File(xmlFile).exists()) {
                        return true;
                    } else {
                        Exception e = new Exception("File not found");
                        LOG.error(LogUtil.getLogMessage(e.getMessage(), LogEventType.System), e);
                        return false;
                    }
                })
                .map(xml -> new WorkloadParser(xml).getWorkload())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void setTestPlans(List<String> testPlanXmls) {
        workloads = testPlanXmls.stream()
                .map(xml -> new WorkloadParser(xml).getWorkload())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<HDWorkload> getTestPlans() {
        return workloads;
    }
}
