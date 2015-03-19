/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.filter;

/*
 * #%L
 * Filter Rest Api
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
 * ProjectContainer jaxb container for ProjectTo
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "FilterGroupContainer", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterGroupContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "filterGroups"
})
public class FilterGroupContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "projects", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "project", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<FilterGroupTO> filterGroups = new ArrayList<FilterGroupTO>();

    /**
     * @param filterGroups
     */
    public FilterGroupContainer(List<FilterGroupTO> filterGroups) {
        super();
        this.filterGroups = filterGroups;
    }

    /**
     * 
     */
    public FilterGroupContainer() {
        super();
    }

    /**
     * @return the filterGroups
     */
    public List<FilterGroupTO> getFilterGroups() {
        return filterGroups;
    }

}
