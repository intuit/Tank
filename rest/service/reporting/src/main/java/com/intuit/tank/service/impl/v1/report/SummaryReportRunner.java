/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.report;

/*
 * #%L
 * Reporting Rest Service Implementation
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.service.v1.report.ReportService;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.PeriodicDataDao;
import com.intuit.tank.dao.SummaryDataDao;
import com.intuit.tank.persistence.databases.BucketDataItem;
import com.intuit.tank.persistence.databases.MetricsCalculator;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.PeriodicData;
import com.intuit.tank.project.PeriodicDataBuilder;
import com.intuit.tank.project.SummaryData;
import com.intuit.tank.project.SummaryDataBuilder;
import com.intuit.tank.reporting.api.ResultsReader;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.event.JobEvent;

/**
 * NotificationRunner
 * 
 * @author dangleton
 * 
 */
public class SummaryReportRunner implements Runnable {
    private static final Logger LOG = LogManager.getLogger(SummaryReportRunner.class);
    private static final char NEWLINE = '\n';

    private JobEvent jobEvent;
    private Event<JobEvent> jobEventProducer;
    private String rootUrl;

    /**
     * 
     * @param jobEventProducer
     * @param jobEvent
     */
    public SummaryReportRunner(String rootUrl,
            Event<JobEvent> jobEventProducer, JobEvent jobEvent) {
        this.jobEvent = jobEvent;
        this.jobEventProducer = jobEventProducer;
        this.rootUrl = rootUrl;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void run() {
        String jobId = jobEvent.getJobId();
        generateSummary(jobEvent.getJobId());
        jobEventProducer.fire(new JobEvent(jobId, getSummaryEventMessage(),
                JobLifecycleEvent.SUMMARY_REPORT_FINISHED));
    }

    /**
     * @return
     */
    private String getSummaryEventMessage() {
        return "Summary Timing Data Report for job " + jobEvent.getJobId()
                + " is ready for download." + NEWLINE + NEWLINE
                + "The report can be downloaded at " + rootUrl
                + "/rest"+ ReportService.SERVICE_RELATIVE_PATH
                + ReportService.METHOD_TIMING_SUMMARY_CSV + '/'
                + jobEvent.getJobId();
    }

    /**
     * @param jobIdString
     */
    public static void generateSummary(String jobIdString) {
        synchronized (jobIdString) {
            LOG.info("generateSummary: job " + jobIdString);
            ResultsReader resultsReader = ReportingFactory.getResultsReader();
            if (resultsReader.hasTimingData(jobIdString)) {
                LOG.info("Generating Summary Report for job " + jobIdString + "...");
                SummaryDataDao dao = new SummaryDataDao();
                int jobId = Integer.parseInt(jobIdString);
                if (dao.findByJobId(jobId).size() == 0) {
                    MethodTimer mt = new MethodTimer(LOG,
                            SummaryReportRunner.class, "generateSummary");
                    MetricsCalculator metricsCalculator = new MetricsCalculator();
                    JobInstance jobInstance = new JobInstanceDao()
                            .findById(Integer.valueOf(jobIdString));
                    metricsCalculator.retrieveAndCalculateTimingData(jobIdString,
                            calculateSteadyStateStart(jobInstance),
                            calculateSteadyStateEnd(jobInstance));
                    mt.markAndLog("calculated timing data");
                    Map<String, DescriptiveStatistics> timingData = metricsCalculator
                            .getSummaryResults();
                    if (!timingData.isEmpty()) {
                        try {
                            List<SummaryData> toStore = timingData
                                    .entrySet().stream().map(entry -> getSummaryData(jobId,
                                            entry.getKey(), entry.getValue())).collect(Collectors.toList());
                            dao.persistCollection(toStore);
                            LOG.info("Finished Summary Report for job " + jobId);
                        } catch (Exception t) {
                            LOG.error("Error adding Summary Items: " + t, t);
                        }
                    }
                    mt.markAndLog("stored " + timingData.size()
                            + " summary data items");
                    // now store buckets
                    Map<String, Map<Date, BucketDataItem>> bucketItems = metricsCalculator
                            .getBucketItems();
                    int countItems = 0;
                    if (!bucketItems.isEmpty()) {
                        PeriodicDataDao periodicDataDao = new PeriodicDataDao();
                        try {
                            List<PeriodicData> toStore = new ArrayList<PeriodicData>();
                            for (Entry<String, Map<Date, BucketDataItem>> entry : bucketItems
                                    .entrySet()) {
                                for (BucketDataItem bucketItem : entry
                                        .getValue().values()) {
                                    PeriodicData data = getBucketData(jobId,
                                            entry.getKey(), bucketItem);
                                    toStore.add(data);
                                    countItems++;
                                }
                            }
                            periodicDataDao.persistCollection(toStore);
                            LOG.info("Finished Summary Report for job " + jobId);
                        } catch (Exception t) {
                            LOG.error("Error adding Summary Items: " + t, t);
                        }
                    }
                    mt.markAndLog("stored " + countItems
                            + " periodic data items");
                    mt.endAndLog();
                }
            } else {
                LOG.info("generateSummary: job " + jobIdString
                        + " has no data.");
            }
            // now delete timing data
            resultsReader.deleteTimingForJob(jobIdString, true);
        }
    }

    private static Date calculateSteadyStateEnd(JobInstance jobInstance) {
        return (jobInstance.getStartTime() != null
                && jobInstance.getSimulationTime() > 0) ? null :
            new Date(jobInstance.getStartTime().getTime()
                    + jobInstance.getSimulationTime());
    }

    private static Date calculateSteadyStateStart(JobInstance jobInstance) {
        return jobInstance.getStartTime() != null ? null :
            new Date(jobInstance.getStartTime().getTime()
                    + jobInstance.getRampTime());
    }

    /**
     * @param jobId
     * @param key
     * @param bucketItem
     * @return
     */
    private static PeriodicData getBucketData(int jobId, String key,
            BucketDataItem bucketItem) {
        DescriptiveStatistics stats = bucketItem.getStats();
        return PeriodicDataBuilder.periodicData().withJobId(jobId)
                .withMax(stats.getMax()).withMean(stats.getMean())
                .withMin(stats.getMin()).withPageId(key)
                .withSampleSize((int) stats.getN())
                .withPeriod(bucketItem.getPeriod())
                .withTimestamp(bucketItem.getStartTime()).build();
    }

    /**
     * @param jobId
     * @param key
     * @param stats
     * @return
     */
    private static SummaryData getSummaryData(int jobId, String key,
            DescriptiveStatistics stats) {
        return SummaryDataBuilder
                .summaryData()
                .withJobId(jobId)
                .withKurtosis(
                        !Double.isNaN(stats.getKurtosis()) ? stats
                                .getKurtosis() : 0)
                .withMax(stats.getMax())
                .withMean(stats.getMean())
                .withMin(stats.getMin())
                .withPageId(key)
                .withPercentile10(stats.getPercentile(10))
                .withPercentile20(stats.getPercentile(20))
                .withPercentile30(stats.getPercentile(30))
                .withPercentile40(stats.getPercentile(40))
                .withPercentile50(stats.getPercentile(50))
                .withPercentile60(stats.getPercentile(60))
                .withPercentile70(stats.getPercentile(70))
                .withPercentile80(stats.getPercentile(80))
                .withPercentile90(stats.getPercentile(90))
                .withPercentile95(stats.getPercentile(95))
                .withPercentile99(stats.getPercentile(99))
                .withSampleSize((int) stats.getN())
                .withSkewness(
                        !Double.isNaN(stats.getSkewness()) ? stats
                                .getSkewness() : 0)
                .withSttDev(
                        !Double.isNaN(stats.getStandardDeviation()) ? stats
                                .getStandardDeviation() : 0)
                .withVarience(
                        !Double.isNaN(stats.getVariance()) ? stats
                                .getVariance() : 0).build();
    }

}
