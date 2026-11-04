/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.JobActionResult;
import com.intuit.tank.rest.mvc.rest.models.JobDetails;
import com.intuit.tank.rest.mvc.rest.models.JobLaunchRequest;
import com.intuit.tank.rest.mvc.rest.models.JobPreview;
import com.intuit.tank.rest.mvc.rest.models.JobTree;
import com.intuit.tank.rest.mvc.rest.models.QueuedJob;
import com.intuit.tank.rest.mvc.rest.models.Timeseries;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobQueueServiceV2;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectJobControllerTest {

    @InjectMocks
    private ProjectJobController projectJobs;

    @InjectMocks
    private JobController jobs;

    @Mock
    private JobQueueServiceV2 service;

    @Mock
    private HttpServletRequest request;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() throws Exception {
        RequestContextHolder.resetRequestAttributes();
        mocks.close();
    }

    @Test
    void previewAndQueue() {
        JobLaunchRequest launch = new JobLaunchRequest("nightly");
        JobPreview preview = new JobPreview("nightly", true, List.of(), List.of(), 10, 1, 2, 3, "<p/>");
        when(service.previewJob(1, launch)).thenReturn(preview);
        when(service.queueJob(1, launch)).thenReturn(new QueuedJob(500, "nightly", "Created"));

        assertSame(preview, projectJobs.previewJob(1, launch).getBody());
        ResponseEntity<QueuedJob> queued = projectJobs.queueJob(1, launch);
        assertEquals(201, queued.getStatusCode().value());
        assertTrue(queued.getHeaders().getLocation().toString().endsWith("/v2/jobs/500/details"));
    }

    @Test
    void jobRoutes() {
        JobTree tree = new JobTree(List.of(), List.of(), new Date());
        when(service.getJobTree(1, true)).thenReturn(tree);
        assertSame(tree, jobs.getJobTree(1, true).getBody());

        JobActionResult stopped = new JobActionResult("10", "stop", "Stopped");
        when(service.controlJob(10, "stop")).thenReturn(stopped);
        assertSame(stopped, jobs.controlJob(10, "stop").getBody());
        JobActionResult killed = new JobActionResult("i-1", "kill", "terminated");
        when(service.controlAgent("i-1", "kill")).thenReturn(killed);
        assertSame(killed, jobs.controlAgent("i-1", "kill").getBody());

        assertEquals(204, jobs.deleteJob(11).getStatusCode().value());
        verify(service).deleteJob(11);

        JobDetails details = new JobDetails(10, "j", "Running", "bob", null, null, null, 1, 2, null, "");
        when(service.getJobDetails(10)).thenReturn(details);
        assertSame(details, jobs.getJobDetails(10).getBody());
        Timeseries series = new Timeseries(List.of(), List.of());
        when(service.getUserSeries(10)).thenReturn(series);
        when(service.getTpsSeries(10, "i-1", 5L)).thenReturn(series);
        assertSame(series, jobs.getUserSeries(10).getBody());
        assertSame(series, jobs.getTpsSeries(10, "i-1", 5L).getBody());
    }
}
