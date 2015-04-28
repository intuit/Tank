package com.intuit.tank.reporting.api;

import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.intuit.tank.results.TankResult;

/**
 * 
 * ResultsReporter interface for reporting results to controller. Default is DynamoDB but for standalone can use a rest
 * service or file system implementation.
 * 
 * @author dangleton
 * 
 */
public interface ResultsReporter {

    /**
     * Sends results to storage
     * 
     * @param jobId
     *            the job id
     * @param instanceId
     *            the instance id
     * @param container
     *            the tps infos
     */
    public void sendTpsResults(String jobId, String instanceId, TPSInfoContainer container);

    /**
     * Send the timing results to storage.
     * 
     * @param jobId
     *            the job id
     * @param instanceId
     *            the instance id
     * @param results
     *            the reulsts
     * @param asynch
     *            true if the job should be run asynchronpously
     */
    public void sendTimingResults(String jobId, String instanceId, List<TankResult> results, boolean asynch);

    /**
     * configure this service from config file.
     * 
     * @param config
     *            the config
     */
    public void config(HierarchicalConfiguration config);
}
