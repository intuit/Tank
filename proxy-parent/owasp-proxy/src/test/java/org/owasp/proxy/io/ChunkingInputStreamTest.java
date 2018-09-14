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

package org.owasp.proxy.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.testng.Assert;

import org.junit.jupiter.api.Test;
import org.owasp.proxy.io.ChunkedInputStream;
import org.owasp.proxy.io.ChunkingInputStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class ChunkingInputStreamTest {

    private static Logger logger = Logger.getAnonymousLogger();

    private byte[] data;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRead() throws IOException {
        data = new byte[16385];
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) (i % 256);
        InputStream is = new ByteArrayInputStream(data);
        is = new ChunkingInputStream(is);
        is = new ChunkedInputStream(is);
        byte[] buff = new byte[data.length + 1024];
        int read = 0, got;
        while ((got = is.read(buff, read, Math.min(1024, buff.length - read))) > -1) {
            logger.fine("Read " + got);
            read += got;
        }
        Assert.assertEquals(data.length, read);
        compare(data, 0, buff, 0, read);
    }

    private void compare(byte[] a, int ao, byte[] b, int bo, int len) {
        for (int i = 0; i < len; i++) {
            Assert.assertEquals( a[ao + i], b[bo + i],"Unexpected input at position " + (ao + i)
                    + "/" + (bo + i));
        }
    }

}
