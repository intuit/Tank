/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.config;

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

import org.jboss.seam.faces.rewrite.FacesRedirect;
import org.jboss.seam.faces.security.AccessDeniedView;
import org.jboss.seam.faces.security.LoginView;
import org.jboss.seam.faces.view.config.ViewConfig;
import org.jboss.seam.faces.view.config.ViewPattern;

/**
 * Pages
 * 
 * @author dangleton
 * 
 */
@ViewConfig
public interface Pages {

    static enum Pages1 {

        @FacesRedirect
        @ViewPattern("/admin/*")
        @Admin
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        ADMIN,

        @FacesRedirect
        @ViewPattern("/projects/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @TsLoggedIn
        PRODUCTS,

        @FacesRedirect
        @ViewPattern("/scripts/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @TsLoggedIn
        SCRIPTS,

        @FacesRedirect
        @ViewPattern("/filters/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @TsLoggedIn
        FILTERS,

        @FacesRedirect
        @ViewPattern("/register.xhtml")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @Admin
        USER_REQUEST,

        @FacesRedirect
        @ViewPattern("/agents/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @TsLoggedIn
        AGENTS,

        @FacesRedirect
        @ViewPattern("/datafiles/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @TsLoggedIn
        DATAFILES,

        @FacesRedirect
        @ViewPattern("/report/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @TsLoggedIn
        REPORT,

        @FacesRedirect
        @ViewPattern("/tools/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        @TsLoggedIn
        TOOLS,
        

        @FacesRedirect
        @ViewPattern("/wats-proxy/*")
        @AccessDeniedView("/moved.xhtml")
        @LoginView("/moved.xhtml")
        @DepricatedView
        OLD_CONTEXT

    }

}
