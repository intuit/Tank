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
import java.util.zip.CRC32;

public class CRC32InputStream extends FilterInputStream {

    private CRC32 crc = new CRC32();

    public CRC32InputStream(InputStream in) {
        super(in);
    }

    public int read() throws IOException {
        int result = super.read();
        if (result > -1)
            crc.update(result);
        return result;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int got = super.read(b, off, len);
        if (got > 0)
            crc.update(b, off, got);
        return got;
    }

    public long getValue() {
        return crc.getValue();
    }

}
