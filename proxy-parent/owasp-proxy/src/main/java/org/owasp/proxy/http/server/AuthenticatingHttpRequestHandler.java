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
import java.io.InputStream;
import java.net.InetAddress;

import org.owasp.proxy.http.HttpAuthenticator;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.ResponseHeader;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.http.MessageUtils.DelayedCopyObserver;

public class AuthenticatingHttpRequestHandler implements HttpRequestHandler {

    private int max = 100000;

    private HttpRequestHandler delegate;

    private HttpAuthenticator auth;

    public AuthenticatingHttpRequestHandler(HttpRequestHandler delegate) {
        this(delegate, new HttpAuthenticator());
    }

    public AuthenticatingHttpRequestHandler(HttpRequestHandler delegate,
            HttpAuthenticator auth) {
        this.delegate = delegate;
        this.auth = auth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.http.server.HttpRequestHandler#dispose()
     */
    public void dispose() throws IOException {
        delegate.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.http.server.HttpRequestHandler#handleRequest(java.net .InetAddress,
     * org.owasp.proxy.http.StreamingRequest, boolean)
     */
    public StreamingResponse handleRequest(InetAddress source,
            StreamingRequest request, boolean isContinue) throws IOException,
            MessageFormatException {
        // if Expect: continue with no content, the caller will supply the
        // content later
        // we must return as soon as we get a 100 response
        boolean expectContinue = MessageUtils.isExpectContinue(request)
                && request.getContent() == null;
        MutableBufferedRequest copy = null;
        CopyMonitor copyMonitor = new CopyMonitor();
        if (!expectContinue) { // we have to keep our own copy
            copy = new MutableBufferedRequest.Impl();
            if (MessageUtils.expectContent(request)) {
                MessageUtils.delayedCopy(request, copy, max, copyMonitor);
            } else {
                MessageUtils.buffer(request, copy, 0);
            }
        }

        StreamingResponse response = delegate.handleRequest(source, request,
                isContinue);

        String status = response.getStatus();
        if (!"401".equals(status) && !"407".equals(status))
            return response;

        if (copyMonitor.overflow) {
            // we don't have a complete copy of the content, so we can't try to
            // replay it with authentication
            // TODO: log this somewhere
            // Not too sure how best to respond.
            // Pass back to the caller? Not ideal
            throw new IOException(
                    "Authorization required, but request content too large to buffer. Aborting . . .");
        }

        ResponseHeader lastResponse = null;
        while ("401".equals(status) || "407".equals(status)) {
            StreamingRequest req = new StreamingRequest.Impl();
            MessageUtils.stream(copy, req);
            if (auth.authenticate(req, response, lastResponse)) {
                consumeContent(response.getContent());
                lastResponse = response;
                response = delegate.handleRequest(source, req, isContinue);
            } else {
                return response;
            }
            status = response.getStatus();
        }
        return response;
    }

    private void consumeContent(InputStream in) throws IOException {
        if (in != null) {
            byte[] buff = new byte[1024];
            while (in.read(buff) > -1) {
                // discard it
            }
        }
    }

    private static class CopyMonitor extends DelayedCopyObserver {
        public boolean overflow = false;

        @Override
        public void copyCompleted(boolean overflow, int size) {
            this.overflow = overflow;
        }
    }

}
