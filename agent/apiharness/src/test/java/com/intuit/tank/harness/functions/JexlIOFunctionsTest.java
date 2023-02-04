/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.harness.functions;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.harness.test.CheckedKillScriptException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JexlFileFunctionsTest
 * 
 * @author dangleton
 * 
 */
public class JexlIOFunctionsTest {

    @Test
    public void testJexlIOFunctions_1() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData();;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_2() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData(1);;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_3() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData(1, false);;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_4() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData("testFile");;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_5() {
        JexlIOFunctions functions = new JexlIOFunctions();
        Exception exception = assertThrows(CheckedKillScriptException.class, () -> {
            functions.getCSVData("testFile", 1);;
        });
        String message = exception.getMessage();
        assertTrue(message.contains("Next line in CSV file is null"));
    }

    @Test
    public void testJexlIOFunctions_6() {
        JexlIOFunctions functions = new JexlIOFunctions();
        assertEquals("", functions.getFileData("testFile"));
    }

    @Test
    public void testJexlIOFunctions_7() {
        JexlIOFunctions functions = new JexlIOFunctions();
        assertNotNull(functions.getFileBytes("testFile"));
    }
}
