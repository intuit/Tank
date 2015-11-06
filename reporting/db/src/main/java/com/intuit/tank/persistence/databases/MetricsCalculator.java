/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import com.intuit.tank.reporting.api.PagedTimingResults;
import com.intuit.tank.reporting.api.ResultsReader;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.TimeUtil;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * MetricsCalculator
 * 
 * @author dangleton
 * 
 */
public class MetricsCalculator {

    private static final Logger LOG = Logger.getLogger(MetricsCalculator.class);

    private Map<String, DescriptiveStatistics> summaryResults = new HashMap<String, DescriptiveStatistics>();
    private Map<String, Map<Date, BucketDataItem>> bucketItems = new HashMap<String, Map<Date, BucketDataItem>>();
    private static final String[] FIELDS = {
            DatabaseKeys.TIMESTAMP_KEY.getShortKey(),
            DatabaseKeys.JOB_ID_KEY.getShortKey(),
            DatabaseKeys.INSTANCE_ID_KEY.getShortKey(),
            DatabaseKeys.LOGGING_KEY_KEY.getShortKey(),
            DatabaseKeys.STATUS_CODE_KEY.getShortKey(),
            DatabaseKeys.RESPONSE_TIME_KEY.getShortKey(),
            DatabaseKeys.RESPONSE_SIZE_KEY.getShortKey(),
            DatabaseKeys.IS_ERROR_KEY.getShortKey()
    };

    /**
     * @param object2
     * @param start
     * @{inheritDoc
     */
    public void retrieveAndCalculateTimingData(@Nonnull String jobId, Date start, Date end) {
        MethodTimer mt = new MethodTimer(LOG, this.getClass(), "retrieveAndCalculateSummaryTimingCsv");
        int period = 15;
        Writer csvFile = null;
        CSVWriter csvWriter = null;
        InputStream is = null;
        try {
            ResultsReader resultsReader = ReportingFactory.getResultsReader();
            String fileName = "timing_" + new TankConfig().getInstanceName() + "_" + jobId + ".csv.gz";
            File f = File.createTempFile("timing", ".csv.gz");
            csvFile = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(f)));
            csvWriter = new CSVWriter(csvFile);
            int count = 0;
            Object nextToken = null;
            csvWriter.writeNext(FIELDS);
            do {
                PagedTimingResults results = resultsReader.getPagedTimingResults(jobId, nextToken);
                for (TankResult result : results.getResults()) {
                    count++;
                    String[] entryArray = getCsvArray(result);
                    csvWriter.writeNext(entryArray);
                    if (count % 1000 == 0) {
                        csvWriter.flush();
                    }

                    double d = result.getResponseTime();
                    if (!skipDate(result.getTimeStamp(), start, end)) {
                        DescriptiveStatistics statistics = summaryResults.get(result.getRequestName());
                        if (statistics == null) {
                            statistics = new DescriptiveStatistics();
                            summaryResults.put(result.getRequestName(), statistics);
                        }
                        statistics.addValue(d);
                    }
                    if (result.getTimeStamp() != null) {
                        Date periodDate = TimeUtil.normalizeToPeriod(period, result.getTimeStamp());
                        DescriptiveStatistics bucketStats = getBucketStats(result.getRequestName(), period, periodDate);
                        bucketStats.addValue(d);
                    }

                }
                nextToken = results.getNextToken();
            } while (nextToken != null);
            csvWriter.flush();
            csvWriter.close();
            csvWriter = null;
            IOUtils.closeQuietly(csvFile);
            FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getTimingDir(), false);
            FileData fd = new FileData("",fileName);
            is = new FileInputStream(f);
            fileStorage.storeFileData(fd, is);
            mt.endAndLog();
            LOG.info("Processed " + count + " total items for job " + jobId);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException e) {
                    // swallow
                    LOG.warn("Error closing csv file: " + e);
                }
            }
            IOUtils.closeQuietly(csvFile);
            IOUtils.closeQuietly(is);
        }
    }

    private String[] getCsvArray(TankResult result) {
        String[] ret = new String[FIELDS.length];
        ret[0] = ReportUtil.getTimestamp(result.getTimeStamp());
        ret[1] = result.getJobId();
        ret[2] = result.getInstanceId();
        ret[3] = result.getRequestName();
        ret[3] = Integer.toString(result.getStatusCode());
        ret[3] = Integer.toString(result.getResponseTime());
        ret[3] = Integer.toString(result.getResponseSize());
        ret[3] = Boolean.toString(result.isError());

        // DatabaseKeys.IS_ERROR_KEY.getShortKey()
        return ret;
    }

    private boolean skipDate(Date date, Date start, Date end) {
        boolean ret = false;
        if (start != null && date.before(start)) {
            ret = true;
        } else if (end != null && date.after(end)) {
            ret = true;
        }
        return ret;
    }

    /**
     * @param loggingKey
     * @param periodDate
     * @return
     */
    private DescriptiveStatistics getBucketStats(String loggingKey, int period, Date periodDate) {
        Map<Date, BucketDataItem> map = bucketItems.get(loggingKey);
        if (map == null) {
            map = new HashMap<Date, BucketDataItem>();
            bucketItems.put(loggingKey, map);
        }
        BucketDataItem bucketDataItem = map.get(periodDate);
        if (bucketDataItem == null) {
            bucketDataItem = new BucketDataItem(period, periodDate, new DescriptiveStatistics());
            map.put(periodDate, bucketDataItem);
        }
        return bucketDataItem.getStats();
    }

    /**
     * @return the summaryResults
     */
    public Map<String, DescriptiveStatistics> getSummaryResults() {
        return summaryResults;
    }

    /**
     * @return the bucketItems
     */
    public Map<String, Map<Date, BucketDataItem>> getBucketItems() {
        return bucketItems;
    }

}
