/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.cloud;

/*
 * #%L
 * Cloud Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.service.v1.cloud.CloudService;
import com.intuit.tank.dao.SummaryDataDao;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.service.util.ResponseUtil;
import com.intuit.tank.service.util.ServletInjector;

/**
 * CloudServiceV1
 * 
 * @author dangleton
 * 
 */
@Path(CloudService.SERVICE_RELATIVE_PATH)
public class CloudServiceV1 implements CloudService {

    private static final Logger LOG = LogManager.getLogger(CloudServiceV1.class);

    @Context
    private ServletContext servletContext;

    private DateFormat dateFormatFull = new SimpleDateFormat("MM-dd-yyyy'T'hh:mm:ss z");
    private DateFormat dateFormatShort = new SimpleDateFormat("MM-dd-yyyy");

    private static Hashtable<String, Stack<Integer>> stackMap = new Hashtable<String, Stack<Integer>>();

    /**
     * @{inheritDoc
     */
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String userIdFromRange(String jobId, int minValue, int maxValue) {
        Stack<Integer> stack = getStack(minValue, maxValue);
        if (stack.size() > 0) {
            return Integer.toString(stack.pop());
        }
        throw new IllegalArgumentException("Exhausted random User Ids. Range not large enough for the number of calls.");
    }

    /**
     * @param minId
     * @param maxId
     * @return
     */
    private synchronized Stack<Integer> getStack(Integer minId, Integer maxId) {
        Stack<Integer> stack = stackMap.get(minId.toString() + "-" + maxId.toString());
        if (stack == null) {
            List<Integer> list = new ArrayList<Integer>();
            for (int i = minId; i <= maxId; i++) {
                list.add(i);
            }
            Collections.shuffle(list);
            stack = new Stack<Integer>();
            stack.addAll(list);
        }
        return stack;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getSummaryStatus(String jobId) {
        ResponseBuilder responseBuilder = null;
        try {
            responseBuilder = Response.ok();
            boolean hasTable = ReportingFactory.getResultsReader().hasTimingData(jobId);
            boolean hasEntries = new SummaryDataDao().findByJobId(Integer.parseInt(jobId)).size() != 0;
            String status = "Gathering";
            if (hasEntries && !hasTable) {
                status = "Complete";
            } else if (!hasTable && !hasEntries) {
                status = "NoData";
            }
            responseBuilder.entity(status);
        } catch (Exception e) {
            LOG.error("Error determining status: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getVmStatus(String instanceId) {
        ResponseBuilder responseBuilder = null;
        try {
            responseBuilder = Response.ok();
            CloudController controller = new ServletInjector<CloudController>().getManagedBean(
                    servletContext, CloudController.class);
            CloudVmStatus status = controller.getVmStatus(instanceId);
            responseBuilder.entity(status);
        } catch (Exception e) {
            LOG.error("Error Applying Filters: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void setVmStatus(final String instanceId, final CloudVmStatus status) {
        CloudController controller = new ServletInjector<CloudController>().getManagedBean(
                servletContext, CloudController.class);
        controller.setVmStatus(instanceId, status);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getVmStatusForJob(String jobId) {
        ResponseBuilder responseBuilder = null;
        try {
            responseBuilder = Response.ok();
            CloudController controller = new ServletInjector<CloudController>().getManagedBean(
                    servletContext, CloudController.class);
            CloudVmStatusContainer container = controller.getVmStatusForJob(jobId);
            responseBuilder.entity(container);
        } catch (Exception e) {
            LOG.error("Error Applying Filters: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();

    }

    /**
     * @{inheritDoc
     */
    @Override
    public String startJob(String jobId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        return controller.startJob(jobId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void killJob(String jobId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.killJob(jobId);
    }
    

    /**
     * @{inheritDoc
     */
    @Override
    public Set<CloudVmStatusContainer> killAllJobs() {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        return controller.killAllJobs();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void killInstance(String instanceId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.killInstance(instanceId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void killInstances(List<String> instanceIds) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.killInstances(instanceIds);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Set<CloudVmStatusContainer> stopAllJobs() {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        return controller.stopAllJobs();
    }
    
    /**
     * @{inheritDoc
     */
    @Override
    public void stopJob(String jobId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.stopJob(jobId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void stopAgent(String instanceId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.stopAgent(instanceId);

    }

    /**
     * @{inheritDoc
     */
    @Override
    public void stopAgents(List<String> instanceIds) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.stopAgents(instanceIds);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void pauseJob(String jobId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.pauseJob(jobId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void pauseAgent(String instanceId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.pauseAgent(instanceId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void pauseAgents(List<String> instanceIds) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.pauseAgents(instanceIds);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void restartJob(String jobId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.restartJob(jobId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void restartAgent(String instanceId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.restartAgent(instanceId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void restartAgents(List<String> instanceIds) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.restartAgents(instanceIds);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void pauseRampInstance(String instanceId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.pauseRampInstance(instanceId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void pauseRampJob(String jobId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.pauseRampJob(jobId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void pauseRampInstances(List<String> instanceIds) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.pauseRampInstances(instanceIds);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void resumeRampInstance(String instanceId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.resumeRampInstance(instanceId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void resumeRampJob(String jobId) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.resumeRampJob(jobId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void resumeRampInstances(List<String> instanceIds) {
        JobController controller = new ServletInjector<JobController>().getManagedBean(
                servletContext, JobController.class);
        controller.resumeRampInstances(instanceIds);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getCostingForDates(String startDate, String endDate) {
        // AWSCostRetriever retriever = new AWSCostRetriever();
        // Calendar start = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        // Calendar end = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        // start.setTime(parseDateString(startDate));
        // end.setTime(parseDateString(endDate));
        // ServiceUsage usage = retriever.getCustomReport(start, end);
        // UsageCalculator calc = new UsageCalculator(usage);
        // return AwsUtil.generateReport(calc);
        throw new RuntimeException("Not implemented");
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getCostingForDates(String timePeriod) {
        throw new RuntimeException("Not implemented");
        // AWSCostRetriever retriever = new AWSCostRetriever();
        // ServiceUsage usage = retriever.getPreDefinedReport(TimePeriodSelectChoice.valueOf(timePeriod));
        // UsageCalculator calc = new UsageCalculator(usage);
        // return AwsUtil.generateReport(calc);
    }

}
