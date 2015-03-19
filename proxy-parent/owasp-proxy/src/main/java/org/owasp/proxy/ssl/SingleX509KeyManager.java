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

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

public class SingleX509KeyManager implements X509KeyManager {

    private String alias;

    private PrivateKey pk;

    private X509Certificate[] certs;

    public SingleX509KeyManager(String alias, PrivateKey pk,
            X509Certificate[] certs) {
        this.alias = alias;
        this.pk = pk;
        this.certs = copy(certs);
    }

    public String chooseClientAlias(String[] keyType, Principal[] issuers,
            Socket socket) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public String chooseServerAlias(String keyType, Principal[] issuers,
            Socket socket) {
        return alias;
    }

    public X509Certificate[] getCertificateChain(String alias) {
        return copy(certs);
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public PrivateKey getPrivateKey(String alias) {
        return pk;
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return new String[] { alias };
    }

    private X509Certificate[] copy(X509Certificate[] certs) {
        if (certs == null)
            return null;
        X509Certificate[] copy = new X509Certificate[certs.length];
        System.arraycopy(certs, 0, copy, 0, certs.length);
        return copy;
    }

}
