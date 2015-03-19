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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "testGroup", propOrder = { "name", "loop", "useCase" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class HDScript {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private int loop;
    @XmlElement(name = "useCase")
    private List<HDScriptUseCase> useCase = new ArrayList<HDScriptUseCase>();

    @XmlTransient
    private HDScriptGroup parent;

    public HDScriptGroup getParent() {
        return parent;
    }

    public void setParent(HDScriptGroup parent) {
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
     * @return the loop
     */
    public int getLoop() {
        return loop;
    }

    /**
     * @param loop
     *            the loop to set
     */
    public void setLoop(int loop) {
        this.loop = loop;
    }

    /**
     * @return the groupSteps
     */
    public List<HDScriptUseCase> getUseCase() {
        return useCase;
    }

}
