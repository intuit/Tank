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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class CopyInputStreamTest {

    private final static String sample = "This is a test of the CopyInputStream";

    private InputStream in;

    private ByteArrayOutputStream[] copies;

    @Before
    public void setUp() throws Exception {
        in = new ByteArrayInputStream(sample.getBytes());
        copies = new ByteArrayOutputStream[] { new ByteArrayOutputStream(),
                new ByteArrayOutputStream() };
    }

    @Test
    public void testRead() {
        try {
            CopyInputStream cis = new CopyInputStream(in, copies);
            while (cis.read() > -1)
                ;
            assertEquals(sample, new String(copies[0].toByteArray()));
            assertEquals(new String(copies[0].toByteArray()), new String(
                    copies[1].toByteArray()));
            cis.close();
        } catch (IOException ioe) {
            fail("IOException not expected!" + ioe);
            ioe.printStackTrace();
        }
    }

    @Test
    public void testReadByteArray() {
        byte[] buff = new byte[4];
        try {
            CopyInputStream cis = new CopyInputStream(in, copies);
            while (cis.read(buff) > 0)
                ;
            assertEquals(sample, new String(copies[0].toByteArray()));
            assertEquals(new String(copies[0].toByteArray()), new String(
                    copies[1].toByteArray()));
            cis.close();
        } catch (IOException ioe) {
            fail("IOException not expected!" + ioe);
            ioe.printStackTrace();
        }
    }

    @Test
    public void testReadByteArrayIntInt() {
        byte[] buff = new byte[6];
        try {
            CopyInputStream cis = new CopyInputStream(in, copies);
            while (cis.read(buff, 1, 4) > 0)
                ;
            assertEquals(sample, new String(copies[0].toByteArray()));
            assertEquals(new String(copies[0].toByteArray()), new String(
                    copies[1].toByteArray()));
            cis.close();
        } catch (IOException ioe) {
            fail("IOException not expected!" + ioe);
            ioe.printStackTrace();
        }
    }

}
