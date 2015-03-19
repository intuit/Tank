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

import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class DefaultClientContextSelector implements SSLContextSelector {

    private X509TrustManager trustManager;

    private Map<String, SSLContext> contextMap = new LinkedHashMap<String, SSLContext>();

    public DefaultClientContextSelector() {
        initTrustManager();
    }

    public SSLContext select(InetSocketAddress target) {
        String host = target.getHostName();
        SSLContext context = contextMap.get(host);
        if (context != null)
            return context;
        try {
            context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] { getTrustManager() },
                    new SecureRandom());
            contextMap.put(host, context);
        } catch (NoSuchAlgorithmException e) {
            // should never happen
            e.printStackTrace();
        } catch (KeyManagementException e) {
            // should never happen
            e.printStackTrace();
        }
        return context;
    }

    public void setTrustManager(X509TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    private void initTrustManager() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init((KeyStore) null);
            TrustManager[] managers = tmf.getTrustManagers();
            X509TrustManager manager = null;
            for (int i = 0; i < managers.length; i++) {
                if (managers[i] instanceof X509TrustManager) {
                    manager = new LoggingTrustManager(
                            (X509TrustManager) managers[i]);
                    break;
                }
            }
            if (manager == null) {
                manager = new X509TrustManager() {

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs,
                            String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs,
                            String authType) {
                    }
                };
            }
            trustManager = manager;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        } catch (KeyStoreException kse) {
            kse.printStackTrace();
        }
    }

    public X509TrustManager getTrustManager() {
        return trustManager;
    }

    private static class LoggingTrustManager implements X509TrustManager {

        private X509TrustManager trustManager;

        private HashMap<X509Certificate, X509Certificate[]> trusted, untrusted;

        public LoggingTrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
            trusted = new HashMap<X509Certificate, X509Certificate[]>();
            untrusted = new HashMap<X509Certificate, X509Certificate[]>();
        }

        public X509Certificate[] getAcceptedIssuers() {
            return trustManager.getAcceptedIssuers();
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
            if (trusted.containsKey(certs[0])
                    || untrusted.containsKey(certs[0]))
                return;
            String dn = certs[0].getSubjectX500Principal().getName();
            try {
                trustManager.checkClientTrusted(certs, authType);
                trusted.put(certs[0], certs);
            } catch (CertificateException ce) {
                untrusted.put(certs[0], certs);
                System.err.printf("Untrusted client certificate for %s", dn);
            }
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
            if (trusted.containsKey(certs[0])
                    || untrusted.containsKey(certs[0]))
                return;
            String dn = certs[0].getSubjectX500Principal().getName();
            try {
                trustManager.checkClientTrusted(certs, authType);
                trusted.put(certs[0], certs);
            } catch (CertificateException ce) {
                untrusted.put(certs[0], certs);
                System.err.printf("Untrusted server certificate for %s", dn);
            }
        }
    }

}
