/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterGroupTO;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterTO;

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

    public static FilterTO filterToTO(ScriptFilter g) {
        FilterTO ret = new FilterTO();
        ret.setId(g.getId());
        ret.setName(g.getName());
        ret.setProductName(g.getProductName());
        return ret;
    }
}
