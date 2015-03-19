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

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;

@Named
@ConversationScoped
public class CookieStepEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private String groupName;
    private String name;
    private String value;
    private String domain;
    private String path;
    private ScriptStep step;
    private String buttonLabel = ADD_LABEL;

    private boolean editMode;

    public void editCookieStep(ScriptStep step) {
        this.step = step;
        this.editMode = true;
        this.groupName = step.getScriptGroupName();
        for (RequestData requestData : step.getData()) {
            if (ScriptConstants.COOKIE_NAME.equals(requestData.getKey())) {
                name = requestData.getValue();
            }
            if (ScriptConstants.COOKIE_VALUE.equals(requestData.getKey())) {
                value = requestData.getValue();
            }
            if (ScriptConstants.COOKIE_DOMAIN.equals(requestData.getKey())) {
                domain = requestData.getValue();
            }
            if (ScriptConstants.COOKIE_PATH.equals(requestData.getKey())) {
                path = requestData.getValue();
            }
        }
        buttonLabel = EDIT_LABEL;
    }

    public void insertCookieStep() {
        this.editMode = false;
        name = "";
        value = "";
        domain = "";
        groupName = "";
        path = "/";
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
        ScriptStep newStep = ScriptStepFactory.createCookie(name, value, domain, path);
        newStep.setScriptGroupName(groupName);
        scriptEditor.insert(newStep);
        name = null;
        value = null;
        domain = null;
        groupName = null;
        path = null;
    }

    public void done() {
        ScriptStep newStep = ScriptStepFactory.createCookie(name, value, domain, path);
        step.setScriptGroupName(groupName);
        step.setData(newStep.getData());
        ScriptUtil.updateStepLabel(step);
        groupName = null;
        domain = null;
        value = null;
        path = null;
        name = null;
    }

    private boolean validate() {
        boolean retVal = true;
        if (StringUtils.isBlank(name)) {
            retVal = false;
            messages.error("Cookie Name cannot be empty");
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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName
     *            the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain
     *            the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

}
