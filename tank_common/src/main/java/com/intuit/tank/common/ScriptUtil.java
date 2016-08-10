package com.intuit.tank.common;

/*
 * #%L
 * Common
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ValidationUtil;
import com.intuit.tank.vm.settings.TimeUtil;
import com.intuit.tank.vm.settings.TankConfig;

public class ScriptUtil {
    private static final Logger LOG = LogManager.getLogger(ScriptUtil.class);
    public static final String TOTAL_TIME_KEY = "_totalTime";
    public static final String START_TIME_KEY = "_startTime";
    private static final Pattern p = Pattern.compile(TankConstants.EXPRESSION_REGEX);
    private static final Pattern csvPattern = Pattern.compile(TankConstants.CSV_EXPRESSION_REGEX);

    public static long getRunTime(List<ScriptStep> steps, Map<String, String> variables) {
        long runTime = 0;
        TankConfig tankConfig = new TankConfig();
        for (ScriptStep step : steps) {
            runTime += calculateStepDuration(step, variables, tankConfig);
        }
        return runTime;
    }

    public static Set<String> getUsedVariables(ScriptStep step) {
        Set<String> ret = new HashSet<String>();
        ret.addAll(extractVariablesFromRequestData(step.getData()));
        ret.addAll(extractVariablesFromRequestData(step.getQueryStrings()));
        ret.addAll(extractVariablesFromRequestData(step.getPostDatas()));
        ret.addAll(extractVariablesFromRequestData(step.getRequestheaders()));
        ret.addAll(extractVariablesFromRequestData(step.getRequestCookies()));
        extractVariables(ret, step.getPayload());
        extractVariables(ret, step.getHostname());
        extractVariables(ret, step.getSimplePath());
        return ret;
    }

    public static Map<String, String> getDeclaredVariables(ScriptStep step) {
        Map<String, String> ret = new HashMap<String, String>();
        if (step.getType().equals("variable")) {
            addKeys(ret, step.getData(), null);
        }
        return ret;
    }

    public static List<ScriptAssignment> getAssignments(ScriptStep step) {
        List<ScriptAssignment> ret = new ArrayList<ScriptAssignment>();
        if (step.getType().equals("request")) {
            for (RequestData rd : step.getResponseData()) {
                if (StringUtils.isNotBlank(rd.getKey())) {
                    if (StringUtils.containsIgnoreCase(rd.getType(), "assignment")) {
                        ret.add(new ScriptAssignment(rd.getKey().trim(), StringUtils.removeStart(
                                StringUtils.trim(rd.getValue()), "="), step.getStepIndex()));
                    }
                }
            }
        }
        return ret;
    }

    private static void addKeys(Map<String, String> ret, Set<RequestData> rds, String type) {
        for (RequestData rd : rds) {
            if (StringUtils.isNotBlank(rd.getKey())) {
                if (type == null || StringUtils.containsIgnoreCase(rd.getType(), type)) {
                    ret.put(rd.getKey().trim(), StringUtils.trim(rd.getValue()));
                }
            }
        }

    }

    private static Set<String> extractVariablesFromRequestData(Set<RequestData> rds) {
        Set<String> ret = new HashSet<String>();
        for (RequestData rd : rds) {
            extractVariables(ret, rd.getKey());
            extractVariables(ret, rd.getValue());
        }
        return ret;
    }

    private static void extractVariables(Set<String> ret, String s) {
        if (StringUtils.isNotBlank(s)) {
            if (ValidationUtil.isVariable(s)) {
                ret.add(ValidationUtil.removeVariableIdentifier(s));
            } else {
                Matcher m = p.matcher(s);
                while (m.find()) { // find next match
                    String group = m.group(1).trim();
                    getVar(ret, group);
                }
            }
        }
    }

    private static void getVar(Set<String> ret, String s) {
        Set<String> toTest = new HashSet<String>();
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == ',' || c == ')') {
                toTest.add(sb.toString());
                sb = new StringBuilder();
            } else if (c == '(') {
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        toTest.add(sb.toString());
        for (String group : toTest) {
            group = group.trim();
            if (StringUtils.isNotBlank(group) && StringUtils.containsNone(group, " ()'\",")) {
                if (!NumberUtils.isNumber(group)) {
                    // its a variable
                    ret.add(group);
                }
            }
        }
    }

    public static Script copyScript(String creator, String name, Script original) {
        Script ret = new Script();
        ret.setComments(original.getComments());
        ret.setCreator(creator);
        ret.setName(name);
        ret.setProductName(original.getProductName());
        ret.setRuntime(original.getRuntime());
        for (ScriptStep step : original.getScriptSteps()) {
            ret.addStep(step);
        }
        return ret;
    }

    /**
     * @param script
     */
    public static void setScriptStepLabels(Script script) {
        int i = 1;
        boolean copy = script.getId() == 0;
        if (script.getScriptSteps() != null) {
            for (ScriptStep step : script.getScriptSteps()) {
                step.setStepIndex(i++);
                if (copy || step.getUuid() == null) {
                    step.setUuid(UUID.randomUUID().toString());
                }
                updateStepLabel(step);
            }
        }
    }

    public static void updateStepLabel(ScriptStep step) {
        step.setLabel(getStepLabel(step));
        if (ScriptConstants.THINK_TIME.equals(step.getType())) {
            step.setComments(getThinkTimeComment(step));
        } else if (ScriptConstants.SLEEP.equals(step.getType())) {
            step.setComments(getSleepTimeComment(step));
        }
    }

    /**
     * @param step
     * @return
     */
    private static String getThinkTimeComment(ScriptStep step) {
        String min = null;
        String max = null;
        for (RequestData rd : step.getData()) {
            if (ScriptConstants.MIN_TIME.equals(rd.getKey())) {
                min = rd.getValue();
            } else if (ScriptConstants.MAX_TIME.equals(rd.getKey())) {
                max = rd.getValue();
            }
        }

        return "ThinkTime " + min + "-" + max;
    }

    /**
     * @param step
     */
    private static String getSleepTimeComment(ScriptStep step) {
        String delay = null;
        for (RequestData rd : step.getData()) {
            if (ScriptConstants.TIME.equals(rd.getKey())) {
                delay = rd.getValue();
            }
        }
        return "SLEEP " + delay;
    }

    /**
     * @param step
     * @return
     */
    private static String getStepLabel(ScriptStep step) {
        StringBuilder label = new StringBuilder();
        if (step.getType().equalsIgnoreCase(ScriptConstants.REQUEST)) {
            label.append(step.getProtocol()).append("://").append(step.getHostname()).append(step.getSimplePath());

            int qsCount = 0;
            if (step.getQueryStrings() != null) {
                for (RequestData qs : step.getQueryStrings()) {
                    label.append(qsCount == 0 ? "?" : "&");
                    label.append(qs.getKey()).append("=").append(qs.getValue());
                    qsCount++;
                }
            }
        } else if (step.getType().equalsIgnoreCase(ScriptConstants.VARIABLE)) {
            Set<RequestData> setData = step.getData();
            if (null != setData) {
                Iterator<RequestData> iter = setData.iterator();
                while (iter.hasNext()) {
                    RequestData d = iter.next();
                    label.append("Variable definition " + d.getKey() + "=>" + d.getValue());
                }
            }
        }  else if (step.getType().equalsIgnoreCase(ScriptConstants.AUTHENTICATION)) {
            Set<RequestData> setData = step.getData();
            if (null != setData) {
                String scheme = "ALL";
                String host = "";
                String user = "";
                Iterator<RequestData> iter = setData.iterator();
                while (iter.hasNext()) {
                    RequestData d = iter.next();
                    if (d.getKey().equals(ScriptConstants.AUTH_SCHEME)) {
                        scheme = d.getValue();
                    } else if (d.getKey().equals(ScriptConstants.AUTH_HOST)) {
                        host = d.getValue();
                    } else if (d.getKey().equals(ScriptConstants.AUTH_USER_NAME)) {
                        user = d.getValue();
                    }
                }
                label.append("Authentication " + scheme + " [host: " + host + " user: " + user + "]");
            }
        } else if (step.getType().equalsIgnoreCase(ScriptConstants.THINK_TIME)) {

            String min = "0";
            String max = "0";
            Set<RequestData> setData = step.getData();
            if (null != setData) {
                Iterator<RequestData> iter = setData.iterator();
                while (iter.hasNext()) {
                    RequestData d = iter.next();
                    if (d.getKey().contains("min"))
                        min = d.getValue();
                    else if (d.getKey().contains("max"))
                        max = d.getValue();
                }
            }
            label.append("Think time " + min + "-" + max);
        } else if (step.getType().equalsIgnoreCase(ScriptConstants.LOGIC)) {
            label.append("Logic Step: " + step.getName());
        } else if (step.getType().equalsIgnoreCase(ScriptConstants.COOKIE)) {
            String name = "";
            String value = "";
            for (RequestData requestData : step.getData()) {
                if (ScriptConstants.COOKIE_NAME.equals(requestData.getKey())) {
                    name = requestData.getValue();
                }
                if (ScriptConstants.COOKIE_VALUE.equals(requestData.getKey())) {
                    value = requestData.getValue();
                }
                // if (ScriptConstants.COOKIE_DOMAIN.equals(requestData.getKey())) {
                // domain = requestData.getValue();
                // }
            }
            label.append("Set Cookie: ").append(name).append(" = ").append(value).toString();
        } else if (step.getType().equalsIgnoreCase(ScriptConstants.SLEEP)) {

            Set<RequestData> setData = step.getData();
            if (null != setData) {
                Iterator<RequestData> iter = setData.iterator();
                while (iter.hasNext()) {
                    RequestData d = iter.next();
                    label.append("Sleep for " + d.getValue());
                }
            }
        } else if (step.getType().equalsIgnoreCase(ScriptConstants.CLEAR)) {
            label.append("Clear session");
        } else if (step.getType().equalsIgnoreCase(ScriptConstants.TIMER)) {
            String name = null;
            String timerAction = null;
            for (RequestData rd : step.getData()) {
                if (rd.getKey().equalsIgnoreCase(ScriptConstants.IS_START)) {
                    timerAction = rd.getValue();
                }
                if (rd.getKey().equalsIgnoreCase(ScriptConstants.LOGGING_KEY)) {
                    name = rd.getValue();
                }
            }

            label.append(name).append(":").append(timerAction);

        }
        return StringUtils.abbreviate(label.toString(), 1024);
    }

    /**
     * @param count
     * @param step
     * @return
     */

    /*
     * public static ScriptStep requestToStep(int count, ScriptStep step) { ScriptStep step = new ScriptStep();
     * 
     * step.setStepIndex(count); step.setHostname(step.getHostname()); step.setSimplePath(step.getSimplePath());
     * step.setMethod(step.getMethod()); step.setMimetype(step.getMimetype()); step.setProtocol(step.getProtocol());
     * step.setType(step.getType()); step.setComments(step.getComments()); step.setName(step.getName());
     * step.setResponse(step.getResponse()); step.setLoggingKey(step.getLoggingKey());
     * step.setReqFormat(step.getReqFormat()); // step.setScriptGroup(e.getScriptGroup());
     * step.setOnFail(step.getOnFail());
     * 
     * step.setData(getRequestData(step, step.getData())); step.setRequestheaders(getRequestData(step,
     * step.getRequestheaders())); step.setResponseheaders(getRequestData(step, step.getResponseheaders()));
     * step.setQueryStrings(getRequestData(step, step.getQueryStrings())); step.setPostDatas(getRequestData(step,
     * step.getPostDatas())); step.setResponseData(getRequestData(step, step.getResponseData()));
     * step.setRequestCookies(getRequestData(step, step.getRequestCookies()));
     * step.setResponseCookies(getRequestData(step, step.getResponseCookies())); return step; }
     */

    private static Set<RequestData> copyRequestData(Set<RequestData> setData) {
        Set<RequestData> rd = null;
        if (null != setData) {
            rd = new HashSet<RequestData>();
            for (RequestData d : setData) {
                RequestData newData = new RequestData();
                newData.setKey(d.getKey());
                newData.setType(d.getType());
                newData.setValue(d.getValue());
                rd.add(newData);
            }
        }
        return rd;
    }

    public static ScriptStep copyScriptStep(ScriptStep step) {
        ScriptStep ret = new ScriptStep();
        ret.setComments(step.getComments());
        ret.setData(copyRequestData(step.getData()));
        ret.setHostname(step.getHostname());
        ret.setMethod(step.getMethod());
        ret.setMimetype(step.getMimetype());
        ret.setName(step.getName());
        ret.setOnFail(step.getOnFail());
        ret.setPayload(step.getPayload());
        ret.setPostDatas(copyRequestData(step.getPostDatas()));
        ret.setProtocol(step.getProtocol());
        ret.setQueryStrings(copyRequestData(step.getQueryStrings()));
        ret.setReqFormat(step.getReqFormat());
        ret.setRequestCookies(copyRequestData(step.getRequestCookies()));
        ret.setRequestheaders(copyRequestData(step.getRequestheaders()));
        ret.setRespFormat(step.getRespFormat());
        ret.setResponse(step.getResponse());
        ret.setResponseCookies(step.getResponseCookies());
        ret.setResponseData(copyRequestData(step.getResponseData()));
        ret.setResponseheaders(copyRequestData(step.getResponseheaders()));
        ret.setResult(step.getResult());
        ret.setSimplePath(step.getSimplePath());
        ret.setType(step.getType());
        ret.setUrl(step.getUrl());
        ret.setUuid(UUID.randomUUID().toString());
        ret.setLabel(getStepLabel(ret));
        return ret;
    }

    public static long calculateStepDuration(ScriptStep step, Map<String, String> variables, TankConfig config) {
        long result = 0;
        try {
            if (step.getType().equalsIgnoreCase("request")) {
                result = config.getAgentConfig().getRange(step.getMethod()).getRandomValueWithin();
            } else if (step.getType().equalsIgnoreCase("variable")) {
                Set<RequestData> data = step.getData();
                for (RequestData requestData : data) {
                    variables.put(requestData.getKey(), requestData.getValue());
                }
            } else if (step.getType().equalsIgnoreCase("thinkTime")) {
                String min = "0";
                String max = "0";
                Set<RequestData> setData = step.getData();
                if (null != setData) {
                    Iterator<RequestData> iter = setData.iterator();
                    while (iter.hasNext()) {
                        RequestData d = iter.next();
                        if (d.getKey().contains("min")) {
                            min = d.getValue();
                        } else if (d.getKey().contains("max")) {
                            max = d.getValue();
                        }
                    }
                }
                if (ValidationUtil.isAnyVariable(min)) {
                    String s = (String) variables.get(ValidationUtil.removeAllVariableIdentifier(min));
                    min = s != null ? s : min;
                }
                if (ValidationUtil.isAnyVariable(max)) {
                    String s = (String) variables.get(ValidationUtil.removeAllVariableIdentifier(max));
                    max = s != null ? s : max;
                }
                result = ((TimeUtil.parseTimeString(max) + TimeUtil.parseTimeString(min)) / 2);
            } else if (step.getType().equalsIgnoreCase("sleep")) {
                Set<RequestData> setData = step.getData();
                if (null != setData) {
                    Iterator<RequestData> iter = setData.iterator();
                    while (iter.hasNext()) {
                        RequestData d = iter.next();
                        if (d.getKey().equalsIgnoreCase("time")) {
                            String time = d.getValue();
                            if (ValidationUtil.isAnyVariable(time)) {
                                String s = (String) variables.get(ValidationUtil.removeAllVariableIdentifier(time));
                                time = s != null ? s : time;
                            }
                            result = TimeUtil.parseTimeString(time);
                            break;
                        }
                    }
                }
            } else {
                result = config.getAgentConfig().getRange("process").getRandomValueWithin();
            }
        } catch (Exception e) {
            LOG.error("Error calculating step time: " + e);
        }
        return result;
    }

    public static String getDataFileUse(ScriptStep step) {
        String ret = null;
        if (step.getType().equalsIgnoreCase("variable")) {
            String value = step.getData().iterator().next().getValue();
            Matcher m = csvPattern.matcher(value);
            while (m.find()) { // find next match
                String args = m.group(1).trim();
                String[] split = args.split(",");
                ret = TankConstants.DEFAULT_CSV_FILE_NAME;
                if (split.length > 0) {
                    String test = split[0].trim();
                    if (StringUtils.isNotBlank(test) && !NumberUtils.isDigits(test)) {
                        ret = test;
                    }
                }
            }
        }
        return ret;
    }
}
