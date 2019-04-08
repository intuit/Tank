package com.intuit.tank.harness.logging;

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

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.harness.HostInfo;
import com.intuit.tank.logging.SourceType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.apache.logging.log4j.ThreadContext;

/**
 * 
 * @author dangleton
 * 
 */
public class ThreadLocalLogEvent extends ThreadLocal<LogEvent> {
    public LogEvent initialValue() {
        LogEvent logEvent = new LogEvent();
        logEvent.setActiveProfile(APITestHarness.getInstance().getAgentRunData().getActiveProfile());
        logEvent.setProjectName(APITestHarness.getInstance().getAgentRunData().getProjectName());
        logEvent.setJobId(APITestHarness.getInstance().getAgentRunData().getJobId());
        logEvent.setInstanceId(APITestHarness.getInstance().getAgentRunData().getInstanceId());
        logEvent.setSourceType(SourceType.agent);
        HostInfo hostInfo = new HostInfo();
        logEvent.setPublicIp(hostInfo.getPublicIp());
        logEvent.setHostname(hostInfo.getPublicHostname());
        logEvent.setThreadId(Thread.currentThread().getName() + " " + Thread.currentThread().getId());

        ThreadContext.put("jobId", APITestHarness.getInstance().getAgentRunData().getJobId());
        ThreadContext.put("projectName", APITestHarness.getInstance().getAgentRunData().getProjectName());
        ThreadContext.put("instanceId", APITestHarness.getInstance().getAgentRunData().getInstanceId());
        ThreadContext.put("publicIp", hostInfo.getPublicIp());
        ThreadContext.put("region", AmazonUtil.getVMRegion().getRegion());
        ThreadContext.put("httpHost", AmazonUtil.getControllerBaseUrl());

        return logEvent;

    }
}
