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
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.User;
import com.intuit.tank.test.TestGroups;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDaoTest {
	
	private UserDao _sut;

	private static final String userNameStub = "TestUser1";
	private static final String userPasswordStub = "TestUser1_Password";
	private static final String userEmailStub = "TestUser1@intuit.com";
	private static final String userGroupStub = "TestGroup";
	
	@BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();
        _sut = new UserDao();
    }
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testUserDao() throws Exception {
		User entity = DaoTestUtil.createUserData(userNameStub, userPasswordStub, userEmailStub, Collections.singleton(userGroupStub));
		entity = _sut.saveOrUpdate(entity);
		
		//Authenticate user with valid credentials
		User result = _sut.authenticate(userNameStub, userPasswordStub);
		assertEquals(entity.getId(), result.getId());
		
		//Authenticate user with invalid credentials
		result = _sut.authenticate(userNameStub, "Wrong_Password");
		assertNull(result);
				
		//Find user by valid API Token
		result = _sut.findByApiToken(entity.getApiToken());
		assertEquals(entity.getId(), result.getId());
		
		//Find user by invalid API Token
		result = _sut.findByApiToken("dummy_token");
		assertNull(result);

		//Find user by valid username
		result = _sut.findByUserName(userNameStub);
		assertEquals(entity.getId(), result.getId());

		//Find user by invalid username
		result = _sut.findByUserName("invalid_username");
		assertNull(result);

		//Find user by valid email
		result = _sut.findByEmail(userEmailStub);
		assertEquals(entity.getId(), result.getId());

		//Find user by invalid username
		result = _sut.findByEmail("invalid_email@intuit.com");
		assertNull(result);
		
		_sut.delete(entity);
	}

	@Test
	@Tag(TestGroups.FUNCTIONAL)
	public void testUserDao_withGroups() throws Exception {
		User entity = DaoTestUtil.createUserData(userNameStub, userPasswordStub, userEmailStub, new HashSet<>(Arrays.asList("Group1", "Group2", "Group3")));
		entity = _sut.saveOrUpdate(entity);

		//Find user by valid username
		User result = _sut.findByUserName(userNameStub);
		assertEquals(3, result.getGroups().size());

		_sut.delete(entity);
	}

	@Test
	@Tag(TestGroups.FUNCTIONAL)
	public void findNames_readsNamesWithoutLoadingGroups() {
		User first = _sut.saveOrUpdate(DaoTestUtil.createUserData("NamesUser1", userPasswordStub, "NamesUser1@intuit.com",
				new HashSet<>(Arrays.asList("NamesGroup1", "NamesGroup2"))));
		User second = _sut.saveOrUpdate(DaoTestUtil.createUserData("NamesUser2", userPasswordStub, "NamesUser2@intuit.com",
				new HashSet<>(Arrays.asList("NamesGroup3", "NamesGroup4"))));
		Statistics statistics = _sut.getEntityManager().getEntityManagerFactory()
				.unwrap(SessionFactory.class).getStatistics();
		statistics.setStatisticsEnabled(true);
		statistics.clear();
		try {
			List<String> names = _sut.findNames();

			assertTrue(names.containsAll(List.of("NamesUser1", "NamesUser2")));
			assertEquals(1, statistics.getPrepareStatementCount());
		} finally {
			statistics.setStatisticsEnabled(false);
			_sut.delete(first);
			_sut.delete(second);
		}
	}

}
