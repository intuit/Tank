/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.vmManager;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JobUtil
 * 
 * @author dangleton
 * 
 */
public final class JobUtil {

    private static final Logger LOG = LogManager.getLogger(JobUtil.class);

    /**
     * private constructor to implement util
     */
    private JobUtil() {

    }

    /**
     * @param users
     * @param total
     * @return users
     */
    public static final int parseUserString(String users) {
        int result = 0;
        String u = users.trim();
        try {
            result = Integer.valueOf(u);
        } catch (NumberFormatException e) {
            LOG.error("Error parsing number of users value of " + users);
        }
        return result;
    }

    /**
     * calculates total number of virtual users by summing the regions.
     * 
     * @return users
     */
    public static final int calculateTotalVirtualUsers(Collection<? extends RegionRequest> jobRegions) {
        int result = 0;
        for (RegionRequest region : jobRegions) {
            result += JobUtil.parseUserString(region.getUsers());
        }
        return result;
    }

}
