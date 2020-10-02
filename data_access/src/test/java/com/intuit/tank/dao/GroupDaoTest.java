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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.Group;
import com.intuit.tank.test.TestGroups;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupDaoTest {
	private GroupDao dao;
	
	@BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();
        dao = new GroupDao();
    }
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFindByName() throws Exception {
		Group first = DaoTestUtil.createGroupData("testGroup");
		first = dao.saveOrUpdate(first);
		
		//Try to fetch the group which was created
		Group result = dao.findByName("testGroup");
		assertEquals(first.getId(), result.getId());
		
		//Try to fetch a group which was not created, it will return null value
		result = dao.findByName("testGroup1");
		assertEquals(null,result);
		
		dao.delete(first);
	}
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetOrCreateGroup() throws Exception {
		Group first = DaoTestUtil.createGroupData("testGroup");
		first = dao.saveOrUpdate(first);
		
		//Try to create a group which was created earlier, it will not create a new group
		// and will match with the group id 
		Group result = dao.getOrCreateGroup("testGroup");
		assertEquals(first.getId(), result.getId());
		
		//Try to create a group with a new name, it will create a new group
		Group result2 = dao.getOrCreateGroup("testGroup2");
		assertEquals("testGroup2", result2.getName());
		
		dao.delete(first);
		dao.delete(result2);
		
	}
}
