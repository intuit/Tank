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

import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.project.ScriptStep;

/**
 * ProductDaoTest
 * 
 * @author dangleton
 * 
 */
public class ScriptStepDaoTest {

    // private ScriptStepDao dao;
    //
    //
    //
    // @BeforeClass
    // public void configure() {
    // BasicConfigurator.configure();
    // dao = new ScriptStepDao();
    // }
    //
    // @Test(groups = { "functional" })
    // public void testBasicCreateUpdateDelete() throws Exception {
    // List<ScriptStep> all = dao.findAll();
    // int originalSize = all.size();
    // ScriptStep entity = DaoTestUtil.createScriptStep();
    // ScriptStep persisted = dao.saveOrUpdate(entity);
    //
    // validate(entity, persisted, false);
    // entity = dao.findById(persisted.getUuid());
    // entity.setComments("New Comments");
    // persisted = dao.saveOrUpdate(entity);
    // validate(entity, persisted, true);
    //
    // all = dao.findAll();
    // Assert.assertNotNull(all);
    // Assert.assertEquals(originalSize + 1, all.size());
    //
    // all = dao.listWithCriteria();
    // Assert.assertNotNull(all);
    // Assert.assertEquals(originalSize + 1, all.size());
    //
    // // delete it
    // dao.delete(persisted);
    // entity = dao.findById(entity.getId());
    // Assert.assertNull(entity);
    // all = dao.findAll();
    // Assert.assertEquals(originalSize, all.size());
    // }
    //
    // private void validate(ScriptStep entity1, ScriptStep entity2, boolean checkCreateAttributes) {
    // if (checkCreateAttributes) {
    // Assert.assertEquals(entity1.getUuid(), entity2.getUuid());
    // } else {
    // Assert.assertNotNull(entity2.getUuid());
    // }
    // Assert.assertEquals(entity1.getComments(), entity2.getComments());
    // }

}
