/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.prefs;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.primefaces.event.ColumnResizeEvent;

import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.util.ComponentUtil;

/**
 * ColPrefsHolder
 * 
 * @author dangleton
 * 
 */
public class TablePreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MIN_SIZE = 50;

    private Map<String, ColumnPreferences> colSizeMap = new HashMap<String, ColumnPreferences>();

    private List<SelectItem> tableColumns;
    private List<String> visibleColumns;
    private Set<PreferencesChangedListener> listeners = new HashSet<PreferencesChangedListener>();

    public TablePreferences(List<? extends ColumnPreferences> prefs) {
        tableColumns = new ArrayList<SelectItem>();
        for (ColumnPreferences pref : prefs) {
            if (pref.isHideable()) {
                tableColumns.add(new SelectItem(pref.getColName(), pref.getDisplayName()));
            }
            colSizeMap.put(pref.getColName(), pref);
        }
        initVisible();
    }

    /**
     * @param listener
     *            the listener to set
     */
    public void registerListener(PreferencesChangedListener listener) {
        this.listeners.add(listener);
    }

    /**
     * @param listener
     *            the listener to set
     */
    public void unregisterListener(PreferencesChangedListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * 
     */
    private void initVisible() {
        visibleColumns = new ArrayList<String>();
        for (ColumnPreferences pref : colSizeMap.values()) {
            if (pref.isVisible()) {
                visibleColumns.add(pref.getColName());
            }
        }
    }

    /**
     * Gets the size of the specified column.
     * 
     * @param colName
     *            the name of the column to fetch
     * @return the size or 0
     */
    public int getSize(String colName) {
        int ret = 0;
        ColumnPreferences prefs = colSizeMap.get(colName);
        if (prefs != null) {
            ret = prefs.getSize();
        }
        return ret != 0 ? ret : MIN_SIZE;
    }

    /**
     * Gets the total size of all visible columns.
     * 
     * @return the size
     */
    public int getTotalSize() {
        int ret = 0;
        for (ColumnPreferences prefs : colSizeMap.values()) {
            if (prefs.isVisible()) {
                ret += prefs.getSize() + 20;
            }
        }
        return ret + 20;
    }

    /**
     * Gets the total size of all visible columns with the max set to max.
     * 
     * @return the size
     */
    public int getMaxTotalSize(int max) {
        int ret = 0;
        for (ColumnPreferences prefs : colSizeMap.values()) {
            if (prefs.isVisible()) {
                ret += prefs.getSize() + 20;
            }
        }
        ret += 40;
        return ret > max ? max : ret;
    }

    /**
     * Sets the size of the specified column.
     * 
     * @param colName
     *            the name of the column to fetch.
     * @param newSize
     *            the new size of the column
     */
    public void setSize(String colName, int newSize) {
        ColumnPreferences prefs = colSizeMap.get(colName);
        if (prefs != null) {
            prefs.setSize(newSize);
            fireEvent();
        }
    }

    /**
     * Gets the size of the specified column.
     * 
     * @param colName
     *            the name of the column to fetch
     * @return
     */
    public boolean isVisible(String colName) {
        boolean ret = false;
        ColumnPreferences prefs = colSizeMap.get(colName);
        if (prefs != null) {
            ret = prefs.isVisible();
        }
        return ret;
    }

    /**
     * Sets the size of the specified column.
     * 
     * @param colName
     *            the name of the column to fetch.
     * @param newSize
     *            the new size of the column
     */
    public void setVisible(String colName, boolean newVisible) {
        ColumnPreferences prefs = colSizeMap.get(colName);
        if (prefs != null) {
            prefs.setVisible(newVisible);
            fireEvent();
        }
    }

    public List<SelectItem> getVisibiltyList() {
        return tableColumns;
    }

    public void setVisibleColumns(List<String> colNames) {
        Set<String> s = new HashSet<String>(colNames);
        for (ColumnPreferences pref : colSizeMap.values()) {
            if (pref.isHideable()) {
                pref.setVisible(s.contains(pref.getColName()));
            }
        }
        fireEvent();
        initVisible();
    }

    public List<String> getVisibleColumns() {
        return visibleColumns;
    }

    public void onResize(ColumnResizeEvent event) {
        String id = ComponentUtil.extractId(event.getColumn().getClientId());
        setSize(id, event.getWidth());

    }

    private void fireEvent() {
        for (PreferencesChangedListener l : listeners) {
            l.prefsChanged();
        }
    }
}
