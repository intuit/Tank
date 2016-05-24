package com.intuit.tank.api.service.v1.cloud;

/*
 * #%L
 * Cloud Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
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

import org.codehaus.enunciate.modules.jersey.ExternallyManagedLifecycle;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */

/**
 * ProjectService
 * 
 * @author dangleton
 * 
 */
@Path(CloudService.SERVICE_RELATIVE_PATH)
@ExternallyManagedLifecycle
public interface CloudService {
    public static final String SERVICE_RELATIVE_PATH = "/v1/cloud-service";

    public static final String METHOD_PING = "/ping";
    public static final String METHOD_USER_ID_FROM_RANGE = "/user-id-from-range";

    public static final String METHOD_INSTANCE_STATUS = "/instance/status";
    public static final String METHOD_JOB_STATUS = "/job/status";

    public static final String METHOD_REPORTING_START = "/reporting/start";
    public static final String METHOD_REPORTING_STOP = "/reporting/stop";
    public static final String METHOD_REPORTING_STATUS = "/reporting/status";
    public static final String METHOD_REPORTING_SUMMARY_STATUS = "/reporting/summary/status";

    public static final String METHOD_KILL_INSTANCE = "/instance/kill";
    public static final String METHOD_KILL_JOB = "/job/kill";

    public static final String METHOD_START_JOB = "/job/start";

    public static final String METHOD_STOP_INSTANCE = "/agent/stop";
    public static final String METHOD_STOP_JOB = "/job/stop";

    public static final String METHOD_PAUSE_INSTANCE = "/agent/pause";
    public static final String METHOD_PAUSE_JOB = "/job/pause";

    public static final String METHOD_RESTART_INSTANCE = "/agent/restart";
    public static final String METHOD_RESTART_JOB = "/job/restart";

    public static final String METHOD_PAUSE_RAMP_JOB = "/job/ramp/pause";
    public static final String METHOD_PAUSE_RAMP_INSTANCE = "/instance/ramp/pause";
    public static final String METHOD_RESUME_RAMP_JOB = "/job/ramp/resume";
    public static final String METHOD_RESUME_RAMP_INSTANCE = "/instance/ramp/resume";

    public static final String METHOD_GET_COST = "/costing/custom";
    public static final String METHOD_GET_COST_PREDEFINED = "/costing/predefined";

    /**
     * Test method to test if the service is up.
     * 
     * @return non-null String value.
     */
    @Path(CloudService.METHOD_PING)
    @GET
    @Nonnull
    public String ping();

    /**
     * Gets a userId form the range specified.
     * 
     * @param jobId
     *            the jobId of the range
     * @param minValue
     *            the minValue of the range
     * @param maxValue
     *            the max value of the range
     * @return the value or throw an exception if range is exhausted.
     */
    @Path(CloudService.METHOD_USER_ID_FROM_RANGE + "/{jobId}/{minValue}/{maxValue}")
    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public String userIdFromRange(@PathParam("jobId") String jobId, @PathParam("minValue") int minValue,
            @PathParam("maxValue") int maxValue);

    /**
     * 
     * @return
     */
    @Path(CloudService.METHOD_INSTANCE_STATUS + "/{instanceId}")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getVmStatus(@PathParam("instanceId") String instanceId);

    /**
     * 
     * @return one of "Complete", "Gathering", or "NoData"
     */
    @Path(CloudService.METHOD_REPORTING_SUMMARY_STATUS + "/{jobId}")
    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public Response getSummaryStatus(@PathParam("jobId") String jobId);

    /**
     * 
     * @param jobId
     * @return
     */
    @Path(CloudService.METHOD_JOB_STATUS + "/{jobId}")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getVmStatusForJob(@PathParam("jobId") String jobId);

    /**
     * 
     * @param instanceId
     * @param status
     */
    @Path(CloudService.METHOD_INSTANCE_STATUS + "/{instanceId}")
    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void setVmStatus(@Nonnull @PathParam("instanceId") String instanceId, CloudVmStatus status);

    /**
     * 
     * @param instanceId
     */
    @Path(CloudService.METHOD_KILL_INSTANCE + "/{instanceId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void killInstance(@Nonnull @PathParam("instanceId") String instanceId);

    /**
     * 
     * @param jobId
     */
    @Path(CloudService.METHOD_KILL_JOB + "/{jobId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void killJob(@Nonnull @PathParam("jobId") String jobId);

    /**
     * 
     * @return String value.
     */
    @Path(CloudService.METHOD_KILL_JOB)
    @GET
    public String killAllJobs();
    
    /**
     * Starts a job
     * 
     * @param jobId
     */
    @Path(CloudService.METHOD_START_JOB + "/{jobId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public String startJob(@Nonnull @PathParam("jobId") String jobId);

    /**
     * 
     * @param instanceIds
     */
    @Path(CloudService.METHOD_KILL_INSTANCE)
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @POST
    public void killInstances(List<String> instanceIds);

    /**
     * 
     * @return String value.
     */
    @Path(CloudService.METHOD_STOP_JOB)
    @GET
    public String stopAllJobs();
    
    /**
     * 
     * @param jobId
     */
    @Path(CloudService.METHOD_STOP_JOB + "/{jobId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void stopJob(@Nonnull @PathParam("jobId") String jobId);

    /**
     * 
     * @param instanceId
     */
    @Path(CloudService.METHOD_STOP_INSTANCE + "/{instanceId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void stopAgent(@Nonnull @PathParam("instanceId") String instanceId);

    /**
     * 
     * @param instanceIds
     */
    @Path(CloudService.METHOD_STOP_INSTANCE)
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void stopAgents(List<String> instanceIds);

    /**
     * 
     * @param jobId
     */
    @Path(CloudService.METHOD_PAUSE_JOB + "/{jobId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void pauseJob(@Nonnull @PathParam("jobId") String jobId);

    /**
     * 
     * @param instanceId
     */
    @Path(CloudService.METHOD_PAUSE_INSTANCE + "/{instanceId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void pauseAgent(@Nonnull @PathParam("instanceId") String instanceId);

    /**
     * 
     * @param group
     */
    @Path(CloudService.METHOD_PAUSE_INSTANCE)
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void pauseAgents(List<String> instanceIds);

    /**
     * 
     * @param jobId
     */
    @Path(CloudService.METHOD_RESTART_JOB + "/{jobId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void restartJob(@Nonnull @PathParam("jobId") String jobId);

    /**
     * 
     * @param instanceId
     */
    @Path(CloudService.METHOD_RESTART_INSTANCE + "/{instanceId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void restartAgent(@Nonnull @PathParam("instanceId") String instanceId);

    /**
     * 
     * @param group
     */
    @Path(CloudService.METHOD_RESTART_INSTANCE)
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void restartAgents(List<String> instanceIds);

    /**
     * 
     * @param instanceId
     */
    @Path(CloudService.METHOD_PAUSE_RAMP_INSTANCE + "/{instanceId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void pauseRampInstance(@Nonnull @PathParam("instanceId") String instanceId);

    /**
     * 
     * @param jobId
     */
    @Path(CloudService.METHOD_PAUSE_RAMP_JOB + "/{jobId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void pauseRampJob(@Nonnull @PathParam("jobId") String jobId);

    /**
     * 
     * @param instanceIds
     */
    @Path(CloudService.METHOD_PAUSE_RAMP_INSTANCE)
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @POST
    public void pauseRampInstances(List<String> instanceIds);

    /**
     * 
     * @param instanceId
     */
    @Path(CloudService.METHOD_RESUME_RAMP_INSTANCE + "/{instanceId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void resumeRampInstance(@Nonnull @PathParam("instanceId") String instanceId);

    /**
     * 
     * @param jobId
     */
    @Path(CloudService.METHOD_RESUME_RAMP_JOB + "/{jobId}")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public void resumeRampJob(@Nonnull @PathParam("jobId") String jobId);

    /**
     * 
     * @param instanceIds
     */
    @Path(CloudService.METHOD_RESUME_RAMP_INSTANCE)
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @POST
    public void resumeRampInstances(List<String> instanceIds);

    /**
     * 
     * @param instanceIds
     */
    @Path(CloudService.METHOD_GET_COST)
    @Produces({ MediaType.TEXT_HTML })
    @GET
    public String getCostingForDates(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate);

    /**
     * 
     * @param instanceIds
     */
    @Path(CloudService.METHOD_GET_COST_PREDEFINED)
    @Produces({ MediaType.TEXT_HTML })
    @GET
    public String getCostingForDates(
            @QueryParam("timePeriod") @DefaultValue("aws-portal-current-bill-period") String timePeriod);

}
