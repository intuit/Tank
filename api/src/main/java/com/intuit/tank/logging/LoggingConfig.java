package com.intuit.tank.logging;

import com.intuit.tank.harness.AmazonUtil;
import org.apache.logging.log4j.ThreadContext;
import com.intuit.tank.harness.HostInfo;

public class LoggingConfig {
    public static void setupThreadContext() {
        if(AmazonUtil.isInAmazon()) {
            HostInfo hostInfo = new HostInfo();
            ThreadContext.put("jobId", AmazonUtil.getJobId());
            ThreadContext.put("projectName", AmazonUtil.getProjectName());
            ThreadContext.put("instanceId", AmazonUtil.getInstanceId());
            ThreadContext.put("publicIp", hostInfo.getPublicIp());
            ThreadContext.put("location", AmazonUtil.getZone());
            ThreadContext.put("httpHost", AmazonUtil.getControllerBaseUrl());
            ThreadContext.put("loggingProfile", AmazonUtil.getLoggingProfile().getDisplayName());
        }
    }

    public static void clearThreadContext() {
        ThreadContext.clearAll();
    }
}
