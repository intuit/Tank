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
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.owasp.proxy.util.Base64;

public class AJPProperties {

    private static final String START_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n";

    private static final String END_CERTIFICATE = "\n-----END CERTIFICATE-----\n";

    private String remoteAddress, remoteHost, context, servletPath, remoteUser,
            authType, route, sslCert, sslCipher, sslSession, sslKeySize,
            secret, storedMethod;

    private Map<String, String> requestAttributes = null;

    public AJPProperties() {
    }

    public AJPProperties(AJPProperties properties) {
        remoteAddress = properties.remoteAddress;
        remoteHost = properties.remoteHost;
        context = properties.context;
        servletPath = properties.servletPath;
        remoteUser = properties.remoteUser;
        authType = properties.authType;
        route = properties.route;
        sslCert = properties.sslCert;
        sslCipher = properties.sslCipher;
        sslSession = properties.sslSession;
        sslKeySize = properties.sslKeySize;
        secret = properties.secret;
        storedMethod = properties.storedMethod;
        requestAttributes = properties.requestAttributes == null ? null
                : new HashMap<String, String>(properties.requestAttributes);
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRequestAttributes(Map<String, String> attributes) {
        this.requestAttributes = attributes;
    }

    public Map<String, String> getRequestAttributes() {
        if (requestAttributes == null)
            requestAttributes = new HashMap<String, String>();
        return requestAttributes;
    }

    /**
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * @param context
     *            the context to set
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * @return the servletPath
     */
    public String getServletPath() {
        return servletPath;
    }

    /**
     * @param servletPath
     *            the servletPath to set
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * @return the remoteUser
     */
    public String getRemoteUser() {
        return remoteUser;
    }

    /**
     * @param remoteUser
     *            the remoteUser to set
     */
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    /**
     * @return the authType
     */
    public String getAuthType() {
        return authType;
    }

    /**
     * @param authType
     *            the authType to set
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * @return the route
     */
    public String getRoute() {
        return route;
    }

    /**
     * @param route
     *            the route to set
     */
    public void setRoute(String route) {
        this.route = route;
    }

    /**
     * @return the sslCert
     */
    public String getSslCert() {
        return sslCert;
    }

    /**
     * @param sslCert
     *            the sslCert to set
     */
    public void setSslCert(String sslCert) {
        this.sslCert = sslCert;
    }

    public void setSslCert(X509Certificate cert)
            throws CertificateEncodingException, IOException {
        String buff = START_CERTIFICATE +
                Base64
                        .encodeBytes(cert.getEncoded(), Base64.DO_BREAK_LINES) +
                END_CERTIFICATE;
        setSslCert(buff);
    }

    /**
     * @return the sslCipher
     */
    public String getSslCipher() {
        return sslCipher;
    }

    /**
     * @param sslCipher
     *            the sslCipher to set
     */
    public void setSslCipher(String sslCipher) {
        this.sslCipher = sslCipher;
    }

    /**
     * @return the sslSession
     */
    public String getSslSession() {
        return sslSession;
    }

    /**
     * @param sslSession
     *            the sslSession to set
     */
    public void setSslSession(String sslSession) {
        this.sslSession = sslSession;
    }

    /**
     * @return the sslKeySize
     */
    public String getSslKeySize() {
        return sslKeySize;
    }

    /**
     * @param sslKeySize
     *            the sslKeySize to set
     */
    public void setSslKeySize(String sslKeySize) {
        this.sslKeySize = sslKeySize;
    }

    /**
     * @return the secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * @param secret
     *            the secret to set
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * @return the storedMethod
     */
    public String getStoredMethod() {
        return storedMethod;
    }

    /**
     * @param storedMethod
     *            the storedMethod to set
     */
    public void setStoredMethod(String storedMethod) {
        this.storedMethod = storedMethod;
    }

    /**
     * @param remoteAddress
     *            the remoteAddress to set
     */
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * @param remoteHost
     *            the remoteHost to set
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
}
