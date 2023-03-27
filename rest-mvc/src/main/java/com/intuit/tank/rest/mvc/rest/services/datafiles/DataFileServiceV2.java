/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.datafiles;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface DataFileServiceV2 {

    /**
     * Test method to test if the service is up.
     *
     * @return non-null String value.
     */
    public String ping();

    /**
     * Retrieves a specific datafile description by datafile ID
     * @param datafileId
     *      datafile ID corresponding to datafile
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are any errors returning datafile
     *
     * @return datafile description JSON response
     */
    public DataFileDescriptor getDatafile(Integer datafileId);

    /**
     * Returns datafile content
     * @param datafileId
     *      datafile ID corresponding to datafile
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are any errors returning datafile
     *
     * @return datafile streaming output
     */
    public StreamingResponseBody getDatafileContent(Integer datafileId, Integer offset, Integer numLines);

    /**
     * Gets all datafile descriptions
     *
     * @throws GenericServiceResourceNotFoundException
     *        if there are any errors returning all datafiles
     *
     * @return list of all datafile descriptions
     */
    public DataFileDescriptorContainer getDatafiles();

    /**
     * Deletes a datafile associated with datafile ID
     *
     * @throws GenericServiceDeleteException
     *         if there are errors deleting the datafile
     *
     * @param datafileId
     *            datafileId for datafile to be deleted
     *
     * @return string confirmation of datafile deletion
     */
    public String deleteDatafile(Integer datafileId);

}
