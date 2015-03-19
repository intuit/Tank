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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class DefaultServerContextSelector implements SSLContextSelector {

    private SSLContext sslContext = null;

    public DefaultServerContextSelector() throws GeneralSecurityException,
            IOException {
        this(null, "password", "password");
    }

    public DefaultServerContextSelector(String resource, String storePassword,
            String keyPassword) throws GeneralSecurityException, IOException {

        KeyStore ks = KeyStore.getInstance("PKCS12");
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                resource);
        if (is != null) {
            char[] ksp = storePassword.toCharArray();
            ks.load(is, ksp);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            char[] kp = keyPassword.toCharArray();
            kmf.init(ks, kp);
            sslContext = SSLContext.getInstance("SSLv3");
            sslContext.init(kmf.getKeyManagers(), null, null);
        } else
            throw new GeneralSecurityException("Couldn't find resource: "
                    + resource);
    }

    /**
     * This default implementation uses the same certificate for all hosts.
     * 
     * @return an SSLSocketFactory generated from the relevant server key material
     */
    public SSLContext select(InetSocketAddress target) {
        if (sslContext == null) {
            throw new NullPointerException("sslContext is null!");
        }
        return sslContext;
    }

}
