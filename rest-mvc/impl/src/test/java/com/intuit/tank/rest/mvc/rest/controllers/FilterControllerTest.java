/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.filters.FilterServiceV2;
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.filters.models.FilterContainer;
import com.intuit.tank.filters.models.FilterGroupDetailTO;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterGroupContainer;
import com.intuit.tank.filters.models.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(443);
        when(request.getRequestURI()).thenReturn("/v2/filters");
        when(request.getRequestURL()).thenReturn(new StringBuffer("https://localhost/v2/filters"));
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
        FilterTO testFilter = FilterTO.builder()
                .withId(5)
                .withName("testFilterName")
                .withProductName("testProductName")
                .build();
        FilterContainer filterContainer = FilterContainer.builder().withFilter(testFilter).build();
        when(filterService.getFilters()).thenReturn(filterContainer);

        ResponseEntity<FilterContainer> result = filterController.getFilters();
        assertEquals(5, result.getBody().getFilters().get(0).getId());
        assertEquals("testFilterName", result.getBody().getFilters().get(0).getName());
        assertEquals("testProductName", result.getBody().getFilters().get(0).getProductName());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).getFilters();
    }

    @Test
    public void testCreateOrUpdateFilter() {
        FilterTO requestFilter = FilterTO.builder()
                .withName("testFilterName")
                .withCreator("sync-user")
                .build();
        FilterTO savedFilter = FilterTO.builder()
                .withId(5)
                .withName("testFilterName")
                .withCreator("sync-user")
                .build();
        when(filterService.createOrUpdateFilter(requestFilter)).thenReturn(savedFilter);

        ResponseEntity<FilterTO> result = filterController.createOrUpdateFilter(requestFilter);

        assertEquals(201, result.getStatusCodeValue());
        assertEquals(5, result.getBody().getId());
        assertEquals("https://localhost/v2/filters/5", result.getHeaders().getLocation().toString());
        verify(filterService).createOrUpdateFilter(requestFilter);
    }

    @Test
    public void testUpdateFilter() {
        FilterTO requestFilter = FilterTO.builder()
                .withId(5)
                .withName("testFilterName")
                .build();
        when(filterService.createOrUpdateFilter(requestFilter)).thenReturn(requestFilter);

        ResponseEntity<FilterTO> result = filterController.createOrUpdateFilter(requestFilter);

        assertEquals(200, result.getStatusCodeValue());
        assertFalse(result.getHeaders().containsKey("Location"));
        verify(filterService).createOrUpdateFilter(requestFilter);
    }

    @Test
    public void testGetFilterGroups() {
        FilterGroupTO testFilterGroup = FilterGroupTO.builder()
                .withId(4)
                .withName("testFilterGroupName")
                .withProductName("testProductName")
                .withFilterIds(List.of(2, 5))
                .build();
        FilterGroupContainer filterGroupContainer = FilterGroupContainer.builder().withFilterGroup(testFilterGroup).build();
        when(filterService.getFilterGroups()).thenReturn(filterGroupContainer);

        ResponseEntity<FilterGroupContainer> result = filterController.getFilterGroups();
        assertEquals(4, result.getBody().getFilterGroups().get(0).getId());
        assertEquals("testFilterGroupName", result.getBody().getFilterGroups().get(0).getName());
        assertEquals("testProductName", result.getBody().getFilterGroups().get(0).getProductName());
        assertEquals(List.of(2, 5), result.getBody().getFilterGroups().get(0).getFilterIds());
        assertEquals(200, result.getStatusCodeValue());
        verify(filterService).getFilterGroups();
    }

    @Test
    public void testGetFilter() {
        FilterTO testFilter = FilterTO.builder()
                .withId(5)
                .withName("testFilterName")
                .withProductName("testProductName")
                .build();

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
        FilterGroupDetailTO testFilterGroup = new FilterGroupDetailTO();
        testFilterGroup.setId(4);
        testFilterGroup.setName("testFilterGroupName");
        testFilterGroup.setProductName("testProductName");
        testFilterGroup.setFilterIds(List.of(5));
        testFilterGroup.setFilters(List.of(FilterTO.builder()
                .withId(5)
                .withName("testFilterName")
                .build()));

        when(filterService.getFilterGroup(1)).thenReturn(testFilterGroup);
        ResponseEntity<FilterGroupDetailTO> result = filterController.getFilterGroup(1);
        assertEquals(4, result.getBody().getId());
        assertEquals("testFilterGroupName", result.getBody().getName());
        assertEquals("testProductName", result.getBody().getProductName());
        assertEquals(List.of(5), result.getBody().getFilterIds());
        assertEquals("testFilterName", result.getBody().getFilters().get(0).getName());
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

    @Test
    public void testUpdateFilterById() {
        FilterTO request = FilterTO.builder().withName("f").build();
        FilterTO saved = FilterTO.builder().withId(3).withName("f").build();
        when(filterService.updateFilter(3, request)).thenReturn(saved);

        ResponseEntity<FilterTO> result = filterController.updateFilter(3, request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(3, result.getBody().getId());
    }

    @Test
    public void testCopyFilter() {
        CopyRequest request = new CopyRequest("copy");
        when(filterService.copyFilter(3, request)).thenReturn(FilterTO.builder().withId(11).withName("copy").build());

        ResponseEntity<FilterTO> result = filterController.copyFilter(3, request);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(11, result.getBody().getId());
        assertTrue(result.getHeaders().getLocation().toString().endsWith("/v2/filters/11"));
    }

    @Test
    public void testCreateFilterGroup() {
        FilterGroupTO request = FilterGroupTO.builder().withName("grp").withFilterIds(List.of(1)).build();
        FilterGroupDetailTO saved = new FilterGroupDetailTO();
        saved.setId(6);
        when(filterService.createFilterGroup(request)).thenReturn(saved);

        ResponseEntity<FilterGroupDetailTO> result = filterController.createFilterGroup(request);

        assertEquals(201, result.getStatusCode().value());
        assertTrue(result.getHeaders().getLocation().toString().endsWith("/v2/filters/groups/6"));
    }

    @Test
    public void testUpdateFilterGroup() {
        FilterGroupTO request = FilterGroupTO.builder().withName("grp").build();
        FilterGroupDetailTO saved = new FilterGroupDetailTO();
        saved.setId(6);
        when(filterService.updateFilterGroup(6, request)).thenReturn(saved);

        ResponseEntity<FilterGroupDetailTO> result = filterController.updateFilterGroup(6, request);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(6, result.getBody().getId());
    }

    @Test
    public void testCopyFilterGroup() {
        CopyRequest request = new CopyRequest("copy");
        FilterGroupDetailTO saved = new FilterGroupDetailTO();
        saved.setId(12);
        when(filterService.copyFilterGroup(6, request)).thenReturn(saved);

        ResponseEntity<FilterGroupDetailTO> result = filterController.copyFilterGroup(6, request);

        assertEquals(201, result.getStatusCode().value());
        assertTrue(result.getHeaders().getLocation().toString().endsWith("/v2/filters/groups/12"));
    }
}
