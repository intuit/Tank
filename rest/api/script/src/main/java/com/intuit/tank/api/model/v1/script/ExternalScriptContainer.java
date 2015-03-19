/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * ScriptStepContainer jaxb container for script steps
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalScriptContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "scripts"
})
public class ExternalScriptContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "scripts", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "externalScript", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ExternalScriptTO> scripts = new ArrayList<ExternalScriptTO>();

    public ExternalScriptContainer() {

    }

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
