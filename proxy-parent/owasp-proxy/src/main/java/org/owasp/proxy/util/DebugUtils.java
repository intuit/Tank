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

import java.io.PrintStream;

public class DebugUtils {

    private static String dumpLine = "00000000"
            + " 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"
            + " |................|\n";

    public static void writev(PrintStream out, String prefix, byte[] b,
            int off, int len) {
        out.println(prefix + System.currentTimeMillis() + " " + len
                + " bytes starting at " + off);
        write(out, prefix, b, off, len);
    }

    public static void write(PrintStream out, String prefix, byte[] b, int off,
            int len) {
        if (prefix == null)
            prefix = "";
        StringBuilder buff = new StringBuilder(prefix.length()
                + dumpLine.length());
        buff.append(prefix).append(dumpLine);
        int start = prefix.length() + 9;
        int end = off + len;
        String hex;
        char c;
        for (int i = off; i < end; i += 16) {
            buff.replace(start - 9, start - 1, hex(i));
            for (int j = 0; j < 16; j++) {
                if (i + j < off + len) {
                    hex = hex(b[i + j]);
                    c = b[i + j] > 31 && b[i + j] < 127 ? (char) b[i + j] : '.';
                } else {
                    hex = "  ";
                    c = ' ';
                }
                int k = start + j * 3;
                buff.replace(k, k + 2, hex);
                k = start + 48 + 1 + j;
                buff.setCharAt(k, c);
            }
            out.print(buff.toString());
        }
    }

    private static String pad(String s, int len, char f) {
        StringBuilder pad = new StringBuilder(len);
        for (int i = 0; i < len - s.length(); i++) {
            pad.append(f);
        }
        pad.append(s);
        return pad.toString();
    }

    private static String hex(byte b) {
        return pad(Integer.toHexString(b & 0xFF), 2, '0');
    }

    private static String hex(int i) {
        return pad(Integer.toHexString(i), 8, '0');
    }

    public static void main(String[] args) {
        byte[] b = { 0, 1, -1, 127, -128 };
        writev(System.err, ">> ", b, 0, b.length);
    }

}
