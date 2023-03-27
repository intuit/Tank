/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.scripts.ExternalScriptContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ExternalScriptTO;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptDescriptionContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptTO;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptServiceV2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/v2/scripts", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Scripts")
public class ScriptController {
    @Resource
    private ScriptServiceV2 scriptService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings script service", summary = "Checks if script service is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Script Service is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>(scriptService.ping(), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all scripts"),
            @ApiResponse(responseCode = "404", description = "All scripts could not be found", content = @Content)
    })
    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns all script descriptions", summary = "Get all script descriptions")
    public ResponseEntity<ScriptDescriptionContainer> getScripts() {
        return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{scriptId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Returns a specific script description by script ID", summary = "Get a specific script description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found script"),
            @ApiResponse(responseCode = "404", description = "Script could not be found", content = @Content)
    })
    public ResponseEntity<ScriptTO> getScript(@PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) {
        ScriptTO script = scriptService.getScript(scriptId);
        if (script != null){
            return new ResponseEntity<>(script, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creates and saves a new script and returns the corresponding script description on success", summary = "Create a new script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created script"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<ScriptTO> createScript(
            @RequestBody @Parameter(description = "Script JSON request payload", required = true) ScriptTO scriptTo) {
        ScriptTO savedScript = scriptService.createScript(scriptTo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().scheme("https").path("/{id}").buildAndExpand(savedScript.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(savedScript, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{scriptID}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Deletes a script by script ID", summary = "Delete a script")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No content (script delete successful)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Script not found", content = @Content) })
    public ResponseEntity<String> deleteScript(
            @PathVariable @Parameter(description = "Script ID", required = true) Integer scriptID) {
        String response = scriptService.deleteScript(scriptID);
        if (Objects.equals(response, "")) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // External Scripts

    @RequestMapping(value = "/external", method = RequestMethod.GET)
    @Operation(description = "Returns all external scripts descriptions", summary = "Get all external script descriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all external scripts"),
            @ApiResponse(responseCode = "404", description = "All external scripts could not be found", content = @Content)
    })
    public ResponseEntity<ExternalScriptContainer> getExternalScripts() {
        return new ResponseEntity<>(scriptService.getExternalScripts(), HttpStatus.OK);
    }

    @RequestMapping(value = "/external/{externalScriptId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Returns an external script by external script ID", summary = "Get a specific external script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found external script"),
            @ApiResponse(responseCode = "404", description = "External script could not be found", content = @Content)
    })
    public ResponseEntity<ExternalScriptTO> getExternalScript(@PathVariable @Parameter(description = "External Script ID", required = true) Integer externalScriptId) {
        ExternalScriptTO script = scriptService.getExternalScript(externalScriptId);
        if (script != null){
            return new ResponseEntity<>(script, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/external", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creates and saves a new external script and returns the corresponding external script description on success", summary = "Create a new external script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created external script"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<ExternalScriptTO> createExternalScript(
            @RequestBody @Parameter(description = "Script JSON request payload", required = true) ExternalScriptTO script) {
        ExternalScriptTO savedScript = scriptService.createExternalScript(script);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().scheme("https").path("/{id}").buildAndExpand(savedScript.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(savedScript, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/external/{externalScriptId}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Deletes a external script by external script ID", summary = "Delete a external script")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No content (external script delete successful)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
    public ResponseEntity<String> deleteExternalScript(
            @PathVariable @Parameter(description = "External Script ID", required = true) Integer externalScriptId) {
        String response = scriptService.deleteExternalScript(externalScriptId);
        if (Objects.equals(response, "")) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
