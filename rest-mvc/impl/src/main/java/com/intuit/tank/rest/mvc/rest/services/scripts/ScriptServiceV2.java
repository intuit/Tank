/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.script.models.ExternalScriptContainer;
import com.intuit.tank.script.models.ExternalScriptTO;
import com.intuit.tank.script.models.ScriptDescriptionContainer;
import com.intuit.tank.script.models.ScriptDescription;
import com.intuit.tank.script.models.ScriptTO;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Map;

public interface ScriptServiceV2 {

    /**
     * Test method to test if the service is up.
     *
     * @return non-null String value.
     */
    public String ping();

    /**
     * Creates a new Tank script through script file upload or creates a copy from existing script
     *
     * @param name
     *            name to assign to new or updated Tank script
     *
     * @param id
     *            existing script's scriptID to overwrite with new script content
     *
     * @param recording
     *           set for uploading Tank Proxy recording file (defaults to uploading Tank XML script if not passed)
     *
     * @param copy
     *           set for copying from an existing script
     *
     * @param sourceId
     *           the scriptId of the source script to copy from
     *
     * @param contentEncoding
     *            content encoding of file (checks for gzip file)
     *
     * @param file
     *            script XML file to be uploaded
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there are errors uploading script
     */
    public Map<String, String> createScript(String name, Integer id,
                                            String recording, String copy,
                                            Integer sourceId, String contentEncoding,
                                            MultipartFile file) throws IOException;

    /**
     * Retrieves a specific script description by script ID
     * @param scriptId
     *      script ID corresponding to script
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are any errors returning script description
     *
     * @return script description JSON response
     */
    public ScriptDescription getScript(Integer scriptId);

    /**
     * Gets all scripts
     *
     * @throws GenericServiceResourceNotFoundException
     *        if there are any errors returning all scripts
     *
     * @return list of all scripts
     */
    public ScriptDescriptionContainer getScripts();

    /**
     * Gets all script names along with scriptId
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error returning all script names
     *
     * @return map of script < scriptName, scriptId > JSON response
     */
    public Map<Integer, String> getAllScriptNames();

    /**
     * Downloads a script's Tank XML file associated with scriptID
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors downloading the script
     *
     * @param scriptId
     *            scriptID for script to be downloaded
     *
     * @return script Tank XML file
     */
    public Map<String, StreamingResponseBody> downloadScript(Integer scriptId);

    /**
     * Downloads a script's Harness XML file associated with scriptID
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors downloading script harness
     *
     * @param scriptId
     *            scriptID for script harness to be downloaded
     *
     * @return script harness XML file
     */
    public Map<String, StreamingResponseBody> downloadHarnessScript(Integer scriptId);

    /**
     * Deletes a script associated with scriptID
     *
     * @throws GenericServiceDeleteException
     *         if there are errors deleting the script
     *
     * @param scriptId
     *            scriptID for script to be deleted
     *
     * @return string confirmation of script deletion
     */
    public String deleteScript(Integer scriptId);


    // External Scripts

    /**
     * Get all external script descriptions
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors returning all external scripts
     *
     * @return list of external script descriptions
     */
    public ExternalScriptContainer getExternalScripts();


    /**
     * Gets a specific external script description by ID
     *
     * @param externalScriptId
     *         id for external script
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors returning the external script description
     *
     * @return External Script description JSON response
     *
     */
    public ExternalScriptTO getExternalScript(Integer externalScriptId);

    /**
     * Creates a new external script
     *
     * @param ExternalScriptRequest External Script JSON request payload
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there are any errors creating the external script
     *
     * @return Exteranl Script JSON response payload and URL in Location header
     */
    public ExternalScriptTO createExternalScript(ExternalScriptTO ExternalScriptRequest);


    /**
     * Downloads an external script's Tank XML file corresponding with external scriptsID
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors downloading external script
     *
     * @param externalScriptId
     *            external scriptID for external script to be downloaded
     *
     * @return external script file
     */
    public Map<String, StreamingResponseBody> downloadExternalScript(Integer externalScriptId);


    /**
     * Deletes an External Script
     *
     * @throws GenericServiceDeleteException
     *         if there are any errors deleting the external script
     *
     * @param externalScriptId
     *            ID for external script
     *
     * @return string confirmation of deletion
     */
    public String deleteExternalScript(Integer externalScriptId);
}
