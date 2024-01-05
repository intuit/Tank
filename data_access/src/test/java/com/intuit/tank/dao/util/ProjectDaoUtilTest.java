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
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * The class <code>ProjectDaoUtilTest</code> contains tests for the class <code>{@link ProjectDaoUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:17 PM
 */
public class ProjectDaoUtilTest {
    
    @Test
    public void testGetScriptFile()
        throws Exception {
        String jobId = "";

        File result = ProjectDaoUtil.getScriptFile(jobId);
        assertNotNull(result);
    }

    @Test
    public void testStoreScriptFile() {
        try (MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class)) {
            ProjectDaoUtil.storeScriptFile("1", "STRING TO WRITE");
            fileUtilsMockedStatic.when(() -> FileUtils.writeStringToFile(any(File.class), anyString(), any(Charset.class))).thenThrow(new IOException());
            assertThrows(RuntimeException.class, () -> ProjectDaoUtil.storeScriptFile("1", "STRING TO WRITE"));
        }
    }

}