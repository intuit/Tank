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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;

@Named
@ConversationScoped
public class ScriptRequestEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;
    private ScriptStep step;
    private boolean editMode;

    private String gotoGroup;
    private String buttonLabel = ADD_LABEL;

    @Inject
    private RequestHeaderEditor requestHeaderEditor;
    @Inject
    private ResponseHeaderEditor responseHeaderEditor;
    @Inject
    private RequestCookiesEditor requestCookiesEditor;
    @Inject
    private ResponseCookiesEditor responseCookiesEditor;
    @Inject
    private ResponseContentEditor responseContentEditor;
    @Inject
    private PostDataEditor postDataEditor;
    @Inject
    private QueryStringEditor queryStringEditor;

    public void insertRequest() {
        this.step = new ScriptStep();
        step.setType(ScriptConstants.REQUEST);
        requestHeaderEditor.editRequestHeaders(step.getRequestheaders());
        responseHeaderEditor.editResponseHeaders(step.getResponseheaders());
        requestCookiesEditor.editRequestCookies(step.getRequestCookies());
        responseCookiesEditor.editResponseCookies(step.getResponseCookies());
        responseContentEditor.editResponseContent(step.getResponseData());
        postDataEditor.editPostData(step.getPostDatas());
        queryStringEditor.editQueryStrings(step.getQueryStrings());
        setupOnFail();
        editMode = false;
        setButtonLabel(ADD_LABEL);
    }

    public void editRequest(ScriptStep scriptStep) {
        this.step = scriptStep;
        step.setType(ScriptConstants.REQUEST);
        requestHeaderEditor.editRequestHeaders(step.getRequestheaders());
        responseHeaderEditor.editResponseHeaders(step.getResponseheaders());
        requestCookiesEditor.editRequestCookies(step.getRequestCookies());
        responseCookiesEditor.editResponseCookies(step.getResponseCookies());
        responseContentEditor.editResponseContent(step.getResponseData());
        postDataEditor.editPostData(step.getPostDatas());
        queryStringEditor.editQueryStrings(step.getQueryStrings());
        setupOnFail();
        setButtonLabel(EDIT_LABEL);
        editMode = true;
    }

    private void setupOnFail() {
        scriptEditor.populateGroupList();
        String onFail = step.getOnFail();
        if (onFail.contains("goto")) {
            String[] onfailure = onFail.split(" ");
            if (onfailure.length > 1) {
                gotoGroup = onfailure[1];
            } else {
                gotoGroup = "";
            }
        }
    }

    public String getOnFail() {
        String onFail = "";
        if (step.getOnFail().startsWith("goto")) {
            onFail = step.getOnFail().substring(0, 4);
        } else {
            onFail = step.getOnFail();
        }
        return onFail;
    }

    public void setOnFail(String onFail) {
        step.setOnFail(onFail);
    }

    /**
     * @return the gotoGroup
     */
    public String getGotoGroup() {
        if (step.getOnFail().startsWith("goto")) {
            if (step.getOnFail().length() > 4) {
                gotoGroup = step.getOnFail().substring(5, step.getOnFail().length());
            } else {
                gotoGroup = "";
            }

        }
        return gotoGroup;
    }

    /**
     * @param gotoGroup
     *            the gotoGroup to set
     */
    public void setGotoGroup(String gotoGroup) {
        this.gotoGroup = gotoGroup;
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

    public FailureTypes[] getFailureValues() {
        FailureTypes[] failureTypes = FailureTypes.values();
        return failureTypes;
        // return new String[] { "Abort Script, goto Next Script", "Continue to Next Request",
        // "Skip remaining Requests in Group", "Goto Group", "Terminate User" };
    }

    public void addToScript() {
        if (editMode) {
            done();
        } else {
            insert();
        }
    }

    public void insert() {
        done();
        scriptEditor.insert(step);
    }

    public void done() {
        step.setRequestheaders(requestHeaderEditor.getRequestDataSet());
        step.setResponseheaders(responseHeaderEditor.getRequestDataSet());
        step.setRequestCookies(requestCookiesEditor.getRequestDataSet());
        step.setResponseCookies(responseCookiesEditor.getRequestDataSet());
        step.setResponseData(responseContentEditor.getRequestDataSet());
        step.setPostDatas(postDataEditor.getRequestDataSet());
        step.setQueryStrings(queryStringEditor.getRequestDataSet());

        if (getOnFail().equals(FailureTypes.gotoGroupRequest.getValue())) {
            step.setOnFail(step.getOnFail() + " " + gotoGroup);
        }
        scriptEditor.populateGroupList();
        ScriptUtil.updateStepLabel(step);
    }

    public RepresentationEntity[] getRequestFormats() {
        RepresentationEntity keyValue = new RepresentationEntity("Key-Value", ScriptConstants.NVP_TYPE);
        RepresentationEntity xml = new RepresentationEntity("XML", ScriptConstants.XML_TYPE);
        RepresentationEntity json = new RepresentationEntity("JSON", ScriptConstants.JSON_TYPE);
        RepresentationEntity multiPart = new RepresentationEntity("Multi-Part", ScriptConstants.MULTI_PART_TYPE);
        RepresentationEntity plainText = new RepresentationEntity("Plain Text", ScriptConstants.PLAIN_TEXT_TYPE);

        RepresentationEntity[] entities = new RepresentationEntity[] { keyValue, xml, json, plainText, multiPart };
        return entities;
    }

    public RepresentationEntity[] getResponseFormats() {
        RepresentationEntity jsonValue = new RepresentationEntity("JSON", "json");
        RepresentationEntity raw = new RepresentationEntity("RAW", "raw");
        RepresentationEntity xml = new RepresentationEntity("XML", "xml");

        RepresentationEntity[] entities = new RepresentationEntity[] { jsonValue, raw, xml };
        return entities;
    }

}
