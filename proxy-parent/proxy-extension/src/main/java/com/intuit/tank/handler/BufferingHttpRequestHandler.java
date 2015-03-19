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

package com.intuit.tank.handler;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.InetAddress;

import javax.xml.bind.JAXBException;

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.NamedValue;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.http.server.BufferedMessageInterceptor;
import org.owasp.proxy.http.server.BufferedMessageInterceptor.Action;
import org.owasp.proxy.http.server.HttpRequestHandler;
import org.owasp.proxy.io.CountingInputStream;
import org.owasp.proxy.io.SizeLimitExceededException;
import org.owasp.proxy.util.AsciiString;

import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Protocol;
import com.intuit.tank.conversation.Request;
import com.intuit.tank.conversation.Response;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.entity.Application;
import com.intuit.tank.util.HeaderParser;

public class BufferingHttpRequestHandler implements HttpRequestHandler {

    private static final byte[] CONTINUE = AsciiString
            .getBytes("HTTP/1.1 100 Continue\r\n\r\n");

    private final HttpRequestHandler next;

    protected int max = 0;

    private BufferedMessageInterceptor interceptor;

    private Application application;

    public BufferingHttpRequestHandler(final HttpRequestHandler next,
            BufferedMessageInterceptor interceptor, Application application) {
        this.next = next;
        this.application = application;
        this.interceptor = interceptor;
    }

    public BufferingHttpRequestHandler(final HttpRequestHandler next,
            BufferedMessageInterceptor interceptor, final int max, Application application) {
        this.next = next;
        this.interceptor = interceptor;
        this.max = max;
        this.application = application;
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

        storeRequest(brq);
        return brq;
    }

    private Transaction storeRequest(final BufferedRequest brq) throws MessageFormatException {
        Request r = new Request();
        r.setBody(brq.getDecodedContent());
        r.setFirstLine(brq.getStartLine());
        r.setProtocol(brq.isSsl() ? Protocol.https : Protocol.http);

        NamedValue[] headers = brq.getHeaders();
        for (NamedValue namedValue : headers) {
            r.getHeaders().add(new Header(namedValue.getName(), namedValue.getValue()));
        }

        return application.setRequestForCurrentTransaction(r, brq);
    }

    private void handleResponse(final BufferedRequest request,
            final StreamingResponse response) throws IOException,
            MessageFormatException {
        Action action = interceptor.directResponse(request, response);
        final MutableBufferedResponse brs;
        Transaction transaction = storeRequest(request);

        if (Action.BUFFER.equals(action)) {
            brs = new MutableBufferedResponse.Impl();
            try {
                if (request.getMethod().equalsIgnoreCase("head")) {
                    brs.setContent(null);
                    brs.setDecodedContent(null);
                    brs.setHeader(response.getHeader());
                    // brs.setStartLine(response.getStartLine());
                } else {
                    MessageUtils.buffer(response, brs, max);
                }
                interceptor.processResponse(request, brs);

                storeResponse(transaction, brs, request);
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
            storeResponse(transaction, brs, request);
        }

    }

    private void storeResponse(Transaction transaction, MutableBufferedResponse brs, BufferedRequest request)
            throws MessageFormatException {

        Response response = new Response();
        response.setBody(brs.getDecodedContent());
        response.setFirstLine(brs.getStartLine());
        NamedValue[] headers = brs.getHeaders();
        for (NamedValue namedValue : headers) {
            response.getHeaders().add(new Header(namedValue.getName(), namedValue.getValue()));
        }
        try {
            if (transaction != null) {
                application.setResponseForCurrentTransaction(transaction, response, request);
            }
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        if (brq != null) {
            handleResponse(brq, response);
        }
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
