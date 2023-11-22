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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "useCase", propOrder = { "name", "scriptSteps" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class HDScriptUseCase {
    @XmlAttribute
    private String name;
    @XmlElement(name = "testStep")
    private List<TestStep> scriptSteps = new ArrayList<TestStep>();

    @XmlTransient
    private HDScript parent;

    public HDScript getParent() {
        return parent;
    }

    public void setParent(HDScript parent) {
        this.parent = parent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the scriptSteps
     */
    public List<TestStep> getScriptSteps() {
        return scriptSteps;
    }

}
