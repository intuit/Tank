package com.intuit.tank.search.util;

/*
 * #%L
 * DocumentUtil
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.apache.lucene.search.Filter;

/**
 * SearchQuery encapsulates a search query.
 */
public class SearchQuery {

    private Collection<SortOrder> sortOrder;
    private Collection<SearchParam> searchParams;
    private Filter filter;
    private int startIndex;
    private int numItems = -1;

    private SearchQuery() {
        sortOrder = new LinkedHashSet<SortOrder>();
        searchParams = new LinkedHashSet<SearchParam>();
    }

    private SearchQuery(SearchQuery copy) {
        sortOrder = new LinkedHashSet<SortOrder>(copy.sortOrder);
        searchParams = new LinkedHashSet<SearchParam>(copy.searchParams);
        this.numItems = copy.numItems;
    }

    /**
     * 
     * @return the builder suitable for building a query
     */
    public static QueryBuilder getBuilder() {
        return new QueryBuilder();
    }

    /**
     * 
     * @param queryToCopy
     *            the query to copy
     * @return the new queryBuilder
     */
    public static QueryBuilder getBuilder(SearchQuery queryToCopy) {
        return new QueryBuilder(queryToCopy);
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @return the sortOrder
     */
    public Collection<SortOrder> getSortOrder() {
        return sortOrder;
    }

    /**
     * @return the searchParams
     */
    public Collection<SearchParam> getSearchParams() {
        return searchParams;
    }

    /**
     * @return the numItems
     */
    public int getNumItems() {
        return numItems;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    private SearchQuery immutableInstance() {
        searchParams = Collections.unmodifiableCollection(searchParams);
        sortOrder = Collections.unmodifiableCollection(sortOrder);
        return this;
    }

    /**
     * 
     * QueryBuilder builder for searchQueries
     * 
     * <br>
     * Patterns: Builder
     * 
     * <br>
     * Revisions: dangleton: May 21, 2008: Initial revision.
     * 
     * @author dangleton
     */
    public static class QueryBuilder {

        private SearchQuery query;

        private QueryBuilder() {
            this.query = new SearchQuery();
        }

        private QueryBuilder(SearchQuery searchQuery) {
            this.query = new SearchQuery(searchQuery);
        }

        /**
         * 
         * @return an immutable instance of the query
         */
        public SearchQuery toSearchQuery() {
            return query.immutableInstance();
        }

        /**
         * 
         * @param num
         *            the max number of items this query should return
         * @return the builder
         */
        public QueryBuilder numItems(int num) {
            query.numItems = num > 0 ? num : -1;
            return this;
        }

        /**
         * 
         * @param num
         *            the item number of items that this query should skip
         * @return the builder
         */
        public QueryBuilder startIndex(int num) {
            query.startIndex = num > 0 ? num : 0;
            return this;
        }

        /**
         * 
         * @param param
         *            the param to add
         * @return the builder
         */
        public QueryBuilder addParam(SearchParam param) {
            query.searchParams.add(param);
            return this;
        }

        /**
         * 
         * @param param
         *            the param to remove
         * @return the builder
         */
        public QueryBuilder removeParam(SearchParam param) {
            query.searchParams.remove(param);
            return this;
        }

        /**
         * 
         * @param sort
         *            the sort to remove
         * @return the builder
         */
        public QueryBuilder removeSort(SortOrder sort) {
            query.sortOrder.remove(sort);
            return this;
        }

        /**
         * 
         * @param sort
         *            the sort to add
         * @return the builder
         */
        public QueryBuilder addSort(SortOrder sort) {
            query.sortOrder.add(sort);
            return this;
        }

        /**
         * 
         * @param filter
         *            the filter to set
         * @return the builder
         */
        public QueryBuilder setFilter(Filter filter) {
            query.filter = filter;
            return this;
        }

        /**
         * 
         * @return true if there is a param set on this builder
         */
        public boolean hasParam() {
            return query.searchParams.size() != 0;
        }

        /**
         * 
         * @return true if there is a sort set on this builder
         */
        public boolean hasSort() {
            return query.getSortOrder().size() != 0;
        }
    }

}
