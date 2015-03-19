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

import java.io.Serializable;

/**
 * 
 * SelectableWrapper generified EntityWrapper that suports selection
 * 
 * @author dangleton
 * 
 * @param <T>
 */
public class SelectableWrapper<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean selected;
    private T entity;

    /**
     * @param entity
     */
    public SelectableWrapper(T entity) {
        super();
        this.entity = entity;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the dataFile
     */
    public T getEntity() {
        return entity;
    }

}
