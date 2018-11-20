package com.intuit.tank.runner;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.FlowController;
import com.intuit.tank.harness.data.FailableStep;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.data.HDVariable;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.logging.LogEvent;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.AbortScriptException;
import com.intuit.tank.harness.test.GotoScriptException;
import com.intuit.tank.harness.test.KillScriptException;
import com.intuit.tank.harness.test.NextScriptGroupException;
import com.intuit.tank.harness.test.RestartScriptException;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.runner.method.TestStepRunner;
import com.intuit.tank.runner.method.TimerMap;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.settings.TankConfig;

public class TestPlanRunner implements Runnable {

    private static Logger LOG = LogManager.getLogger(TestPlanRunner.class);
    private Variables variables;
    private TimerMap timerMap;
    private String uniqueName;
    private HDTestPlan testPlan;
    private int threadNumber;
    private TankHttpClient httpClient;
    private BaseRequest previousRequest = null;
    private BaseResponse previousResponse = null;
    private boolean finished = false;
    private Map<String, String> headerMap;

    public TestPlanRunner(HDTestPlan testPlan, int threadNumber) {
        headerMap = new TankConfig().getAgentConfig().getRequestHeaderMap();
        this.testPlan = testPlan;
        this.threadNumber = threadNumber;
        setHttpClient(initHttpClient());
    }
    
    public TestPlanRunner(HDTestPlan testPlan, int threadNumber, TankHttpClient client) {
        headerMap = new TankConfig().getAgentConfig().getRequestHeaderMap();
        this.testPlan = testPlan;
        this.threadNumber = threadNumber;
        this.httpClient = client;
    }

    /**
     * @return the headerMap
     */
    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHttpClient(TankHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public TankHttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setUniqueName(String name) {
        this.uniqueName = name;
    }

    public void run() {
        MethodTimer mt = new MethodTimer(LOG, getClass(), "runTestPlan(" + testPlan.getTestPlanName() + ")");
        LogEvent logEvent = LogUtil.getLogEvent();
        variables = new Variables();
        logEvent.setVariables(variables);
        logEvent.setTestPlan(testPlan);
        timerMap = new TimerMap();
        HDTestVariables variableDefinitions = testPlan.getVariables();
        if (variableDefinitions != null) {
            for (HDVariable v : variableDefinitions.getVariables()) {
                variables.addVariable(v.getKey(), v.getValue(), variableDefinitions.isAllowOverride());
            }
        }
        variables.addVariable("THREAD_ID", Integer.toString(threadNumber), false);
        List<HDScriptGroup> group = testPlan.getGroup();
        try {
            int i = 0;
            int loopCount = 1;

            mainLoop: while (i < group.size()) {
                HDScriptGroup hdScriptGroup = group.get(i);
                logEvent.setGroup(hdScriptGroup);
                hdScriptGroup.setParent(testPlan);

                int scriptGroupLoop = 0;
                while (scriptGroupLoop < hdScriptGroup.getLoop()) {
                    List<HDScript> groupSteps = hdScriptGroup.getGroupSteps();
                    if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.kill) {
                        httpClient.close();
                        return;
                    }
                    try {
                        runTestGroup(groupSteps, hdScriptGroup);
                    } catch (KillScriptException e) {
                        LOG.info(LogUtil.getLogMessage(e.getMessage()));
                        httpClient.close();
                        return;
                    } catch (NextScriptGroupException e) {
                        scriptGroupLoop = hdScriptGroup.getLoop();
                        break;
                    } catch (RestartScriptException e) {
                        i = 0;
                        loopCount = 1;
                        continue mainLoop;
                    }
                    if (isCompleted(RunPhase.group, finished)) {
                        LOG.info(LogUtil.getLogMessage("finished or Stop set to group or less, exiting at group " + hdScriptGroup.getName()));
                        httpClient.close();
                        return;
                    }
                    scriptGroupLoop++;
                }
                if (group.get(group.size() - 1) != hdScriptGroup) {
                    i++;
                } else {
                    finished = true;
                    if (isCompleted(RunPhase.test, finished)) {
                        LOG.info(LogUtil.getLogMessage("finished or Stop set to test or less, exiting at test..."));
                        httpClient.close();
                        return;
                    }
                    i = 0;
                    loopCount++;
                    LOG.info(LogUtil.getLogMessage("Test for test plan " + testPlan.getTestPlanName()
                            + " has finished and is now starting over for the  "
                            + loopCount + " time"));
                }
            }
        } catch (Throwable e) {
            LOG.error(LogUtil.getLogMessage("Unexpected exception in test: " + e.toString()), e);

        } finally {
            APITestHarness.getInstance().threadComplete();
            LOG.info(LogUtil.getLogMessage(mt.getNaturalTimeMessage() + " Test complete. Exiting..."));
        }
    }

    private void runTestGroup(List<HDScript> scripts, HDScriptGroup parent) throws KillScriptException,
            RestartScriptException {
        MethodTimer mt = new MethodTimer(LOG, getClass(), "runScriptGroup(" + parent.getName() + ")");
        LogEvent logEvent = LogUtil.getLogEvent();
        try {
            for (HDScript hdscript : scripts) {
                MethodTimer mt1 = new MethodTimer(LOG, getClass(), "runScript(" + hdscript.getName() + ")");
                logEvent.setScript(hdscript);
                //LOG.info(LogUtil.getLogMessage("Entering Script", LogEventType.Informational));
                hdscript.setParent(parent);
                int scriptLoop = 0;
                APITestHarness.getInstance().getUserTracker().add(hdscript.getName());
                try {
                    while (scriptLoop < hdscript.getLoop()) {
                        String gotoGroup = null;
                        List<HDScriptUseCase> useCaseList = hdscript.getUseCase();
                        for (int i = 0; i < useCaseList.size(); i++) {
                            if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.kill) {
                                return;
                            }
                            HDScriptUseCase hdScriptUseCase = useCaseList.get(i);
                            hdScriptUseCase.setParent(hdscript);
                            if (gotoGroup != null) {
                                if (!checkGotoGroupUseCase(hdScriptUseCase, gotoGroup)) {
                                    continue;
                                } else {
                                    gotoGroup = null;
                                }
                            }
                            try {
                                runScriptSteps(hdScriptUseCase);
                            } catch (GotoScriptException e) {
                                i = 0;
                                gotoGroup = e.getGotoTarget();
                            }
                        }

                        scriptLoop++;
                    }
                } catch (AbortScriptException ase) {
                    // ignore and go to next script
                } finally {
                    LOG.info(LogUtil.getLogMessage(mt1.getNaturalTimeMessage()));
                    APITestHarness.getInstance().getUserTracker().remove(hdscript.getName());
                }
                if (shouldStop(RunPhase.script)) {
                    LOG.info("Stop set to script or less, exiting at script " + hdscript.getName());
                    return;
                }
            }
        } finally {
            LOG.info(LogUtil.getLogMessage(mt.getNaturalTimeMessage()));
        }
    }

    /**
     * Check to see if the thread/virtual user should continue for another loop
     * 
     * @param phase
     * @param finished
     * 
     * @return
     */
    private boolean isCompleted(RunPhase phase, boolean finished) {
        if (shouldStop(phase)
                || (finished && (APITestHarness.getInstance().getAgentRunData().getSimulationTime() <= 0
                || APITestHarness.getInstance().hasMetSimulationTime()
                || APITestHarness.getInstance().isDebug()))) {
            return true;
        }
        return false;
    }

    private boolean shouldStop(RunPhase phase) {
        boolean ret = false;
        if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.stop) {
            ret = phase.ordinal() >= APITestHarness.getInstance().getAgentRunData().getStopBehavior().ordinal();
            LOG.info("shouldStop: stopBehavior="
                    + APITestHarness.getInstance().getAgentRunData().getStopBehavior().name() + " : phase="
                    + phase.name());
        }
        return ret;
    }

    private boolean checkGotoGroupUseCase(HDScriptUseCase hdScriptUseCase, String gotoGroup) {
        List<TestStep> scriptSteps = hdScriptUseCase.getScriptSteps();
        return scriptSteps.stream().filter(testStep -> testStep instanceof RequestStep).map(testStep -> (RequestStep) testStep).anyMatch(rs -> gotoGroup.equals(rs.getScriptGroupName()));
    }

    private void runScriptSteps(HDScriptUseCase hdScriptUseCase) throws KillScriptException,
            AbortScriptException, GotoScriptException, RestartScriptException, NextScriptGroupException {
        List<TestStep> scriptSteps = hdScriptUseCase.getScriptSteps();
        String gotoGroup = null;
        LogEvent logEvent = LogUtil.getLogEvent();
        for (int i = 0; i < scriptSteps.size(); i++) {
            while (APITestHarness.getInstance().getCmd() == WatsAgentCommand.pause) {
                if (APITestHarness.getInstance().hasMetSimulationTime()) {
                    APITestHarness.getInstance().setCommand(WatsAgentCommand.stop);
                    break;
                } else {
                    try {
                        Thread.sleep(APITestHarness.POLL_INTERVAL);
                    } catch (InterruptedException e) {
                        // LOG.warn("Got interrupded during pause.");
                    }
                }
            }
            if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.kill) {
                return;
            }
            TestStep testStep = scriptSteps.get(i);
            logEvent.setStep(testStep);
            logEvent.setTransactionId(UUID.randomUUID().toString());
            testStep.setParent(hdScriptUseCase);
            if (gotoGroup != null) {
                if (testStep instanceof RequestStep) {
                    RequestStep rs = (RequestStep) testStep;
                    logEvent.setStepGroupName(rs.getScriptGroupName());
                    if (!gotoGroup.equalsIgnoreCase(rs.getScriptGroupName())) {
                        continue;
                    }
                }
            }

            FlowController flowController = APITestHarness.getInstance().getFlowController(
                    Thread.currentThread().getId());

            TestStepContext tsc = new TestStepContext(testStep, variables,
                    testPlan.getTestPlanName(),
                    uniqueName, timerMap, this);
            tsc.setRequest(previousRequest);
            tsc.setResponse(previousResponse);
            if (!flowController.shouldExecute(tsc)) {
                continue;
            }
            flowController.nextStep(tsc);
            if (!flowController.shouldExecute(tsc)) {
                continue;
            }

            TestStepRunner tsr = new TestStepRunner(tsc);
            if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.stop) {
                LOG.info(LogUtil.getLogMessage("Executing step after stop command " + tsc.getTestStep(),
                        LogEventType.Script));
            }
            flowController.startStep(tsc);
            String validation = TankConstants.HTTP_CASE_FAIL;
            try {
                validation = tsr.execute();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            } finally {
                flowController.endStep(tsc);
            }
            previousRequest = tsc.getRequest();
            previousResponse = tsc.getResponse();

            if (validation.equals(TankConstants.HTTP_CASE_FAIL)) {
                FailableStep rs = (FailableStep) tsc.getTestStep();
                String onFail = rs.getOnFail();
                if (onFail.equalsIgnoreCase(TankConstants.HTTP_CASE_SKIP)) {
                    APITestHarness.getInstance().addSkip();
                    String groupNameToSkip = rs.getScriptGroupName();
                    for (int j = i + 1; j < scriptSteps.size(); j++) {
                        TestStep testStep2 = scriptSteps.get(j);
                        String scriptGroupToTest = null;
                        if (testStep2 instanceof RequestStep) {
                            scriptGroupToTest = ((RequestStep) testStep2).getScriptGroupName();
                        }

                        if (!groupNameToSkip.equals(scriptGroupToTest)) {
                            i = j;
                            break;
                        }
                        if (j == scriptSteps.size() - 1) {
                            return;
                        }
                    }
                } else if (onFail.equalsIgnoreCase(TankConstants.HTTP_CASE_KILL)) {
                    APITestHarness.getInstance().addKill();
                    throw new KillScriptException("Killing " + tsc.getTestStep());
                } else if (onFail.equalsIgnoreCase(TankConstants.HTTP_CASE_SKIPGROUP)) {
                    APITestHarness.getInstance().addSkipGroup();
                    throw new NextScriptGroupException("Skilling test group at " + tsc.getTestStep());
                } else if (onFail.equalsIgnoreCase(TankConstants.HTTP_CASE_ABORT)) {
                    APITestHarness.getInstance().addAbort();
                    throw new AbortScriptException("Aborting " + tsc.getTestStep());
                } else if (onFail.equalsIgnoreCase(TankConstants.HTTP_CASE_RESTART)) {
                    if (APITestHarness.getInstance().hasMetSimulationTime()) {
                        APITestHarness.getInstance().addKill();
                        throw new KillScriptException("Killing " + tsc.getTestStep());
                    }
                    else {
                        APITestHarness.getInstance().addRestart();
                        throw new RestartScriptException("Restarting user at " + tsc.getTestStep());
                    }
                } else if (onFail.toLowerCase().startsWith(ScriptConstants.ACTION_GOTO_PREFIX)) {
                    gotoGroup = StringUtils.substring(onFail, ScriptConstants.ACTION_GOTO_PREFIX.length());
                    i = -1;
                    APITestHarness.getInstance().addGoto();
                    throw new GotoScriptException("Go to group " + gotoGroup, gotoGroup);
                }
            }
            if (shouldStop(RunPhase.step)) {
                LOG.info("Stop set to step, exiting at step " + testStep.getStepIndex());
                return;
            }
        }
    }

    public TankHttpClient initHttpClient() {
        try {
            //get the client from a factory and set it here.
            TankHttpClient ret = (TankHttpClient) Class.forName(APITestHarness.getInstance().getTankHttpClientClass()).newInstance();
            Long connectionTimeout = APITestHarness.getInstance().getTankConfig().getAgentConfig().getConnectionTimeout();
            if (connectionTimeout != null) {
                ret.setConnectionTimeout(connectionTimeout);
            }
            return ret;
        } catch (Exception e) {
            LOG.error("TankHttpClient specified incorrectly: " + e, e);
            throw new RuntimeException(e);
        }
    }
}
