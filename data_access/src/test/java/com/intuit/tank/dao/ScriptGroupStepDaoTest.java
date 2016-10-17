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

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.dao.ScriptGroupStepDao;
import com.intuit.tank.project.ScriptGroupStep;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
public class ScriptGroupStepDaoTest {

    private ScriptGroupStepDao dao;

    @BeforeClass
    public void configure() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        dao = new ScriptGroupStepDao();
    }

    @Test(groups = { "functional" })
    public void testBasicCreateUpdateDelete() throws Exception {
        List<ScriptGroupStep> all = dao.findAll();
        int originalSize = all.size();
        ScriptGroupStep entity = DaoTestUtil.createScriptGroupStep(10);
        ScriptGroupStep persisted = dao.saveOrUpdate(entity);

        validateScriptGroupStep(entity, persisted, false);
        entity = dao.findById(persisted.getId());
        entity.setLoop(20);
        persisted = dao.saveOrUpdate(entity);
        validateScriptGroupStep(entity, persisted, true);

        all = dao.findAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(originalSize + 1, all.size());

        all = dao.findAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(originalSize + 1, all.size());

        // delete it
        dao.delete(persisted);
        entity = dao.findById(entity.getId());
        Assert.assertNull(entity);
        all = dao.findAll();
        Assert.assertEquals(originalSize, all.size());
    }

    private void validateScriptGroupStep(ScriptGroupStep entity1, ScriptGroupStep entity2, boolean checkCreateAttributes) {
        if (checkCreateAttributes) {
            Assert.assertEquals(entity1.getId(), entity2.getId());
            Assert.assertEquals(entity1.getCreated(), entity2.getCreated());
            Assert.assertNotSame(entity1.getModified(), entity2.getModified());
        } else {
            Assert.assertNotNull(entity2.getId());
            Assert.assertNotNull(entity2.getCreated());
            Assert.assertNotNull(entity2.getModified());
        }
        Assert.assertEquals(entity1.getLoop(), entity2.getLoop());
    }

}
