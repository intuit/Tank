package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientResponse;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.util.DataFileUtil;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.JobVmCalculator;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

@ApplicationScoped
public class JobManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private static final Logger LOG = LogManager.getLogger(JobManager.class);

    private static final int MAX_RETRIES = 60;
    private static final long RETRY_SLEEP = 30 * 1000; // 30 second

    @Inject
    private VMTracker vmTracker;

    @Inject
    private StandaloneAgentTracker standaloneTracker;

    @Inject
    private Instance<WorkLoadFactory> workLoadFactoryInstance;

    private Map<String, JobInfo> agentMap = new HashMap<String, JobInfo>();

    private Map<Integer, Integer> dataFileCountMap = new ConcurrentHashMap<Integer, Integer>();

    @Inject
    private TankConfig tankConfig;

    /**
     * @param id
     * @throws Exception
     */
    public synchronized void startJob(int id) {
        IncreasingWorkLoad project = workLoadFactoryInstance.get().getModelRunner(id);
        JobRequest jobRequest = project.getJob();
        agentMap.put(Integer.toString(id), new JobInfo(jobRequest));
        if (new TankConfig().getStandalone()) {
            JobInstanceDao jobInstanceDao = new JobInstanceDao();
            JobInstance jobInstance = jobInstanceDao.findById(id);
            List<AgentAvailability> agents = standaloneTracker.getAgents(jobRequest.getTotalVirtualUsers());
            if (agents == null) {
                jobInstance.setStatus(JobQueueStatus.Aborted);
                jobInstanceDao.saveOrUpdate(jobInstance);
            } else {
                // start the jobs by sending start commmand
                try {
                    for (AgentAvailability a : agents) {
                        StandaloneAgentRequest standaloneAgentRequest = new StandaloneAgentRequest(
                                Integer.toString(id), a.getInstanceId(), jobRequest.getTotalVirtualUsers());
                        standaloneAgentRequest.setStopBehavior(jobRequest.getStopBehavior());
                        sendRequest(a.getInstanceUrl(), standaloneAgentRequest);
                    }
                } catch (Exception e) {
                    LOG.error("Error starting agents: " + e, e);

                    // TODO: kill any agents that were started
                    jobInstance.setStatus(JobQueueStatus.Aborted);
                    jobInstanceDao.saveOrUpdate(jobInstance);
                }
            }
        } else {
            executor.execute(project);
        }
    }

    private void sendRequest(String instanceUrl, StandaloneAgentRequest standaloneAgentRequest) {
        Client client = ClientBuilder.newClient();
        //client.setConnectTimeout(5000);
        //client.setFollowRedirects(true);
        WebTarget webTarget = client.target(instanceUrl + WatsAgentCommand.request.getPath());
        ClientResponse response = webTarget.request().post(Entity.entity(standaloneAgentRequest, MediaType.APPLICATION_XML), ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("failed to start agent: " + response.toString());
        }

    }

    public AgentTestStartData registerAgentForJob(AgentData agent) {
        AgentTestStartData ret = null;
        JobInfo jobInfo = agentMap.get(agent.getJobId());
        // TODO: figure out restarts
        if (jobInfo != null) {
            synchronized (jobInfo) {
                ret = new AgentTestStartData(jobInfo.scripts, jobInfo.getUsers(agent), jobInfo.jobRequest.getRampTime());
                ret.setAgentInstanceNum(jobInfo.agentData.size());
                ret.setDataFiles(getDataFileRequests(jobInfo));
                ret.setJobId(agent.getJobId());
                ret.setSimulationTime(jobInfo.jobRequest.getSimulationTime());
                ret.setStartUsers(jobInfo.jobRequest.getBaselineVirtualUsers());
                ret.setTotalAgents(jobInfo.numberOfMachines);
                ret.setUserIntervalIncrement(jobInfo.jobRequest.getUserIntervalIncrement());
                jobInfo.agentData.add(agent);
                CloudVmStatus status = vmTracker.getStatus(agent.getInstanceId());
                status.setVmStatus(VMStatus.pending);
                vmTracker.setStatus(status);
                if (jobInfo.isFilled()) {
                    startTest(jobInfo);
                }
            }
        }
        return ret;

    }

    private void startTest(final JobInfo info) {
        LOG.info("Sending start command asynchronously.");
        Thread thread = new Thread( () -> {
            LOG.info("Sleeping for one minute before starting test to give time for all agents to download files.");
            try {
                Thread.sleep(60 * 1000);// 1 minute
            } catch (InterruptedException e) {
                // ignore
            }
            try {
                LOG.info("Sending start commands on executer.");
                List<FutureTask<AgentData>> futures = info.agentData.stream().map(agent -> sendCommand(agent, WatsAgentCommand.start, true)).collect(Collectors.toList());
                LOG.info("waiting for agentFutures to return.");
                for (FutureTask<AgentData> future : futures) {
                    AgentData dataFuture = future.get();
                    if (dataFuture != null) {
                        // error happened. TODO: message system that agent did not start.
                        vmTracker.setStatus(crateFailureStatus(dataFuture));
                        vmTracker.stopJob(info.jobRequest.getId());
                    }
                }
                LOG.info("All agents received start command.");
            } catch (Exception e) {
                LOG.error("Error sending start: " + e, e);
                vmTracker.stopJob(info.jobRequest.getId());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private CloudVmStatus crateFailureStatus(AgentData data) {
        return new CloudVmStatus(data.getInstanceId(), data.getJobId(), null, JobStatus.Unknown, VMImageType.AGENT,
                data.getRegion(), VMStatus.stopped, new ValidationStatus(), data.getUsers(), 0, null, null);
    }

    public FutureTask<AgentData> sendCommand(String instanceId, WatsAgentCommand cmd) {
        AgentData agent = getAgentData(instanceId);
        if (agent != null) {
            return sendCommand(agent, cmd, false);
        }
        return null;
    }

    private AgentData getAgentData(String instanceId) {
        return agentMap.values().stream().flatMap(info -> info.agentData.stream()).filter(data -> instanceId.equals(data.getInstanceId())).findFirst().orElse(null);
    }

    private FutureTask<AgentData> sendCommand(final AgentData agent, final WatsAgentCommand cmd, final boolean retry) {
        FutureTask<AgentData> future =
                new FutureTask<AgentData>( () -> {
                    int retries = retry ? MAX_RETRIES : 0;
                    String url = agent.getInstanceUrl() + cmd.getPath();
                    while (retries >= 0) {
                        retries--;
                        try {
                            LOG.info("Sending command " + cmd + " to url " + url);
                            new URL(url).getContent();
                            break;
                        } catch (Exception e) {
                            LOG.error("Error sending command " + cmd.name() + " to " + url + ": " + e);
                            // look up public ip
                            if (!tankConfig.getStandalone()) {
                                AmazonInstance amazonInstance = new AmazonInstance(null, agent.getRegion());
                                String dns = amazonInstance.findPublicName(agent.getInstanceId());
                                if (StringUtils.isNotEmpty(dns)) {
                                    url = "http://" + dns + ":"
                                            + new TankConfig().getAgentConfig().getAgentPort()
                                            + cmd.getPath();
                                }
                            }
                            if (retries >= 0) {
                                try {
                                    Thread.sleep(RETRY_SLEEP);
                                } catch (InterruptedException e1) {
                                    LOG.error("interrupted: " + e1);
                                }
                                continue;
                            }
                            return agent;
                        }
                    }
                    return null;
                });
        executor.execute(future);
        return future;

    }

    private DataFileRequest[] getDataFileRequests(JobInfo info) {
        List<DataFileRequest> ret = new ArrayList<DataFileRequest>();
        DataFileDao dataFileDao = new DataFileDao();
        boolean setAsDefault = info.jobRequest.getDataFileIds().size() == 1;
        for (Integer id : info.jobRequest.getDataFileIds()) {
            int version = 0;
            DataFile dataFile = dataFileDao.findById(id);
            if (dataFile != null) {
                int numLinesPerAgent = (int) Math.floor(getNumberOfLines(id) / info.numberOfMachines);
                int offset = info.agentData.size() * numLinesPerAgent;
                DataFileRequest dataRequest = new DataFileRequest(dataFile.getPath(), setAsDefault,
                        DataFileUtil.getDataFileServiceUrl(dataFile.getId(), version, offset, numLinesPerAgent));
                ret.add(dataRequest);
            }
        }
        return ret.toArray(new DataFileRequest[0]);
    }

    private int getNumberOfLines(Integer dataFileId) {
        Integer ret = dataFileCountMap.get(dataFileId);
        if (ret == null) {
            synchronized (dataFileId) {
                ret = 0;
                DataFileDao dfd = new DataFileDao();
                DataFile dataFile = dfd.findById(dataFileId);
                if (dataFile != null) {
                    ret = DataFileUtil.getNumLines(dataFile);
                }
                dataFileCountMap.put(dataFileId, ret);
            }
        }
        return ret;
    }

    private static class JobInfo {
        public String scripts;
        private JobRequest jobRequest;
        private Set<AgentData> agentData = new HashSet<AgentData>();
        private Map<RegionRequest, Integer> userMap = new HashMap<RegionRequest, Integer>();
        private int numberOfMachines;

        public JobInfo(JobRequest jobRequest) {
            super();
            this.jobRequest = jobRequest;
            initializeUserMap(jobRequest);
            scripts = jobRequest.getScriptsXmlUrl();
        }

        public boolean isFilled() {
            boolean ret = true;
            for (Integer i : userMap.values()) {
                if (i != 0) {
                    ret = false;
                }
            }
            return ret;
        }

        public int getUsers(AgentData agent) {
            int ret = 0;
            VMRegion region = agent.getRegion();
            for (RegionRequest r : jobRequest.getRegions()) {
                if (Integer.parseInt(r.getUsers()) > 0) {
                    if (region == r.getRegion()) {
                        int numUsersRemaining = userMap.get(r);
                        if (agent.getCapacity() >= numUsersRemaining) {
                            ret = numUsersRemaining;
                        } else {
                            ret = agent.getCapacity();
                        }
                        userMap.put(r, numUsersRemaining - ret);
                        break;
                    }
                }
            }
            return ret;
        }

        private void initializeUserMap(JobRequest request) {
            for (RegionRequest r : request.getRegions()) {
                int numUsers = NumberUtils.toInt(r.getUsers());
                if (numUsers > 0) {
                    userMap.put(r, numUsers);
                    numberOfMachines += JobVmCalculator.getMachinesForAgent(numUsers, request.getNumUsersPerAgent());
                }
            }

        }

    }
}
