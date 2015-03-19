package com.intuit.tank.harness.parsers;

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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.parsers.WorkloadParser;

/**
 * The class <code>WorkloadParserTest</code> contains tests for the class <code>{@link WorkloadParser}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class WorkloadParserTest {
    /**
     * Run the WorkloadParser(File) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testWorkloadParser_1()
        throws Exception {
        File xmlFile = new File("src/test/resources/TEST_H.xml");

        WorkloadParser result = new WorkloadParser(xmlFile);
        assertNotNull(result);
    }

    /**
     * Run the WorkloadParser(File) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testWorkloadParser_2()
        throws Exception {
        File xmlFile = new File("src/test/resources/TEST_H.xml");

        WorkloadParser result = new WorkloadParser(xmlFile);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.parsers.WorkloadParser
        assertNotNull(result);
    }

    /**
     * Run the WorkloadParser(String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testWorkloadParser_3()
        throws Exception {
        String xmlFile = FileUtils.readFileToString(new File("src/test/resources/TEST_H.xml"));

        WorkloadParser result = new WorkloadParser(xmlFile);

        assertNotNull(result);
    }

    /**
     * Run the HDWorkload getWorkload() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetWorkload_1()
        throws Exception {
        WorkloadParser fixture = new WorkloadParser(new File("src/test/resources/TEST_H.xml"));

        HDWorkload result = fixture.getWorkload();
        assertNotNull(result);
    }
}