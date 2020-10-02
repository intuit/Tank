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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.PeriodicData;
import com.intuit.tank.test.TestGroups;

public class PeriodicDataTest {
	private PeriodicDataDao dao;
	
	@BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();
        dao = new PeriodicDataDao();
    }
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFindByJobId() throws Exception {
		PeriodicData first = DaoTestUtil.createPeriodicData(1, new Date());
        first = dao.saveOrUpdate(first);
        
        PeriodicData second = DaoTestUtil.createPeriodicData(2, new Date());
        second = dao.saveOrUpdate(second);
        
        PeriodicData third = DaoTestUtil.createPeriodicData(3, new Date());
        third = dao.saveOrUpdate(third);
        
        List<PeriodicData> list = dao.findByJobId(2);
        assertEquals(second.getJobId(), list.get(0).getJobId());
	}
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFindByJobIdForDateRange() throws Exception {
		Date tenDaysBeforeDate = getCustomDate(-10);
        PeriodicData first = DaoTestUtil.createPeriodicData(1, tenDaysBeforeDate);
        first = dao.saveOrUpdate(first);
        
        Date sevenDaysBeforeDate = getCustomDate(-7);
        PeriodicData second = DaoTestUtil.createPeriodicData(2, sevenDaysBeforeDate);
        second = dao.saveOrUpdate(second);
        
        Date fiveDaysBeforeDate = getCustomDate(-5);
        PeriodicData third = DaoTestUtil.createPeriodicData(2, fiveDaysBeforeDate);
        third = dao.saveOrUpdate(third);
        
        
        Date fourDaysBeforeDate = getCustomDate(-4);
        List<PeriodicData> list = dao.findByJobId(2, sevenDaysBeforeDate, fourDaysBeforeDate);
        assertEquals(2, list.size());
        
       //Even though PeriodicData exists with jobId, the date range is outside, so empty list is returned
        list = dao.findByJobId(2, new Date(), new Date());
        assertEquals(0, list.size());
        
        //If both dates are null, then PeriodicData matching the jobIds are returned
        list = dao.findByJobId(2, null,null);
        assertEquals(2, list.size());
	}
	
	private Date getCustomDate(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
}
