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

import java.io.UnsupportedEncodingException;

public class AsciiString {

    public static String create(byte[] buff) {
        if (buff == null)
            return "";
        try {
            return new String(buff, "ASCII");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            return null;
        }
    }

    public static String create(byte[] buff, int off, int len) {
        if (buff == null)
            return "";
        try {
            return new String(buff, off, len, "ASCII");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            return null;
        }
    }

    public static byte[] getBytes(String string) {
        try {
            return string.getBytes("ASCII");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            return null;
        }
    }
}
