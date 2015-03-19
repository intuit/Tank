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

package org.owasp.proxy.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.Socket;

public class PushbackSocket extends SocketWrapper {

    public PushbackSocket(Socket socket) throws IOException {
        super(socket, new PushbackInputStream(socket.getInputStream(), 128),
                socket.getOutputStream());
    }

    protected PushbackSocket(Socket socket, PushbackInputStream in,
            OutputStream out) throws IOException {
        super(socket, in, out);
    }

    public PushbackInputStream getInputStream() {
        return (PushbackInputStream) super.getInputStream();
    }

}
