package com.intuit.tank.script;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.script.QueryStringEditor;

/**
 * The class <code>QueryStringEditorTest</code> contains tests for the class <code>{@link QueryStringEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class QueryStringEditorTest {
    /**
     * Run the void editQueryStrings(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testEditQueryStrings_1()
        throws Exception {
        QueryStringEditor fixture = new QueryStringEditor();
        fixture.setQueryStrings(new LinkedList());
        Set<RequestData> queryStrings = new HashSet();

        fixture.editQueryStrings(queryStrings);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.QueryStringEditor.setQueryStrings(QueryStringEditor.java:40)
    }

    /**
     * Run the List<RequestData> getQueryStrings() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetQueryStrings_1()
        throws Exception {
        QueryStringEditor fixture = new QueryStringEditor();
        fixture.setQueryStrings(null);

        List<RequestData> result = fixture.getQueryStrings();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.QueryStringEditor.setQueryStrings(QueryStringEditor.java:40)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getQueryStrings() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetQueryStrings_2()
        throws Exception {
        QueryStringEditor fixture = new QueryStringEditor();
        fixture.setQueryStrings(new LinkedList());

        List<RequestData> result = fixture.getQueryStrings();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.QueryStringEditor.setQueryStrings(QueryStringEditor.java:40)
        assertNotNull(result);
    }

    /**
     * Run the Set<RequestData> getRequestDataSet() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetRequestDataSet_1()
        throws Exception {
        QueryStringEditor fixture = new QueryStringEditor();
        fixture.setQueryStrings(new LinkedList());

        Set<RequestData> result = fixture.getRequestDataSet();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.QueryStringEditor.setQueryStrings(QueryStringEditor.java:40)
        assertNotNull(result);
    }

    /**
     * Run the void insertQueryString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testInsertQueryString_1()
        throws Exception {
        QueryStringEditor fixture = new QueryStringEditor();
        fixture.setQueryStrings(new LinkedList());

        fixture.insertQueryString();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.QueryStringEditor.setQueryStrings(QueryStringEditor.java:40)
    }

    /**
     * Run the void removeQueryString(RequestData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testRemoveQueryString_1()
        throws Exception {
        QueryStringEditor fixture = new QueryStringEditor();
        fixture.setQueryStrings(new LinkedList());
        RequestData rd = new RequestData();

        fixture.removeQueryString(rd);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.QueryStringEditor.setQueryStrings(QueryStringEditor.java:40)
    }

    /**
     * Run the void setQueryStrings(List<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetQueryStrings_1()
        throws Exception {
        QueryStringEditor fixture = new QueryStringEditor();
        fixture.setQueryStrings(new LinkedList());
        List<RequestData> queryStrings = new LinkedList();

        fixture.setQueryStrings(queryStrings);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.QueryStringEditor.setQueryStrings(QueryStringEditor.java:40)
    }
}