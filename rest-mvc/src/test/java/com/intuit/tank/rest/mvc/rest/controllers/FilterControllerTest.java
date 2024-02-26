/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.filters.FilterServiceV2;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterTO;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterContainer;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterGroupTO;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterGroupContainer;
import com.intuit.tank.rest.mvc.rest.models.filters.ApplyFiltersRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

public class FilterControllerTest {
    @InjectMocks
    private  FilterController filterController;

    @Mock
    private FilterServiceV2 filterService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGetPing() {
        when(filterService.ping()).thenReturn("PONG");
        ResponseEntity<String> result = filterController.ping();
        assertEquals("PONG", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).ping();
    }

    @Test
    public void testGetFilters() {
        List<FilterTO> filters = new ArrayList<>();
        FilterTO testFilter = new FilterTO();
        testFilter.setId(5);
        testFilter.setName("testFilterName");
        testFilter.setProductName("testProductName");
        filters.add(testFilter);
        FilterContainer filterContainer = new FilterContainer(filters);
        when(filterService.getFilters()).thenReturn(filterContainer);

        ResponseEntity<FilterContainer> result = filterController.getFilters();
        assertEquals(5, result.getBody().getFilters().get(0).getId());
        assertEquals("testFilterName", result.getBody().getFilters().get(0).getName());
        assertEquals("testProductName", result.getBody().getFilters().get(0).getProductName());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).getFilters();
    }

    @Test
    public void testGetFilterGroups() {
        List<FilterGroupTO> filters = new ArrayList<>();
        FilterGroupTO testFilterGroup = new FilterGroupTO();
        testFilterGroup.setId(4);
        testFilterGroup.setName("testFilterGroupName");
        testFilterGroup.setProductName("testProductName");
        filters.add(testFilterGroup);
        FilterGroupContainer filterGroupContainer = new FilterGroupContainer(filters);
        when(filterService.getFilterGroups()).thenReturn(filterGroupContainer);

        ResponseEntity<FilterGroupContainer> result = filterController.getFilterGroups();
        assertEquals(4, result.getBody().getFilterGroups().get(0).getId());
        assertEquals("testFilterGroupName", result.getBody().getFilterGroups().get(0).getName());
        assertEquals("testProductName", result.getBody().getFilterGroups().get(0).getProductName());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).getFilterGroups();
    }

    @Test
    public void testGetFilter() {
        FilterTO testFilter = new FilterTO();
        testFilter.setId(5);
        testFilter.setName("testFilterName");
        testFilter.setProductName("testProductName");

        when(filterService.getFilter(2)).thenReturn(testFilter);
        ResponseEntity<FilterTO> result = filterController.getFilter(2);
        assertEquals(5, result.getBody().getId());
        assertEquals("testFilterName", result.getBody().getName());
        assertEquals("testProductName", result.getBody().getProductName());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).getFilter(2);
    }

    @Test
    public void testGetFilterGroup() {
        FilterGroupTO testFilterGroup = new FilterGroupTO();
        testFilterGroup.setId(4);
        testFilterGroup.setName("testFilterGroupName");
        testFilterGroup.setProductName("testProductName");

        when(filterService.getFilterGroup(1)).thenReturn(testFilterGroup);
        ResponseEntity<FilterGroupTO> result = filterController.getFilterGroup(1);
        assertEquals(4, result.getBody().getId());
        assertEquals("testFilterGroupName", result.getBody().getName());
        assertEquals("testProductName", result.getBody().getProductName());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).getFilterGroup(1);
    }

    @Test
    public void testApplyFilter() {
        ApplyFiltersRequest request = new ApplyFiltersRequest();
        when(filterService.applyFilters(1, request)).thenReturn("Filters applied");
        ResponseEntity<String> result = filterController.applyFilters(1, request);
        assertEquals("Filters applied", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).applyFilters(1, request);

        when(filterService.applyFilters(2, request)).thenReturn(null);
        result = filterController.applyFilters(2, request);
        assertEquals("Bad JSON request", result.getBody());
        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    public void testDeleteFilter() {
        when(filterService.deleteFilter(3)).thenReturn("");
        ResponseEntity<String> result = filterController.deleteFilter(3);

        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCodeValue());
        verify(filterService).deleteFilter(3);

        when(filterService.deleteFilter(3)).thenReturn("Filter with filter id 3 does not exist");
        result = filterController.deleteFilter(3);
        assertTrue(result.getBody().contains("not exist"));
        assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    public void testDeleteFilterGroup() {
        when(filterService.deleteFilterGroup(4)).thenReturn("");
        ResponseEntity<String> result = filterController.deleteFilterGroup(4);

        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCodeValue());
        verify(filterService).deleteFilterGroup(4);

        when(filterService.deleteFilterGroup(4)).thenReturn("Filter Group with filter group id 4 does not exist");
        result = filterController.deleteFilterGroup(4);
        assertTrue(result.getBody().contains("not exist"));
        assertEquals(404, result.getStatusCodeValue());
    }
}
