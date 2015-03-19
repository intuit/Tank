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
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

public class KeystoreUtils {

    public static X509KeyManager getKeyManagerForAlias(KeyStore keyStore,
            String alias, char[] password) throws GeneralSecurityException {
        Key key = keyStore.getKey(alias, password);
        Certificate[] chain = keyStore.getCertificateChain(alias);
        if (key instanceof PrivateKey) {
            X509Certificate[] certChain = new X509Certificate[chain.length];
            for (int i = 0; i < chain.length; i++) {
                if (chain[i] instanceof X509Certificate) {
                    certChain[i] = (X509Certificate) chain[i];
                } else {
                    throw new InvalidKeyException("CA cert[" + i
                            + "] is not an X509Certificate: " + chain[i]);
                }
            }
            return new SingleX509KeyManager(alias, (PrivateKey) key, certChain);
        } else {
            throw new InvalidKeyException(
                    "The CA private key should implement PrivateKey");
        }
    }

    public static X509KeyManager loadFromKeyStore(InputStream in, String type,
            String alias, char[] password) throws GeneralSecurityException,
            IOException {
        KeyStore ks = KeyStore.getInstance(type);
        ks.load(in, password);
        return getKeyManagerForAlias(ks, alias, password);
    }

    public static void addToKeyStore(KeyStore keyStore, X509KeyManager km,
            String alias, char[] password) throws GeneralSecurityException {
        keyStore.setKeyEntry(alias, km.getPrivateKey(alias), password, km
                .getCertificateChain(alias));
    }

    public static void saveToKeyStore(OutputStream out, X509KeyManager km,
            String alias, String type, char[] password)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(type);
        ks.load(null, password);
        addToKeyStore(ks, km, alias, password);
        ks.store(out, password);
    }

}
