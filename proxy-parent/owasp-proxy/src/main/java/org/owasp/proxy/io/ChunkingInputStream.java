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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.owasp.proxy.util.AsciiString;
import org.owasp.proxy.util.CircularByteBuffer;

public class ChunkingInputStream extends FilterInputStream {

    private static byte[] CRLF = { '\r', '\n' };

    private CircularByteBuffer buff;

    private byte[] b;

    private boolean eof = false;

    public ChunkingInputStream(InputStream in) {
        this(in, 1024);
    }

    public ChunkingInputStream(InputStream in, int size) {
        super(in);
        buff = new CircularByteBuffer(size + 8); // 4 digits + CRLF + chunk +
        // CRLF
        b = new byte[size];
    }

    private void fill() throws IOException {
        if (buff.length() > 0 || eof)
            return;
        int got = super.read(b, 0, b.length);
        if (got == -1) {
            buff.add(AsciiString.getBytes("0\r\n\r\n"));
            eof = true;
        } else {
            buff.add(AsciiString.getBytes(Integer.toHexString(got) + "\r\n"));
            buff.add(b, 0, got);
            buff.add(CRLF);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterInputStream#read()
     */
    @Override
    public int read() throws IOException {
        fill();
        if (buff.length() > 0)
            return buff.remove();
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterInputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = 0, got;
        fill();
        while (read < len
                && (got = buff.remove(b, off + read, len - read)) > -1) {
            fill();
            read += got;
        }
        if (len > 0 && read == 0)
            return -1;
        return read;
    }

}
