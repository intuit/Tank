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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;

import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.ModifiedDateComparator;
import com.intuit.tank.util.ModifiedDateComparator.SortOrder;
import com.intuit.tank.view.filter.ViewFilterType;

/**
 * EntityVersionLoader
 * 
 * @author dangleton
 * 
 */
public abstract class EntityVersionLoader<T extends BaseEntity, MESSAGE_TYPE> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final long MAX_VALID_TIME = 1000 * 60 * 5; // five minutes

    private int version;
    private List<T> versionEntities;
    private long timeStamp;

    /**
     * 
     * @param p
     */
    public void observeEvents(@Observes @Modified MESSAGE_TYPE p) {
        invalidate();
    }

    /**
     * @return the users
     */
    public VersionContainer<T> getVersionContainer(ViewFilterType viewFilter) {
        if (shouldReload()) {
            initContainer();
        }
        return new VersionContainer<T>(getVersionEntities(viewFilter), version);
    }

    /**
     * @return the users
     */
    public VersionContainer<T> getVersionContainer() {
        return getVersionContainer(ViewFilterType.ALL);
    }

    /**
     * @return the users
     */
    public List<T> getVersionEntities() {
        return getVersionEntities(ViewFilterType.ALL);
    }

    /**
     * @return the users
     */
    public List<T> getVersionEntities(ViewFilterType viewFilter) {
        if (shouldReload()) {
            initContainer();
        }
        return filterEntities(versionEntities, viewFilter);
    }

    public boolean isCurrent(int version) {
        try {
            return version == this.version;
        } catch (Exception e) {
            // slim possibility of this happening in a race condition. Easier to let it fail and reload than synchronize
        }
        return false;
    }

    private boolean shouldReload() {
        return versionEntities == null || System.currentTimeMillis() > timeStamp + MAX_VALID_TIME;
    }

    /**
     * 
     */
    protected void invalidate() {
        // this.versionContainer = null;
        this.versionEntities = null;
        this.version++;
    }

    /**
     * @return the
     */
    private synchronized void initContainer() {
        versionEntities = getEntities();
        this.version++;
        // versionContainer = new VersionContainer<T>(versionEntities, version);
        timeStamp = System.currentTimeMillis();
    }

    /**
     * Filters loaded entities based on viewFilter
     * 
     * @param entityList
     *            - list of entities to filter
     * @param viewFilter
     *            - filter criteria
     * @return List of T that satisfy the filter condition
     */
    private List<T> filterEntities(List<T> entityList, ViewFilterType viewFilter) {
        if (viewFilter == ViewFilterType.ALL) {
            return entityList;
        }
        Date d = ViewFilterType.getViewFilterDate(viewFilter);
        return entityList.stream().filter(entity -> entity.getCreated().after(d)).sorted(new ModifiedDateComparator(SortOrder.DESCENDING)).collect(Collectors.toList());
    }

    /**
     * Return a list of Entities that this VersionLoader deals with
     * 
     * @return
     */
    protected abstract List<T> getEntities();

}
