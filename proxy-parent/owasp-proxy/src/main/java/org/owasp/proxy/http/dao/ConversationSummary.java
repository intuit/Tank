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

package org.owasp.proxy.http.dao;

import java.net.InetSocketAddress;

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.BufferedResponse;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.ResponseHeader;

public class ConversationSummary {

    private long id;

    private InetSocketAddress target;

    private String requestMethod, requestResource, requestContentType,
            responseStatus, responseReason, responseContentType, connection;

    private boolean ssl;

    private int requestContentSize, responseContentSize;

    private long requestTime, responseHeaderTime, responseContentTime;

    public ConversationSummary() {
    }

    public void summarizeRequest(BufferedRequest request)
            throws MessageFormatException {
        byte[] content = request.getContent();
        int contentSize = content == null ? 0 : content.length;
        summarizeRequest(request, contentSize);
    }

    public void summarizeRequest(RequestHeader request, int contentSize)
            throws MessageFormatException {
        target = request.getTarget();
        ssl = request.isSsl();
        requestMethod = request.getMethod();
        requestResource = request.getResource();
        requestContentType = request.getHeader("Content-Type");
        requestContentSize = contentSize;
        requestTime = request.getTime();
    }

    public void summarizeResponse(BufferedResponse response)
            throws MessageFormatException {
        byte[] content = response.getContent();
        int contentSize = content == null ? 0 : content.length;
        summarizeResponse(response, contentSize);
    }

    public void summarizeResponse(ResponseHeader response, int contentSize)
            throws MessageFormatException {
        responseStatus = response.getStatus();
        responseReason = response.getReason();
        responseContentType = response.getHeader("Content-Type");
        responseContentSize = contentSize;
        responseHeaderTime = response.getHeaderTime();
        responseContentTime = response.getContentTime();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InetSocketAddress getTarget() {
        return target;
    }

    public void setTarget(InetSocketAddress target) {
        this.target = target;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestResource() {
        return requestResource;
    }

    public void setRequestResource(String requestResource) {
        this.requestResource = requestResource;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseReason() {
        return responseReason;
    }

    public void setResponseReason(String responseReason) {
        this.responseReason = responseReason;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getRequestContentSize() {
        return requestContentSize;
    }

    public void setRequestContentSize(int requestContentSize) {
        this.requestContentSize = requestContentSize;
    }

    public int getResponseContentSize() {
        return responseContentSize;
    }

    public void setResponseContentSize(int responseContentSize) {
        this.responseContentSize = responseContentSize;
    }

    public long getRequestSubmissionTime() {
        return requestTime;
    }

    public void setRequestSubmissionTime(long time) {
        this.requestTime = time;
    }

    public long getResponseHeaderTime() {
        return responseHeaderTime;
    }

    public void setResponseHeaderTime(long time) {
        this.responseHeaderTime = time;
    }

    public long getResponseContentTime() {
        return responseContentTime;
    }

    public void setResponseContentTime(long time) {
        this.responseContentTime = time;
    }

}
