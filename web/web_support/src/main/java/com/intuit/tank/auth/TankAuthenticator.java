/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.auth;

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

import javax.enterprise.event.Event;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.picketlink.idm.api.User;
import org.picketlink.idm.impl.api.PasswordCredential;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.vm.common.TankConstants;

/**
 * TankAuthenticator
 * 
 * @author dangleton
 * 
 */
@Named("tsAuthenticator")
@ViewScoped
public class TankAuthenticator extends BaseAuthenticator implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(TankAuthenticator.class);

    @Inject
    private Identity identity;

    @Inject
    private Credentials credentials;

    @Inject
    private Messages messages;

    @Inject
    @Authenticated
    private Event<User> loginEventSrc;

    private String uri;

    public void authenticate() {
        LOG.info("Logging in " + credentials.getUsername());
        if ((credentials.getUsername() == null) || (credentials.getCredential() == null)) {
            messages.error("Invalid username or password");
            setStatus(AuthenticationStatus.FAILURE);
        }
        if (credentials.getCredential() instanceof PasswordCredential) {
            com.intuit.tank.project.User user = new UserDao().authenticate(credentials.getUsername(),
                    ((PasswordCredential) credentials.getCredential()).getValue());
            if (user != null) {
                setUser(new TankUser(user));
                for (Group g : user.getGroups()) {
                    identity.addGroup(g.getName(), TankConstants.TANK_GROUP_TYPE);
                    identity.addRole(g.getName(), g.getName(), TankConstants.TANK_GROUP_TYPE);
                }
                loginEventSrc.fire(getUser());
                // messages.info("You're signed in as " + user.getName());
                setStatus(AuthenticationStatus.SUCCESS);
                // messages.clear();
                return;
            }
        }

        messages.error("Invalid username or password");
        setStatus(AuthenticationStatus.FAILURE);

    }

    public void initUri() {
        if (uri == null) {
        	HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            uri = req.getRequestURI();
            uri = uri.replace(req.getContextPath(), "");
            int indexOf = uri.lastIndexOf('/');
            if (indexOf != -1) {
                uri = uri.substring(0, indexOf) + "/index.xhtml";
            }
        }
    }

    public String getUri() {
        initUri();
        return uri;
    }

    public String login() {
        String login = identity.login();
        if (login == "success") {
            if (uri == null || StringUtils.countMatches(uri, "/") <= 1) {
                return "/projects/index.xhtml";
            }
        }
        return uri;
    }

}
