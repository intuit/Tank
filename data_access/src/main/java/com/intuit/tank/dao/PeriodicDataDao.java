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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.project.PeriodicData;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class PeriodicDataDao extends BaseDao<PeriodicData> {
    private static final Logger LOG = LogManager.getLogger(PeriodicDataDao.class);

    /**
     * @param entityClass
     */
    public PeriodicDataDao() {
        super();
        setReloadEntities(false);
    }

    /**
     * 
     * @param name
     * @return
     */
    public List<PeriodicData> findByJobId(int jobId) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(PeriodicData.PROPERTY_JOB_ID, "j", jobId);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.EQUALS, prefix, parameter) +
                buildSortOrderClause(SortDirection.ASC, prefix, PeriodicData.PROPERTY_TIMESTAMP);
        return super.listWithJQL(sb, parameter);
    }

    /**
     * 
     * @param name
     * @return
     */
    public List<PeriodicData> findByJobId(int jobId, Date minDate, Date maxDate) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(PeriodicData.PROPERTY_JOB_ID, "j", jobId);
        StringBuilder sb = new StringBuilder();
        List<NamedParameter> paramList = new ArrayList<NamedParameter>();
        paramList.add(parameter);
        sb.append(buildQlSelect(prefix)).append(startWhere())
                .append(buildWhereClause(Operation.EQUALS, prefix, parameter));
        if (minDate != null) {
            NamedParameter minParam = new NamedParameter(PeriodicData.PROPERTY_TIMESTAMP, "minTime", minDate);
            LOG.info(minDate);
            sb.append(getAnd()).append(buildWhereClause(Operation.GREATER_THAN_OR_EQUALS, prefix, minParam));
            paramList.add(minParam);
        }
        if (maxDate != null) {
            LOG.info(maxDate);
            NamedParameter maxParam = new NamedParameter(PeriodicData.PROPERTY_TIMESTAMP, "maxTime", maxDate);
            sb.append(getAnd()).append(buildWhereClause(Operation.LESS_THAN, prefix, maxParam));
            paramList.add(maxParam);
        }
        sb.append(buildSortOrderClause(SortDirection.ASC, prefix, PeriodicData.PROPERTY_TIMESTAMP));
        LOG.info(sb.toString());
        return super.listWithJQL(sb.toString(), paramList.toArray(new NamedParameter[paramList.size()]));
    }

}
