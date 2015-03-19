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
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.dao.FilterDao;
import com.intuit.tank.project.ScriptFilter;

@Named("filterService")
public class FilterServiceBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<ScriptFilter> filters;

    @Inject
    private Event<InvalidScriptFilter> scriptFilterEvent;

    /**
     * initializes and fetches the filters from persistent storage.
     * 
     * @return
     */
    public List<ScriptFilter> getScriptFilters() {
        initialize();
        return filters;
    }

    /**
     * Will implement the functionality for editing a filter
     * 
     * @param event
     * 
     */
    public void edit(ScriptFilter filter) {

    }

    public void edit() {

    }

    /**
     * 
     * @param filterEvent
     */
    public void handleInvalidEvent(@Observes InvalidScriptFilter filterEvent) {
        filters = null;
    }

    /**
     * re-fetch filters from DB next time our model is called.
     * 
     * @param
     */
    public void handleModifyEvent(@Observes ScriptFilter filter) {
        filters = null;
    }

    /**
     * Delete the specified filter
     * 
     * @param filter
     *            the filter to delete
     */
    public void delete(ScriptFilter filter) {
        // if (filters.remove(filter)) {

        new FilterDao().delete(filter);
        scriptFilterEvent.fire(new InvalidScriptFilter(filter.getId()));
        // }
    }

    private void initialize() {
        if (filters == null) {
            filters = new FilterDao().findAll();
        }
    }

    public static class InvalidScriptFilter implements Serializable {
        private int invalidScriptFilterId;

        /**
         * @param invalidScriptFilterId
         */
        private InvalidScriptFilter(int invalidScriptFilterId) {
            super();
            this.invalidScriptFilterId = invalidScriptFilterId;
        }

        /**
         * @return the invalidScriptFilterId
         */
        public int getInvalidScriptFilterId() {
            return invalidScriptFilterId;
        }

    }

}
