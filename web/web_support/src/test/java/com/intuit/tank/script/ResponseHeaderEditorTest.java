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
import com.intuit.tank.script.ResponseHeaderEditor;

/**
 * The class <code>ResponseHeaderEditorTest</code> contains tests for the class <code>{@link ResponseHeaderEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ResponseHeaderEditorTest {
    /**
     * Run the void editResponseHeaders(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testEditResponseHeaders_1()
        throws Exception {
        ResponseHeaderEditor fixture = new ResponseHeaderEditor();
        fixture.setResponseHeaders(new LinkedList());
        Set<RequestData> headers = new HashSet();

        fixture.editResponseHeaders(headers);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseHeaderEditor.setResponseHeaders(ResponseHeaderEditor.java:30)
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
        ResponseHeaderEditor fixture = new ResponseHeaderEditor();
        fixture.setResponseHeaders(new LinkedList());

        Set<RequestData> result = fixture.getRequestDataSet();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseHeaderEditor.setResponseHeaders(ResponseHeaderEditor.java:30)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getResponseHeaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetResponseHeaders_1()
        throws Exception {
        ResponseHeaderEditor fixture = new ResponseHeaderEditor();
        fixture.setResponseHeaders(null);

        List<RequestData> result = fixture.getResponseHeaders();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseHeaderEditor.setResponseHeaders(ResponseHeaderEditor.java:30)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getResponseHeaders() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetResponseHeaders_2()
        throws Exception {
        ResponseHeaderEditor fixture = new ResponseHeaderEditor();
        fixture.setResponseHeaders(new LinkedList());

        List<RequestData> result = fixture.getResponseHeaders();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseHeaderEditor.setResponseHeaders(ResponseHeaderEditor.java:30)
        assertNotNull(result);
    }

    /**
     * Run the void setResponseHeaders(List<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetResponseHeaders_1()
        throws Exception {
        ResponseHeaderEditor fixture = new ResponseHeaderEditor();
        fixture.setResponseHeaders(new LinkedList());
        List<RequestData> responseHeaders = new LinkedList();

        fixture.setResponseHeaders(responseHeaders);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseHeaderEditor.setResponseHeaders(ResponseHeaderEditor.java:30)
    }
}