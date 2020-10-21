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

import com.intuit.tank.project.User;
import com.intuit.tank.test.TestGroups;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserDaoTest {
	
	private UserDao dao;
	
	@BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();
        dao = new UserDao();
    }
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testUserDao() throws Exception {
		User entity = DaoTestUtil.createUserData("TestUser1", "TestUser1_Password", "TestUser1@intuit.com", "TestGroup");
		entity = dao.saveOrUpdate(entity);
		
		//Authenticate user with valid credentials
		User result = dao.authenticate("TestUser1", "TestUser1_Password");
		assertEquals(entity.getId(), result.getId());
		
		//Authenticate user with invalid credentials
		result = dao.authenticate("TestUser1", "Wrong_Password");
		assertNull(result);
				
		//Find user by valid API Token
		result = dao.findByApiToken(entity.getApiToken());
		assertEquals(entity.getId(), result.getId());
		
		//Find user by invalid API Token
		result = dao.findByApiToken("dummy_token");
		assertNull(result);
		
		//Find user by invalid username
		result = dao.findByUserName("invalid_username");
		assertNull(result);
		
		dao.delete(entity);
	}
	
}
