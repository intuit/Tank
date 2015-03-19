/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.client.v1.report;

/*
 * #%L
 * Reporting Rest Client
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.InputStream;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.time.FastDateFormat;

import com.intuit.tank.api.service.v1.report.ReportService;
import com.intuit.tank.rest.BaseRestClient;
import com.intuit.tank.rest.util.ServiceConsants;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * ProjectClientV1
 * 
 * @author dangleton
 * 
 */
public class ReportServiceClientV1 extends BaseRestClient {

    private static final FastDateFormat fmt = FastDateFormat.getInstance(ReportService.DATE_FORMAT,
            TimeZone.getTimeZone("PST"));
    private static final String SERVICE_BASE_URL = ServiceConsants.REST_SERVICE_CONTEXT
            + ReportService.SERVICE_RELATIVE_PATH;

    /**
     * 
     * @param serviceUrl
     */
    public ReportServiceClientV1(String serviceUrl) {
        super(serviceUrl, null, null);
    }

    /**
     * 
     * @param serviceUrl
     */
    public ReportServiceClientV1(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    /**
     * 
     * @return
     */
    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }

    /**
     * Gets the csv data stream.
     * 
     * @param jobId
     *            the jobId of the timing data to get
     * @param period
     *            the requested period for timing data. can be 15, 30, 45, or 60. If null period is system set default
     *            15
     * @param minDate
     *            the minimumDate inclusive. if null no minimum is set
     * @param maxDate
     *            the maximum date exclusive. if null no max is set
     * @return stream of csv data in format of "Job ID", "Page ID", "Sample Size", "Average", "Min", "Max", "Period",
     *         "Start Time" first row is header row. If empty stream no results are returned.
     */
    public InputStream getBucketTimingData(@Nonnull String jobId, @Nullable Integer period, @Nullable Date minDate,
            Date maxDate) {
        UriBuilder uriBuilder = UriBuilder
                .fromUri(urlBuilder.buildUrl(ReportService.METHOD_TIMING_PERIODIC_CSV, jobId));
        if (minDate != null) {
            uriBuilder.queryParam("minTime", fmt.format(minDate));
        }
        if (maxDate != null) {
            uriBuilder.queryParam("maxTime", fmt.format(maxDate));
        }
        if (period != null && period != 15) {
            uriBuilder.queryParam("period", period.toString());
        }
        WebResource webResource = client.resource(uriBuilder.build());
        ClientResponse response = webResource.accept(MediaType.APPLICATION_OCTET_STREAM).get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntityInputStream();
    }

}
