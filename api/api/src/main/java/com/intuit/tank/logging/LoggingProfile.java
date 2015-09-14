package com.intuit.tank.logging;

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

public enum LoggingProfile {

    STANDARD("Standard", "Logs common fields.",
            LogFields.EventType,
            LogFields.SourceType,
            LogFields.InstanceId,
            LogFields.PublicIp,
            LogFields.Hostname,
            LogFields.JobId,
            LogFields.ThreadId,
            LogFields.TransactionId,
            LogFields.LoggingKey,
            LogFields.ProjectName,
            LogFields.TestPlanName,
            LogFields.GroupName,
            LogFields.ScriptName,
            LogFields.StepName,
            LogFields.StepIndex,
            LogFields.StepGroupName,
            LogFields.RequestUrl,
            LogFields.HttpResponseTime,
            LogFields.TestIteration
    ),
    USER_VARIABLE("User Variables", "Logs common fields plus user variables.",
            LogFields.EventType,
            LogFields.SourceType,
            LogFields.InstanceId,
            LogFields.PublicIp,
            LogFields.Hostname,
            LogFields.JobId,
            LogFields.ThreadId,
            LogFields.TransactionId,
            LogFields.LoggingKey,
            LogFields.ProjectName,
            LogFields.TestPlanName,
            LogFields.GroupName,
            LogFields.ScriptName,
            LogFields.StepName,
            LogFields.StepIndex,
            LogFields.StepGroupName,
            LogFields.TestIteration,
            LogFields.RequestUrl,
            LogFields.HttpResponseTime,
            LogFields.UserVariables
    ),
    TRACE("Trace Calls", "Logs all calls made.",
            LogFields.EventType,
            LogFields.SourceType,
            LogFields.InstanceId,
            LogFields.PublicIp,
            LogFields.Hostname,
            LogFields.JobId,
            LogFields.ThreadId,
            LogFields.TransactionId,
            LogFields.LoggingKey,
            LogFields.ProjectName,
            LogFields.TestPlanName,
            LogFields.GroupName,
            LogFields.ScriptName,
            LogFields.StepName,
            LogFields.StepIndex,
            LogFields.StepGroupName,
            LogFields.TestIteration,
            LogFields.RequestUrl,
            LogFields.HttpResponseTime,
            LogFields.UserVariables
    ),
    VERBOSE("Verbose", "Logs all available fields.",
            LogFields.EventType,
            LogFields.SourceType,
            LogFields.InstanceId,
            LogFields.PublicIp,
            LogFields.Hostname,
            LogFields.JobId,
            LogFields.ThreadId,
            LogFields.TransactionId,
            LogFields.LoggingKey,
            LogFields.ProjectName,
            LogFields.TestPlanName,
            LogFields.GroupName,
            LogFields.ScriptName,
            LogFields.StepName,
            LogFields.StepIndex,
            LogFields.StepGroupName,
            LogFields.TestIteration,
            LogFields.RequestUrl,
            LogFields.HttpRequestHeaders,
            LogFields.HttpRequestBody,
            LogFields.HttpResponseHeaders,
            LogFields.HttpResponseBody,
            LogFields.HttpResponseTime,
            LogFields.UserVariables,
            LogFields.ValidationStatus,
            LogFields.ValidationCriteria,
            LogFields.PreValidationCriteria
    ),
    TERSE("Terse", "Logs minimum fields.",
            LogFields.EventType,
            LogFields.SourceType,
            LogFields.InstanceId,
            LogFields.PublicIp,
            LogFields.Hostname,
            LogFields.JobId,
            LogFields.TestIteration,
            LogFields.ThreadId,
            LogFields.TransactionId
    )

    ;
    private String displayName;
    private String description;
    private LogFields[] fieldsToLog;

    private LoggingProfile(String displayName, String description, LogFields... fieldsToLog) {
        this.displayName = displayName;
        this.description = description;
        this.fieldsToLog = fieldsToLog;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFieldsToLog(LogFields[] fieldsToLog) {
        this.fieldsToLog = fieldsToLog;
    }

    public LogFields[] getFieldsToLog() {
        return fieldsToLog;
    }

    public boolean isFieldLogged(LogFields field) {
        boolean ret = false;
        for (LogFields f : fieldsToLog) {
            if (f == field) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public static LoggingProfile fromString(String s) {
        LoggingProfile ret = LoggingProfile.STANDARD;
        try {
            ret = LoggingProfile.valueOf(s);
        } catch (Exception e) {
            // ignore
        }
        return ret != null ? ret : LoggingProfile.STANDARD;
    }

}
