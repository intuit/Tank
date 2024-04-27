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
import com.intuit.tank.project.*;
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
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.ServletContext;

import java.util.*;
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
            List<FilterTO> filters = all.stream()
                    .map(FilterServiceUtil::filterToTO)
                    .collect(Collectors.toList());
            return FilterContainer.builder().withFilters(filters).build();
        } catch(Exception e){
            LOGGER.error("Error returning all filters: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("filter", "all filters", e);
        }
    }

    @Override
    public FilterGroupContainer getFilterGroups() {
        try {
        List<ScriptFilterGroup> all = new ScriptFilterGroupDao().findAll();
        List<FilterGroupTO> filterGroups = all.stream()
                .map(FilterServiceUtil::filterGroupToTO)
                .collect(Collectors.toList());
        return FilterGroupContainer.builder().withFilterGroups(filterGroups).build();
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

                request.getFilterGroupIds().stream()
                        .map(dao::findById)
                        .filter(Objects::nonNull)
                        .flatMap(group -> group.getFilters().stream())
                        .map(ScriptFilter::getId)
                        .forEach(filterIds::add);

                if (!filterIds.isEmpty()) {
                    ScriptFilterUtil.applyFilters(filterIds, script);
                    ScriptUtil.setScriptStepLabels(script);

                    // attach new filterGroups
                    List<Integer> filterGroups = script.getFilterGroupIds() != null && !script.getFilterGroupIds().isEmpty()
                            ? Arrays.stream(script.getFilterGroupIds().trim().split(",")).map(Integer::parseInt).collect(Collectors.toList())
                            : new ArrayList<>();

                    filterGroups.addAll(request.getFilterGroupIds());
                    List<Integer> distinctFilterGroupIds = new ArrayList<>(new HashSet<>(filterGroups));
                    script.setFilterGroupIds(distinctFilterGroupIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

                    // attach new filters
                    List<Integer> filters = script.getFilterIds() != null && !script.getFilterIds().isEmpty()
                            ? Arrays.stream(script.getFilterIds().trim().split(",")).map(Integer::parseInt).collect(Collectors.toList())
                            : new ArrayList<>();

                    filters.addAll(request.getFilterIds());
                    List<Integer> distinctFilterIds = new ArrayList<>(new HashSet<>(filters));
                    script.setFilterIds(distinctFilterIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

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
        MessageEventSender sender = new ServletInjector<MessageEventSender>().getManagedBean(servletContext, MessageEventSender.class);
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

