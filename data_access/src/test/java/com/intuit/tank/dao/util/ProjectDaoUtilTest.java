package com.intuit.tank.dao.util;

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

import java.io.File;

import org.junit.jupiter.api.*;

import com.intuit.tank.dao.util.ProjectDaoUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ProjectDaoUtilTest</code> contains tests for the class <code>{@link ProjectDaoUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:17 PM
 */
public class ProjectDaoUtilTest {
    
    /**
     * Run the File getScriptFile(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:17 PM
     */
    @Test
    public void testGetScriptFile_1()
        throws Exception {
        String jobId = "";

        File result = ProjectDaoUtil.getScriptFile(jobId);
        assertNotNull(result);
    }

}