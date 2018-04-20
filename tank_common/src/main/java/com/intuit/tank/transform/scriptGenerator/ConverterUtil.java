package com.intuit.tank.transform.scriptGenerator;

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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.data.AssignmentData;
import com.intuit.tank.harness.data.AuthenticationStep;
import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.CookieStep;
import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.HDResponse;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.data.Header;
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.ResponseData;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.harness.data.TimerStep;
import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.project.BaseJob;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.script.RequestDataType;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.TimerAction;
import com.intuit.tank.vm.api.enumerated.ValidationType;
import com.intuit.tank.vm.settings.TankConfig;

public class ConverterUtil {
    private static final Logger LOG = LogManager.getLogger(ConverterUtil.class);

    public static HDWorkload convertScriptToHdWorkload(Script script) {

        String name = "TestPlan for " + script.getName();
        name += " (id_" + script.getId() + ")";
        HDWorkload hdWorkload = new HDWorkload();
        hdWorkload.setTankHttpClientClass(new TankConfig().getAgentConfig().getTankClientClassDefault());

        hdWorkload.setDescription(name);
        hdWorkload.setName(name);

        HDTestPlan tp = new HDTestPlan();
        hdWorkload.getPlans().add(tp);
        tp.setTestPlanName("Main");
        tp.setUserPercentage(100);

        HDScriptGroup hdScriptGroup = new HDScriptGroup();
        tp.getGroup().add(hdScriptGroup);
        hdScriptGroup.setLoop(1);
        hdScriptGroup.setName("Group-1");
        hdScriptGroup.setDescription("autoGen");

        HDScript testGroup = new HDScript();
        hdScriptGroup.getGroupSteps().add(testGroup);
        testGroup.setName(script.getName());
        testGroup.setLoop(1);
        testGroup.getUseCase().addAll(convertScript(script.getScriptSteps(), new StepCounter()));

        return hdWorkload;

    }

    public static HDWorkload convertWorkload(Workload workload, BaseJob job) {

        String name = "TestPlan for " + workload.getProject() != null ? " project " + workload.getProject().getName()
                : " workload " + workload.getName();
        int id = workload.getProject() != null ? workload.getProject().getId() : workload.getId();
        name += " (id" + id + ")";

        HDWorkload hdWorkload = new HDWorkload();
        hdWorkload.setTankHttpClientClass(job.getTankClientClass() != null ? job.getTankClientClass() : new TankConfig().getAgentConfig().getTankClientClassDefault());

        hdWorkload.setDescription(name);
        hdWorkload.setName(name);
        hdWorkload.setVariables(new HDTestVariables(job.isAllowOverride(), job.getVariables()));
        for (TestPlan plan : workload.getTestPlans()) {
            HDTestPlan tp = new HDTestPlan();
            tp.setTestPlanName(plan.getName());
            tp.setUserPercentage(plan.getUserPercentage());
            tp.getGroup().addAll(convertScriptGroups(plan.getScriptGroups(), new StepCounter()));
            hdWorkload.getPlans().add(tp);
        }
        return hdWorkload;

    }

    public static String getWorkloadXML(HDWorkload hdWorkload) {
        StringWriter sw;
        try {
            JAXBContext context = JAXBContext.newInstance(HDWorkload.class.getPackage().getName());
            Marshaller createMarshaller = context.createMarshaller();
            createMarshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            sw = new StringWriter();
            createMarshaller.marshal(hdWorkload, sw);
            sw.flush();
            sw.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return sw.toString();

    }

    private static List<HDScriptGroup> convertScriptGroups(List<ScriptGroup> scriptGroups, StepCounter sc) {
        return scriptGroups.stream().map(scriptGroup -> convertScriptGroup(scriptGroup, sc)).collect(Collectors.toList());
    }

    private static HDScriptGroup convertScriptGroup(ScriptGroup scriptGroup, StepCounter sc) {
        HDScriptGroup hdScriptGroup = new HDScriptGroup();
        hdScriptGroup.setName(scriptGroup.getName());
        hdScriptGroup.setDescription(scriptGroup.getName());
        hdScriptGroup.setLoop(scriptGroup.getLoop());
        hdScriptGroup.getGroupSteps().addAll(convertScriptGroupSteps(scriptGroup.getScriptGroupSteps(), sc));
        return hdScriptGroup;
    }

    private static List<HDScript> convertScriptGroupSteps(List<ScriptGroupStep> sgs, StepCounter sc) {
        return sgs.stream().map(scriptGroupStep -> convertScriptGroupStep(scriptGroupStep, sc)).collect(Collectors.toList());
    }

    private static HDScript convertScriptGroupStep(ScriptGroupStep sgs, StepCounter sc) {
        HDScript testGroup = new HDScript();
        testGroup.setName(sgs.getScript().getName());
        testGroup.setLoop(sgs.getLoop());

        Script script = sgs.getScript();
        List<ScriptStep> steps = script.getScriptSteps();
        testGroup.getUseCase().addAll(convertScript(steps, sc));
        return testGroup;
    }

    private static List<HDScriptUseCase> convertScript(List<ScriptStep> scriptSteps, StepCounter sc) {
        List<HDScriptUseCase> useCaseList = new ArrayList<HDScriptUseCase>();
        String currentGroup = null;
        HDScriptUseCase useCase = null;
        for (ScriptStep scriptStep : scriptSteps) {

            String group = scriptStep.getScriptGroupName();
            if (group == null || group.length() == 0) {
                // test step has not group name, so generate one:
                group = "Group " + scriptStep.getStepIndex();
            }
            if (!group.equals(currentGroup)) {
                useCaseList.add(useCase);
                useCase = new HDScriptUseCase();
                currentGroup = group;
            }
            useCase.getScriptSteps().add(convertScriptStep(scriptStep, sc.getNextStepIndex()));
        }
        if (useCase != null) {
            useCaseList.add(useCase);
        }
        return useCaseList;
    }

    private static TestStep convertScriptStep(ScriptStep scriptStep, int stepIndex) {
        TestStep testStep = null;
        if (ScriptConstants.SLEEP.equals(scriptStep.getType())) {
            testStep = convertSleepStep(scriptStep);
        } else if (ScriptConstants.THINK_TIME.equals(scriptStep.getType())) {
            testStep = convertThinkStep(scriptStep);
        } else if (ScriptConstants.VARIABLE.equals(scriptStep.getType())) {
            testStep = convertVariableStep(scriptStep);
        } else if (ScriptConstants.AUTHENTICATION.equals(scriptStep.getType())) {
            testStep = convertAuthenticationStep(scriptStep);
        } else if (ScriptConstants.COOKIE.equals(scriptStep.getType())) {
            testStep = convertCookieStep(scriptStep);
        } else if (ScriptConstants.LOGIC.equals(scriptStep.getType())) {
            testStep = convertLogicStep(scriptStep);
        } else if (ScriptConstants.REQUEST.equals(scriptStep.getType())) {
            testStep = convertRequestStep(scriptStep);
        } else if (ScriptConstants.CLEAR.equals(scriptStep.getType())) {
            testStep = convertClearStep(scriptStep);
        } else if (ScriptConstants.TIMER.equals(scriptStep.getType())) {
            testStep = convertTimerStep(scriptStep);
        }
        testStep.setStepIndex(stepIndex);
        return testStep;
    }

    private static TestStep convertSleepStep(ScriptStep scriptStep) {
        SleepTimeStep sts = new SleepTimeStep();
        Set<RequestData> data = scriptStep.getData();
        data.stream().filter(requestData -> ScriptConstants.TIME.equals(requestData.getKey())).findFirst().ifPresent(requestData -> sts.setValue(requestData.getValue()));
        return sts;
    }

    private static TestStep convertLogicStep(ScriptStep scriptStep) {
        LogicStep sts = new LogicStep();
        sts.setName(scriptStep.getName());
        sts.setScriptGroupName(scriptStep.getScriptGroupName());
        Set<RequestData> data = scriptStep.getData();
        data.stream().filter(requestData -> ScriptConstants.SCRIPT.equals(requestData.getKey())).findFirst().ifPresent(requestData -> sts.setScript(requestData.getValue()));
        return sts;
    }

    private static TestStep convertCookieStep(ScriptStep scriptStep) {
        CookieStep sts = new CookieStep();
        Set<RequestData> data = scriptStep.getData();
        for (RequestData requestData : data) {
            if (ScriptConstants.COOKIE_NAME.equals(requestData.getKey())) {
                sts.setCookieName(requestData.getValue());
            }
            if (ScriptConstants.COOKIE_VALUE.equals(requestData.getKey())) {
                sts.setCookieValue(requestData.getValue());
            }
            if (ScriptConstants.COOKIE_DOMAIN.equals(requestData.getKey())) {
                sts.setCookieDomain(requestData.getValue());
            }
            if (ScriptConstants.COOKIE_PATH.equals(requestData.getKey())) {
                sts.setCookiePath(requestData.getValue());
            }
        }
        return sts;
    }

    private static TestStep convertClearStep(ScriptStep scriptStep) {
        ClearCookiesStep sts = new ClearCookiesStep();
        // Set<RequestData> data = scriptStep.getData();
        // for (RequestData requestData : data) {
        // if (ScriptConstants.TIME.equals(requestData.getKey())) {
        // sts.setValue(requestData.getValue());
        // break;
        // }
        // }
        return sts;
    }

    private static TestStep convertTimerStep(ScriptStep scriptStep) {
        TimerStep sts = new TimerStep();
        Set<RequestData> data = scriptStep.getData();
        for (RequestData requestData : data) {
            if (ScriptConstants.LOGGING_KEY.equals(requestData.getKey())) {
                sts.setValue(requestData.getValue());
            } else if (ScriptConstants.IS_START.equals(requestData.getKey())) {
                TimerAction action = TimerAction.valueOf(requestData.getValue());
                sts.setStart(action == TimerAction.START);
            }
        }
        return sts;
    }

    private static TestStep convertRequestStep(ScriptStep scriptStep) {
        RequestStep rs = new RequestStep();
        rs.setName(scriptStep.getName() + " (" + scriptStep.getStepIndex() + ")");
        rs.setScriptGroupName(scriptStep.getScriptGroupName());
        rs.setOnFail(scriptStep.getOnFail());

        HDRequest hdRequest = new HDRequest();
        hdRequest.setMethod(scriptStep.getMethod());
        hdRequest.setReqFormat(scriptStep.getReqFormat());
        hdRequest.setLoggingKey(scriptStep.getLoggingKey());
        hdRequest.setPath(scriptStep.getSimplePath());
        hdRequest.setProtocol(scriptStep.getProtocol());
        hdRequest.setPayload(scriptStep.getPayload());
        hdRequest.setLabel(scriptStep.getLabel());
        String host = extractHost(scriptStep.getHostname());
        String port = extractPort(scriptStep.getHostname());
        hdRequest.setHost(host);
        hdRequest.setPort(port);
        hdRequest.setPostDatas(getPostData(scriptStep.getPostDatas()));
        hdRequest.setQueryString(getPostData(scriptStep.getQueryStrings()));
        hdRequest.setRequestHeaders(getRequestHeaders(scriptStep.getRequestheaders(), scriptStep.getRequestCookies()));

        HDResponse hdResponse = new HDResponse(scriptStep.getRespFormat());

        hdResponse.getAssignment().getHeaderVariable()
                .addAll(getAssignments(RequestDataType.headerAssignment, scriptStep.getResponseData()));
        hdResponse.getAssignment().getCookieVariable()
                .addAll(getAssignments(RequestDataType.cookieAssignment, scriptStep.getResponseData()));
        hdResponse.getAssignment().getBodyVariable()
                .addAll(getAssignments(RequestDataType.bodyAssignment, scriptStep.getResponseData()));

        hdResponse.getValidation().getHeaderValidation()
                .addAll(getValidations(RequestDataType.headerValidation, scriptStep.getResponseData()));
        hdResponse.getValidation().getCookieValidation()
                .addAll(getValidations(RequestDataType.cookieValidation, scriptStep.getResponseData()));
        hdResponse.getValidation().getBodyValidation()
                .addAll(getValidations(RequestDataType.bodyValidation, scriptStep.getResponseData()));

        rs.setRequest(hdRequest);
        rs.setResponse(hdResponse);

        return rs;
    }

    /**
     * @param hostname
     * @return
     */
    protected static String extractPort(String hostname) {
        int index = hostname.lastIndexOf(':');
        String port = null;
        if (index != -1 && index + 1 < hostname.length()) {
            String candidate = hostname.substring(index + 1);
            if (NumberUtils.isDigits(candidate)) {
                port = candidate;
            } 
        }
        return port;
    }

    /**
     * @param hostname
     * @return
     */
    protected static String extractHost(String hostname) {
        int index = hostname.lastIndexOf(':');
        if (index != -1) {
            try {
                String port = hostname.substring(index + 1);
                if (NumberUtils.isDigits(port)) {
                    hostname = hostname.substring(0, index);
                }
            } catch (Exception e) {
                LOG.error("Error parsing host");
            }
        }
        return hostname;
    }

    private static Collection<? extends AssignmentData> getAssignments(RequestDataType type,
            Set<RequestData> requestDatas) {
        List<AssignmentData> assignmentDataList = new ArrayList<AssignmentData>();
        for (RequestData requestData : requestDatas) {
            if (isAssignment(requestData)) {
                if (type.name().equals(requestData.getType())
                        || (type == RequestDataType.bodyAssignment
                        && requestData.getType().equals("responseData"))) {
                    AssignmentData ad = new AssignmentData();
                    ad.setKey(requestData.getKey());
                    if (requestData.getValue().length() > 1 && requestData.getValue().startsWith("==")) {
                        ad.setValue(requestData.getValue().substring(2));
                    } else {
                        ad.setValue(requestData.getValue().substring(1));
                    }
                    ad.setPhase(requestData.getPhase());
                    assignmentDataList.add(ad);
                }
            }
        }
        return assignmentDataList;
    }

    /**
     * @param data
     * @return
     */
    public static boolean isAssignment(RequestData data) {
        if (data.getType().equals(RequestDataType.bodyAssignment.name())
                || data.getType().equals(RequestDataType.cookieAssignment.name())
                || data.getType().equals(RequestDataType.headerAssignment.name())
                || (data.getType().equals("responseData") && data.getValue() != null
                        && data.getValue().startsWith("=") && !data.getValue().startsWith("=="))) {
            return true;
        }
        return false;
    }

    private static Collection<? extends ValidationData> getValidations(RequestDataType type,
            Set<RequestData> requestDatas) {
        List<ValidationData> validationDataList = new ArrayList<ValidationData>();
        for (RequestData requestData : requestDatas) {
            if (!isAssignment(requestData)) {
                if (type.name().equals(requestData.getType())
                        || (type == RequestDataType.bodyValidation
                        && requestData.getType().equals("responseData"))) {
                    ValidationData ad = new ValidationData();
                    ad.setKey(requestData.getKey());
                    ValidationType validationType = ValidationType.getValidationType(requestData.getValue());
                    ad.setCondition(validationType.getValue());
                    ad.setValue(stripValidationType(requestData.getValue(), validationType));
                    ad.setPhase(requestData.getPhase());
                    validationDataList.add(ad);
                }
            }
        }
        return validationDataList;
    }

    /**
     * @param value
     * @param validationType
     * @return
     */
    private static String stripValidationType(String value, ValidationType validationType) {
        if (value.startsWith(validationType.getValue())) {
            return value.substring(validationType.getValue().length());
        }
        return value;
    }

    private static TestStep convertVariableStep(ScriptStep scriptStep) {
        Set<RequestData> data = scriptStep.getData();
        VariableStep vs = new VariableStep();
        for (RequestData requestData : data) {
            vs.setKey(requestData.getKey());
            vs.setValue(requestData.getValue());
            break;
        }
        return vs;
    }
    private static TestStep convertAuthenticationStep(ScriptStep scriptStep) {
        Set<RequestData> data = scriptStep.getData();
        AuthenticationStep as = new AuthenticationStep();
        for (RequestData requestData : data) {
            if (ScriptConstants.AUTH_USER_NAME.equals(requestData.getKey())) {
                as.setUserName(requestData.getValue());
            } else if (ScriptConstants.AUTH_PASSWORD.equals(requestData.getKey())) {
                as.setPassword(requestData.getValue());
            } else if (ScriptConstants.AUTH_REALM.equals(requestData.getKey())) {
                as.setRealm(requestData.getValue());
            } else if (ScriptConstants.AUTH_SCHEME.equals(requestData.getKey())) {
                as.setScheme(AuthScheme.getScheme(requestData.getValue()));
            } else if (ScriptConstants.AUTH_HOST.equals(requestData.getKey())) {
                as.setHost(requestData.getValue());
            } else if (ScriptConstants.AUTH_PORT.equals(requestData.getKey())) {
                as.setPort(requestData.getValue());
            }
        }
        return as;
    }

    private static TestStep convertThinkStep(ScriptStep scriptStep) {
        ThinkTimeStep tts = new ThinkTimeStep();
        Set<RequestData> data = scriptStep.getData();
        for (RequestData requestData : data) {
            if (ScriptConstants.MIN_TIME.equals(requestData.getKey())) {
                tts.setMinTime(requestData.getValue());
            } else if (ScriptConstants.MAX_TIME.equals(requestData.getKey())) {
                tts.setMaxTime(requestData.getValue());
            }
        }
        return tts;
    }

    public static List<Header> getPostData(Set<RequestData> postData) {
        List<Header> dataList = new ArrayList<Header>();
        for (RequestData requestData : postData) {
            Header td = new Header();
            td.setKey(requestData.getKey());
            td.setValue(requestData.getValue());

            dataList.add(td);
        }
        return dataList;
    }

    private static String getValidateValue(String value) {
        if (value.startsWith("==")) {
            return value.substring(2);
        } else if (value.startsWith("!=")) {
            return value.substring(2);
        } else if (value.startsWith("Contains")) {
            return value.substring("Contains".length());
        } else if (value.startsWith("Does not contain")) {
            return value.substring("Does not contain".length());
        } else if (value.startsWith("Empty")) {
            return value.substring("Empty".length());
        } else if (value.startsWith("Not empty")) {
            return value.substring("Not empty".length());
        } else
            return value;
    }

    private static String getCondition(String conditionStr) {
        if (conditionStr.startsWith("==")) {
            return ValidationType.equals.name();
        } else if (conditionStr.startsWith("!=")) {
            return ValidationType.notequals.name();
        } else if (conditionStr.startsWith("Contains")) {
            return ValidationType.contains.name();
        } else if (conditionStr.startsWith("Does not contain")) {
            return ValidationType.doesnotcontain.name();
        } else if (conditionStr.startsWith("Empty")) {
            return ValidationType.empty.name();
        } else if (conditionStr.startsWith("Not empty")) {
            return ValidationType.notempty.name();
        } else if (conditionStr.startsWith("Greater Than")) {
            return ValidationType.greaterthan.name();
        } else if (conditionStr.startsWith("Less Than")) {
            return ValidationType.lessthan.name();
        }
        return "";
    }

    public static List<ResponseData> getResponseData(Set<RequestData> responseData) {
        List<ResponseData> dataList = new ArrayList<ResponseData>();

        for (RequestData rd : responseData) {
            if (rd.getValue() != null) {
                if (rd.getValue().length() > 1 && rd.getValue().charAt(0) == '=' && rd.getValue().charAt(1) != '=') {
                    AssignmentData data = new AssignmentData();
                    data.setKey(rd.getKey());
                    data.setValue(rd.getValue().substring(1));
                    dataList.add(data);
                    data.setPhase(rd.getPhase());
                } else {
                    ValidationData vd = new ValidationData();
                    vd.setKey(rd.getKey());
                    vd.setCondition(getCondition(rd.getValue()));
                    vd.setValue(getValidateValue(rd.getValue()));
                    vd.setPhase(rd.getPhase());
                    dataList.add(vd);
                }
            }
        }
        return dataList;
    }

    public static List<Header> getResponseHeaders(Set<RequestData> responseHeaders, Set<RequestData> responseCookies) {
        List<Header> dataList = new ArrayList<Header>();
        for (RequestData rd : responseHeaders) {
            if (rd.getValue() != null) {
                Header header = new Header();
                header.setKey(rd.getKey());
                if (rd.getValue().length() > 1 && rd.getValue().charAt(0) == '=' && rd.getValue().charAt(1) != '=') {
                    header.setValue(rd.getValue().substring(1));
                }
                dataList.add(header);
            }
        }

        for (RequestData sc : responseCookies) {
            if (sc.getValue() == null)
                continue;
            if (sc.getValue().startsWith("@")) {
                Header header = new Header();
                header.setKey(sc.getValue().substring(1));
                header.setValue(sc.getKey());
            }
        }
        return dataList;
    }

    public static List<Header> getRequestHeaders(Set<RequestData> headers, Set<RequestData> cookies) {
        List<Header> dataList = new ArrayList<Header>();

        Header testData = new Header();
        testData.setKey("TEST");
        testData.setValue("LoadTest");

        dataList.add(testData);
        for (RequestData data : headers) {
            if (includedHeader(data.getKey())) {
                String key = data.getKey();
                if (key.toLowerCase().startsWith("x-include-")) {
                    key = key.substring(10);
                }
                Header td = new Header();
                td.setKey(key);
                td.setValue(data.getValue());
                dataList.add(td);
            }
        }

        if (null != cookies) {
            String cookieStr = "";
            for (RequestData c : cookies) {
                // only add cookies for variables
                if (c.getValue().startsWith("@")) {
                    cookieStr += "." + c.getKey().replace(".", "-dot-") + "=." + c.getValue() + ".;";
                } /*
                   * else { cookieStr += "." + c.getKey().replace(".", "-dot-") + "=." + c.getValue() + ".;"; }
                   */
            }
            if (!cookieStr.isEmpty()) {
                String cookieValue = "#function.string.concat" + cookieStr;
                Header cookieData = new Header();
                cookieData.setKey("Cookie");
                cookieData.setValue(cookieValue);
                dataList.add(cookieData);
            }
        }
        return dataList;
    }

    public static boolean includedHeader(String header) {

        return (header.startsWith("Accept") ||
                (!header.equalsIgnoreCase("host")
                        && !header.startsWith("Content")
                        && !header.equalsIgnoreCase("Connection")
                        && !header.equalsIgnoreCase("Authorization")
                        && !header.equalsIgnoreCase("Cookie")
                        && !header.equalsIgnoreCase("Referer")
                        && !header.toLowerCase().startsWith("get ")
                        && !header.toLowerCase().startsWith("post ")
                        && !header.equalsIgnoreCase("If-None-Match")
                        && !header.equalsIgnoreCase("If-Modified-Since")));
    }

    private static class StepCounter {
        private int stepIndex = 0;

        private int getNextStepIndex() {
            return stepIndex++;
        }
    }

}