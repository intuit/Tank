package com.intuit.tank.search.script;

/*
 * #%L
 * Script Search
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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.Script;
import com.intuit.tank.search.script.SearchCriteria;
import com.intuit.tank.search.script.Section;

/**
 * The class <code>SearchCriteriaTest</code> contains tests for the class <code>{@link SearchCriteria}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:33 AM
 */
public class SearchCriteriaTest {
    /**
     * Run the SearchCriteria() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testSearchCriteria_1()
            throws Exception {
        SearchCriteria result = new SearchCriteria();
        assertNotNull(result);
    }

    /**
     * Run the List<Section> getCriteria() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testGetCriteria_1()
            throws Exception {
        SearchCriteria fixture = new SearchCriteria();
        fixture.setSearchQuery("");
        fixture.setScript(new Script());

        List<Section> result = fixture.getCriteria();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Script getScript() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testGetScript_1()
            throws Exception {
        SearchCriteria fixture = new SearchCriteria();
        fixture.setSearchQuery("");
        fixture.setScript(new Script());

        Script result = fixture.getScript();

        assertNotNull(result);
        assertEquals(null, result.toString());
        assertEquals(null, result.getName());
        assertEquals(0, result.getRuntime());
        assertEquals(null, result.getProductName());
        assertEquals(null, result.getSerializedScriptStepId());
        assertEquals(null, result.getComments());
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }

    /**
     * Run the String getSearchQuery() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testGetSearchQuery_1()
            throws Exception {
        SearchCriteria fixture = new SearchCriteria();
        fixture.setSearchQuery("");
        fixture.setScript(new Script());

        String result = fixture.getSearchQuery();

        assertEquals("", result);
    }

    /**
     * Run the void setScript(Script) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testSetScript_1()
            throws Exception {
        SearchCriteria fixture = new SearchCriteria();
        fixture.setSearchQuery("");
        fixture.setScript(new Script());
        Script script = new Script();

        fixture.setScript(script);

    }

    /**
     * Run the void setSearchQuery(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:33 AM
     */
    @Test
    public void testSetSearchQuery_1()
            throws Exception {
        SearchCriteria fixture = new SearchCriteria();
        fixture.setSearchQuery("");
        fixture.setScript(new Script());
        String searchQuery = "";

        fixture.setSearchQuery(searchQuery);

    }
}