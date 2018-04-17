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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.intuit.tank.api.model.v1.filter.FilterGroupContainer;
import com.intuit.tank.api.model.v1.filter.FilterGroupTO;
import com.intuit.tank.api.service.v1.filter.FilterService;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.project.ScriptFilterGroup;

/**
 * AutomationServiceV1
 * 
 * @author dangleton
 * 
 */
@Path(FilterService.SERVICE_RELATIVE_PATH)
public class FilterServiceV1 implements FilterService {

    // private static final Logger LOG = LogManager.getLogger(FilterServiceV1.class);

    // @Context
    // private ServletContext servletContext;

    /**
     * @inheritDoc
     */
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getFilterGroups() {
        ResponseBuilder response = Response.ok();
        List<FilterGroupTO> ret;
        ScriptFilterGroupDao dao = new ScriptFilterGroupDao();
        List<ScriptFilterGroup> all = dao.findAll();
        ret = all.stream().map(FilterServiceUtil::filterGroupToTO).collect(Collectors.toList());
        response.entity(new FilterGroupContainer(ret));
        return response.build();
    }

}
