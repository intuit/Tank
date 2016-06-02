/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.project;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.model.SelectItem;

import com.intuit.tank.ModifiedDatafileMessage;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.wrapper.EntityVersionLoader;

/**
 * ProjectModifiedObserver
 * 
 * @author Kevin McGoldrick
 * 
 */
@ApplicationScoped
public class DataFileLoader extends EntityVersionLoader<DataFile, ModifiedDatafileMessage> {

    private static final long serialVersionUID = 1L;

    private SelectItem[] creatorList;

    // @Inject
    // private Messages messages;
    //
    // /**
    // *
    // * @param jobEvent
    // */
    // public void observeJobEvents(@Observes JobEvent jobEvent) {
    // if (jobEvent.getSeverity() == JobEventSeverity.INFO) {
    // messages.info(jobEvent.getMessage());
    // } else if (jobEvent.getSeverity() == JobEventSeverity.WARN) {
    // messages.warn(jobEvent.getMessage());
    // } else {
    // messages.error(jobEvent.getMessage());
    // }
    // }

    /**
     * 
     * @param p
     */
    public void observeEvents(@Observes ModifiedEntityMessage entityMsg) {
        if (entityMsg.getEntityClass() == DataFile.class) {
            invalidate();
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected List<DataFile> getEntities() {
    	List<DataFile> files = new DataFileDao().findFiltered(ViewFilterType.ALL);
        Set<String> set = new HashSet<String>();
        for (DataFile f : files) {
        	if( !f.getCreator().isEmpty() ) {
        		set.add(f.getCreator());
        	}
        }
        List<String> list = new ArrayList<String>(set);
        Collections.sort(list);
        creatorList = new SelectItem[list.size() + 1];
        creatorList[0] = new SelectItem("", "All");
        for (int i = 0; i < list.size(); i++) {
            creatorList[i + 1] = new SelectItem(list.get(i));
        }
        return files;
    }

    /**
     * @return the creatorList
     */
    public SelectItem[] getCreatorList() {
        if (creatorList == null) {
            getEntities();
        }
        return creatorList;
    }

}
