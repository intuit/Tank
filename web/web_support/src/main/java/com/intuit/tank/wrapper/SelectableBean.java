/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.wrapper;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.data.FilterEvent;

import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.util.SelectionTracker;
import com.intuit.tank.view.filter.ViewFilterType;

/**
 * SelectableBean
 * 
 * @author dangleton
 * 
 */
public abstract class SelectableBean<T> implements Multiselectable<T> {
	
	private static final Logger LOG = LogManager.getLogger(SelectableBean.class);

    private List<SelectableWrapper<T>> selectionList;
    private List<SelectableWrapper<T>> filteredData;

    private SelectionTracker<T> selectionTracker = new SelectionTracker<T>(this);

    private ViewFilterType viewFilterType = ViewFilterType.ALL;

    private boolean needsRefresh = false;

    protected TablePreferences tablePrefs;
    protected TableViewState tableState = new TableViewState();

    /**
     * @return the tablePrefs
     */
    public TablePreferences getTablePrefs() {
        return tablePrefs;
    }

    /**
     * @return the tableState
     */
    public TableViewState getTableState() {
        return tableState;
    }

    public ViewFilterType[] getViewFilterTypeList() {
        return ViewFilterType.values();
    }

    /**
     * 
     * @inheritDoc
     */
    public void deleteSelected() {
        ArrayList<SelectableWrapper<T>> copied = new ArrayList<SelectableWrapper<T>>(getSelectionList());
        for (SelectableWrapper<T> wrapper : copied) {
            if (wrapper.isSelected()) {
                delete(wrapper.getEntity());
            }
        }
    }

    /**
     * 
     * @inheritDoc
     */
    public void selectAll() {
        selectionTracker.selectAll();
    }

    /**
     * 
     * @inheritDoc
     */
    public void unselectAll() {
        selectionTracker.unselectAll();
    }

    /**
     * 
     * @inheritDoc
     */
    public boolean hasSelected() {
        return selectionTracker.hasSelected();
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<SelectableWrapper<T>> getSelectionList() {
        if (selectionList == null || !isCurrent() || needsRefresh) {
            List<T> l = getEntityList(this.viewFilterType);
            selectionList = new ArrayList<SelectableWrapper<T>>();
            for (T entity : l) {
                selectionList.add(new SelectableWrapper<T>(entity));
            }
            needsRefresh = false;
        }
        return selectionList;
    }

    public List<SelectableWrapper<T>> getFilteredData() {
    	if (filteredData == null) {
        	if (selectionList == null ) {
        		return getSelectionList();
        	}
            return selectionList;
        }
        return filteredData;
    }

    /**
     * @param filteredData
     *            the filteredData to set
     */
    public void setFilteredData(List<SelectableWrapper<T>> filteredData) {
        this.filteredData = filteredData;
        if (filteredData != null) {
            unselect(filteredData);
        }
    }

    /**
     * @param filteredData2
     */
    private void unselect(List<SelectableWrapper<T>> filteredData2) {
        Set<SelectableWrapper<T>> set = new HashSet<SelectableWrapper<T>>(filteredData2);
        for (SelectableWrapper<T> wrapper : getSelectionList()) {
            if (wrapper.isSelected() && !set.contains(wrapper)) {
                wrapper.setSelected(false);
            }
        }

    }

    public void refresh() {
        needsRefresh = true;
        selectionList = getSelectionList();
        filteredData = null;
    }

    public ViewFilterType getViewFilterType() {
        return viewFilterType;
    }

    public void setViewFilterType(ViewFilterType viewFilterType) {
        this.viewFilterType = viewFilterType;
    }
    
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void onFilter(FilterEvent event) {
    	filteredData = (List<SelectableWrapper<T>>) event.getData();
    }

/*    public void onFilter(AjaxBehaviorEvent event) {
        DataTable dataTable = (DataTable) event.getSource();
        @SuppressWarnings("unchecked") ArrayList<SelectableWrapper<T>> tempList = (ArrayList<SelectableWrapper<T>>) dataTable
                .getFilteredValue();
        // ArrayList<SelectableWrapper<T>> tempList = (ArrayList<SelectableWrapper<T>>)dataTable.getFilteredData();
        if (tempList != null) {
            filteredData = tempList;
        } else {
            filteredData = selectionList;
        }

    }
*/
    public abstract List<T> getEntityList(ViewFilterType viewFilter);

    public abstract void delete(T entity);

    public abstract boolean isCurrent();

}
