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

import com.intuit.tank.project.SummaryData;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class SummaryDataDao extends BaseDao<SummaryData> {

    /**
     * @param entityClass
     */
    public SummaryDataDao() {
        super();
        setReloadEntities(false);
    }

    /**
     * 
     * @param name
     * @return
     */
    public List<SummaryData> findByJobId(int jobId) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(SummaryData.PROPERTY_JOB_ID, "j", jobId);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.EQUALS, prefix, parameter) +
                buildSortOrderClause(SortDirection.ASC, prefix, SummaryData.PROPERTY_PAGE_ID);
        return super.listWithJQL(sb, parameter);
    }

}
