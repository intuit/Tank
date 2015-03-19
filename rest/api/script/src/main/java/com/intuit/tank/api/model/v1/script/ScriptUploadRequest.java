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
