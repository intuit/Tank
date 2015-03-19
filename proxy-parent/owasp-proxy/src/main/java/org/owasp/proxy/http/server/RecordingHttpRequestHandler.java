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
import java.net.InetAddress;

import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.http.dao.MessageDAO;

public class RecordingHttpRequestHandler implements HttpRequestHandler {

    protected MessageDAO dao;

    private HttpRequestHandler next;

    private int max;

    public RecordingHttpRequestHandler(MessageDAO dao, HttpRequestHandler next,
            int maxContentSize) {
        this.dao = dao;
        this.next = next;
        this.max = maxContentSize;
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
    public StreamingResponse handleRequest(final InetAddress source,
            StreamingRequest request, boolean isContinue) throws IOException,
            MessageFormatException {
        MutableBufferedRequest req = new MutableBufferedRequest.Impl();
        MutableBufferedResponse resp = new MutableBufferedResponse.Impl();
        ConversationObserver observer = new ConversationObserver(source, req,
                resp);
        MessageUtils.delayedCopy(request, req, max, observer
                .getRequestObserver());
        StreamingResponse response = next.handleRequest(source, request,
                isContinue);
        MessageUtils.delayedCopy(response, resp, max, observer
                .getResponseObserver());
        return response;
    }

    protected void record(InetAddress source, MutableBufferedRequest request,
            MutableBufferedResponse response) {
        dao.saveRequest(request);
        dao.saveResponse(response);
        dao.saveConversation(request.getId(), response.getId());
    }

    private class ConversationObserver {

        private InetAddress source;
        private MutableBufferedRequest request;
        private MutableBufferedResponse response;

        private boolean requestContentOverflow, responseContentOverflow;

        public ConversationObserver(InetAddress source,
                MutableBufferedRequest request, MutableBufferedResponse response) {
            this.source = source;
            this.request = request;
            this.response = response;
        }

        public MessageUtils.DelayedCopyObserver getRequestObserver() {
            return new MessageUtils.DelayedCopyObserver() {
                @Override
                public void copyCompleted(boolean overflow, int size) {
                    requestContentOverflow = overflow;
                }
            };
        }

        public MessageUtils.DelayedCopyObserver getResponseObserver() {
            return new MessageUtils.DelayedCopyObserver() {
                @Override
                public void copyCompleted(boolean overflow, int size) {
                    responseContentOverflow = overflow;
                    if (requestContentOverflow)
                        request.setContent(null);
                    if (responseContentOverflow)
                        response.setContent(null);
                    record(source, request, response);
                }
            };
        }
    }
}
