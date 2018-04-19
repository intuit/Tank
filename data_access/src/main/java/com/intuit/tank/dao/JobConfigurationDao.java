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

import com.intuit.tank.project.JobConfiguration;

/**
 * ProductDao
 * 
 * @author dangleton
 * 
 */
public class JobConfigurationDao extends BaseDao<JobConfiguration> {

    /**
     * @param entityClass
     */
    public JobConfigurationDao() {
        super();
    }

    /**
     * gets a list of JobConfiguration for the given list of IDs.
     * 
     * @param ids
     *            the ids to get the configurations for.
     * @return the list of job configuration.
     */
    public List<JobConfiguration> getConfigurationsForWorkplace(Integer... ids) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(JobConfiguration.PROPERTY_WORKLOAD_ID, "wId", ids);
        return listWithJQL(buildQlSelect(prefix) + startWhere() + buildWhereClause(Operation.IN, prefix, parameter), parameter);
        // return super.listWithCriteria(Restrictions.in(JobConfiguration.PROPERTY_WORKLOAD_ID, ids));
    }

    /**
     * gets a list of JobConfiguration for the given project id.
     * 
     * @param projectId
     *            the id of the project to get the JobConfigurations for.
     * @return the list of job configuration.
     */
    public List<JobConfiguration> getConfigurationsForProject(int projectId) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(JobConfiguration.PROPERTY_WORKLOAD_ID, "pId", projectId);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.EQUALS, prefix, parameter);
        return listWithJQL(sb, parameter);
        // return super.listWithCriteria(Restrictions.eq(JobConfiguration.PROPERTY_PROJECT_ID, projectId));
    }

}
