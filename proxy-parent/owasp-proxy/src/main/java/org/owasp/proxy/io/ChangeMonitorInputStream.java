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
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is intended to provide as efficient a method of tracking changes in a series of filters operating on
 * InputStreams, as possible.
 * 
 * The idea is that we make a copy of the data read from the first InputStream, which is usually wanted anyway. If we
 * have a filter that operates selectively on the InputStream, we "watch" it, with a tag to describe the filter.
 * 
 * Watching the InputStream wraps it in a new InputStream that compares any data read through it with the data read
 * through the most recent prior watched stream, or the original InputStream if this is the first watch.
 * 
 * No data is copied unless a discrepancy is noted. At that point, a local copy is created, and future data read from
 * the watched inputstream is recorded to the local copy.
 * 
 * A possible enhancement is to record how much of the original data had been read before a discrepancy was noted, and
 * avoid copying that data as well.
 * 
 * @author rogan
 * 
 */
public class ChangeMonitorInputStream extends FilterInputStream {

    private Copy original = new Copy();

    private List<CopyStream> watches = new ArrayList<CopyStream>();

    public ChangeMonitorInputStream(InputStream in) {
        super(in);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterInputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterInputStream#read()
     */
    @Override
    public int read() throws IOException {
        int ret = super.read();
        if (ret > -1)
            original.write(ret);
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.FilterInputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int ret = super.read(b, off, len);
        if (ret > 0)
            original.write(b, off, ret);
        return ret;
    }

    public byte[] getOriginal() {
        return original.toByteArray();
    }

    public InputStream watch(InputStream in, String tag) throws IOException {
        CopyStream watch = new CopyStream(in, tag, watches.size());
        watches.add(watch);
        return watch;
    }

    public CopyStream[] getModifiedStreams() {
        List<CopyStream> changed = new ArrayList<CopyStream>();
        Iterator<CopyStream> it = watches.iterator();
        while (it.hasNext()) {
            CopyStream copy = it.next();
            if (copy.getCopy() != null)
                changed.add(copy);
        }
        return changed.toArray(new CopyStream[changed.size()]);
    }

    public class CopyStream extends FilterInputStream {
        private int index = 0;
        private Copy copy = null;
        private boolean changed = false;
        private String tag;
        private int watchPosition;

        public CopyStream(InputStream in, String tag, int position) {
            super(in);
            this.tag = tag;
            this.watchPosition = position;
        }

        public String getTag() {
            return tag;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterInputStream#markSupported()
         */
        @Override
        public boolean markSupported() {
            return false;
        }

        private Copy getPriorCopy() {
            Copy prior = null;
            for (int i = watchPosition - 1; i >= 0; i--) {
                prior = watches.get(i).getCopy();
                if (prior != null)
                    break;
            }
            if (prior == null)
                prior = original;
            return prior;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterInputStream#read()
         */
        @Override
        public int read() throws IOException {
            int ret = super.read();
            if (ret > -1) {
                if (!changed) {
                    Copy prior = getPriorCopy();
                    if (!prior.compare(index, ret)) {
                        copy(prior);
                        copy.write(ret);
                        changed = true;
                    } else {
                        index++;
                    }
                } else {
                    copy.write(ret);
                }
            }
            return ret;
        }

        private void copy(Copy prior) {
            this.copy = new Copy();
            prior.copyTo(this.copy, index);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilterInputStream#read(byte[], int, int)
         */
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int ret = super.read(b, off, len);
            if (ret > 0) {
                if (!changed) {
                    Copy prior = getPriorCopy();
                    if (!prior.compare(index, b, off, ret)) {
                        copy(prior);
                        copy.write(b, off, ret);
                        changed = true;
                    } else {
                        index += ret;
                    }
                } else {
                    copy.write(b, off, ret);
                }
            }
            return ret;
        }

        private Copy getCopy() {
            return copy;
        }

        public byte[] toByteArray() {
            return copy == null ? null : copy.toByteArray();
        }
    }

    private static class Copy extends ByteArrayOutputStream {

        public boolean compare(int start, byte[] data, int offset, int len) {
            if (start + len > this.count)
                return false;
            for (int i = 0; i < len; i++)
                if (this.buf[start + i] != data[offset + i])
                    return false;
            return true;
        }

        private boolean compare(int start, int b) {
            if (start > this.count)
                return false;
            byte l = buf[start];
            if (l == (byte) (b & 0xFF))
                return true;
            return false;
        }

        /*
         * copies the first len bytes from this Copy to the specified one. We do it this way to avoid making more copies
         * of the byte[] than we have to.
         */
        public void copyTo(Copy copy, int len) {
            copy.write(this.buf, 0, len);
        }
    }
}
