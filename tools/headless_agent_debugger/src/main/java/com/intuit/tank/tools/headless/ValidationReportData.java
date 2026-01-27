/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.tools.headless;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Collects rich validation data and outputs it as JSON for external report generation.
 * This enables pipeline scripts to generate detailed HTML reports with full request/response data.
 */
public class ValidationReportData {
    private static final Logger LOG = LogManager.getLogger(ValidationReportData.class);
    private static final int BODY_MAX_LENGTH = 10000; // Truncate very large bodies
    
    private final List<StepData> steps = new ArrayList<>();
    private final SummaryData summary = new SummaryData();
    
    public ValidationReportData(String workloadName, int projectId) {
        summary.workloadName = workloadName;
        summary.projectId = projectId;
        summary.startTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
    
    /**
     * Records a completed step with all its details.
     */
    public void recordStep(DebugStep debugStep, boolean success, List<String> validationErrors) {
        StepData step = new StepData();
        
        // Basic info
        if (debugStep.getStepRun() != null) {
            step.stepIndex = debugStep.getStepRun().getStepIndex() + 1;
            step.info = debugStep.getStepRun().getInfo();
        }
        step.success = success;
        step.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        
        // Request details
        if (debugStep.getRequest() != null) {
            BaseRequest req = debugStep.getRequest();
            step.request = new RequestData();
            step.request.method = req.getMethod();
            step.request.url = req.getRequestUrl();
            step.request.contentType = req.getContentType();
            step.request.headers = req.getHeaderInformation() != null ? 
                new LinkedHashMap<>(req.getHeaderInformation()) : null;
            step.request.body = truncateBody(req.getBody());
        }
        
        // Response details
        if (debugStep.getResponse() != null) {
            BaseResponse resp = debugStep.getResponse();
            step.response = new ResponseData();
            step.response.httpCode = resp.getHttpCode();
            step.response.httpMessage = resp.getHttpMsg();
            step.response.responseTime = resp.getResponseTime();
            step.response.headers = resp.getHeaders() != null ? 
                new LinkedHashMap<>(resp.getHeaders()) : null;
            step.response.body = truncateBody(resp.getBody());
        }
        
        // Variables (only capture changed ones to reduce size)
        Map<String, String> entry = debugStep.getEntryVariables();
        Map<String, String> exit = debugStep.getExitVariables();
        if (entry != null && exit != null) {
            step.variableChanges = new LinkedHashMap<>();
            for (String key : exit.keySet()) {
                String before = entry.get(key);
                String after = exit.get(key);
                if (!Objects.equals(before, after)) {
                    VariableChange change = new VariableChange();
                    change.before = before;
                    change.after = after;
                    step.variableChanges.put(key, change);
                }
            }
            if (step.variableChanges.isEmpty()) {
                step.variableChanges = null; // Don't include if no changes
            }
        }
        
        // Validation errors
        if (validationErrors != null && !validationErrors.isEmpty()) {
            step.errors = new ArrayList<>(validationErrors);
        }
        
        steps.add(step);
        
        // Update counts
        if (step.request == null || "null".equals(step.request.method)) {
            summary.skippedCount++;
        } else if (success) {
            summary.successCount++;
        } else {
            summary.failureCount++;
        }
    }
    
    /**
     * Sets final summary metadata before writing.
     */
    public void setFinalMetadata(String authId, String username, String srsVersion, 
                                  boolean filingStatus, boolean taxhubFlow) {
        summary.authId = authId;
        summary.username = username;
        summary.srsVersion = srsVersion;
        summary.filingStatus = filingStatus;
        summary.taxhubFlow = taxhubFlow;
        summary.endTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        summary.totalSteps = steps.size();
        summary.executedCount = summary.successCount + summary.failureCount;
        if (summary.totalSteps > 0) {
            summary.executionPercentage = (int) Math.round(((double) summary.executedCount / summary.totalSteps) * 100);
        }
    }
    
    /**
     * Writes the collected data to a JSON file.
     */
    public void writeToFile(File outputDir) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            
            ReportOutput output = new ReportOutput();
            output.summary = summary;
            output.steps = steps;
            
            File jsonFile = new File(outputDir, "validation-report.json");
            mapper.writeValue(jsonFile, output);
            LOG.info("Validation report JSON written to: " + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Error writing validation report JSON: " + e.getMessage(), e);
        }
    }
    
    private String truncateBody(String body) {
        if (body == null) return null;
        if (body.length() <= BODY_MAX_LENGTH) return body;
        return body.substring(0, BODY_MAX_LENGTH) + "...[truncated]";
    }
    
    // Data classes for JSON serialization
    public static class ReportOutput {
        public SummaryData summary;
        public List<StepData> steps;
    }
    
    public static class SummaryData {
        public String workloadName;
        public int projectId;
        public String authId;
        public String username;
        public String srsVersion;
        public boolean filingStatus;
        public boolean taxhubFlow;
        public String startTime;
        public String endTime;
        public int totalSteps;
        public int executedCount;
        public int successCount;
        public int failureCount;
        public int skippedCount;
        public int executionPercentage;
    }
    
    public static class StepData {
        public int stepIndex;
        public String info;
        public boolean success;
        public String timestamp;
        public RequestData request;
        public ResponseData response;
        public Map<String, VariableChange> variableChanges;
        public List<String> errors;
    }
    
    public static class RequestData {
        public String method;
        public String url;
        public String contentType;
        public Map<String, String> headers;
        public String body;
    }
    
    public static class ResponseData {
        public int httpCode;
        public String httpMessage;
        public long responseTime;
        public Map<String, String> headers;
        public String body;
    }
    
    public static class VariableChange {
        public String before;
        public String after;
    }
}
