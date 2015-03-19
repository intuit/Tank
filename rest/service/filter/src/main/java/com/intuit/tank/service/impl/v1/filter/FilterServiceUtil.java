/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.filter;

/*
 * #%L
 * Filter Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.api.model.v1.filter.FilterGroupTO;
import com.intuit.tank.project.ScriptFilterGroup;

/**
 * FilterServiceUtil
 * 
 * @author dangleton
 * 
 */
public class FilterServiceUtil {

    /**
     * @param g
     * @return
     */
    public static FilterGroupTO filterGroupToTO(ScriptFilterGroup g) {
        FilterGroupTO ret = new FilterGroupTO();
        ret.setId(g.getId());
        ret.setName(g.getName());
        ret.setProductName(g.getProductName());
        return ret;
    }

}
