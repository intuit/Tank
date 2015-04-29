package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.intuit.tank.AgentServiceClient;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;

public class APITestHarness {

    private static Logger LOG = Logger.getLogger(APITestHarness.class);
    public static final int POLL_INTERVAL = 15000;
    private static final int RETRY_SLEEP = 2000;
    private static final int MAX_RETRIES = 10;

    private static APITestHarness instance;

    private AgentRunData agentRunData;

    private static final int BATCH_SIZE = 100;
    private String testPlans = "";
    private String instanceId;
    private List<String> testPlanXmls = null;
    private ThreadGroup threadGroup = new ThreadGroup("Test Runner Group");
    private int currentNumThreads = 0;
    private long startTime = 0;
    private int capacity = -1;
    private boolean DEBUG = false;

    private boolean logTiming = true;
    private boolean isLocal = true;
    private boolean started = false;
    private WatsAgentCommand cmd = WatsAgentCommand.run;
    private Thread[] sessionThreads;
    private CountDownLatch doneSignal;
    private boolean loggedSimTime;
    private int currentUsers = 0;
    private Vector<TankResult> results = new Vector<TankResult>();
    private ValidationStatus validationFailures;
    private TankConfig tankConfig;
    private UserTracker userTracker = new UserTracker();
    private FlowController flowControllerTemplate;
    private Map<Long, FlowController> controllerMap = new HashMap<Long, FlowController>();
    private TPSMonitor tpsMonitor;
    private ResultsReporter resultsReporter;

    static {
        try {
            java.security.Security.setProperty("networkaddress.cache.ttl", "0");
        } catch (Throwable e1) {
            LOG.warn(LogUtil.getLogMessage("Error setting dns timeout: " + e1.toString(), LogEventType.System));
        }
        try {
            System.setProperty("jsse.enableSNIExtension", "false");
        } catch (Throwable e1) {
            LOG.warn(LogUtil.getLogMessage("Error disabling SNI extension: " + e1.toString(), LogEventType.System));
        }
        try {
            System.setProperty("jdk.certpath.disabledAlgorithms", "");
        } catch (Throwable e1) {
            System.err.println("Error setting property jdk.certpath.disabledAlgorithms: " + e1.toString());
            e1.printStackTrace();
        }
        try {
            AgentUtil.setTrustCerts();
        } catch (Throwable e1) {
            LOG.warn(LogUtil.getLogMessage("Error setting trust all cert checker: " + e1.toString(),
                    LogEventType.System));
        }
    }

    /**
     * 
     * @return
     */
    public static APITestHarness getInstance() {
        if (instance == null) {
            instance = new APITestHarness();
        }
        return instance;
    }

    public static void destroyCurrentInstance() {
        instance = null;
    }

    private APITestHarness() {
        tankConfig = new TankConfig();
        resultsReporter = ReportingFactory.getResultsReporter();
        validationFailures = new ValidationStatus();
        setFlowControllerTemplate(new DefaultFlowController());
        agentRunData = new AgentRunData();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // set ttl on dns to small value
        try {
            java.security.Security.setProperty("networkaddress.cache.ttl", "0");
        } catch (Throwable e1) {
            LOG.warn(LogUtil.getLogMessage("Error setting dns timeout: " + e1.toString(), LogEventType.System));
        }
        try {
            System.setProperty("jsse.enableSNIExtension", "false");
        } catch (Throwable e1) {
            LOG.warn(LogUtil.getLogMessage("Error disabling SNI extension: " + e1.toString(), LogEventType.System));
        }
        AgentUtil.setTrustCerts();
        if (args.length < 1) {
            usage();
            return;
        }
        getInstance().initializeFromArgs(args);

    }

    private void initializeFromArgs(String[] args) {
        String controllerBase = null;
        for (int iter = 0; iter < args.length; ++iter) {
            String argument = args[iter];
            LOG.info("checking arg " + argument);

            String[] values = argument.split("=");
            if (values[0].equalsIgnoreCase("-tp")) {
                testPlans = values[1];
                if (!AgentUtil.validateTestPlans(testPlans)) {
                    return;
                }
                continue;
            } else if (values[0].equalsIgnoreCase("-ramp")) {
                agentRunData.setRampTime(Long.parseLong(values[1]) * 60000);
                // agentRunData.setRampT
                continue;
            } else if (values[0].equalsIgnoreCase("-d")) {
                Logger.getLogger("com.intuit.tank.http").setLevel(Level.DEBUG);
                Logger.getLogger("com.intuit.tank").setLevel(Level.DEBUG);
                DEBUG = true;
                agentRunData.setActiveProfile(LoggingProfile.VERBOSE);
                setFlowControllerTemplate(new DebugFlowController());
                continue;
            } else if (values[0].equalsIgnoreCase("-local")) {
                isLocal = true;
                continue;
            } else if (values[0].equalsIgnoreCase("-instanceId")) {
                instanceId = values[1];
                continue;
            } else if (values[0].equalsIgnoreCase("-logging")) {
                agentRunData.setActiveProfile(LoggingProfile.fromString(values[1]));
                continue;
            } else if (values[0].equalsIgnoreCase("-users")) {
                agentRunData.setNumUsers(Integer.parseInt(values[1]));
                continue;
            } else if (values[0].equalsIgnoreCase("-capacity")) {
                capacity = Integer.parseInt(values[1]);
                continue;
            } else if (values[0].equalsIgnoreCase("-start")) {
                agentRunData.setNumStartUsers(Integer.parseInt(values[1]));
                continue;
            } else if (values[0].equalsIgnoreCase("-jobId")) {
                agentRunData.setJobId(values[1]);
                continue;
            } else if (values[0].equalsIgnoreCase("-stopBehavior")) {
                agentRunData.setStopBehavior(StopBehavior.fromString(values[1]));
                continue;
            } else if (values[0].equalsIgnoreCase("-http")) {
                controllerBase = (values.length > 1 ? values[1] : null);
                continue;
            } else if (values[0].equalsIgnoreCase("-time")) {
                agentRunData.setSimulationTime(Integer.parseInt(values[1]) * 60000);
                continue;
            }

        }
        if (instanceId == null) {
            try {
                instanceId = AmazonUtil.getInstanceId();
                if (instanceId == null) {
                    instanceId = getLocalInstanceId();
                }
            } catch (Exception e) {
                instanceId = getLocalInstanceId();
            }
        }
        if (agentRunData.getActiveProfile() == null && !isLocal) {
            agentRunData.setActiveProfile(AmazonUtil.getLoggingProfile());
        }
        agentRunData.setMachineName(instanceId);

        if (controllerBase != null) {
            startHttp(controllerBase);
        } else {
            TestPlanSingleton plans = TestPlanSingleton.getInstance();
            if (null == testPlanXmls) {
                plans.setTestPlans(testPlans);
            } else {
                plans.setTestPlans(testPlanXmls);
            }
            runConcurrentTestPlans();
        }
    }

    private String getLocalInstanceId() {
        isLocal = true;
        String iId = "local-instance";
        try {
            iId = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            // cannot determine instanceid
        }
        return iId;
    }

    /**
     * Display usage error text
     */
    private static void usage() {
        System.out.println("API Test Harness Usage:");
        System.out.println("java -jar apiharness-1.0-all.jar -tp=<test plan file>");
        System.out.println("-tp=<file name>:  The test plan file to execute");
        System.out.println("-ramp=<time>:  The time (min) to get to the ideal concurrent users specified");
        System.out.println("-time=<time>:  The time (min) of the simulation");
        System.out.println("-users=<# of total users>:  The number of total users to run concurrently");
        System.out
                .println("-start=<# of users to start with>:  The number of users to run concurrently when test begins");
        System.out.println("-http=<controller_base_url>:  The url of the controller to get test info from");
        System.out.println("-d:  Turns debug on to step through each request");
    }

    private void startHttp(String baseUrl) {
        isLocal = false;
        CommandListener.startHttpServer(tankConfig.getAgentConfig().getAgentPort());
        if (baseUrl == null) {
            baseUrl = AmazonUtil.getControllerBaseUrl();
        }
        AgentServiceClient client = new AgentServiceClient(baseUrl);
        String instanceUrl = null;
        int tries = 0;
        while (instanceUrl == null) {
            try {
                instanceUrl = "http://" + AmazonUtil.getPublicHostName() + ":"
                        + tankConfig.getAgentConfig().getAgentPort();
            } catch (IOException e1) {
                tries++;
                if (tries < 10) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } else {
                    LOG.error("Error getting amazon host. maybe local.");
                    String publicIp = new HostInfo().getPublicIp();
                    if (!publicIp.equals(HostInfo.UNKNOWN)) {
                        instanceUrl = "http://" + publicIp + ":"
                                + tankConfig.getAgentConfig().getAgentPort();
                    } else {
                        instanceUrl = "http://localhost:" + tankConfig.getAgentConfig().getAgentPort();
                    }
                }
            }
        }
        if (capacity < 0) {
            capacity = AmazonUtil.getCapacity();
        }
        VMRegion region = VMRegion.US_EAST;
        if (AmazonUtil.isInAmazon()) {
            try {
                region = AmazonUtil.getVMRegion();
            } catch (IOException e) {
                LOG.warn(LogUtil.getLogMessage("Error getting region. using CUSTOM...", LogEventType.System));
            }
        }
        if (agentRunData.getJobId() == null) {
            agentRunData.setJobId(AmazonUtil.getJobId());
            agentRunData.setStopBehavior(AmazonUtil.getStopBehavior());
        }

        LogUtil.getLogEvent().setJobId(agentRunData.getJobId());
        LOG.info(LogUtil.getLogMessage("Active Profile" + agentRunData.getActiveProfile().getDisplayName()));
        AgentData data = new AgentData(agentRunData.getJobId(), instanceId, instanceUrl, capacity,
                region, AmazonUtil.getZone());
        try {
            AgentTestStartData startData = null;
            int count = 0;
            LOG.info(LogUtil.getLogMessage("Sending AgentData to controller: " + data.toString(), LogEventType.System));
            while (count < 10) {
                try {
                    startData = client.agentReady(data);
                    break;
                } catch (Exception e) {
                    LOG.error("Error sending ready: " + e, e);
                    count++;
                }
            }
            writeXmlToFile(startData.getScriptUrl());

            agentRunData.setNumUsers(startData.getConcurrentUsers());
            agentRunData.setNumStartUsers(startData.getStartUsers());
            agentRunData.setRampTime(startData.getRampTime());
            agentRunData.setJobId(startData.getJobId());
            agentRunData.setUserInterval(startData.getUserIntervalIncrement());
            agentRunData.setSimulationTime(startData.getSimulationTime());
            agentRunData.setAgentInstanceNum(startData.getAgentInstanceNum());
            agentRunData.setTotalAgents(startData.getTotalAgents());

            if (startData.getDataFiles() != null) {
                for (DataFileRequest dfRequest : startData.getDataFiles()) {
                    saveDataFile(dfRequest);
                }
            }
            Thread t = new Thread(new StartedChecker());
            t.setName("StartedChecker");
            t.setDaemon(false);
            t.start();
        } catch (Exception e) {
            LOG.error("Error communicating with controller: " + e, e);
            System.exit(0);
        }
    }

    /**
     * @throws IOException
     * 
     */
    public void writeXmlToFile(String scriptUrl) throws IOException {
        File f = new File("script.xml");
        LOG.info(LogUtil.getLogMessage("Writing xml to " + f.getAbsolutePath()));
        Writer out = null;
        InputStream is = null;
        int count = 0;

        while (count++ < MAX_RETRIES) {
            try {
                if (f.exists()) {
                    f.delete();
                    f = new File("script.xml");
                }
                URL url = new URL(scriptUrl);
                LOG.info("Downloading file from url " + scriptUrl + " to file " + f.getAbsolutePath());
                is = url.openStream();
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(f), "UTF8"));
                IOUtils.copy(is, out);
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(out);
                String scriptXML = FileUtils.readFileToString(f, "UTF-8");
                List<String> tps = new ArrayList<String>();
                tps.add(scriptXML);
                setTestPlans(tps);
                TestPlanSingleton.getInstance().setTestPlans(tps);
                break;
            } catch (Exception e) {
                if (count < MAX_RETRIES) {
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(out);
                    LOG.warn(LogUtil.getLogMessage("Failed to download script file because of: " + e.toString()
                            + ". Will try "
                            + (MAX_RETRIES - count) + " more times.", LogEventType.System));
                    try {
                        Thread.sleep(RETRY_SLEEP);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                } else {
                    LOG.error(LogUtil.getLogMessage("Error writing script file: " + e, LogEventType.IO), e);
                    throw new RuntimeException(e);
                }
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(out);
            }
        }
    }

    private void saveDataFile(DataFileRequest dataFileRequest) {
        String dataFileDirPath = new TankConfig().getAgentConfig().getAgentDataFileStorageDir();
        File dataFileDir = new File(dataFileDirPath);
        if (!dataFileDir.exists()) {
            if (!dataFileDir.mkdirs()) {
                throw new RuntimeException("Cannot create data file dir " + dataFileDirPath);
            }
        }
        File dataFile = new File(dataFileDir, dataFileRequest.getFileName());
        FileOutputStream fos = null;
        InputStream is = null;
        int count = 0;
        while (count++ < MAX_RETRIES) {
            try {
                URL url = new URL(dataFileRequest.getFileUrl());
                LOG.info(LogUtil.getLogMessage(
                        "writing file " + dataFileRequest.getFileName() + " to " + dataFile.getAbsolutePath()
                                + " from url " + url.toExternalForm(), LogEventType.System));
                is = url.openStream();
                fos = new FileOutputStream(dataFile);
                IOUtils.copy(is, fos);
                IOUtils.closeQuietly(fos);
                if (dataFileRequest.isDefault()
                        && !dataFileRequest.getFileName().equals(TankConstants.DEFAULT_CSV_FILE_NAME)) {
                    File defaultFile = new File(dataFileDir, TankConstants.DEFAULT_CSV_FILE_NAME);
                    LOG.debug("Copying default  file " + dataFile.getAbsolutePath() + " to "
                            + defaultFile.getAbsolutePath());

                    FileUtils.copyFile(dataFile, defaultFile);
                }
                break;
            } catch (Exception e) {
                if (count < MAX_RETRIES) {
                    LOG.warn(LogUtil.getLogMessage("Failed to download CSV file because of: " + e.toString()
                            + ". Will try "
                            + (MAX_RETRIES - count) + " more times.", LogEventType.System));
                    try {
                        Thread.sleep(RETRY_SLEEP);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                } else {
                    LOG.error(LogUtil.getLogMessage("Error downloading csv file: " + e, LogEventType.IO), e);
                    throw new RuntimeException(e);
                }
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(fos);
            }
        }

    }

    /**
     * 
     */
    public synchronized void addKill() {
        validationFailures.addKill();
    }

    /**
     * 
     */
    public synchronized void addAbort() {
        validationFailures.addAbort();
    }

    /**
     * 
     */
    public synchronized void addGoto() {
        validationFailures.addGoto();
    }

    /**
     *
     */
    public synchronized void addSkip() {
        validationFailures.addSkip();
    }

    /**
     *
     */
    public synchronized void addSkipGroup() {
        validationFailures.addSkipGroup();
    }

    /**
     *
     */
    public synchronized void addRestart() {
        validationFailures.addRestart();
    }

    public UserTracker getUserTracker() {
        return userTracker;
    }

    /**
     * Run concurrent test plans at the same time
     * 
     * @param parser
     */
    public void runConcurrentTestPlans() {
        if (started) {
            LOG.warn("Agent already started. Ignoring start command");
            return;
        }
        tpsMonitor = new TPSMonitor(tankConfig.getAgentConfig().getTPSPeriod());
        StringBuilder info = new StringBuilder().append(" RAMP_TIME=").append(agentRunData.getRampTime())
                .append("; agentRunData.getNumUsers()=").append(agentRunData.getNumUsers())
                .append("; NUM_START_THREADS=").append(agentRunData.getNumStartUsers())
                .append("; simulationTime=").append(agentRunData.getSimulationTime());
        LOG.info(LogUtil.getLogMessage("starting test with " + info.toString()));
        started = true;

        if (agentRunData.getJobId() == null) {
            String jobId = AmazonUtil.getJobId();
            agentRunData.setJobId(jobId);
        }

        TestPlanRunner[] sessions = new TestPlanRunner[agentRunData.getNumUsers()];
        sessionThreads = new Thread[agentRunData.getNumUsers()];
        Thread monitorThread = null;
        doneSignal = new CountDownLatch(agentRunData.getNumUsers());
        try {
            HDWorkload hdWorkload = TestPlanSingleton.getInstance().getTestPlans().get(0);
            agentRunData.setProjectName(hdWorkload.getName());
            List<TestPlanStarter> testPlans = new ArrayList<TestPlanStarter>();
            int total = 0;
            for (HDTestPlan plan : hdWorkload.getPlans()) {
                plan.setVariables(hdWorkload.getVariables());
                TestPlanStarter starter = new TestPlanStarter(plan, agentRunData.getNumUsers());
                total += starter.getNumThreads();
                testPlans.add(starter);
                LOG.info(LogUtil.getLogMessage("Users for Test Plan " + plan.getTestPlanName() + " at "
                        + plan.getUserPercentage()
                        + "% = " + starter.getNumThreads()));
            }
            LOG.info(LogUtil.getLogMessage("Total Users calculated for all test Plans = " + total));
            if (total != agentRunData.getNumUsers()) {
                int numToAdd = agentRunData.getNumUsers() - total;
                TestPlanStarter starter = testPlans.get(testPlans.size() - 1);
                LOG.info(LogUtil.getLogMessage("adding " + numToAdd + " threads to testPlan "
                        + starter.getPlan().getTestPlanName()));
                starter.setNumThreads(starter.getNumThreads() + numToAdd);
            }

            // create threads
            int tp = 0;
            for (TestPlanStarter starter : testPlans) {
                for (int i = 0; i < starter.getNumThreads(); i++) {
                    sessions[tp] = new TestPlanRunner(starter.getPlan(), tp);
                    sessionThreads[tp] = new Thread(threadGroup, sessions[tp], "AGENT");
                    sessionThreads[tp].setDaemon(true);// system won't shut down normally until all user threads stop
                    starter.addThread(sessionThreads[tp]);
                    sessions[tp].setUniqueName(
                            sessionThreads[tp].getThreadGroup().getName() + "-" +
                                    sessionThreads[tp].getId());
                    tp++;
                }
            }

            // start status thread first only
            if (!isLocal && !isDebug() && NumberUtils.isDigits(agentRunData.getJobId())) {
                CloudVmStatus status = getInitialStatus();
                monitorThread = new Thread(new APIMonitor(status));
                monitorThread.setDaemon(true);
                monitorThread.setPriority(Thread.NORM_PRIORITY - 2);
                monitorThread.start();
            }

            // start initial users
            startTime = System.currentTimeMillis();
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            LOG.info(LogUtil.getLogMessage("Simulation start: " + df.format(new Date(getStartTime()))));
            if (agentRunData.getSimulationTime() != 0) {
                LOG.info(LogUtil.getLogMessage("Scheduled Simulation End : "
                        + df.format(new Date(getSimulationEndTimeMillis()))));
                LOG.info(LogUtil.getLogMessage("Max Simulation End : "
                        + df.format(new Date(getMaxSimulationEndTimeMillis()))));
            } else {
                LOG.info(LogUtil.getLogMessage("Ends at script loops completed with no Max Simulation Time."));
            }
            currentNumThreads = 0;
            if (agentRunData.getNumUsers() > 0) {
                for (TestPlanStarter starter : testPlans) {
                    if (isDebug()) {
                        starter.run();
                    } else {
                        Thread t = new Thread(starter);
                        t.setDaemon(true);
                        t.start();
                    }
                }
                boolean ramping = true;
                while (ramping) {
                    boolean done = true;
                    for (TestPlanStarter starter : testPlans) {
                        done = done && starter.isDone();
                    }
                    ramping = !done;
                    if (ramping) {
                        Thread.sleep(5000);
                    }
                }
                // if we broke early, fix our countdown latch
                int numToCount = 0;
                for (TestPlanStarter starter : testPlans) {
                    numToCount += starter.getThreadsStarted();
                }
                while (numToCount < agentRunData.getNumUsers()) {
                    doneSignal.countDown();
                    numToCount++;
                }
                // wait for them to finish
                LOG.info(LogUtil.getLogMessage("Ramp Complete..."));

                doneSignal.await();
            }
        } catch (Exception t) {
            LOG.error(LogUtil.getLogMessage(t.getMessage()), t);
        } finally {
            LOG.info(LogUtil.getLogMessage("Test Complete..."));
            if (!isDebug() && NumberUtils.isDigits(agentRunData.getJobId())) {
                if (null != monitorThread) {
                    APIMonitor.setJobStatus(JobStatus.Completed);
                    APIMonitor.setDoMonitor(false);
                }
                sendBatchToDB(false);
                // sleep for 60 seconds to let wily agent clear any data
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    // nothing to do
                }
            }
        }
        flowControllerTemplate.endTest();
        // System.exit(0);
    }

    public TPSMonitor getTPMonitor() {
        return tpsMonitor;
    }

    /**
     * @return
     * @throws IOException
     */
    public CloudVmStatus getInitialStatus() {
        CloudVmStatus status = null;
        try {
            VMRegion region = VMRegion.US_EAST;
            String secGroups = "unknown";
            if (AmazonUtil.isInAmazon()) {
                try {
                    region = AmazonUtil.getVMRegion();
                    secGroups = AmazonUtil.getMetaData(CloudMetaDataType.security_groups);
                } catch (IOException e) {
                    LOG.warn(LogUtil.getLogMessage("Error gettting region. using Custom...", LogEventType.System));
                }
            }
            status = new CloudVmStatus(instanceId, agentRunData.getJobId(), secGroups, JobStatus.Unknown,
                    VMImageType.AGENT, region, VMStatus.running,
                    new ValidationStatus(), 0, 0, new Date(), null);
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage("Error creating intial status."));
            status = new CloudVmStatus(instanceId, "unknown", "wats-dev", JobStatus.Unknown,
                    VMImageType.AGENT, VMRegion.US_EAST, VMStatus.running,
                    new ValidationStatus(), 0, 0, new Date(), null);
        }
        status.setUserDetails(userTracker.getSnapshot());
        return status;
    }

    synchronized public void threadComplete() {
        currentUsers--;
        doneSignal.countDown();
        long count = doneSignal.getCount();
        // numCompletedThreads = (int) (agentRunData.getNumUsers() - count);
        if (isDebug() || count < 10) {
            LOG.info(LogUtil
                    .getLogMessage("User thread finished... Remaining->" + currentUsers, LogEventType.System));
        }
    }

    public WatsAgentStatusResponse getStats() {
        int ramp = (agentRunData.getNumUsers() - agentRunData.getNumStartUsers());

        if (ramp > 0) {
            ramp = (int) (agentRunData.getRampTime() - (agentRunData.getRampTime() * currentNumThreads) /
                    (agentRunData.getNumUsers() - agentRunData.getNumStartUsers()));
        }
        return new WatsAgentStatusResponse(System.currentTimeMillis() - startTime,
                validationFailures.getValidationKills(),
                validationFailures.getValidationAborts(),
                validationFailures.getValidationGotos(),
                validationFailures.getValidationSkips(),
                validationFailures.getValidationSkipGroups(),
                validationFailures.getValidationRestarts(),
                currentUsers, agentRunData.getNumUsers(), ramp);
    }

    public synchronized void threadStarted() {
        currentNumThreads++;
        currentUsers++;
    }

    public void setTestPlans(List<String> scripts) {
        testPlanXmls = scripts;
        for (String script : scripts) {
            LOG.debug(script);
        }
    }

    public boolean isDebug() {
        return DEBUG;
    }

    /**
     * @return the instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    public long getSimulationEndTimeMillis() {
        return getStartTime() + agentRunData.getSimulationTime();
    }

    public boolean hasMetSimulationTime() {
        boolean ret = false;
        if (agentRunData.getSimulationTime() > 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime > getSimulationEndTimeMillis()) {
                if (!loggedSimTime) {
                    LOG.info(LogUtil.getLogMessage("Simulation time met", LogEventType.System));
                    loggedSimTime = true;
                }
                ret = true;
            }
        }
        return ret;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getMaxSimulationEndTimeMillis() {
        return getSimulationEndTimeMillis() + tankConfig.getAgentConfig().getOverSimulationMaxTime();
    }

    /**
     * check the agent threads if simulation time has been met.
     */
    public void checkAgentThreads() {
        int activeCount = threadGroup.activeCount();
        Thread[] threads = new Thread[activeCount];
        threadGroup.enumerate(threads);
        int activeThreads = 0;
        for (Thread t : threads) {
            if (t != null) {
                if (t.getState() == Thread.State.TIMED_WAITING || t.getState() == Thread.State.WAITING) {
                    activeThreads++;
                }
            }
        }
        LOG.info(LogUtil.getLogMessage("Have " + activeThreads + " of " + activeCount
                + " active Threads in thread group "
                + threadGroup.getName(), LogEventType.System));
        if (agentRunData.getSimulationTime() != 0 && hasMetSimulationTime() && doneSignal.getCount() != 0) {
            boolean exceededTimeLimit = System.currentTimeMillis() > getMaxSimulationEndTimeMillis();
            if (exceededTimeLimit) {
                LOG.info(LogUtil.getLogMessage("Max simulation time has been met and there are "
                        + doneSignal.getCount() + " threads not reporting done."));
                for (Thread t : sessionThreads) {
                    if (t.isAlive()) {
                        if (exceededTimeLimit) {
                            LOG.warn(LogUtil.getLogMessage("thread " + t.getName() + '-' + t.getId()
                                    + " is still running with a State of " + t.getState().name(), LogEventType.System));
                            t.interrupt();
                            doneSignal.countDown();
                        }
                    }
                }
            }
        }
    }

    /**
     * @param cmd
     */
    public void setCommand(WatsAgentCommand newCommand) {
        if (cmd != WatsAgentCommand.stop) {
            cmd = newCommand;
            LOG.info(LogUtil.getLogMessage("Got new Command: " + newCommand + " with " + currentNumThreads
                    + " User Threads running.", LogEventType.System));
            if (cmd == WatsAgentCommand.stop || cmd == WatsAgentCommand.pause || cmd == WatsAgentCommand.pause_ramp) {
                APIMonitor.setJobStatus(cmd == WatsAgentCommand.stop ? JobStatus.Stopped
                        : cmd == WatsAgentCommand.pause ? JobStatus.Paused : JobStatus.RampPaused);
                if (cmd == WatsAgentCommand.pause) {
                    for (Thread t : sessionThreads) {
                        t.interrupt();
                    }
                }
            } else if (cmd == WatsAgentCommand.resume_ramp || cmd == WatsAgentCommand.run) {
                APIMonitor.setJobStatus(JobStatus.Running);
            }

        }
    }

    /**
     * @return the cmd
     */
    public WatsAgentCommand getCmd() {
        return cmd;
    }

    public void queueTimingResult(TankResult result) {
        if (logTiming) {
            results.add(result);
            if (results.size() >= BATCH_SIZE) {
                sendBatchToDB(true);
            }
        }
    }

    private void sendBatchToDB(boolean asynch) {
        if (results.size() != 0 && logTiming) {
            final List<TankResult> l = new ArrayList<TankResult>();
            synchronized (results) {
                // logger.info("sendBatchToDB(" + tableName + "); sending " + results.size() + " results.");
                l.addAll(results);
                results.clear();
            }
            resultsReporter.sendTimingResults(getAgentRunData().getJobId(), getAgentRunData().getInstanceId(), l, false);
        }
    }

    /**
     * @return
     */
    public TankConfig getTankConfig() {
        return tankConfig;
    }

    /**
     * @return the flowControllerTemplate
     */
    public FlowController getFlowControllerTemplate() {
        return flowControllerTemplate;
    }

    /**
     * @param flowControllerTemplate
     *            the flowControllerTemplate to set
     */
    public void setFlowControllerTemplate(FlowController flowControllerTemplate) {
        this.flowControllerTemplate = flowControllerTemplate;
    }

    public FlowController getFlowController(Long threadId) {
        FlowController ret = controllerMap.get(threadId);
        if (ret == null) {
            ret = flowControllerTemplate.cloneController();
            controllerMap.put(threadId, ret);
        }
        return ret;
    }

    /**
     * @return the started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * @return the agentRunData
     */
    public AgentRunData getAgentRunData() {
        return agentRunData;
    }

    public void setDebug(boolean b) {
        DEBUG = true;
    }

    /**
     * @return the resultsReporter
     */
    public ResultsReporter getResultsReporter() {
        return resultsReporter;
    }

}
