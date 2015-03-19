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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * ProjectAggregate
 * 
 * @author dangleton
 * 
 */
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
