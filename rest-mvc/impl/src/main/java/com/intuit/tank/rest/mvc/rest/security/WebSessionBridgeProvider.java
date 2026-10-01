/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceInternalServerException;
import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Looks up the {@link WebSessionBridge} CDI bean that the web module provides.
 */
@Component
public class WebSessionBridgeProvider {

    private static final Logger LOGGER = LogManager.getLogger(WebSessionBridgeProvider.class);

    @Autowired
    private ServletContext servletContext;

    public WebSessionBridge get() {
        try {
            return new ServletInjector<WebSessionBridge>().getManagedBean(servletContext, WebSessionBridge.class);
        } catch (Exception e) {
            LOGGER.error("No web session bridge is available", e);
            throw new GenericServiceInternalServerException("auth", "session", e);
        }
    }
}
