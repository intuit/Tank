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
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.owasp.proxy.daemon.ConnectionHandler;
import org.owasp.proxy.daemon.Server;
import org.owasp.proxy.io.LoggingSocketWrapper;

public class AjpProxy {

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 2) {
            System.out.println("Usage: AjpProxy <host> <port>");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        DefaultAJPRequestHandler reqHandler = new DefaultAJPRequestHandler();
        reqHandler.setTarget(new InetSocketAddress(host, port));
        ConnectionHandler ch = new AJPConnectionHandler(reqHandler);
        ch = new LoggingConnectionHandler(ch);
        Server s = new Server(new InetSocketAddress("*", 8009), ch);
        s.start();
        System.out.println("Press a key to terminate");
        System.in.read();
        s.stop();
        System.out.println("Done");
    }

    private static class LoggingConnectionHandler implements ConnectionHandler {

        private ConnectionHandler delegate;

        public LoggingConnectionHandler(ConnectionHandler delegate) {
            this.delegate = delegate;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.owasp.proxy.daemon.ConnectionHandler#handleConnection(java.net .Socket)
         */
        public void handleConnection(Socket socket) throws IOException {
            PrintStream dump = System.err;
            String sock = "AJP " + socket.toString();
            socket = new LoggingSocketWrapper(socket, dump, sock + " << ", sock
                    + " >> ");
            delegate.handleConnection(socket);
        }

    }
}
