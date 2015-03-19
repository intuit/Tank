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

import java.io.IOException;
import java.io.InputStream;

public interface StreamingRequest extends MutableRequestHeader,
        StreamingMessage {

    public class Impl extends MutableRequestHeader.Impl implements
            StreamingRequest {

        public Impl() {
        }

        public Impl(MutableRequestHeader header) {
            setTarget(header.getTarget());
            setSsl(header.isSsl());
            setHeader(header.getHeader());
        }

        private InputStream content;

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.httpclient.StreamingResponse#getContent()
         */
        public InputStream getContent() {
            return content;
        }

        public InputStream getDecodedContent() throws MessageFormatException {
            try {
                return MessageUtils.decode(this, content);
            } catch (IOException ioe) {
                MessageFormatException mfe = new MessageFormatException(
                        "Error decoding content");
                mfe.initCause(ioe);
                throw mfe;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.httpclient.StreamingResponse#setContent(java.io.InputStream )
         */
        public void setContent(InputStream content) {
            this.content = content;
        }

        public void setDecodedContent(InputStream content)
                throws MessageFormatException {
            this.content = MessageUtils.encode(this, content);
        }

    }

}
