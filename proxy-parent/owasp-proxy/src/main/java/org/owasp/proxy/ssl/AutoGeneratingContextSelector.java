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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

import org.owasp.proxy.util.Base64;
import org.owasp.proxy.util.BouncyCastleCertificateUtils;

public class AutoGeneratingContextSelector implements SSLContextSelector {

    private static final long DEFAULT_VALIDITY = 10L * 365L * 24L * 60L * 60L
            * 1000L;

    private static Logger logger = Logger
            .getLogger(AutoGeneratingContextSelector.class.getName());

    private boolean reuseKeys = false;

    private Map<String, SSLContext> contextCache = new HashMap<String, SSLContext>();

    private PrivateKey caKey;

    private X509Certificate[] caCerts;

    private Set<BigInteger> serials = new HashSet<BigInteger>();

    /**
     * creates a {@link AutoGeneratingContextSelector} that will create a RSA {@link KeyPair} and self-signed
     * {@link X509Certificate} based on the {@link X500Principal} supplied. The user can call
     * {@link #save(File, String, char[], char[], String)} to save the generated details at a later stage.
     * 
     * @param ca
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public AutoGeneratingContextSelector(X500Principal ca)
            throws GeneralSecurityException, IOException {
        create(ca);
    }

    /**
     * creates a {@link AutoGeneratingContextSelector} that will use the supplied {@link PrivateKey} and
     * {@link X509Certificate} chain
     * 
     * @param caKey
     *            the CA key
     * @param caCerts
     *            the certificate chain
     */
    public AutoGeneratingContextSelector(PrivateKey caKey,
            X509Certificate[] caCerts) {
        this.caKey = caKey;
        this.caCerts = new X509Certificate[caCerts.length];
        System.arraycopy(caCerts, 0, this.caCerts, 0, caCerts.length);
    }

    /**
     * creates a {@link AutoGeneratingContextSelector} that will load its CA {@link PrivateKey} and
     * {@link X509Certificate} chain from the indicated keystore
     * 
     * @param keystore
     *            the location of the keystore
     * @param type
     *            the keystore type
     * @param password
     *            the keystore password
     * @param keyPassword
     *            the key password
     * @param caAlias
     *            the alias of the key entry
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public AutoGeneratingContextSelector(File keystore, String type,
            char[] password, char[] keyPassword, String caAlias)
            throws GeneralSecurityException, IOException {
        initFromKeyStore(keystore, type, password, keyPassword, caAlias);
    }

    private void initFromKeyStore(File ks, String type, char[] kspassword,
            char[] keyPassword, String alias) throws GeneralSecurityException,
            IOException {
        InputStream in = new FileInputStream(ks);
        try {
            KeyStore keyStore = KeyStore.getInstance(type);
            keyStore.load(in, kspassword);
            caKey = (PrivateKey) keyStore.getKey(alias, keyPassword);
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            caCerts = new X509Certificate[certChain.length];
            System.arraycopy(certChain, 0, caCerts, 0, certChain.length);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            in.close();
        }
    }

    private void create(X500Principal caName) throws GeneralSecurityException,
            IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048, new SecureRandom());
        KeyPair caPair = keyGen.generateKeyPair();
        caKey = caPair.getPrivate();
        PublicKey caPubKey = caPair.getPublic();
        Date begin = new Date();
        Date ends = new Date(begin.getTime() + DEFAULT_VALIDITY);

        X509Certificate cert = BouncyCastleCertificateUtils.sign(caName, caPubKey,
                caName, caPubKey, caKey, begin, ends, BigInteger.ONE);
        caCerts = new X509Certificate[] { cert };
    }

    public String getCACert() throws CertificateEncodingException {
        try {
            return "-----BEGIN CERTIFICATE-----\n"
                    + Base64.encodeBytes(caCerts[0].getEncoded(),
                            Base64.DO_BREAK_LINES)
                    + "\n-----END CERTIFICATE-----\n";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Saves the CA key and the Certificate chain to the specified keystore
     * 
     * @param keyStore
     *            the file to save the keystore to
     * @param type
     *            the keystore type (PKCS12, JKS, etc)
     * @param password
     *            the keystore password
     * @param keyPassword
     *            the key password
     * @param caAlias
     *            the alias to save the key and cert under
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void save(File keyStore, String type, char[] password,
            char[] keyPassword, String caAlias)
            throws GeneralSecurityException, IOException {
        KeyStore store = KeyStore.getInstance(type);
        store.load(null, password);
        store.setKeyEntry(caAlias, caKey, keyPassword, caCerts);
        OutputStream out = new FileOutputStream(keyStore);
        try {
            store.store(out, password);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            out.close();
        }
    }

    /**
     * Determines whether the public and private key used for the CA will be reused for other hosts as well.
     * 
     * This is mostly just a performance optimisation, to save time generating a key pair for each host. Paranoid
     * clients may have an issue with this, in theory.
     * 
     * @param reuse
     *            true to reuse the CA key pair, false to generate a new key pair for each host
     */
    public synchronized void setReuseKeys(boolean reuse) {
        reuseKeys = reuse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.CertificateProvider#getSocketFactory(java.lang .String, int)
     */
    public synchronized SSLContext select(InetSocketAddress target) {
        String host = target.getHostName();
        SSLContext sslContext = contextCache.get(host);
        if (sslContext == null) {
            try {
                X509KeyManager km = createKeyMaterial(host);
                sslContext = SSLContext.getInstance("SSLv3");
                sslContext.init(new KeyManager[] { km }, null, null);
                contextCache.put(host, sslContext);
            } catch (GeneralSecurityException gse) {
                logger.warning("Error obtaining the SSLContext: "
                        + gse.getLocalizedMessage());
            }
        }
        return sslContext;
    }

    protected X500Principal getSubjectPrincipal(String host) {
        return new X500Principal("cn=" + host + ",ou=UNTRUSTED,o=UNTRUSTED");
    }

    private X509KeyManager createKeyMaterial(String host)
            throws GeneralSecurityException {
        KeyPair keyPair;

        if (reuseKeys) {
            keyPair = new KeyPair(caCerts[0].getPublicKey(), caKey);
        } else {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(2048, new SecureRandom());
            keyPair = keygen.generateKeyPair();
        }

        X500Principal subject = getSubjectPrincipal(host);
        Date begin = new Date();
        Date ends = new Date(begin.getTime() + DEFAULT_VALIDITY);

        X509Certificate cert = BouncyCastleCertificateUtils.sign(subject, keyPair
                .getPublic(), caCerts[0].getSubjectX500Principal(), caCerts[0]
                .getPublicKey(), caKey, begin, ends, getNextSerialNo());

        X509Certificate[] chain = new X509Certificate[caCerts.length + 1];
        System.arraycopy(caCerts, 0, chain, 1, caCerts.length);
        chain[0] = cert;

        return new SingleX509KeyManager(host, keyPair.getPrivate(), chain);
    }

    protected BigInteger getNextSerialNo() {
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        while (serials.contains(serial))
            serial = serial.add(BigInteger.ONE);
        serials.add(serial);
        return serial;
    }

}
