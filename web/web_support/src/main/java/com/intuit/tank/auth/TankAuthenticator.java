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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;
import org.picketlink.Identity;
import org.picketlink.Identity.AuthenticationResult;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;

import com.intuit.tank.dao.UserDao;

import static org.picketlink.idm.model.basic.BasicModel.*;

/**
 * TankAuthenticator
 * 
 * @author dangleton
 * 
 */
@Named("tsAuthenticator")
@RequestScoped
@PicketLink
public class TankAuthenticator extends BaseAuthenticator implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(TankAuthenticator.class);

    @Inject
    private Identity identity;
    
    @Inject
    IdentityManager identityManager;
    
    @Inject
    RelationshipManager relationshipManager;

    @Inject
    private DefaultLoginCredentials credentials;
    
    @Inject
    private Messages messages;

    @Inject
    @Authenticated
    private Event<User> loginEventSrc;

    private String uri;

    public void authenticate() {
        
        LOG.info("Logging in " + credentials.getUserId());
        if ((credentials.getUserId() == null) || (credentials.getPassword() == null)) {
            messages.error("Invalid username or password");
            setStatus(AuthenticationStatus.FAILURE);
        }
        com.intuit.tank.project.User user = new UserDao().authenticate(credentials.getUserId(), credentials.getPassword());
        if (user != null) {
        	User idmuser = getUser(identityManager,user.getName());
        	if (idmuser == null ) {
        		idmuser = new User(user.getName());
        		idmuser.setId(Integer.toString(user.getId()));
	        	idmuser.setCreatedDate(user.getCreated());
	        	idmuser.setEmail(user.getEmail());
	        	idmuser.setAttribute(new Attribute<String>("name", user.getName()));
	        	identityManager.add(idmuser);
	            for (com.intuit.tank.project.Group g : user.getGroups()) {
	            	Role role = getRole(identityManager, g.getName());
	            	if (role == null) {
	            		role = new Role(g.getName());
	            		identityManager.add(role);
	            	}
	            	grantRole(relationshipManager, idmuser, role);
	            }
        	}
            loginEventSrc.fire(idmuser);
            messages.info("You're signed in as " + idmuser.getLoginName());
            setStatus(AuthenticationStatus.SUCCESS);
            setAccount(idmuser);
            // messages.clear();
            return;
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
        AuthenticationResult result = identity.login();
        if (AuthenticationResult.SUCCESS.equals(result)) {
            if (uri == null || StringUtils.countMatches(uri, "/") <= 1) {
                return "/projects/index.xhtml";
            }
        }
        return uri;
    }
    
    public String logout() {
    	identity.logout();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

}
