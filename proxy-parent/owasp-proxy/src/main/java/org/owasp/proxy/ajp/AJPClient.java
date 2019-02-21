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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;

import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.NamedValue;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.io.BufferedInputStream;

public class AJPClient {

    private static final byte[] PING;

    private static final AJPMessage PONG = new AJPMessage(16);

    static {
        AJPMessage msg = new AJPMessage(16);
        msg.reset();
        msg.appendByte(AJPConstants.JK_AJP13_CPING_REQUEST);
        msg.endClientMessage();
        PING = msg.toByteArray();
    }

    private enum State {
        DISCONNECTED, IDLE, ASSIGNED
    }

    private Socket sock;

    private InputStream in;

    private OutputStream out;

    private State state = State.DISCONNECTED;

    private int timeout = 0;

    private AJPProperties properties = new AJPProperties();

    private AJPMessage ajpRequest = new AJPMessage(8192),
            ajpResponse = new AJPMessage(8192);

    private long requestSubmissionTime, responseHeaderTime;

    public AJPClient() {
    }

    public void connect(InetSocketAddress target) throws IOException {
        connect(target, Proxy.NO_PROXY);
    }

    public void connect(InetSocketAddress target, Proxy proxy)
            throws IOException {
        try {
            close();
        } catch (IOException ignored) {
        }
        validateTarget(proxy.address());
        sock = new Socket(proxy);
        sock.connect(target);
        sock.setSoTimeout(timeout);
        in = sock.getInputStream();
        out = sock.getOutputStream();
        state = State.IDLE;
    }

    public void setTimeout(int timeout) throws IOException {
        this.timeout = timeout;
    }

    public void close() throws IOException {
        if (sock != null) {
            if (!sock.isClosed()) {
                sock.close();
            }
        }
    }

    protected void validateTarget(SocketAddress target) throws IOException {
    }

    public boolean isConnected() {
        if (sock == null)
            return false;
        try {
            int to = sock.getSoTimeout();
            try {
                sock.setSoTimeout(10);
                int got = sock.getInputStream().read();
                if (got == -1)
                    return false;
                throw new RuntimeException("Unexpected data read from socket: "
                        + got);
            } catch (SocketTimeoutException ste) {
                return true;
            } catch (IOException ioe) {
                return false;
            } finally {
                sock.setSoTimeout(to);
            }
        } catch (IOException ioe) {
            return false;
        }
    }

    public boolean isIdle() {
        return State.IDLE.equals(state);
    }

    public boolean ping() throws IOException {
        if (!isConnected())
            throw new IOException("Not connected to server");
        if (!isIdle())
            throw new IllegalStateException("Client is currently assigned");
        out.write(PING);
        out.flush();
        PONG.readMessage(in);
        return PONG.peekByte() == AJPConstants.JK_AJP13_CPONG_REPLY;
    }

    public StreamingResponse fetchResponse(StreamingRequest request)
            throws MessageFormatException, IOException {
        if (!isConnected())
            throw new IOException("Not connected to server");
        if (!isIdle())
            throw new IllegalStateException("Client is currently assigned");

        translate(request, ajpRequest);
        ajpRequest.write(out);
        InputStream content = request.getContent();
        if (request.getContent() != null) {
            ajpRequest.reset();
            ajpRequest.appendBytes(content, Integer.MAX_VALUE);
            ajpRequest.endClientMessage();
            ajpRequest.write(out);
        }
        requestSubmissionTime = System.currentTimeMillis();

        ajpResponse.readMessage(in);

        byte type = ajpResponse.peekByte();

        int wrote = 0;
        while (type == AJPConstants.JK_AJP13_GET_BODY_CHUNK) {
            ajpResponse.getByte(); // get the type
            int max = ajpResponse.getInt();
            ajpRequest.reset();
            wrote += ajpRequest.appendBytes(content, max);
            ajpRequest.endClientMessage();
            ajpRequest.write(out);
            requestSubmissionTime = System.currentTimeMillis();
            ajpResponse.readMessage(in);
            type = ajpResponse.peekByte();
        }

        StreamingResponse response = new StreamingResponse.Impl();
        if (type == AJPConstants.JK_AJP13_SEND_HEADERS) {
            responseHeaderTime = System.currentTimeMillis();
            translate(ajpResponse, response);
        } else
            throw new IllegalStateException(
                    "Expected message of type SEND_HEADERS, got " + type);

        response.setContent(new AJPResponseInputStream(in));

        return response;
    }

    private class AJPResponseInputStream extends BufferedInputStream {

        public AJPResponseInputStream(InputStream in) throws IOException {
            super(in);
        }

        protected void fillBuffer() throws IOException {
            ajpResponse.readMessage(in);
            int type = ajpResponse.getByte();
            if (type == AJPConstants.JK_AJP13_END_RESPONSE) {
                state = State.IDLE;
                buff = null;
                start = 0;
                end = 0;
            } else if (type == AJPConstants.JK_AJP13_SEND_BODY_CHUNK) {
                int len = ajpResponse.peekInt();
                if (buff == null || buff.length < len)
                    buff = new byte[len];
                int got = ajpResponse.getBytes(buff);
                if (got != len)
                    throw new IllegalStateException(
                            "buffer lengths did not match!");
                start = 0;
                end = len;
            }
        }

    }

    private void translate(RequestHeader request, AJPMessage message)
            throws MessageFormatException {
        message.reset();
        message.appendByte(AJPConstants.JK_AJP13_FORWARD_REQUEST);
        int method = AJPConstants.getRequestMethodIndex(request.getMethod());
        if (method == 0)
            throw new RuntimeException("Unsupported request method: "
                    + request.getMethod());
        message.appendByte(method);
        message.appendString(request.getVersion());
        String resource = request.getResource();
        String query = null;
        int q = resource.indexOf('?');
        if (q > -1) {
            query = resource.substring(q + 1);
            resource = resource.substring(0, q);
        }
        message.appendString(resource); // resource
        message.appendString(getRemoteAddress()); // remote_addr
        message.appendString(getRemoteHost()); // remote_host
        message.appendString(request.getTarget().getHostName()); // server_name
        message.appendInt(request.getTarget().getPort()); // server_port
        message.appendBoolean(request.isSsl()); // is_ssl
        NamedValue[] headers = request.getHeaders();
        if (headers == null)
            headers = new NamedValue[0];
        message.appendInt(headers.length);
        for (NamedValue header : headers) {
            appendRequestHeader(message, header);
        }

        appendRequestAttribute(message, AJPConstants.SC_A_CONTEXT, properties
                .getContext());
        appendRequestAttribute(message, AJPConstants.SC_A_SERVLET_PATH,
                properties.getServletPath());
        appendRequestAttribute(message, AJPConstants.SC_A_REMOTE_USER,
                properties.getRemoteUser());
        appendRequestAttribute(message, AJPConstants.SC_A_AUTH_TYPE, properties
                .getAuthType());
        appendRequestAttribute(message, AJPConstants.SC_A_QUERY_STRING, query);
        appendRequestAttribute(message, AJPConstants.SC_A_JVM_ROUTE, properties
                .getRoute());
        appendRequestAttribute(message, AJPConstants.SC_A_SSL_CERT, properties
                .getSslCert());
        appendRequestAttribute(message, AJPConstants.SC_A_SSL_CIPHER,
                properties.getSslCipher());
        appendRequestAttribute(message, AJPConstants.SC_A_SSL_SESSION,
                properties.getSslSession());
        appendRequestAttributes(message, AJPConstants.SC_A_REQ_ATTRIBUTE,
                properties.getRequestAttributes());
        appendRequestAttribute(message, AJPConstants.SC_A_SSL_KEY_SIZE,
                properties.getSslKeySize());
        appendRequestAttribute(message, AJPConstants.SC_A_SECRET, properties
                .getSecret());
        appendRequestAttribute(message, AJPConstants.SC_A_STORED_METHOD,
                properties.getStoredMethod());

        message.appendByte(AJPConstants.SC_A_ARE_DONE);
        message.endClientMessage();
    }

    private static void appendRequestHeader(AJPMessage message,
            NamedValue header) {
        int code = AJPConstants.getRequestHeaderIndex(header.getName());
        if (code > 0) {
            message.appendInt(code);
        } else {
            message.appendString(header.getName());
        }
        message.appendString(header.getValue());
    }

    private void appendRequestAttribute(AJPMessage message, byte attribute,
            String value) {
        if (value == null)
            return;
        message.appendByte(attribute);
        message.appendString(value);
    }

    private String getRemoteAddress() {
        String remoteAddress = properties.getRemoteAddress();
        if (remoteAddress != null)
            return remoteAddress;
        return sock.getLocalAddress().getHostAddress();
    }

    private String getRemoteHost() {
        String remoteHost = properties.getRemoteHost();
        if (remoteHost != null)
            return remoteHost;
        return sock.getLocalAddress().getHostName();
    }

    private void appendRequestAttributes(AJPMessage message, byte attribute,
            Map<String, String> values) {
        if (values == null || values.size() == 0)
            return;
        for (Entry<String, String> entry : values.entrySet()) {
            message.appendByte(AJPConstants.SC_A_REQ_ATTRIBUTE);
            message.appendString(entry.getKey());
            message.appendString(entry.getValue());
        }
    }

    private static void translate(AJPMessage message,
            MutableResponseHeader response) throws MessageFormatException {
        byte messageType = message.getByte();
        if (messageType != AJPConstants.JK_AJP13_SEND_HEADERS)
            throw new RuntimeException("Expected SEND_HEADERS, got "
                    + messageType);
        int status = message.getInt();
        response.setVersion("HTTP/1.1"); // must be, surely?
        response.setStatus(Integer.toString(status));
        String reason = message.getString();
        response.setReason(reason);
        int n = message.getInt();
        for (int i = 0; i < n; i++) {
            byte code = message.peekByte();
            String name;
            if (code == (byte) 0xA0) {
                int index = message.getInt();
                name = AJPConstants.getResponseHeader(index);
            } else {
                name = message.getString();
            }
            response.addHeader(name, message.getString());
        }

    }

    /**
     * @return the properties
     */
    public AJPProperties getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(AJPProperties properties) {
        this.properties = properties;
    }

    /**
     * @return the requestSubmissionTime
     */
    public long getRequestTime() {
        return requestSubmissionTime;
    }

    /**
     * @return the responseHeaderStartTime
     */
    public long getResponseHeaderTime() {
        return responseHeaderTime;
    }

}
