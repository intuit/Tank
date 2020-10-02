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
		User u = DaoTestUtil.createUserData("TestUser1", "TestUser1_Password", "TestUser1@intuit.com");
		u = dao.saveOrUpdate(u);
		
		//Authenticate user with valid credentials
		User result = dao.authenticate("TestUser1", "TestUser1_Password");
		assertEquals(u.getId(), result.getId());
		
		//Authenticate user with invalid credentials
		result = dao.authenticate("TestUser1", "Wrong_Password");
		assertEquals(null, result);
				
		//Find user by valid API Token
		result = dao.findByApiToken(u.getApiToken());
		assertEquals(u.getId(), result.getId());
		
		//Find user by invalid API Token
		result = dao.findByApiToken("dummy_token");
		assertEquals(null, result);
		
		//Find user by invalid username
		result = dao.findByUserName("invalid_username");
		assertEquals(null, result);
		
		dao.delete(u);
	}
	
}
