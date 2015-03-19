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

package org.owasp.proxy.socks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;

import org.owasp.proxy.socks.impl.ProxyMessage;
import org.owasp.proxy.socks.impl.ServerAuthenticator;
import org.owasp.proxy.socks.impl.ServerAuthenticatorNone;
import org.owasp.proxy.socks.impl.Socks4Message;
import org.owasp.proxy.socks.impl.Socks5Message;
import org.owasp.proxy.socks.impl.SocksConstants;
import org.owasp.proxy.socks.impl.SocksException;

public class SocksProtocolHandler {

    private Socket socket;

    private InputStream in;

    private OutputStream out;

    private ProxyMessage msg;

    private ServerAuthenticator auth;

    public SocksProtocolHandler(Socket accept, ServerAuthenticator auth) {
        this.socket = accept;
        if (auth == null) {
            this.auth = new ServerAuthenticatorNone();
        } else {
            this.auth = auth;
        }
    }

    public InetSocketAddress handleConnectRequest() throws IOException {
        try {
            return startSession();
        } catch (IOException ioe) {
            handleException(ioe);
            throw ioe;
        }
    }

    private InetSocketAddress startSession() throws IOException {
        auth = auth.startSession(socket);

        if (auth == null) { // Authentication failed
            throw new SocksException(SocksConstants.SOCKS_AUTH_FAILURE);
        }

        in = auth.getInputStream();
        out = auth.getOutputStream();

        msg = readMsg(in);
        return handleRequest(msg);
    }

    private void handleException(IOException ioe) {
        // If we couldn't read the request, return;
        if (msg == null)
            return;
        int error_code = SocksConstants.SOCKS_FAILURE;

        if (ioe instanceof SocksException)
            error_code = ((SocksException) ioe).getErrorCode();
        else if (ioe instanceof NoRouteToHostException)
            error_code = SocksConstants.SOCKS_HOST_UNREACHABLE;
        else if (ioe instanceof ConnectException)
            error_code = SocksConstants.SOCKS_CONNECTION_REFUSED;
        else if (ioe instanceof InterruptedIOException)
            error_code = SocksConstants.SOCKS_TTL_EXPIRE;

        if (error_code > SocksConstants.SOCKS_ADDR_NOT_SUPPORTED
                || error_code < 0) {
            error_code = SocksConstants.SOCKS_FAILURE;
        }

        sendErrorMessage(error_code);
    }

    private void sendErrorMessage(int error_code) {
        ProxyMessage err_msg;
        if (msg instanceof Socks4Message)
            err_msg = new Socks4Message(Socks4Message.REPLY_REJECTED);
        else
            err_msg = new Socks5Message(error_code);
        try {
            err_msg.write(out);
        } catch (IOException ioe) {
        }
    }

    private ProxyMessage readMsg(InputStream in) throws IOException {
        PushbackInputStream push_in;
        if (in instanceof PushbackInputStream)
            push_in = (PushbackInputStream) in;
        else
            push_in = new PushbackInputStream(in);

        int version = push_in.read();
        push_in.unread(version);

        ProxyMessage msg;

        if (version == 5) {
            msg = new Socks5Message(push_in, false);
        } else if (version == 4) {
            msg = new Socks4Message(push_in, false);
        } else {
            throw new SocksException(SocksConstants.SOCKS_FAILURE);
        }
        return msg;
    }

    private InetSocketAddress handleRequest(ProxyMessage msg)
            throws IOException {
        if (!auth.checkRequest(msg))
            throw new SocksException(SocksConstants.SOCKS_FAILURE);

        if (msg.ip == null && !(msg instanceof Socks5Message))
            // Socks5 allows specifying a host name
            throw new SocksException(SocksConstants.SOCKS_FAILURE);

        switch (msg.command) {
        case SocksConstants.SOCKS_CMD_CONNECT:
            return onConnect(msg);
        default:
            throw new SocksException(SocksConstants.SOCKS_CMD_NOT_SUPPORTED);
        }
    }

    private InetSocketAddress onConnect(ProxyMessage msg) throws IOException {
        ProxyMessage response = null;

        if (msg instanceof Socks5Message) {
            response = new Socks5Message(SocksConstants.SOCKS_SUCCESS, msg.ip,
                    msg.port);
        } else {
            response = new Socks4Message(Socks4Message.REPLY_OK, msg.ip,
                    msg.port);
        }
        response.write(out);

        return new InetSocketAddress(msg.host, msg.port);
    }

}
