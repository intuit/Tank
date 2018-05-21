/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.dao;

/*
 * #%L
 * Data Access
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

import javax.annotation.Nonnull;

import com.intuit.tank.project.ScriptFilterGroup;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class FilterGroupDao extends BaseDao<ScriptFilterGroup> {

    /**
     * @param entityClass
     */
    public FilterGroupDao() {
        super();
    }

    /**
     * @param productName
     * @return
     */
    @Nonnull
    public List<ScriptFilterGroup> getFilterGroupsForProduct(@Nonnull String productName) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(ScriptFilterGroup.PROPERTY_PRODUCT_NAME, "productName",
                productName);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.EQUALS, prefix, parameter);
        return listWithJQL(sb, parameter);
    }

}
