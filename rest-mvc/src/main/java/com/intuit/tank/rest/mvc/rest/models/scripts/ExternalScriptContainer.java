/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.scripts;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
