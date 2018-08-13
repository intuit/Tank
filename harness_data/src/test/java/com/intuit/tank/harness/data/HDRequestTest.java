package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.Header;

/**
 * The class <code>HDRequestTest</code> contains tests for the class <code>{@link HDRequest}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDRequestTest {
    /**
     * Run the String getHost() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetHost_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getHost();

        assertEquals("", result);
    }

    /**
     * Run the String getLabel() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetLabel_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getLabel();

        assertEquals("", result);
    }

    /**
     * Run the String getLoggingKey() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetLoggingKey_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getLoggingKey();

        assertEquals("", result);
    }

    /**
     * Run the String getMethod() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetMethod_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getMethod();

        assertEquals("", result);
    }

    /**
     * Run the String getPath() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetPath_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getPath();

        assertEquals("", result);
    }

    /**
     * Run the String getPayload() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetPayload_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getPayload();

        assertEquals("", result);
    }

    /**
     * Run the String getPort() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetPort_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getPort();

        assertEquals("", result);
    }

    /**
     * Run the List<Header> getPostDatas() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetPostDatas_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        List<Header> result = fixture.getPostDatas();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getProtocol() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetProtocol_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getProtocol();

        assertEquals("", result);
    }

    /**
     * Run the List<Header> getQueryString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetQueryString_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        List<Header> result = fixture.getQueryString();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getReqFormat() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetReqFormat_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        String result = fixture.getReqFormat();

        assertEquals("", result);
    }

    /**
     * Run the List<Header> getRequestHeaders() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetRequestHeaders_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");

        List<Header> result = fixture.getRequestHeaders();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setHost(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetHost_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String host = "";

        fixture.setHost(host);

    }

    /**
     * Run the void setLabel(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetLabel_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String label = "";

        fixture.setLabel(label);

    }

    /**
     * Run the void setLoggingKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetLoggingKey_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String loggingKey = "";

        fixture.setLoggingKey(loggingKey);

    }

    /**
     * Run the void setMethod(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetMethod_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String method = "";

        fixture.setMethod(method);

    }

    /**
     * Run the void setPath(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetPath_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String path = "";

        fixture.setPath(path);

    }

    /**
     * Run the void setPayload(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetPayload_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String payload = "";

        fixture.setPayload(payload);

    }

    /**
     * Run the void setPort(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetPort_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String port = "";

        fixture.setPort(port);

    }

    /**
     * Run the void setPostDatas(List<Header>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetPostDatas_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        List<Header> postDatas = new LinkedList();

        fixture.setPostDatas(postDatas);

    }

    /**
     * Run the void setProtocol(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetProtocol_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String protocol = "";

        fixture.setProtocol(protocol);

    }

    /**
     * Run the void setQueryString(List<Header>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetQueryString_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        List<Header> queryString = new LinkedList();

        fixture.setQueryString(queryString);

    }

    /**
     * Run the void setReqFormat(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetReqFormat_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        String reqFormat = "";

        fixture.setReqFormat(reqFormat);

    }

    /**
     * Run the void setRequestHeaders(List<Header>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetRequestHeaders_1()
            throws Exception {
        HDRequest fixture = new HDRequest();
        fixture.setProtocol("");
        fixture.setQueryString(new LinkedList());
        fixture.setRequestHeaders(new LinkedList());
        fixture.setMethod("");
        fixture.setPostDatas(new LinkedList());
        fixture.setReqFormat("");
        fixture.setLoggingKey("");
        fixture.setLabel("");
        fixture.setPort("");
        fixture.setPayload("");
        fixture.setPath("");
        fixture.setHost("");
        List<Header> requestHeaders = new LinkedList();

        fixture.setRequestHeaders(requestHeaders);

    }
}