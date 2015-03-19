package com.intuit.tank.script.replace;

/*
 * #%L
 * JSF Support Beans
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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.replace.MimeTypeReplacement;
import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.script.replace.SearchMode;

/**
 * The class <code>MimeTypeReplacementTest</code> contains tests for the class <code>{@link MimeTypeReplacement}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:32 PM
 */
public class MimeTypeReplacementTest {
    /**
     * Run the MimeTypeReplacement() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testMimeTypeReplacement_1()
        throws Exception {

        MimeTypeReplacement result = new MimeTypeReplacement();

        assertNotNull(result);
        assertEquals("request", result.getType());
    }

    /**
     * Run the List<ReplaceEntity> getReplacements(ScriptStep,String,String,SearchMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacements_1()
        throws Exception {
        MimeTypeReplacement fixture = new MimeTypeReplacement();
        ScriptStep step = new ScriptStep();
        String searchQuery = "";
        String replaceString = "";
        SearchMode searchMode = SearchMode.all;

        List<ReplaceEntity> result = fixture.getReplacements(step, searchQuery, replaceString, searchMode);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void replace(ScriptStep,String,String,ReplaceMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplace_1()
        throws Exception {
        MimeTypeReplacement fixture = new MimeTypeReplacement();
        ScriptStep step = new ScriptStep();
        String replaceString = "";
        String key = "";
        ReplaceMode replaceMode = ReplaceMode.KEY;

        fixture.replace(step, replaceString, key, replaceMode);

    }
}