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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.test.TestGroups;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFileDaoTest {
	private DataFileDao dao;
	
	@BeforeEach
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();
        dao = new DataFileDao();
    }
	
	@Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testStoreDataFile() throws Exception {
		DataFile dataFile = DaoTestUtil.createDataFileData("TestFile");
		
		String initialString = "text";
	    InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
	    
		DataFile result = dao.storeDataFile(dataFile, targetStream);
		assertEquals(dataFile.getFileName(), result.getFileName());
		
		
	}
}
