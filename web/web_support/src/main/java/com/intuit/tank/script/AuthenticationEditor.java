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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import com.intuit.tank.util.Messages;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;

@Named
@ConversationScoped
public class AuthenticationEditor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private String userName;
    private String password;
    private String realm;
    private String scheme;
    private String host;
    private String port;
    
    private List<String> authSchemes = new ArrayList<String>();

    private String buttonLabel = ADD_LABEL;

    private ScriptStep step;

    private boolean editMode;
    
    @PostConstruct
    public void init() {
        for (AuthScheme s : AuthScheme.values()) {
            authSchemes.add(s.name());
        }
    }
    
    

    /**
     * @return the authSchemes
     */
    public List<String> getAuthSchemes() {
        return authSchemes;
    }



    public void editAuthentication(ScriptStep step) {
        this.step = step;
        this.editMode = true;
        for (RequestData requestData : step.getData()) {

            if (ScriptConstants.AUTH_USER_NAME.equals(requestData.getKey())) {
                userName = requestData.getValue();
            } else if (ScriptConstants.AUTH_PASSWORD.equals(requestData.getKey())) {
                password = requestData.getValue();
            } else if (ScriptConstants.AUTH_REALM.equals(requestData.getKey())) {
                realm = requestData.getValue();
            } else if (ScriptConstants.AUTH_SCHEME.equals(requestData.getKey())) {
                scheme = requestData.getValue();
            } else if (ScriptConstants.AUTH_HOST.equals(requestData.getKey())) {
                host = requestData.getValue();
            } else if (ScriptConstants.AUTH_PORT.equals(requestData.getKey())) {
                port = requestData.getValue();
            }

        }
        setButtonLabel(EDIT_LABEL);
    }

    public void insertAuthentication() {
        this.editMode = false;
        clear();
        setButtonLabel(ADD_LABEL);
    }

    private void clear() {
        userName = null;
        password = null;
        realm = null;
        scheme = AuthScheme.Basic.name();
        host = null;
        port = null;
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
        scriptEditor.insert(ScriptStepFactory.createAuthentication(userName, password, realm, AuthScheme.getScheme(scheme), host, port));
    }

    public void done() {
        Set<RequestData> ds = new HashSet<RequestData>();
        RequestData rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_USER_NAME);
        rd.setValue(userName);
        ds.add(rd);

        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_PASSWORD);
        rd.setValue(password);
        ds.add(rd);

        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_REALM);
        rd.setValue(realm);
        ds.add(rd);

        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_SCHEME);
        rd.setValue(scheme);
        ds.add(rd);

        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_HOST);
        rd.setValue(host);
        ds.add(rd);

        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_PORT);
        rd.setValue(port);
        ds.add(rd);

        step.setData(ds);
        step.setComments("Authenticator " + scheme + " " + host);
        ScriptUtil.updateStepLabel(step);
        clear();
    }

    private boolean validate() {
        boolean retVal = true;
        if (StringUtils.isBlank(userName)) {
            retVal = false;
            messages.error("User Name is required.");
        }
        if (StringUtils.isBlank(password)) {
            retVal = false;
            messages.error("Password is required.");
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
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * @param realm
     *            the realm to set
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * @return the scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * @param scheme
     *            the scheme to set
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

}
