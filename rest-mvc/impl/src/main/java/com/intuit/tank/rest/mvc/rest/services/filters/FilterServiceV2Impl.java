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
import com.intuit.tank.dao.ScriptFilterActionDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.dao.FilterGroupDao;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
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
            LOGGER.error("Error returning specific filter: {}", e.getMessage(), e);
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
            LOGGER.error("Error returning specific filter: {}", e.getMessage(), e);
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
            LOGGER.error("Error returning all filters: {}", e.getMessage(), e);
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
            LOGGER.error("Error returning all filter groups: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("filter", "all filter groups", e);
        }
    }

    @Override
    public Map<String, Integer> createFilter(FilterTO filterTO) {
        try {
            ScriptFilter filter = FilterServiceUtil.toFromTO(filterTO);
            filter = new ScriptFilterDao().saveOrUpdate(filter);
            Map<String, Integer> result = new HashMap<>();
            result.put("filterId", filter.getId());
            return result;
        } catch (Exception e) {
            LOGGER.error("Error creating filter: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("filter", "filter", e);
        }
    }

    @Override
    public FilterTO updateFilter(Integer filterId, FilterTO filterTO) {
        try {
            ScriptFilterDao dao = new ScriptFilterDao();
            ScriptFilter existing = dao.findById(filterId);
            if (existing == null) {
                throw new GenericServiceResourceNotFoundException("filter", "filter with id " + filterId, null);
            }
            filterTO.setId(filterId);
            ScriptFilter updated = FilterServiceUtil.toFromTO(filterTO);
            updated = dao.saveOrUpdate(updated);
            return FilterServiceUtil.filterToTO(updated);
        } catch (GenericServiceResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error updating filter: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("filter", "filter", e);
        }
    }

    @Override
    public Map<String, Integer> createFilterGroup(FilterGroupTO filterGroupTO) {
        try {
            ScriptFilterGroup group = new ScriptFilterGroup();
            group.setName(filterGroupTO.getName());
            group.setProductName(filterGroupTO.getProductName());
            group = new ScriptFilterGroupDao().saveOrUpdate(group);
            Map<String, Integer> result = new HashMap<>();
            result.put("filterGroupId", group.getId());
            return result;
        } catch (Exception e) {
            LOGGER.error("Error creating filter group: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("filter", "filterGroup", e);
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
                    script = new ScriptDao().saveOrUpdate(script);
                    sendMsg(script, ModificationType.UPDATE);
                    return "Filters applied";
                }
            }
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
                dao.delete(filter);
                return "";
            }
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
                dao.delete(filterGroup);
                return "";
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting FilterGroup: {}", e, e);
            throw new GenericServiceDeleteException("filter", "filterGroup", e);
        }
    }

    @Override
    public String upgradeFilters() {
        try {
            final String CONCAT_FULL  = "#{function.string.concat}";
            final String CONCAT       = "#function.string.concat.";
            final String GET_CSV      = "#function.generic.getcsv.";
            final java.util.regex.Pattern VAR_PATTERN = java.util.regex.Pattern.compile("(@[\\w_-]*)");

            ScriptFilterDao filterDao = new ScriptFilterDao();
            ScriptFilterActionDao actionDao = new ScriptFilterActionDao();
            List<ScriptFilter> all = filterDao.findAll();
            Set<Integer> toDelete = actionDao.findAll().stream()
                    .map(BaseEntity::getId)
                    .collect(java.util.stream.Collectors.toSet());

            for (ScriptFilter filter : all) {
                for (ScriptFilterAction action : filter.getActions()) {
                    toDelete.remove(action.getId());
                    String value = action.getValue();
                    if (value == null) continue;
                    String original = value;
                    if (value.startsWith("@") && value.lastIndexOf('@') == 0) {
                        value = "#{" + value.substring(1) + "}";
                    } else if (value.startsWith(CONCAT_FULL)) {
                        value = value.substring(CONCAT_FULL.length());
                    } else if (value.startsWith(CONCAT)) {
                        String[] parts = org.apache.commons.lang3.StringUtils.split(value.substring(CONCAT.length()), '.');
                        StringBuilder sb = new StringBuilder();
                        for (String p : parts) {
                            p = p.replace("-dot-", ".");
                            sb.append(p.startsWith("@") ? "#{" + p.substring(1) + "}" : p);
                        }
                        value = sb.toString();
                    } else if (value.startsWith(GET_CSV)) {
                        String[] parts = org.apache.commons.lang3.StringUtils.split(value, '.');
                        StringBuilder sb = new StringBuilder("#{ioFunctions.getCSVData(");
                        for (int i = 3; i < parts.length; i++) {
                            String p = parts[i].replace("-dot-", ".");
                            if (p.startsWith("@")) p = "#{" + p.substring(1) + "}";
                            else if (p.startsWith("#{")) p = p.substring(2, p.length() - 1);
                            else if (!org.apache.commons.lang3.math.NumberUtils.isCreatable(p)) p = '"' + p + '"';
                            sb.append(p);
                            if (i < parts.length - 1) sb.append(", ");
                        }
                        value = sb.append(")}").toString();
                    } else if (value.indexOf('@') != -1) {
                        java.util.regex.Matcher m = VAR_PATTERN.matcher(value);
                        while (m.find()) {
                            String g = m.group(1).trim();
                            value = value.replace(g, "#{" + g.substring(1) + "}");
                        }
                    }
                    if (!original.equals(value)) {
                        action.setValue(value);
                        actionDao.saveOrUpdate(action);
                    }
                }
            }
            for (Integer id : toDelete) {
                actionDao.delete(id);
            }
            return "Filters upgraded successfully";
        } catch (Exception e) {
            LOGGER.error("Error upgrading filters: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("filter", "upgrade", e);
        }
    }
}

