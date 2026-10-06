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

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;
import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilterServiceV2Impl implements FilterServiceV2 {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LogManager.getLogger(FilterServiceV2Impl.class);
    private static final String SERVICE = "filters";
    private static final int MAX_NAME_LENGTH = 255;

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
            String error = filterError(request);
            if (error != null) {
                throw new IllegalArgumentException(error);
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
    public FilterTO createFilter(FilterTO request) {
        RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireRight(AccessRight.CREATE_FILTER, SERVICE);
        requireValidFilter(request);
        ScriptFilter filter = FilterServiceUtil.toScriptFilter(request, new ScriptFilter());
        filter.setCreator(RestAuthorization.currentUserName());
        return saveFilter(filter, "filter");
    }

    @Override
    public FilterTO updateFilter(Integer filterId, FilterTO request) {
        RestAuthorization.requireUser(SERVICE);
        ScriptFilter filter = findFilter(filterId);
        RestAuthorization.requireRightOrOwner(AccessRight.EDIT_FILTER, filter, SERVICE);
        requireValidFilter(request);
        requireCurrent(request.getModified(), filter.getModified(), "Filter " + filterId);
        String creator = filter.getCreator();
        FilterServiceUtil.toScriptFilter(request, filter);
        filter.setCreator(creator);
        return saveFilter(filter, "filter");
    }

    @Override
    public FilterTO copyFilter(Integer filterId, CopyRequest request) {
        RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireRight(AccessRight.CREATE_FILTER, SERVICE);
        String name = requireName(request != null ? request.name() : null);
        ScriptFilter source = findFilter(filterId);
        // round-trip through the TO so the copy gets new condition and action rows
        ScriptFilter copy = FilterServiceUtil.toScriptFilter(FilterServiceUtil.filterToTO(source), new ScriptFilter());
        copy.setName(name);
        copy.setCreator(RestAuthorization.currentUserName());
        FilterTO saved = saveFilter(copy, "filter copy");
        LOGGER.info("{} copied filter {} to {} ({})", copy.getCreator(), filterId, saved.getId(), name);
        return saved;
    }

    @Override
    public FilterGroupDetailTO createFilterGroup(FilterGroupTO request) {
        RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireRight(AccessRight.CREATE_FILTER, SERVICE);
        if (request == null) {
            throw new GenericServiceBadRequestException(SERVICE, "filterGroup", "request body is required");
        }
        ScriptFilterGroup group = new ScriptFilterGroup();
        applyGroup(request, group);
        group.setCreator(RestAuthorization.currentUserName());
        return saveFilterGroup(group, "filter group");
    }

    @Override
    public FilterGroupDetailTO updateFilterGroup(Integer filterGroupId, FilterGroupTO request) {
        RestAuthorization.requireUser(SERVICE);
        ScriptFilterGroup group = findFilterGroup(filterGroupId);
        RestAuthorization.requireRightOrOwner(AccessRight.EDIT_FILTER, group, SERVICE);
        if (request == null) {
            throw new GenericServiceBadRequestException(SERVICE, "filterGroup", "request body is required");
        }
        requireCurrent(request.getModified(), group.getModified(), "Filter group " + filterGroupId);
        applyGroup(request, group);
        return saveFilterGroup(group, "filter group");
    }

    @Override
    public FilterGroupDetailTO copyFilterGroup(Integer filterGroupId, CopyRequest request) {
        RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireRight(AccessRight.CREATE_FILTER, SERVICE);
        String name = requireName(request != null ? request.name() : null);
        ScriptFilterGroup source = findFilterGroup(filterGroupId);
        ScriptFilterGroup copy = new ScriptFilterGroup();
        copy.setName(name);
        copy.setProductName(source.getProductName());
        copy.setFilters(new HashSet<>(source.getFilters()));
        copy.setCreator(RestAuthorization.currentUserName());
        FilterGroupDetailTO saved = saveFilterGroup(copy, "filter group copy");
        LOGGER.info("{} copied filter group {} to {} ({})", copy.getCreator(), filterGroupId, saved.getId(), name);
        return saved;
    }

    /**
     * The first problem with a filter request, or null when it can be saved. Only internal filters can be saved
     * through REST.
     */
    private static String filterError(FilterTO request) {
        if (request == null) {
            return "Filter request is required";
        }
        if (request.getName() == null || request.getName().isBlank()) {
            return "Filter name is required";
        }
        if (request.getName().length() > MAX_NAME_LENGTH) {
            return "Filter name must be at most " + MAX_NAME_LENGTH + " characters";
        }
        if (request.getFilterType() != null && !ScriptFilterType.INTERNAL.name().equals(request.getFilterType())) {
            return "Only internal filters are supported";
        }
        if (request.getExternalScriptId() != null) {
            return "Internal filters cannot reference an external script";
        }
        return null;
    }

    private static void requireValidFilter(FilterTO request) {
        String error = filterError(request);
        if (error != null) {
            throw new GenericServiceBadRequestException(SERVICE, "filter", error);
        }
    }

    private static String requireName(String requested) {
        String name = StringUtils.trimToNull(requested);
        if (name == null) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name is required");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new GenericServiceBadRequestException(SERVICE, "name",
                    "name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        return name;
    }

    /**
     * Rejects a save based on a stale copy. Compares to the second: the database may not store milliseconds.
     */
    private static void requireCurrent(Date sent, Date stored, String what) {
        if (sent == null) {
            throw new GenericServiceBadRequestException(SERVICE, "modified",
                    "modified is required; send the value from the last GET");
        }
        if (stored == null || sent.getTime() / 1000 != stored.getTime() / 1000) {
            throw new GenericServiceConflictException(SERVICE,
                    what + " was changed by someone else since it was loaded; reload it and try again");
        }
    }

    /**
     * Copies the name, product and members from the request; the creator is left to the caller.
     */
    private static void applyGroup(FilterGroupTO request, ScriptFilterGroup group) {
        group.setName(requireName(request.getName()));
        group.setProductName(StringUtils.trimToNull(request.getProductName()));
        List<Integer> ids = request.getFilterIds() == null ? List.of()
                : request.getFilterIds().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<ScriptFilter> filters = ids.isEmpty() ? List.of() : new ScriptFilterDao().findForIds(ids);
        Set<Integer> found = filters.stream().map(ScriptFilter::getId).collect(Collectors.toSet());
        List<Integer> missing = ids.stream().filter(id -> !found.contains(id)).collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "filterIds", "no such filters: " + missing);
        }
        group.setFilters(new HashSet<>(filters));
    }

    private static ScriptFilter findFilter(Integer filterId) {
        ScriptFilter filter = filterId != null ? new ScriptFilterDao().findById(filterId) : null;
        if (filter == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "filter " + filterId, null);
        }
        return filter;
    }

    private static ScriptFilterGroup findFilterGroup(Integer filterGroupId) {
        ScriptFilterGroup group = filterGroupId != null ? new ScriptFilterGroupDao().findById(filterGroupId) : null;
        if (group == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "filter group " + filterGroupId, null);
        }
        return group;
    }

    private static FilterTO saveFilter(ScriptFilter filter, String what) {
        try {
            return FilterServiceUtil.filterToTO(new ScriptFilterDao().saveOrUpdate(filter));
        } catch (RuntimeException e) {
            LOGGER.error("Error saving {}: {}", what, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, what, e);
        }
    }

    private static FilterGroupDetailTO saveFilterGroup(ScriptFilterGroup group, String what) {
        try {
            return FilterServiceUtil.filterGroupToDetailTO(new ScriptFilterGroupDao().saveOrUpdate(group));
        } catch (RuntimeException e) {
            LOGGER.error("Error saving {}: {}", what, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, what, e);
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
                // the join table has no cascade, so drop the filter from its groups first (as FilterBean.delete does)
                ScriptFilterGroupDao groupDao = new ScriptFilterGroupDao();
                for (ScriptFilterGroup group : groupDao.getScriptFilterGroupForFilter(filterId)) {
                    if (group.getFilters().remove(filter)) {
                        groupDao.saveOrUpdate(group);
                    }
                }
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

