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

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.BufferedResponse;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.ResponseHeader;

public abstract class BufferedMessageInterceptor {

    /**
     * The Action enumeration is returned by the {@link #directRequest(MutableRequestHeader)} and
     * {@link #directResponse(BufferedRequest, MutableResponseHeader)} methods, and describes how the message content
     * should be handled.
     * 
     * {@value #BUFFER} indicates that the content should be buffered entirely before being sent to the destination.
     * 
     * {@value #STREAM} indicates that the content should be streamed directly to the destination, and a copy of the
     * content stored for processing once the message has been completely streamed.
     * 
     * {@value #IGNORE} indicates that the request or response is uninteresting, and should not be buffered at all. No
     * further methods in this interface will be invoked for that request/response pair once {@value #IGNORE} has been
     * returned.
     * 
     * @author rogan
     * 
     */
    public enum Action {
        BUFFER, STREAM, IGNORE
    };

    /**
     * Called to determine what to do with the request. Implementations can choose to buffer the request to allow for
     * modification, stream the request directly to the server while buffering the request for later review, or ignore
     * the request entirely.
     * 
     * NOTE: if the request is ignored, neither {@link #directResponse(BufferedRequest, MutableResponseHeader)} nor
     * {@link #responseContentSizeExceeded(BufferedRequest, ResponseHeader, int)} will be called, as no BufferedRequest
     * would be available.
     * 
     * @param request
     *            the request
     * @return the desired Action
     */
    public Action directRequest(final MutableRequestHeader request) {
        return Action.STREAM;
    }

    /**
     * Called if the return value from {@link #directRequest(MutableRequestHeader)} is BUFFER, once the request has been
     * completely buffered. The request may be modified within this method.
     * 
     * NOTE: This method will not be called if the message content is larger than the maximum message body size set on
     * the {@link BufferingHttpRequestHandler}.
     * 
     * See {@link #requestContentSizeExceeded(BufferedRequest, int)}
     * 
     * @param request
     *            the request
     */
    public void processRequest(final MutableBufferedRequest request) {
    }

    /**
     * Called if the return value from {@link #directRequest(MutableRequestHeader)} is BUFFER or STREAM, and the request
     * body is larger than the maximum message body specified.
     * 
     * @param request
     *            the request, containing max bytes of partial content
     * @param size
     *            the ultimate size of the request content
     */
    public void requestContentSizeExceeded(final BufferedRequest request,
            int size) {
    }

    /**
     * Called if the return value from {@link #directRequest(MutableRequestHeader)} was STREAM, once the request has
     * been completely sent to the server, and buffered. This method will not be called if the message content is larger
     * than max bytes.
     * 
     * See also {@link #requestContentSizeExceeded(BufferedRequest, int)}
     * 
     * @param request
     *            the request
     */
    public void requestStreamed(final BufferedRequest request) {
    }

    /**
     * Called to determine what to do with the response. Implementations can choose to buffer the response to allow for
     * modification, stream the response directly to the client, or ignore the response entirely.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @return the desired Action
     */
    public Action directResponse(final RequestHeader request,
            final MutableResponseHeader response) {
        return Action.STREAM;
    }

    /**
     * Called if the return value from {@link #directResponse(MutableRequestHeader, MutableResponseHeader)} was BUFFER,
     * once the response has been completely buffered. The response may be modified within this method, before being
     * sent to the client.
     * 
     * NOTE: This method will NOT be called if the Action returned by the corresponding
     * {@link #directRequest(MutableRequestHeader)} method was Action.IGNORE, as no BufferedRequest would be available.
     * 
     * NOTE: This method will not be called if the message content is larger than max bytes.
     * 
     * See {@link #responseContentSizeExceeded(BufferedRequest, ResponseHeader, int)}
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     */
    public void processResponse(final BufferedRequest request,
            final MutableBufferedResponse response) {
    }

    /**
     * Called if the return value from {@link #directResponse(MutableRequestHeader, MutableResponseHeader)} is BUFFER or
     * STREAM, and the response body is larger than the maximum message body specified
     * 
     * @param request
     *            the request
     * @param response
     *            the response, containing max bytes of partial content
     * @param size
     *            the eventual size of the response content
     */
    public void responseContentSizeExceeded(final BufferedRequest request,
            final ResponseHeader response, int size) {
    }

    /**
     * Called if the return value from {@link #directResponse(MutableRequestHeader, MutableResponseHeader)} was STREAM,
     * once the response has been completely sent to the client, and buffered. This method will not be called if the
     * message content is larger than max bytes.
     * 
     * See {@link #responseContentSizeExceeded(BufferedRequest, ResponseHeader, int)}
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     */
    public void responseStreamed(final BufferedRequest request,
            final BufferedResponse response) {
    }

}
