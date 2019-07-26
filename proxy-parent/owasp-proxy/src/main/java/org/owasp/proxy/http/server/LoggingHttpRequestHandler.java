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

package org.owasp.proxy.http.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;

import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.NamedValue;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.io.CountingInputStream;
import org.owasp.proxy.io.EofNotifyingInputStream;

public class LoggingHttpRequestHandler implements HttpRequestHandler {

    private PrintStream out;

    private HttpRequestHandler next;

    public LoggingHttpRequestHandler(HttpRequestHandler next) {
        this(System.out, next);
    }

    public LoggingHttpRequestHandler(PrintStream out, HttpRequestHandler next) {
        this.out = out;
        this.next = next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.HttpRequestHandler#dispose()
     */
    public void dispose() throws IOException {
        next.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.HttpRequestHandler#handleRequest(java.net.InetAddress ,
     * org.owasp.httpclient.StreamingRequest)
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.DefaultHttpRequestHandler#handleRequest (java.net.InetAddress,
     * org.owasp.httpclient.StreamingRequest)
     */
    public StreamingResponse handleRequest(final InetAddress source,
            final StreamingRequest request, boolean isContinue) throws IOException,
            MessageFormatException {
        final StreamingResponse response = next.handleRequest(source, request, isContinue);
        request.getResource();
        request.getMethod();
        request.getContent();
        request.getTarget();
        if (response.getContent() != null) {
            final CountingInputStream cis = new CountingInputStream(response
                    .getContent());
            final EofNotifyingInputStream eofis = new EofNotifyingInputStream(
                    cis) {
                public void eof() {
                    log(source, request, response, cis.getCount());
                }
            };
            response.setContent(eofis);
        } else {
            log(source, request, response, 0);
        }
        return response;
    }

    protected void log(InetAddress source, MutableRequestHeader request,
            MutableResponseHeader response, int bytes) {
        try {
            StringBuilder buff = new StringBuilder();
            buff.append(source.getHostAddress()).append(" - - ");
            buff.append(System.currentTimeMillis()).append(" \"");
            buff.append(request.getMethod()).append(" ");
            buff.append(request.isSsl() ? "https://" : "http://");
            buff.append(request.getTarget().getHostName());
            buff.append(request.getResource()).append(" ");
            buff.append(request.getVersion()).append("\" ");
            buff.append(response.getStatus()).append(" \n");
            for (NamedValue nv : response.getHeaders()) {
                buff.append(nv.getName()).append(" ").append(nv.getSeparator());
                buff.append(nv.getValue()).append(" ");
            }
            buff.append("\n").append(response);

            buff.append(bytes);
            out.println(buff.toString());
        } catch (MessageFormatException ignore) {
        }
    }

}
