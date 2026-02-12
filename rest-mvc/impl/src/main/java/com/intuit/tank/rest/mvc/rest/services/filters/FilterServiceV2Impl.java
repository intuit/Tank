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
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.filters.models.FilterContainer;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterGroupContainer;
import com.intuit.tank.filters.models.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.util.FilterServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptFilterUtil;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                    // DIAGNOSTIC: Track timing and memory for filter group performance analysis
                    long groupStart = System.nanoTime();
                    long groupStartMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    
                    ScriptFilterUtil.applyFilters(filterIds, script);
                    ScriptUtil.setScriptStepLabels(script);
                    
                    // DIAGNOSTIC: Log filter group performance metrics
                    long groupElapsed = (System.nanoTime() - groupStart) / 1_000_000;
                    long groupEndMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                    long groupMemDelta = (groupEndMem - groupStartMem) / 1024 / 1024;
                    
                    LOGGER.warn("FILTER_GROUP_PERF: scriptId={}, filterCount={}, totalTime={}ms, totalMemDelta={}MB", 
                        scriptId, filterIds.size(), groupElapsed, groupMemDelta);
                    
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

