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

import com.intuit.tank.project.AssignmentResponseContent;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ValidationResponseContent;
import com.intuit.tank.vm.api.enumerated.ValidationType;

public class ResponseContentParser {

    public static String extractOperator(RequestData data) {
        if (data instanceof AssignmentResponseContent) {
            return "=";
        } else if (data instanceof ValidationResponseContent) {
            ValidationResponseContent vrc = (ValidationResponseContent) data;
            return vrc.getOperator().getValue();
        } else {
            return extractCondition(data.getValue());
        }

    }

    public static String extractValidateValue(String value) {
        if (value.length() > 1 && value.charAt(0) == '=' && value.charAt(1) != '=') {
            value = value.substring(1);
        } else {
            for (ValidationType type : ValidationType.values()) {
                if (value.startsWith(type.getValue())) {
                    value = value.substring(type.getValue().length());
                    break;
                }
            }
        }
        return value;
    }

    public static String extractCondition(String conditionStr) {
        String ret = "";
        if (conditionStr.length() > 1 && conditionStr.charAt(0) == '=' && conditionStr.charAt(1) != '=') {
            ret = "=";
        } else {
            for (ValidationType type : ValidationType.values()) {
                if (conditionStr.startsWith(type.getValue())) {
                    ret = type.getValue();
                    break;
                }
            }
        }
        return ret;
    }
}