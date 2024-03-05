package com.intuit.tank.http;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BaseRequestTest {

    @Test
    public void testGetLogUtil() {
        BaseRequest fixture = new MockBaseRequest(null);
        TankHttpLogger log = fixture.getLogUtil();
        assertNotNull(log);
    }

    @Test
    public void testGetResponse() {
        BaseRequest fixture = new MockBaseRequest(null);
        BaseResponse response = fixture.getResponse();
        assertNull(response);
        MockResponse expected = new MockResponse();
        fixture.setResponse(expected);
        response = fixture.getResponse();
        assertEquals(expected, response);
    }

    @Test
    public void testGetLogMsg() {
        BaseRequest fixture = new MockBaseRequest(null);
        String logMsg = fixture.getLogMsg();
        assertNull(logMsg);
        fixture.logMsg = "testLog";
        logMsg = fixture.getLogMsg();
        assertEquals("testLog", logMsg);
    }

    @Test
    public void testGetContentType() {
        BaseRequest fixture = new MockBaseRequest(null);
        String contentType = fixture.getContentType();
        assertEquals("application/x-www-form-urlencoded", contentType);
        fixture.contentType = "text/html";
        contentType = fixture.getContentType();
        assertEquals("text/html", contentType);
    }

    @Test
    public void testGetHttpclient() {
        TankHttpClient client = new MockTankHttpClient();
        BaseRequest fixture = new MockBaseRequest(client);
        TankHttpClient result = fixture.getHttpclient();
        assertEquals(client, result);
    }

    @Test
    public void testGetNullHttpclient() {
        BaseRequest fixture = new MockBaseRequest(null);
        TankHttpClient client = fixture.getHttpclient();
        assertNull(client);
    }

    @Test
    public void testGetHeaderInformation() {
        BaseRequest fixture = new MockBaseRequest(null);
        Map<String, String> headerInformation = fixture.getHeaderInformation();
        Map<String, String> expected = new HashMap<String, String>();
        assertEquals(expected, headerInformation);
    }

    @Test
    public void testGetRequestUrl() {
        BaseRequest fixture = new MockBaseRequest(null);
        String requestUrl = fixture.getRequestUrl();
        assertNull(requestUrl);
        fixture.requestUrl = "testUrl";
        requestUrl = fixture.getRequestUrl();
        assertEquals("testUrl", requestUrl);
    }

    @Test
    public void testGetTimeStamp() {
        BaseRequest fixture = new MockBaseRequest(null);
        Date timestamp = fixture.getTimeStamp();
        assertNull(timestamp);
        Date testDate = new Date();
        fixture.setTimestamp(testDate);
        assertEquals(testDate, fixture.getTimeStamp());
    }

    @Test
    public void testDoGet() {
        TankHttpClient client = new MockTankHttpClient();
        BaseRequest fixture = new MockBaseRequest(client);
        MockResponse mockResponse = new MockResponse();
        fixture.doGet(mockResponse);
        assertEquals(mockResponse, fixture.response);
    }

    @Test
    public void testDoPut() {
        TankHttpClient client = new MockTankHttpClient();
        BaseRequest fixture = new MockBaseRequest(client);
        MockResponse mockResponse = new MockResponse();
        fixture.doPut(mockResponse);
        assertEquals(mockResponse, fixture.response);
    }

    @Test
    public void testDoDelete() {
        TankHttpClient client = new MockTankHttpClient();
        BaseRequest fixture = new MockBaseRequest(client);
        MockResponse mockResponse = new MockResponse();
        fixture.doDelete(mockResponse);
        assertEquals(mockResponse, fixture.response);
    }

    @Test
    public void testDoOptions() {
        TankHttpClient client = new MockTankHttpClient();
        BaseRequest fixture = new MockBaseRequest(client);
        MockResponse mockResponse = new MockResponse();
        fixture.doOptions(mockResponse);
        assertEquals(mockResponse, fixture.response);
    }

    @Test
    public void testDoPost() {
        TankHttpClient client = new MockTankHttpClient();
        BaseRequest fixture = new MockBaseRequest(client);
        MockResponse mockResponse = new MockResponse();
        fixture.doPost(mockResponse);
        assertEquals(mockResponse, fixture.response);
    }

    @Test
    public void testAddURLVariable() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.addURLVariable("testKey", "testValue");
        Map<String, String> urlVariable = new HashMap<String, String>();
        urlVariable.put("testKey", "testValue");
        assertEquals(urlVariable, fixture.urlVariables);
    }

    @Test
    public void testRemoveURLVariable() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.addURLVariable("testKey", "testValue");
        Map<String, String> urlVariable = new HashMap<String, String>();
        fixture.removeURLVariable("testKey");
        assertEquals(urlVariable, fixture.urlVariables);
    }

    @Test
    public void testAddHeader() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.addHeader("testKey", "testValue");
        Map<String, String> header = new HashMap<String, String>();
        header.put("testKey", "testValue");
        assertEquals(header, fixture.headerInformation);
    }

    @Test
    public void testRemoveHeader() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.addHeader("testKey", "testValue");
        Map<String, String> header = new HashMap<String, String>();
        fixture.removeHeader("testKey");
        assertEquals(header, fixture.headerInformation);
    }

    @Test
    public void testSetProtocol() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setProtocol("testProtocol");
        assertEquals("testProtocol", fixture.protocol);
    }

    @Test
    public void testSetHost() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setHost("testHost");
        assertEquals("testHost", fixture.host);
    }

    @Test
    public void testSetIntPort() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setPort(14);
        assertEquals(14, fixture.port);
    }

    @Test
    public void testSetStringPort() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setPort("16");
        assertEquals(16, fixture.port);
    }

    @Test
    public void testSetPath() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setPath("testPath");
        assertEquals("testPath", fixture.path);
    }

    @Test
    public void testSetProxyHost() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setProxyHost("testProxyHost");
        assertEquals("testProxyHost", fixture.proxyHost);
    }

    @Test
    public void testSetBody() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setBody("testBody");
        assertEquals("testBody", fixture.getBody());
    }

    @Test
    public void testGetConnectionTime() {
        BaseRequest fixture = new MockBaseRequest(null);
        long connectionTime = fixture.getConnectionTime();
        assertEquals(0L, fixture.connectionTime);
    }

    @Test
    public void testGetWriteTime() {
        BaseRequest fixture = new MockBaseRequest(null);
        long connectionTime = fixture.getWriteTime();
        assertEquals(0L, fixture.writeTime);
    }

    @Test
    public void testGetReqQueryString() {
        BaseRequest fixture = new MockBaseRequest(null);
        String reqQueryString = fixture.getReqQueryString();
        assertEquals("", reqQueryString);
    }

    @Test
    public void testLogRequest() {
        BaseRequest fixture = new MockBaseRequest(null);
        Map <String, String> headerInformation = new HashMap<>();
        headerInformation.put("testHeaderKey", "testHeaderValue");
        List <String> cookies = new ArrayList<>();
        cookies.add("testCookie");
        fixture.logRequest("testUrl", "testBody", "testMethod", headerInformation,
                                cookies, false);
        assertEquals("REQUEST URL: testMethod testUrl\n" +
                "CONTENT TYPE: application/x-www-form-urlencoded\n" +
                "REQUEST HEADER: testHeaderKey = testHeaderValue\n" +
                "testCookie\n" +
                "REQUEST SIZE: 8\n" +
                "REQUEST BODY: testBody\n", fixture.logMsg);
    }

    @Test
    public void testGetContentTypeCharSet() {
        BaseRequest fixture = new MockBaseRequest(null);
        String contentTypeCharSet = fixture.getContentTypeCharSet();
        assertEquals(null, contentTypeCharSet);

        fixture.setContentTypeCharSet(StandardCharsets.UTF_8.toString());
        contentTypeCharSet = fixture.getContentTypeCharSet();
        assertEquals("UTF-8", contentTypeCharSet);
    }

    @Test
    public void testAsync() {
        BaseRequest fixture = new MockBaseRequest(null);
        fixture.setAsync(true);
        assertTrue(fixture.getAsync());
    }
}
