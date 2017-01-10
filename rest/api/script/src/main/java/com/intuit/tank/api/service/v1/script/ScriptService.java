package com.intuit.tank.api.service.v1.script;

/*
 * #%L
 * Script Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.intuit.tank.api.model.v1.script.ExternalScriptContainer;
import com.intuit.tank.api.model.v1.script.ExternalScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptDescriptionContainer;
import com.intuit.tank.api.model.v1.script.ScriptFilterRequest;
import com.intuit.tank.api.model.v1.script.ScriptStepContainer;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@Path(ScriptService.SERVICE_RELATIVE_PATH)
public interface ScriptService {

    public static final String SERVICE_RELATIVE_PATH = "/v1/script-service";

    public static final String METHOD_PING = "/ping";

    public static final String METHOD_CONVERT_SCRIPT = "/convert/script";
    public static final String METHOD_SCRIPT_DESCRIPTION = "/script/description";
    public static final String METHOD_SCRIPT_STEPS = "/script/steps";
    public static final String METHOD_SCRIPT = "/script";
    public static final String METHOD_FILTER = "/filter";
    public static final String METHOD_SCRIPT_UPDATE = "/script/update";
    public static final String METHOD_SCRIPT_FILTER = "/script/filter";
    public static final String METHOD_EXTERNAL_SCRIPT = "/external/script";
    public static final String METHOD_EXTERNAL_SCRIPTS = "/external/scripts";
    public static final String METHOD_SCRIPT_DOWNLOAD = "/download/script";
    public static final String METHOD_HARNESS_DOWNLOAD = "/download/harness/script";

    /**
     * Test method to test if the service is up.
     * 
     * @return non-null String value.
     */
    @Path(ScriptService.METHOD_PING)
    @Produces({ MediaType.TEXT_PLAIN })
    @GET
    @Nonnull
    public String ping();

    /**
     * Convert a script from a recorder to Tank steps.
     * 
     * @param formData
     *            FormDataMultiPart should contain a ScriptUploadRequest with the formKey of xmlString and an
     *            octetStream of the recorded script
     * @return created no content response with location field set to url of newly created object
     */
    @Path(ScriptService.METHOD_CONVERT_SCRIPT)
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @POST
    @Nonnull
    @TypeHint(TypeHint.NO_CONTENT.class)
    @StatusCodes({ @ResponseCode(code = 201, condition = "Normal Exit Code") })
    public Response convertScript(@Nonnull FormDataMultiPart formData);

    /**
     * Gets the harness representation of a script.
     * 
     * @param scriptId
     *            the id of the script to fetch
     * @return the script in xml form
     */
    @Path(ScriptService.METHOD_HARNESS_DOWNLOAD + "/{scriptId}")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    @GET
    @TypeHint(String.class)
    public Response downloadHarnessScript(
            @PathParam("scriptId") Integer scriptId);

    /**
     * Gets all the ScriptDescriptions in the system.
     * 
     * @return List of ScriptDescriptions
     */
    @Path(ScriptService.METHOD_SCRIPT_DESCRIPTION)
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Nonnull
    @TypeHint(ScriptDescriptionContainer.class)
    public Response getScriptDescriptions();

    /*
     * 
     */
    /**
     * Gets a ScriptStepContainer for the given script.
     * 
     * @param id
     *            the id of the script to get the steps for
     * @param start
     *            the starting index of the steps.
     * @param numSteps
     *            the number of steps to return
     * @return ScriptStepContainer for the given script
     */
    @Path(ScriptService.METHOD_SCRIPT_STEPS + "/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Nonnull
    @TypeHint(ScriptStepContainer.class)
    public Response getScriptSteps(@Nonnull @PathParam("id") Integer id,
            @DefaultValue("0") @QueryParam("start") int start, @QueryParam("numSteps") @DefaultValue("-1") int numSteps);

    /**
     * Gets a single Script Description.
     * 
     * @param id
     *            the id of the script description to fetch.
     * @return the ScriptDescription
     */
    @Path(ScriptService.METHOD_SCRIPT_DESCRIPTION + "/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Nullable
    @TypeHint(ScriptDescription.class)
    public Response getScriptDescription(@Nonnull @PathParam("id") Integer id);

    /**
     * Gets a single ScriptTO which will include the steps.
     * 
     * @param id
     *            the id of the script to fetch
     * @return the ScriptTO
     */
    @Path(ScriptService.METHOD_SCRIPT + "/{id}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @GET
    @Nullable
    @TypeHint(ScriptTO.class)
    public Response getScript(@Nonnull @PathParam("id") Integer id);

    /**
     * Gets a single ScriptTO which will include the steps as a Streamed output for download.
     * 
     * @param id
     *            the id of the script to fetch
     * @return the ScriptTO
     */
    @Path(ScriptService.METHOD_SCRIPT_DOWNLOAD + "/{id}")
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    @GET
    @Nullable
    @TypeHint(ScriptTO.class)
    public Response downloadScript(@Nonnull @PathParam("id") Integer id);

    /**
     * Deletes the script and all steps from the system.
     * 
     * @param id
     *            the id of the script to delete
     * @return http status. Response containing a status code 204(no content) if successful and 400(bad request) if id
     *         cannot be found on the system.
     */
    @Path(ScriptService.METHOD_SCRIPT + "/{id}")
    @DELETE
    @TypeHint(TypeHint.NO_CONTENT.class)
    public Response deleteScript(@Nonnull @PathParam("id") Integer id);

    /**
     * Deletes the filter group from the system.
     * 
     * @param id
     *            the id of the filter to delete
     * @return Response containing a status code 204(no content) if successful and 400(bad request) if id cannot be
     *         found on the system.
     * 
     */
    @Path(ScriptService.METHOD_FILTER + "/{id}")
    @DELETE
    @TypeHint(TypeHint.NO_CONTENT.class)
    public Response deleteFilter(@Nonnull @PathParam("id") Integer id);

    /**
     * Updates the script.
     * 
     * @param id
     *            the id of the script to update
     * @return created status code (201) with uri to new resource.
     */
    @Path(ScriptService.METHOD_SCRIPT + "/{id}")
    @PUT
    @TypeHint(TypeHint.NO_CONTENT.class)
    public Response updateScript(@Nonnull @PathParam("id") Integer id, @Nonnull ScriptTO script);

    /**
     * Updates a script in Tank xml format.
     * 
     * @param formData
     *            Formdata should contain a ScriptTO with the formKey of xmlString
     * @return Response String indicating the id of the script that was updated
     */
    @Path(ScriptService.METHOD_SCRIPT_UPDATE)
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.TEXT_PLAIN })
    @POST
    @Nonnull
    @TypeHint(String.class)
    public Response updateTankScript(@Nonnull FormDataMultiPart formData);

    /**
     * Creates a new Script from the given ScriptTO. will nullify id and reset all uuids of steps
     * 
     * @param script
     *            the script to store
     * @return created status code (201) with uri to new resource.
     */
    @Path(ScriptService.METHOD_SCRIPT)
    @POST
    @Nonnull
    @TypeHint(TypeHint.NO_CONTENT.class)
    public Response newScript(@Nonnull ScriptTO script);

    /**
     * Filters the script using the supplied filter ids.
     * 
     * @param filterRequest
     *            the filter request
     * @return created status code (201) with uri to script resource.
     */
    @Path(ScriptService.METHOD_SCRIPT_FILTER)
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @POST
    @Nonnull
    @TypeHint(TypeHint.NO_CONTENT.class)
    public Response scriptFilterRequest(@Nonnull ScriptFilterRequest filterRequest);

    /**
     * Gets a single ExternalScriptTO object.
     * 
     * @param id
     *            the id of the script to fetch
     * @return the ExternalScriptTO
     */
    @Path(ScriptService.METHOD_EXTERNAL_SCRIPT + "/{id}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @GET
    @Nonnull
    @TypeHint(ExternalScriptTO.class)
    public Response getExternalScript(@Nonnull @PathParam("id") Integer id);

    /**
     * Gets all ExternalScriptTO objects.
     * 
     * @return the ExternalScriptContainer
     */
    @Path(ScriptService.METHOD_EXTERNAL_SCRIPTS)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @GET
    @Nonnull
    @TypeHint(ExternalScriptContainer.class)
    public Response getExternalScripts();

    /**
     * Saves or updates the External Script.
     * 
     * @param script
     *            the script to store or update
     * @return created status code (201) with uri to script resource.
     */
    @Path(ScriptService.METHOD_EXTERNAL_SCRIPT)
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @POST
    @Nonnull
    @TypeHint(TypeHint.NO_CONTENT.class)
    public Response saveOrUpdateExternalScript(ExternalScriptTO script);

}
