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

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.intuit.tank.auth.sso.TankSsoHandler;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * TankAuthenticator
 *
 * @author dangleton
 *
 */
@Named("tsAuthenticator")
@RequestScoped
public class TankAuthenticator implements Serializable {

    @NotEmpty
    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;

    @NotEmpty
    @Size(min= 2, message = "Please provide a valid username")
    private String username;

    @Inject
    private TankSecurityContext securityContext;

    @Inject
    private Messages messages;

    @Inject
    private TankConfig _tankConfig;

    @Inject
    private TankSsoHandler _tankSsoHandler;

    public void login() throws IOException {

        switch (continueAuthentication()) {
            case SEND_CONTINUE:
                FacesContext.getCurrentInstance().responseComplete();
                break;
            case SEND_FAILURE:
                messages.error("Invalid username or password");
                break;
            case SUCCESS:
                messages.info("You're signed in as " + username);
                FacesContext.getCurrentInstance().getExternalContext().redirect(
                        FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/projects/index.jsf");
                break;
            case NOT_DONE:
        }
    }

    public void ssoLogin() throws IOException {
        String authorizationRequest = _tankSsoHandler.GetOnLoadAuthorizationRequest();
        FacesContext.getCurrentInstance().getExternalContext().redirect(authorizationRequest);
    }

    private AuthenticationStatus continueAuthentication() {
        try {
            return securityContext.authenticate(
                    (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(),
                    (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse(),
                    AuthenticationParameters.withParams().credential(new UsernamePasswordCredential(username, password))
            );
        } catch (NullPointerException e) {
            return AuthenticationStatus.SEND_FAILURE;
        }
    }

    public boolean isloggedIn() {
        return securityContext.getCallerPrincipal() != null;
    }

    public boolean isssoLogin() {
        OidcSsoConfig oidcSsoConfig = _tankConfig.getOidcSsoConfig();
        return Objects.requireNonNull(oidcSsoConfig).getConfiguration() != null;
    }


    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}