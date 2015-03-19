package com.intuit.tank.vm.api.service.v1.project;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.vm.api.service.v1.project.ProjectServiceUrlBuilder;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.test.TestGroups;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Summary
 * 
 * @author wlee5
 */
public class ProjectServiceUrlBuilderTest {

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testGetScriptXmlUrl() throws Exception {
        Assert.assertEquals(ProjectServiceUrlBuilder.getScriptXmlUrl("1"), new TankConfig().getControllerBase()
                + "/rest/v1/project-service/script/1");
    }
}
