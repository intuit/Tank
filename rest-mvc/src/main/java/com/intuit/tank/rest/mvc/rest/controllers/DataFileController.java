/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;
import com.intuit.tank.rest.mvc.rest.services.datafiles.DataFileServiceV2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/v2/datafiles", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Datafiles")
public class DataFileController {
    @Resource
    private DataFileServiceV2 dataFileService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings datafile service", summary = "Checks if datafile service is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datafile Service is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>(dataFileService.ping(), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all datafiles"),
            @ApiResponse(responseCode = "404", description = "All datafiles could not be found", content = @Content)
    })
    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns all datafile descriptions", summary = "Get all datafile descriptions")
    public ResponseEntity<DataFileDescriptorContainer> getDatafiles() {
        return new ResponseEntity<>(dataFileService.getDatafiles(), HttpStatus.OK);
    }

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @Operation(description = "Returns all datafile names with corresponding datafile IDs", summary = "Get all datafile names with datafile IDs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all datafile names with IDs", content = @Content),
            @ApiResponse(responseCode = "404", description = "All datafile names with IDs could not be found", content = @Content)
    })
    public ResponseEntity<Map<Integer, String>> getAllDatafileNames() {
        return new ResponseEntity<>(dataFileService.getAllDatafileNames(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{datafileId}", method = RequestMethod.GET)
    @Operation(description = "Returns a specific datafile description by datafile ID", summary = "Get a specific datafile description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found datafile"),
            @ApiResponse(responseCode = "404", description = "datafile could not be found", content = @Content)
    })
    public ResponseEntity<DataFileDescriptor> getDatafile(@PathVariable @Parameter(description = "Datafile ID", required = true) Integer datafileId) {
        DataFileDescriptor datafile = dataFileService.getDatafile(datafileId);
        if (datafile != null){
            return new ResponseEntity<>(datafile, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/content", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Returns datafile content by datafile ID, with optional offset and lines parameters to adjust number of total lines returned", summary = "Get datafile content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found datafile content", content = @Content),
            @ApiResponse(responseCode = "404", description = "Datafile content could not be found", content = @Content)
    })
    public ResponseEntity<String> getDatafileContent(@RequestParam(required = true) @Parameter(description = "Datafile ID", required = true) Integer id,
                                                                    @RequestParam(required = false) @Parameter(description = "Starting line offset (0 by default)", required = false) Integer offset,
                                                                    @RequestParam(required = false) @Parameter(description = "Number of lines returned from offset (all lines by default)", required = false) Integer lines) throws IOException {
        StreamingResponseBody content = dataFileService.getDatafileContent(id, offset, lines);
        if (content != null) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            content.writeTo(out);
            return new ResponseEntity<>(out.toString(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/download/{datafileId}", method = RequestMethod.GET)
    @Operation(description = "Downloads a datafile with the corresponding datafile ID", summary = "Download a datafile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded the datafile", content = @Content),
            @ApiResponse(responseCode = "404", description = "Datafile could not be found", content = @Content)
    })
    public ResponseEntity<StreamingResponseBody> downloadDatafile(@PathVariable @Parameter(description = "Datafile ID", required = true) Integer datafileId) throws IOException {
        Map<String, StreamingResponseBody> response = dataFileService.downloadDatafile(datafileId);
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
    @Operation(description = "Uploads a datafile to Tank (supports gzip files) \n\n " +
            "Examples: \n\n" +
            "        curl -v -X POST -H 'Content-Type: multipart/form-data' -F \"file=@<filename>\" 'https://{tank-base-url}/v2/datafiles/upload'\n\n" +
            "\n\n" +
            "        curl -v -X POST -H 'Content-Type: multipart/form-data' -F \"file=@<filename>\" 'https://{tank-base-url}/v2/datafiles/upload?id=<datafileId>'\n\n" +
            "\n\n" +
            "        gzip <filename>\n\n" +
            "        curl -v -X POST -H 'Content-Type: multipart/form-data' -H 'Content-Encoding: gzip' -F \"file=@<filename>.gz\" 'https://{tank-base-url}/v2/datafiles/upload'\n\n" +
            "Notes: \n\n " +
            " - Datafile id is an optional parameter \n\n" +
            " - Passing in an existing Tank Datafile ID will overwrite the existing datafile in Tank, but will otherwise create a new datafile", summary = "Upload datafile to Tank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully uploaded datafile", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datafile could not be uploaded", content = @Content)
    })
    public ResponseEntity<Map<String, String>> uploadDatafile(@RequestHeader(value = HttpHeaders.CONTENT_ENCODING, required = false) @Parameter(description = "Content-Encoding", required = false) String contentEncoding,
                                                            @RequestParam(required = false) @Parameter(description = "Existing Datafile ID (optional)", required = false) Integer id,
                                                            @RequestParam(value = "file", required = true) @Parameter(schema = @Schema(type = "string", format = "binary", description = "Datafile file")) MultipartFile file) throws IOException{
        Map<String, String> response = dataFileService.uploadDatafile(id, contentEncoding, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{datafileID}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Deletes a specific datafile by datafile ID", summary = "Delete a datafile")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No content (datafile delete successful)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Datafile not found", content = @Content) })
    public ResponseEntity<String> deleteDatafile(@PathVariable @Parameter(description = "Datafile ID", required = true) Integer datafileID) {
        String response = dataFileService.deleteDatafile(datafileID);
        if (Objects.equals(response, "")) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
