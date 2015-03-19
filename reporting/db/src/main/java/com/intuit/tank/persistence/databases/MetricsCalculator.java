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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;
import com.intuit.tank.reporting.databases.TankDatabaseType;
import com.intuit.tank.vm.common.util.MethodTimer;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.TimeUtil;

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

    /**
     * @param object2
     * @param start
     * @{inheritDoc
     */
    public void retrieveAndCalculateTimingData(String tableName, @Nonnull String jobId, Date start, Date end) {
        MethodTimer mt = new MethodTimer(LOG, this.getClass(), "retrieveAndCalculateSummaryTimingCsv");
        IDatabase db = DataBaseFactory.getDatabase();
        int period = 15;
        Writer csvFile = null;
        CSVWriter csvWriter = null;
        try {

            File parentFile = new File("/mnt/wats/timing");
            parentFile.mkdirs();
            csvFile = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(new File(parentFile,
                    db.getDatabaseName(TankDatabaseType.timing, jobId) + ".csv.gz"))));
            boolean outputHeaderRow = true;
            csvWriter = new CSVWriter(csvFile);
            List<String> headers = new ArrayList<String>();
            int count = 0;
            Object nextToken = null;
            do {
                PagedDatabaseResult pagedItems = db.getPagedItems(tableName, jobId, nextToken, null, null, null);
                for (Item item : pagedItems.getItems()) {
                    count++;
                    if (outputHeaderRow) {
                        for (Attribute attr : item.getAttributes()) {
                            headers.add(attr.getName());
                        }
                        csvWriter.writeNext(headers.toArray(new String[headers.size()]));
                        outputHeaderRow = false;
                    }
                    Map<String, String> entries = new HashMap<String, String>();
                    for (Attribute attr : item.getAttributes()) {
                        entries.put(attr.getName(), attr.getValue());
                    }
                    String[] entryArray = new String[headers.size()];
                    for (int i = 0; i < headers.size(); i++) {
                        entryArray[i] = entries.get(headers.get(i));
                    }
                    csvWriter.writeNext(entryArray);
                    if (count % 1000 == 0) {
                        csvWriter.flush();
                    }
                    String loggingKey = entries.get(DatabaseKeys.LOGGING_KEY_KEY.getShortKey());
                    String value = entries.get(DatabaseKeys.RESPONSE_TIME_KEY.getShortKey());
                    Date date = null;
                    boolean skip = true;
                    try {
                        date = ReportUtil.parseTimestamp(entries
                                .get(DatabaseKeys.TIMESTAMP_KEY.getShortKey()));
                        skip = skipDate(date, start, end);
                    } catch (ParseException e) {
                        LOG.warn("Cannot parse date: " + entries.get(DatabaseKeys.TIMESTAMP_KEY.getShortKey()));
                    }

                    if (NumberUtils.isNumber(value)) {
                        double d = Double.parseDouble(value);
                        if (!skip) {
                            DescriptiveStatistics statistics = summaryResults.get(loggingKey);
                            if (statistics == null) {
                                statistics = new DescriptiveStatistics();
                                summaryResults.put(loggingKey, statistics);
                            }
                            statistics.addValue(d);
                        }
                        if (date != null) {
                            Date periodDate = TimeUtil.normalizeToPeriod(period, date);
                            DescriptiveStatistics bucketStats = getBucketStats(loggingKey, period, periodDate);
                            bucketStats.addValue(d);
                        }

                    }
                }
                nextToken = pagedItems.getNextToken();
            } while (nextToken != null);
            csvWriter.flush();
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
        }
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
