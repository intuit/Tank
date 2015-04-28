/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.intuit.tank.CloudServiceClient;
import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * DatabaseResultsReporter
 * 
 * @author dangleton
 * 
 */
public class CloudResultsReporter implements ResultsReporter {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CloudResultsReporter.class);

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(50), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private CloudServiceClient client;

    /**
     * @{inheritDoc
     */
    @Override
    public void sendTpsResults(String jobId, String instanceId, TPSInfoContainer container) {
        try {
            getClient().postTpsResults(jobId, instanceId, container);
        } catch (Exception e) {
            LOG.error("Error storing TPS: " + e, e);
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void sendTimingResults(final String jobId, final String instanceId, List<TankResult> results, boolean asynch) {
        if (results != null && !results.isEmpty()) {
            final List<TankResult> list = new ArrayList<TankResult>(results);
            Runnable task = new Runnable() {
                public void run() {
                    try {
                        getClient().postTimingResults(jobId, instanceId, list);
                    } catch (Exception t) {
                        LOG.error("Error adding results: " + t.getMessage(), t);
                        throw new RuntimeException(t);
                    }
                }
            };
            if (asynch) {
                EXECUTOR.execute(task);
            } else {
                task.run();
            }
        }
    }

    @Override
    public void config(HierarchicalConfiguration config) {
        try {
        } catch (Exception e) {
            LOG.error("Config not correct. Using default options.");
        }
    }

    private CloudServiceClient getClient() {
        if (client == null) {
            TankConfig config = new TankConfig();
            config.getControllerBase();
            client = new CloudServiceClient(config.getControllerBase());
        }
        return client;

    }

}
