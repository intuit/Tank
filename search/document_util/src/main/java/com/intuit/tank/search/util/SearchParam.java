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

import org.apache.lucene.search.Query;

/**
 * SearchParam
 */
public interface SearchParam {

    /**
     * 
     * @return the string representation of the query
     */
    public String getQuery();

    /**
     * 
     * @return the lucene query for this param
     */
    public Query getLuceneQuery();
}
