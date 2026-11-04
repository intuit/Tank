/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

import com.intuit.tank.rest.mvc.rest.controllers.DataFileController;
import com.intuit.tank.rest.mvc.rest.controllers.ProjectController;
import com.intuit.tank.rest.mvc.rest.services.datafiles.DataFileServiceV2;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sends requests through real controller mappings, where Spring raises these exceptions before any controller
 * method runs.
 */
public class GenericExceptionHandlerMvcTest {

    private ProjectServiceV2 projectService;
    private DataFileServiceV2 dataFileService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        ProjectController projectController = new ProjectController();
        projectService = mock(ProjectServiceV2.class);
        ReflectionTestUtils.setField(projectController, "projectService", projectService);
        DataFileController dataFileController = new DataFileController();
        dataFileService = mock(DataFileServiceV2.class);
        ReflectionTestUtils.setField(dataFileController, "dataFileService", dataFileService);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController, dataFileController)
                .setControllerAdvice(new GenericExceptionHandler())
                .build();
    }

    @Test
    public void testUnacceptableMediaTypeIs406() throws Exception {
        mockMvc.perform(delete("/v2/projects/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());
        verify(projectService, never()).deleteProject(anyInt());
    }

    @Test
    public void testAcceptingTheEndpointsMediaTypeStillWorks() throws Exception {
        when(projectService.deleteProject(1)).thenReturn("");
        mockMvc.perform(delete("/v2/projects/1").accept(MediaType.ALL))
                .andExpect(status().isNoContent());
        verify(projectService).deleteProject(1);
    }

    @Test
    public void testPathVariableTypeMismatchIs400() throws Exception {
        mockMvc.perform(get("/v2/datafiles/not-a-number").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
