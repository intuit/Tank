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

package org.owasp.proxy.ssl;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.io.PushbackSocket;

/*
 * Of interest is RFC 3546 (http://tools.ietf.org/html/rfc3546#section-3.1), which specifies
 * a Server Name Indicator extension which can be provided by the client to indicate
 * which SSL server it is trying to connect to. This could be used for the case where we
 * are acting as an SSL Server, but we don't have a specified target, or we are prepared
 * to allow the client to override the target. There is no support for this in the standard JRE
 * at present, but is expected to be added in JDK7.
 */

public class SSLConnectionHandler implements TargetedConnectionHandler {

    private SSLContextSelector sslContextSelector;

    private boolean detect;

    private EncryptedConnectionHandler next;

    public SSLConnectionHandler(SSLContextSelector sslContextSelector,
            boolean detect, EncryptedConnectionHandler next) {
        this.sslContextSelector = sslContextSelector;
        this.detect = detect;
        this.next = next;
    }

    private byte[] sniff(PushbackSocket socket, int len) throws IOException {
        PushbackInputStream in = socket.getInputStream();
        byte[] sniff = new byte[len];
        int read = 0, attempt = 0;
        do {
            int got = in.read(sniff, read, sniff.length - read);
            if (got == -1)
                return null;
            read += got;
            attempt++;
        } while (read < sniff.length && attempt < sniff.length);
        if (read < sniff.length)
            throw new IOException("Failed to read " + len
                    + " bytes in as many attempts!");
        in.unread(sniff);
        return sniff;
    }

    protected SSLSocketFactory getSSLSocketFactory(InetSocketAddress target)
            throws IOException, GeneralSecurityException {
        SSLContext sslContext = sslContextSelector == null ? null
                : sslContextSelector.select(target);
        return sslContext == null ? null : sslContext.getSocketFactory();
    }

    protected SSLSocket negotiateSSL(Socket socket, SSLSocketFactory factory)
            throws GeneralSecurityException, IOException {
        SSLSocket sslsock = (SSLSocket) factory.createSocket(socket, socket
                .getInetAddress().getHostName(), socket.getPort(), true);
        sslsock.setUseClientMode(false);
        return sslsock;
    }

    protected final boolean isSSL(byte[] sniff) {
        for (int i = 0; i < sniff.length; i++)
            if (sniff[i] == 0x03)
                return true;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.ConnectionHandler#handleConnection(java.net.Socket , java.net.InetSocketAddress)
     */
    public void handleConnection(Socket socket, InetSocketAddress target)
            throws IOException {
        boolean ssl = true;
        if (detect) {
            PushbackSocket pbs = socket instanceof PushbackSocket ? (PushbackSocket) socket
                    : new PushbackSocket(socket);
            socket = pbs;
            // check if it is an SSL connection
            byte[] sniff = sniff(pbs, 4);
            if (sniff == null) // connection closed
                return;

            if (!isSSL(sniff))
                ssl = false;
        }

        if (ssl) {
            try {
                SSLSocketFactory factory = getSSLSocketFactory(target);
                if (factory == null)
                    return;
                socket = negotiateSSL(socket, factory);
                if (socket == null)
                    return;
            } catch (GeneralSecurityException gse) {
                IOException ioe = new IOException(
                        "Error obtaining the certificate");
                ioe.initCause(gse);
                throw ioe;
            }
        }

        next.handleConnection(socket, target, ssl);
    }
}
