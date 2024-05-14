/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script.models;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * ScriptStepContainer jaxb container for script steps
 * 
 * @author dangleton
 * 
 */
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "scripts"
})
public class ExternalScriptContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Singular(ignoreNullCollections = true)
    @XmlElementWrapper(name = "scripts", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "externalScript", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ExternalScriptTO> scripts;

    /**
     * @return the scripts
     */
    public List<ExternalScriptTO> getScripts() {
        return scripts;
    }

    /**
     * @param scripts
     *            the scripts to set
     */
    public void setScripts(List<ExternalScriptTO> scripts) {
        this.scripts = scripts;
    }

}
