/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.datafiles;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;

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
     * Downloads a specific datafile by datafile ID
     *
     * @param datafileId
     *      datafile ID corresponding to datafile
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are any errors downloading datafile
     *
     * @return datafile file
     */
    public Map<String, StreamingResponseBody> downloadDatafile(Integer datafileId);

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
     * Upload datafile to Tank
     *
     * @param datafileId
     *            existing datafile's datafileId to overwrite with new datafile
     *
     * @param contentEncoding
     *            content encoding of datafile (checks for gzip file)
     *
     * @param file
     *            datafile to be uploaded
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there are errors uploading datafile
     *
     * @return datafileId with upload status JSON payload
     */
    public Map<String, String> uploadDatafile(Integer datafileId, String contentEncoding, MultipartFile file) throws IOException;

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
