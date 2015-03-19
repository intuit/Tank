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

import java.util.List;

import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * Multiselectable
 * 
 * @author dangleton
 * 
 */
public interface Multiselectable<T> {

    /**
     * Deletes all selected items.
     */
    public void deleteSelected();

    /**
     * Selects all items in view.
     */
    public void selectAll();

    /**
     * deselects all items in view.
     */
    public void unselectAll();

    /**
     * determinesif there are any items selected.
     * 
     * @return true if any items are selected
     */
    public boolean hasSelected();

    /**
     * 
     */
    public List<SelectableWrapper<T>> getSelectionList();

    public List<SelectableWrapper<T>> getFilteredData();
}
