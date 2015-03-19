/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao.util;

/*
 * #%L
 * Data Access
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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.vm.settings.TankConfig;

/**
 * ProjectServiceUtil
 * 
 * @author dangleton
 * 
 */
public class ProjectDaoUtil {

    private static final Logger LOG = Logger.getLogger(ProjectDaoUtil.class);

    private ProjectDaoUtil() {

    }

    /**
     * @param job
     * @return
     */
    public static synchronized File getScriptFile(String jobId) {
        File f = createScriptFile(jobId);
        return f;
    }

    /**
     * @param jobId
     * @return
     */
    private static File createScriptFile(String jobId) {
        String parent = new TankConfig().getTmpDir();
        File parentDir = new File(parent);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        File f = new File(parentDir, jobId);
        return f;
    }

    /**
     * 
     * @param jobId
     * @param scriptString
     */
    public static void storeScriptFile(String jobId, String scriptString) {
        try {
            File f = createScriptFile(jobId);
            FileUtils.writeStringToFile(f, scriptString, "UTF-8");
        } catch (IOException e) {
            LOG.error("Erorr writing file: " + e, e);
            throw new RuntimeException(e);
        }
    }
}
