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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import com.intuit.tank.project.AssignmentResponseContent;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ValidationResponseContent;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.api.enumerated.DataLocation;
import com.intuit.tank.vm.api.enumerated.ValidationType;

@Named
@ConversationScoped
public class ResponseContentEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RequestDataContentWrapper> validationContent = new ArrayList<RequestDataContentWrapper>();
    private List<RequestDataContentWrapper> assignmentContent = new ArrayList<RequestDataContentWrapper>();

    public void editResponseContent(Set<RequestData> requestData) {
        validationContent = new ArrayList<RequestDataContentWrapper>();
        assignmentContent = new ArrayList<RequestDataContentWrapper>();
        for (RequestData data : requestData) {
            if (ConverterUtil.isAssignment(data)) {
                assignmentContent.add(new RequestDataContentWrapper(data));
            } else {
                validationContent.add(new RequestDataContentWrapper(data));
            }
        }
    }

    /**
     * @return the validationContent
     */
    public List<RequestDataContentWrapper> getValidationContent() {
        return validationContent;
    }

    /**
     * @return the assignmentContent
     */
    public List<RequestDataContentWrapper> getAssignmentContent() {
        return assignmentContent;
    }

    private void insertValidation(RequestDataPhase phase) {
        RequestData rd = new RequestData();
        rd.setKey("key");
        rd.setValue(ValidationType.equals.getValue() + "value");
        rd.setPhase(phase);
        validationContent.add(new RequestDataContentWrapper(rd, false));
    }

    public void insertPreValidation() {
        insertValidation(RequestDataPhase.PRE_REQUEST);
    }

    public void insertPostValidation() {
        insertValidation(RequestDataPhase.POST_REQUEST);
    }

    public void insertAssignment() {
        RequestData rd = new RequestData();
        rd.setKey("key");
        rd.setValue("=value");
        rd.setPhase(RequestDataPhase.POST_REQUEST);
        assignmentContent.add(new RequestDataContentWrapper(rd, true));
    }

    public void removeAssignment(RequestDataContentWrapper data) {
        assignmentContent.remove(data);
    }

    public void removeValidation(RequestDataContentWrapper data) {
        validationContent.remove(data);
    }

    public ValidationType[] getValidationValues() {
        return ValidationType.values();
    }

    public DataLocation[] getDataLocationValues() {
        return DataLocation.values();
    }

    public RequestDataPhase[] getPhases() {
        return RequestDataPhase.values();
    }

    public boolean isValidation(RequestData data) {
        return data instanceof ValidationResponseContent;
    }

    public boolean isAssignment(RequestData data) {
        return data instanceof AssignmentResponseContent;
    }

    public Set<RequestData> getRequestDataSet() {
        Set<RequestData> reqDataSet = assignmentContent.stream().map(RequestDataContentWrapper::getData).collect(Collectors.toSet());
        for (RequestDataContentWrapper requestDataContentWrapper : validationContent) {
            reqDataSet.add(requestDataContentWrapper.getData());
        }
        return reqDataSet;
    }

}
