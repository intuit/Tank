package com.intuit.tank.runner.method;

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

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngineManager;

import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.tools.script.ScriptIOBean;
import com.intuit.tank.tools.script.ScriptRunner;
import com.intuit.tank.vm.common.LogicScriptUtil;
import com.intuit.tank.vm.common.TankConstants;

class LogicRunner implements Runner {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LogicRunner.class);

    private TestStepContext tsc;
    private LogicStep step;

    // private Variables variables;
    LogicRunner(TestStepContext tsc) {
        this.tsc = tsc;
        step = (LogicStep) tsc.getTestStep();
    }

    public String execute() {
        String ret = TankConstants.HTTP_CASE_PASS;
        // execute script here
        AgentLoggingOutputLogger outputLogger = new AgentLoggingOutputLogger();
        Map<String, Object> inputs = new HashMap<String, Object>();

        inputs.put("variables", tsc.getVariables());
        if (tsc.getRequest() != null) {
            inputs.put("request", tsc.getRequest());
        }
        if (tsc.getResponse() != null) {
            inputs.put("response", tsc.getResponse());
        }
        String scriptToRun = new LogicScriptUtil().buildScript(step.getScript());
        try {
            ScriptIOBean ioBean = new ScriptRunner().runScript(step.getName(), scriptToRun,
                    new ScriptEngineManager().getEngineByExtension("js"), inputs, outputLogger);
            String action = (String) ioBean.getOutput("action");
            if (action != null) {
                ret = handleAction(action);
            }
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage("Error running script: " + e, LogEventType.Informational,
                    LoggingProfile.VERBOSE), e);
            LOG.error("Script:\n" + scriptToRun);
            step.setOnFail(TankConstants.HTTP_CASE_ABORT);
            ret = TankConstants.HTTP_CASE_FAIL;
        }
        return ret;
    }

    private String handleAction(String action) {
        String ret = TankConstants.HTTP_CASE_PASS;
        String onFail = null;
        if (ScriptConstants.ACTION_TERMINATE_USER.equalsIgnoreCase(action)) {
            onFail = TankConstants.HTTP_CASE_KILL;
        } else if (ScriptConstants.ACTION_ABORT_GROUP.equalsIgnoreCase(action)) {
            onFail = TankConstants.HTTP_CASE_SKIP;
        } else if (ScriptConstants.ACTION_ABORT_SCRIPT.equalsIgnoreCase(action)) {
            onFail = TankConstants.HTTP_CASE_ABORT;
        } else if (ScriptConstants.ACTION_ABORT_SCRIPT_GROUP.equalsIgnoreCase(action)) {
            onFail = TankConstants.HTTP_CASE_SKIPGROUP;
        } else if (ScriptConstants.ACTION_RESTART_PLAN.equalsIgnoreCase(action)) {
            onFail = TankConstants.HTTP_CASE_RESTART;
        } else if (action.startsWith(ScriptConstants.ACTION_GOTO_PREFIX)) {
            onFail = action;
        } else {
            LOG.warn(LogUtil.getLogMessage("LogicRunner does not know about action " + action,
                    LogEventType.Informational));
        }
        if (onFail != null) {
            step.setOnFail(onFail);
            ret = TankConstants.HTTP_CASE_FAIL;
        }
        return ret;
    }
}
