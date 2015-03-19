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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.script.ResponseCookiesEditor;

/**
 * The class <code>ResponseCookiesEditorTest</code> contains tests for the class <code>{@link ResponseCookiesEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ResponseCookiesEditorTest {
    /**
     * Run the ResponseCookiesEditor() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testResponseCookiesEditor_1()
        throws Exception {
        ResponseCookiesEditor result = new ResponseCookiesEditor();
        assertNotNull(result);
    }

    /**
     * Run the void editResponseCookies(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testEditResponseCookies_1()
        throws Exception {
        ResponseCookiesEditor fixture = new ResponseCookiesEditor();
        fixture.setResponseCookies(new LinkedList());
        Set<RequestData> cookies = new HashSet();

        fixture.editResponseCookies(cookies);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseCookiesEditor.setResponseCookies(ResponseCookiesEditor.java:41)
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
        ResponseCookiesEditor fixture = new ResponseCookiesEditor();
        fixture.setResponseCookies(new LinkedList());

        Set<RequestData> result = fixture.getRequestDataSet();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseCookiesEditor.setResponseCookies(ResponseCookiesEditor.java:41)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getResponseCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetResponseCookies_1()
        throws Exception {
        ResponseCookiesEditor fixture = new ResponseCookiesEditor();
        fixture.setResponseCookies(null);

        List<RequestData> result = fixture.getResponseCookies();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseCookiesEditor.setResponseCookies(ResponseCookiesEditor.java:41)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getResponseCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetResponseCookies_2()
        throws Exception {
        ResponseCookiesEditor fixture = new ResponseCookiesEditor();
        fixture.setResponseCookies(new LinkedList());

        List<RequestData> result = fixture.getResponseCookies();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseCookiesEditor.setResponseCookies(ResponseCookiesEditor.java:41)
        assertNotNull(result);
    }

    /**
     * Run the void setResponseCookies(List<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetResponseCookies_1()
        throws Exception {
        ResponseCookiesEditor fixture = new ResponseCookiesEditor();
        fixture.setResponseCookies(new LinkedList());
        List<RequestData> responseCookies = new LinkedList();

        fixture.setResponseCookies(responseCookies);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.ResponseCookiesEditor.setResponseCookies(ResponseCookiesEditor.java:41)
    }
}