/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.filters;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.dao.FilterGroupDao;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.project.Script;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterTO;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterContainer;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterGroupTO;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterGroupContainer;
import com.intuit.tank.rest.mvc.rest.models.filters.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.util.FilterServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptFilterUtil;
import com.intuit.tank.service.impl.v1.automation.MessageSender;
import com.intuit.tank.service.util.ServletInjector;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterServiceV2Impl implements FilterServiceV2 {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LogManager.getLogger(FilterServiceV2Impl.class);

    @Override
    public String ping() {
        return "PONG " + getClass().getInterfaces()[0].getSimpleName();
    }


    @Override
    public FilterTO getFilter(Integer filterId){
        try {
            ScriptFilterDao dao = new ScriptFilterDao();
            ScriptFilter filter = dao.findById(filterId);
            return FilterServiceUtil.filterToTO(filter);
        } catch(Exception e){
            LOGGER.error("Error returning specific filter: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("filter", "filter", e);
        }
    }

    @Override
    public FilterGroupTO getFilterGroup(Integer filterGroupId){
        try {
            ScriptFilterGroupDao dao = new ScriptFilterGroupDao();
            ScriptFilterGroup filterGroup = dao.findById(filterGroupId);
            return FilterServiceUtil.filterGroupToTO(filterGroup);
        } catch(Exception e){
            LOGGER.error("Error returning specific filter: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("filter", "filterGroup", e);
        }
    }

    @Override
    public FilterContainer getFilters() {
        try {
            List<ScriptFilter> all = new ScriptFilterDao().findAll();
            List<FilterTO> ret = all.stream()
                    .map(FilterServiceUtil::filterToTO)
                    .collect(Collectors.toList());
            return new FilterContainer(ret);
        } catch(Exception e){
            LOGGER.error("Error returning all filters: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("filter", "all filters", e);
        }
    }

    @Override
    public FilterGroupContainer getFilterGroups() {
        try {
        List<ScriptFilterGroup> all = new ScriptFilterGroupDao().findAll();
        List<FilterGroupTO> ret = all.stream()
                .map(FilterServiceUtil::filterGroupToTO)
                .collect(Collectors.toList());
        return new FilterGroupContainer(ret);
        } catch(Exception e){
            LOGGER.error("Error returning all filter groups: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("filter", "all filter groups", e);
        }
    }

    @Override
    public String applyFilters(Integer scriptId, ApplyFiltersRequest request) {
        try {
            if (scriptId != null) {
                Script script = new ScriptDao().findById(scriptId);
                if (script == null){
                    return "Script with that script ID does not exist";
                }
                List<Integer> filterIds = new ArrayList<>(request.getFilterIds());
                FilterGroupDao dao = new FilterGroupDao();
                for (Integer id : request.getFilterGroupIds()) {
                    ScriptFilterGroup group = dao.findById(id);
                    if (group != null) {
                        for (ScriptFilter filter : group.getFilters()) {
                            filterIds.add(filter.getId());
                        }
                    }
                }
                if (!filterIds.isEmpty()) {
                    ScriptFilterUtil.applyFilters(filterIds, script);
                    ScriptUtil.setScriptStepLabels(script);
                    script = new ScriptDao().saveOrUpdate(script);
                    sendMsg(script, ModificationType.UPDATE);
                    return "Filters applied";
                }
            }
        } catch(Exception e){
            LOGGER.error("Error applying filter to script: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("filter", "script", e);
        }
        return null;
    }
    private void sendMsg(BaseEntity entity, ModificationType type) {
        MessageSender sender = new ServletInjector<MessageSender>().getManagedBean(servletContext, MessageSender.class);
        sender.sendEvent(new ModifiedEntityMessage(entity.getClass(), entity.getId(), type));
    }

    @Override
    public String deleteFilter(Integer filterId) {
        try {
            ScriptFilterDao dao = new ScriptFilterDao();
            ScriptFilter filter = dao.findById(filterId);
            if (filter == null) {
                LOGGER.warn("Filter with filter id " + filterId + " does not exist");
                return "Filter with filter id " + filterId + " does not exist";
            } else {
                dao.delete(filter);
                return "";
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting filter: " + e, e);
            throw new GenericServiceDeleteException("filter", "filter", e);
        }
    }

    @Override
    public String deleteFilterGroup(Integer filterGroupId) {
        try {
            ScriptFilterGroupDao dao = new ScriptFilterGroupDao();
            ScriptFilterGroup filterGroup = dao.findById(filterGroupId);
            if (filterGroup == null) {
                LOGGER.warn("Filter Group with id " + filterGroupId + " does not exist");
                return "Filter Group with filter group id " + filterGroupId + " does not exist";
            } else {
                dao.delete(filterGroup);
                return "";
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting FilterGroup: " + e, e);
            throw new GenericServiceDeleteException("filter", "filterGroup", e);
        }
    }
}

