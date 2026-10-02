/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.datafiles;

import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.DataFileBatchResult;
import com.intuit.tank.rest.mvc.rest.models.DataFilePreview;
import com.intuit.tank.rest.mvc.rest.models.DataFileSummary;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.datafiles.models.DataFileDescriptorContainer;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
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
     * Gets all datafile names along with datafileId
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error returning all datafile names
     *
     * @return map of datafile < datafileName, datafileId > JSON response
     */
    public Map<Integer, String> getAllDatafileNames();

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


    /**
     * Lines of a data file, read as {@code /content} reads them, with the file's total line count.
     */
    record ContentPage(String text, int totalLines) {
    }

    /**
     * Reads a data file as {@link #getDatafileContent} does: {@code numLines} limits CSV files only.
     *
     * @return null when there is no such data file
     */
    ContentPage readDatafileContent(Integer datafileId, Integer offset, Integer numLines);

    /**
     * Lists data files one page at a time, for the data files table.
     *
     * @param sort  {@code id}, {@code name}, {@code owner}, {@code created} or {@code modified}, optionally
     *              followed by {@code ,asc} or {@code ,desc}; default {@code modified,desc}
     * @param owner only data files owned by this user
     * @param q     text that the name or comments must contain (case-insensitive)
     */
    PageResponse<DataFileSummary> listDatafiles(Integer page, Integer size, String sort, String owner, String q);

    /**
     * Creates a data file from each uploaded {@code .csv}, {@code .txt} or {@code .xml} file and from each
     * such entry of uploaded {@code .zip} archives. Needs {@code CREATE_DATAFILE}.
     */
    DataFileBatchResult uploadDatafiles(List<MultipartFile> files);

    /**
     * A page of any data file's lines, with its total line count.
     *
     * @param lines at most {@value DataFileServiceV2Impl#MAX_PREVIEW_LINES}; default
     *              {@value DataFileServiceV2Impl#DEFAULT_PREVIEW_LINES}
     */
    DataFilePreview previewDatafile(Integer datafileId, Integer offset, Integer lines);

    /**
     * Deletes several data files. Nothing is deleted unless the caller may delete all of the existing ones.
     */
    BulkDeleteResult deleteDatafiles(List<Integer> datafileIds);
}
