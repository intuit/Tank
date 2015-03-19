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

@XmlType(propOrder = { "headerVariable", "cookieVariable", "bodyVariable" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class HDAssignment {

    @XmlElementWrapper(name = "headerVariables")
    @XmlElement(name = "variable")
    private List<AssignmentData> headerVariable = new ArrayList<AssignmentData>();

    @XmlElementWrapper(name = "cookieVariables")
    @XmlElement(name = "variable")
    private List<AssignmentData> cookieVariable = new ArrayList<AssignmentData>();

    @XmlElementWrapper(name = "bodyVariables")
    @XmlElement(name = "variable")
    private List<AssignmentData> bodyVariable = new ArrayList<AssignmentData>();

    /**
     * @return the headerValidation
     */
    public List<AssignmentData> getHeaderVariable() {
        return headerVariable;
    }

    /**
     * @return the cookieValidation
     */
    public List<AssignmentData> getCookieVariable() {
        return cookieVariable;
    }

    /**
     * @return the bodyValidation
     */
    public List<AssignmentData> getBodyVariable() {
        return bodyVariable;
    }

}
