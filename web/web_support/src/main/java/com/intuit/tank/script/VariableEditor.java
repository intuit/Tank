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

import static com.intuit.tank.util.ButtonLabel.ADD_LABEL;
import static com.intuit.tank.util.ButtonLabel.EDIT_LABEL;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import com.intuit.tank.util.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;

@Named
@ConversationScoped
public class VariableEditor implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private String key = "";
    private String value = "";
    private ScriptStep step;
    private String buttonLabel = ADD_LABEL;

    private boolean editMode;

    public void insertVariable() {
        ScriptStep variableStep = ScriptStepFactory.createVariable("", "");
        setStep(variableStep);
        this.editMode = false;
        setButtonLabel(ADD_LABEL);
    }

    public void editVariable(ScriptStep step) {
        setStep(step);
        for (RequestData requestData : step.getData()) {
            key = requestData.getKey();
            value = requestData.getValue();
        }
        this.editMode = true;
        setButtonLabel(EDIT_LABEL);
    }

    /**
     * @return the buttonLabel
     */
    public String getButtonLabel() {
        return buttonLabel;
    }

    /**
     * @param buttonLabel
     *            the buttonLabel to set
     */
    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the step
     */
    public ScriptStep getStep() {
        if (step == null) {
            step = new ScriptStep();
        }
        return step;
    }

    /**
     * @param step
     *            the step to set
     */
    public void setStep(ScriptStep step) {
        this.step = step;
    }

    public void done() {
        for (RequestData requestData : step.getData()) {
            requestData.setKey(key);
            requestData.setValue(value);
        }
        ScriptUtil.updateStepLabel(step);
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

    private boolean validate() {
        boolean retVal = true;
        if (StringUtils.isEmpty(key)) {
            messages.error("Key cannot be empty");
            retVal = false;
        }
        if (StringUtils.isEmpty(value)) {
            messages.error("Value cannot be empty");
            retVal = false;
        }
        // Add extra validation here to check for existing key
        return retVal;
    }

    private void insert() {
        scriptEditor.insert(ScriptStepFactory.createVariable(key, value));
    }
}
