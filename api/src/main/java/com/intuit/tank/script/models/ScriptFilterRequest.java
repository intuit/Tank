/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script.models;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "filterRequest", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScriptFilterRequest", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "scriptId",
        "filterIds"
})
public class ScriptFilterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "scriptId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int scriptId;

    @XmlElement(name = "filterId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private List<Integer> filterIds = new ArrayList<Integer>();

    /**
     * @FrameworkUseOnly
     */
    protected ScriptFilterRequest() {

    }

    /**
     * @param scriptId
     * @param scripts
     */
    public ScriptFilterRequest(int scriptId, List<Integer> filterIds) {
        this.scriptId = scriptId;
        this.filterIds = filterIds;
    }

    /**
     * @return the scriptId
     */
    public int getScriptId() {
        return scriptId;
    }

    /**
     * @return the filterIds
     */
    public List<Integer> getFilterIds() {
        return filterIds;
    }

}
