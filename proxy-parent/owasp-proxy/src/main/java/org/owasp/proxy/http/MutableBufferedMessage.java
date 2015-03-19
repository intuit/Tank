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

public interface MutableBufferedMessage extends BufferedMessage,
        MutableMessageHeader {

    /**
     * @param content
     */
    void setContent(byte[] content);

    /**
     * Allows the caller to avoid dealing with Transfer-Encoding and Content-Encoding details.
     * 
     * The content provided will have Chunking and Gzip, etc encodings applied as specified by the message header,
     * before being set as the message content.
     * 
     * @param content
     * @throws MessageFormatException
     */
    void setDecodedContent(byte[] content) throws MessageFormatException;

}
