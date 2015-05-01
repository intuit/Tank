/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.rest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.intuit.tank.reporting.api.PagedTimingResults;
import com.intuit.tank.reporting.api.ResultsReader;
import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.reporting.local.ResultsStorage;
import com.intuit.tank.results.TankResult;

/**
 * RestResultsReader
 * 
 * @author dangleton
 * 
 */
public class RestResultsReader implements ResultsReader {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RestResultsReader.class);

    @Override
    public List<TankResult> getAllTimingResults(String jobId) {
        return ResultsStorage.instance().getAllTimingResults(jobId);
    }

    @Override
    public PagedTimingResults getPagedTimingResults(String jobId, Object nextToken) {
        return new PagedTimingResults(null, ResultsStorage.instance().getAllTimingResults(jobId));
    }

    @Override
    public boolean hasTimingData(String jobId) {
        return !ResultsStorage.instance().getAllTimingResults(jobId).isEmpty();
    }

    @Override
    public void deleteTimingForJob(String jobId, boolean asynch) {
        ResultsStorage.instance().deleteForJob(jobId);
    }

    @Override
    public Map<Date, Map<String, TPSInfo>> getTpsMapForJob(String... jobId) {
        return ResultsStorage.instance().getTpsMapForJob(jobId);
    }

    @Override
    public Map<Date, Map<String, TPSInfo>> getTpsMapForInstance(String jobId, String instanceId) {
        return ResultsStorage.instance().getTpsMapForInstance(jobId, instanceId);
    }

    @Override
    public void config(HierarchicalConfiguration config) {
        // TODO Auto-generated method stub
        
    }

    

}
