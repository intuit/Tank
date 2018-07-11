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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.service.v1.report.ReportService;
import com.intuit.tank.dao.PeriodicDataDao;
import com.intuit.tank.dao.SummaryDataDao;
import com.intuit.tank.project.PeriodicData;
import com.intuit.tank.project.SummaryData;
import com.intuit.tank.reporting.api.TPSReportingPackage;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.reporting.local.ResultsStorage;
import com.intuit.tank.results.TankResultPackage;
import com.intuit.tank.service.util.AuthUtil;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.common.util.TimingPageName;
import com.intuit.tank.vm.settings.TankConfig;
import com.opencsv.CSVWriter;

/**
 * ProjectServiceV1
 * 
 * @author dangleton
 * 
 */
@Path(ReportService.SERVICE_RELATIVE_PATH)
public class ReportServiceV1 implements ReportService {

    private static final Logger LOG = LogManager.getLogger(ReportServiceV1.class);

    @Context
    private ServletContext servletContext;

    private static final FastDateFormat FMT = FastDateFormat.getInstance(ReportService.DATE_FORMAT, TimeZone.getTimeZone("PST"));

    /**
     * @inheritDoc
     */
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * Gets all MetricDescriptors and returns them in a list.
     * 
     * @return a Response of type MetricList
     */

    public Response getFile(String filePath, String start) {
        ResponseBuilder responseBuilder = Response.ok();
        try {
            if (filePath.contains("..") || filePath.startsWith("/")) {
                responseBuilder.status(Status.BAD_REQUEST);
            } else {
                String rootDir = "logs";
                final File f = new File(rootDir, filePath);
                if (!f.exists()) {
                    responseBuilder.status(Status.NOT_FOUND);
                } else if (!f.isFile()) {
                    responseBuilder.status(Status.BAD_REQUEST);
                } else if (!f.canRead()) {
                    responseBuilder.status(Status.FORBIDDEN);
                } else {
                    long total = f.length();
                    StreamingOutput streamer = FileReader.getFileStreamingOutput(f, total, start);
                    responseBuilder.header("X-total-Content-Length", total).entity(streamer);
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting object: " + e, e);
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
        }
        return responseBuilder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response deleteTiming(String jobId) {
        AuthUtil.checkAdmin(servletContext);
        ResponseBuilder responseBuilder = Response.noContent();
        try {
            ReportingFactory.getResultsReader().deleteTimingForJob(jobId, true);
        } catch (RuntimeException e) {
            LOG.error("Error deleting timing data: " + e, e);
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("An error occurred while deleting the timing data.");
        }
        return responseBuilder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response processSummary(final String jobId) {
        ResponseBuilder responseBuilder = Response.ok();
        try {
            Thread t = new Thread( () -> {
                SummaryReportRunner.generateSummary(jobId);
            });
            t.setDaemon(true);
            t.start();
            responseBuilder.entity("Generating summary data for job " + jobId);
        } catch (RuntimeException e) {
            LOG.error("Error deleting timing data: " + e, e);
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("An error occurred while deleting the timing data.");
        }
        return responseBuilder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getTimingCsv(final String jobId) {
        ResponseBuilder responseBuilder = Response.ok();
        final TankConfig tankConfig = new TankConfig();
        // AuthUtil.checkLoggedIn(servletContext);
        final String fileName = "timing_" + tankConfig.getInstanceName() + "_" + jobId + ".csv.gz";

        final FileStorage fileStorage = FileStorageFactory.getFileStorage(tankConfig.getTimingDir(), false);
        final FileData fd = new FileData("", fileName);
        if (fileStorage.exists(fd)) {
            StreamingOutput streamingOutput = outputStream -> {
                InputStream in = null;
                in = new GZIPInputStream(fileStorage.readFileData(fd));
                try {
                    IOUtils.copy(in, outputStream);
                } catch (RuntimeException e) {
                    throw e;
                } finally {
                    IOUtils.closeQuietly(in);
                }
            };
            String filename = "timing_" + tankConfig.getInstanceName() + "_" + jobId + ".csv";

            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            responseBuilder.entity(streamingOutput);
        } else {
            responseBuilder.status(Status.NOT_FOUND);
        }
        return responseBuilder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getTimingBucketCsv(final String jobId, final int period, final String min, final String max) {
        ResponseBuilder responseBuilder = Response.ok();
        final String tableName = ReportUtil.getBucketedTableName(jobId);
        StreamingOutput streamingOutput = outputStream -> {
            Date minDate = null;
            Date maxDate = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                minDate = StringUtils.isBlank(min) ? null : sdf.parse(min);
                maxDate = StringUtils.isBlank(max) ? null : sdf.parse(max);
            } catch (ParseException e1) {
                LOG.warn("Could not parse date : " + e1);
            }
            PeriodicDataDao dao = new PeriodicDataDao();
            List<PeriodicData> data = dao.findByJobId(Integer.parseInt(jobId), minDate, maxDate);
            LOG.info("found " + data.size() + " entries for job " + jobId + " for dates " + minDate + " - " + maxDate);
            if (!data.isEmpty()) {
                CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream));
                try {
                    String[] headers = ReportUtil.BUCKET_HEADERS;
                    csvWriter.writeNext(headers);
                    int count = 0;
                    if (period > 15) {
                        data = consolidatePeriod(data, period);
                    }
                    for (PeriodicData item : data) {
                        count++;
                        String[] line = getBucketLine(jobId, period, item);
                        csvWriter.writeNext(line);
                        if (count % 500 == 0) {
                            csvWriter.flush();
                        }
                    }
                    csvWriter.flush();
                } catch (RuntimeException e) {
                    throw e;
                } finally {
                    csvWriter.close();
                }
            }
        };

        String filename = tableName + "_" + jobId + ".csv";

        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        responseBuilder.entity(streamingOutput);
        return responseBuilder.build();
    }

    private List<PeriodicData> consolidatePeriod(List<PeriodicData> data, int period) {
        List<PeriodicData> ret = new ArrayList<PeriodicData>();
        Map<String, List<PeriodicData>> map = data.stream().collect(Collectors.groupingBy(PeriodicData::getPageId));
        // bucket the data
        // sort the buckets
        for (List<PeriodicData> l : map.values()) {
            Collections.sort(l);
            DataAverager da = new DataAverager(period);
            for (PeriodicData pd : l) {
                if (!da.isWithinPeriod(pd)) {
                    // calculate and add
                    ret.add(da.sum());
                    da = new DataAverager(period);
                }
                da.addPeriodicData(pd);
            }
            // throw away the remainder
            // PeriodicData sum = da.sum();
            // if (sum != null) {
            // ret.add(sum);
            // }
        }

        return ret;
    }

    /**
     * @param jobId
     * @param period
     * @param item
     * @return "Job ID", "Page ID", "Page Name", "Index" "Sample Size",
     *         "Average", "Min", "Max", "Period", "Start Time"
     */
    protected String[] getBucketLine(String jobId, int period, PeriodicData item) {
        TimingPageName tpn = new TimingPageName(item.getPageId());
        List<String> list = new ArrayList<String>();
        list.add(jobId);
        list.add(tpn.getId());
        list.add(tpn.getName());
        if (tpn.getIndex() != null) {
            list.add(ReportUtil.INT_NF.format(tpn.getIndex()));

        } else {
            list.add("");
        }
        list.add(ReportUtil.INT_NF.format(item.getSampleSize()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getMean()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getMin()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getMax()));
        list.add(ReportUtil.INT_NF.format(period));
        list.add(FMT.format(item.getTimestamp()));
        return list.toArray(new String[0]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getSummaryTimingCsv(final String jobId) {
        ResponseBuilder responseBuilder = Response.ok();
        // AuthUtil.checkLoggedIn(servletContext);
        StreamingOutput streamingOutput = outputStream -> {
            List<SummaryData> data = new SummaryDataDao().findByJobId(Integer.parseInt(jobId));

            if (!data.isEmpty()) {
                CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream));
                try {
                    String[] headers = ReportUtil.getSummaryHeaders();
                    csvWriter.writeNext(headers);
                    for (SummaryData item : data) {
                        String[] line = getLine(item);
                        csvWriter.writeNext(line);
                        csvWriter.flush();
                    }
                } catch (RuntimeException e) {
                    throw e;
                } finally {
                    csvWriter.close();
                }
            }
        };
        String filename = "timing_" + new TankConfig().getInstanceName() + "_" + jobId + ".csv";

        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        responseBuilder.entity(streamingOutput);
        return responseBuilder.build();
    }

    /**
     * @param item
     * @return "Page ID", "Page Name", "Index""Sample Size", "Mean", "Median",
     *         "Min", "Max", "Std Dev", "Kurtosis", "Skewness", "Varience" {
     *         "10th Percentile", 10 }, { "20th Percentile", 20 }, {
     *         "30th Percentile", 30 }, { "40th Percentile", 40 }, {
     *         "50th Percentile", 50 }, { "60th Percentile", 60 }, {
     *         "70th Percentile", 70 }, { "80th Percentile", 80 }, {
     *         "90th Percentile", 90 }, { "99th Percentile", 99 }
     */
    protected String[] getLine(SummaryData item) {
        TimingPageName tpn = new TimingPageName(item.getPageId());
        List<String> list = new ArrayList<String>();
        list.add(tpn.getId());
        list.add(tpn.getName());
        if (tpn.getIndex() != null) {
            list.add(ReportUtil.INT_NF.format(tpn.getIndex()));

        } else {
            list.add("");
        }
        list.add(ReportUtil.INT_NF.format(item.getSampleSize()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getMean()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile50()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getMin()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getMax()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getSttDev()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getKurtosis()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getSkewness()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getVarience()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile10()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile20()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile30()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile40()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile50()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile60()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile70()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile80()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile90()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile95()));
        list.add(ReportUtil.DOUBLE_NF.format(item.getPercentile99()));
        return list.toArray(new String[0]);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getSummaryTimingHtml(final String jobId) {
        ResponseBuilder responseBuilder = Response.ok();
        // AuthUtil.checkLoggedIn(servletContext);
        StringBuilder writer = new StringBuilder();
        writer.append("<html>");
        writer.append("<head>");
        writer.append("<title>");
        String title = "Summary Report for Job " + jobId;
        writer.append(title);
        writer.append("</title>");
        writer.append("<style type='text/css'>");
        writer.append("table {border: 1px solid black;white-space:nowrap;}").append('\n');
        writer.append("table tr th {border: 1px solid black;white-space:nowrap;}").append('\n');
        writer.append("table tr td {border: 1px solid black;white-space:nowrap;}").append('\n');

        writer.append("</style>");

        writer.append("</head>");
        writer.append("<body>");
        List<SummaryData> data = new SummaryDataDao().findByJobId(Integer.parseInt(jobId));
        if (!data.isEmpty()) {
            try {
                writer.append("<h2>" + title + "</h2>");
                writer.append("<table>");
                String[] headers = ReportUtil.getSummaryHeaders();
                writeRow(writer, headers, "th", "lightgray");
                int row = 0;
                for (SummaryData item : data) {
                    String[] line = getLine(item);
                    String color = row++ % 2 == 0 ? "white" : "#DBEAFF";
                    writeRow(writer, line, "td", color);
                }
            } finally {
                writer.append("</table>");
                String downloadUrl = servletContext.getContextPath() + TankConstants.REST_SERVICE_CONTEXT + ReportService.SERVICE_RELATIVE_PATH + ReportService.METHOD_TIMING_SUMMARY_CSV + "/" + jobId;
                writer.append("<p>Download CSV file <a href='" + downloadUrl + "'>" + downloadUrl + "</a></p>");
            }
        } else {
            writer.append("<p>No Summary data available for Job " + jobId + "</p>");
        }
        writer.append("</body>");
        writer.append("</html>");

        responseBuilder.entity(writer.toString());
        return responseBuilder.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getTimingBucketHtml(String jobId, int period) {
        ResponseBuilder responseBuilder = Response.ok();
        // AuthUtil.checkLoggedIn(servletContext);
        StringBuilder writer = new StringBuilder();
        writer.append("<html>");
        writer.append("<head>");
        writer.append("<title>");
        String title = "Periodic Timing Report for Job " + jobId;
        writer.append(title);
        writer.append("</title>");
        writer.append("<style type='text/css'>");
        writer.append("table {border: 1px solid black;white-space:nowrap;}").append('\n');
        writer.append("table tr th {border: 1px solid black;white-space:nowrap;}").append('\n');
        writer.append("table tr td {border: 1px solid black;white-space:nowrap;}").append('\n');

        writer.append("</style>");

        writer.append("</head>");
        writer.append("<body>");
        List<PeriodicData> data = new PeriodicDataDao().findByJobId(Integer.parseInt(jobId));
        if (!data.isEmpty()) {
            try {
                writer.append("<h2>" + title + "</h2>");
                writer.append("<table>");
                String[] headers = ReportUtil.BUCKET_HEADERS;
                writeRow(writer, headers, "th", "lightgray");
                int row = 0;
                if (period > 15) {
                    data = consolidatePeriod(data, period);
                }
                for (PeriodicData item : data) {
                    String[] line = getBucketLine(jobId, item.getPeriod(), item);
                    String color = row++ % 2 == 0 ? "white" : "#DBEAFF";
                    writeRow(writer, line, "td", color);
                }
            } finally {
                writer.append("</table>");
                String downloadUrl = servletContext.getContextPath() + TankConstants.REST_SERVICE_CONTEXT + ReportService.SERVICE_RELATIVE_PATH + ReportService.METHOD_TIMING_PERIODIC_CSV + "/"
                        + jobId;
                writer.append("<p>Download CSV file <a href='" + downloadUrl + "'>" + downloadUrl + "</a></p>");
            }
        } else {
            writer.append("<p>No Periodic data available for Job " + jobId + "</p>");
        }
        writer.append("</body>");
        writer.append("</html>");

        responseBuilder.entity(writer.toString());
        return responseBuilder.build();
    }

    @Override
    public Response setTPSInfos(final TPSReportingPackage reportingPackage) {
        ResponseBuilder responseBuilder = null;
        try {
            new Thread( () -> {
                ResultsStorage.instance().storeTpsResults(reportingPackage.getJobId(), reportingPackage.getInstanceId(), reportingPackage.getContainer());
            }).start();
            responseBuilder = Response.status(Status.ACCEPTED);

        } catch (Exception e) {
            LOG.error("Error determining status: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        return responseBuilder.build();
    }

    @Override
    public Response sendTimingResults(final TankResultPackage results) {
        ResponseBuilder responseBuilder = null;
        try {
            new Thread( () -> {
                ResultsStorage.instance().storeTimingResults(results.getJobId(), results.getInstanceId(), results.getResults());
            }).start();
            responseBuilder = Response.status(Status.ACCEPTED);

        } catch (Exception e) {
            LOG.error("Error determining status: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        return responseBuilder.build();
    }

    private void writeRow(StringBuilder out, String[] line, String cell, String bgColor) {
        out.append(Arrays.stream(line).map(s -> "<" + cell + ">" + s + "</" + cell + ">").collect(Collectors.joining("", "<tr style='background-color: " + bgColor + ";'>", "</tr>")));
    }

    /**
     * 
     * ReportServiceV1 DataAverager
     * 
     * @author dangleton
     * 
     */
    private static class DataAverager {
        private List<PeriodicData> l = new ArrayList<PeriodicData>();
        private int period;

        DataAverager(int period) {
            this.period = period;
        }

        boolean isWithinPeriod(PeriodicData pd) {
            long s = pd.getTimestamp().getTime();
            long e = pd.getTimestamp().getTime();
            for (PeriodicData d : l) {
                s = Math.min(s, d.getTimestamp().getTime());
                e = Math.max(s, pd.getTimestamp().getTime());
            }
            return e - s < period * 1000;
        }

        void addPeriodicData(PeriodicData toAdd) {
            l.add(toAdd);
        }

        PeriodicData sum() {
            PeriodicData ret = null;
            if (!l.isEmpty()) {
                ret = new PeriodicData();
                ret.setJobId(l.get(0).getJobId());
                ret.setPeriod(period);
                ret.setPageId(l.get(0).getPageId());
                ret.setTimestamp(l.get(0).getTimestamp());
                ret.setMin(Double.MAX_VALUE);
                double avg = 0;
                for (PeriodicData d : l) {
                    ret.setSampleSize(ret.getSampleSize() + d.getSampleSize());
                    ret.setMin(Math.min(ret.getMin(), d.getMin()));
                    ret.setMax(Math.max(ret.getMax(), d.getMax()));
                    avg += d.getMean();
                }
                ret.setMean(avg / l.size());
            }
            return ret;
        }
    }
}
