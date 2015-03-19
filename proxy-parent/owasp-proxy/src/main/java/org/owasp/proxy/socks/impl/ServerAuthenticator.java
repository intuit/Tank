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
import java.net.Socket;

/**
 * Classes implementing this interface should provide socks server with authentication and authorization of users.
 **/
public interface ServerAuthenticator {

    /**
     * This method is called when a new connection accepted by the server.
     * <p>
     * At this point no data have been extracted from the connection. It is responsibility of this method to ensure that
     * the next byte in the stream after this method have been called is the first byte of the socks request message.
     * For SOCKSv4 there is no authentication data and the first byte in the stream is part of the request. With SOCKSv5
     * however there is an authentication data first. It is expected that implementations will process this
     * authentication data.
     * <p>
     * If authentication was successful an instance of ServerAuthentication should be returned, it later will be used by
     * the server to perform authorization and some other things. If authentication fails null should be returned, or an
     * exception may be thrown.
     * 
     * @param s
     *            Accepted Socket.
     * @return An instance of ServerAuthenticator to be used for this connection or null
     */
    ServerAuthenticator startSession(Socket s) throws IOException;

    /**
     * This method should return input stream which should be used on the accepted socket.
     * <p>
     * SOCKSv5 allows to have multiple authentication methods, and these methods might require some kind of
     * transformations being made on the data.
     * <p>
     * This method is called on the object returned from the startSession function.
     */
    InputStream getInputStream();

    /**
     * This method should return output stream to use to write to the accepted socket.
     * <p>
     * SOCKSv5 allows to have multiple authentication methods, and these methods might require some kind of
     * transformations being made on the data.
     * <p>
     * This method is called on the object returned from the startSession function.
     */
    OutputStream getOutputStream();

    /**
     * This method is called when a request have been read.
     * <p>
     * Implementation should decide whether to grant request or not. Returning true implies granting the request, false
     * means request should be rejected.
     * <p>
     * This method is called on the object returned from the startSession function.
     * 
     * @param msg
     *            Request message.
     * @return true to grant request, false to reject it.
     */
    boolean checkRequest(ProxyMessage msg);

    /**
     * This method is called when session is completed. Either due to normal termination or due to any error condition.
     * <p>
     * This method is called on the object returned from the startSession function.
     */
    void endSession();
}
