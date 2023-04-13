/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.common.CloudVmStatus;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.rest.mvc.rest.services.agent.AgentServiceV2;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/v2/agent", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Agent")
public class AgentController {

    @Resource
    private AgentServiceV2 agentService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings agent service", summary = "Check if agent service is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agent Service is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<String>(agentService.ping(), HttpStatus.OK);
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Returns XML of agent settings file", summary = "Get the agent settings file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found agent settings file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Agent settings file could not be found", content = @Content)
    })
    public  ResponseEntity<String> getSettings() {
        return new ResponseEntity<>(agentService.getSettings(), HttpStatus.OK);
    }

    @RequestMapping(value = "/support-files", method = RequestMethod.GET, produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @Operation(description = "Returns streaming output of a gzipped archive of the support files.", summary = "Gets the agent support files (jar files)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found agent support files", content = @Content),
            @ApiResponse(responseCode = "404", description = "Agent support files could not be found", content = @Content)
    })
    public ResponseEntity<org.springframework.core.io.Resource> getSupportFiles() throws IOException {
        File supportFiles = agentService.getSupportFiles();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(supportFiles));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + supportFiles.getName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(supportFiles.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @RequestMapping(value = "/ready", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Registers an agent instance to a job and sets it's status to ready to start", summary = "Set an agent instance status to ready to start", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully set agent to ready"),
            @ApiResponse(responseCode = "400", description = "Could not register agent due to bad request", content = @Content)
    })
    public ResponseEntity<AgentTestStartData> agentReady(
            @RequestBody @Parameter(description = "agentData object that contains agent data settings", required = true) AgentData agentData) {
        return new ResponseEntity<AgentTestStartData>(agentService.agentReady(agentData), HttpStatus.OK);
    }

    @RequestMapping(value = "/headers", method = RequestMethod.GET)
    @Operation(description = "Returns agent headers", summary = "Get the agent headers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found agent headers"),
            @ApiResponse(responseCode = "404", description = "Agent headers could not be found", content = @Content)
    })
    public ResponseEntity<Headers> getHeaders() {
        return new ResponseEntity<>(agentService.getHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/clients", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Returns the agent client definitions", summary = "Get the agent client definitions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found agent client definitions"),
            @ApiResponse(responseCode = "404", description = "Agent client definitions could not be found", content = @Content)
    })
    public ResponseEntity<TankHttpClientDefinitionContainer> getClients() {
        return new ResponseEntity<>(agentService.getClients(), HttpStatus.OK);
    }

    @RequestMapping(value = "/availability", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Sets the availability status for a standalone agent", summary = "Set standalone agent availability", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully set agent availability"),
            @ApiResponse(responseCode = "400", description = "Could not update agent availability due to bad request", content = @Content)
    })
    public ResponseEntity<Void> setStandaloneAgentAvailability(
            @RequestBody @Parameter(description = "Agent availability request to update standalone agent availability", required = true) AgentAvailability availability) {
        agentService.setStandaloneAgentAvailability(availability);
        return ResponseEntity.ok().build();
    }

    // Instance status operations
    @RequestMapping(value = "/instance/status/{instanceId}", method = RequestMethod.GET)
    @Operation(description = "Returns the agent instance status", summary = "Get the agent instance status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found agent instance status"),
            @ApiResponse(responseCode = "404", description = "Agent instance status could not be found", content = @Content)
    })
    public ResponseEntity<CloudVmStatus> getInstanceStatus(@PathVariable @Parameter(description = "The instance ID associated with the instance", required = true) String instanceId) {
        CloudVmStatus status = agentService.getInstanceStatus(instanceId);
        if (status != null) {
            return new ResponseEntity<CloudVmStatus>(status, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/instance/status/{instanceId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Sets the agent instance status via instanceID and CloudVMStatus payload", summary = "Set the agent instance status", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully set agent instance status"),
            @ApiResponse(responseCode = "400", description = "Could not update agent instance status due to bad request", content = @Content)
    })
    public ResponseEntity<Void> setInstanceStatus(@PathVariable @Parameter(description = "The instance ID associated with the instance", required = true) String instanceId,
                                                  @RequestBody @Parameter(description = "CloudVmStatus object that contains updated content", required = true) CloudVmStatus status) {
        agentService.setInstanceStatus(instanceId, status);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/instance/stop/{instanceId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Stops specific agent instance by instance ID", summary = "Stop an agent instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully stopped agent instance"),
            @ApiResponse(responseCode = "400", description = "Could not update agent instance status due to invalid instanceId", content = @Content)
    })
    public ResponseEntity<String> stopInstance(@PathVariable @Parameter(description = "The instance ID associated with the instance", required = true) String instanceId) {
        String status = agentService.stopInstance(instanceId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/instance/pause/{instanceId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pauses a specific running agent instance by instance ID", summary = "Pause a running agent instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully paused agent instance"),
            @ApiResponse(responseCode = "400", description = "Could not update agent instance status due to invalid instanceId", content = @Content)
    })
    public ResponseEntity<String> pauseInstance(@PathVariable @Parameter(description = "The instance ID associated with the instance", required = true) String instanceId) {
        String status = agentService.pauseInstance(instanceId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/instance/resume/{instanceId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Resumes a specific paused agent instance by instance ID", summary = "Resume a paused agent instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully resumed agent instance"),
            @ApiResponse(responseCode = "400", description = "Could not update agent instance status due to invalid instanceId", content = @Content)
    })
    public ResponseEntity<String> resumeInstance(@PathVariable @Parameter(description = "The instance ID associated with the instance", required = true) String instanceId) {
        String status = agentService.resumeInstance(instanceId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/instance/kill/{instanceId}", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Terminates a specific agent instance by instance ID", summary = "Terminate an agent instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully terminated agent instance"),
            @ApiResponse(responseCode = "400", description = "Could not update agent instance status due to invalid instanceId", content = @Content)
    })
    public ResponseEntity<String> killInstance(@PathVariable @Parameter(description = "The instance ID associated with the instance", required = true) String instanceId) {
        String status = agentService.killInstance(instanceId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
