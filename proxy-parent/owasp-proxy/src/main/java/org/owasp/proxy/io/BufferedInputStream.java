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

/**
 * This class provides an abstract class for InputStreams that need to preprocess their input.
 * 
 * @author Rogan Dawes
 */
public abstract class BufferedInputStream extends FilterInputStream {

    protected byte[] buff;

    protected int start = 0, end = 0;

    public BufferedInputStream(InputStream in) throws IOException {
        this(in, 1024);
    }

    public BufferedInputStream(InputStream in, int size) throws IOException {
        super(in);
        buff = new byte[size];
    }

    /**
     * This method is called whenever the buff has been exhausted. i.e. start >= end. Implementations must update
     * <code>buff</code>, <code>start</code> and <code>end</code> appropriately. If the stream should be closed,
     * implementations must set <code>buff</code> to null to indicate this. Implementations may replace
     * <code>buff</code> with a more appropriately sized one as required.
     * 
     * @return
     * @throws IOException
     */
    protected abstract void fillBuffer() throws IOException;

    private boolean isEmptyBuffer() {
        return start >= end;
    }

    public int read() throws IOException {
        if (isEmptyBuffer()) {
            if (buff == null)
                return -1;
            fillBuffer();
            if (buff == null)
                return -1;
        }

        return buff[start++] & 0xFF;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int got = 0;

        do {
            if (isEmptyBuffer()) {
                if (buff == null)
                    break;
                fillBuffer();
                if (buff == null)
                    break;
            }
            int l = Math.min(end - start, len - got);
            System.arraycopy(buff, start, b, off + got, l);
            start += l;
            got += l;
        } while (buff != null && got < len);
        return got == 0 ? -1 : got;
    }

    public int available() throws IOException {
        return buff == null ? 0 : end - start;
    }

    public boolean markSupported() {
        return false;
    }

}
