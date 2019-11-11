/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

import java.util.List;

import com.intuit.tank.conversation.Cookie;
import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Protocol;
import com.intuit.tank.conversation.Request;
import com.intuit.tank.conversation.Response;
import com.intuit.tank.util.HeaderParser;
import com.intuit.tank.util.KeyValuePair;
import com.intuit.tank.util.HeaderParser.HeaderType;
import com.intuit.tank.test.TestGroups;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HeaderParserTest
 * 
 * @author dangleton
 * 
 */
public class HeaderParserTest {

    private HeaderParser requestParser;
    private HeaderParser responseParser;

    @BeforeEach
    public void setup() throws Exception {
        Request req = new Request();
        req.setFirstLine("GET /b/test?test=bogus HTTP/1.1");
        req.addHeader(
                "Cookie",
                "test; testValue");
        req.addHeader("Host", "test.testdomain.com");
        req.addHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        requestParser = new HeaderParser(HeaderType.Request, req.getFirstLine(), req.getHeaders());

        Response res = new Response();
        res.setFirstLine("HTTP/1.1 200 OK");
        res.addHeader("Content-Length", "43");
        res.addHeader("Content-Type", "image/gif");

        res.addHeader("Set-Cookie", "sdsa_cc=0;Expires=Thu, 01-Jan-1970 00:00:10 GMT;Path=/sdsa;Domain=.testdomain.com;");
        res.addHeader("Set-Cookie", "JSESSIONID=bogus; Path=/services");
        res.addHeader("Date", "Wed, 02 Nov 2011 00:23:59 GMT");
        responseParser = new HeaderParser(HeaderType.Response, res.getFirstLine(), res.getHeaders());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testRequestParser() {
        List<Cookie> cookies = requestParser.getCookies();
        assertFalse(cookies.isEmpty());
        String host = requestParser.getHost();
        assertEquals("test.testdomain.com", host);
        String path = requestParser.getPath();
        assertEquals("/b/test", path);
        List<KeyValuePair> queryParams = requestParser.getQueryParams();
        assertFalse(queryParams.isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testResponseParser() {
        String contentType = responseParser.getContentType();
        assertEquals("image/gif", contentType);
        int contentLength = responseParser.getContentLength();
        assertEquals(43, contentLength);
        int statusCode = responseParser.getStatusCode();
        assertEquals(200, statusCode);
        // Date responseDate = responseParser.getResponseDate();
        // assertNotNull(responseDate);
        String statusMessage = responseParser.getStatusMessage();
        assertEquals("OK", statusMessage);
        List<Cookie> cookies = responseParser.getCookies();
        assertNotNull(cookies);
    }

}
