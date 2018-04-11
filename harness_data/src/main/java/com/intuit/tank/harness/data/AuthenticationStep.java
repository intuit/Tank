package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.intuit.tank.http.AuthScheme;

@XmlType(name = "authentication", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class AuthenticationStep extends TestStep {

    @XmlElement
    private String userName;

    @XmlElement
    private String password;

    @XmlElement
    private String realm;

    @XmlElement
    private AuthScheme scheme;

    @XmlElement
    private String host;

    @XmlElement
    private String port;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * @param realm
     *            the realm to set
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * @return the scheme
     */
    public AuthScheme getScheme() {
        return scheme;
    }

    /**
     * @param scheme
     *            the scheme to set
     */
    public void setScheme(AuthScheme scheme) {
        this.scheme = scheme;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String getInfo() {
        return new StringBuilder().append("Authentication(").append(scheme).append(", ").append(host).append(":").append(port).append("[").append(userName).append(", ").append(realm).append("])")
                .toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + scheme + host + userName;
    }

}
