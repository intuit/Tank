package com.intuit.tank.harness.ssl;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.TrustManagerFactory;
import com.sun.net.ssl.X509TrustManager;

/**
 * <p>
 * EasyX509TrustManager unlike default {@link X509TrustManager} accepts self-signed certificates.
 * </p>
 * <p>
 * This trust manager SHOULD NOT be used for productive systems due to security reasons, unless it is a concious
 * decision and you are perfectly aware of security implications of accepting self-signed certificates
 * </p>
 * 
 * @author <a href="mailto:adrian.sutton@ephox.com">Adrian Sutton</a>
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 *         <p>
 *         DISCLAIMER: HttpClient developers DO NOT actively support this component. The component is provided as a
 *         reference material, which may be inappropriate for use without additional customization.
 *         </p>
 */

@SuppressWarnings({ "deprecation", "restriction" })
public class EasyX509TrustManager implements X509TrustManager {
    private static final Logger LOG = LogManager.getLogger(EasyX509TrustManager.class);
    private X509TrustManager standardTrustManager = null;

    /**
     * Constructor for EasyX509TrustManager.
     */
    public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
        super();
        TrustManagerFactory factory = TrustManagerFactory.getInstance("SunX509");
        factory.init(keystore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException("SunX509 trust manager not supported");
        }
        this.standardTrustManager = (X509TrustManager) trustmanagers[0];
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#isClientTrusted(X509Certificate[])
     */
    public boolean isClientTrusted(X509Certificate[] certificates) {
        // return this.standardTrustManager.isClientTrusted(certificates);
        boolean clientTrusted = this.standardTrustManager.isClientTrusted(certificates);
        if (!clientTrusted) {
            LOG.warn(LogUtil.getLogMessage("Client not natively trusted. Ignoring and Trusting anyway.",
                    LogEventType.System));
        }
        return true;
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#isServerTrusted(X509Certificate[])
     */
    public boolean isServerTrusted(X509Certificate[] certificates) {
        // if ((certificates != null) && LOG.isDebugEnabled()) {
        // LOG.debug("Server certificate chain:");
        // for (int i = 0; i < certificates.length; i++) {
        // LOG.debug("X509Certificate[" + i + "]=" + certificates[i]);
        // }
        // }
        if ((certificates != null) && (certificates.length == 1)) {
            X509Certificate certificate = certificates[0];
            try {
                certificate.checkValidity();
            } catch (CertificateException e) {
                LOG.warn(LogUtil.getLogMessage("Sertver not natively trusted. Ignoring and Trusting anyway. Reason: "
                        + e.toString(), LogEventType.System));
                // return false;
            }
        } else {
            boolean serverTrusted = this.standardTrustManager.isServerTrusted(certificates);
            if (!serverTrusted) {
                LOG.warn("Server not natively trusted. Ignoring and Trusting anyway.");
            }
        }
        return true;
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }
}
