/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.projects.AutomationRequest;
import com.intuit.tank.rest.mvc.rest.models.projects.AutomationRequest.AutomationRequestBuilder;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void testGetAllProjects() {
        List<ProjectTO> projects = new ArrayList<>();
        ProjectTO testProject = new ProjectTO();
        testProject.setId(5);
        testProject.setCreator("Test");
        testProject.setName("testProjectName");
        testProject.setProductName("testProductName");
        projects.add(testProject);
        ProjectContainer projectContainer = new ProjectContainer(projects);
        when(projectService.getAllProjects()).thenReturn(projectContainer);

        ResponseEntity<ProjectContainer> result = projectController.getAllProjects();
        assertEquals(5, result.getBody().getProjects().get(0).getId());
        assertEquals("Test", result.getBody().getProjects().get(0).getCreator());
        assertEquals("testProjectName", result.getBody().getProjects().get(0).getName());
        assertEquals("testProductName", result.getBody().getProjects().get(0).getProductName());
        assertEquals(200, result.getStatusCodeValue());
        verify(projectService).getAllProjects();
    }

    @Test
    public void testGetAllProjectNames() {
        Map<Integer, String> projectMap = new HashMap<>();
        projectMap.put(3, "testProject");
        when(projectService.getAllProjectNames()).thenReturn(projectMap);
        ResponseEntity<Map<Integer, String>> result = projectController.getAllProjectNames();
        assertEquals("testProject", result.getBody().get(3));
        assertEquals(200, result.getStatusCodeValue());
        verify(projectService).getAllProjectNames();
    }

    @Test
    public void testGetProject() {
        ProjectTO testProject = new ProjectTO();
        testProject.setId(9);
        testProject.setName("testProjectName");
        testProject.setProductName("testProductName");

        when(projectService.getProject(3)).thenReturn(testProject);
        ResponseEntity<ProjectTO> result = projectController.getProject(3);
        assertEquals(9, result.getBody().getId());
        assertEquals("testProjectName", result.getBody().getName());
        assertEquals("testProductName", result.getBody().getProductName());
        assertEquals(200, result.getStatusCodeValue());
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
                .withUserIntervalIncrement(1);
        AutomationRequest request = builder.build();
        when(projectService.createProject(request)).thenReturn(response);
        ResponseEntity<Map<String, String>> result = projectController.createProject(request);
        assertEquals("testProjectStatus", result.getBody().get("testProjectId"));
        assertEquals(201, result.getStatusCodeValue());
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
        assertEquals(200, result.getStatusCodeValue());
        verify(projectService).updateProject(1, request);
    }

    @Test
    public void testDownloadTestScriptForProject() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ProjectTO projectTO = new ProjectTO();
        projectTO.setId(4);
        projectTO.setCreator("Test");
        projectTO.setName("testName");
        projectTO.setLocation("testLocation");
        projectTO.setProductName("testProductName");
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(projectTO);
        String filename = "project_test_H.xml";
        payload.put(filename, streamingResponse);
        when(projectService.downloadTestScriptForProject(4)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = projectController.downloadTestScriptForProject(4);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCodeValue());
        verify(projectService).downloadTestScriptForProject(4);
    }

    @Test
    public void testDeleteProject() {
        when(projectService.deleteProject(2)).thenReturn("");
        ResponseEntity<String> result = projectController.deleteProject(2);
        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCodeValue());
        verify(projectService).deleteProject(2);

        when(projectService.deleteProject(3)).thenReturn("Project with id 2 does not exist");
        ResponseEntity<String> error = projectController.deleteProject(3);
        assertTrue(error.getBody().contains("not exist"));
        assertEquals(404, error.getStatusCodeValue());
    }

}
