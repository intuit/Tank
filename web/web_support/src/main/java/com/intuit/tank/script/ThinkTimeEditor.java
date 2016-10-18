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

import org.apache.commons.lang3.math.NumberUtils;
import com.intuit.tank.util.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ValidationUtil;

@Named
@ConversationScoped
public class ThinkTimeEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private String minTime = "";
    private String maxTime = "";
    private String buttonLabel = ADD_LABEL;

    private ScriptStep step;

    private boolean editMode;

    public void editThinkTime(ScriptStep step) {
        this.step = step;
        this.editMode = true;
        for (RequestData requestData : step.getData()) {
            if (requestData.getKey().equals(ScriptConstants.MIN_TIME)) {
                minTime = requestData.getValue();
            } else if (requestData.getKey().equals(ScriptConstants.MAX_TIME)) {
                maxTime = requestData.getValue();
            }
        }
        setButtonLabel(EDIT_LABEL);
    }

    public void insertThinkTime() {
        this.editMode = false;
        minTime = "";
        maxTime = "";
        setButtonLabel(ADD_LABEL);
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
        scriptEditor.insert(ScriptStepFactory.createThinkTime(minTime, maxTime));
    }

    public void done() {
        RequestData minTimedata = new RequestData();
        minTimedata.setKey(ScriptConstants.MIN_TIME);
        minTimedata.setValue(minTime);
        RequestData maxTimedata = new RequestData();
        maxTimedata.setKey(ScriptConstants.MAX_TIME);
        maxTimedata.setValue(maxTime);

        Set<RequestData> datas = new HashSet<RequestData>();
        datas.add(minTimedata);
        datas.add(maxTimedata);
        step.setData(datas);
        ScriptUtil.updateStepLabel(step);
        minTime = "";
        maxTime = "";
    }

    private boolean validate() {
        boolean retVal = true;
        if (!NumberUtils.isDigits(minTime) && !ValidationUtil.isVariable(minTime)
                && !ValidationUtil.isFunction(minTime)) {
            if (!minTime.matches(TankConstants.EXPRESSION_REGEX)) {
                retVal = false;
                messages.error("Minimum think time has to be an integer, function, or variable.");
            }
        }
        if (!NumberUtils.isDigits(maxTime) && !ValidationUtil.isVariable(maxTime)
                && !ValidationUtil.isFunction(maxTime)) {
            if (!maxTime.matches(TankConstants.EXPRESSION_REGEX)) {
                retVal = false;
                messages.error("Maximum think time has to be an integer, function, or variable.");
            }
        }
        if (NumberUtils.isDigits(minTime) && NumberUtils.isDigits(maxTime)) {
            if (retVal && Integer.parseInt(minTime) > Integer.parseInt(maxTime)) {
                messages.error("Minimum think time should be less than maximum think time.");
                retVal = false;
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
     * @return the minTime
     */
    public String getMinTime() {
        return minTime;
    }

    /**
     * @param minTime
     *            the minTime to set
     */
    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    /**
     * @return the maxTime
     */
    public String getMaxTime() {
        return maxTime;
    }

    /**
     * @param maxTime
     *            the maxTime to set
     */
    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

}
