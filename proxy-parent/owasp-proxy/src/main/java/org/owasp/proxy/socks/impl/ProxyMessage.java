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

package org.owasp.proxy.socks.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Abstract class which describes SOCKS4/5 response/request.
 */
public abstract class ProxyMessage {

    /** Host as an IP address */
    public InetAddress ip = null;

    /** SOCKS version, or version of the response for SOCKS4 */
    public int version;

    /** Port field of the request/response */
    public int port;

    /** Request/response code as an int */
    public int command;

    /** Host as string. */
    public String host = null;

    /** User field for SOCKS4 request messages */
    public String user = null;

    ProxyMessage(int command, InetAddress ip, int port) {
        this.command = command;
        this.ip = ip;
        this.port = port;
    }

    ProxyMessage() {
    }

    /**
     * Initialises Message from the stream. Reads server response from given stream.
     * 
     * @param in
     *            Input stream to read response from.
     * @throws SocksException
     *             If server response code is not SOCKS_SUCCESS(0), or if any error with protocol occurs.
     * @throws IOException
     *             If any error happens with I/O.
     */
    public abstract void read(InputStream in) throws SocksException,
            IOException;

    /**
     * Initialises Message from the stream. Reads server response or client request from given stream.
     * 
     * @param in
     *            Input stream to read response from.
     * @param clinetMode
     *            If true read server response, else read client request.
     * @throws SocksException
     *             If server response code is not SOCKS_SUCCESS(0) and reading in client mode, or if any error with
     *             protocol occurs.
     * @throws IOException
     *             If any error happens with I/O.
     */
    public abstract void read(InputStream in, boolean client_mode)
            throws SocksException, IOException;

    /**
     * Writes the message to the stream.
     * 
     * @param out
     *            Output stream to which message should be written.
     */
    public abstract void write(OutputStream out) throws SocksException,
            IOException;

    /**
     * Get the Address field of this message as InetAddress object.
     * 
     * @return Host address or null, if one can't be determined.
     */
    public InetAddress getInetAddress() throws UnknownHostException {
        return ip;
    }

    /**
     * Get string representation of this message.
     * 
     * @return string representation of this message.
     */
    public String toString() {
        return "Proxy Message:\n" + "Version:" + version + "\n" + "Command:"
                + command + "\n" + "IP:     " + ip + "\n" + "Port:   " + port
                + "\n" + "User:   " + user + "\n";
    }

}
