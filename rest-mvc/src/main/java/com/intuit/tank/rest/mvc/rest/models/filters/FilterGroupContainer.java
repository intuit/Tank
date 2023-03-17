/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.filters;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
