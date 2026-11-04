/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.dao;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * One page of a filtered, sorted entity listing for {@link BaseDao#findPaged}.
 *
 * <p>Property names are used directly in the query, so callers must only pass names from their own
 * whitelist, never raw client input.</p>
 *
 * @param page              zero-based page number
 * @param size              page size, at least 1
 * @param sortProperty      entity property to sort by; ties are broken by id
 * @param ascending         sort direction
 * @param equalTo           property to value filters, all of which must match
 * @param search            case-insensitive text that at least one of {@code searchProperties} must contain,
 *                          or null for no text search
 * @param searchProperties  the string properties searched by {@code search}
 */
public record PagedQuery(int page, int size, String sortProperty, boolean ascending, Map<String, Object> equalTo,
                         String search, List<String> searchProperties) {

    public PagedQuery {
        if (page < 0) {
            throw new IllegalArgumentException("page must not be negative");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size must be at least 1");
        }
        Objects.requireNonNull(sortProperty, "sortProperty");
        equalTo = equalTo == null ? Map.of() : Map.copyOf(equalTo);
        searchProperties = searchProperties == null ? List.of() : List.copyOf(searchProperties);
    }
}
