/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.script.models.ExternalScriptContainer;
import com.intuit.tank.script.models.ExternalScriptTO;
import com.intuit.tank.script.models.ScriptDescription;
import com.intuit.tank.script.models.ScriptDescriptionContainer;
import com.intuit.tank.script.models.ScriptTO;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ScriptDocument;
import com.intuit.tank.rest.mvc.rest.models.ScriptSummary;
import com.intuit.tank.rest.mvc.rest.models.ApplyFiltersRequest;
import com.intuit.tank.rest.mvc.rest.models.DraftSteps;
import com.intuit.tank.rest.mvc.rest.models.LogicTestRequest;
import com.intuit.tank.rest.mvc.rest.models.LogicTestResult;
import com.intuit.tank.rest.mvc.rest.models.ScriptValidation;
import com.intuit.tank.rest.mvc.rest.models.StepMatch;
import com.intuit.tank.rest.mvc.rest.models.StepReplaceRequest;
import com.intuit.tank.rest.mvc.rest.models.StepSearchRequest;
import com.intuit.tank.rest.mvc.rest.models.ValidateStepsRequest;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptDraftServiceV2;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptServiceV2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.annotation.Resource;

@RestController
@RequestMapping(value = "/v2/scripts", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Scripts")
public class ScriptController {
    @Resource
    private ScriptServiceV2 scriptService;

    @Resource
    private ScriptDraftServiceV2 scriptDraftService;

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
            @ApiResponse(responseCode = "200", description = "Successfully found all script names with IDs"),
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
        if (script == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(script, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Operation(description = "Creates a new Tank script through either script file upload or copying an existing script. \n\n " +
            "Tank Script Upload (default): \n\n" +
            "        curl -v -X POST -H \"Authorization: Bearer <token>\" -F \"file=@<filename>\" 'https://{tank-base-url}/v2/scripts' \n\n" +
            "\n\n" +
            "        gzip <filename>\n\n" +
            "        curl -v -X POST -H \"Authorization: Bearer <token>\" -H 'Content-Encoding: gzip' -F \"file=@<filename>.gz\" 'https://{tank-base-url}/v2/scripts'\n\n" +
            "\n\n" +
            "Tank Proxy Recording Upload: \n\n" +
            "        curl -v -X POST -H \"Authorization: Bearer <token>\" -F \"file=@<filename>\" 'https://{tank-base-url}/v2/scripts?recording&id=<scriptId>&name=<script-name>' \n\n" +
            "\n\n" +
            "        gzip <filename>\n\n" +
            "        curl -v -X POST -H \"Authorization: Bearer <token>\" -H 'Content-Encoding: gzip' -F \"file=@<filename>.gz\" 'https://{tank-base-url}/v2/scripts?recording&id=<scriptId>&name=<script-name>' \n\n" +
            "Creating a copy of an existing script: \n\n" +
            "        curl -v -X POST -H \"Authorization: Bearer <token>\" 'https://{tank-base-url}/v2/scripts?copy&sourceId=<scriptId>&name=<script-name>' \n\n" +
            "Notes: \n\n " +
            " - **Tank Script Upload**: Only accepts Tank script XML files for an existing Tank script to update. The script ID and script name defined in the first few lines of script XML file should match an existing script entry in Tank to update that script, but setting the script ID to 0 will create a new script with any name.\n\n" +
            " - **Tank Proxy Recording Upload**: Only accepts the XML script file recorded and produced by the Tank Proxy Package (see Tools tab in Tank), setting id to an existing scriptId will overwrite that script, both scriptId and name parameters are optional. productName sets the script's product, and filterIds applies those script filters to the recording, in order \n\n " +
            " - **Copying**: You must pass a value for the name parameter to successfully create a copy of an existing script.\n\n " +
            "\n\n", summary = "Creates a new Tank script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully uploaded or copied the script to Tank"),
            @ApiResponse(responseCode = "400", description = "Script file could not be uploaded or copied", content = @Content)
    })
    public ResponseEntity<Map<String, String>> createScript(@RequestHeader(value = HttpHeaders.CONTENT_ENCODING, required = false) @Parameter(description = "Content-Encoding", required = false) String contentEncoding,
                                                 @RequestParam(required = false) @Parameter(description = "Script Name", required = false) String name,
                                                 @RequestParam(required = false) @Parameter(description = "Existing Script ID to overwrite (optional)", required = false) Integer id,
                                                 @RequestParam(name = "recording", required = false) @Parameter(description = "Enables Tank Proxy Recording file upload mode", required = false) String recording,
                                                 @RequestParam(required = false) @Parameter(description = "Enables copying from existing Tank Script", required = false) String copy,
                                                 @RequestParam(required = false) @Parameter(description = "Source ScriptId to copy from", required = false) Integer sourceId,
                                                 @RequestParam(value = "file", required = false) @Parameter(schema = @Schema(type = "string", format = "binary", description = "Script file")) MultipartFile file,
                                                 @RequestParam(required = false) @Parameter(description = "Recording upload: the script's product", required = false) String productName,
                                                 @RequestParam(required = false) @Parameter(description = "Recording upload: script filter IDs to apply, in order", required = false) List<Integer> filterIds) throws IOException{
        Map<String, String> response = scriptService.createScript(name, id, recording, copy, sourceId, contentEncoding, file,
                productName, filterIds);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/download/{scriptId}", method = RequestMethod.GET)
    @Operation(description = "Downloads the Tank XML file for a script with the corresponding script ID", summary = "Download the Tank XML script file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the Tank XML script file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tank XML script file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadScript(@PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) throws IOException {
        Map<String, StreamingResponseBody> response = scriptService.downloadScript(scriptId);
        if (response == null) return ResponseEntity.notFound().build();

        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @RequestMapping(value = "/harness/download/{scriptId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Downloads the Tank Harness file for a script with the corresponding script ID", summary = "Download the Tank Harness script file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the Tank Harness script file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tank Harness script file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadHarnessScript(@PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) throws IOException {
        Map<String, StreamingResponseBody> response = scriptService.downloadHarnessScript(scriptId);
        if (response == null) return ResponseEntity.notFound().build();

        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .body(responseBody);
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
        if (script == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(script, HttpStatus.OK);
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

    @RequestMapping(value = "/external/download/{externalScriptId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE })
    @Operation(description = "Downloads the Tank XML file for an external script with the corresponding external script ID", summary = "Download an external script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the Tank XML external script file", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tank XML external script file could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadExternalScript(@PathVariable @Parameter(description = "External Script ID", required = true) Integer externalScriptId) throws IOException {
        Map<String, StreamingResponseBody> response = scriptService.downloadExternalScript(externalScriptId);
        if (response == null) return ResponseEntity.notFound().build();

        String filename = response.keySet().iterator().next();
        StreamingResponseBody responseBody = response.get(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_XML)
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

    @RequestMapping(method = RequestMethod.GET, params = "page")
    @Operation(description = "Lists scripts one page at a time. The page parameter selects this form; without it "
            + "GET /v2/scripts returns every script unpaged", summary = "List scripts (paged)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the page"),
            @ApiResponse(responseCode = "400", description = "Invalid page, size or sort", content = @Content)
    })
    public ResponseEntity<PageResponse<ScriptSummary>> listScripts(
            @RequestParam @Parameter(description = "Zero-based page number") Integer page,
            @RequestParam(required = false) @Parameter(description = "Page size, 1 to 200 (default 25)") Integer size,
            @RequestParam(required = false) @Parameter(description = "id, name, productName, owner, created, modified or "
                    + "runtime, optionally followed by ,asc or ,desc (default modified,desc)") String sort,
            @RequestParam(required = false) @Parameter(description = "Only scripts owned by this user") String owner,
            @RequestParam(required = false) @Parameter(description = "Text the name, product or comments contain") String q) {
        return ResponseEntity.ok(scriptService.listScripts(page, size, sort, owner, q));
    }

    @RequestMapping(value = "/{scriptId}/steps", method = RequestMethod.GET)
    @Operation(description = "Returns the script with every step, for the script editor. Recorded responses are left out "
            + "and authentication passwords are masked", summary = "Get a script for editing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the script"),
            @ApiResponse(responseCode = "404", description = "No such script", content = @Content)
    })
    public ResponseEntity<ScriptDocument> getScriptDocument(
            @PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId) {
        return ResponseEntity.ok(scriptService.getScriptDocument(scriptId));
    }

    @RequestMapping(value = "/{scriptId}/steps", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Replaces the script's name, product, comments and every step, in list order. Send modified "
            + "from the GET; if the script was saved since, the request is rejected with 409", summary = "Save a script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved; returns the script as stored"),
            @ApiResponse(responseCode = "400", description = "Invalid script", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to edit the script", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such script", content = @Content),
            @ApiResponse(responseCode = "409", description = "Saved by someone else since it was loaded", content = @Content)
    })
    public ResponseEntity<ScriptDocument> updateScriptDocument(
            @PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId,
            @RequestBody ScriptDocument document) {
        return ResponseEntity.ok(scriptService.updateScriptDocument(scriptId, document));
    }

    @RequestMapping(value = "/{scriptId}/copy", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Copies a script under a new name, owned by the caller", summary = "Copy a script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Copied; returns the new script"),
            @ApiResponse(responseCode = "400", description = "Name missing or too long", content = @Content),
            @ApiResponse(responseCode = "403", description = "Needs CREATE_SCRIPT", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such script", content = @Content)
    })
    public ResponseEntity<ScriptSummary> copyScript(
            @PathVariable @Parameter(description = "Script ID to copy", required = true) Integer scriptId,
            @RequestBody CopyRequest request) {
        ScriptSummary copy = scriptService.copyScript(scriptId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v2/scripts/{id}/steps").buildAndExpand(copy.id()).toUri();
        return ResponseEntity.created(location).body(copy);
    }

    @RequestMapping(value = "/{scriptId}/steps/{stepUuid}/response", method = RequestMethod.GET,
            produces = { MediaType.TEXT_PLAIN_VALUE })
    @Operation(description = "Returns the response recorded for a step, as stored", summary = "Get a step's recorded response")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The recorded response"),
            @ApiResponse(responseCode = "404", description = "No such script or step, or no recorded response", content = @Content)
    })
    public ResponseEntity<String> getStepResponse(
            @PathVariable @Parameter(description = "Script ID", required = true) Integer scriptId,
            @PathVariable @Parameter(description = "Step uuid", required = true) String stepUuid) {
        return ResponseEntity.ok(scriptService.getStepResponse(scriptId, stepUuid));
    }

    // Operations on an unsaved step list; nothing is saved

    @RequestMapping(value = "/steps/search", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Finds steps whose chosen parts match the query (* and ? are wildcards; the whole value must "
            + "match)", summary = "Search a step list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns every match"),
            @ApiResponse(responseCode = "400", description = "No query, no sections, or an unknown section", content = @Content)
    })
    public ResponseEntity<List<StepMatch>> searchSteps(@RequestBody StepSearchRequest request) {
        return ResponseEntity.ok(scriptDraftService.search(request));
    }

    @RequestMapping(value = "/steps/replace", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Replaces the matching keys or values in the step list, or only in the steps listed in uuids, "
            + "and returns the updated list", summary = "Replace in a step list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the updated steps and how many changed"),
            @ApiResponse(responseCode = "400", description = "Invalid query, sections, replacement or mode", content = @Content)
    })
    public ResponseEntity<DraftSteps> replaceInSteps(@RequestBody StepReplaceRequest request) {
        return ResponseEntity.ok(scriptDraftService.replace(request));
    }

    @RequestMapping(value = "/steps/apply-filters", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Applies script filters, in order, to the step list and returns the result. To filter a saved "
            + "script in place, use POST /v2/filters/apply-filters/{scriptId}", summary = "Apply filters to a step list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the filtered steps and how many changed"),
            @ApiResponse(responseCode = "400", description = "No filters, or an unknown filter id", content = @Content)
    })
    public ResponseEntity<DraftSteps> applyFiltersToSteps(@RequestBody ApplyFiltersRequest request) {
        return ResponseEntity.ok(scriptDraftService.applyFilters(request));
    }

    @RequestMapping(value = "/steps/validate", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Checks the step list as the script editor's Validate dialog does: estimated duration, "
            + "best practices and variable usage", summary = "Validate a step list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the checks")
    })
    public ResponseEntity<ScriptValidation> validateSteps(@RequestBody ValidateStepsRequest request) {
        return ResponseEntity.ok(scriptDraftService.validate(request));
    }

    @RequestMapping(value = "/logic/test", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Runs a logic step script against the given variables, request and response, in a sandbox "
            + "with no Java access and a 5 second limit, and returns what it printed", summary = "Test a logic step")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the output; timedOut is true if the script did not finish"),
            @ApiResponse(responseCode = "400", description = "No script, or a script over 64 KB", content = @Content),
            @ApiResponse(responseCode = "403", description = "Needs EDIT_SCRIPT or ownership of scriptId", content = @Content),
            @ApiResponse(responseCode = "429", description = "Too many tests are running", content = @Content)
    })
    public ResponseEntity<LogicTestResult> testLogicStep(@RequestBody LogicTestRequest request) {
        return ResponseEntity.ok(scriptDraftService.testLogic(request));
    }
}
