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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "scriptGroupStep", propOrder = { "useCase" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class HDScriptGroupStep {
    @XmlElement
    private HDScriptUseCase useCase;

    /**
     * @return the useCase
     */
    public HDScriptUseCase getUseCase() {
        return useCase;
    }

    /**
     * @param useCase
     *            the useCase to set
     */
    public void setUseCase(HDScriptUseCase useCase) {
        this.useCase = useCase;
    }

}
