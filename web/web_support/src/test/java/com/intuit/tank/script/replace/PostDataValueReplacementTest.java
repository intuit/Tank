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
import com.intuit.tank.script.replace.PostDataValueReplacement;
import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.script.replace.SearchMode;

/**
 * The class <code>PostDataValueReplacementTest</code> contains tests for the class <code>{@link PostDataValueReplacement}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:32 PM
 */
public class PostDataValueReplacementTest {
    /**
     * Run the PostDataValueReplacement() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testPostDataValueReplacement_1()
        throws Exception {

        PostDataValueReplacement result = new PostDataValueReplacement();

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
        PostDataValueReplacement fixture = new PostDataValueReplacement();
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
        PostDataValueReplacement fixture = new PostDataValueReplacement();
        ScriptStep step = new ScriptStep();
        String replaceString = "";
        String key = "";
        ReplaceMode replaceMode = ReplaceMode.KEY;

        fixture.replace(step, replaceString, key, replaceMode);

    }
}