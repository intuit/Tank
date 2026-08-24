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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.rest.mvc.rest.util.LogDirectory;

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
        Set<String> names = new LinkedHashSet<>();
        for (File root : LogDirectory.candidateRoots()) {
            LOG.info("Scanning log file dir {}", root.getAbsolutePath());
            File[] files = root.listFiles();
            if (files == null) {
                continue;
            }
            for (File file : files) {
                if (file.isFile()) {
                    names.add(file.getName());
                }
            }
        }
        logFiles = new ArrayList<>(names);
        // Surface Tank app logs first so they are easy to find among Tomcat access logs.
        logFiles.sort((left, right) -> {
            int leftRank = tankLogRank(left);
            int rightRank = tankLogRank(right);
            if (leftRank != rightRank) {
                return Integer.compare(leftRank, rightRank);
            }
            return String.CASE_INSENSITIVE_ORDER.compare(left, right);
        });
    }

    private static int tankLogRank(String name) {
        if (name == null) {
            return 3;
        }
        String lower = name.toLowerCase();
        if (lower.equals("tank.log")) {
            return 0;
        }
        if (lower.startsWith("tank") && lower.endsWith(".log")) {
            return 1;
        }
        return 2;
    }

    public String getLogFileUrl() {
        if (StringUtils.isNotBlank(currentLogFile)) {
            String contextPath = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestContextPath();
            // Always use URL path separators; File.separator breaks on Windows.
            return getContextRoot(contextPath) + "v2/logs/"
                    + URLEncoder.encode(currentLogFile, StandardCharsets.UTF_8);
        }
        return null;
    }

    private String getContextRoot(String contextPath) {
        if (!StringUtils.endsWith(contextPath, "/")) {
            contextPath = contextPath + "/";
        }
        return contextPath;
    }

    public String getCurrentLogFile() {
        return currentLogFile;
    }

    public void setCurrentLogFile(String currentLogFile) {
        this.currentLogFile = currentLogFile;
    }

    public int getNumLines() {
        return numLines;
    }

    public void setNumLines(int numLines) {
        this.numLines = numLines;
    }

    public int getPollSeconds() {
        return pollSeconds;
    }

    public void setPollSeconds(int pollSeconds) {
        if (pollSeconds < 0) {
            this.pollSeconds = 0;
        } else if (pollSeconds > 300) {
            this.pollSeconds = 300;
        } else {
            this.pollSeconds = pollSeconds;
        }
    }

    public List<String> getLogFiles() {
        return logFiles;
    }

}
