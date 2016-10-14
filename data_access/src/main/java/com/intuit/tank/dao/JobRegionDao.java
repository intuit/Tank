/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.project.JobRegion;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * JobInstance
 * 
 * @author dangleton
 * 
 */
public class JobRegionDao extends BaseDao<JobRegion> {
    private static final Logger LOG = LogManager.getLogger(JobRegionDao.class);

    /**
     * @param entityClass
     */
    public JobRegionDao() {
        super();
        setReloadEntities(true);
    }

    /**
     * @param jobRegions
     * @return
     */
    public static Set<JobRegion> cleanRegions(Collection<JobRegion> jobRegions) {
        Map<VMRegion, JobRegion> map = new HashMap<VMRegion, JobRegion>();
        for (JobRegion region : jobRegions) {
            JobRegion existing = map.get(region.getRegion());
            if (existing != null) {
                LOG.warn("found duplicte JobRegion = " + region + "; Existing region = " + existing);
                if (existing.getCreated() != null || region.getCreated() != null
                        && region.getCreated().compareTo(existing.getCreated()) < 0) {
                    LOG.warn("duplicte JobRegion = is earlier than existing. keeping existing.");
                    region = existing;
                }
            }
            map.put(region.getRegion(), region);
        }
        return new HashSet<JobRegion>(map.values());
    }

}
