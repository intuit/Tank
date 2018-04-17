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

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.intuit.tank.search.util.MultiSearchParam.Operator;

/**
 * SearchUtils
 */
public class SearchUtils {

    /**
     * Padding represents padding to use on a number
     * 
     * <br>
     * Patterns:
     * 
     * <br>
     * Revisions: dangleton: May 20, 2008: Initial revision.
     * 
     * @author dangleton
     */
    public enum Padding {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8);

        private final DecimalFormat format;

        Padding(int numPlaces) {
            String sb = IntStream.range(0, numPlaces).mapToObj(i -> "0").collect(Collectors.joining());
            format = new DecimalFormat(sb);
        }

        /**
         * 
         * @return the decimal format associated with this Padding
         */
        public DecimalFormat getFormat() {
            return format;
        }

    }

    private static final FastDateFormat DF = FastDateFormat.getInstance("yyyyMMddHHmm");

    /**
     * private constructor
     */
    private SearchUtils() {
        // unused constructor
    }

    /**
     * pads the number to the specified padding.
     * 
     * @param num
     *            the number to format
     * @param padding
     *            the padding to use
     * @return formatted number
     */
    public static final String padInt(Number num, Padding padding) {
        return padding.getFormat().format(num);
    }

    /**
     * Formats the date to lexically correct format e.g. YYYYMMDDHHmm
     * 
     * @param d
     *            the date to format
     * @return the formatted date as a string
     */
    public static final String formatDate(Date d) {
        return DF.format(d);
    }

    /**
     * creates a compound field term using the terms provided.
     * 
     * @param delimiter
     *            the delimiter to use
     * @param term
     *            varargs terms to concatenate
     * @return the concatenated string separated by the delimiter.
     */
    public static final String makeCompoundField(String delimiter, String... term) {
        StringBuilder sb = new StringBuilder();
        for (String s : term) {
            if (s.length() != 0) {
                if (delimiter != null && sb.length() != 0) {
                    sb.append(delimiter);
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }

    /**
     * Builds the query.
     * 
     * @param searchQuery
     *            the query meta object to build the query from
     * @return Query the Lucene query object
     */
    public static final Query createLuceneQuery(SearchQuery searchQuery) {
        return createLuceneQuery(Operator.AND, searchQuery);
    }

    /**
     * Builds the query.
     * 
     * @param searchQuery
     *            the query meta object to build the query from
     * @return Query the Lucene query object
     */
    public static final Query createLuceneQuery(Operator operator, SearchQuery searchQuery) {
        Query luceneQuery = new MultiSearchParam(operator, searchQuery.getSearchParams().toArray(new SearchParam[0]))
                .getLuceneQuery();
        return new MultiSearchParam(operator, searchQuery.getSearchParams().toArray(new SearchParam[0]))
                .getLuceneQuery();
    }

    public static final Query createLuceneQuery(Operator operator, CompositeSearchQuery query) {
        Query luceneQuery = new MultiSearchParam(query.getOperator(), query.getQuery1().getSearchParams()
                .toArray((new SearchParam[0]))).getLuceneQuery();
        Query otherLuceneQuery = new MultiSearchParam(Operator.OR, query.getQuery2().getSearchParams()
                .toArray((new SearchParam[0]))).getLuceneQuery();
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(luceneQuery, operator.getOccur());
        booleanQuery.add(otherLuceneQuery, query.getOperator().getOccur());
        return booleanQuery;
    }

    /**
     * returns a sort or null if no sort is specified.
     * 
     * @param searchQuery
     *            the query containing the sorts
     * @return the lucene sort or null if no sort is specified
     */
    public static final Sort createLuceneSort(SearchQuery searchQuery) {
        Sort ret = null;
        Collection<SortOrder> sorts = searchQuery.getSortOrder();
        if (!sorts.isEmpty()) {
            SortField[] array = new SortField[sorts.size()];
            int i = 0;
            for (SortOrder sortOrder : sorts) {
                array[i] = new SortField(sortOrder.getField(), SortField.STRING, sortOrder.isDescending());
            }
            ret = new Sort(array);
        }
        return ret;
    }

}
