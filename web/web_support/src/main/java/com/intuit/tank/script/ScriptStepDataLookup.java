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

import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;

public class ScriptStepDataLookup {

    public static String getData(ScriptStep scriptStep) {
        if (scriptStep.getType().equals(ScriptConstants.THINK_TIME)) {
            return getThinkTimeRepresentation(scriptStep.getData());
        } else if (scriptStep.getType().equals(ScriptConstants.SLEEP)) {
            return getSleepTimeRepresentation(scriptStep.getData());
        } else if (scriptStep.getType().equals(ScriptConstants.VARIABLE)) {
            return getVariableRepresentation(scriptStep.getData());
        } else if (scriptStep.getType().equals(ScriptConstants.REQUEST)) {
            return getRequestRepresentation(scriptStep);
        } else {
            throw new NotImplementedException("Request representation is not implemented.");
        }
    }

    private static String getThinkTimeRepresentation(Set<RequestData> data) {
        String minTime = "";
        String maxTime = "";
        for (RequestData requestData : data) {
            if (requestData.getKey().equals(ScriptConstants.MIN_TIME)) {
                minTime = requestData.getValue();
            } else if (requestData.getKey().equals(ScriptConstants.MAX_TIME)) {
                maxTime = requestData.getValue();
            }
        }
        return "Think time : " + minTime + " - " + maxTime;
    }

    private static String getSleepTimeRepresentation(Set<RequestData> data) {
        String time = "";
        for (RequestData requestData : data) {
            if (requestData.getKey().equals(ScriptConstants.TIME)) {
                time = requestData.getValue();
            }
        }
        return "Sleep time : " + time;
    }

    private static String getVariableRepresentation(Set<RequestData> data) {
        String key = "";
        String value = "";
        for (RequestData requestData : data) {
            key = requestData.getKey();
            value = requestData.getValue();
        }
        return "Variable : " + key + " = " + value;
    }

    private static String getRequestRepresentation(ScriptStep step) {
        return step.getHostname();
    }

}
