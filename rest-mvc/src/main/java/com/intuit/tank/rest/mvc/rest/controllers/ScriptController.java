/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.api.model.v1.script.ExternalScriptContainer;
import com.intuit.tank.api.model.v1.script.ExternalScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptDescriptionContainer;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptServiceV2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.net.URI;
import java.util.Map;
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
            @ApiResponse(responseCode = "200", description = "Successfully found all script descriptions"),
            @ApiResponse(responseCode = "404", description = "All scripts descriptions could not be found", content = @Content)
    })
    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns all script descriptions", summary = "Get all script descriptions")
    public ResponseEntity<ScriptDescriptionContainer> getScripts() {
        return new ResponseEntity<>(scriptService.getScripts(), HttpStatus.OK);
    }

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @Operation(description = "Returns all script names with corresponding script IDs", summary = "Get all script names with script IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all script names with IDs", content = @Content),
            @ApiResponse(responseCode = "404", description = "All script names with IDs could not be found", content = @Content)
    })
    public ResponseEntity<Map<Integer, String>> getAllScriptNames() {
        return new ResponseEntity<>(scriptService.getAllScriptNames(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{scriptId}", method = RequestMethod.GET)
    @Operation(description = "Returns a specific script description by script ID", summary = "Get a specific script description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found script description"),
            @ApiResponse(responseCode = "404", description = "Script description could not be found", content = @Content)
    })
    public ResponseEntity<ScriptDescription> getScript(@PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) {
        ScriptDescription script = scriptService.getScript(scriptId);
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

    @RequestMapping(value = "/download/{scriptId}", method = RequestMethod.GET)
    @Operation(description = "Downloads the Tank XML file for a script with the corresponding script ID", summary = "Download the Tank XML script file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the Tank XML script file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tank XML script file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadScript(@PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) throws IOException {
        Map<String, StreamingResponseBody> response = scriptService.downloadScript(scriptId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @RequestMapping(value = "/harness/download/{scriptId}", method = RequestMethod.GET)
    @Operation(description = "Downloads the Tank Harness file for a script with the corresponding script ID", summary = "Download the Tank Harness script file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the Tank Harness script file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tank Harness script file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadHarnessScript(@PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) throws IOException {
        Map<String, StreamingResponseBody> response = scriptService.downloadHarnessScript(scriptId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(description = "Uploads a script XML file output generated by the Tank Proxy Package (supports gzip files) \n\n " +
                             "Examples: \n\n" +
                             "        curl -v -X POST -H 'Content-Type: multipart/form-data' -F \"file=@<filename>\" 'https://{tank-base-url}/v2/scripts/upload' \n\n" +
                             "\n\n" +
                             "        curl -v -X POST -H 'Content-Type: multipart/form-data' -F \"file=@<filename>\" 'https://{tank-base-url}/v2/scripts/upload?id=<scriptId>&name=<script-name>'\n\n" +
                             "\n\n" +
                             "        gzip <filename>\n\n" +
                             "        curl -v -X POST -H 'Content-Type: multipart/form-data' -H 'Content-Encoding: gzip' -F \"file=@<filename>.gz\" 'https://{tank-base-url}/v2/scripts/upload'\n\n" +
                             "Notes: \n\n " +
                             " - This endpoint only accepts XML script file recorded and produced by the Tank Proxy Package (see Tools tab in Tank)\n\n" +
                             " - Both script name and script id are optional parameters \n\n" +
                             " - Passing in an existing Tank Script ID will overwrite the existing script in Tank, but will otherwise create a new Tank script", summary = "Upload Tank Proxy script to Tank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully uploaded script file to Tank", content = @Content),
            @ApiResponse(responseCode = "400", description = "Script file could not be uploaded", content = @Content)
    })
    public ResponseEntity<Map<String, String>> uploadScript(@RequestHeader(value = HttpHeaders.CONTENT_ENCODING, required = false) @Parameter(description = "Content-Encoding", required = false) String contentEncoding,
                                               @RequestParam(required = false) @Parameter(description = "Script Name", required = false) String name,
                                               @RequestParam(required = false) @Parameter(description = "Existing Script ID (optional)", required = false) Integer id,
                                               @RequestParam(value = "file", required = true) @Parameter(schema = @Schema(type = "string", format = "binary", description = "Script file")) MultipartFile file) throws IOException{
        Map<String, String> response = scriptService.uploadProxyScript(name, id, contentEncoding, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(description = "Update an existing Tank script by importing a Tank script XML file (supports gzip files) \n\n " +
            "Examples: \n\n" +
            "        curl -v -X POST -H 'Content-Type: multipart/form-data' -F \"file=@<filename>\" 'https://{tank-base-url}/v2/scripts/update' \n\n" +
            "\n\n" +
            "        gzip <filename>\n\n" +
            "        curl -v -X POST -H 'Content-Type: multipart/form-data' -H 'Content-Encoding: gzip' -F \"file=@<filename>.gz\" 'https://{tank-base-url}/v2/scripts/update'\n\n" +
            "Notes: \n\n " +
            " - This endpoint only accepts Tank script XML files for an existing Tank script to update. The script ID and script name in the file must match an existing script entry in Tank\n\n" +
            " - This endpoint can be used to overwrite any Tank script by opening the Tank script XML file and changing the < id > and < name > key values to match the ID and script name of the existing Tank script \n\n", summary = "Import Tank XML script to Tank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully updated script in Tank", content = @Content),
            @ApiResponse(responseCode = "400", description = "Script could not be updated", content = @Content)
    })
    public ResponseEntity<Map<String, String>> updateTankScript(@RequestHeader(value = HttpHeaders.CONTENT_ENCODING, required = false) @Parameter(description = "Content-Encoding", required = false) String contentEncoding,
                                                            @RequestParam(value = "file", required = true) @Parameter(schema = @Schema(type = "string", format = "binary", description = "Script file")) MultipartFile file) throws IOException{
        Map<String, String> response = scriptService.updateTankScript(contentEncoding, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{scriptId}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Deletes a script by script ID", summary = "Delete a script")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No content (script delete successful)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Script not found", content = @Content) })
    public ResponseEntity<String> deleteScript(
            @PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) {
        String response = scriptService.deleteScript(scriptId);
        if (Objects.equals(response, "")) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // External Scripts

    @RequestMapping(value = "/external", method = RequestMethod.GET)
    @Operation(description = "Returns all external scripts descriptions", summary = "Get all external script descriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all external scripts descriptions"),
            @ApiResponse(responseCode = "404", description = "All external scripts descriptions could not be found", content = @Content)
    })
    public ResponseEntity<ExternalScriptContainer> getExternalScripts() {
        return new ResponseEntity<>(scriptService.getExternalScripts(), HttpStatus.OK);
    }

    @RequestMapping(value = "/external/{externalScriptId}", method = RequestMethod.GET)
    @Operation(description = "Returns an external script description by external script ID", summary = "Get a specific external script description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found external script description"),
            @ApiResponse(responseCode = "404", description = "External script description could not be found", content = @Content)
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
            @RequestBody @Parameter(description = "External Script JSON request payload", required = true) ExternalScriptTO script) {
        ExternalScriptTO savedScript = scriptService.createExternalScript(script);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().scheme("https").path("/{id}").buildAndExpand(savedScript.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(savedScript, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/external/download/{externalScriptId}", method = RequestMethod.GET)
    @Operation(description = "Downloads the Tank XML file for an external script with the corresponding external script ID", summary = "Download an external script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the Tank XML external script file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tank XML external script file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadExternalScript(@PathVariable @Parameter(description = "External Script ID", required = true) Integer externalScriptId) throws IOException {
        Map<String, StreamingResponseBody> response = scriptService.downloadExternalScript(externalScriptId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
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
