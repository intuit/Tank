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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "teststep", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public abstract class TestStep {

    public abstract String getInfo();

    public int stepIndex;

    @XmlTransient
    private HDScriptUseCase parent;

    public HDScriptUseCase getParent() {
        return parent;
    }

    public void setParent(HDScriptUseCase parent) {
        this.parent = parent;
    }

    /**
     * @return the stepIndex
     */
    public int getStepIndex() {
        return stepIndex;
    }

    /**
     * @param stepIndex
     *            the stepIndex to set
     */
    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

}
