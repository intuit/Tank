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
import com.intuit.tank.filters.models.FilterGroupDetailTO;
import com.intuit.tank.filters.models.FilterGroupContainer;
import com.intuit.tank.filters.models.FilterContainer;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;
import com.intuit.tank.rest.mvc.rest.services.filters.FilterServiceV2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.Resource;
import java.net.URI;
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

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creates a new filter, or updates the filter when the payload contains an existing ID. "
            + "New clients should use PUT /v2/filters/{filterId} to update, which checks for concurrent changes",
            summary = "Create or update a filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully saved filter"),
            @ApiResponse(responseCode = "200", description = "Successfully updated filter"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    public ResponseEntity<FilterTO> createOrUpdateFilter(
            @RequestBody @Parameter(description = "Complete filter JSON payload", required = true) FilterTO filter) {
        FilterTO savedFilter = filterService.createOrUpdateFilter(filter);
        if (filter.getId() != null && filter.getId() > 0) {
            return new ResponseEntity<>(savedFilter, HttpStatus.OK);
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .scheme("https")
                .path("/{id}")
                .buildAndExpand(savedFilter.getId())
                .toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return new ResponseEntity<>(savedFilter, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{filterId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Replaces a filter's settings, conditions and actions. Send the modified time from the "
            + "last GET; the owner is unchanged", summary = "Update a filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved; returns the filter"),
            @ApiResponse(responseCode = "400", description = "Invalid filter or modified missing", content = @Content),
            @ApiResponse(responseCode = "403", description = "Needs EDIT_FILTER or ownership", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such filter", content = @Content),
            @ApiResponse(responseCode = "409", description = "Changed by someone else since it was loaded", content = @Content)
    })
    public ResponseEntity<FilterTO> updateFilter(
            @PathVariable @Parameter(description = "The filter ID", required = true) Integer filterId,
            @RequestBody @Parameter(description = "Complete filter JSON payload", required = true) FilterTO filter) {
        return ResponseEntity.ok(filterService.updateFilter(filterId, filter));
    }

    @RequestMapping(value = "/{filterId}/copy", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Copies a filter, with its conditions and actions, under a new name owned by the caller",
            summary = "Copy a filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Copied; returns the new filter"),
            @ApiResponse(responseCode = "400", description = "Name missing or too long", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to create filters", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such filter", content = @Content)
    })
    public ResponseEntity<FilterTO> copyFilter(
            @PathVariable @Parameter(description = "The filter ID to copy", required = true) Integer filterId,
            @RequestBody CopyRequest request) {
        FilterTO copy = filterService.copyFilter(filterId, request);
        return ResponseEntity.created(location("/v2/filters/{id}", copy.getId())).body(copy);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creates a filter group owned by the caller from a name, product and member filter IDs",
            summary = "Create a filter group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created; returns the group with its filters"),
            @ApiResponse(responseCode = "400", description = "Name missing or unknown filter IDs", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to create filters", content = @Content)
    })
    public ResponseEntity<FilterGroupDetailTO> createFilterGroup(
            @RequestBody @Parameter(description = "The filter group", required = true) FilterGroupTO group) {
        FilterGroupDetailTO saved = filterService.createFilterGroup(group);
        return ResponseEntity.created(location("/v2/filters/groups/{id}", saved.getId())).body(saved);
    }

    @RequestMapping(value = "/groups/{filterGroupId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Replaces a filter group's name, product and members. Send the modified time from the "
            + "last GET; the owner is unchanged", summary = "Update a filter group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved; returns the group with its filters"),
            @ApiResponse(responseCode = "400", description = "Name missing, unknown filter IDs or modified missing", content = @Content),
            @ApiResponse(responseCode = "403", description = "Needs EDIT_FILTER or ownership", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such filter group", content = @Content),
            @ApiResponse(responseCode = "409", description = "Changed by someone else since it was loaded", content = @Content)
    })
    public ResponseEntity<FilterGroupDetailTO> updateFilterGroup(
            @PathVariable @Parameter(description = "The filter group ID", required = true) Integer filterGroupId,
            @RequestBody @Parameter(description = "The filter group", required = true) FilterGroupTO group) {
        return ResponseEntity.ok(filterService.updateFilterGroup(filterGroupId, group));
    }

    @RequestMapping(value = "/groups/{filterGroupId}/copy", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Copies a filter group, holding the same filters, under a new name owned by the caller",
            summary = "Copy a filter group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Copied; returns the new group with its filters"),
            @ApiResponse(responseCode = "400", description = "Name missing or too long", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to create filters", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such filter group", content = @Content)
    })
    public ResponseEntity<FilterGroupDetailTO> copyFilterGroup(
            @PathVariable @Parameter(description = "The filter group ID to copy", required = true) Integer filterGroupId,
            @RequestBody CopyRequest request) {
        FilterGroupDetailTO copy = filterService.copyFilterGroup(filterGroupId, request);
        return ResponseEntity.created(location("/v2/filters/groups/{id}", copy.getId())).body(copy);
    }

    private static URI location(String path, Integer id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).buildAndExpand(id).toUri();
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @Operation(description = "Returns all filter groups with their member filter IDs",
            summary = "Get all filter groups")
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
    @Operation(description = "Returns the specified filter group with complete member filter definitions",
            summary = "Get a filter group with its filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found filter group"),
            @ApiResponse(responseCode = "404", description = "Filter group could not be found", content = @Content)
    })
    public ResponseEntity<FilterGroupDetailTO> getFilterGroup(@PathVariable @Parameter(description = "The filter group ID", required = true) Integer filterGroupId) {
        return new ResponseEntity<>(filterService.getFilterGroup(filterGroupId), HttpStatus.OK);
    }

    @RequestMapping(value = "/apply-filters/{scriptId}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.TEXT_PLAIN_VALUE })
    @Operation(description = "Given an apply filters request payload with list of filters and filter groups to apply, " +
                             "returns success message if filters successfully applied to an existing script", summary = "Apply filters to an existing script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully applied filters"),
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
