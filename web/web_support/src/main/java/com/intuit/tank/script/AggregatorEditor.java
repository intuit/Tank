/**
 * Copyright 2012 Intuit Inc. All Rights Reserved
 */
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.util.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.TimerAction;

/**
 * @author rchalmela
 * 
 */
@Named
@ConversationScoped
public class AggregatorEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private String aggregatorName = "";
    private ScriptStep step;
    private String buttonLabel = ADD_LABEL;

    private boolean editMode;

    // Aggregator button will be enabled/disabled bsaed on this value
    private boolean canBeAggregated = false;

    public void editAggregator(ScriptStep step) {
        this.step = step;
        this.editMode = true;
        for (RequestData requestData : step.getData()) {
            if (requestData.getKey().equalsIgnoreCase(ScriptConstants.LOGGING_KEY)) {
                aggregatorName = requestData.getValue();
                break;
            }
        }
        buttonLabel = EDIT_LABEL;
    }

    public void insertAggregator() {
        this.editMode = false;
        this.step = null;
        aggregatorName = "";
        buttonLabel = ADD_LABEL;
    }

    public void addToScript() {
        if (validate()) {
            if (editMode) {
                edit();
            } else {
                insert();
            }
        }
    }

    public boolean isStart(ScriptStep step) {
        boolean ret = false;
        for (RequestData rd : step.getData()) {
            if (rd.getKey().equals(ScriptConstants.IS_START) && rd.getValue().equals(TimerAction.START.name())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public void checkFixOrder(ScriptStep step) {
        if (isStart(step)) {
            ScriptStep stepPair = getAggregatorPair(step);
            if (step.getStepIndex() > stepPair.getStepIndex()) {
                int temp = step.getStepIndex();
                step.setStepIndex(stepPair.getStepIndex());
                stepPair.setStepIndex(temp);
            }
        }
    }

    public void edit() {

        // Change name of current step
        renameScriptStep(step, aggregatorName);

        // Change name of current's pair
        ScriptStep stepPair = getAggregatorPair(step);
        renameScriptStep(stepPair, aggregatorName);

        ScriptUtil.updateStepLabel(step);
        ScriptUtil.updateStepLabel(stepPair);

        aggregatorName = "";
    }

    /**
     * Renames logging key of an aggregator
     * 
     * @param step
     * @param name
     */
    private void renameScriptStep(ScriptStep step, String name) {
        RequestData rd = getRequestDataWithKey(step, ScriptConstants.LOGGING_KEY);
        rd.setValue(name);
    }

    /**
     * Returns the RequestData of a ScriptTest with a particular key
     * 
     * @param step
     * @param key
     * @return
     */
    private RequestData getRequestDataWithKey(ScriptStep step, String key) {
        RequestData ret = null;
        for (RequestData rd : step.getData()) {
            if (rd.getKey().equalsIgnoreCase(key)) {
                ret = rd;
                break;
            }

        }
        return ret;
    }

    /**
     * Performs validation on the save
     * 
     * @return
     */
    private boolean validate() {
        boolean retVal = true;
        // TODO check if name is is not nested within a pair with the same name
        return retVal;
    }

    /**
     * Gets the paired aggreagator ScriptStep of the given ScriptStep
     * 
     * @param step
     * @return
     */
    public ScriptStep getAggregatorPair(ScriptStep step) {
        ScriptStep ret = null;
        String pairId = null;
        for (RequestData rd : step.getData()) {
            if (rd.getKey().equals(ScriptConstants.AGGREGATOR_PAIR)) {
                pairId = rd.getValue();
                break;
            }
        }

        for (ScriptStep tempStep : scriptEditor.getSteps()) {
            if (tempStep.getUuid().equals(pairId)) {
                ret = tempStep;
                break;
            }
        }
        return ret;
    }

    /**
     * Add an aggregator pair to a contiguous list of steps, at the beginning and end of the selection
     */
    public void insert() {

        List<ScriptStep> selectedSteps = scriptEditor.getSelectedSteps();

        ScriptStep aggregatorStart = getAggregatorStep(true);
        ScriptStep aggregatorStop = getAggregatorStep(false);

        setAggregatorPair(aggregatorStop, aggregatorStart.getUuid());
        setAggregatorPair(aggregatorStart, aggregatorStop.getUuid());

        // Add stop first then start
        scriptEditor.insert(aggregatorStop, selectedSteps.get(selectedSteps.size() - 1).getStepIndex());
        scriptEditor.insert(aggregatorStart, selectedSteps.get(0).getStepIndex() - 1);

    }

    /**
     * Adds the Uuid of the paired aggregator step to the given aggregator
     * 
     * @param aggregator
     *            the given aggregator
     * @param Uuid
     *            the uuid of the paired aggregator to add
     */
    private void setAggregatorPair(ScriptStep aggregator, String Uuid) {
        aggregator.getData().add(getAggregatorRequestData(ScriptConstants.AGGREGATOR_PAIR, Uuid));
    }

    /**
     * Get back a RequestData of type ScriptConstants.AGGREGATOR with given key and value pair
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the RequestData of type ScriptConstants.AGGREGATOR with given key and value
     */
    private RequestData getAggregatorRequestData(String key, String value) {
        RequestData rd = new RequestData();
        rd.setType(ScriptConstants.TIMER);
        rd.setKey(key);
        rd.setValue(value);
        return rd;
    }

    /**
     * Get an aggregator step of start or end depending on passed value
     * 
     * @param isStart
     *            true if start, false otherwise
     * @return aggregator ScriptStep
     */
    private ScriptStep getAggregatorStep(boolean isStart) {

        ScriptStep aggregator = new ScriptStep();
        aggregator.setType(ScriptConstants.TIMER);

        Set<RequestData> ds = new HashSet<RequestData>();
        ds.add(getAggregatorRequestData(ScriptConstants.LOGGING_KEY, aggregatorName));

        if (isStart) {
            ds.add(getAggregatorRequestData(ScriptConstants.IS_START, TimerAction.START.name()));
        } else {
            ds.add(getAggregatorRequestData(ScriptConstants.IS_START, TimerAction.STOP.name()));
        }

        aggregator.setData(ds);
        return aggregator;
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
     * @return the aggregatorName
     */
    public String getAggregatorName() {
        return aggregatorName;
    }

    /**
     * @param aggregatorName
     *            the aggregatorName to set
     */
    public void setAggregatorName(String aggregatorName) {
        this.aggregatorName = aggregatorName;
    }

    /**
     * Used to render the aggregator button if steps can be aggregated
     * 
     * @return
     */
    public void aggregateCheck() {
        boolean ret = true;

        List<ScriptStep> selectedSteps = scriptEditor.getSelectedSteps();
        List<ScriptStep> steps = scriptEditor.getSteps();

        if (selectedSteps.size() > 1) {
            Collections.sort(selectedSteps);

            // Check if steps are contiguous and also if they contain another
            // agregator
            int index = selectedSteps.get(0).getStepIndex();
            for (int i = 0; i < selectedSteps.size(); i++) {
                if (selectedSteps.get(i).getStepIndex() != (i + index)) {
                    ret = false;
                    break;
                }
            }

            // confirm that this selection does not already have an aggregator
            // pair around it
            // if (isAggregator(steps.get(index - 1))) {
            // // TODO
            // }

            // if (isAggregator(steps.get(index + selectedSteps.size() + 1))) {
            // // TODO
            // }

        } else {
            ret = false;
        }
        canBeAggregated = ret;
    }

    /**
     * Checks if the given ScriptStep is an Aggregator
     * 
     * @param step
     *            the ScriptStep to check
     * @return true if the ScriptStep is an aggregator
     */
    public boolean isAggregator(ScriptStep step) {
        boolean ret = false;
        if (ScriptConstants.TIMER.equals(step.getType())) {
            Set<RequestData> set = step.getData();
            for (RequestData rd : set) {
                if (ScriptConstants.LOGGING_KEY.equals(rd.getKey())) {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * @return the canBeAggregated
     */
    public boolean isCanBeAggregated() {
        return canBeAggregated;
    }

}
