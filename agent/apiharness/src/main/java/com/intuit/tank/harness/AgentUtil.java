package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;

public class AgentUtil {
    private static Logger LOG = LogManager.getLogger(AgentUtil.class);

   
    /**
     * Verify that the testplans exist
     * 
     * @param plans
     *            The list of test plans
     * @return TRUE if all test plans exist; FALSE if one or more do not exist
     */
    public static boolean validateTestPlans(String plans) {
        return Arrays.stream(plans.split(","))
                .allMatch(xmlFilePath -> {
                    if (!xmlFilePath.isBlank() && new File(xmlFilePath).exists()) {
                        return true;
                    }
                    LOG.error(LogUtil.getLogMessage("File not found:  " + xmlFilePath));
                    System.err.println("File not found:  " + xmlFilePath);
                    return false;
                });
    }
}
