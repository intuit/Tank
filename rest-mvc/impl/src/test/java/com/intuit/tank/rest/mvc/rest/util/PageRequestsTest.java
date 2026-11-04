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
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PageRequestsTest {

    private static final Map<String, String> FIELDS = Map.of("name", "name", "owner", "creator");

    private static PagedQuery query(Integer page, Integer size, String sort) {
        return PageRequests.toQuery("test", page, size, sort, FIELDS, "name,desc", null, null, List.of());
    }

    @Test
    void defaults() {
        PagedQuery q = query(null, null, null);
        assertEquals(0, q.page());
        assertEquals(PageRequests.DEFAULT_SIZE, q.size());
        assertEquals("name", q.sortProperty());
        assertFalse(q.ascending());
    }

    @Test
    void mapsApiFieldToEntityProperty() {
        PagedQuery q = query(2, 50, "owner");
        assertEquals("creator", q.sortProperty());
        assertTrue(q.ascending());
        assertEquals(2, q.page());
        assertEquals(50, q.size());
        assertFalse(query(0, 10, "owner,DESC").ascending());
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(GenericServiceBadRequestException.class, () -> query(-1, 10, null));
        assertThrows(GenericServiceBadRequestException.class, () -> query(0, 0, null));
        assertThrows(GenericServiceBadRequestException.class, () -> query(0, PageRequests.MAX_SIZE + 1, null));
        assertThrows(GenericServiceBadRequestException.class, () -> query(0, 10, "password"));
        assertThrows(GenericServiceBadRequestException.class, () -> query(0, 10, "name,sideways"));
        assertThrows(GenericServiceBadRequestException.class, () -> query(0, 10, "name,asc,x"));
    }

    @Test
    void skipsNullFiltersAndBlankSearch() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("creator", null);
        filters.put("productName", "Tax");
        PagedQuery q = PageRequests.toQuery("test", 0, 10, null, FIELDS, "name", filters, "  ", List.of("name"));
        assertEquals(Map.of("productName", "Tax"), q.equalTo());
        assertNull(q.search());
    }
}
