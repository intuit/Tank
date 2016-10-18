package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.model.v1.script.ScriptStepTO;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.api.script.util.ScriptServiceUtil;
import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.RequestDataType;
import com.intuit.tank.tools.script.LoggingOutputLogger;
import com.intuit.tank.tools.script.ScriptRunner;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import com.intuit.tank.vm.script.util.AddActionScope;
import com.intuit.tank.vm.script.util.RemoveActionScope;
import com.intuit.tank.vm.script.util.ReplaceActionScope;

public class ScriptFilterUtil {

    private static Logger logger = LogManager.getLogger(ScriptFilterUtil.class);

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
     * @param steps
     */
    public static void applyFilters(List<Integer> filterIds, Script script) {
        ScriptFilterDao dao = new ScriptFilterDao();
        applyFiltersToScript(dao.findForIds(filterIds), script);
    }

    /**
     * Applys the filters specified by filterIds
     * 
     * @param filterIds
     * @param steps
     */
    public static void applyFiltersToScript(Collection<ScriptFilter> filters, Script script) {
        ExternalScriptDao externalScriptDao = new ExternalScriptDao();
        List<ScriptFilter> internalFilters = new ArrayList<ScriptFilter>();
        List<ScriptFilter> externalFilters = new ArrayList<ScriptFilter>();

        for (ScriptFilter tempFilter : filters) {
            if (tempFilter.getFilterType() == ScriptFilterType.EXTERNAL) {
                externalFilters.add(tempFilter);
            } else {
                internalFilters.add(tempFilter);
            }
        }

        for (ScriptFilter filter : internalFilters) {
            applyFilter(filter, script.getScriptSteps());
        }
        if (!externalFilters.isEmpty()) {
            LoggingOutputLogger outputLogger = new LoggingOutputLogger();
            ScriptTO scriptTo = ScriptServiceUtil.scriptToTransferObject(script);
            ScriptRunner runner = new ScriptRunner();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("script", scriptTo);
            for (ScriptFilter filter : externalFilters) {
                ExternalScript externalScript = externalScriptDao.findById(filter.getExternalScriptId());
                logger.info("Running external Script: " + externalScript);
                if (script != null) {
                    try {
                        runner.runScript(externalScript.getName(), externalScript.getScript(),
                                externalScript.getEngine(),
                                map,
                                outputLogger);
                    } catch (ScriptException e) {
                        logger.error("Error Running Script: " + e);
                        throw new RuntimeException(e);
                    }
                }
            }
            script.getScriptSteps().clear();
            for (ScriptStepTO stepTo : scriptTo.getSteps()) {
                script.getScriptSteps().add(ScriptServiceUtil.transferObjectToScriptStep(stepTo));
            }
        }
    }

    /**
     * 
     * @param filter
     * @param steps
     */
    public static void applyFilter(ScriptFilter filter, List<ScriptStep> steps) {
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
        Iterator<Integer> itr = stepsToAdd.keySet().iterator();
        while (itr.hasNext()) {
            int index = itr.next();
            steps.add(index + reqCount, stepsToAdd.get(index));
            reqCount++;
        }

        for (ScriptStep delete : stepsToDelete) {
            steps.remove(delete);
        }
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
            for (RequestData qs : step.getRequestheaders()) {
                if (qs.getKey().trim().equals(scriptFilterAction.getKey())) {
                    qs.setValue(scriptFilterAction.getValue());
                    break;
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.requestCookie.getValue())) {
            for (RequestData qs : step.getRequestCookies()) {
                if (qs.getKey().trim().equals(scriptFilterAction.getKey())) {
                    qs.setValue(scriptFilterAction.getValue());
                    break;
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.queryString.getValue())) {
            for (RequestData qs : step.getQueryStrings()) {
                if (qs.getKey().trim().equals(scriptFilterAction.getKey())) {
                    qs.setValue(scriptFilterAction.getValue());
                    break;
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(ReplaceActionScope.postData.getValue())) {
            for (RequestData pd : step.getPostDatas()) {
                if (pd.getKey().trim().equals(scriptFilterAction.getKey())) {
                    pd.setValue(scriptFilterAction.getValue());
                    break;
                }
            }
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
            Set<RequestData> requestCookies = step.getRequestCookies();
            for (Iterator<RequestData> iterator = requestCookies.iterator(); iterator.hasNext();) {
                RequestData requestData = iterator.next();
                if (requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey())) {
                    iterator.remove();
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.requestHeader.getValue())) {
            Set<RequestData> requestHeaders = step.getRequestheaders();
            for (Iterator<RequestData> iterator = requestHeaders.iterator(); iterator.hasNext();) {
                RequestData requestData = iterator.next();
                if (requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey())) {
                    iterator.remove();
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.queryString.getValue())) {
            Set<RequestData> queryStrings = step.getQueryStrings();
            for (Iterator<RequestData> iterator = queryStrings.iterator(); iterator.hasNext();) {
                RequestData requestData = iterator.next();
                if (requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey())) {
                    iterator.remove();
                }
            }
        } else if (scriptFilterAction.getScope().equalsIgnoreCase(RemoveActionScope.postData.getValue())) {
            Set<RequestData> postData = step.getPostDatas();
            for (Iterator<RequestData> iterator = postData.iterator(); iterator.hasNext();) {
                RequestData requestData = iterator.next();
                if (requestData.getKey().equalsIgnoreCase(scriptFilterAction.getKey())) {
                    iterator.remove();
                }
            }
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
        String ret = RequestDataType.bodyValidation.name();
        if (!StringUtils.isEmpty(value) && value.startsWith("=") && !value.startsWith("==")) {
            ret = RequestDataType.bodyAssignment.name();
        }
        return ret;
    }

    static boolean testConditions(String scope, ScriptFilterCondition condition, ScriptStep step,
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

    public static boolean filterOnField(String filterField, ScriptFilterCondition condition,
            ScriptStep currentStep) {
        if (filterField == null)
            return false;
        if (condition.getCondition().equalsIgnoreCase("Contains")) {
            if (filterField.contains(condition.getValue()))
                return true;
        } else if (condition.getCondition().equalsIgnoreCase("Matches")) {
            if (filterField.matches(condition.getValue()))
                return true;
        } else if (condition.getCondition().equalsIgnoreCase("Does not contain")) {
            if (!filterField.contains(condition.getValue()))
                return true;
        } else if (condition.getCondition().equalsIgnoreCase("Starts with")) {
            if (filterField.startsWith(condition.getValue()))
                return true;
        } else if (condition.getCondition().equalsIgnoreCase("Exist")) {
            if (!filterField.equals(""))
                return true;
        } else if (condition.getCondition().equalsIgnoreCase("Does not exist")) {
            if (filterField.equals(""))
                return true;
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
        for (RequestData field : fields) {
            String filterField = field.getKey() + "=" + field.getValue();
            if (matchContition(condition, filterField)) {
                return true;
            }
        }

        if ((condition.getCondition().equals("Does not contain")) ||
                (condition.getCondition().equals("Does not exist"))) {
            return true;
        }

        return false;
    }

    private static boolean matchContition(ScriptFilterCondition condition, String filterField) {
        if (condition.getCondition().equals("Contains")) {
            if (filterField.contains(condition.getValue())) {
                return true;
            }
        } else if (condition.getCondition().equals("Matches")) {
            if (filterField.matches(condition.getValue())) {
                return true;
            }
        } else if (condition.getCondition().equals("Does not contain")) {
            if (filterField.contains(condition.getValue())) {
                return false;
            }
        } else if (condition.getCondition().equals("Starts with")) {
            if (filterField.startsWith(condition.getValue())) {
                return true;
            }
        } else if (condition.getCondition().equals("Exist") || condition.getCondition().equals("Does not exist")) {
            if (StringUtils.isNotBlank(filterField)) {
                return true;
            }
        }
        return false;
    }

    // actions : replace, remove, add
    // scope : request, host, path, query string, post data, response data
    //
    //
    public static ScriptStep doAction(Set<ScriptFilterAction> actions, ScriptStep step) {

        for (ScriptFilterAction action : actions) {
            if (action.getAction() == ScriptFilterActionType.remove) {
                if (action.getScope().equalsIgnoreCase("request")) {
                    return step;
                } else {
                    logger.warn("Remove Action not available for scope " + action.getScope());
                }
            } else if (action.getAction() == ScriptFilterActionType.replace) {
                doReplaceAction(action, step);
            } else if (action.getAction() == ScriptFilterActionType.add) {
                ScriptStep addStep = doAddAction(action, step);
                if (addStep != null)
                    return addStep;
            }

        }
        return null;
    }

    private static ScriptStep doAddAction(ScriptFilterAction action, ScriptStep step) {

        if (action.getScope().equalsIgnoreCase("responseData")) {
            RequestData rd = new RequestData();
            rd.setType(RequestDataType.responseContent.name());
            rd.setKey(action.getKey());
            rd.setValue(action.getValue());
            getDataType(action.getValue());
            if (step.getResponseData() == null)
                step.setResponseData(new HashSet<RequestData>());

            step.getResponseData().add(rd);
        } else if (action.getScope().equalsIgnoreCase("thinkTime")) {
            ScriptStep think = new ScriptStep();
            think.setType("thinkTime");
            String[] values = action.getValue().split(",");

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
            return think;
        } else if (action.getScope().equalsIgnoreCase("sleepTime")) {
            ScriptStep sleep = new ScriptStep();
            sleep.setType("sleep");
            sleep.setComments("SLEEP " + action.getValue());
            RequestData data = new RequestData();
            data.setType("sleep");
            data.setKey("time");
            data.setValue(action.getValue());
            Set<RequestData> ds = new HashSet<RequestData>();
            ds.add(data);
            sleep.setData(ds);

            return sleep;
        } else if (action.getScope().equalsIgnoreCase("responseCookie")) {
            RequestData rd = new RequestData();
            rd.setType("responseCookie");
            rd.setKey(action.getKey());
            rd.setValue(action.getValue());
            if (step.getResponseCookies() == null)
                step.setResponseCookies(new HashSet<RequestData>());

            step.getResponseCookies().add(rd);
        } else if (action.getScope().equalsIgnoreCase("requestCookie")) {
            RequestData rd = new RequestData();
            rd.setType(RequestDataType.requestCookie.name());
            rd.setKey(action.getKey());
            rd.setValue(action.getValue());
            if (step.getRequestCookies() == null)
                step.setRequestCookies(new HashSet<RequestData>());

            step.getRequestCookies().add(rd);
        } else {
            logger.warn("Add Action not available for scope " + action.getScope());
        }
        return null;
    }

    private static void doReplaceAction(ScriptFilterAction action, ScriptStep step) {

        if (action.getScope().equalsIgnoreCase(ReplaceActionScope.queryString.getValue())) {
            for (RequestData qs : step.getQueryStrings()) {
                if (qs.getKey().trim().equals(action.getKey())) {
                    qs.setValue(action.getValue());
                    qs.setType(RequestDataType.queryString.name());
                    break;
                }
            }
        } else if (action.getScope().equalsIgnoreCase(ReplaceActionScope.postData.getValue())) {
            for (RequestData pd : step.getPostDatas()) {
                if (pd.getKey().trim().equals(action.getKey())) {
                    pd.setValue(action.getValue());
                    pd.setType(RequestDataType.requestPostData.name());
                    break;
                }
            }
        } else if (action.getScope().equalsIgnoreCase(ReplaceActionScope.requestCookie.getValue())) {
            for (RequestData pd : step.getRequestCookies()) {
                if (pd.getKey().trim().equals(action.getKey())) {
                    pd.setValue(action.getValue());
                    pd.setType(RequestDataType.requestCookie.name());
                    break;
                }
            }
        } else if (action.getScope().equalsIgnoreCase(ReplaceActionScope.validation.getValue())) {
            for (RequestData pd : step.getResponseData()) {
                if (pd.getKey().trim().equals(action.getKey())) {
                    pd.setValue(action.getValue());
                    pd.setType(RequestDataType.bodyValidation.name());
                    break;
                }
            }
        } else if (action.getScope().equalsIgnoreCase(ReplaceActionScope.assignment.getValue())) {
            for (RequestData pd : step.getResponseData()) {
                if (pd.getKey().trim().equals(action.getKey())) {
                    pd.setValue(action.getValue());
                    pd.setType(RequestDataType.bodyAssignment.name());
                    break;
                }
            }
        } else if (action.getScope().equalsIgnoreCase(ReplaceActionScope.onfail.getValue())) {
            step.setOnFail(action.getValue());
        } else if (action.getScope().equalsIgnoreCase("responseData")) {
            for (RequestData pd : step.getResponseData()) {
                if (pd.getKey().trim().equals(action.getKey())) {
                    pd.setValue(action.getValue());
                    if (ConverterUtil.isAssignment(pd)) {
                        pd.setType(RequestDataType.bodyAssignment.name());
                    }
                    getDataType(action.getValue());
                    break;
                }
            }
        } else if (action.getScope().equalsIgnoreCase(ReplaceActionScope.path.getValue())) {
            step.setSimplePath(action.getValue());
        } else if (action.getScope().equalsIgnoreCase(ReplaceActionScope.host.getValue())) {
            step.setHostname(action.getValue());
        } else {
            logger.warn("Replace Action not available for scope " + action.getScope());
        }
    }

}
