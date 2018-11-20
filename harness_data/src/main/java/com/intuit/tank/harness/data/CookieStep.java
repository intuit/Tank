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

@XmlType(name = "cookieStep", propOrder = { "cookieName", "cookieValue", "cookieDomain", "cookiePath" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class CookieStep extends TestStep {

    @XmlElement
    private String cookieName = "";

    @XmlElement
    private String cookieValue = "";

    @XmlElement
    private String cookieDomain = "";

    @XmlElement
    private String cookiePath = "/";

    /**
     * @return the cookieName
     */
    public String getCookieName() {
        return cookieName;
    }

    /**
     * @param cookieName
     *            the cookieName to set
     */
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    /**
     * @return the cookieValue
     */
    public String getCookieValue() {
        return cookieValue;
    }

    /**
     * @param cookieValue
     *            the cookieValue to set
     */
    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    /**
     * @return the cookieDomain
     */
    public String getCookieDomain() {
        return cookieDomain;
    }

    /**
     * @return the cookiePath
     */
    public String getCookiePath() {
        return cookiePath;
    }

    /**
     * @param cookiePath
     *            the cookiePath to set
     */
    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    /**
     * @param cookieDomain
     *            the cookieDomain to set
     */
    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    @Override
    public String getInfo() {
        return "Set Cookie: domain: " + cookieDomain + "; " + cookieName +
                " = " + cookieValue;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + cookieName + "=" + cookieValue;
    }

}
