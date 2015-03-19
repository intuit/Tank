package com.intuit.tank.search.script;

/*
 * #%L
 * Script Search
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
import java.util.List;

import com.intuit.tank.project.Script;

public class SearchCriteria implements Serializable {

    private static final long serialVersionUID = 1L;
    private Script script;
    private String searchQuery;
    private List<Section> criteria = new ArrayList<Section>();

    /**
     * @return the script
     */
    public Script getScript() {
        return script;
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScript(Script script) {
        this.script = script;
    }

    /**
     * @return the searchQuery
     */
    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * @param searchQuery
     *            the searchQuery to set
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    /**
     * @return the criteria
     */
    public List<Section> getCriteria() {
        return criteria;
    }

}
