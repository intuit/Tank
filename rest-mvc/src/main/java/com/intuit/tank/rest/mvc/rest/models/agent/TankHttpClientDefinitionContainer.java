/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.agent;

import javax.annotation.Nonnull;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "TankHttpClientDefinitionContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TankHttpClientDefinitionContainer", namespace = Namespace.NAMESPACE_V1)
public class TankHttpClientDefinitionContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "definitions", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "tankHttpClientDefinition", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<TankHttpClientDefinition> definitions = new ArrayList<TankHttpClientDefinition>();

    @XmlElement(name = "defaultDefinition", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String defaultDefinition;

    /**
     * @param definitions
     * @param defaultDefinition
     */
    public TankHttpClientDefinitionContainer(@Nonnull List<TankHttpClientDefinition> definitions, String defaultDefinition) {
        super();
        this.definitions = definitions;
        this.defaultDefinition = defaultDefinition;
    }
    /**
     * @FrameworkUseOnly
     */
    protected TankHttpClientDefinitionContainer() {
    }

    /**
     * @return the definitions
     */
    @Nonnull
    public List<TankHttpClientDefinition> getDefinitions() {
        return definitions;
    }

    /**
     * @return the defaultDefinition
     */
    public String getDefaultDefinition() {
        return defaultDefinition;
    }
    
    

}
