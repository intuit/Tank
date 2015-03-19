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

import java.io.Serializable;

import com.intuit.tank.project.ScriptStep;

public class ScriptStepWrapper implements Serializable {

    private static final long serialVersionUID = 1L;
    private ScriptStep scriptStep;

    /**
     * @param script
     */
    public ScriptStepWrapper(ScriptStep script) {
        this.scriptStep = script;
    }

    /**
     * @return the scriptGroup
     */
    public String getScriptGroupName() {
        return scriptStep.getScriptGroupName();
    }

    /**
     * @param scriptGroupName
     *            the scriptGroup to set
     */
    public void setScriptGroupName(String scriptGroupName) {
        scriptStep.setScriptGroupName(scriptGroupName);
    }

    public String getLabel() {
        return ScriptStepDataLookup.getData(scriptStep);
    }

    public String getMethod() {
        return scriptStep.getMethod();
    }

    public String getName() {
        return scriptStep.getName();
    }

    public String getMimetype() {
        return scriptStep.getMimetype();
    }

    public String getComments() {
        return scriptStep.getComments();
    }

    /**
     * @return the scriptStep
     */
    public ScriptStep getScriptStep() {
        return scriptStep;
    }

    /**
     * @param scriptStep
     *            the scriptStep to set
     */
    public void setScriptStep(ScriptStep scriptStep) {
        this.scriptStep = scriptStep;
    }

}
