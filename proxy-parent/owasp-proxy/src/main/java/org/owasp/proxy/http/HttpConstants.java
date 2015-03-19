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

public class HttpConstants {

    public final static String CHUNKED = "chunked";

    public final static String CONTENT_ENCODING = "Content-Encoding";

    public final static String CONTENT_LENGTH = "Content-Length";

    public final static String GZIP = "gzip";

    public final static String IDENTITY = "identity";

    public final static String TRANSFER_ENCODING = "Transfer-Encoding";

    public final static String AUTHENTICATE = "WWW-Authenticate";

    public final static String AUTHORIZATION = "Authorization";

    public final static String PROXY_AUTHENTICATE = "Proxy-Authenticate";

    public final static String PROXY_AUTHORIZATION = "Proxy-Authorization";

    public final static String EXPECT = "Expect";

    public final static String CONTINUE = "100-continue";

    public final static String DEFLATE = "deflate";

}
