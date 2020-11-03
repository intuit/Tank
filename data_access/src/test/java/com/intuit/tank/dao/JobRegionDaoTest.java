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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.JobRegion;
import com.intuit.tank.test.TestGroups;

public class JobRegionDaoTest {

	private JobRegionDao dao;
	
	@BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();
        dao = new JobRegionDao();
    }
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCleanRegions() throws Exception {
		JobRegion jobRegion3 = DaoTestUtil.createJobRegionData("TestUser_1");
		jobRegion3.setCreated(new Date());
		
		JobRegion jobRegion2 = DaoTestUtil.createJobRegionData("TestUser_1");
		jobRegion2 = dao.saveOrUpdate(jobRegion2);
		
		JobRegion jobRegion1 = DaoTestUtil.createJobRegionData("TestUser_1");
		jobRegion1.setCreated(new Date());
		jobRegion1 = dao.saveOrUpdate(jobRegion1);
		
		
		List<JobRegion> jobRegions = new ArrayList<JobRegion>(2);
		jobRegions.add(jobRegion1);
		jobRegions.add(jobRegion3);
		
		Set<JobRegion> regions = dao.cleanRegions(jobRegions);
		assertEquals(1,  regions.size());
		
	}
}
