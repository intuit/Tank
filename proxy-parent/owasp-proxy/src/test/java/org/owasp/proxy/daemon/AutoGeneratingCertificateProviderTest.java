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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.junit.Test;
import org.owasp.proxy.ssl.AutoGeneratingContextSelector;
import org.owasp.proxy.ssl.EncryptedConnectionHandler;
import org.owasp.proxy.ssl.SSLConnectionHandler;
import org.owasp.proxy.ssl.SSLContextSelector;

public class AutoGeneratingCertificateProviderTest {

    @Test
    public void test() {
        // Dummy to shut maven up
    }

    public static void main(String[] args) throws Exception {

        X500Principal ca = new X500Principal("cn=OWASP Custom CA for "
                + java.net.InetAddress.getLocalHost().getHostName() + " at "
                + new Date()
                + ",ou=OWASP Custom CA,o=OWASP,l=OWASP,st=OWASP,c=OWASP");

        SSLContextSelector cp = new AutoGeneratingContextSelector(ca);
        EncryptedConnectionHandler ech = new EncryptedConnectionHandler() {

            /*
             * (non-Javadoc)
             * 
             * @see org.owasp.proxy.daemon.EncryptedConnectionHandler#handleConnection (java.net.Socket,
             * java.net.InetSocketAddress, boolean)
             */
            public void handleConnection(Socket socket,
                    InetSocketAddress target, boolean ssl) throws IOException {
                InputStream is = socket.getInputStream();
                byte[] buff = new byte[1024];
                int got;
                while ((got = is.read(buff)) > -1) {
                    System.out.write(buff, 0, got);
                }
            }

        };
        TargetedConnectionHandler sslch = new SSLConnectionHandler(cp, false,
                ech);
        Proxy proxy = new Proxy(new InetSocketAddress("localhost", 4433),
                sslch, new InetSocketAddress("www.example.com", 443));
        proxy.start();
        System.out.println("Started");
        System.in.read();
        proxy.stop();
        System.out.println("Stopped");
    }
}
