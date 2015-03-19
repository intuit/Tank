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

import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

/**
 * 
 * @author Rogan Dawes
 */
public class ChunkedOutputStream extends FilterOutputStream {

    boolean closed = false;

    private int maxChunkSize = Integer.MAX_VALUE;

    public ChunkedOutputStream(OutputStream out) {
        super(out);
    }

    public ChunkedOutputStream(OutputStream out, int chunkSize) {
        super(out);
        maxChunkSize = chunkSize;
    }

    @Override
    public void write(int b) throws IOException {
        out.write("1\r\n".getBytes());
        out.write(b);
        out.write("\r\n".getBytes());
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        do {
            int chunk = Math.min(len, maxChunkSize);
            out.write((Integer.toString(chunk, 16) + "\r\n").getBytes());
            out.write(b, off, chunk);
            out.write("\r\n".getBytes());
            off += chunk;
            len -= chunk;
        } while (len > 0);
    }

    @Override
    public void close() throws IOException {
        if (closed)
            return;
        out.write("0\r\n\r\n".getBytes());
        flush();
        super.close();
    }

}
