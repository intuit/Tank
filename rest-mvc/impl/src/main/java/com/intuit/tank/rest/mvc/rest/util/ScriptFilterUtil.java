/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.*;
import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.script.models.ScriptTO;
import com.intuit.tank.script.RequestDataType;
import com.intuit.tank.tools.script.LoggingOutputLogger;
import com.intuit.tank.tools.script.ScriptRunner;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import com.intuit.tank.vm.script.util.AddActionScope;
import com.intuit.tank.vm.script.util.RemoveActionScope;
import com.intuit.tank.vm.script.util.ReplaceActionScope;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptException;
import java.util.*;

public class ScriptFilterUtil {

    private static final Logger logger = LogManager.getLogger(ScriptFilterUtil.class);

    /**
     * Applys the filters specified by filterIds
     * 
     * @param filters
     * @param steps
     */
    public static List<ScriptStep> applyFilters(Collection<ScriptFilter> filters, List<ScriptStep> steps) {
        Script script = new Script();
        script.setScriptSteps(steps);
        applyFiltersToScript(filters, script);
        return script.getScriptSteps();
    }

    /**
     * Applys the filters specified by filterIds
     * 
     * @param filterIds
     * @param script
     */
    public static void applyFilters(List<Integer> filterIds, Script script) {
        ScriptFilterDao dao = new ScriptFilterDao();
        applyFiltersToScript(dao.findForIds(filterIds), script);
    }

    /**
     * Applys the filters specified by filterIds
     * 
     * @param filters
     * @param script
     */
    public static void applyFiltersToScript(Collection<ScriptFilter> filters, Script script) {
        ExternalScriptDao externalScriptDao = new ExternalScriptDao();
        List<ScriptFilter> internalFilters = new ArrayList<ScriptFilter>();
        List<ScriptFilter> externalFilters = new ArrayList<ScriptFilter>();

        for (ScriptFilter filter : filters) {
            if (filter.getFilterType() == ScriptFilterType.EXTERNAL) {
                externalFilters.add(filter);
            } else {
                internalFilters.add(filter);
            }
        }

        for (ScriptFilter filter : internalFilters) {
            AWSXRay.createSubsegment("Apply.InternalFilter." + filter.getName(), (subsegment) -> {
                applyFilter(filter, script.getScriptSteps());
            });
        }
        if (!externalFilters.isEmpty()) {
            // DIAGNOSTIC: Track external script filter performance
            long extGroupStart = System.nanoTime();
            long extGroupStartMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            
            LoggingOutputLogger outputLogger = new LoggingOutputLogger();
            
            // DIAGNOSTIC: Track ScriptTO conversion time
            long conversionStart = System.nanoTime();
            ScriptTO scriptTo = ScriptServiceUtil.scriptToTransferObject(script);
            long conversionTime = (System.nanoTime() - conversionStart) / 1_000_000;
            logger.warn("EXTERNAL_CONVERSION_PERF: scriptId={}, stepCount={}, Script->ScriptTO time={}ms",
                script.getId(), script.getScriptSteps().size(), conversionTime);
            
            ScriptRunner runner = new ScriptRunner();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("script", scriptTo);
            
            for (ScriptFilter filter : externalFilters) {
                // DIAGNOSTIC: Track per-filter execution
                long filterStart = System.nanoTime();
                long filterStartMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                
                ExternalScript externalScript = externalScriptDao.findById(filter.getExternalScriptId());
                logger.info("Running external Script: " + externalScript);
                
                if (externalScript != null) {
                    Subsegment subsegment = AWSXRay.beginSubsegment("Apply.ExternalFilter." + externalScript.getName());
                    try {
                        // DIAGNOSTIC: Track script execution time
                        long execStart = System.nanoTime();
                        runner.runScript(externalScript.getName(), externalScript.getScript(),
                                externalScript.getEngine(),
                                map,
                                outputLogger);
                        long execTime = (System.nanoTime() - execStart) / 1_000_000;
                        
                        long filterElapsed = (System.nanoTime() - filterStart) / 1_000_000;
                        long filterEndMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                        long filterMemDelta = (filterEndMem - filterStartMem) / 1024 / 1024;
                        
                        logger.warn("EXTERNAL_SCRIPT_PERF: filter='{}', scriptName='{}', scriptId={}, stepCount={}, execTime={}ms, totalTime={}ms, memDelta={}MB",
                            filter.getName(), externalScript.getName(), script.getId(), 
                            scriptTo.getSteps().size(), execTime, filterElapsed, filterMemDelta);
                    } catch (ScriptException e) {
                        logger.error("Error Running Script: " + e);
                        subsegment.addException(e);
                        throw new RuntimeException(e);
                    } finally {
                        AWSXRay.endSubsegment();
                    }
                }
            }
            
            // DIAGNOSTIC: Track object conversion back to entities
            long reconversionStart = System.nanoTime();
            script.getScriptSteps().clear();
            for (ScriptStepTO stepTo : scriptTo.getSteps()) {
                script.getScriptSteps().add(ScriptServiceUtil.transferObjectToScriptStep(stepTo));
            }
            long reconversionTime = (System.nanoTime() - reconversionStart) / 1_000_000;
            
            long extGroupElapsed = (System.nanoTime() - extGroupStart) / 1_000_000;
            long extGroupEndMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long extGroupMemDelta = (extGroupEndMem - extGroupStartMem) / 1024 / 1024;
            
            logger.warn("EXTERNAL_RECONVERSION_PERF: scriptId={}, stepCount={}, ScriptTO->Script time={}ms",
                script.getId(), script.getScriptSteps().size(), reconversionTime);
            logger.warn("EXTERNAL_GROUP_PERF: scriptId={}, filterCount={}, totalTime={}ms, totalMemDelta={}MB, breakdown: conversion={}ms, reconversion={}ms",
                script.getId(), externalFilters.size(), extGroupElapsed, extGroupMemDelta, conversionTime, reconversionTime);
        }
    }

    /**
     *
     * @param filter
     * @param steps
     */
    protected static void applyFilter(ScriptFilter filter, List<ScriptStep> steps) {
        // DIAGNOSTIC: Track timing and memory for filter performance analysis
        long startTime = System.nanoTime();
        long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        boolean allConditionsMustPass = filter.getAllConditionsMustPass();
        Set<ScriptStep> stepsToDelete = new HashSet<ScriptStep>();
        SortedMap<Integer, ScriptStep> stepsToAdd = new TreeMap<Integer, ScriptStep>();
        for (ScriptStep step : steps) {
            boolean conditionsMet = true;

            for (ScriptFilterCondition condition : filter.getConditions()) {
                conditionsMet = testConditions(condition.getScope(), condition, step, steps);

                if (!conditionsMet && allConditionsMustPass) {
                    break;
                }

                else if (conditionsMet && !allConditionsMustPass) {
                    break;
                }
            }

            if (conditionsMet) {
                doAction(steps, stepsToAdd, stepsToDelete, filter.getActions(), step);
            }
        }
        int reqCount = 0;
        for (Integer index : stepsToAdd.keySet()) {
            steps.add(index + reqCount, stepsToAdd.get(index));
            reqCount++;
        }

        for (ScriptStep delete : stepsToDelete) {
            steps.remove(delete);
        }
        
        // DIAGNOSTIC: Log filter performance metrics
        long elapsed = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms
        long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memDelta = (endMem - startMem) / 1024 / 1024; // Convert to MB
        
        logger.warn("FILTER_PERF: filter='{}', steps={}, added={}, deleted={}, time={}ms, memDelta={}MB", 
            filter.getName(), steps.size(), stepsToAdd.size(), stepsToDelete.size(), elapsed, memDelta);
    }

    private static void doAction(List<ScriptStep> steps, SortedMap<Integer, ScriptStep> stepsToAdd,
            Set<ScriptStep> stepsToDelete, Set<ScriptFilterAction> actions, ScriptStep step) {

        for (ScriptFilterAction scriptFilterAction : actions) {
            if (scriptFilterAction.getAction().equals(ScriptFilterActionType.add)) {
                doAddAction(steps, stepsToAdd, scriptFilterAction, step);
            } else if (scriptFilterAction.getAction().equals(ScriptFilterActionType.remove)) {
                doRemoveAction(steps, stepsToAdd, stepsToDelete, scriptFilterAction, step);
            } else if (scriptFilterAction.getAction().equals(ScriptFilterActionType.replace)) {
                doReplaceAction(steps, stepsToAdd, stepsToDelete, scriptFilterAction, step);
            }
        }

    }

    private static void doReplaceAction(List<ScriptStep> steps, SortedMap<Integer, ScriptStep> stepsToAdd,
            Set<ScriptStep> stepsToDelete, ScriptFilterAction scriptFilterAction, ScriptStep step) {

        if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.requestHeader.getValue())) {
            step.getRequestheaders().stream()
                    .filter(qs -> qs.getKey().trim().equals(scriptFilterAction.getKey()))
                    .findFirst().ifPresent(qs -> qs.setValue(scriptFilterAction.getValue()));
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.requestCookie.getValue())) {
            step.getRequestCookies().stream()
                    .filter(qs -> qs.getKey().trim().equals(scriptFilterAction.getKey()))
                    .findFirst().ifPresent(qs -> qs.setValue(scriptFilterAction.getValue()));
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.queryString.getValue())) {
            step.getQueryStrings().stream()
                    .filter(qs -> qs.getKey().trim().equals(scriptFilterAction.getKey()))
                    .findFirst().ifPresent(qs -> qs.setValue(scriptFilterAction.getValue()));
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.postData.getValue())) {
            step.getPostDatas().stream()
                    .filter(pd -> pd.getKey().trim().equals(scriptFilterAction.getKey()))
                    .findFirst().ifPresent(pd -> pd.setValue(scriptFilterAction.getValue()));
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.validation.getValue())) {
            for (RequestData pd : step.getResponseData()) {
                if (pd.getKey().trim().equals(scriptFilterAction.getKey())) {
                    pd.setValue(scriptFilterAction.getValue());
                    pd.setType(RequestDataType.bodyValidation.name());
                    break;
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.assignment.getValue())) {
            for (RequestData pd : step.getResponseData()) {
                if (pd.getKey().trim().equals(scriptFilterAction.getKey())) {
                    pd.setValue(scriptFilterAction.getValue());
                    pd.setType(RequestDataType.bodyAssignment.name());
                    break;
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.onfail.getValue())) {
            step.setOnFail(scriptFilterAction.getValue());
        } else if (scriptFilterAction.getScope().equalsIgnoreCase("responseData")) {
            for (RequestData pd : step.getResponseData()) {
                if (pd.getKey().trim().equals(scriptFilterAction.getKey())) {
                    pd.setValue(scriptFilterAction.getValue());
                    pd.setType(getDataType(scriptFilterAction.getValue()));
                    break;
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.path.getValue())) {
            step.setSimplePath(scriptFilterAction.getValue());
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.host.getValue())) {
            step.setHostname(scriptFilterAction.getValue());
        } else {
            logger.warn("Replace action not available for scope " + scriptFilterAction.getScope());
        }
    }

    private static void doRemoveAction(List<ScriptStep> steps, SortedMap<Integer, ScriptStep> stepsToAdd,
            Set<ScriptStep> stepsToDelete, ScriptFilterAction scriptFilterAction, ScriptStep step) {

        if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.request.getValue())) {
            stepsToDelete.add(step);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.requestCookie.getValue())) {
            step.getRequestCookies().removeIf(requestData -> requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey()));
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.requestHeader.getValue())) {
            step.getRequestheaders().removeIf(requestData -> requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey()));
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.queryString.getValue())) {
            step.getQueryStrings().removeIf(requestData -> requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey()));
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.postData.getValue())) {
            step.getPostDatas().removeIf(requestData -> requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey()));
        } else {
            logger.warn("Remove Action not available for scope " + scriptFilterAction.getScope());
        }
    }

    private static void doAddAction(List<ScriptStep> steps, SortedMap<Integer, ScriptStep> stepsToAdd,
            ScriptFilterAction scriptFilterAction, ScriptStep step) {

        if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.responseData.getValue())) {
            RequestData rd = new RequestData();
            rd.setType(AddActionScope.responseData.getValue());
            rd.setKey(scriptFilterAction.getKey());
            rd.setValue(scriptFilterAction.getValue());
            rd.setType(getDataType(scriptFilterAction.getValue()));
            if (step.getResponseData() == null)
                step.setResponseData(new HashSet<RequestData>());

            step.getResponseData().add(rd);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.requestCookie.getValue())) {
            RequestData rd = new RequestData();
            rd.setType(AddActionScope.requestCookie.getValue());
            rd.setKey(scriptFilterAction.getKey());
            rd.setValue(scriptFilterAction.getValue());
            if (step.getRequestCookies() == null)
                step.setRequestCookies(new HashSet<RequestData>());

            step.getRequestCookies().add(rd);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.requestHeader.getValue())) {
            RequestData rd = new RequestData();
            rd.setType(AddActionScope.requestHeader.getValue());
            rd.setKey(scriptFilterAction.getKey());
            rd.setValue(scriptFilterAction.getValue());
            if (step.getRequestheaders() == null)
                step.setRequestheaders(new HashSet<RequestData>());

            step.getRequestheaders().add(rd);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.queryString.getValue())) {
            RequestData rd = new RequestData();
            rd.setType(AddActionScope.queryString.getValue());
            rd.setKey(scriptFilterAction.getKey());
            rd.setValue(scriptFilterAction.getValue());
            if (step.getQueryStrings() == null)
                step.setQueryStrings(new HashSet<RequestData>());

            step.getQueryStrings().add(rd);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.postData.getValue())) {
            RequestData rd = new RequestData();
            rd.setType(AddActionScope.postData.getValue());
            rd.setKey(scriptFilterAction.getKey());
            rd.setValue(scriptFilterAction.getValue());
            if (step.getPostDatas() == null)
                step.setPostDatas(new HashSet<RequestData>());

            step.getPostDatas().add(rd);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.validation.getValue())) {
            RequestData rd = new RequestData();
            rd.setType(RequestDataType.bodyValidation.name());
            rd.setKey(scriptFilterAction.getKey());
            rd.setValue(scriptFilterAction.getValue());
            if (step.getResponseData() == null) {
                step.setResponseData(new HashSet<RequestData>());
            }
            step.getResponseData().add(rd);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.assignment.getValue())) {
            RequestData rd = new RequestData();
            rd.setType(RequestDataType.bodyAssignment.name());
            rd.setKey(scriptFilterAction.getKey());
            rd.setValue(scriptFilterAction.getValue());
            if (step.getResponseData() == null)
                step.setResponseData(new HashSet<RequestData>());

            step.getResponseData().add(rd);
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.thinkTime.getValue())) {
            ScriptStep think = new ScriptStep();
            think.setType("thinkTime");
            String[] values = scriptFilterAction.getValue().split(",");

            RequestData min = new RequestData();
            min.setType("thinkTime");
            min.setKey("minTime");
            min.setValue(values[0]);

            RequestData max = new RequestData();
            max.setType("thinkTime");
            max.setKey("maxTime");
            max.setValue(values[1]);

            Set<RequestData> ds = new HashSet<RequestData>();
            ds.add(min);
            ds.add(max);
            think.setData(ds);
            think.setComments("ThinkTime " + min.getValue() + "-" + max.getValue());

            stepsToAdd.put(steps.indexOf(step) + 1, think);

        } else if (scriptFilterAction.getScope().equalsIgnoreCase(AddActionScope.sleepTime.getValue())) {
            ScriptStep sleep = new ScriptStep();
            sleep.setType("sleep");
            sleep.setComments("SLEEP " + scriptFilterAction.getValue());
            RequestData data = new RequestData();
            data.setType("sleep");
            data.setKey("time");
            data.setValue(scriptFilterAction.getValue());
            Set<RequestData> ds = new HashSet<RequestData>();
            ds.add(data);
            sleep.setData(ds);

            stepsToAdd.put(steps.indexOf(step) + 1, sleep);
        } else {
            logger.warn("Add Action not available for scope " + scriptFilterAction.getScope());
        }

    }

    /**
     * @param value
     * @return
     */
    private static String getDataType(String value) {
        return (!StringUtils.isEmpty(value) && value.startsWith("=") && !value.startsWith("=="))
                ? RequestDataType.bodyAssignment.name()
                : RequestDataType.bodyValidation.name();
    }

    private static boolean testConditions(String scope, ScriptFilterCondition condition, ScriptStep step,
            List<ScriptStep> steps) {
        if (scope.equalsIgnoreCase("Hostname")) {
            return filterOnField(step.getHostname(), condition, step);
        } else if (scope.equalsIgnoreCase("Path")) {
            return filterOnField(step.getSimplePath(), condition, step);
        } else if (scope.equalsIgnoreCase("Query String")) {
            return filterOnFieldSets(step.getQueryStrings(), condition, step, steps);
        } else if (scope.equalsIgnoreCase("Post Data")) {
            return filterOnFieldSets(step.getPostDatas(), condition, step, steps);
        }
        throw new IllegalArgumentException("Unknown scope: " + scope);
    }

    private static boolean filterOnField(String filterField, ScriptFilterCondition condition,
            ScriptStep currentStep) {
        if (filterField == null)
            return false;
        if (condition.getCondition().equalsIgnoreCase("Contains")) {
            return filterField.contains(condition.getValue());
        } else if (condition.getCondition().equalsIgnoreCase("Matches")) {
            return filterField.matches(condition.getValue());
        } else if (condition.getCondition().equalsIgnoreCase("Does not contain")) {
            return !filterField.contains(condition.getValue());
        } else if (condition.getCondition().equalsIgnoreCase("Starts with")) {
            return filterField.startsWith(condition.getValue());
        } else if (condition.getCondition().equalsIgnoreCase("Exist")) {
            return !filterField.isEmpty();
        } else if (condition.getCondition().equalsIgnoreCase("Does not exist")) {
            return filterField.isEmpty();
            // return doAction(condition.getActions(), currentStep);
        }
        return false;
    }

    public static boolean filterOnFieldSets(Set<RequestData> fields, ScriptFilterCondition condition,
            ScriptStep currentStep, List<ScriptStep> steps) {

        if (StringUtils.isNotBlank(currentStep.getPayload())) {
            if (matchContition(condition, currentStep.getPayload())) {
                return true;
            }
        }
        if (fields == null) {
            return false;
        }
        if (fields.stream().map(field -> field.getKey() + "=" + field.getValue())
                .anyMatch(filterField -> matchContition(condition, filterField))) {
            return true;
        }

        return (condition.getCondition().equals("Does not contain")) ||
                (condition.getCondition().equals("Does not exist"));
    }

    private static boolean matchContition(ScriptFilterCondition condition, String filterField) {
        switch (condition.getCondition()) {
            case "Contains":
                if (filterField.contains(condition.getValue())) {
                    return true;
                }
                break;
            case "Matches":
                if (filterField.matches(condition.getValue())) {
                    return true;
                }
                break;
            case "Does not contain":
                if (filterField.contains(condition.getValue())) {
                    return false;
                }
                break;
            case "Starts with":
                if (filterField.startsWith(condition.getValue())) {
                    return true;
                }
                break;
            case "Exist":
            case "Does not exist":
                if (StringUtils.isNotBlank(filterField)) {
                    return true;
                }
                break;
        }
        return false;
    }
}
