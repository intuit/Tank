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
import java.util.Collections;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.util.Messages;

import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.ScriptFilterGroupDao;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;

@Named
@ViewScoped
public class FilterGroupBean extends SelectableBean<ScriptFilterGroup> implements Serializable,
        Multiselectable<ScriptFilterGroup> {

    private static final long serialVersionUID = 1L;

    private SelectableWrapper<ScriptFilterGroup> selectedFilter;

    @Inject
    private FilterBean filterBean;

    @Inject
    private Security security;

    @Inject
    private Messages messages;

    /**
     * Code for deleting a filter group goes here.
     * 
     * @param event
     */
    public void delete(ScriptFilterGroup group) {
        if (!security.hasRight(AccessRight.DELETE_FILTER) && !security.isOwner(group)) {
            messages.warn("You don't have permission to delete this filter group.");
        } else {
            new ScriptFilterGroupDao().delete(group);
            refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ScriptFilterGroup> getEntityList(ViewFilterType viewFilter) {
        List<ScriptFilterGroup> all = new ScriptFilterGroupDao().findAll();
        Collections.sort(all);
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCurrent() {
        return true;
    }

    /**
     * @return the selectedFilter
     */
    public SelectableWrapper<ScriptFilterGroup> getSelectedFilter() {
        return selectedFilter;
    }

    /**
     * @param selectedFilter
     *            the selectedFilter to set
     */
    public void setSelectedFilter(SelectableWrapper<ScriptFilterGroup> selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public void deleteSelectedFilterGroup() {
        if (selectedFilter != null) {
            delete(selectedFilter.getEntity());
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void selectAll() {
        super.selectAll();
        processAllSelection();
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void unselectAll() {
        super.unselectAll();
        processAllSelection();
    }

    public void processAllSelection() {
        List<SelectableWrapper<ScriptFilterGroup>> wrappers = getSelectionList();
        for (SelectableWrapper<ScriptFilterGroup> wrapper : wrappers) {
            filterBean.processSelection(wrapper);
        }
    }

}
