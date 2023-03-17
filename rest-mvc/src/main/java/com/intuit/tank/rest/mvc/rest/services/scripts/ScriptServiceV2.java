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
import com.intuit.tank.rest.mvc.rest.models.scripts.ExternalScriptContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ExternalScriptTO;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptDescriptionContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptTO;


public interface ScriptServiceV2 {

    /**
     * Test method to test if the service is up.
     *
     * @return non-null String value.
     */
    public String ping();

    /**
     * Creates a new script
     *
     * @param scriptTo
     *       script JSON request payload
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there are any errors creating the script
     *
     * @return script JSON response with (201 Created) and URL in Location header
     */
    public ScriptTO createScript(ScriptTO scriptTo);

    /**
     * Retrieves a specific script by script ID
     * @param scriptId
     *      script ID corresponding to script
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are any errors returning script
     *
     * @return script JSON response
     */
    public ScriptTO getScript(Integer scriptId);

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
     * Deletes a script associated with scriptID
     *
     * @throws GenericServiceDeleteException
     *         if there are errors deleting the script
     *
     * @param scriptID
     *            scriptID for script to be deleted
     *
     * @return string confirmation of script deletion
     */
    public String deleteScript(Integer scriptID);


    // External Scripts

    /**
     * Get all external scripts
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors returning all external scripts
     *
     * @return list of external scripts
     */
    public ExternalScriptContainer getExternalScripts();


    /**
     * Gets a specific external script by ID
     *
     * @param externalScriptId
     *         id for external script
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors returning the external script
     *
     * @return External Script JSON response
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
