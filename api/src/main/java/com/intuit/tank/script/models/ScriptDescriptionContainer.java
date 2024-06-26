/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script.models;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "scriptDescriptions", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScriptDescriptionContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "scripts"
})
public class ScriptDescriptionContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "scripts", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "script", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ScriptDescription> scripts;

    /**
     * @return the scripts
     */
    public List<ScriptDescription> getScripts() {
        return scripts;
    }

}
