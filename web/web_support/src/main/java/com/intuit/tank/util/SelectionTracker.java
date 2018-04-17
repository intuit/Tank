/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.util;

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
import java.util.List;

import javax.annotation.Nonnull;

import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * SelectionTracker
 * 
 * @author dangleton
 * 
 */
public class SelectionTracker<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Multiselectable<T> selectable;

    /**
     * Costructs a Tracker with the specified backing object
     * 
     * @param selectable
     */
    public SelectionTracker(@Nonnull Multiselectable<T> selectable) {
        this.selectable = selectable;
    }

    /**
     * Selects all objects in view.
     * 
     * @see Multiselectable
     */
    public void selectAll() {
        List<SelectableWrapper<T>> selectionList = selectable.getSelectionList();
        List<SelectableWrapper<T>> filteredData = selectable.getFilteredData();
        for (SelectableWrapper<T> wrapper : selectionList) {
            if (filteredData.contains(wrapper)) {
                wrapper.setSelected(true);
            }
        }
    }

    /**
     * Unselects all objects in view.
     * 
     * @see Multiselectable
     */
    public void unselectAll() {
        List<SelectableWrapper<T>> selectionList = selectable.getSelectionList();
        List<SelectableWrapper<T>> filteredData = selectable.getFilteredData();
        for (SelectableWrapper<T> wrapper : selectionList) {
            if (filteredData.contains(wrapper)) {
                wrapper.setSelected(false);
            }
        }
    }

    /**
     * @return true if any objects are selected.
     * @see Multiselectable
     */
    public boolean hasSelected() {
        List<SelectableWrapper<T>> list = selectable.getSelectionList();
        return list.stream().anyMatch(SelectableWrapper::isSelected);
    }
}
