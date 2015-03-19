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
import com.intuit.tank.script.RequestCookiesEditor;

/**
 * The class <code>RequestCookiesEditorTest</code> contains tests for the class <code>{@link RequestCookiesEditor}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class RequestCookiesEditorTest {
    /**
     * Run the void editRequestCookies(Set<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEditRequestCookies_1()
        throws Exception {
        RequestCookiesEditor fixture = new RequestCookiesEditor();
        fixture.setRequestCookies(new LinkedList());
        Set<RequestData> requestHeaders = new HashSet();

        fixture.editRequestCookies(requestHeaders);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestCookiesEditor.setRequestCookies(RequestCookiesEditor.java:30)
    }

    /**
     * Run the List<RequestData> getRequestCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRequestCookies_1()
        throws Exception {
        RequestCookiesEditor fixture = new RequestCookiesEditor();
        fixture.setRequestCookies(null);

        List<RequestData> result = fixture.getRequestCookies();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestCookiesEditor.setRequestCookies(RequestCookiesEditor.java:30)
        assertNotNull(result);
    }

    /**
     * Run the List<RequestData> getRequestCookies() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRequestCookies_2()
        throws Exception {
        RequestCookiesEditor fixture = new RequestCookiesEditor();
        fixture.setRequestCookies(new LinkedList());

        List<RequestData> result = fixture.getRequestCookies();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestCookiesEditor.setRequestCookies(RequestCookiesEditor.java:30)
        assertNotNull(result);
    }

    /**
     * Run the Set<RequestData> getRequestDataSet() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRequestDataSet_1()
        throws Exception {
        RequestCookiesEditor fixture = new RequestCookiesEditor();
        fixture.setRequestCookies(new LinkedList());

        Set<RequestData> result = fixture.getRequestDataSet();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestCookiesEditor.setRequestCookies(RequestCookiesEditor.java:30)
        assertNotNull(result);
    }

    /**
     * Run the void insertCookie() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testInsertCookie_1()
        throws Exception {
        RequestCookiesEditor fixture = new RequestCookiesEditor();
        fixture.setRequestCookies(new LinkedList());

        fixture.insertCookie();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestCookiesEditor.setRequestCookies(RequestCookiesEditor.java:30)
    }

    /**
     * Run the void removeCookie(RequestData) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRemoveCookie_1()
        throws Exception {
        RequestCookiesEditor fixture = new RequestCookiesEditor();
        fixture.setRequestCookies(new LinkedList());
        RequestData rd = new RequestData();

        fixture.removeCookie(rd);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestCookiesEditor.setRequestCookies(RequestCookiesEditor.java:30)
    }

    /**
     * Run the void setRequestCookies(List<RequestData>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetRequestCookies_1()
        throws Exception {
        RequestCookiesEditor fixture = new RequestCookiesEditor();
        fixture.setRequestCookies(new LinkedList());
        List<RequestData> cookies = new LinkedList();

        fixture.setRequestCookies(cookies);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.RequestCookiesEditor.setRequestCookies(RequestCookiesEditor.java:30)
    }
}