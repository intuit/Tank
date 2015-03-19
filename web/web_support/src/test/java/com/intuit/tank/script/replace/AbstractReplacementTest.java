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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.replace.AbstractReplacement;
import com.intuit.tank.script.replace.HostReplacement;
import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.script.replace.SearchMode;
import com.intuit.tank.search.script.Section;

/**
 * The class <code>AbstractReplacementTest</code> contains tests for the class <code>{@link AbstractReplacement}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:32 PM
 */
public class AbstractReplacementTest {
    /**
     * Run the List<ReplaceEntity> getReplacementInKey(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInKey_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInKey(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInKey(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInKey_2()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInKey(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInKey(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInKey_3()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInKey(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInKey(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInKey_4()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInKey(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInKey(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInKey_5()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInKey(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInValue(String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInValue_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";

        List<ReplaceEntity> result = fixture.getReplacementInValue(searchQuery, replaceString, value, type);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInValue(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInValue_2()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInValue(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInValue(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInValue_3()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInValue(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInValue(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInValue_4()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInValue(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInValue(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInValue_5()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInValue(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementInValue(String,String,String,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementInValue_6()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String searchQuery = "";
        String replaceString = "";
        String value = "";
        String type = "";
        String key = "";

        List<ReplaceEntity> result = fixture.getReplacementInValue(searchQuery, replaceString, value, type, key);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementsInRequestData(ScriptStep,String,String,Set<RequestData>,SearchMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementsInRequestData_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        ScriptStep step = new ScriptStep();
        String searchQuery = "";
        String replaceString = "";
        Set<RequestData> requestDatas = new HashSet();
        SearchMode searchMode = SearchMode.all;

        List<ReplaceEntity> result = fixture.getReplacementsInRequestData(step, searchQuery, replaceString, requestDatas, searchMode);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementsInRequestData(ScriptStep,String,String,Set<RequestData>,SearchMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementsInRequestData_2()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        ScriptStep step = new ScriptStep();
        String searchQuery = "";
        String replaceString = "";
        Set<RequestData> requestDatas = new HashSet();
        SearchMode searchMode = SearchMode.all;

        List<ReplaceEntity> result = fixture.getReplacementsInRequestData(step, searchQuery, replaceString, requestDatas, searchMode);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementsInRequestData(ScriptStep,String,String,Set<RequestData>,SearchMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementsInRequestData_3()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        ScriptStep step = new ScriptStep();
        String searchQuery = "";
        String replaceString = "";
        Set<RequestData> requestDatas = new HashSet();
        SearchMode searchMode = SearchMode.all;

        List<ReplaceEntity> result = fixture.getReplacementsInRequestData(step, searchQuery, replaceString, requestDatas, searchMode);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementsInRequestData(ScriptStep,String,String,Set<RequestData>,SearchMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementsInRequestData_4()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        ScriptStep step = new ScriptStep();
        String searchQuery = "";
        String replaceString = "";
        Set<RequestData> requestDatas = new HashSet();
        SearchMode searchMode = SearchMode.all;

        List<ReplaceEntity> result = fixture.getReplacementsInRequestData(step, searchQuery, replaceString, requestDatas, searchMode);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ReplaceEntity> getReplacementsInRequestData(ScriptStep,String,String,Set<RequestData>,SearchMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetReplacementsInRequestData_5()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        ScriptStep step = new ScriptStep();
        String searchQuery = "";
        String replaceString = "";
        Set<RequestData> requestDatas = new HashSet();
        SearchMode searchMode = SearchMode.all;

        List<ReplaceEntity> result = fixture.getReplacementsInRequestData(step, searchQuery, replaceString, requestDatas, searchMode);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Section getSection() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetSection_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();

        Section result = fixture.getSection();

        assertNotNull(result);
        assertEquals("host", result.getValue());
        assertEquals("Host", result.getDisplay());
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();

        String result = fixture.getType();

        assertEquals("request", result);
    }

    /**
     * Run the void replaceInRequestDatas(Set<RequestData>,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceInRequestDatas_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Set<RequestData> requestDatas = new HashSet();
        String replaceString = "";
        String key = "";

        fixture.replaceInRequestDatas(requestDatas, replaceString, key);

    }

    /**
     * Run the void replaceInRequestDatas(Set<RequestData>,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceInRequestDatas_2()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Set<RequestData> requestDatas = new HashSet();
        String replaceString = "";
        String key = "";

        fixture.replaceInRequestDatas(requestDatas, replaceString, key);

    }

    /**
     * Run the void replaceInRequestDatas(Set<RequestData>,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceInRequestDatas_3()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Set<RequestData> requestDatas = new HashSet();
        String replaceString = "";
        String key = "";

        fixture.replaceInRequestDatas(requestDatas, replaceString, key);

    }

    /**
     * Run the void replaceInRequestDatas(Set<RequestData>,String,String,ReplaceMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceInRequestDatas_4()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Set<RequestData> requestDatas = new HashSet();
        String replaceString = "";
        String key = "";
        ReplaceMode replaceMode = ReplaceMode.KEY;

        fixture.replaceInRequestDatas(requestDatas, replaceString, key, replaceMode);

    }

    /**
     * Run the void replaceInRequestDatas(Set<RequestData>,String,String,ReplaceMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceInRequestDatas_5()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Set<RequestData> requestDatas = new HashSet();
        String replaceString = "";
        String key = "";
        ReplaceMode replaceMode = ReplaceMode.KEY;

        fixture.replaceInRequestDatas(requestDatas, replaceString, key, replaceMode);

    }

    /**
     * Run the void replaceInRequestDatas(Set<RequestData>,String,String,ReplaceMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceInRequestDatas_6()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Set<RequestData> requestDatas = new HashSet();
        String replaceString = "";
        String key = "";
        ReplaceMode replaceMode = ReplaceMode.KEY;

        fixture.replaceInRequestDatas(requestDatas, replaceString, key, replaceMode);

    }

    /**
     * Run the void replaceInRequestDatas(Set<RequestData>,String,String,ReplaceMode) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testReplaceInRequestDatas_7()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Set<RequestData> requestDatas = new HashSet();
        String replaceString = "";
        String key = "";
        ReplaceMode replaceMode = ReplaceMode.KEY;

        fixture.replaceInRequestDatas(requestDatas, replaceString, key, replaceMode);

    }

    /**
     * Run the void setSection(Section) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testSetSection_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        Section section = null;

        fixture.setSection(section);

    }

    /**
     * Run the void setType(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:32 PM
     */
    @Test
    public void testSetType_1()
        throws Exception {
        AbstractReplacement fixture = new HostReplacement();
        String type = "";

        fixture.setType(type);

    }
}