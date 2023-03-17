/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.scripts;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "scriptUploadRequest", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScriptUploadRequest", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "script",
        "filterIds"
})
public class ScriptUploadRequest {

    @XmlElement(name = "script", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private ScriptDescription script;

    @XmlElement(name = "filterIds", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<Integer> filterIds;

    public ScriptUploadRequest() {

    }

    /**
     * create a descriptor from the script object
     * 
     * @param script
     */
    public ScriptUploadRequest(ScriptDescription script, List<Integer> filterIds) {
        this.script = script;
        this.filterIds = filterIds;
    }

    /**
     * @return the script
     */
    public ScriptDescription getScript() {
        return script;
    }

    /**
     * @return the filterIds
     */
    public List<Integer> getFilterIds() {
        return filterIds;
    }

}
