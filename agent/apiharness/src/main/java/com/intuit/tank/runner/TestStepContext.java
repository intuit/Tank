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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.runner.method.TimerMap;

public class TestStepContext {

    private TestStep testStep;
    private Variables variables;
    private String testPlanName;
    private String uniqueName;
    private TankHttpClient httpClient;
    private TimerMap timerMap;
    private TestPlanRunner parent;
    private BaseRequest request;
    private BaseResponse response;
    private String result;
    private List<ErrorContainer> errors = new ArrayList<ErrorContainer>();

    public TestStepContext(@Nonnull TestStep testStep,
            @Nonnull Variables variables, @Nonnull String testPlanName,
            String uniqueName, @Nonnull TimerMap timerMap, TestPlanRunner parent) {
        this.testStep = testStep;
        this.variables = variables;
        this.testPlanName = testPlanName;
        this.uniqueName = uniqueName;
        this.parent = parent;
        this.timerMap = timerMap;
        this.httpClient = parent.getHttpClient();
    }

    public TimerMap getTimerMap() {
        return timerMap;
    }

    public TestPlanRunner getParent() {
        return this.parent;

    }

    /**
     * @return the errors
     */
    public List<ErrorContainer> getErrors() {
        return errors;
    }

    /**
     * @param errors
     *            the errors to set
     */
    public void addError(ErrorContainer error) {
        this.errors.add(error);
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the testStep
     */
    public TestStep getTestStep() {
        return testStep;
    }

    /**
     * @return the request
     */
    public BaseRequest getRequest() {
        return request;
    }

    /**
     * @param request
     *            the request to set
     */
    public void setRequest(BaseRequest request) {
        this.request = request;
    }

    /**
     * @return the response
     */
    public BaseResponse getResponse() {
        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(BaseResponse response) {
        this.response = response;
    }

    /**
     * @param testStep
     *            the testStep to set
     */
    public void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public String getTestPlanName() {
        return testPlanName;
    }

    public void setTestPlanName(String testPlanName) {
        this.testPlanName = testPlanName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * @return the httpClient
     */
    public TankHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * @param httpClient
     *            the httpClient to set
     */
    public void setHttpClient(TankHttpClient httpClient) {
        this.httpClient = httpClient;
    }

}
