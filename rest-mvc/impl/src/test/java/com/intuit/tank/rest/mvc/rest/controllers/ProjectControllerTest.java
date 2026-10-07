/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.projects.models.*;
import com.intuit.tank.projects.models.AutomationRequest.AutomationRequestBuilder;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ProjectCopyRequest;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectSummary;
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2;
import com.intuit.tank.rest.mvc.rest.util.ProjectFixtures;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;


public class ProjectControllerTest {
    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectServiceV2 projectService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGetPing() {
        when(projectService.ping()).thenReturn("PONG");
        ResponseEntity<String> result = projectController.ping();
        assertEquals("PONG", result.getBody());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    public void testGetAllProjects() {
        ProjectTO project = ProjectTO.builder()
                .withId(5)
                .withCreator("Test")
                .withName("testProjectName")
                .withProductName("testProductName")
                .build();
        ProjectContainer projectContainer = ProjectContainer.builder().withProject(project).build();
        when(projectService.getAllProjects()).thenReturn(projectContainer);

        ResponseEntity<ProjectContainer> result = projectController.getAllProjects();
        assertEquals(5, result.getBody().getProjects().get(0).getId());
        assertEquals("Test", result.getBody().getProjects().get(0).getCreator());
        assertEquals("testProjectName", result.getBody().getProjects().get(0).getName());
        assertEquals("testProductName", result.getBody().getProjects().get(0).getProductName());
        assertEquals(200, result.getStatusCode().value());
        verify(projectService).getAllProjects();
    }

    @Test
    public void testGetAllProjectNames() {
        Map<Integer, String> projectMap = new HashMap<>();
        projectMap.put(3, "testProject");
        when(projectService.getAllProjectNames()).thenReturn(projectMap);
        ResponseEntity<Map<Integer, String>> result = projectController.getAllProjectNames();
        assertEquals("testProject", result.getBody().get(3));
        assertEquals(200, result.getStatusCode().value());
        verify(projectService).getAllProjectNames();
    }

    @Test
    public void testGetProject() {
        ProjectTO testProject = ProjectTO.builder()
                .withId(9)
                .withName("testProjectName")
                .withProductName("testProductName")
                .build();

        when(projectService.getProject(3)).thenReturn(testProject);
        ResponseEntity<ProjectTO> result = projectController.getProject(3);
        assertEquals(9, result.getBody().getId());
        assertEquals("testProjectName", result.getBody().getName());
        assertEquals("testProductName", result.getBody().getProductName());
        assertEquals(200, result.getStatusCode().value());
        verify(projectService).getProject(3);
    }

    @Test
    public void testCreateProject() {
        Map<String, String> response = new HashMap<>();
        response.put("testProjectId", "testProjectStatus");
        AutomationRequestBuilder builder = AutomationRequest.builder()
                .withProductName("Test Product")
                .withRampTime("1920s")
                .withSimulationTime("7200s")
                .withUserIntervalIncrement(1)
                .withTestPlan(AutomationTestPlan.builder().withName("Main").withUserPercentage(100).withPosition(0).withScriptGroup(
                        AutomationScriptGroup.builder().withName("testScriptGroup").withLoop(0).withPosition(0).withScript(
                                AutomationScriptGroupStep.builder().withScriptId(123).withName("testScript").withLoop(1).withPosition(0)
                                        .build())
                                .build())
                        .build());
        AutomationRequest request = builder.build();
        when(projectService.createProject(request)).thenReturn(response);
        ResponseEntity<Map<String, String>> result = projectController.createProject(request);
        assertEquals("testProjectStatus", result.getBody().get("testProjectId"));
        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getHeaders().getLocation());
        verify(projectService).createProject(request);
    }

    @Test
    public void testUpdateProject() {
        Map<String, String> response = new HashMap<>();
        response.put("testProjectId", "testProjectStatus");
        AutomationRequestBuilder builder = AutomationRequest.builder()
                .withProductName("Test Product")
                .withRampTime("1920s")
                .withSimulationTime("7200s")
                .withUserIntervalIncrement(1);
        AutomationRequest request = builder.build();
        when(projectService.updateProject(1, request)).thenReturn(response);
        ResponseEntity<Map<String, String>> result = projectController.updateProject(1, request);
        assertEquals("testProjectStatus", result.getBody().get("testProjectId"));
        assertEquals(200, result.getStatusCode().value());
        verify(projectService).updateProject(1, request);
    }

    @Test
    public void testDownloadTestScriptForProject() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ProjectTO projectTO = ProjectTO.builder()
                .withId(4)
                .withCreator("Test")
                .withName("testName")
                .withLocation("testLocation")
                .withProductName("testProductName")
                .build();
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(projectTO);
        String filename = "project_test_H.xml";
        payload.put(filename, streamingResponse);
        when(projectService.downloadTestScriptForProject(4)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = projectController.downloadTestScriptForProject(4);
        assertEquals(MediaType.APPLICATION_XML, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCode().value());
        verify(projectService).downloadTestScriptForProject(4);
    }

    @Test
    public void testDeleteProject() {
        when(projectService.deleteProject(2)).thenReturn("");
        ResponseEntity<String> result = projectController.deleteProject(2);
        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCode().value());
        verify(projectService).deleteProject(2);

        when(projectService.deleteProject(3)).thenReturn("Project with id 2 does not exist");
        ResponseEntity<String> error = projectController.deleteProject(3);
        assertTrue(error.getBody().contains("not exist"));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    public void testListProjects() {
        PageResponse<ProjectSummary> page = new PageResponse<>(List.of(), 0, 1, 10);
        when(projectService.listProjects(1, 10, "name", "alice", "q")).thenReturn(page);
        assertSame(page, projectController.listProjects(1, 10, "name", "alice", "q").getBody());
    }

    @Test
    public void testProjectDetail() {
        ProjectDetail detail = ProjectFixtures.detail("Load", "alice");
        when(projectService.getProjectDetail(1)).thenReturn(detail);
        when(projectService.updateProjectDetail(1, detail)).thenReturn(detail);
        assertSame(detail, projectController.getProjectDetail(1).getBody());
        ResponseEntity<ProjectDetail> saved = projectController.updateProjectDetail(1, detail);
        assertEquals(200, saved.getStatusCode().value());
        assertSame(detail, saved.getBody());
    }

    @Test
    public void testCopyProject() {
        ProjectDetail copy = ProjectFixtures.detail("Copy", "alice");
        ProjectCopyRequest request = new ProjectCopyRequest("Copy");
        when(projectService.copyProject(1, request)).thenReturn(copy);
        ResponseEntity<ProjectDetail> result = projectController.copyProject(1, request);
        assertEquals(201, result.getStatusCode().value());
        assertTrue(result.getHeaders().getLocation().toString().endsWith("/v2/projects/1/full"));
        assertSame(copy, result.getBody());
    }

    @Test
    public void testDeleteProjects() {
        BulkDeleteResult deleted = new BulkDeleteResult(List.of(1), List.of(2));
        when(projectService.deleteProjects(List.of(1, 2))).thenReturn(deleted);
        assertSame(deleted, projectController.deleteProjects(List.of(1, 2)).getBody());
    }

    @Test
    public void testValidateProject() {
        ProjectValidation validation = new ProjectValidation(true, List.of(), List.of(), 1, 2, 3, List.of(), List.of(),
                List.of(), List.of());
        when(projectService.validateProject(1)).thenReturn(validation);
        assertSame(validation, projectController.validateProject(1).getBody());
    }
}
