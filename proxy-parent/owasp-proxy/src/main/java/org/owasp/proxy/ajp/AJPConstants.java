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

package org.owasp.proxy.ajp;

import java.util.HashMap;
import java.util.Map;

/**
 * Taken almost directly from org.apache.coyote.ajp.Constants
 * 
 */
public class AJPConstants {

    // Prefix codes for message types from server to container
    public static final byte JK_AJP13_FORWARD_REQUEST = 2;
    public static final byte JK_AJP13_SHUTDOWN = 7;
    public static final byte JK_AJP13_PING_REQUEST = 8;
    public static final byte JK_AJP13_CPING_REQUEST = 10;

    // Prefix codes for message types from container to server
    public static final byte JK_AJP13_SEND_BODY_CHUNK = 3;
    public static final byte JK_AJP13_SEND_HEADERS = 4;
    public static final byte JK_AJP13_END_RESPONSE = 5;
    public static final byte JK_AJP13_GET_BODY_CHUNK = 6;
    public static final byte JK_AJP13_CPONG_REPLY = 9;

    // Integer codes for common response header strings
    public static final int SC_RESP_CONTENT_TYPE = 0xA001;
    public static final int SC_RESP_CONTENT_LANGUAGE = 0xA002;
    public static final int SC_RESP_CONTENT_LENGTH = 0xA003;
    public static final int SC_RESP_DATE = 0xA004;
    public static final int SC_RESP_LAST_MODIFIED = 0xA005;
    public static final int SC_RESP_LOCATION = 0xA006;
    public static final int SC_RESP_SET_COOKIE = 0xA007;
    public static final int SC_RESP_SET_COOKIE2 = 0xA008;
    public static final int SC_RESP_SERVLET_ENGINE = 0xA009;
    public static final int SC_RESP_STATUS = 0xA00A;
    public static final int SC_RESP_WWW_AUTHENTICATE = 0xA00B;
    public static final int SC_RESP_AJP13_MAX = 11;

    // Integer codes for common (optional) request attribute names
    public static final byte SC_A_CONTEXT = 1; // XXX Unused
    public static final byte SC_A_SERVLET_PATH = 2; // XXX Unused
    public static final byte SC_A_REMOTE_USER = 3;
    public static final byte SC_A_AUTH_TYPE = 4;
    public static final byte SC_A_QUERY_STRING = 5;
    public static final byte SC_A_JVM_ROUTE = 6;
    public static final byte SC_A_SSL_CERT = 7;
    public static final byte SC_A_SSL_CIPHER = 8;
    public static final byte SC_A_SSL_SESSION = 9;
    public static final byte SC_A_SSL_KEYSIZE = 11;
    public static final byte SC_A_SECRET = 12;
    public static final byte SC_A_STORED_METHOD = 13;

    // AJP14 new header
    public static final byte SC_A_SSL_KEY_SIZE = 11; // XXX ???

    // Used for attributes which are not in the list above
    public static final byte SC_A_REQ_ATTRIBUTE = 10;

    // Terminates list of attributes
    public static final byte SC_A_ARE_DONE = (byte) 0xFF;

    // Ajp13 specific - needs refactoring for the new model

    /**
     * Default maximum total byte size for a AJP packet
     */
    public static final int MAX_PACKET_SIZE = 8192;
    /**
     * Size of basic packet header
     */
    public static final int H_SIZE = 4;

    /**
     * Size of the header metadata
     */
    public static final int READ_HEAD_LEN = 6;
    public static final int SEND_HEAD_LEN = 8;

    /**
     * Default maximum size of data that can be sent in one packet
     */
    public static final int MAX_READ_SIZE = MAX_PACKET_SIZE - READ_HEAD_LEN;
    public static final int MAX_SEND_SIZE = MAX_PACKET_SIZE - SEND_HEAD_LEN;

    // Translates integer codes to names of HTTP methods
    private static final String[] requestMethods = { "OPTIONS", "GET", "HEAD",
            "POST", "PUT", "DELETE", "TRACE", "PROPFIND", "PROPPATCH", "MKCOL",
            "COPY", "MOVE", "LOCK", "UNLOCK", "ACL", "REPORT",
            "VERSION-CONTROL", "CHECKIN", "CHECKOUT", "UNCHECKOUT", "SEARCH",
            "MKWORKSPACE", "UPDATE", "LABEL", "MERGE", "BASELINE-CONTROL",
            "MKACTIVITY" };

    private static final Map<String, Integer> requestMethodsHash = new HashMap<String, Integer>(
            requestMethods.length);

    static {
        int i;
        for (i = 0; i < requestMethods.length; i++) {
            requestMethodsHash.put(requestMethods[i], Integer.valueOf(1 + i));
        }

    }

    public static final int SC_M_JK_STORED = (byte) 0xFF;

    // id's for common request headers
    public static final int SC_REQ_ACCEPT = 1;
    public static final int SC_REQ_ACCEPT_CHARSET = 2;
    public static final int SC_REQ_ACCEPT_ENCODING = 3;
    public static final int SC_REQ_ACCEPT_LANGUAGE = 4;
    public static final int SC_REQ_AUTHORIZATION = 5;
    public static final int SC_REQ_CONNECTION = 6;
    public static final int SC_REQ_CONTENT_TYPE = 7;
    public static final int SC_REQ_CONTENT_LENGTH = 8;
    public static final int SC_REQ_COOKIE = 9;
    public static final int SC_REQ_COOKIE2 = 10;
    public static final int SC_REQ_HOST = 11;
    public static final int SC_REQ_PRAGMA = 12;
    public static final int SC_REQ_REFERER = 13;
    public static final int SC_REQ_USER_AGENT = 14;

    // Translates integer codes to request header names
    private static final String[] requestHeaders = { "Accept",
            "Accept-Charset", "Accept-Encoding", "Accept-Language",
            "Authorization", "Connection", "Content-Type", "Content-Length",
            "Cookie", "Cookie2", "Host", "Pragma", "Referer", "User-Agent" };

    private static final Map<String, Integer> requestHeadersHash = new HashMap<String, Integer>(
            requestHeaders.length);

    static {
        int i;
        for (i = 0; i < requestHeaders.length; i++) {
            requestHeadersHash.put(requestHeaders[i].toLowerCase(), Integer
                    .valueOf(0xA001 + i));
        }

    }

    // Translates integer codes to response header names
    private static final String[] responseHeaders = { "Content-Type",
            "Content-Language", "Content-Length", "Date", "Last-Modified",
            "Location", "Set-Cookie", "Set-Cookie2", "Servlet-Engine",
            "Status", "WWW-Authenticate" };

    private static final Map<String, Integer> responseHeadersHash = new HashMap<String, Integer>(
            responseHeaders.length);

    static {
        int i;
        for (i = 0; i < responseHeaders.length; i++) {
            responseHeadersHash.put(responseHeaders[i], Integer
                    .valueOf(0xA001 + i));
        }
    }

    public static final int getRequestMethodIndex(String method) {
        Integer i = requestMethodsHash.get(method);
        if (i == null)
            return 0;
        else
            return i.intValue();
    }

    public static final String getRequestMethod(int index) {
        return requestMethods[index - 1];
    }

    public static final int getRequestHeaderIndex(String header) {
        Integer i = requestHeadersHash.get(header.toLowerCase());
        if (i == null)
            return 0;
        else
            return i.intValue();
    }

    public static final String getRequestHeader(int index) {
        return requestHeaders[index - 0xA001];
    }

    public static final int getResponseHeaderIndex(String header) {
        Integer i = responseHeadersHash.get(header);
        if (i == null)
            return 0;
        else
            return i.intValue();
    }

    public static final String getResponseHeader(int index) {
        return responseHeaders[index - 0xA001];
    }
}
