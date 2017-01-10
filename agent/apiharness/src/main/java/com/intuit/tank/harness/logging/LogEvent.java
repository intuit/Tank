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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDValidation;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LogFields;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.logging.SourceType;
import com.intuit.tank.script.RequestDataPhase;

public class LogEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    static enum HttpType {
        REQUEST,
        RESPONSE
    }

    private LoggingProfile activeProfile = LoggingProfile.STANDARD;
    private LogEventType eventType = LogEventType.Other;
    private SourceType sourceType = SourceType.agent;
    private String message; // message
    private String instanceId; // instnaceId
    private String publicIp; // IP of Agent
    private String hostname; // hostname of Agent
    private String jobId; // job id as seen on Tank job queue
    private String threadId; // thread ID to help trace events in sequence
    private String loggingKey; // transaction label
    private String projectName; // project name
    private String stepIndex; // request step number
    private TestStep step; // request step number
    private HDScript script; // if script groups are created within the project
    private HDScriptGroup group;
    private HDTestPlan testPlan; // if the project has multiple test plans this field can be useful
    private String iteration; // current iteration number
    private BaseRequest request;
    private Variables variables; // all static and dynamic parameters set by the user
    private String validationStatus; // if validation is not set then the field can contain value = "NA"
    private String transactionId; // Transaction Id
    private String stepGroupName; // Step group name

    public LogEvent() {
        super();
    }

    public Map<String, String> buildMessage(LogEventType type, String message) {
        setEventType(type);
        setMessage(message);
        return buildMessage();
    }

    public Map<String, String> buildMessage() {
    	Map<String, String> map = new HashMap<String,String>();
        appendField(map, LogFields.EventType, eventType.name());
        appendField(map, LogFields.SourceType, sourceType.name());
        appendField(map, LogFields.InstanceId, instanceId);
        appendField(map, LogFields.PublicIp, publicIp);
        appendField(map, LogFields.Hostname, hostname);
        appendField(map, LogFields.JobId, jobId);
        appendField(map, LogFields.TransactionId, transactionId);
        appendField(map, LogFields.ThreadId, threadId);
        appendField(map, LogFields.LoggingKey, loggingKey);
        appendField(map, LogFields.ProjectName, projectName);
        appendField(map, LogFields.TestPlanName, testPlan != null ? testPlan.getTestPlanName() : null);
        appendField(map, LogFields.GroupName, group != null ? group.getName() : null);
        appendField(map, LogFields.ScriptName, script != null ? script.getName() : null);
        appendField(map, LogFields.StepName, step != null ? step.getInfo() : null);
        appendField(map, LogFields.StepIndex, stepIndex);
        appendField(map, LogFields.StepGroupName, stepGroupName);
        appendField(map, LogFields.TestIteration, iteration);
        appendField(map, LogFields.RequestUrl, buildUrl());
        appendField(map, LogFields.ValidationStatus, validationStatus);
        appendField(map, LogFields.HttpResponseTime, getResponseTime());
        appendField(map, LogFields.UserVariables, getVariableValues());
        appendField(map, LogFields.ValidationCriteria, getValidationCriteria(RequestDataPhase.POST_REQUEST));
        appendField(map, LogFields.PreValidationCriteria, getValidationCriteria(RequestDataPhase.PRE_REQUEST));
        appendField(map, LogFields.HttpRequestHeaders, getHeaders(HttpType.REQUEST));
        appendField(map, LogFields.HttpRequestBody, getBody(HttpType.REQUEST));
        appendField(map, LogFields.HttpResponseHeaders, getHeaders(HttpType.RESPONSE));
        appendField(map, LogFields.HttpResponseBody, getBody(HttpType.RESPONSE));
        map.put("Message", message);
        return map;
    }

    private String getValidationCriteria(RequestDataPhase type) {
        StringBuilder sb = new StringBuilder();
        if (step instanceof RequestStep) {
            RequestStep rs = (RequestStep) step;
            HDValidation validation = rs.getResponse().getValidation();
            if (validation != null) {
                addValidation(sb, type, "CookieValidation", validation.getCookieValidation());
                addValidation(sb, type, "HeaderValidation", validation.getHeaderValidation());
                addValidation(sb, type, "BodyValidation", validation.getBodyValidation());
            }

        }
        return sb.toString();
    }

    private void addValidation(StringBuilder sb, RequestDataPhase type, String validataionType,
            List<ValidationData> validations) {
        StringBuilder vsb = new StringBuilder();
        if (validations != null && !validations.isEmpty()) {
            for (ValidationData validation : validations) {
                if (validation.getPhase() == type) {
                    appendField(vsb, validation.getKey(), validation.getValue(), validation.getCondition());
                }
            }
            if (vsb.length() > 0) {
                sb.append(validataionType).append(" : ");
                sb.append(vsb.toString());
            }
        }

    }

    private String getBody(HttpType type) {
        String ret = null;
        if (request != null) {
            if (type == HttpType.REQUEST) {
                ret = request.getBody();
            } else if (request.getResponse() != null) {
                ret = request.getResponse().getBody();
                ret = truncateBody(ret);
            }
        }
        return ret;
    }

    private String truncateBody(String ret) {
        if (ret != null) {
            String mimeType = request.getResponse().getHttpHeader("Content-Type");
            if (LogUtil.isTextMimeType(mimeType)) {
                int maxBodySize = 5000;
                try {
                    maxBodySize = APITestHarness.getInstance().getTankConfig().getAgentConfig()
                            .getMaxBodyReportSize();
                } catch (Exception e) {
                    // LOG.warn("Cannot read config. Using maxBodySize of " + maxBodySize);
                }
                if (ret.length() > maxBodySize) {
                    ret = ret.substring(0, maxBodySize) + "...";
                }
            } else {
                ret = null;
            }
        }
        return ret;
    }

    private String getHeaders(HttpType type) {
        StringBuilder sb = new StringBuilder();
        if (request != null) {
            Map<String, String> headers = null;
            if (type == HttpType.REQUEST) {
                headers = request.getHeaderInformation();
            } else if (request.getResponse() != null) {
                headers = request.getResponse().getHeaders();
            }
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    appendField(sb, entry.getKey(), entry.getValue(), " : ");
                }
            }
        }
        return sb.toString();
    }

    private String getVariableValues() {
        StringBuilder sb = new StringBuilder();
        if (variables != null) {
            for (Entry<String, String> entry : variables.getVaribleValues().entrySet()) {
            	if (!entry.getKey().toUpperCase().startsWith("UUID_")) {		// Exclude UUID variables, there are just too many.
            		appendField(sb, entry.getKey(), entry.getValue(), "=");
            	}
            }
        }
        return sb.toString();
    }

    private String getResponseTime() {
        String ret = null;
        if (request != null && request.getResponse() != null && request.getResponse().getResponseTime() >= 0) {
            ret = Long.toString(request.getResponse().getResponseTime()) + " ms";
        }
        return ret;
    }

    public String getStepGroupName() {
        return stepGroupName;
    }

    public void setStepGroupName(String stepGroupName) {
        this.stepGroupName = stepGroupName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LogEventType getEventType() {
        return eventType;
    }

    public void setEventType(LogEventType eventType) {
        this.eventType = eventType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getLoggingKey() {
        return loggingKey;
    }

    public void setLoggingKey(String loggingKey) {
        this.loggingKey = loggingKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(String stepIndex) {
        this.stepIndex = stepIndex;
    }

    public TestStep getStep() {
        return step;
    }

    public void setStep(TestStep step) {
        this.step = step;
        this.loggingKey = null;
        this.request = null;
        this.validationStatus = null;
        this.transactionId = null;
        this.stepGroupName = null;
    }

    public HDScript getScript() {
        return script;
    }

    public void setScript(HDScript script) {
        this.script = script;
        setStep(null);
    }

    public HDScriptGroup getGroup() {
        return group;
    }

    public void setGroup(HDScriptGroup group) {
        this.group = group;
        setScript(null);

    }

    public HDTestPlan getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(HDTestPlan testPlan) {
        this.testPlan = testPlan;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public void setRequest(BaseRequest request) {
        this.request = request;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public LoggingProfile getActiveProfile() {
        return activeProfile != null ? activeProfile : LoggingProfile.STANDARD;
    }

    public void setActiveProfile(LoggingProfile activeProfile) {
        this.activeProfile = activeProfile;
    }

    private void appendField(Map<String,String> map, LogFields field, String value) {
        if (getActiveProfile().isFieldLogged(field)) {
            if (StringUtils.isNotBlank(value)) {
                map.put(field.name(), value);
            }
        }
    }

    private void appendField(StringBuilder sb, String key, String value, String separator) {
        if (StringUtils.isNotBlank(value)) {
            sb.append(' ').append(StringUtils.trim(key)).append(separator).append(StringUtils.trim(value)).append(' ');
        }
    }

    private String buildUrl() {
        String ret = null;
        if (request != null) {
            ret = request.getRequestUrl();
        }
        return ret;
    }

}
