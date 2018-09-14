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

package org.owasp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.owasp.proxy.util.AsciiString;
import org.owasp.proxy.util.CircularByteBuffer;

public class CircularByteBufferTest {

    private CircularByteBuffer cb = new CircularByteBuffer(16);

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
    public void pushAndRemove() {
        cb.push((byte) 'C');
        cb.push((byte) 'B');
        cb.push((byte) 'A');

        cb.add((byte) 'D');
        cb.add((byte) 'E');

        assertEquals(5, cb.length());

        assertTrue(cb.remove() == (byte) 'A');
        assertTrue(cb.remove() == (byte) 'B');
        assertTrue(cb.remove() == (byte) 'C');
        assertTrue(cb.remove() == (byte) 'D');
        assertTrue(cb.remove() == (byte) 'E');

        assertEquals(0, cb.length());

        cb.push(AsciiString.getBytes("WXYZ"));
        cb.push(AsciiString.getBytes("JKLMNOPQRSTUV"));
        cb.push(AsciiString.getBytes("ABCDEFGHI"));
        byte[] buff = new byte[30];
        assertEquals(26, cb.remove(buff));
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", new String(buff, 0, 26));
    }

    @Test
    public void addAndRemove() {
        cb.add((byte) 'A');
        cb.add((byte) 'B');
        cb.add((byte) 'C');
        assertEquals(3, cb.length());
        assertTrue(cb.remove() == (byte) 'A');
        assertTrue(cb.remove() == (byte) 'B');
        assertTrue(cb.remove() == (byte) 'C');
        String s = "DEFGHIJKLMNOPQRS";
        cb.add(s.getBytes(), 0, s.length());
        assertEquals(cb.length(), s.length());
        byte[] b = new byte[s.length()];
        int got = cb.remove(b, 0, b.length);
        assertEquals(got, s.length());
        assertEquals(new String(b), s);
        s = "TUVWXYZABCDEFGHIJKLMNOPQRS";
        cb.add(s.getBytes(), 0, s.length());
        assertEquals(cb.length(), s.length());
        b = new byte[s.length()];
        got = cb.remove(b, 0, b.length);
        assertEquals(got, s.length());
        assertEquals(new String(b), s);
    }

    @Test
    public void bigChunks() {
        String s16 = "ABCDEFGHIJKLMNOP";
        cb.add(s16.getBytes());
        assertEquals(s16.length(), cb.length());
        byte[] buff = new byte[cb.length()];
        int got = cb.remove(buff);
        assertEquals(s16.length(), got);
        assertTrue(Arrays.equals(s16.getBytes(), buff));

        cb.add((byte) 'Z');
        cb.add((byte) 'Z');
        cb.add((byte) 'Z');
        assertEquals((byte) 'Z', (byte) cb.remove());
        assertEquals((byte) 'Z', (byte) cb.remove());
        assertEquals((byte) 'Z', (byte) cb.remove());

        cb.add(s16.getBytes());
        assertEquals(s16.length(), cb.length());
        buff = new byte[cb.length()];
        got = cb.remove(buff);
        assertEquals(s16.length(), got);
        assertTrue(Arrays.equals(s16.getBytes(), buff));
    }
}
