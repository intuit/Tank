/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.admin;

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

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.service.v1.report.ReportService;

/**
 * LogViewer
 * 
 * @author dangleton
 * 
 */
@Named
@RequestScoped
public class LogViewer implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LogViewer.class);

    private String currentLogFile;
    private int numLines = 20;
    private int pollSeconds = 5;
    private List<String> logFiles;

    @PostConstruct
    public void init() {
        logFiles = new ArrayList<String>();
        String fileRoot = System.getProperty("catalina.base")
                + File.separator
                + "logs";
        File f = new File(fileRoot);
        LOG.info("Log file dir is " + f.getAbsolutePath());
        if (!f.exists()) {
            f = new File("/opt/tomcat/logs");
        }
        try {
            for (File file : Objects.requireNonNull(f.listFiles())) {
                if (file.isFile()) {
                    logFiles.add(file.getName());
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting log files: " + e, e);
        }
        logFiles.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public String getLogFileUrl() {
        if (StringUtils.isNotBlank(currentLogFile)) {
            String contextPath = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestContextPath();
            try {
                return getContextRoot(contextPath) + "rest"
                        + ReportService.SERVICE_RELATIVE_PATH + File.separator
                        + URLEncoder.encode(currentLogFile, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                // never happens stupid exception
            }
        }
        return null;
    }

    /**
     * @param contextPath
     * @return
     */
    private String getContextRoot(String contextPath) {
        if (!StringUtils.endsWith(contextPath, File.separator)) {
            contextPath = contextPath + File.separator;
        }
        return contextPath;
    }

    /**
     * @return the currentLogFile
     */
    public String getCurrentLogFile() {
        return currentLogFile;
    }

    /**
     * @param currentLogFile
     *            the currentLogFile to set
     */
    public void setCurrentLogFile(String currentLogFile) {
        this.currentLogFile = currentLogFile;
    }

    /**
     * @return the numLines
     */
    public int getNumLines() {
        return numLines;
    }

    /**
     * @param numLines
     *            the numLines to set
     */
    public void setNumLines(int numLines) {
        this.numLines = numLines;
    }

    /**
     * @return the pollSeconds
     */
    public int getPollSeconds() {
        return pollSeconds;
    }

    /**
     * @param pollSeconds
     *            the pollSeconds to set
     */
    public void setPollSeconds(int pollSeconds) {
        this.pollSeconds = pollSeconds;
    }

    /**
     * @return the logFiles
     */
    public List<String> getLogFiles() {
        return logFiles;
    }

}
