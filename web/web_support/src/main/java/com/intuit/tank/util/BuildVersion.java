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
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import com.intuit.tank.vm.common.TankConstants;

@Named
@ApplicationScoped
public class BuildVersion {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BuildVersion.class);

    private String version = TankConstants.TANK_BUILD_VERSION;
    private static final String BASE_DATE = "2013-01-15T00:00:00Z";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    
    private Date buildDate;

    @PostConstruct
    public void init() {
        readManifest();
    }

    public String getVersion() {
        return version;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    private void readManifest() {
        Manifest manifest = null;
        try {
        	ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
            manifest = new Manifest(inputStream);
        } catch (Exception e) {
            LOG.error("Error reading Manifest from war: " + e, e);
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
                manifest = new Manifest(inputStream);
            } catch (IOException e1) {
                LOG.error("Error reading Manifest from jar: " + e, e);
            }
        }
        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            if (attributes != null) {
                String timestamp = attributes.getValue("Implementation-Build-timestamp");
                if (timestamp != null) {
                    try {
                        this.buildDate = sdf.parse(timestamp);
                        Date d = sdf.parse(this.BASE_DATE);
                        int buildNum = (int) (buildDate.getTime() - d.getTime()) / 60000;
                        version = version + "-" + buildNum;
                    } catch (ParseException e) {
                        LOG.error("Error parsing date " + timestamp + ": " + e, e);
                        version = version + "-" + timestamp;
                    }
                }
            }
        }
    }
}
