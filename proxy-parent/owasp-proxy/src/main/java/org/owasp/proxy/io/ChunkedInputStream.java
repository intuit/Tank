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

import org.owasp.proxy.util.CircularByteBuffer;

/**
 * This class processes streams encoded according to RFC2616 "chunked" specification.
 * 
 * This class will either return decoded chunks, or will return the original stream, but return EOF when the last chunk
 * and trailer have been read (if raw == true).
 * 
 * @author Rogan Dawes
 */
public class ChunkedInputStream extends FilterInputStream {

    private boolean raw = false;

    private boolean eof = false;

    private byte[] b = new byte[1024];

    private CircularByteBuffer buffer = new CircularByteBuffer(1024);

    public ChunkedInputStream(InputStream in) throws IOException {
        super(in);
        readChunk();
    }

    /**
     * Running with raw = true returns the individual chunks, but still shows eof when the last chunk has been read,
     * rather than continuing into the next message
     * 
     * @param in
     * @param raw
     * @throws IOException
     */
    public ChunkedInputStream(InputStream in, boolean raw) throws IOException {
        super(in);
        this.raw = raw;
        readChunk();
    }

    private int addRaw(int i) {
        if (raw && i != -1) {
            buffer.add((byte) i);
        }
        return i;
    }

    private void readChunk() throws IOException {
        String line = readLine();
        try {
            int semi = line.indexOf(';');
            if (semi > -1)
                line = line.substring(0, semi);
            int size = Integer.parseInt(line.trim(), 16);
            int got, read = 0;
            while (read < size
                    && (got = in.read(b, 0, Math.min(b.length, size - read))) > 0) {
                buffer.add(b, 0, got);
                read = read + got;
            }
            if (read < size)
                throw new IOException(
                        "Unexpected end of stream reading a chunk of " + size);
            if (size > 0) {
                // read the trailing line feed after the chunk body,
                // but before the next chunk size
                if (!"".equals(line = readLine()))
                    throw new IOException(
                            "Unexpected characters reading the trailing CRLF : "
                                    + line);
            } else {
                discardTrailer();
                eof = true;
            }
        } catch (NumberFormatException nfe) {
            IOException ioe = new IOException("Error parsing chunk size from '"
                    + line);
            ioe.initCause(nfe);
            throw ioe;
        }
    }

    private boolean isEmptyBuffer() {
        return buffer == null || buffer.length() == 0;
    }

    public int read() throws IOException {
        if (eof && isEmptyBuffer())
            return -1;
        int i = buffer.remove();
        while (!eof && isEmptyBuffer())
            readChunk();
        return i;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (eof && isEmptyBuffer())
            return -1;
        int got = buffer.remove(b, off, len);
        while (!eof && isEmptyBuffer())
            readChunk();
        return got;
    }

    public int available() throws IOException {
        return buffer == null ? 0 : buffer.length();
    }

    public boolean markSupported() {
        return false;
    }

    private String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        int i = addRaw(in.read());
        while (i > -1 && i != '\r' && i != '\n') {
            line = line.append((char) (i & 0xFF));
            i = addRaw(in.read());
        }
        if (i == '\n') {
            throw new IOException("Unexpected LF, was expecting a CR first");
        } else if (i == '\r') {
            i = addRaw(in.read());
            if (i != '\n')
                throw new IOException("Unexpected character "
                        + Integer.toHexString(i) + ", was expecting 0x0A");
        }
        return line.toString();
    }

    /**
     * This actually discards the trailer, since it is available for use via the raw content, if desired
     * 
     * @throws IOException
     */
    private void discardTrailer() throws IOException {
        while (!"".equals(readLine()))
            ;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterInputStream#close()
     */
    @Override
    public void close() throws IOException {
        // do nothing
    }

}
