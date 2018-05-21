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

package org.owasp.proxy.http.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.owasp.proxy.daemon.AddressResolver;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.io.ChunkedInputStream;
import org.owasp.proxy.io.EofNotifyingInputStream;
import org.owasp.proxy.io.FixedLengthInputStream;
import org.owasp.proxy.io.TimingInputStream;
import org.owasp.proxy.ssl.DefaultClientContextSelector;
import org.owasp.proxy.ssl.SSLContextSelector;
import org.owasp.proxy.util.AsciiString;

public class HttpClient {

    private static Logger logger = Logger.getLogger(HttpClient.class.getName());

    public static final ProxySelector NO_PROXY = new ProxySelector() {

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        }

        @Override
        public List<Proxy> select(URI uri) {
            return Collections.singletonList(Proxy.NO_PROXY);
        }
    };

    private static final InputStream NO_CONTENT = new ByteArrayInputStream(
            new byte[0]);

    public enum State {
        DISCONNECTED, CONNECTED, REQUEST_HEADER_SENT, REQUEST_CONTENT_SENT, RESPONSE_HEADER_READ, RESPONSE_CONTINUE, RESPONSE_CONTENT_IN_PROGRESS, RESPONSE_CONTENT_READ
    }

    private SSLContextSelector contextSelector = new DefaultClientContextSelector();

    private ProxySelector proxySelector = null;

    private AddressResolver resolver = null;

    protected Socket socket = null;

    private InetSocketAddress target = null;

    private boolean direct = true;

    private State state = State.DISCONNECTED;

    private boolean expectResponseContent;

    private InputStream responseContent = null;

    private long requestSubmissionTime, responseHeaderStartTime,
            responseHeaderEndTime;

    private int soTimeout = 10000;

    private String[] enabledProtocols = { "SSLv3", "TLSv1.2" };

    public HttpClient() {
        String s = System.getProperty("https.protocols");
        if (s != null && s.length() > 0) {
            String[] split = s.split(",");
            List<String> protos = Arrays.stream(split).map(String::trim).filter(candidate -> candidate.length() > 0).collect(Collectors.toList());
            if (protos.size() != 0) {
                enabledProtocols = protos.toArray(new String[protos.size()]);
            }
        }
    }

    public void setProxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }

    public ProxySelector getProxySelector() {
        if (proxySelector == null)
            return NO_PROXY;
        return proxySelector;
    }

    public void setSslEnabledProtocols(String[] protocols) {
        if (protocols == null) {
            enabledProtocols = null;
        } else {
            enabledProtocols = new String[protocols.length];
            System.arraycopy(protocols, 0, enabledProtocols, 0,
                    protocols.length);
        }
    }

    public String[] getSslEnabledProtocols() {
        if (enabledProtocols == null)
            return null;
        String[] protocols = new String[enabledProtocols.length];
        System.arraycopy(enabledProtocols, 0, protocols, 0,
                enabledProtocols.length);
        return protocols;
    }

    public void setSoTimeout(int timeout) {
        this.soTimeout = timeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSslContextSelector(SSLContextSelector contextSelector) {
        this.contextSelector = contextSelector;
    }

    public void setAddressResolver(AddressResolver resolver) {
        this.resolver = resolver;
    }

    public State getState() {
        return state;
    }

    protected void validateTarget(SocketAddress target) throws IOException {
    }

    private URI constructUri(boolean ssl, String host, int port)
            throws IOException {
        StringBuilder buff = new StringBuilder();
        if (ssl) {
            buff.append("https");
        } else {
            buff.append("http");
        }
        buff.append("://").append(host).append(":").append(port);
        try {
            return new URI(buff.toString());
        } catch (URISyntaxException use) {
            IOException ioe = new IOException("Unable to construct a URI");
            ioe.initCause(use);
            throw ioe;
        }
    }

    private boolean isConnected(InetSocketAddress target) {
        if (socket == null || socket.isClosed() || socket.isInputShutdown()) {
            return false;
        }
        if (target.equals(this.target)) {
            try {
                // FIXME: This only works because we don't implement pipelining!
                int oldtimeout = socket.getSoTimeout();
                try {
                    socket.setSoTimeout(1);
                    byte[] buff = new byte[1024];
                    int got = socket.getInputStream().read(buff);
                    if (got == -1) {
                        return false;
                    }
                    if (got > 0) {
                        logger.warning("Unexpected data read from socket ("
                                + got + " bytes):\n"
                                + AsciiString.create(buff, 0, got));
                        socket.close();
                        return false;
                    }
                } catch (SocketTimeoutException e) {
                    return true;
                } finally {
                    socket.setSoTimeout(oldtimeout);
                }
            } catch (IOException ioe) {
                logger.fine("Connection looks closed! Opening a new one");
                return false;
            }
        }
        return false;
    }

    private StreamingResponse proxyConnect(InetSocketAddress target)
            throws IOException, MessageFormatException {
        MutableRequestHeader req = new MutableRequestHeader.Impl();
        req.setStartLine("CONNECT " + target.getHostName() + ":"
                + target.getPort() + " HTTP/1.0");
        OutputStream out = socket.getOutputStream();
        out.write(req.getHeader());
        out.flush();

        return readResponse(socket.getInputStream());
    }

    public StreamingResponse connect(String host, int port, boolean ssl)
            throws IOException {
        return connect(new InetSocketAddress(host, port), ssl);
    }

    public StreamingResponse connect(InetSocketAddress target, boolean ssl)
            throws IOException {
        if (resolver != null) {
            InetAddress addr = resolver.getAddress(target.getHostName());
            target = new InetSocketAddress(addr, target.getPort());
        }

        if (target.isUnresolved()) {
            target = new InetSocketAddress(target.getHostName(), target
                    .getPort());
        }

        URI uri = constructUri(ssl, target.getHostName(), target.getPort());
        List<Proxy> proxies = getProxySelector().select(uri);

        if (isConnected(target)) {
            if (state == State.RESPONSE_CONTENT_READ
                    || state == State.CONNECTED) {
                return null;
            }
            disconnect();
        } else if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        this.target = target;

        socket = null;
        IOException lastAttempt = null;
        for (Proxy proxy : proxies) {
            direct = true;
            SocketAddress addr = proxy == Proxy.NO_PROXY ? target : proxy
                    .address();
            try {
                validateTarget(addr);
                if (proxy.type() == Proxy.Type.HTTP) {
                    socket = new Socket(Proxy.NO_PROXY);
                    socket.setSoTimeout(soTimeout);
                    socket.connect(addr);
                    if (ssl) {
                        try {
                            StreamingResponse proxyResponse = proxyConnect(target);
                            if (!"200".equals(proxyResponse.getStatus())) {
                                return proxyResponse;
                            }
                        } catch (MessageFormatException mfe) {
                            IOException ioe = new IOException(
                                    "Malformed proxy response");
                            ioe.initCause(mfe);
                            throw ioe;
                        }
                        layerSsl(target);
                    } else {
                        direct = false;
                    }
                } else {
                    socket = new Socket(proxy);
                    socket.setSoTimeout(soTimeout);
                    socket.connect(target);
                    if (ssl) {
                        layerSsl(target);
                    }
                }
            } catch (IOException ioe) {
                getProxySelector().connectFailed(uri, addr, ioe);
                lastAttempt = ioe;
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            }
            if (socket != null && socket.isConnected()) {
                // success
                state = State.CONNECTED;
                return null;
            }
        }
        if (lastAttempt != null) {
            throw lastAttempt;
        }
        throw new IOException("Couldn't connect to server");
    }

    private void layerSsl(InetSocketAddress target) throws IOException {
        if (contextSelector == null) {
            throw new IllegalStateException(
                    "SSL Context Selector is null, SSL is not supported!");
        }
        SSLContext sslContext = contextSelector.select(target);
        SSLSocketFactory factory = sslContext.getSocketFactory();
        SSLSocket sslsocket = (SSLSocket) factory.createSocket(socket, socket
                .getInetAddress().getHostName(), socket.getPort(), true);
        sslsocket.setEnabledProtocols(enabledProtocols); // should be set by settings
        sslsocket.setUseClientMode(true);
        sslsocket.setSoTimeout(soTimeout);
        sslsocket.startHandshake();
        socket = sslsocket;
    }

    public void sendRequestHeader(byte[] header) throws IOException,
            MessageFormatException {
        if (state == State.RESPONSE_CONTINUE) {
            throw new IllegalStateException(
                    "Cannot start a new request when the "
                            + "previous request content has not yet been sent");
        }
        if (state != State.CONNECTED && state != State.RESPONSE_CONTENT_READ) {
            throw new IllegalStateException(
                    "Illegal state. Can't send request headers when state is "
                            + state);
        }
        OutputStream os = new BufferedOutputStream(socket.getOutputStream());

        int resourceStart = -1;
        String method = null;
        for (int i = 0; i < header.length; i++) {
            if (method == null && Character.isWhitespace(header[i])) {
                method = AsciiString.create(header, 0, i - 1);
            }
            if (method != null && !Character.isWhitespace(header[i])
                    && resourceStart == -1) {
                resourceStart = i;
                break;
            }
            if (header[i] == '\r' || header[i] == '\n') {
                throw new MessageFormatException(
                        "Encountered CR or LF when parsing the URI!", header);
            }
        }
        expectResponseContent = !"HEAD".equals(method);
        if (!direct) {
            if (resourceStart > 0) {
                os.write(header, 0, resourceStart);
                os.write(("http://" + target.getHostName() + ":" + target
                        .getPort()).getBytes());
                os.write(header, resourceStart, header.length - resourceStart);
            } else {
                throw new MessageFormatException("Couldn't parse the URI!",
                        header);
            }
        } else {
            os.write(header);
        }
        os.flush();
        state = State.REQUEST_HEADER_SENT;
        requestSubmissionTime = System.currentTimeMillis();
    }

    public void sendRequestContent(byte[] content) throws IOException {
        if (content != null) {
            sendRequestContent(new ByteArrayInputStream(content));
        } else {
            sendRequestContent((InputStream) null);
        }
    }

    public void sendRequestContent(InputStream content) throws IOException {
        if (state != State.REQUEST_HEADER_SENT
                && state != State.RESPONSE_CONTINUE) {
            throw new IllegalStateException(
                    "Ilegal state. Can't send request content when state is "
                            + state);
        }
        if (content != null) {
            OutputStream os = socket.getOutputStream();
            byte[] buff = new byte[1024];
            int got;
            while ((got = content.read(buff)) > 0) {
                os.write(buff, 0, got);
            }
            os.flush();
        } else if (state == State.RESPONSE_CONTINUE)
            throw new IllegalStateException(
                    "Cannot send null content after a 100 Continue response!");
        state = State.REQUEST_CONTENT_SENT;
        requestSubmissionTime = System.currentTimeMillis();
    }

    private StreamingResponse readResponse(InputStream in) throws IOException,
            MessageFormatException {
        InputStream is = socket.getInputStream();
        HeaderByteArrayOutputStream header = new HeaderByteArrayOutputStream();
        StreamingResponse response = new StreamingResponse.Impl();
        int i = -1;
        try {
            while (!header.isEndOfHeader() && (i = is.read()) > -1) {
                header.write(i);
            }
            response.setHeaderTime(System.currentTimeMillis());
        } catch (SocketTimeoutException ste) {
            if (header.size() > 0) {
                MessageFormatException mfe = new MessageFormatException(
                        "Timeout reading response header", header.toByteArray());
                mfe.initCause(ste);
                throw mfe;
            }
            throw ste;
        }
        if (!header.isEndOfHeader() && i == -1) {
            if (header.size() > 0)
                throw new MessageFormatException("Invalid header ", header
                        .toByteArray());
            throw new IOException("Unexpected end of stream reading header");
        }

        response.setHeader(header.toByteArray());
        response.setContent(is);
        return response;
    }

    /**
     * returns the bytes of the response header.
     * 
     * NB: The response header may be a "100 Continue" response. Callers MUST check if the response code is "100", and
     * be prepared to call getHeader() again to retrieve the real response headers, BEFORE calling getResponseContent().
     * 
     * @return
     * @throws IOException
     * @throws MessageFormatException
     */
    public byte[] getResponseHeader() throws IOException,
            MessageFormatException {
        if (state != State.REQUEST_HEADER_SENT
                && state != State.REQUEST_CONTENT_SENT) {
            throw new IllegalStateException(
                    "Ilegal state. Can't read response header when state is "
                            + state);
        }
        InputStream is = socket.getInputStream();
        HeaderByteArrayOutputStream header = new HeaderByteArrayOutputStream();
        int i = -1;
        try {
            responseHeaderStartTime = responseHeaderEndTime = 0;
            while (!header.isEndOfHeader() && (i = is.read()) > -1) {
                if (responseHeaderStartTime == 0) {
                    responseHeaderStartTime = System.currentTimeMillis();
                }
                header.write(i);
            }
            responseHeaderEndTime = System.currentTimeMillis();
        } catch (SocketTimeoutException ste) {
            logger.fine("Timeout reading response header. Had read "
                    + header.size() + " bytes");
            if (header.size() > 0) {
                logger.fine(AsciiString.create(header.toByteArray()));
            }
            throw ste;
        }
        if (i == -1) {
            throw new IOException("Unexpected end of stream reading header");
        }
        MutableResponseHeader.Impl rh = new MutableResponseHeader.Impl();
        rh.setHeader(header.toByteArray());
        String status = rh.getStatus();
        if (status.equals("100")) {
            state = State.RESPONSE_CONTINUE;
        } else {
            state = State.RESPONSE_HEADER_READ;
            responseContent = getContentStream(rh, is);
        }
        return rh.getHeader();
    }

    private InputStream getContentStream(MutableResponseHeader header,
            InputStream is) throws IOException, MessageFormatException {
        String status = header.getStatus();
        if ("204".equals(status) || "304".equals(status)
                || !expectResponseContent) {
            return NO_CONTENT;
        } else {
            String transferCoding = header.getHeader("Transfer-Encoding");
            String contentLength = header.getHeader("Content-Length");
            if (transferCoding != null
                    && transferCoding.trim().equalsIgnoreCase("chunked")) {
                is = new ChunkedInputStream(is, true);
            } else if (contentLength != null) {
                try {
                    is = new FixedLengthInputStream(is, Integer
                            .parseInt(contentLength.trim()));
                } catch (NumberFormatException nfe) {
                    IOException ioe = new IOException(
                            "Invalid content-length header: " + contentLength);
                    ioe.initCause(nfe);
                    throw ioe;
                }
            }
            return is;
        }
    }

    public InputStream getResponseContent() throws IOException {
        if (state == State.RESPONSE_CONTINUE) {
            return null;
        }
        if (state != State.RESPONSE_HEADER_READ) {
            throw new IllegalStateException(
                    "Illegal state. Can't read response body when state is "
                            + state);
        }
        state = State.RESPONSE_CONTENT_IN_PROGRESS;
        return new EofNotifyingInputStream(responseContent) {
            @Override
            public void eof() {
                state = State.RESPONSE_CONTENT_READ;
            }
        };
    }

    public void disconnect() throws IOException {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } finally {
            socket = null;
            state = State.DISCONNECTED;
        }
    }

    public long getRequestTime() {
        return requestSubmissionTime;
    }

    public long getResponseHeaderStartTime() {
        return responseHeaderStartTime;
    }

    public long getResponseHeaderEndTime() {
        return responseHeaderEndTime;
    }

    public StreamingResponse fetchResponse(StreamingRequest request)
            throws IOException, MessageFormatException {
        StreamingResponse response = connect(request.getTarget(), request
                .isSsl());
        if (response != null)
            return response;
        response = new StreamingResponse.Impl();
        sendRequestHeader(request.getHeader());
        request.setTime(getRequestTime());
        if (MessageUtils.isExpectContinue(request)) {
            socket.setSoTimeout(2000);
            try {
                response.setHeader(getResponseHeader());
                response.setHeaderTime(getResponseHeaderEndTime());
            } catch (SocketTimeoutException ste) {
            } finally {
                socket.setSoTimeout(getSoTimeout());
            }
            if (response.getHeader() != null
                    && !"100".equals(response.getStatus())) {
                InputStream content = getResponseContent();
                if (content != null)
                    content = new TimingInputStream(content, response);
                response.setContent(content);
                return response;
            }
            if (request.getContent() != null) {
                sendRequestContent(request.getContent());
                request.setTime(getRequestTime());
            }
        } else {
            if (request.getContent() != null)
                sendRequestContent(request.getContent());
            request.setTime(System.currentTimeMillis());
        }
        if (response.getHeader() != null) {
            byte[] cont = response.getHeader();
            byte[] header = getResponseHeader();
            response.setHeaderTime(getResponseHeaderEndTime());
            byte[] newHeader = new byte[cont.length + header.length];
            System.arraycopy(cont, 0, newHeader, 0, cont.length);
            System.arraycopy(header, 0, newHeader, cont.length, header.length);
            response.setHeader(newHeader);
        } else {
            response.setHeader(getResponseHeader());
            response.setHeaderTime(getResponseHeaderEndTime());
        }
        InputStream content = getResponseContent();
        if (content != null)
            content = new TimingInputStream(content, response);
        response.setContent(content);
        return response;
    }

    private static class HeaderByteArrayOutputStream extends
            ByteArrayOutputStream {

        // we do it here because we have direct access to the buffer
        public boolean isEndOfHeader() {
            int i = count;
            return i > 4 && buf[i - 4] == '\r' && buf[i - 3] == '\n'
                    && buf[i - 2] == '\r' && buf[i - 1] == '\n';
        }

    }

}
