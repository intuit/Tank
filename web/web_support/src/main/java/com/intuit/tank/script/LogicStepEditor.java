package com.intuit.tank.script;

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

import static com.intuit.tank.util.ButtonLabel.ADD_LABEL;
import static com.intuit.tank.util.ButtonLabel.EDIT_LABEL;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import javax.script.ScriptEngineManager;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import com.intuit.tank.util.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.tools.script.ScriptIOBean;
import com.intuit.tank.tools.script.ScriptRunner;
import com.intuit.tank.tools.script.StringOutputLogger;
import com.intuit.tank.vm.common.LogicScriptUtil;
import com.intuit.tank.vm.settings.TankConfig;

@Named
@ConversationScoped
public class LogicStepEditor implements Serializable {

    private static final String DASHES = "-------------";

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    @Getter
    @Setter
    private String script;

    @Getter
    @Setter
    private String name;

    @Getter
    private String output;

    @Getter
    @Setter
    private String groupName;

    @Getter
    private LogicTestData logicTestData;
    private ScriptStep step;

    @Getter
    @Setter
    private String buttonLabel = ADD_LABEL;
    private ScriptStep previousRequest = null;

    protected boolean editMode;

    public void editLogicStep(ScriptStep step) {
        this.step = step;
        this.editMode = true;
        this.name = step.getName();
        this.previousRequest = scriptEditor.getPreviousRequest(step);
        this.groupName = step.getScriptGroupName();
        logicTestData = new LogicTestData(step, scriptEditor.getScript());
        // dd
        script = step.getData().stream().filter(requestData -> ScriptConstants.SCRIPT.equals(requestData.getKey())).findFirst().map(RequestData::getValue).orElseGet(() -> script);
        buttonLabel = EDIT_LABEL;
    }

    public void insertLogicStep() {
        this.previousRequest = scriptEditor.getPreviousRequest(null);
        this.editMode = false;
        script = "";
        name = "";
        output = "";
        groupName = "";
        logicTestData = new LogicTestData(previousRequest, scriptEditor.getScript());
        buttonLabel = ADD_LABEL;
    }

    public void addToScript() {
        if (validate()) {
            if (editMode) {
                done();
            } else {
                insert();
            }
        }
    }

    public void insert() {
        ScriptStep newStep = ScriptStepFactory.createLogic(name, script);
        newStep.setScriptGroupName(groupName);
        logicTestData.setInStep(newStep);
        scriptEditor.insert(newStep);
        groupName = null;
        script = null;
        name = null;
        output = null;
    }

    public void done() {
        RequestData rd = new RequestData();
        rd.setKey(ScriptConstants.SCRIPT);
        rd.setValue(script);
        rd.setType(ScriptConstants.SCRIPT);

        Set<RequestData> datas = new HashSet<RequestData>();
        datas.add(rd);

        step.setData(datas);
        logicTestData.setInStep(step);
        step.setName(name);
        step.setScriptGroupName(groupName);
        step.setComments("Logic Step: " + name);
        ScriptUtil.updateStepLabel(step);
        script = null;
        groupName = null;
        name = null;
        output = null;
        logicTestData = null;
    }

    public String getInsertBefore() {
        return new TankConfig().getLogicStepConfig().getInsertBefore();
    }

    public void setValuesFromPrevious() {
        if (previousRequest != null) {
            logicTestData = new LogicTestData(previousRequest, scriptEditor.getScript());
        }
    }

    private boolean validate() {
        if (StringUtils.isBlank(name)) {
            messages.error("Name cannot be empty");
            return false;
        }
        return true;
    }

    public void testScript() {
        StringOutputLogger outputLogger = new StringOutputLogger();
        Map<String, Object> inputs = new HashMap<String, Object>();
        Variables vars = new Variables();
        for (Entry<String, String> entry : logicTestData.getVariables()) {
            vars.addVariable(entry.getKey(), entry.getValue());
        }
        inputs.put("variables", vars);
        inputs.put("request", createRequest());
        inputs.put("response", createResponse());
        try {
            String scriptToRun = new LogicScriptUtil().buildScript(script);
            logMap("Variables", vars.getVariableValues(), outputLogger);
            outputLogger.logLine(DASHES + " script " + DASHES);
            ScriptIOBean ioBean = new ScriptRunner().runScript(name, scriptToRun,
                    new ScriptEngineManager().getEngineByExtension("js"), inputs, outputLogger);
            logMap("Outputs", ioBean.getOutputs(), outputLogger);
            logMap("Variables", vars.getVariableValues(), outputLogger);
        } catch (Exception e) {
            outputLogger.logLine("\nException thrown: " + e);
        }
        this.output = outputLogger.getOutput();
    }

    private BaseResponse createResponse() {
        BaseResponse ret = new BaseResponse() {

            @Override
            public String getValue(String key) {
                return null;
            }
        };
        ret.setResponseBody(logicTestData.getResponseBody());
        for (Entry<String, String> entry : logicTestData.getResponseHeaders()) {
            ret.getHeaders().put(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    private BaseRequest createRequest() {
        BaseRequest ret = new BaseRequest(null, null) {

            @Override
            public void setNamespace(String name, String value) {
            }

            @Override
            public void setKey(String key, String value) {
            }

            @Override
            public String getKey(String key) {
                return null;
            }
        };

        ret.setBody(logicTestData.getRequestBody());
        ret.addHeader("Test", "LoadTest");
        for (Entry<String, String> entry : logicTestData.getRequestHeaders()) {
            ret.addHeader(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    private void logMap(String label, Map<String, ? extends Object> map, StringOutputLogger outputLogger) {
        if (!map.isEmpty()) {
            outputLogger.logLine(DASHES + " " + label + " " + DASHES);
            for (Entry<String, ? extends Object> entry : map.entrySet()) {
                outputLogger.logLine(entry.getKey() + " = " + entry.getValue());
            }
        }
    }

}
