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

import org.apache.commons.lang.StringUtils;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.script.RequestDataType;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.api.enumerated.DataLocation;
import com.intuit.tank.vm.api.enumerated.ValidationType;

public final class RequestDataContentWrapper {

    private RequestData data;
    private String operator;
    private String value;
    private String key;
    private String type;
    private boolean assignment;

    /**
     * 
     * @param data
     */
    public RequestDataContentWrapper(RequestData data) {
        this(data, ConverterUtil.isAssignment(data));

    }

    /**
     * 
     * @param data
     * @param assignemnt
     */
    public RequestDataContentWrapper(RequestData data, boolean assignemnt) {
        this.data = data;
        this.operator = ResponseContentParser.extractOperator(data);
        this.value = ResponseContentParser
                .extractValidateValue(data.getValue());
        this.key = data.getKey();
        this.assignment = assignemnt;
        this.type = getDataType(data.getType());
    }

    /**
     * @param type2
     * @return
     */
    private String getDataType(String dt) {
        String ret = DataLocation.Body.name();
        if (!StringUtils.isBlank(dt)) {
            if (dt.startsWith("header")) {
                ret = DataLocation.Header.name();
            } else if (dt.startsWith("cookie")) {
                ret = DataLocation.Cookie.name();
            }
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public boolean isPreRequest() {
        return data.getPhase() == RequestDataPhase.PRE_REQUEST;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RequestData getData() {
        data.setKey(getKey());
        data.setType(getRequestDataType().name());
        if (getOperator().equals("=")) {
            data.setValue("=" + getValue());
        } else {
            data.setValue(ValidationType.getValidationTypeFromRepresentation(
                    getOperator()).getValue()
                    + getValue());
        }
        return data;
    }

    private RequestDataType getRequestDataType() {
        RequestDataType ret = null;
        if (DataLocation.Header.name().equalsIgnoreCase(type)) {
            ret = assignment ? RequestDataType.headerAssignment
                    : RequestDataType.headerValidation;
        } else if (DataLocation.Cookie.name().equalsIgnoreCase(type)) {
            ret = assignment ? RequestDataType.cookieAssignment
                    : RequestDataType.cookieValidation;
        } else {
            ret = assignment ? RequestDataType.bodyAssignment
                    : RequestDataType.bodyValidation;
        }
        return ret;
    }

}
