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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

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
        String fileRoot = "logs";
        File f = new File(fileRoot);
        LOG.info("Log file dir is " + f.getAbsolutePath());
        if (!f.exists()) {
            f = new File("/opt/tomcat/logs");
        }
        try {
            File[] list = f.listFiles();
            for (File file : list) {
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
        String ret = null;
        if (StringUtils.isNotBlank(currentLogFile)) {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                    .getRequest();
            try {
                ret = getContextRoot(req.getContextPath()) + "rest"
                        + ReportService.SERVICE_RELATIVE_PATH + "/"
                        + URLEncoder.encode(currentLogFile, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // never happens stupid exception
            }
        }
        return ret;
    }

    /**
     * @param contextPath
     * @return
     */
    private String getContextRoot(String contextPath) {
        if (!contextPath.endsWith("/")) {
            contextPath = contextPath + "/";
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
