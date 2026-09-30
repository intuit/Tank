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
import com.intuit.tank.filters.models.FilterGroupDetailTO;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterGroupContainer;
import com.intuit.tank.filters.models.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.util.FilterServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptFilterUtil;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.util.ScriptFilterType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.vm.settings.AccessRight;
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
    public FilterGroupDetailTO getFilterGroup(Integer filterGroupId){
        try {
            ScriptFilterGroupDao dao = new ScriptFilterGroupDao();
            ScriptFilterGroup filterGroup = dao.findById(filterGroupId);
            return FilterServiceUtil.filterGroupToDetailTO(filterGroup);
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
    public FilterTO createOrUpdateFilter(FilterTO request) {
        ScriptFilterDao dao = new ScriptFilterDao();
        try {
            if (request == null) {
                throw new IllegalArgumentException("Filter request is required");
            }
            if (request.getName() == null || request.getName().isBlank()) {
                throw new IllegalArgumentException("Filter name is required");
            }
            if (request.getFilterType() != null && !ScriptFilterType.INTERNAL.name().equals(request.getFilterType())) {
                throw new IllegalArgumentException("Only internal filters are supported");
            }
            if (request.getExternalScriptId() != null) {
                throw new IllegalArgumentException("Internal filters cannot reference an external script");
            }

            ScriptFilter filter = new ScriptFilter();
            if (request.getId() != null && request.getId() > 0) {
                filter = dao.findById(request.getId());
                if (filter == null) {
                    throw new IllegalArgumentException("Filter with filter id " + request.getId() + " does not exist");
                }
                RestAuthorization.requireRightOrOwner(AccessRight.EDIT_FILTER, filter, "filters");
            } else {
                RestAuthorization.requireRight(AccessRight.CREATE_FILTER, "filters");
            }

            // the owner is the caller for new filters and unchanged for existing ones; never taken from the request
            String creator = filter.getCreator() != null ? filter.getCreator() : RestAuthorization.currentUserName();
            FilterServiceUtil.toScriptFilter(request, filter);
            filter.setCreator(creator);
            return FilterServiceUtil.filterToTO(dao.saveOrUpdate(filter));
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error saving filter: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("filter", "filter", e);
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
            LOGGER.error("Error returning all filter groups: {}", e.getMessage(), e);
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
                RestAuthorization.requireRightOrOwner(AccessRight.EDIT_SCRIPT, script, "filters");
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
                    script = new ScriptDao().saveOrUpdate(script);
                    sendMsg(script, ModificationType.UPDATE);
                    return "Filters applied";
                }
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch(Exception e){
            LOGGER.error("Error applying filter to script: {}", e.getMessage(), e);
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
                LOGGER.warn("Filter with filter id {} does not exist", filterId);
                return "Filter with filter id " + filterId + " does not exist";
            } else {
                RestAuthorization.requireRightOrOwner(AccessRight.DELETE_FILTER, filter, "filters");
                dao.delete(filter);
                return "";
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting filter: {}", e, e);
            throw new GenericServiceDeleteException("filter", "filter", e);
        }
    }

    @Override
    public String deleteFilterGroup(Integer filterGroupId) {
        try {
            ScriptFilterGroupDao dao = new ScriptFilterGroupDao();
            ScriptFilterGroup filterGroup = dao.findById(filterGroupId);
            if (filterGroup == null) {
                LOGGER.warn("Filter Group with id {} does not exist", filterGroupId);
                return "Filter Group with filter group id " + filterGroupId + " does not exist";
            } else {
                RestAuthorization.requireRightOrOwner(AccessRight.DELETE_FILTER, filterGroup, "filters");
                dao.delete(filterGroup);
                return "";
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting FilterGroup: {}", e, e);
            throw new GenericServiceDeleteException("filter", "filterGroup", e);
        }
    }
}

