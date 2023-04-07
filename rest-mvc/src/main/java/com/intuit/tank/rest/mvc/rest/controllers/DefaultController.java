/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.clients.AgentClient;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import org.springframework.web.reactive.function.client.WebClient;
import com.intuit.tank.rest.mvc.rest.clients.DataFileClient;
import com.intuit.tank.rest.mvc.rest.clients.ProjectClient;
import com.intuit.tank.rest.mvc.rest.clients.ScriptClient;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ExternalScriptTO;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptDescriptionContainer;

import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Tag(name = "Default")
public class DefaultController {

    private ScriptClient scriptClient;
    private AgentClient agentClient;
    private ProjectClient projectClient;
    private DataFileClient dataFileClient;

    @GetMapping(value ="/v2/ping",  produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings Tank V2 API default service", summary = "Check if Tank V2 API is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tank V2 API is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("PONG Tank V2 API", HttpStatus.OK);
    }

    @GetMapping(value ="/v2/pong",  produces = { MediaType.TEXT_PLAIN_VALUE } )
    public String pong() {
        this.dataFileClient = new  DataFileClient("http://localhost:8080/tank_war");

//        String result = WebClient.create("http://localhost:8080/tank_war/api/v2/datafiles")
//                .get()
//                .uri(uriBuilder -> uriBuilder.path("/content")
//                        .queryParam("id", "24")
//                        .build())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();


        InputStream is = IOUtils.toInputStream(dataFileClient.getDatafileContent(24), StandardCharsets.UTF_8);

        return dataFileClient.getDatafileContent(24);
    }
}
