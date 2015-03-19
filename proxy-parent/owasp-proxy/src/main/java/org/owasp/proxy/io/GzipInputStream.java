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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;

public class GzipInputStream extends DeflaterInputStream {
    protected CRC32InputStream crc;

    /*
     * GZIP header magic number.
     */
    private final static int GZIP_MAGIC = 0x8b1f;

    private boolean eof = false;

    private int header = 0;

    private int trailer = -1;

    public GzipInputStream(InputStream in) {
        this(in, 512);
    }

    public GzipInputStream(InputStream in, int size) {
        // this is a bit of a hack. We HAVE to calculate the CRC on
        // uncompressed data, but reading from super gives Deflated data.
        // So wrap the original InputStream in a CRC-calculating InputStream
        // before we call the super.constructor.
        // To get our own reference to it, we have to refer to super.in, and
        // cast it back
        super(new CRC32InputStream(in), new Deflater(
                Deflater.DEFAULT_COMPRESSION, true), size);
        crc = (CRC32InputStream) super.in;
    }

    public int read() throws IOException {
        return super.read();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.zip.DeflaterInputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = 0;
        if (header < HEADER.length) {
            read += addHeader(b, off, len);
        }
        if (!eof) {
            int got;
            while (read < len
                    && (got = super.read(b, off + read, len - read)) > -1) {
                read += got;
            }
            if (read < len)
                eof = true;
        }
        if (eof)
            if (trailer < TRAILER.length) {
                read += addTrailer(b, off + read, len - read);
            } else {
                return -1;
            }
        return read;
    }

    private int addHeader(byte[] b, int off, int len) {
        int l = Math.min(HEADER.length - header, len);
        if (l > 0) {
            System.arraycopy(HEADER, header, b, off, l);
            header += l;
        }
        return l;
    }

    private int addTrailer(byte[] b, int off, int len) throws IOException {
        if (trailer == -1) {
            makeTrailer();
            trailer = 0;
        }
        int l = Math.min(TRAILER.length - trailer, len);
        if (l > 0) {
            System.arraycopy(TRAILER, trailer, b, off, l);
            trailer += l;
        }
        return l;
    }

    /*
     * Writes GZIP member header.
     */

    private final static byte[] HEADER = { (byte) GZIP_MAGIC, // Magic number
            // (short)
            (byte) (GZIP_MAGIC >> 8), // Magic number (short)
            Deflater.DEFLATED, // Compression method (CM)
            0, // Flags (FLG)
            0, // Modification time MTIME (int)
            0, // Modification time MTIME (int)
            0, // Modification time MTIME (int)
            0, // Modification time MTIME (int)
            0, // Extra flags (XFLG)
            0 // Operating system (OS)
    };

    private byte[] TRAILER = new byte[8];

    /*
     * Writes GZIP member trailer to a byte array, starting at a given offset.
     */
    private void makeTrailer() throws IOException {
        writeInt((int) crc.getValue(), TRAILER, 0); // CRC-32 of uncompr. data
        writeInt(def.getTotalIn(), TRAILER, 4); // Number of uncompr. bytes
    }

    /*
     * Writes integer in Intel byte order to a byte array, starting at a given offset.
     */
    private void writeInt(int i, byte[] buf, int offset) throws IOException {
        writeShort(i & 0xffff, buf, offset);
        writeShort((i >> 16) & 0xffff, buf, offset + 2);
    }

    /*
     * Writes short integer in Intel byte order to a byte array, starting at a given offset
     */
    private void writeShort(int s, byte[] buf, int offset) throws IOException {
        buf[offset] = (byte) (s & 0xff);
        buf[offset + 1] = (byte) ((s >> 8) & 0xff);
    }

}
