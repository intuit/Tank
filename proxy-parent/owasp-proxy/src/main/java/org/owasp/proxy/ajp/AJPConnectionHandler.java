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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.owasp.proxy.daemon.ConnectionHandler;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.NamedValue;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.io.BufferedInputStream;
import org.owasp.proxy.io.EofNotifyingInputStream;

public class AJPConnectionHandler implements ConnectionHandler {

    private static final byte[] PONG;

    static {
        AJPMessage msg = new AJPMessage(16);
        msg.reset();
        msg.appendByte(AJPConstants.JK_AJP13_CPONG_REPLY);
        msg.endServerMessage();
        PONG = msg.toByteArray();
    }

    private AJPRequestHandler handler;

    private int timeout = 30000;

    public AJPConnectionHandler(AJPRequestHandler handler) {
        this.handler = handler;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.ConnectionHandler#handleConnection(java.net.Socket )
     */
    public void handleConnection(Socket socket) throws IOException {
        socket.setSoTimeout(timeout);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        AJPMessage ajpRequest = new AJPMessage(8192);
        final StateHolder holder = new StateHolder();
        do {
            if (!holder.state.equals(State.READY))
                throw new IllegalStateException(
                        "Trying to read a new request in state " + holder.state);
            ajpRequest.readMessage(in);
            int type = ajpRequest.peekByte();
            switch (type) {
            case AJPConstants.JK_AJP13_CPING_REQUEST:
                out.write(PONG);
                out.flush();
            case AJPConstants.JK_AJP13_FORWARD_REQUEST:
                doForwardRequest(socket, ajpRequest, holder);
            }
        } while (true);
    }

    private void doForwardRequest(Socket socket, AJPMessage ajpRequest,
            final StateHolder holder) throws IOException {
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        InetAddress source = socket.getInetAddress();

        try {
            AJPRequest request = new AJPRequest();
            translate(ajpRequest, request);
            String cl = request.getHeader("Content-Length");
            if (cl != null) {
                long len = Long.parseLong(cl);
                if (len >= 0 && len < Integer.MAX_VALUE) {
                    InputStream content = new AJPInputStream(in, out,
                            (int) len, ajpRequest);
                    content = new EofNotifyingInputStream(content) {
                        protected void eof() {
                            holder.state = State.RESPONSE_HEADER;
                        }
                    };
                    request.setContent(content);
                    holder.state = State.REQUEST_CONTENT;
                } else {
                    throw new IllegalArgumentException(
                            "Invalid Content-Length: " + cl);
                }
            } else {
                holder.state = State.RESPONSE_HEADER;
            }
            StreamingResponse response = handler.handleRequest(source, request);
            if (holder.state != State.RESPONSE_HEADER)
                throw new IllegalStateException(
                        "handler did not read all the request content");
            AJPMessage ajpResponse = new AJPMessage(8192);
            translate(response, ajpResponse);
            ajpResponse.write(out);
            InputStream content = response.getContent();
            if (content != null) {
                int wrote;
                do {
                    ajpResponse.reset();
                    ajpResponse
                            .appendByte(AJPConstants.JK_AJP13_SEND_BODY_CHUNK);
                    wrote = ajpResponse.appendBytes(content,
                            AJPConstants.MAX_SEND_SIZE);
                    if (wrote == 0)
                        break;
                    ajpResponse.endServerMessage();
                    ajpResponse.write(out);
                } while (wrote > 0);
            }
            ajpResponse.reset();
            ajpResponse.appendByte(AJPConstants.JK_AJP13_END_RESPONSE);
            ajpResponse.endServerMessage();
            ajpResponse.write(out);
            holder.state = State.READY;
        } catch (MessageFormatException mfe) {
            mfe.printStackTrace();
            return;
        }

    }

    private static void translate(AJPMessage ajp, AJPRequest request)
            throws MessageFormatException {
        request.setHeader(null);
        if (ajp.getByte() != AJPConstants.JK_AJP13_FORWARD_REQUEST)
            throw new IllegalStateException(
                    "Can't translate this message into a request");
        request.setMethod(AJPConstants.getRequestMethod(ajp.getByte()));
        if (request.getMethod() == null)
            throw new RuntimeException("Unsupported request method");
        request.setVersion(ajp.getString());
        request.setResource(ajp.getString());
        request.setRemoteAddress(ajp.getString());
        request.setRemoteHost(ajp.getString());
        String target = ajp.getString();
        int port = ajp.getInt();
        request.setTarget(new InetSocketAddress(target, port));
        request.setSsl(ajp.getBoolean());

        getHeaders(ajp, request);
        getRequestAttributes(ajp, request);
    }

    private static void getHeaders(AJPMessage ajp, AJPRequest request)
            throws MessageFormatException {
        int len = ajp.getInt();
        for (int i = 0; i < len; i++) {
            byte coded = ajp.peekByte();
            String name;
            if (coded == (byte) 0xA0) {
                name = AJPConstants.getRequestHeader(ajp.getInt());
            } else {
                name = ajp.getString();
            }
            String value = ajp.getString();
            request.addHeader(name, value);
        }
    }

    private static void getRequestAttributes(AJPMessage ajp, AJPRequest request)
            throws MessageFormatException {
        byte attr = ajp.getByte();
        for (; attr != AJPConstants.SC_A_ARE_DONE; attr = ajp.getByte()) {
            switch (attr) {
            case AJPConstants.SC_A_CONTEXT:
                request.setContext(ajp.getString());
                break;
            case AJPConstants.SC_A_SERVLET_PATH:
                request.setServletPath(ajp.getString());
                break;
            case AJPConstants.SC_A_REMOTE_USER:
                request.setRemoteUser(ajp.getString());
                break;
            case AJPConstants.SC_A_AUTH_TYPE:
                request.setAuthType(ajp.getString());
                break;
            case AJPConstants.SC_A_QUERY_STRING:
                request.setResource(request.getResource() + "?"
                        + ajp.getString());
                break;
            case AJPConstants.SC_A_JVM_ROUTE:
                request.setRoute(ajp.getString());
                break;
            case AJPConstants.SC_A_SSL_CERT:
                request.setSslCert(ajp.getString());
                break;
            case AJPConstants.SC_A_SSL_CIPHER:
                request.setSslCipher(ajp.getString());
                break;
            case AJPConstants.SC_A_SECRET:
                request.setSecret(ajp.getString());
                break;
            case AJPConstants.SC_A_SSL_SESSION:
                request.setSslSession(ajp.getString());
                break;
            case AJPConstants.SC_A_REQ_ATTRIBUTE:
                request.getRequestAttributes().put(ajp.getString(),
                        ajp.getString());
                break;
            case AJPConstants.SC_A_SSL_KEY_SIZE:
                request.setSslKeySize(ajp.getString());
                break;
            case AJPConstants.SC_A_STORED_METHOD:
                request.setStoredMethod(ajp.getString());
                break;
            default:
                System.out.println("Unexpected request attribute: " + attr
                        + ": value was '" + ajp.getString() + "'");
            }
        }
    }

    private static void translate(MutableResponseHeader response,
            AJPMessage message) throws MessageFormatException {
        message.reset();
        message.appendByte(AJPConstants.JK_AJP13_SEND_HEADERS);
        message.appendInt(Integer.parseInt(response.getStatus()));
        message.appendString(response.getReason());
        NamedValue[] headers = response.getHeaders();
        if (headers == null)
            headers = new NamedValue[0];
        message.appendInt(headers.length);
        for (NamedValue header : headers) {
            int code = AJPConstants
                    .getResponseHeaderIndex(header.getName());
            if (code > 0) {
                message.appendInt(code);
            } else {
                message.appendString(header.getName());
            }
            message.appendString(header.getValue());
        }
        message.endServerMessage();
    }

    private static class AJPInputStream extends BufferedInputStream {
        private OutputStream out;
        private int len, read = 0;
        private AJPMessage request, response;

        public AJPInputStream(InputStream in, OutputStream out, int len,
                AJPMessage request) throws IOException {
            super(in, 8192);
            this.out = out;
            this.len = len;
            this.request = request;
            this.response = new AJPMessage(16);
        }

        protected void fillBuffer() throws IOException {
            if (read >= len) {
                buff = null;
                return;
            }
            request.readMessage(in);
            int size = request.peekInt();
            if (buff.length < size)
                buff = new byte[size];
            size = request.getBytes(buff);
            read += size;
            if (read > len)
                throw new IllegalStateException(
                        "Request body packet sizes mismatch! Expected " + len
                                + ", got " + read);
            start = 0;
            end = size;
            if (read < len) { // ask for more
                response.reset();
                response.appendByte(AJPConstants.JK_AJP13_GET_BODY_CHUNK);
                response.appendInt(Math.min(len - read,
                        AJPConstants.MAX_READ_SIZE));
                response.endServerMessage();
                response.write(out);
            }
        }

    }

    private static class StateHolder {
        public State state = State.READY;
    }

    private enum State {
        READY, REQUEST_HEADER, REQUEST_CONTENT, RESPONSE_HEADER, RESPONSE_CONTENT
    }

}
