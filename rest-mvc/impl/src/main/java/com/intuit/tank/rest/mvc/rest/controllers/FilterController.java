/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.filters.models.ApplyFiltersRequest;
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.filters.models.FilterGroupContainer;
import com.intuit.tank.filters.models.FilterContainer;
import com.intuit.tank.rest.mvc.rest.services.filters.FilterServiceV2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping(value = "/v2/filters", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Filters")
public class FilterController {

    @Resource
    private FilterServiceV2 filterService;

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = { MediaType.TEXT_PLAIN_VALUE } )
    @Operation(description = "Pings filter service", summary = "Check if filter service is up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filter Service is up", content = @Content)
    })
    public ResponseEntity<String> ping() {
        return new ResponseEntity<String>(filterService.ping(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns list of all filter descriptions", summary = "Get all filter descriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all filter descriptions"),
            @ApiResponse(responseCode = "404", description = "All filter descriptions could not be found", content = @Content)
    })
    public ResponseEntity<FilterContainer> getFilters() {
        return new ResponseEntity<>(filterService.getFilters(), HttpStatus.OK);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @Operation(description = "Returns list of all filter group descriptions", summary = "Get all filter group descriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found all filter group descriptions"),
            @ApiResponse(responseCode = "404", description = "All filter group descriptions could not be found", content = @Content)
    })
    public ResponseEntity<FilterGroupContainer> getFilterGroups() {
        return new ResponseEntity<>(filterService.getFilterGroups(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{filterId}", method = RequestMethod.GET)
    @Operation(description = "Returns specified filter description by filter id", summary = "Get a specific filter description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found filter"),
            @ApiResponse(responseCode = "404", description = "Filter could not be found", content = @Content)
    })
    public ResponseEntity<FilterTO> getFilter(@PathVariable @Parameter(description = "The filter ID", required = true) Integer filterId) {
        return new ResponseEntity<>(filterService.getFilter(filterId), HttpStatus.OK);
    }

    @RequestMapping(value = "/groups/{filterGroupId}", method = RequestMethod.GET)
    @Operation(description = "Returns specified filter group description by filter group id", summary = "Get a specific filter group description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found filter group"),
            @ApiResponse(responseCode = "404", description = "Filter group could not be found", content = @Content)
    })
    public ResponseEntity<FilterGroupTO> getFilterGroup(@PathVariable @Parameter(description = "The filter group ID", required = true) Integer filterGroupId) {
        return new ResponseEntity<>(filterService.getFilterGroup(filterGroupId), HttpStatus.OK);
    }

    @RequestMapping(value = "/apply-filters/{scriptId}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.TEXT_PLAIN_VALUE })
    @Operation(description = "Given an apply filters request payload with list of filters and filter groups to apply, " +
                             "returns success message if filters successfully applied to an existing script", summary = "Apply filters to an existing script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully applied filters", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<String> applyFilters(@PathVariable @Parameter(description = "The script ID", required = true) Integer scriptId,
                                               @RequestBody @Parameter(description = "apply filter request", required = true) ApplyFiltersRequest request){
        String response = filterService.applyFilters(scriptId, request);
        if (response != null){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>("Bad JSON request", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{filterId}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Deletes a filter by filter ID", summary = "Delete a specific filter")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No content (filter delete successful)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
    public ResponseEntity<String> deleteFilter(
            @PathVariable @Parameter(description = "The filter ID", required = true) Integer filterId) {
        String response = filterService.deleteFilter(filterId);
        if (Objects.equals(response, "")) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/groups/{filterGroupId}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_PLAIN_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Delete a filter group by filter group ID", summary = "Delete a specific filter group")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No content (filter group delete successful)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
    public ResponseEntity<String> deleteFilterGroup(
            @PathVariable @Parameter(description = "The filter group ID", required = true) Integer filterGroupId) {
        String response = filterService.deleteFilterGroup(filterGroupId);
        if (Objects.equals(response, "")) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
