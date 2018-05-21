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

package org.owasp.proxy.daemon;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executor;

/**
 * This class implements a Proxy server. The user is required to provide an implementation of
 * {@link TargetedConnectionHandler} implementing the desired protocol.
 * 
 * The major difference between {@link Server} and Proxy is that Proxy may have a target address. i.e. connections
 * received by the Proxy should be directed to the specified target.
 * 
 * @author Rogan Dawes
 * 
 */
public class Proxy extends Server {

    public Proxy(InetSocketAddress listen,
            final TargetedConnectionHandler connectionHandler,
            final InetSocketAddress target) throws IOException {
        this(listen, null, connectionHandler, target);
    }

    public Proxy(InetSocketAddress listen, Executor executor,
            final TargetedConnectionHandler connectionHandler,
            final InetSocketAddress target) throws IOException {
        super(listen, executor, connectionHandler == null ? null
                : (Socket socket) -> {
                    connectionHandler.handleConnection(socket, target);
                });
    }
}
