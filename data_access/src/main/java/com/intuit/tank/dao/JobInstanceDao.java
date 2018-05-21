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
import java.util.List;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

/**
 * JobInstance
 * 
 * @author dangleton
 * 
 */
public class JobInstanceDao extends BaseDao<JobInstance> {

    /**
     * @param entityClass
     */
    public JobInstanceDao() {
        super();
        setReloadEntities(true);
    }

    public List<JobInstance> getForStatus(List<JobQueueStatus> statuses) {
        String prefix = "x";
        NamedParameter parameter = new NamedParameter(JobInstance.PROPERTY_STATUS, "status", statuses);
        return listWithJQL(buildQlSelect(prefix) + startWhere() + buildWhereClause(Operation.IN, prefix, parameter), parameter);
    }

    public List<JobInstance> findCompleted() {
        String prefix = "x";
        NamedParameter startTimeParam = new NamedParameter(JobInstance.PROPERTY_START_TIME, "startTime", null);
        NamedParameter endTimeParam = new NamedParameter(JobInstance.PROPERTY_END_TIME, "endTime", null);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.NOT_NULL, prefix, startTimeParam) + getAnd() +
                buildWhereClause(Operation.NOT_NULL, prefix, endTimeParam);
        return listWithJQL(sb);
    }

    public List<JobInstance> findNotCompleted() {
        String prefix = "x";
        List<JobQueueStatus> statuses = new ArrayList<JobQueueStatus>();
        statuses.add(JobQueueStatus.Completed);
        statuses.add(JobQueueStatus.Aborted);
        NamedParameter parameter = new NamedParameter(JobInstance.PROPERTY_STATUS, "status", statuses);
        String sb = buildQlSelect(prefix) + startWhere() +
                buildWhereClause(Operation.NOT_IN, prefix, parameter);
        return listWithJQL(sb, parameter);
    }

}
