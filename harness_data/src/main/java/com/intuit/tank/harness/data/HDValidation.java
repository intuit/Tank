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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "headerValidation", "cookieValidation", "bodyValidation" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class HDValidation {

    @XmlElementWrapper(name = "headerValidations")
    @XmlElement(name = "validation")
    private List<ValidationData> headerValidation = new ArrayList<ValidationData>();
    @XmlElementWrapper(name = "cookieValidations")
    @XmlElement(name = "validation")
    private List<ValidationData> cookieValidation = new ArrayList<ValidationData>();
    @XmlElementWrapper(name = "bodyValidations")
    @XmlElement(name = "validation")
    private List<ValidationData> bodyValidation = new ArrayList<ValidationData>();

    /**
     * @return the headerValidation
     */
    public List<ValidationData> getHeaderValidation() {
        return headerValidation;
    }

    /**
     * @return the cookieValidation
     */
    public List<ValidationData> getCookieValidation() {
        return cookieValidation;
    }

    /**
     * @return the bodyValidation
     */
    public List<ValidationData> getBodyValidation() {
        return bodyValidation;
    }

}
