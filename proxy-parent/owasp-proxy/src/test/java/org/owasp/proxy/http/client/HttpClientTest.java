/*
 * This file is part of the OWASP Proxy, a free intercepting proxy library.
 * Copyright (C) 2008-2010 Rogan Dawes <rogan@dawes.za.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to:
 * The Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.owasp.proxy.http.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.owasp.proxy.http.MutableMessageHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.client.HttpClient;
import org.owasp.proxy.io.ChunkedInputStream;
import org.owasp.proxy.test.TraceServer;
import org.owasp.proxy.util.AsciiString;

public class HttpClientTest {

    private static Logger logger = Logger.getAnonymousLogger();

    private static TraceServer ts = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ts = new TraceServer(9999);
        ts.start();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        ts.stop();
        Thread.sleep(1000);
        assertTrue(ts.isStopped(),"TraceServer shutdown failed!");
    }

    @Test
    public void testFetchResponse() throws Exception {
        HttpClient client = new HttpClient();
        client.connect("localhost", 9999, false);
        String request = "GET /blah/blah?abc=def HTTP/1.0\r\nHost: localhost\r\n\r\n";
        client.sendRequestHeader(request.getBytes());
        byte[] header = client.getResponseHeader();
        logger.fine("Header length: " + header.length);
        int got, read = 0;
        byte[] buff = new byte[1024];
        InputStream is = client.getResponseContent();
        while ((got = is.read(buff)) > 0)
            read = read + got;
        assertEquals(request.length(), read);
    }

    @Test
    public void testContinue() throws Exception {
        HttpClient client = new HttpClient();
        client.connect("localhost", 9999, false);
        byte[] header = AsciiString
                .getBytes("POST /target HTTP/1.1\r\n"
                        + "Host: localhost\r\nContent-Length: 20\r\nExpect: 100-continue\r\n\r\n");
        byte[] content = AsciiString.getBytes("01234567890123456789");
        client.sendRequestHeader(header);
        MutableResponseHeader resp = new MutableResponseHeader.Impl();
        resp.setHeader(client.getResponseHeader());
        assertEquals("Expected continue", "100", resp.getStatus());
        client.sendRequestContent(content);
        resp.setHeader(client.getResponseHeader());
        assertEquals("Expected OK", "200", resp.getStatus());
        InputStream rc = client.getResponseContent();
        assertTrue(Arrays.equals(content, toArray(rc)),"Content is encorrect!");
    }

    private byte[] toArray(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int got;
        while ((got = in.read(buff)) > -1)
            baos.write(buff, 0, got);
        in.close();
        return baos.toByteArray();
    }

    @Test
    @Ignore("needs internet access")
    public void testChunked() throws Exception {
        HttpClient client = new HttpClient();
        client.connect("www.google.co.za", 80, false);
        String request = "GET /search?q=OWASP+Proxy&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:en-US:official&client=firefox-a HTTP/1.1\r\n"
                + "Host : www.google.co.za\r\n\r\n";
        client.sendRequestHeader(request.getBytes());
        byte[] responseHeader = client.getResponseHeader();
        logger.finer(AsciiString.create(responseHeader));
        MutableMessageHeader mh = new MutableMessageHeader.Impl();
        mh.setHeader(responseHeader);
        InputStream is = client.getResponseContent();
        if ("chunked".equalsIgnoreCase(mh.getHeader("Transfer-Encoding")))
            is = new ChunkedInputStream(is);

        byte[] buff = new byte[1024];
        int got;
        while ((got = is.read(buff)) > 0) {
            logger.finer(new String(buff, 0, got));
        }
        is.close();
    }
}
