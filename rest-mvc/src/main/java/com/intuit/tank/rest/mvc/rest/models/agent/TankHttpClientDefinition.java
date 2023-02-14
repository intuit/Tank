/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.agent;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "tankHttpClientDefinition", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TankHttpClientDefinition", namespace = Namespace.NAMESPACE_V1)
public class TankHttpClientDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String name;

    @XmlElement(name = "className", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String className;

    /**
     * @param name
     * @param className
     */
    public TankHttpClientDefinition(String name, String className) {
        this.name = name;
        this.className = className;
    }
    /**
     * @frameworkuseonly
     */
    protected TankHttpClientDefinition() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return name + " (" + className + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
