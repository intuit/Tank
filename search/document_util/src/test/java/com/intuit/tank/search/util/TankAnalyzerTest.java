package com.intuit.tank.search.util;

/*
 * #%L
 * DocumentUtil
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.PipedReader;
import java.io.Reader;

import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.util.Version;
import org.junit.*;

import com.intuit.tank.search.util.TankAnalyzer;

import static org.junit.Assert.*;

/**
 * The class <code>TankAnalyzerTest</code> contains tests for the class <code>{@link TankAnalyzer}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:27 AM
 */
public class TankAnalyzerTest {
    /**
     * Run the TAnkAnalyzer(Version) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testTAnkAnalyzer_1()
            throws Exception {
        Version matchVersion = Version.LUCENE_20;

        TankAnalyzer result = new TankAnalyzer(matchVersion);

        assertNotNull(result);
    }

    /**
     * Run the org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents createComponents(String,Reader)
     * method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testCreateComponents_1()
            throws Exception {
        TankAnalyzer fixture = new TankAnalyzer(Version.LUCENE_20);
        String fieldName = "";
        Reader reader = new PipedReader();

        org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents result = fixture.createComponents(
                fieldName, reader);

        assertNotNull(result);
    }
}