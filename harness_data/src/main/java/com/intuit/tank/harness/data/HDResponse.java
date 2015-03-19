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

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "response", propOrder = { "respFormat", "assignment", "validation" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class HDResponse {

    @XmlAttribute(name = "format")
    private String respFormat;
    @XmlElement(name = "assignments")
    private HDAssignment assignment = new HDAssignment();
    @XmlElement(name = "validations")
    private HDValidation validation = new HDValidation();

    public HDResponse() {
    }

    public HDResponse(@Nonnull String responseFormat) {
        this.respFormat = responseFormat;
    }

    /**
     * @return the respFormat
     */
    public String getRespFormat() {
        return respFormat;
    }

    /**
     * @return the assignment
     */
    public HDAssignment getAssignment() {
        return assignment;
    }

    /**
     * @return the validation
     */
    public HDValidation getValidation() {
        return validation;
    }

}
