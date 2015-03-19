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

import java.io.ByteArrayOutputStream;

public class SizeLimitedByteArrayOutputStream extends ByteArrayOutputStream {

    private int max;

    public SizeLimitedByteArrayOutputStream(int max) {
        super();
        if (max <= 0)
            throw new IllegalArgumentException("max cannot be zero or negative");
        this.max = max;
    }

    public SizeLimitedByteArrayOutputStream(int size, int max) {
        super(size);
        this.max = max;
    }

    @Override
    public void write(int b) throws SizeLimitExceededException {
        if (count < max) {
            super.write(b);
            if (count >= max)
                overflow();
        }
    }

    @Override
    public void write(byte[] b, int off, int len)
            throws SizeLimitExceededException {
        if (count < max) {
            super.write(b, off, len);
            if (count >= max)
                overflow();
        }
    }

    protected void overflow() {
        throw new SizeLimitExceededException(count + ">=" + max);
    }
}
