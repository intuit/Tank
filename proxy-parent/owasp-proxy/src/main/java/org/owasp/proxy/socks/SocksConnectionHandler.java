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
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.owasp.proxy.daemon.ConnectionHandler;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.io.PushbackSocket;

public class SocksConnectionHandler implements TargetedConnectionHandler,
        ConnectionHandler {

    private TargetedConnectionHandler next;

    boolean detect;

    public SocksConnectionHandler(TargetedConnectionHandler next, boolean detect) {
        this.next = next;
        this.detect = detect;
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
            // System.out.println(read);
        } while (read < sniff.length && attempt < sniff.length);
        if (read < sniff.length)
            throw new IOException("Failed to read " + len
                    + " bytes in as many attempts!");
        in.unread(sniff);
        return sniff;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.ConnectionHandler#handleConnection(java.net.Socket )
     */
    public void handleConnection(Socket socket) throws IOException {
        handleConnection(socket, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.ConnectionHandler#handleConnection(java.net.Socket , java.net.InetSocketAddress)
     */
    public void handleConnection(Socket socket, InetSocketAddress target)
            throws IOException {
        boolean socks = true;
        if (detect) {
            PushbackSocket pbs = socket instanceof PushbackSocket ? (PushbackSocket) socket
                    : new PushbackSocket(socket);
            socket = pbs;
            // check if it is a SOCKS request
            byte[] sniff = sniff(pbs, 1);
            if (sniff == null) // connection closed
                return;

            if (!(sniff[0] == 4 || sniff[0] == 5)) // SOCKS v4 or V5
                socks = false;
        }

        if (socks) {
            SocksProtocolHandler sp = new SocksProtocolHandler(socket, null);
            target = sp.handleConnectRequest();
        }

        next.handleConnection(socket, target);
    }

}
