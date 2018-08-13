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

import org.junit.After;
import org.testng.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.util.AsciiString;

public class MutableResponseHeaderTest {

    private static final String cont = "HTTP/1.0 100 Continue\r\n"
            + "Header: Value\r\n" + "\r\n";
    private static final String ok = "HTTP/1.0 200 Ok\r\n"
            + "Content-Type: text/html\r\n" + "\r\n";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test100Continue() throws Exception {
        MutableResponseHeader.Impl resp = new MutableResponseHeader.Impl();
        resp.setHeader(AsciiString.getBytes(cont));
        Assert.assertFalse(resp.has100Continue());
        Assert.assertEquals("100", resp.getStatus());
        resp.setStatus("200");
        Assert.assertEquals("200", resp.getStatus());

        resp.setHeader(AsciiString.getBytes(cont + ok));
        Assert.assertTrue(resp.has100Continue());
        Assert.assertEquals("200", resp.getStatus());
        resp.setHeaderLines(new String[] { "HTTP/1.0 302 Moved",
                "Location: new location", "" });
        Assert.assertTrue(resp.has100Continue());
        Assert.assertEquals("new location", resp.getHeader("Location"));
        System.out.write(resp.getHeader());
    }
}
