package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.runner.ErrorContainer;

public class DebugStep implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> entryVariables;
    private Map<String, String> exitVariables;
    private BaseRequest request;
    private BaseResponse response;
    private String logEntry;
    private TestStep stepRun;
    private List<ErrorContainer> errors = new ArrayList<ErrorContainer>();

    public DebugStep(TestStep step) {
        super();
        this.stepRun = step;
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
    public void setErrors(List<ErrorContainer> errors) {
        this.errors = new ArrayList<ErrorContainer>(errors);
    }

    /**
     * @return the entryVariables
     */
    public Map<String, String> getEntryVariables() {
        return entryVariables;
    }

    /**
     * @param entryVariables
     *            the entryVariables to set
     */
    public void setEntryVariables(Map<String, String> entryVariables) {
        this.entryVariables = entryVariables;
    }

    /**
     * @return the exitVariables
     */
    public Map<String, String> getExitVariables() {
        return exitVariables;
    }

    /**
     * @param exitVariables
     *            the exitVariables to set
     */
    public void setExitVariables(Map<String, String> exitVariables) {
        this.exitVariables = exitVariables;
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
     * @return the logEntry
     */
    public String getLogEntry() {
        return logEntry;
    }

    /**
     * @param logEntry
     *            the logEntry to set
     */
    public void setLogEntry(String logEntry) {
        this.logEntry = logEntry;
    }

    /**
     * @return the stepRun
     */
    public TestStep getStepRun() {
        return stepRun;
    }

    /**
     * @param stepRun
     *            the stepRun to set
     */
    public void setStepRun(TestStep stepRun) {
        this.stepRun = stepRun;
    }

    public void clear() {
        entryVariables = null;
        exitVariables = null;
        request = null;
        response = null;
        logEntry = null;

    }

}
