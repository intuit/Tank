package com.intuit.tank.transform.scriptGenerator;

/*
 * #%L
 * Common
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

import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.data.Header;
import com.intuit.tank.harness.data.ResponseData;
import com.intuit.tank.project.BaseJob;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.script.RequestDataType;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

/**
 * The class <code>ConverterUtilTest</code> contains tests for the class
 * <code>{@link ConverterUtil}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:36 AM
 */
public class ConverterUtilTest {

    @Test
    public void testParseHost() {
        String hostname = "www.company.com";
        String result = ConverterUtil.extractHost(hostname);
        Assert.assertEquals(hostname, result);

        hostname = "www.company.com:8080";
        result = ConverterUtil.extractHost(hostname);
        Assert.assertEquals("www.company.com", result);

        hostname = "denis:angleton@www.company.com";
        result = ConverterUtil.extractHost(hostname);
        Assert.assertEquals("denis:angleton@www.company.com", result);

        hostname = "denis:angleton@www.company.com:8080";
        result = ConverterUtil.extractHost(hostname);
        Assert.assertEquals("denis:angleton@www.company.com", result);
    }

    @Test
    public void testParsePort() {
        String hostname = "www.company.com";
        String result = ConverterUtil.extractPort(hostname);
        Assert.assertEquals(null, result);

        hostname = "www.company.com:8080";
        result = ConverterUtil.extractPort(hostname);
        Assert.assertEquals("8080", result);

        hostname = "denis:angleton@www.company.com";
        result = ConverterUtil.extractPort(hostname);
        Assert.assertEquals(null, result);

        hostname = "denis:angleton@www.company.com:8080";
        result = ConverterUtil.extractPort(hostname);
        Assert.assertEquals("8080", result);
    }

    /**
     * Run the HDWorkload convertScriptToHdWorkload(Script) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testConvertScriptToHdWorkload_1() throws Exception {
        Script script = new Script();
        script.setSteps(new LinkedList());
        script.setName("");

        HDWorkload result = ConverterUtil.convertScriptToHdWorkload(script);

        assertNotNull(result);
        assertEquals("TestPlan for  (id_0)", result.getName());
        assertEquals("TestPlan for  (id_0)", result.getDescription());
        assertEquals(null, result.getVariables());
    }

    /**
     * Run the HDWorkload convertWorkload(Workload,BaseJob) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testConvertWorkload_1() throws Exception {
        Workload workload = new Workload();
        workload.setName("");
        workload.setParent(new Project());
        workload.setTestPlan(new LinkedList());
        BaseJob job = new JobConfiguration();

        HDWorkload result = ConverterUtil.convertWorkload(workload, job);

        assertNotNull(result);
        assertEquals(" project null (id0)", result.getName());
        assertEquals(" project null (id0)", result.getDescription());
    }

    /**
     * Run the List<Header> getPostData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetPostData_1() throws Exception {
        Set<RequestData> postData = new HashSet();

        List<Header> result = ConverterUtil.getPostData(postData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getPostData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetPostData_2() throws Exception {
        Set<RequestData> postData = new HashSet();

        List<Header> result = ConverterUtil.getPostData(postData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getRequestHeaders(Set<RequestData>,Set<RequestData>)
     * method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetRequestHeaders_1() throws Exception {
        Set<RequestData> headers = new HashSet();
        headers.add(new RequestData("X-Include-Referer", "myValue", RequestDataType.requestHeader.name()));
        headers.add(new RequestData("Referer", "myValueNotIncluded", RequestDataType.requestHeader.name()));
        Set<RequestData> cookies = new HashSet();

        List<Header> result = ConverterUtil.getRequestHeaders(headers, cookies);

        assertNotNull(result);
        assertEquals(2, result.size());
        for (Header h : result) {
            if (h.getKey().equalsIgnoreCase("Referer")) {
                Assert.assertEquals("myValue", h.getValue());
                return;
            }
        }
        Assert.fail("X-Include header not included");
    }

    /**
     * Run the List<Header> getRequestHeaders(Set<RequestData>,Set<RequestData>)
     * method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetRequestHeaders_2() throws Exception {
        Set<RequestData> headers = new HashSet();
        Set<RequestData> cookies = new HashSet();

        List<Header> result = ConverterUtil.getRequestHeaders(headers, cookies);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the List<Header> getRequestHeaders(Set<RequestData>,Set<RequestData>)
     * method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetRequestHeaders_3() throws Exception {
        Set<RequestData> headers = new HashSet();
        Set<RequestData> cookies = new HashSet();

        List<Header> result = ConverterUtil.getRequestHeaders(headers, cookies);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the List<Header> getRequestHeaders(Set<RequestData>,Set<RequestData>)
     * method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetRequestHeaders_4() throws Exception {
        Set<RequestData> headers = new HashSet();
        Set<RequestData> cookies = null;

        List<Header> result = ConverterUtil.getRequestHeaders(headers, cookies);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the List<ResponseData> getResponseData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseData_1() throws Exception {
        Set<RequestData> responseData = new HashSet();

        List<ResponseData> result = ConverterUtil.getResponseData(responseData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ResponseData> getResponseData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseData_2() throws Exception {
        Set<RequestData> responseData = new HashSet();

        List<ResponseData> result = ConverterUtil.getResponseData(responseData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ResponseData> getResponseData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseData_3() throws Exception {
        Set<RequestData> responseData = new HashSet();

        List<ResponseData> result = ConverterUtil.getResponseData(responseData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ResponseData> getResponseData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseData_4() throws Exception {
        Set<RequestData> responseData = new HashSet();

        List<ResponseData> result = ConverterUtil.getResponseData(responseData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ResponseData> getResponseData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseData_5() throws Exception {
        Set<RequestData> responseData = new HashSet();

        List<ResponseData> result = ConverterUtil.getResponseData(responseData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<ResponseData> getResponseData(Set<RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseData_6() throws Exception {
        Set<RequestData> responseData = new HashSet();

        List<ResponseData> result = ConverterUtil.getResponseData(responseData);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getResponseHeaders(Set<RequestData>,Set
     * <RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseHeaders_1() throws Exception {
        Set<RequestData> responseHeaders = new HashSet();
        Set<RequestData> responseCookies = new HashSet();

        List<Header> result = ConverterUtil.getResponseHeaders(responseHeaders, responseCookies);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getResponseHeaders(Set<RequestData>,Set
     * <RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseHeaders_2() throws Exception {
        Set<RequestData> responseHeaders = new HashSet();
        Set<RequestData> responseCookies = new HashSet();

        List<Header> result = ConverterUtil.getResponseHeaders(responseHeaders, responseCookies);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getResponseHeaders(Set<RequestData>,Set
     * <RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseHeaders_3() throws Exception {
        Set<RequestData> responseHeaders = new HashSet();
        Set<RequestData> responseCookies = new HashSet();

        List<Header> result = ConverterUtil.getResponseHeaders(responseHeaders, responseCookies);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getResponseHeaders(Set<RequestData>,Set
     * <RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseHeaders_4() throws Exception {
        Set<RequestData> responseHeaders = new HashSet();
        Set<RequestData> responseCookies = new HashSet();

        List<Header> result = ConverterUtil.getResponseHeaders(responseHeaders, responseCookies);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getResponseHeaders(Set<RequestData>,Set
     * <RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseHeaders_5() throws Exception {
        Set<RequestData> responseHeaders = new HashSet();
        Set<RequestData> responseCookies = new HashSet();

        List<Header> result = ConverterUtil.getResponseHeaders(responseHeaders, responseCookies);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<Header> getResponseHeaders(Set<RequestData>,Set
     * <RequestData>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetResponseHeaders_6() throws Exception {
        Set<RequestData> responseHeaders = new HashSet();
        Set<RequestData> responseCookies = new HashSet();

        List<Header> result = ConverterUtil.getResponseHeaders(responseHeaders, responseCookies);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getWorkloadXML(HDWorkload) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testGetWorkloadXML_1() throws Exception {
        HDWorkload hdWorkload = new HDWorkload();

        String result = ConverterUtil.getWorkloadXML(hdWorkload);

        Assert.assertTrue(result.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
        Assert.assertTrue(result.contains("<ns2:workload xmlns:ns2=\"urn:com/intuit/tank/harness/data/v1\">"));
        Assert.assertTrue(result.contains("<client-class>com.intuit.tank.httpclient3.TankHttpClient3</client-class>"));
        Assert.assertTrue(result.contains("</ns2:workload>"));
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_1() throws Exception {
        String header = "Accept";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_2() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_3() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_4() throws Exception {
        String header = "Content";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(false, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_5() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_6() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_7() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_8() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_9() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_10() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean includedHeader(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIncludedHeader_11() throws Exception {
        String header = "";

        boolean result = ConverterUtil.includedHeader(header);

        assertEquals(true, result);
    }

    /**
     * Run the boolean isAssignment(RequestData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIsAssignment_1() throws Exception {
        RequestData data = new RequestData("", "", "");

        boolean result = ConverterUtil.isAssignment(data);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isAssignment(RequestData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIsAssignment_2() throws Exception {
        RequestData data = new RequestData("", "", "");

        boolean result = ConverterUtil.isAssignment(data);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isAssignment(RequestData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIsAssignment_3() throws Exception {
        RequestData data = new RequestData("", "", "");

        boolean result = ConverterUtil.isAssignment(data);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isAssignment(RequestData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIsAssignment_4() throws Exception {
        RequestData data = new RequestData("", "=", "responseData");

        boolean result = ConverterUtil.isAssignment(data);

        assertEquals(true, result);
    }

    /**
     * Run the boolean isAssignment(RequestData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIsAssignment_5() throws Exception {
        RequestData data = new RequestData("", "", "");

        boolean result = ConverterUtil.isAssignment(data);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isAssignment(RequestData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIsAssignment_6() throws Exception {
        RequestData data = new RequestData("", "", "responseData");

        boolean result = ConverterUtil.isAssignment(data);

        assertEquals(false, result);
    }

    /**
     * Run the boolean isAssignment(RequestData) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testIsAssignment_7() throws Exception {
        RequestData data = new RequestData("", "", "responseData");

        boolean result = ConverterUtil.isAssignment(data);

        assertEquals(false, result);
    }
}