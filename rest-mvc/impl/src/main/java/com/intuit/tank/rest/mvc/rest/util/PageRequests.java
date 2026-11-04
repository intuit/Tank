/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Turns {@code page}, {@code size} and {@code sort} request parameters into a {@link PagedQuery}, mapping
 * API field names to entity properties through a whitelist.
 */
public final class PageRequests {

    public static final int DEFAULT_SIZE = 25;
    public static final int MAX_SIZE = 200;

    private PageRequests() {
    }

    /**
     * @param sort          {@code field} or {@code field,asc|desc}; null for {@code defaultSort}
     * @param sortableFields API field name to entity property; the only fields that can be sorted on
     * @param defaultSort   the API field and direction used when {@code sort} is null, such as {@code modified,desc}
     * @param equalTo       entity property to value filters; null values are skipped
     * @throws GenericServiceBadRequestException for a negative page, a size outside 1..{@value #MAX_SIZE},
     *                                           or an unknown sort field or direction
     */
    public static PagedQuery toQuery(String service, Integer page, Integer size, String sort,
                                     Map<String, String> sortableFields, String defaultSort,
                                     Map<String, Object> equalTo, String search, List<String> searchProperties) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : DEFAULT_SIZE;
        if (pageNumber < 0) {
            throw new GenericServiceBadRequestException(service, "page", "page must not be negative");
        }
        if (pageSize < 1 || pageSize > MAX_SIZE) {
            throw new GenericServiceBadRequestException(service, "size", "size must be from 1 to " + MAX_SIZE);
        }
        String[] parts = StringUtils.defaultIfBlank(sort, defaultSort).split(",", -1);
        String property = sortableFields.get(parts[0].trim());
        if (property == null || parts.length > 2) {
            throw new GenericServiceBadRequestException(service, "sort",
                    "sort must be one of " + String.join(", ", sortableFields.keySet().stream().sorted().toList())
                            + ", optionally followed by ,asc or ,desc");
        }
        boolean ascending = true;
        if (parts.length == 2) {
            String direction = parts[1].trim().toLowerCase();
            if (!direction.equals("asc") && !direction.equals("desc")) {
                throw new GenericServiceBadRequestException(service, "sort", "sort direction must be asc or desc");
            }
            ascending = direction.equals("asc");
        }
        Map<String, Object> filters = new java.util.HashMap<>();
        if (equalTo != null) {
            equalTo.forEach((key, value) -> {
                if (value != null) {
                    filters.put(key, value);
                }
            });
        }
        return new PagedQuery(pageNumber, pageSize, property, ascending, filters, StringUtils.trimToNull(search),
                searchProperties);
    }
}
