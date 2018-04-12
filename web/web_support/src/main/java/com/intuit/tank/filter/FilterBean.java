package com.intuit.tank.filter;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.util.Messages;

import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;

@Named
@ViewScoped
public class FilterBean extends SelectableBean<ScriptFilter> implements Serializable, Multiselectable<ScriptFilter> {

    private static final long serialVersionUID = 1L;

    private SelectableWrapper<ScriptFilter> selectedFilter;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private Security security;

    @Inject
    private Messages messages;

    /**
     * @inheritDoc
     */
    @Override
    public boolean isCurrent() {
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void delete(ScriptFilter entity) {
        if (!security.hasRight(AccessRight.DELETE_FILTER) && !security.isOwner(entity)) {
            messages.warn("You don't have permission to delete this filter.");
        } else {
            try {
                ScriptFilterGroupDao groupDao = new ScriptFilterGroupDao();
                List<ScriptFilterGroup> groups = groupDao.getScriptFilterGroupForFilter(entity.getId());
                for (ScriptFilterGroup group : groups) {
                    if (group.getFilters().remove(entity)) {
                        groupDao.saveOrUpdate(group);
                    }
                }
                new ScriptFilterDao().delete(entity);
                refresh();
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
        }
    }

    public void deleteSelectedFilter() {
        if (selectedFilter != null) {
            delete(selectedFilter.getEntity());
        }
    }

    /**
     * @return the selectedFilter
     */
    public SelectableWrapper<ScriptFilter> getSelectedFilter() {
        return selectedFilter;
    }

    /**
     * @param selectedFilter
     *            the selectedFilter to set
     */
    public void setSelectedFilter(SelectableWrapper<ScriptFilter> selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<ScriptFilter> getEntityList(ViewFilterType viewFilter) {
        List<ScriptFilter> all = new ScriptFilterDao().findFiltered(ViewFilterType.ALL);
        Collections.sort(all);
        return all;
    }

    public void processSelection(SelectableWrapper<ScriptFilterGroup> w) {
        Map<Integer, SelectableWrapper<ScriptFilter>> map = getListMap(getSelectionList());
        boolean selected = w.isSelected();
        for (ScriptFilter sf : w.getEntity().getFilters()) {
            SelectableWrapper<ScriptFilter> wrapper = map.get(sf.getId());
            if (wrapper != null) {
                wrapper.setSelected(selected);
            }
        }
    }

    /**
     * @param list
     * @return
     */
    private Map<Integer, SelectableWrapper<ScriptFilter>> getListMap(List<SelectableWrapper<ScriptFilter>> list) {
        Map<Integer, SelectableWrapper<ScriptFilter>> ret = new HashMap<Integer, SelectableWrapper<ScriptFilter>>();
        for (SelectableWrapper<ScriptFilter> f : list) {
            ret.put(f.getEntity().getId(), f);
        }
        return ret;
    }

    /**
     * @return the list of integers representing the IDs of each selected filter
     */
    public List<Integer> getSelectedFilterIds() {
        return this.getSelectionList().stream().filter(SelectableWrapper::isSelected).map(wrapper -> wrapper.getEntity().getId()).collect(Collectors.toList());
    }

}
