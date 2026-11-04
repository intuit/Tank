/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.filters;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.filters.models.ApplyFiltersRequest;
import com.intuit.tank.filters.models.FilterGroupContainer;
import com.intuit.tank.filters.models.FilterGroupDetailTO;
import com.intuit.tank.filters.models.FilterGroupTO;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;
import com.intuit.tank.filters.models.FilterContainer;
import com.intuit.tank.filters.models.FilterTO;

public interface FilterServiceV2 {

    /**
     * Test method to test if the service is up.
     *
     * @return non-null String value.
     */
    public String ping();

    /**
     * Returns a specific filter
     *
     * @param filterId
     *          filter id for the filter
     *
     * @throws GenericServiceResourceNotFoundException
     *          if there are errors returning the filter
     *
     * @return list of filters
     */
    public FilterTO getFilter(Integer filterId);

    /**
     * Returns a specific filter group
     *
     * @param filterGroupId
     *          filter group id for the filter group
     *
     * @throws GenericServiceResourceNotFoundException
     *          if there are errors returning list of all filters
     *
     * @return list of filters
     */
    public FilterGroupDetailTO getFilterGroup(Integer filterGroupId);

    /**
     * Returns all filters
     *
     * @throws GenericServiceCreateOrUpdateException
     *          if there are errors returning list of all filters
     *
     * @return list of filters
     */
    public FilterContainer getFilters();

    /**
     * Creates a new filter or updates the filter identified by the request ID.
     *
     * @param request complete filter JSON payload
     * @return the persisted filter
     */
    public FilterTO createOrUpdateFilter(FilterTO request);

    /**
     * Creates a filter owned by the caller. Any ID or creator in the request is ignored.
     *
     * @param request the filter
     * @return the new filter
     */
    public FilterTO createFilter(FilterTO request);

    /**
     * Replaces a filter's settings, conditions and actions. Needs {@code EDIT_FILTER} or ownership; the owner is
     * unchanged.
     *
     * @param filterId the filter to update
     * @param request  the filter, with the {@code modified} time from the last GET
     * @throws GenericServiceResourceNotFoundException if there is no such filter
     * @return the saved filter
     */
    public FilterTO updateFilter(Integer filterId, FilterTO request);

    /**
     * Copies a filter, with its conditions and actions, under a new name owned by the caller.
     *
     * @param filterId the filter to copy
     * @param request  the name of the copy
     * @return the new filter
     */
    public FilterTO copyFilter(Integer filterId, CopyRequest request);

    /**
     * Creates a filter group owned by the caller from a name, product and member filter IDs.
     *
     * @param request the group; {@code filterIds} must all exist
     * @return the new group with its filters
     */
    public FilterGroupDetailTO createFilterGroup(FilterGroupTO request);

    /**
     * Replaces a filter group's name, product and members. Needs {@code EDIT_FILTER} or ownership; the owner is
     * unchanged.
     *
     * @param filterGroupId the group to update
     * @param request       the group, with the {@code modified} time from the last GET
     * @throws GenericServiceResourceNotFoundException if there is no such group
     * @return the saved group with its filters
     */
    public FilterGroupDetailTO updateFilterGroup(Integer filterGroupId, FilterGroupTO request);

    /**
     * Copies a filter group under a new name owned by the caller. The copy holds the same filters.
     *
     * @param filterGroupId the group to copy
     * @param request       the name of the copy
     * @return the new group with its filters
     */
    public FilterGroupDetailTO copyFilterGroup(Integer filterGroupId, CopyRequest request);

    /**
     * Gets the list of filter groups
     *
     * @throws GenericServiceCreateOrUpdateException
     *        if there are errors returning list of all filter groups
     *
     * @return list of filter groups
     */
    public FilterGroupContainer getFilterGroups();

    /**
     * Applies filters to an existing script
     *
     * @param request
     *          filter request JSON payload
     *
     * @param scriptId
     *          scriptId of script to apply filters to
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there are errors applying filter to script
     *
     * @return 201 (created) status code and "Filters applied" string response if successful
     */
    public String applyFilters(Integer scriptId, ApplyFiltersRequest request);


    /**
     * Deletes a specific filter, first removing it from every filter group that holds it
     *
     * @param filterId Filter ID
     *
     * @throws GenericServiceDeleteException
     *          if there are errors deleting filter
     *
     * @return 204 No Content or error string if filter does not exist
     */
    public String deleteFilter(Integer filterId);


    /**
     * Deletes a specific filter group
     *
     * @param filterGroupId Filter Group ID
     *
     * @throws GenericServiceDeleteException
     *         if there are errors deleting filter group
     *
     * @return 204 No Content or error string if filter group does not exist
     */
    public String deleteFilterGroup(Integer filterGroupId);

}
