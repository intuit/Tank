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

import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Summary
 * 
 * @author wlee5
 */
public class ProjectServiceUrlBuilderTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetScriptXmlUrl() throws Exception {
        assertEquals(ProjectServiceUrlBuilder.getScriptXmlUrl("1"), new TankConfig().getControllerBase()
                + "/v2/jobs/script/1");
    }
}
