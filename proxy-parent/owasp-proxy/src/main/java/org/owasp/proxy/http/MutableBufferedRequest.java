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

package org.owasp.proxy.http;

import java.lang.ref.WeakReference;

import org.owasp.proxy.util.AsciiString;

public interface MutableBufferedRequest extends MutableRequestHeader,
        MutableBufferedMessage, BufferedRequest {

    public static class Impl extends MutableRequestHeader.Impl implements
            MutableBufferedRequest {

        private byte[] content;

        private WeakReference<byte[]> decoded = null;

        public void setContent(byte[] content) {
            this.content = content;
        }

        public byte[] getContent() {
            return content;
        }

        public byte[] getDecodedContent() throws MessageFormatException {
            if (content == null)
                return null;
            if (decoded == null || decoded.get() == null) {
                decoded = new WeakReference<byte[]>(MessageUtils.decode(this));
            }
            return decoded.get();
        }

        /**
         * This method automatically applies any chunked or gzip encoding specified in the message headers before
         * setting the message content.
         * 
         * The decoded content is cached using a WeakReference to reduce the need to perform repeated decoding
         * operations.
         */
        public void setDecodedContent(byte[] content)
                throws MessageFormatException {
            if (content == null) {
                this.decoded = null;
                this.content = null;
            } else {
                decoded = new WeakReference<byte[]>(content);
                this.content = MessageUtils.encode(this, content);
            }
        }

        @Override
        public String toString() {
            return super.toString()
                    + (content != null ? AsciiString.create(content) : "");
        }
    }

}
