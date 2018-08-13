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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.owasp.proxy.io.ChunkedInputStream;
import org.owasp.proxy.io.ChunkedOutputStream;

public class ChunkedInputStreamTest {

    private static Logger logger = Logger.getAnonymousLogger();

    private InputStream is;

    private String sample = "F ; extension=value\r\n" + "123456789ABCDEF\r\n"
            + "E\r\n" + "123456789ABCDE\r\n" + "D; extension=value\r\n"
            + "123456789ABCD\r\n" + "0; extension=\"value\"\r\n" + "\r\n";

    private String result = "123456789ABCDEF" + "123456789ABCDE"
            + "123456789ABCD";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRead() throws Exception {
        is = new ChunkedInputStream(new ByteArrayInputStream(sample.getBytes()));
        try {
            StringBuilder buff = new StringBuilder();
            int got;
            while ((got = is.read()) > -1)
                buff.append((char) got);
            assertEquals(result, buff.toString());
        } catch (IOException ioe) {
            fail("Exception unexpected!" + ioe);
            ioe.printStackTrace();
        }
    }

    @Test
    public void testReadByteArrayIntInt() throws Exception {
        is = new ChunkedInputStream(new ByteArrayInputStream(sample.getBytes()));
        try {
            StringBuilder buff = new StringBuilder();
            byte[] b = new byte[12];
            int got;
            while ((got = is.read(b, 2, 7)) > -1)
                buff.append(new String(b, 2, got));
            assertEquals(result, buff.toString());
        } catch (IOException ioe) {
            fail("Exception unexpected!" + ioe);
            ioe.printStackTrace();
        }
    }

    @Test
    public void testReadLargeStream() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChunkedOutputStream out = new ChunkedOutputStream((baos));
        byte[] buff = new byte[1786]; // odd random number not a power of 2
        for (int c = 0; c < 5; c++) {
            for (int i = 0; i < buff.length; i++) {
                buff[i] = (byte) ((c * buff.length + i) % 26 + 'A');
            }
            out.write(buff);
        }
        out.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        is = new ChunkedInputStream(bais);
        buff = new byte[1024]; // different size to previous
        int got;
        int total = 0;
        while ((got = is.read(buff)) > 0) {
            logger.fine("Read " + got + " bytes");
            // verify expectation
            for (int i = 0; i < got; i++) {
                assertEquals(
                        (byte) ((total + i) % 26 + 'A'), buff[i], "byte " + (total + i) + " different!");
            }
            total = total + got;
        }
    }

    @Test
    public void testRaw() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(sample.getBytes());
        ChunkedInputStream raw = new ChunkedInputStream(bais, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4];
        int got;
        while ((got = raw.read(buff)) > -1)
            baos.write(buff, 0, got);
        byte[] result = baos.toByteArray();
        logger.fine(new String(result));
        assertEquals(sample.length(), result.length);
        assertTrue(Arrays.equals(sample.getBytes(), result));
    }
}
