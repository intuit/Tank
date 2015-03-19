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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.InetAddress;

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.http.server.BufferedMessageInterceptor.Action;
import org.owasp.proxy.io.CountingInputStream;
import org.owasp.proxy.io.SizeLimitExceededException;
import org.owasp.proxy.util.AsciiString;

public class BufferingHttpRequestHandler implements HttpRequestHandler {

    private static final byte[] CONTINUE = AsciiString
            .getBytes("HTTP/1.1 100 Continue\r\n\r\n");

    private final HttpRequestHandler next;

    protected int max = 0;

    private BufferedMessageInterceptor interceptor;

    public BufferingHttpRequestHandler(final HttpRequestHandler next,
            BufferedMessageInterceptor interceptor) {
        this.next = next;
        this.interceptor = interceptor;
    }

    public BufferingHttpRequestHandler(final HttpRequestHandler next,
            BufferedMessageInterceptor interceptor, final int max) {
        this.next = next;
        this.interceptor = interceptor;
        this.max = max;
    }

    public void setMaximumContentSize(int max) {
        this.max = max;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.HttpRequestHandler#dispose()
     */
    public final void dispose() throws IOException {
        next.dispose();
    }

    private BufferedRequest handleRequest(StreamingRequest request)
            throws IOException, MessageFormatException {
        final Action action = interceptor.directRequest(request);
        final MutableBufferedRequest brq;
        if (Action.BUFFER.equals(action)) {
            brq = new MutableBufferedRequest.Impl();
            try {
                MessageUtils.buffer(request, brq, max);
                interceptor.processRequest(brq);
                MessageUtils.stream(brq, request);
            } catch (SizeLimitExceededException slee) {
                final InputStream buffered = new ByteArrayInputStream(brq
                        .getContent());
                InputStream content = request.getContent();
                content = new SequenceInputStream(buffered, content);
                content = new CountingInputStream(content) {
                    protected void eof() {
                        interceptor.requestContentSizeExceeded(brq, getCount());
                    }
                };
                request.setContent(content);
            }
        } else if (Action.STREAM.equals(action)) {
            brq = new MutableBufferedRequest.Impl();
            MessageUtils.delayedCopy(request, brq, max,
                    new MessageUtils.DelayedCopyObserver() {
                        @Override
                        public void copyCompleted(boolean overflow, int size) {
                            if (overflow) {
                                interceptor.requestContentSizeExceeded(brq,
                                        size);
                            } else {
                                interceptor.requestStreamed(brq);
                            }
                        }
                    });
        } else {
            brq = null;
        }
        return brq;
    }

    private void handleResponse(final BufferedRequest request,
            final StreamingResponse response) throws IOException,
            MessageFormatException {
        Action action = interceptor.directResponse(request, response);
        final MutableBufferedResponse brs;
        if (Action.BUFFER.equals(action)) {
            brs = new MutableBufferedResponse.Impl();
            try {
                MessageUtils.buffer(response, brs, max);
                interceptor.processResponse(request, brs);
                new String(brs.getDecodedContent(), "utf-8");
                MessageUtils.stream(brs, response);
            } catch (SizeLimitExceededException slee) {
                InputStream buffered = new ByteArrayInputStream(brs
                        .getContent());
                InputStream content = response.getContent();
                content = new SequenceInputStream(buffered, content);
                content = new CountingInputStream(content) {
                    protected void eof() {
                        interceptor.responseContentSizeExceeded(request, brs,
                                getCount());
                    }
                };
                response.setContent(content);
            }
        } else if (Action.STREAM.equals(action)) {
            brs = new MutableBufferedResponse.Impl();
            MessageUtils.delayedCopy(response, brs, max,
                    new MessageUtils.DelayedCopyObserver() {
                        @Override
                        public void copyCompleted(boolean overflow, int size) {
                            if (overflow) {
                                interceptor.responseContentSizeExceeded(
                                        request, brs, size);
                            } else {
                                interceptor.responseStreamed(request, brs);
                            }
                        }

                    });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.HttpRequestHandler#handleRequest(java.net.InetAddress ,
     * org.owasp.httpclient.StreamingRequest)
     */
    final public StreamingResponse handleRequest(final InetAddress source,
            final StreamingRequest request, boolean isContinue)
            throws IOException, MessageFormatException {
        if (!isContinue && isExpectContinue(request)) {
            return get100Continue();
        }

        BufferedRequest brq = handleRequest(request);
        isContinue = false;

        StreamingResponse response = null;
        if (isExpectContinue(request)) {
            StreamingRequest cont = new StreamingRequest.Impl(request);
            response = next.handleRequest(source, cont, false);
            isContinue = isContinue(response);
            if (!isContinue) {
                return response;
            }
        }
        response = next.handleRequest(source, request, isContinue);
        if (brq != null)
            handleResponse(brq, response);
        return response;
    }

    private boolean isExpectContinue(final RequestHeader request)
            throws MessageFormatException {
        return "100-continue".equalsIgnoreCase(request.getHeader("Expect"));
    }

    private boolean isContinue(StreamingResponse response)
            throws MessageFormatException {
        return "100".equals(response.getStatus());
    }

    private StreamingResponse get100Continue() {
        StreamingResponse response = new StreamingResponse.Impl();
        response.setHeader(CONTINUE);
        return response;
    }

}
