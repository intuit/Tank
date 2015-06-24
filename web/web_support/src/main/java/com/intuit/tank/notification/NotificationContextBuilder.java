/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.notification;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_DATA_FILES_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_DURATION_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_END_TIME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_EVENT_NAME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_EVENT_TIME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_JOB_ID_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_JOB_NAME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_LOAD_START_TIME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_LOCATION_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_NUM_USERS_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_PREDICTED_END_TIME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_PROJECT_ID_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_PROJECT_NAME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_RAMP_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_SCRIPT_INFO_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_START_TIME_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_STEADY_STATE_END_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_STEADY_STATE_START_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_SUMMARY_DATA_URL_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_TERMINATION_POLICY_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_TIMING_DATA_URL_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_VALIDATION_FAILURES_KEY;
import static com.intuit.tank.vm.common.TankConstants.NOTIFICATIONS_EVENT_VARIABLES_KEY;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.api.service.v1.report.ReportService;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.event.NotificationContext;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.TimeUtil;

/**
 * NotificationContextBuilder
 * 
 * @author dangleton
 * 
 */
public class NotificationContextBuilder {

    /**
     * 
     */
    private static final String N_A = "N/A";

    private NotificationContext context = new NotificationContext();
    TankConfig config = new TankConfig();

    private JobInstance jobInstance;

    private VMTracker vmTracker;

    public NotificationContextBuilder(JobEvent e, VMTracker tracker) {
        // build context
        this.vmTracker = tracker;
        JobInstance jobInstance = new JobInstanceDao().findById(Integer.parseInt(e.getJobId()));
        Workload workload = new WorkloadDao().findById(jobInstance.getWorkloadId());
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(e.getJobId());
        this.jobInstance = jobInstance;
        addJobInfo(jobInstance);
        addTrackerInfo(container);
        addProjectInfo(workload);
        addJobEventInfo(e);
        addCalculatedTimes(container);
    }

    public NotificationContextBuilder(JobEvent event, Workload workload, JobInstance jobInstance,
            CloudVmStatusContainer container) {
        this.jobInstance = jobInstance;
        addJobInfo(jobInstance);
        addTrackerInfo(container);
        addProjectInfo(workload);
        addJobEventInfo(event);
        addCalculatedTimes(container);
    }

    public NotificationContext getContext() {
        return context;
    }

    /**
     * @param jobId
     */
    private void addProjectInfo(Workload workload) {
        if (workload != null) {
            Project project = workload.getProject();
            context.addContextEntry(NOTIFICATIONS_EVENT_PROJECT_NAME_KEY, cleanString(project.getName()))
                    .addContextEntry(NOTIFICATIONS_EVENT_PROJECT_ID_KEY, Integer.toString(project.getId()));
        } else {
            context.addContextEntry(NOTIFICATIONS_EVENT_PROJECT_NAME_KEY, N_A)
                    .addContextEntry(NOTIFICATIONS_EVENT_PROJECT_ID_KEY, N_A);
        }
        addScriptInfo(workload);
    }

    /**
     * @param jobId
     */
    private void addTrackerInfo(CloudVmStatusContainer container) {
        ValidationStatus failures = new ValidationStatus();
        String actualDuration = N_A;
        Date startTime = container != null && container.getStartTime() != null ? container.getStartTime() : new Date();
        Date endTime = container != null && container.getEndTime() != null ? container.getEndTime() : null;
        if (container != null) {
            if (endTime != null && startTime != null) {
                actualDuration = TimeUtil.toTimeString(endTime.getTime() - startTime.getTime());
            }

            for (CloudVmStatus status : container.getStatuses()) {
                failures.addFailures(status.getValidationFailures());
            }
        }
        context.addContextEntry(NOTIFICATIONS_EVENT_START_TIME_KEY, cleanDate(startTime))
                .addContextEntry(NOTIFICATIONS_EVENT_END_TIME_KEY, cleanDate(endTime));

        context.addContextEntry(TankConstants.NOTIFICATIONS_EVENT_ACTUAL_DURTION, actualDuration);
        context.addContextEntry(NOTIFICATIONS_EVENT_VALIDATION_FAILURES_KEY, formatInt(failures.getTotal()));

    }

    private void addJobEventInfo(JobEvent e) {
        context.getContext().putAll(e.getExtraContext());
        context.addContextEntry(NOTIFICATIONS_EVENT_EVENT_NAME_KEY, e.getEvent().getDisplay());
        context.addContextEntry(NOTIFICATIONS_EVENT_EVENT_TIME_KEY, cleanDate(new Date()));
        context.addContextEntry(TankConstants.NOTIFICATIONS_EVENT_BASE_URL_KEY, config.getControllerBase());

    }

    /**
     * @param jobInstance
     */
    private void addJobInfo(JobInstance jobInstance) {
        context.addContextEntry(NOTIFICATIONS_EVENT_JOB_NAME_KEY, cleanString(jobInstance.getName()));
        context.addContextEntry(NOTIFICATIONS_EVENT_JOB_ID_KEY, Integer.toString(jobInstance.getId()));
        context.addContextEntry(NOTIFICATIONS_EVENT_RAMP_KEY, TimeUtil.toTimeString(jobInstance.getRampTime()));
        context.addContextEntry(NOTIFICATIONS_EVENT_NUM_USERS_KEY, formatInt(jobInstance.getTotalVirtualUsers()));
        context.addContextEntry(NOTIFICATIONS_EVENT_TERMINATION_POLICY_KEY, jobInstance.getTerminationPolicy()
                .getDisplay());
        context.addContextEntry(NOTIFICATIONS_EVENT_VARIABLES_KEY, cleanString(mapToString(jobInstance.getVariables())));
        context.addContextEntry(NOTIFICATIONS_EVENT_LOCATION_KEY, cleanString(jobInstance.getLocation()));
        context.addContextEntry(TankConstants.NOTIFICATIONS_EVENT_LOGGING_PROFILE_KEY,
                LoggingProfile.fromString(jobInstance.getLoggingProfile()).getDisplayName());
        context.addContextEntry(TankConstants.NOTIFICATIONS_EVENT_STOP_BEHAVIOR_KEY,
                StopBehavior.fromString(jobInstance.getStopBehavior()).getDisplay());
        context.addContextEntry(NOTIFICATIONS_EVENT_DURATION_KEY,
                TimeUtil.toTimeString(jobInstance.getSimulationTime()));
        String dataFiles = getDataFileString(jobInstance);
        context.addContextEntry(NOTIFICATIONS_EVENT_DATA_FILES_KEY, cleanString(dataFiles));
        String jobId = Integer.toString(jobInstance.getId());
        context.addContextEntry(NOTIFICATIONS_EVENT_TIMING_DATA_URL_KEY, getTimingDataUrl(jobId));
        context.addContextEntry(NOTIFICATIONS_EVENT_SUMMARY_DATA_URL_KEY, getTimingSummaryUrl(jobId));
        context.addContextEntry(NOTIFICATIONS_EVENT_LOAD_START_TIME_KEY, cleanDate(jobInstance.getStartTime()));
    }

    private String cleanString(String s) {
        return StringUtils.isBlank(s) ? NotificationContextBuilder.N_A : s;
    }

    private String cleanDate(Date d) {
        return d == null ? NotificationContextBuilder.N_A : DateFormatUtils.format(d, TankConstants.DATE_FORMAT_WITH_TIMEZONE);
    }

    private String formatInt(int i) {
        return NumberFormat.getIntegerInstance().format(i);
    }

    private String getDataFileString(JobInstance jobInstance) {
        DataFileDao dataFileDao = new DataFileDao();
        StringBuilder dataFiles = new StringBuilder();
        for (EntityVersion entity : jobInstance.getDataFileVersions()) {
            if (dataFiles.length() != 0) {
                dataFiles.append(", ");
            }
            DataFile df = dataFileDao.findById(entity.getObjectId());
            if (df != null) {
                dataFiles.append(df.getPath());
            }
        }
        return dataFiles.toString();
    }

    private void addCalculatedTimes(CloudVmStatusContainer container) {
        long duration = jobInstance.getSimulationTime();
        long ramp = jobInstance.getRampTime();
        Date startTime = container != null && container.getStartTime() != null ? container.getStartTime() : new Date();
        Date loadStartTime = jobInstance.getStartTime();
        Date calcTime = loadStartTime != null ? loadStartTime : startTime;
        Date predictedEndTimeDate = new Date(calcTime.getTime() + duration + ramp);
        Date steadyStateStartDate = new Date(calcTime.getTime() + ramp);
        Date steadyStateEndDate = new Date(calcTime.getTime() + duration);
        context.addContextEntry(NOTIFICATIONS_EVENT_PREDICTED_END_TIME_KEY, cleanDate(predictedEndTimeDate))
                .addContextEntry(NOTIFICATIONS_EVENT_STEADY_STATE_START_KEY, cleanDate(steadyStateStartDate))
                .addContextEntry(NOTIFICATIONS_EVENT_STEADY_STATE_END_KEY, cleanDate(steadyStateEndDate));

    }

    private String mapToString(Map<String, String> map) {
        StringBuilder ret = new StringBuilder();
        if (!map.isEmpty()) {
            ret.append("[");
            for (Entry<String, String> entry : map.entrySet()) {
                if (ret.length() != 1) {
                    ret.append(", ");
                }
                ret.append(entry.getKey()).append(" = ").append(entry.getValue());
            }
            ret.append("]");
        }
        return ret.toString();
    }

    private void addScriptInfo(Workload workload) {
        StringBuilder ret = new StringBuilder();
        if (workload != null) {
            List<TestPlan> testPlans = workload.getTestPlans();
            for (TestPlan plan : testPlans) {
                ret.append("Test Plan: ").append(plan.getName());
                ret.append("<br/>\n");
                for (ScriptGroup group : plan.getScriptGroups()) {
                    ret.append("&nbsp;&nbsp;");
                    ret.append("Group: " + group.getName());
                    if (group.getLoop() > 1) {
                        ret.append(" (Loops: " + group.getLoop() + ")");
                    }
                    ret.append("<br/>\n");
                    for (ScriptGroupStep groupStep : group.getScriptGroupSteps()) {
                        Script script = groupStep.getScript();
                        ret.append("&nbsp;&nbsp;");
                        ret.append("&nbsp;&nbsp;");
                        ret.append("Script: " + script.getName());
                        if (groupStep.getLoop() > 1) {
                            ret.append(" (Loops: " + groupStep.getLoop() + ")");
                        }
                        ret.append("<br/>\n");
                    }
                }
            }
        }
        context = context.addContextEntry(NOTIFICATIONS_EVENT_SCRIPT_INFO_KEY, ret.toString());
    }

    private String getTimingDataUrl(String jobId) {
        StringBuilder ret = new StringBuilder();
        ret.append(config.getControllerBase());
        ret.append(TankConstants.REST_SERVICE_CONTEXT + ReportService.SERVICE_RELATIVE_PATH
                + ReportService.METHOD_TIMING_CSV);
        ret.append("/");
        ret.append(jobId);
        return ret.toString();
    }

    private String getTimingSummaryUrl(String jobId) {
        StringBuilder ret = new StringBuilder();
        ret.append(config.getControllerBase());
        ret.append(TankConstants.REST_SERVICE_CONTEXT + ReportService.SERVICE_RELATIVE_PATH
                + ReportService.METHOD_TIMING_SUMMARY_HTML);
        ret.append("/");
        ret.append(jobId);
        return ret.toString();
    }

}
