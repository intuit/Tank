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

package org.owasp.proxy.socks.impl;

public class SocksConstants {

    // Constants

    public static final int SOCKS_SUCCESS = 0;

    public static final int SOCKS_FAILURE = 1;

    public static final int SOCKS_BADCONNECT = 2;

    public static final int SOCKS_BADNETWORK = 3;

    public static final int SOCKS_HOST_UNREACHABLE = 4;

    public static final int SOCKS_CONNECTION_REFUSED = 5;

    public static final int SOCKS_TTL_EXPIRE = 6;

    public static final int SOCKS_CMD_NOT_SUPPORTED = 7;

    public static final int SOCKS_ADDR_NOT_SUPPORTED = 8;

    public static final int SOCKS_NO_PROXY = 1 << 16;

    public static final int SOCKS_PROXY_NO_CONNECT = 2 << 16;

    public static final int SOCKS_PROXY_IO_ERROR = 3 << 16;

    public static final int SOCKS_AUTH_NOT_SUPPORTED = 4 << 16;

    public static final int SOCKS_AUTH_FAILURE = 5 << 16;

    public static final int SOCKS_JUST_ERROR = 6 << 16;

    public static final int SOCKS_DIRECT_FAILED = 7 << 16;

    public static final int SOCKS_METHOD_NOTSUPPORTED = 8 << 16;

    public static final int SOCKS_CMD_CONNECT = 0x1;

    public static final int SOCKS_CMD_BIND = 0x2;

    public static final int SOCKS_CMD_UDP_ASSOCIATE = 0x3;

}
