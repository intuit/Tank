package com.intuit.tank.reporting.local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.results.TankResult;

public final class ResultsStorage {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResultsStorage.class);
    private static ResultsStorage instance = new ResultsStorage();

    private Map<String, Map<String, List<TankResult>>> timingResultMap = new ConcurrentHashMap<String, Map<String, List<TankResult>>>();
    private Map<String, Map<String, List<TPSInfoContainer>>> tpsMap = new ConcurrentHashMap<String, Map<String, List<TPSInfoContainer>>>();

    private ResultsStorage() {
        // private constructor to implement singleton pattern
    }

    public static final ResultsStorage instance() {
        return instance;
    }

    /**
     * 
     * @param jobId
     * @param results
     */
    public void storeTimingResults(@Nonnull String jobId, @Nonnull String instanceId, List<TankResult> results) {
        if (results != null && !results.isEmpty()) {
            Map<String, List<TankResult>> map = timingResultMap.get(jobId);
            if (map == null) {
                map = new ConcurrentHashMap<String, List<TankResult>>();
                timingResultMap.put(jobId, map);
            }
            List<TankResult> list = map.get(instanceId);
            if (list == null) {
                list = new ArrayList<TankResult>();
                map.put(instanceId, list);
            }
            list.addAll(results);
        }
    }

    /**
     * 
     * @param jobId
     * @param results
     */
    public void storeTpsResults(@Nonnull String jobId, @Nonnull String instanceId, TPSInfoContainer container) {
        if (container != null) {
            LOG.info("Storing results for job: " + jobId + " instance: " + instanceId);
            Map<String, List<TPSInfoContainer>> map = tpsMap.get(jobId);
            if (map == null) {
                map = new ConcurrentHashMap<String, List<TPSInfoContainer>>();
                tpsMap.put(jobId, map);
                LOG.info("Creating Map for job: " + jobId);
            }
            List<TPSInfoContainer> list = map.get(instanceId);
            if (list == null) {
                list = new ArrayList<TPSInfoContainer>();
                map.put(instanceId, list);
                LOG.info("Creating List for instance: " + instanceId);
            }
            list.add(container);
        }
    }

    /**
     * 
     * @param jobId
     * @return
     */
    public List<TankResult> getAllTimingResults(String jobId) {
        List<TankResult> ret = new ArrayList<TankResult>();
        Map<String, List<TankResult>> map = timingResultMap.get(jobId);
        if (map != null) {
            for (List<TankResult> r : map.values()) {
                ret.addAll(r);
            }
        }

        return ret;
    }

    public void deleteForJob(String jobId) {
        timingResultMap.remove(jobId);
    }

    public Map<Date, Map<String, TPSInfo>> getTpsMapForJob(Date minDate, String... jobId) {
        List<TPSInfoContainer> containers = new ArrayList<TPSInfoContainer>();
        for (String id : jobId) {
            Map<String, List<TPSInfoContainer>> map = tpsMap.get(id);
            if (map != null) {
                for (List<TPSInfoContainer> list : map.values()) {
                    containers.addAll(list);
                }
            }
        }
        LOG.info("Have " + containers.size() + " containers for jobs " + Arrays.toString(jobId));
        return getTpsMapForJob(minDate, containers);
    }

    public Map<Date, Map<String, TPSInfo>> getTpsMapForInstance(Date minDate, String jobId, String instanceId) {
        List<TPSInfoContainer> containers = new ArrayList<TPSInfoContainer>();
        Map<String, List<TPSInfoContainer>> map = tpsMap.get(jobId);
        if (map != null) {
            List<TPSInfoContainer> list = map.get(instanceId);
            if (list != null) {
                containers.addAll(list);
            }
        }
        LOG.info("Have " + containers.size() + " containers for job " + jobId + " and instance " + instanceId);
        return getTpsMapForJob(minDate, containers);
    }

    private Map<Date, Map<String, TPSInfo>> getTpsMapForJob(Date minDate, List<TPSInfoContainer> conatiners) {
        Map<Date, Map<String, TPSInfo>> ret = new HashMap<Date, Map<String, TPSInfo>>();
        for (TPSInfoContainer container : conatiners) {
            for (TPSInfo info : container.getTpsInfos()) {
                if (minDate != null && info.getTimestamp().getTime() >= minDate.getTime()) {
                    Map<String, TPSInfo> map = ret.get(info.getTimestamp());
                    if (map == null) {
                        map = new HashMap<String, TPSInfo>();
                        ret.put(info.getTimestamp(), map);
                    }
                    TPSInfo existing = map.get(info.getKey());
                    if (existing != null) {
                        info = existing.add(info);
                    }
                    map.put(info.getKey(), info);
                }
            }
        }
        return ret;
    }

}
