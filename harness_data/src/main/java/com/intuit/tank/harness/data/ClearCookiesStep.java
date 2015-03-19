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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "clearCookiesStep", propOrder = { "dummy" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ClearCookiesStep extends TestStep {

    @XmlAttribute
    private String dummy = "clear";

    /**
     * @return the dummy var
     */
    public String getDummy() {
        return dummy;
    }

    /**
     * @return the dummy var
     */
    public void setDummy(String dummyvalue) {
        this.dummy = dummyvalue;
    }

    @Override
    public String getInfo() {
        return "Clear HTTP Session";
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
