package com.intuit.tank.util;

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

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jboss.seam.security.Identity;

import com.intuit.tank.auth.TankUser;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.TankConfig;

public class RestSecurityFilter implements Filter {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RestSecurityFilter.class);

    private TankConfig config;

    @Inject
    private Identity identity;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = new TankConfig();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (config.isRestSecurityEnabled()) {
            User user = getUser((HttpServletRequest) request);
            if (user == null) {
                // send 401 unauthorized and return
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return; // break filter chain, requested JSP/servlet will not be executed
            }
        }
        chain.doFilter(request, response);
    }

    public User getUser(HttpServletRequest req) {
        User user = null;
        // firsttry the session
        if (identity != null) {
            org.picketlink.idm.api.User picketLinkUser = identity.getUser();
            if (picketLinkUser != null && picketLinkUser instanceof TankUser) {
                user = ((TankUser)picketLinkUser).getUserEntity();
            }
        }
        if (user == null) {
            String authHeader = req.getHeader("authorization");
            try {
                if (authHeader != null) {
                    String[] split = StringUtils.split(authHeader, ' ');
                    if (split.length == 2) {
                        String s = new String(Base64.decodeBase64(split[1]), "UTF-8");
                        String[] upass = StringUtils.split(s, ":", 2);
                        if (upass.length == 2) {
                            String name = upass[0];
                            String token = upass[1];
                            UserDao userDao = new UserDao();
                            user = userDao.findByApiToken(token);
                            if (user == null || user.getName().equals(name)) {
                                user = userDao.authenticate(name, token);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error getting user: " + e, e);
            }
        }
        return user;
    }

    @Override
    public void destroy() {

    }

}
