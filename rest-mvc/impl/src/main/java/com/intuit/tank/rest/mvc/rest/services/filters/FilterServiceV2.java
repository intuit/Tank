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
import com.intuit.tank.filters.models.FilterContainer;
import com.intuit.tank.filters.models.FilterTO;
import com.intuit.tank.filters.models.FilterGroupTO;

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
    public FilterGroupTO getFilterGroup(Integer filterGroupId);

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
     * Deletes a specific filter
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
