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
import java.net.Proxy;
import java.net.Socket;

import org.owasp.proxy.util.Pump;

public abstract class SelectiveConnectionHandler implements
        TargetedConnectionHandler {

    /**
     * Simply relays bytes from one socket to the other, ignoring any upstream proxy settings
     */
    protected static final TargetedConnectionHandler RELAY = (Socket src, InetSocketAddress target) -> {
        Socket dest = new Socket(Proxy.NO_PROXY);
        dest.connect(target);
        Pump.connect(src, dest);
    };

    public abstract TargetedConnectionHandler getConnectionHandler(
            InetSocketAddress target);

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.TargetedConnectionHandler#handleConnection(java .net.Socket,
     * java.net.InetSocketAddress)
     */
    public void handleConnection(Socket socket, InetSocketAddress target)
            throws IOException {
        TargetedConnectionHandler tch = getConnectionHandler(target);
        tch.handleConnection(socket, target);
    }

}
