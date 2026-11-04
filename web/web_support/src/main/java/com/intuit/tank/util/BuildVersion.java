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

import java.io.InputStream;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.rest.mvc.rest.util.BuildInfo;

@Named
@ApplicationScoped
public class BuildVersion {
    private static final Logger LOG = LogManager.getLogger(BuildVersion.class);

    private BuildInfo buildInfo = BuildInfo.fromManifest(null);

    @PostConstruct
    public void init() {
        InputStream warManifest = null;
        try {
            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            warManifest = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
        } catch (Exception e) {
            LOG.error("Error reading Manifest from war: " + e, e);
        }
        buildInfo = BuildInfo.read(warManifest);
    }

    public String getVersion() {
        return buildInfo.version();
    }

    public Date getBuildDate() {
        return buildInfo.buildDate();
    }
}
