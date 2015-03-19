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

package org.owasp.proxy.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableMessageHeader;
import org.owasp.proxy.http.NamedValue;
import org.owasp.proxy.util.AsciiString;

/**
 * @author Rogan Dawes
 * 
 */
public class MessageHeaderTest {

    private String CRLF = "\r\n";

    private String CRLFCRLF = CRLF + CRLF;

    private String get = "GET / HTTP/1.0";

    private String get3 = "GET / HTTP/1.0\r\nHost: localhost\r\nCookie: a=b";

    private String post = "POST / HTTP/1.0\r\nHost: localhost\r\nCookie: a=b\r\nContent-Length: 10";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#getMessage()}.
     */
    @Test
    public void testGetSetHeader() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(get + CRLFCRLF));
        String s = AsciiString.create(m.getHeader());
        assertEquals(get + CRLFCRLF, s);
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#getStartLine()}.
     */
    @Test
    public void testGetFirstLine() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(get + CRLFCRLF));
        assertEquals(get, m.getStartLine());
        m.setHeader(AsciiString.getBytes(get3 + CRLFCRLF));
        assertEquals(get, m.getStartLine());
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#setStartLine(java.lang.String)} .
     */
    @Test
    public void testSetFirstLine() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setStartLine(get);
        assertEquals(get + CRLFCRLF, AsciiString.create(m.getHeader()));
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#getHeaders()}.
     */
    @Test
    public void testGetHeaders() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(post + CRLFCRLF));
        String result = m.getStartLine() + CRLF
                + NamedValue.join(m.getHeaders(), CRLF) + CRLFCRLF;
        assertEquals(post + CRLFCRLF, result);
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#setHeaders(org.owasp.proxy.http.NamedValue[])}
     * .
     */
    @Test
    public void testSetHeaders() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        String first = post.substring(0, post.indexOf(CRLF));
        NamedValue[] h = NamedValue.parse(post.substring(first.length()
                + CRLF.length()), CRLF, " *: *");
        try {
            m.setHeaders(h);
            fail("Should have thrown an exception here");
        } catch (MessageFormatException mfe) {
            // expected
            m.setHeader(null);
        }
        m.setStartLine(first);
        m.setHeaders(h);
        assertEquals(post + CRLFCRLF, AsciiString.create(m.getHeader()));
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#getHeader(java.lang.String)} .
     */
    @Test
    public void testGetHeaderString() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(post + CRLFCRLF));
        assertEquals("a=b", m.getHeader("cookie"));
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#setHeader(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testSetHeaderStringString() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(post + CRLFCRLF));
        m.setHeader("Cookie", "a=c");
        assertEquals("a=c", m.getHeader("cookie"));
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#addHeader(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testAddHeader() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(post + CRLFCRLF));
        m.addHeader("Cookie", "b=c");
        NamedValue[] headers = m.getHeaders();
        boolean found = false;
        for (int i = 0; i < headers.length; i++)
            if ("cookie".equalsIgnoreCase(headers[i].getName())
                    && "a=b".equals(headers[i].getValue()))
                found = true;
        assertTrue(found);
        found = false;
        for (int i = 0; i < headers.length; i++)
            if ("cookie".equalsIgnoreCase(headers[i].getName())
                    && "b=c".equals(headers[i].getValue()))
                found = true;
        assertTrue(found);
    }

    /**
     * Test method for {@link org.owasp.proxy.http.MutableBufferedMessage#deleteHeader(java.lang.String)} .
     */
    @Test
    public void testDeleteHeader() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(post + CRLFCRLF));
        assertEquals("a=b", m.getHeader("Cookie"));
        m.deleteHeader("cookie");
        assertEquals(null, m.getHeader("cookie"));
    }

    @Test
    public void testHeaderLines() throws Exception {
        MutableMessageHeader.Impl m = new MutableMessageHeader.Impl();
        m.setHeader(AsciiString.getBytes(post + CRLFCRLF));
        assertEquals(5, m.getHeaderLines().length);
        assertEquals(3, m.getHeaders().length);
    }
}
