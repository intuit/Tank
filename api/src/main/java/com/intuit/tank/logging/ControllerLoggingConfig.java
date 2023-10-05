package com.intuit.tank.logging;

import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.vm.vmManager.JobRequest;
import org.apache.logging.log4j.ThreadContext;
import com.intuit.tank.harness.HostInfo;

public class ControllerLoggingConfig {

    private static String jobId = "unknown";
    private static String env = "unknown";
    private static String projectName = "unknown";
    private static String instanceId = "unknown";
    private static String publicIp = "unknown";
    private static String location = "unknown";
    private static String httpHost = "unknown";
    private static String loggingProfile = "unknown";
    private static String useEips = "unknown";
    private static String workloadType = "unknown";
    private static String terminationPolicy = "unknown";
    private static String stopBehavior = "unknown";

    public static void initializeControllerThreadContext(JobRequest job, String instanceName, String controllerBaseUrl) {
        jobId = job.getId();
        env = instanceName;
        httpHost = controllerBaseUrl;
        useEips = String.valueOf(job.isUseEips());
        workloadType = job.getIncrementStrategy().getDisplay();
        terminationPolicy = job.getTerminationPolicy().getDisplay();
        stopBehavior = StopBehavior.valueOf(job.getStopBehavior()).getDisplay();

        if(AmazonUtil.isInAmazon()) {
            HostInfo hostInfo = new HostInfo();
            instanceId = AmazonUtil.getInstanceId();
            publicIp = hostInfo.getPublicIp();
            location = AmazonUtil.getZone();
            loggingProfile = AmazonUtil.getLoggingProfile().getDisplayName();
            projectName = AmazonUtil.getProjectName();
        }
    }

    public static void setupThreadContext() {
        ThreadContext.put("jobId", jobId);
        ThreadContext.put("projectName", projectName);
        ThreadContext.put("instanceId", instanceId);
        ThreadContext.put("publicIp", publicIp);
        ThreadContext.put("location", location);
        ThreadContext.put("env", env);
        ThreadContext.put("httpHost", httpHost);
        ThreadContext.put("loggingProfile", loggingProfile);
        ThreadContext.put("useEips", useEips);
        ThreadContext.put("workloadType", workloadType);
        ThreadContext.put("terminationPolicy", terminationPolicy);
        ThreadContext.put("stopBehavior", stopBehavior);
    }
}
