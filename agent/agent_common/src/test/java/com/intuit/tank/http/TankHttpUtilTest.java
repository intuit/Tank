package com.intuit.tank.http;

import static org.junit.jupiter.api.Assertions.*;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intuit.tank.http.binary.BinaryRequest;
import com.intuit.tank.http.binary.BinaryResponse;
import com.intuit.tank.http.json.JsonResponse;
import com.intuit.tank.http.xml.XMLResponse;
import org.junit.jupiter.api.*;

/**
 * The class <code>BaseRequestHandlerTest</code> contains tests for the class <code>{@link TankHttpUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:57 PM
 */
public class TankHttpUtilTest {
    /**
     * Run the BaseRequestHandler() constructor test.
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBaseRequestHandler_1()
        throws Exception {
        TankHttpUtil result = new TankHttpUtil();
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_1()
        throws Exception {
        String protocol = "";
        String host = "";
        int port = 8080;
        String path = "";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_2()
        throws Exception {
        String protocol = "http";
        String host = "www.testdomain.com";
        int port = 80;
        String path = "/index.html";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_3()
        throws Exception {
        String protocol = "https";
        String host = "www.testdomain.com";
        int port = 443;
        String path = "/index.html";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the URL buildUrl(String,String,int,String,Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testBuildUrl_4()
        throws Exception {
        String protocol = "http";
        String host = "www.testdomain.com";
        int port = 8080;
        String path = "/index.html";
        Map<String, String> urlVariables = new HashMap();

        URL result = TankHttpUtil.buildUrl(protocol, host, port, path, urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_1()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_2()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();
        urlVariables.put("val1", "val1");
        urlVariables.put("val2", "val2");
        urlVariables.put("val3", "val3");

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
        assertEquals( "?val3=val3&val2=val2&val1=val1", result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_3()
        throws Exception {
        Map<String, String> urlVariables = new HashMap();
        urlVariables.put("data", "{\"val1\":\"val1\", \"val2\":\"val2\", \"val3\":\"val3\"}");

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
        assertEquals( "?data=%7B%22val1%22%3A%22val1%22%2C+%22val2%22%3A%22val2%22%2C+%22val3%22%3A%22val3%22%7D", result);
    }

    /**
     * Run the String getQueryString(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:57 PM
     */
    @Test
    public void testGetQueryString_6()
        throws Exception {
        Map<String, String> urlVariables = null;

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
    }

    /**
     * Test empty and null url variables.
     * @throws Exception
     */
    @Test
    public void testGetQueryString_7() throws Exception {
        Map<String, String> urlVariables = new HashMap();
        urlVariables.put("val1", "");
        urlVariables.put("val2", null);

        String result = TankHttpUtil.getQueryString(urlVariables);
        assertNotNull(result);
        assertEquals( "?val2&val1", result);
    }

    @Test
    public void testNewResponseObject() {
        BaseResponse result = TankHttpUtil.newResponseObject("test");
        assertEquals(BinaryResponse.class, result.getClass());
        BaseResponse xmlResult = TankHttpUtil.newResponseObject("xml");
        assertEquals(XMLResponse.class, xmlResult.getClass());
        BaseResponse jsonResult = TankHttpUtil.newResponseObject("json");
        assertEquals(JsonResponse.class, jsonResult.getClass());
    }

    @Test
    public void testGetPartsFromBody() {
        BaseRequest request = new BinaryRequest(null, null);
        List<TankHttpUtil.PartHolder> result = TankHttpUtil.getPartsFromBody(request);
        List<TankHttpUtil.PartHolder> expected = new ArrayList<>();
        assertEquals(expected, result);
        request.setBody("test");
        result = TankHttpUtil.getPartsFromBody(request);
        assertEquals(expected, result);
    }

    @Test
    public void testPartHolderHandler() {
        byte[] byteArray = { 'T', 'E', 'S', 'T' };
        TankHttpUtil.PartHolder partHolder = new TankHttpUtil.PartHolder(byteArray, "testHeader");
        assertNotNull(partHolder);
    }

    @Test
    public void testPartHolderGetBody() {
        byte[] byteArray = { 'T', 'E', 'S', 'T' };
        TankHttpUtil.PartHolder partHolder = new TankHttpUtil.PartHolder(byteArray, "testHeader");
        byte[] actual = partHolder.getBody();
        assertEquals(byteArray, actual);
    }

    @Test
    public void testPartHolderGetPartName() {
        byte[] byteArray = { 'T', 'E', 'S', 'T' };
        TankHttpUtil.PartHolder partHolder = new TankHttpUtil.PartHolder(byteArray, "Content-Disposition: form-data; name=\"uploadname1\";\n filename=\"diamond-sword.png\"");
        String actual = partHolder.getPartName();
        assertEquals("uploadname1", actual);
    }

    @Test
    public void testPartHolderGetFileName() {
        byte[] byteArray = { 'T', 'E', 'S', 'T' };
        TankHttpUtil.PartHolder partHolder = new TankHttpUtil.PartHolder(byteArray, "Content-Disposition: form-data; name=\"uploadname1\";\n " +
                "filename=\"diamond-sword.png\"");
        String actual = partHolder.getFileName();
        assertEquals("diamond-sword.png", actual);
    }

    @Test
    public void testPartHolderGetContentType() {
        byte[] byteArray = { 'T', 'E', 'S', 'T' };
        TankHttpUtil.PartHolder partHolder = new TankHttpUtil.PartHolder(byteArray, "Content-Disposition: form-data; name=\"uploadname1\";\n " +
                "filename=\"diamond-sword.png\"");
        String actual = partHolder.getContentType();
        assertEquals("text/plain", actual);
    }

    @Test
    public void testPartHolderGetContentDisposition() {
        byte[] byteArray = { 'T', 'E', 'S', 'T' };
        TankHttpUtil.PartHolder partHolder = new TankHttpUtil.PartHolder(byteArray, "Content-Disposition: form-data; name=\"uploadname1\";\n " +
                "filename=\"diamond-sword.png\"");
        String actual = partHolder.getContentDisposition();
        assertEquals("form-data; name=\"uploadname1\";\n" +
                " filename=\"diamond-sword.png\"", actual);
    }

    @Test
    public void testPartHolderIsContentTypeSet() {
        byte[] byteArray = { 'T', 'E', 'S', 'T' };
        TankHttpUtil.PartHolder partHolder = new TankHttpUtil.PartHolder(byteArray, "Content-Disposition: form-data; name=\"uploadname1\";\n " +
                "filename=\"diamond-sword.png\"");
        boolean actual = partHolder.isContentTypeSet();
        assertFalse(actual);
    }
}
