/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.services.agent.AgentServiceV2;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinition;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.Header;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class AgentControllerTest {
    @InjectMocks
    private AgentController agentController;

    @Mock
    private AgentServiceV2 agentService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGetPing() {
        when(agentService.ping()).thenReturn("PONG");
        ResponseEntity<String> result = agentController.ping();
        assertEquals("PONG", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).ping();
    }

    @Test
    public void testGetSettings() {
        when(agentService.getSettings()).thenReturn("testSettings");
        ResponseEntity<String> result = agentController.getSettings();
        assertEquals("testSettings", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).getSettings();
    }

    @Test
    public void testGetHeaders() {
        Headers testHeaders = new Headers();
        testHeaders.getHeaders().add(new Header("testKey", "testValue"));
        when(agentService.getHeaders()).thenReturn(testHeaders);
        ResponseEntity<Headers> result = agentController.getHeaders();
        assertEquals("testKey", result.getBody().getHeaders().get(0).getKey());
        assertEquals("testValue", result.getBody().getHeaders().get(0).getValue());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).getHeaders();
    }

    @Test
    public void testGetClients() {
        List<TankHttpClientDefinition> definitions = new ArrayList<>();
        definitions.add(new TankHttpClientDefinition("testClientName", "testClientClassName"));
        TankHttpClientDefinitionContainer testContainer = new TankHttpClientDefinitionContainer(definitions, "testDefaultDefinition");
        when(agentService.getClients()).thenReturn(testContainer);
        ResponseEntity<TankHttpClientDefinitionContainer> result = agentController.getClients();
        assertEquals("testClientName", result.getBody().getDefinitions().get(0).getName());
        assertEquals("testClientClassName", result.getBody().getDefinitions().get(0).getClassName());
        assertEquals("testDefaultDefinition", result.getBody().getDefaultDefinition());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).getClients();

    }

    @Test
    public void testGetSupportFiles() throws IOException {
        File testSupport = new File("src/test/resources/agent-support-files-test.zip");
        when(agentService.getSupportFiles()).thenReturn(testSupport);
        ResponseEntity<org.springframework.core.io.Resource> result = agentController.getSupportFiles();
        assertTrue(Objects.requireNonNull(result.getBody()).exists());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(testSupport.length(), result.getHeaders().getContentLength());
        assertEquals(testSupport.getName(), result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).getSupportFiles();
    }

    @Test
    public void testAgentReady() {
        AgentData testAgentData = new AgentData();
        AgentTestStartData testAgentStartData = new AgentTestStartData();
        testAgentStartData.setJobId("2");
        testAgentStartData.setRampTime(1L);
        testAgentStartData.setTotalAgents(1000);
        when(agentService.agentReady(testAgentData)).thenReturn(testAgentStartData);
        ResponseEntity<AgentTestStartData> result = agentController.agentReady(testAgentData);
        assertEquals("2", result.getBody().getJobId());
        assertEquals(1L, result.getBody().getRampTime());
        assertEquals(1000, result.getBody().getTotalAgents());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).agentReady(testAgentData);
    }

    @Test
    public void testStandaloneAgentAvailability() {
        AgentAvailability availability = new AgentAvailability("testInstanceId",
                "testInstanceUrl", 5,
                AgentAvailabilityStatus.AVAILABLE);
        ResponseEntity<Void> result = agentController.setStandaloneAgentAvailability(availability);
        assertEquals(200, result.getStatusCodeValue());
    }

    // Instance status operations
    @Test
    public void testGetInstanceStatus() {
        CloudVmStatus testStatus = new CloudVmStatus("testInstanceId", "2", "testSecurityGroup", JobStatus.Starting,
                                                      VMImageType.AGENT, VMRegion.US_WEST_2, null, null, 0, 0, null, null);
        when(agentService.getInstanceStatus("testInstanceId")).thenReturn(testStatus);
        ResponseEntity<CloudVmStatus> result = agentController.getInstanceStatus("testInstanceId");
        assertEquals("testInstanceId", result.getBody().getInstanceId());
        assertEquals("2", result.getBody().getJobId());
        assertEquals("testSecurityGroup", result.getBody().getSecurityGroup());
        assertEquals(JobStatus.Starting, result.getBody().getJobStatus());
        assertEquals(VMImageType.AGENT, result.getBody().getRole());
        assertEquals(VMRegion.US_WEST_2, result.getBody().getVmRegion());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).getInstanceStatus("testInstanceId");

        when(agentService.getInstanceStatus("testInstanceId")).thenReturn(null);
        result = agentController.getInstanceStatus("testInstanceId");
        assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    public void testSetInstanceStatus() {
        CloudVmStatus testStatus = new CloudVmStatus("testInstanceId", "2", "testSecurityGroup", JobStatus.Starting,
                VMImageType.AGENT, VMRegion.US_WEST_2, null, null, 0, 0, null, null);

        ResponseEntity<Void> result = agentController.setInstanceStatus("testInstanceStatusIdAPIV2", testStatus);
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).setInstanceStatus("testInstanceStatusIdAPIV2", testStatus);
    }

    @Test
    public void testStopInstance() {
        when(agentService.stopInstance("testInstanceIdAPIV2")).thenReturn("stopping");
        ResponseEntity<String> result = agentController.stopInstance("testInstanceIdAPIV2");
        assertEquals("stopping", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).stopInstance("testInstanceIdAPIV2");
    }

    @Test
    public void testPauseInstance() {
        when(agentService.pauseInstance("testInstanceIdAPIV2")).thenReturn("rampPaused");
        ResponseEntity<String> result = agentController.pauseInstance("testInstanceIdAPIV2");
        assertEquals("rampPaused", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).pauseInstance("testInstanceIdAPIV2");
    }

    @Test
    public void testResumeInstance() {
        when(agentService.resumeInstance("testInstanceIdAPIV2")).thenReturn("running");
        ResponseEntity<String> result = agentController.resumeInstance("testInstanceIdAPIV2");
        assertEquals("running", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).resumeInstance("testInstanceIdAPIV2");
    }

    @Test
    public void testKillInstance() {
        when(agentService.killInstance("testInstanceIdAPIV2")).thenReturn("shutting_down");
        ResponseEntity<String> result = agentController.killInstance("testInstanceIdAPIV2");
        assertEquals("shutting_down", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(agentService).killInstance("testInstanceIdAPIV2");
    }
}
