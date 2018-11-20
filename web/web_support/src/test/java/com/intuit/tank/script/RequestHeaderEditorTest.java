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
import com.intuit.tank.script.RequestHeaderEditor;

/**
 * The class <code>RequestHeaderEditorTest</code> contains tests for the class <code>{@link RequestHeaderEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class RequestHeaderEditorTest {
    /**
     * Run the void editRequestHeaders(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEditRequestHeaders_1()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());
        Set<RequestData> requestHeaders = new HashSet();

        fixture.editRequestHeaders(requestHeaders);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
    }

    /**
     * Run the Set<RequestData> getRequestDataSet() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetRequestDataSet_1()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());

        Set<RequestData> result = fixture.getRequestDataSet();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getRequestHeaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetRequestHeaders_1()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(null);

        List<RequestData> result = fixture.getRequestHeaders();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getRequestHeaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetRequestHeaders_2()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());

        List<RequestData> result = fixture.getRequestHeaders();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
        assertNotNull(result);
    }

    /**
     * Run the String getStyle(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetStyle_1()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());
        String key = null;

        String result = fixture.getStyle(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
        assertNotNull(result);
    }

    /**
     * Run the String getStyle(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetStyle_2()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());
        String key = "";

        String result = fixture.getStyle(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
        assertNotNull(result);
    }

    /**
     * Run the String getStyle(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetStyle_3()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());
        String key = "";

        String result = fixture.getStyle(key);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
        assertNotNull(result);
    }

    /**
     * Run the void insertHeader() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testInsertHeader_1()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());

        fixture.insertHeader();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
    }

    /**
     * Run the void removeHeader(RequestData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testRemoveHeader_1()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());
        RequestData rd = new RequestData();

        fixture.removeHeader(rd);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
    }

    /**
     * Run the void setRequestHeaders(List<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetRequestHeaders_1()
        throws Exception {
        RequestHeaderEditor fixture = new RequestHeaderEditor();
        fixture.setRequestHeaders(new LinkedList());
        List<RequestData> requestHeaders = new LinkedList();

        fixture.setRequestHeaders(requestHeaders);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestHeaderEditor.setRequestHeaders(RequestHeaderEditor.java:31)
    }
}