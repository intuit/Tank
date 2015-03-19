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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.owasp.proxy.io.GzipInputStream;

public class GzipInputStreamTest {

    private static Logger logger = Logger.getAnonymousLogger();

    private static byte[] random;

    private static byte[] data;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        random = new byte[16385];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(random);
        data = new byte[16385];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testReadByteArrayIntInt() throws IOException {
        verify(data);
        verify(random);
    }

    private void verify(byte[] test) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = new GZIPOutputStream(baos);
        gzos.write(test);
        gzos.close();
        byte[] gzipped = baos.toByteArray();

        InputStream in = new ByteArrayInputStream(test);
        in = new GzipInputStream(in);
        byte[] buff = new byte[gzipped.length];
        int read = 0, got;
        while ((got = in.read(buff, read, buff.length - read)) > -1) {
            logger.fine("Read " + got);
            read += got;
        }
        compare(gzipped, 0, buff, 0, read);
    }

    private void compare(byte[] a, int ao, byte[] b, int bo, int len) {
        for (int i = 0; i < len; i++) {
            Assert.assertEquals("Unexpected input at position " + (ao + i)
                    + "/" + (bo + i), a[ao + i], b[bo + i]);
        }
    }
}
