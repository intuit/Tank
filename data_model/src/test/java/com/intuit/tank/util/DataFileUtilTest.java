/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.project.DataFile;
import com.intuit.tank.storage.FileData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DataFileUtilTest
 *
 */
public class DataFileUtilTest {

    @Test
    public void testGetDataFileServiceUrl() {
        String expected = "http://localhost:8080/tank/v2/datafiles/content?id=2&offset=4&lines=10";
        assertEquals(expected, DataFileUtil.getDataFileServiceUrl(2, 4, 10));
    }

    @Test
    public void testGetFileData() {
        DataFile df = new DataFile();
        df.setId(1);
        df.setFileName("testFileName");

        FileData fd = DataFileUtil.getFileData(df);

        assertEquals("1", fd.getPath());
        assertEquals("testFileName", fd.getFileName());
    }

    @Test
    public void testGetNumLines() {
        assertEquals(0, DataFileUtil.getNumLines(new DataFile()));
    }
}
