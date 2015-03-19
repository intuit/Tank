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
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.math.NumberUtils;
import org.jboss.seam.international.status.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ValidationUtil;

@Named
@ConversationScoped
public class SleepTimeEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private String sleepTime = "";
    private ScriptStep step;
    private String buttonLabel = ADD_LABEL;

    private boolean editMode;

    public void editSleepTime(ScriptStep step) {
        this.step = step;
        this.editMode = true;
        for (RequestData requestData : step.getData()) {
            sleepTime = requestData.getValue();
        }
        buttonLabel = EDIT_LABEL;
    }

    public void insertSleepTime() {
        this.editMode = false;
        sleepTime = "";
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
        scriptEditor.insert(ScriptStepFactory.createSleepTime(sleepTime));
    }

    public void done() {
        RequestData sleepTimeData = new RequestData();
        sleepTimeData.setKey(ScriptConstants.TIME);
        sleepTimeData.setValue(sleepTime);
        sleepTimeData.setType(ScriptConstants.SLEEP);

        Set<RequestData> datas = new HashSet<RequestData>();
        datas.add(sleepTimeData);

        step.setData(datas);
        ScriptUtil.updateStepLabel(step);
        sleepTime = "";
    }

    private boolean validate() {
        boolean retVal = true;

        if (!NumberUtils.isDigits(sleepTime) && !ValidationUtil.isVariable(sleepTime)
                && !ValidationUtil.isFunction(sleepTime)) {
            if (!sleepTime.matches(TankConstants.EXPRESSION_REGEX)) {
                retVal = false;
                messages.error("Sleep time has to be an integer, function, or variable.");
            }
        }
        if (NumberUtils.isDigits(sleepTime)) {
            if (retVal && Integer.parseInt(sleepTime) <= 0) {
                retVal = false;
                messages.error("Sleep time has to be greater than 0");
            }
        }
        return retVal;
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
     * @return the sleepTime
     */
    public String getSleepTime() {
        return sleepTime;
    }

    /**
     * @param sleepTime
     *            the sleepTime to set
     */
    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

}
