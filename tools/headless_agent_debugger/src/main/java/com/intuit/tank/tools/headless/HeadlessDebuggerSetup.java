/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.tools.headless;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.TestPlanSingleton;
import com.intuit.tank.harness.data.*;
import com.intuit.tank.harness.functions.JexlIOFunctions;
import com.intuit.tank.harness.functions.JexlStringFunctions;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.clients.ProjectClient;
import com.intuit.tank.projects.models.KeyPair;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HeadlessDebuggerSetup implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HeadlessDebuggerSetup.class);
    private HDWorkload currentWorkload;
    private HDTestPlan currentTestPlan;
    private final Map<String, String> projectVariables = new HashMap<String, String>();
    private final List<DebugStep> steps = new ArrayList<DebugStep>();
    private final List<StepListener> stepChangedListeners = new ArrayList<StepListener>();
    private final List<ScriptChangedListener> scriptChangedListeners = new ArrayList<ScriptChangedListener>();
    private int currentRunningStep;
    private AutomationFlowController flowController;
    private APITestHarness harness;
    private static File workingDir;
    private Thread runningThread;
    private VariablesOutput variablesOutput;
    private static final String HEADERS_PATH = "v2/agent/headers";
    private static final char NEWLINE = '\n';
    private ProjectClient projectClient;
    private int executedStepCounter;
    private int projectId;
    private String username;
    private String authId;
    private String srsVersion;
    private boolean filingStatus;
    private boolean taxhubFlow;
    private ValidationReportData reportData;

    public HeadlessDebuggerSetup(String serviceUrl, Integer projectId, String token) {
        try {
            workingDir = createWorkingDir(serviceUrl, token);
            variablesOutput = new VariablesOutput(this);
            this.projectClient = new ProjectClient(serviceUrl, token);
            this.projectId = projectId;

            setProject(projectId); // set project to step through
            if(currentWorkload == null) {
                LOG.error("Error retrieving project scripts for project with projectId: " + projectId);
                throw new RuntimeException("Error: Empty or Invalid Test Plan");
            }
        } catch (Exception e) {
            LOG.error("Error setting up project: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3) {
            usage();
            return;
        }

        LOG.info("Headless Agent Debugger - STARTING EXECUTION");

        String url = null;
        String token = "";
        int projectId = -1;

        for (String argument : args) {
            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "checking arg " + argument)));

            String[] values = argument.split("=");
            if (values[0].equalsIgnoreCase("-p")) {
                projectId = Integer.parseInt(values[1]);
            } else if(values[0].equalsIgnoreCase("-h")) {
                url = values[1];
            } else if(values[0].equalsIgnoreCase("-t")) {
                token = values[1];
            }
        }

        if (url == null || projectId == -1) {
            usage();
            return;
        }

        HeadlessDebuggerSetup automationRunner = new HeadlessDebuggerSetup(url, projectId, token);

        try {
            automationRunner.start();
            automationRunner.getRunningThread().join(); // wait for runConcurrentTestPlans to finish
        } catch (Exception e) {
            LOG.error("Error running headless debugger: " + e.getMessage(), e);
        } finally {
            automationRunner.stop();
            automationRunner.quit();
        }
    }

    private static void usage() {
        System.out.println("Headless Agent Debugger");
        System.out.println("java -jar Headless-Debugger-all.jar -h=<controller_base_url> -p=<projectId> -t=<token>");
        System.out.println("-h=<controller_base_url>: The url of the controller to get test info from");
        System.out.println("-p=<projectId>:  The projectId of the Tank project with test plan file to execute");
        System.out.println("-t=<token>: Tank API token needed to connect to the Tank controller");
    }

    public void setProject(Integer projectId) throws IOException, InterruptedException {
            try {
                ProjectTO project = projectClient.getProject(projectId);
                if (project != null) {
                    try {
                        String scriptXml = projectClient.downloadTestScriptForProject(project
                                .getId());
                        setProjectVariables(project);
                        setFromString(scriptXml);
                    } catch (Exception e1) {
                        LOG.error("Error downloading project script: " + e1, e1);
                        throw e1;
                    }
                } else {
                    LOG.error("Error: Project Not Found");
                    throw new RuntimeException("Error: Project Not Found");
                }
            } catch (InterruptedException | IOException e1) {
                LOG.error("Error: Empty or Invalid Tank URL");
                throw e1;
            } catch (Exception e2) {
                LOG.error("Error setting project: " + e2);
                throw new RuntimeException("Error setting project: " + e2);
            }
    }

    /**
     * @return the steps
     */
    public List<DebugStep> getSteps() {
        return steps;
    }

    public void addStepChangedListener(StepListener l) {
        stepChangedListeners.add(l);
    }

    public void addScriptChangedListener(ScriptChangedListener l) {
        scriptChangedListeners.add(l);
    }


    /**
     * @return the projectVariables
     */
    public Map<String, String> getProjectVariables() {
        return projectVariables;
    }

    /**
     * @param projectVariables
     *            the projectVariables to set
     */
    public void setProjectVariables(Map<String, String> projectVariables) {
        this.projectVariables.clear();
        if (projectVariables != null) {
            this.projectVariables.putAll(projectVariables);
        }
    }

    /**
     * @return the currentWorkload
     */
    public HDWorkload getCurrentWorkload() {
        return currentWorkload;
    }

    /**
     * Attempts to delete the working dir and exits program
     */
    public void quit() {
        LOG.info("Headless Agent Debugger - FINISHED EXECUTION");
        double executedPercentage = ((double) executedStepCounter / steps.size()) * 100;
        int roundedPercentage = (int) Math.round(executedPercentage);
        LOG.info("validation_summary");

        // get project and project_id
        Pattern pattern = Pattern.compile("project\\s+(.+)\\s+\\(id(\\d+)\\)");
        Matcher matcher = pattern.matcher(getCurrentWorkload().getName());

        if (matcher.find()) {
            String projectName = matcher.group(1);  // get PROJECT_NAME
            String projectId = matcher.group(2);  // get PROJECT_ID

            LOG.info("project," + projectName);
            LOG.info("project_id," + projectId);
        } else {
            LOG.info("project," + getCurrentWorkload().getName());
            LOG.info("project_id," + projectId);
        }
        LOG.info("authid," + authId);
        LOG.info("username," + username);
        LOG.info("srsversion," + srsVersion);
        LOG.info("total_steps," + steps.size());
        LOG.info("total_executed," + executedStepCounter);
        LOG.info("execution_percentage," + roundedPercentage);
        LOG.info("taxhub_flow," + taxhubFlow);
        if(!getCurrentWorkload().getName().contains("UC2") || !getCurrentWorkload().getName().contains("UC7")){
            LOG.info("filing_status," + filingStatus);
        }

        // timestamp
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        LOG.info("timestamp," + now.format(formatter));

        // Write rich JSON report for external HTML report generation
        if (reportData != null) {
            reportData.setFinalMetadata(authId, username, srsVersion, filingStatus, taxhubFlow);
            reportData.writeToFile(new File(System.getProperty("user.dir")));
        }

        if (workingDir.exists()) {
            try {
                FileUtils.deleteDirectory(workingDir);
            } catch (IOException e) {
                System.out.println("Error deleting directory " + workingDir.getAbsolutePath() + ": " + e);
            }
        }
        System.exit(0);
    }

    /**
     * @param currentWorkload
     *            the currentWorkload to set
     */
    public void setCurrentWorkload(HDWorkload currentWorkload) {
        setCurrentTestPlan(null);
        this.currentWorkload = currentWorkload;
        if (currentWorkload != null) {
            if (!currentWorkload.getPlans().isEmpty()) {
                setCurrentTestPlan(currentWorkload.getPlans().get(0));
            }
        } else {
            setCurrentTestPlan(null);
        }
    }

    /**
     * @return the currentTestPlan
     */
    public HDTestPlan getCurrentTestPlan() {
        return currentTestPlan;
    }

    /**
     * @return the currentRunningStep
     */
    public int getCurrentRunningStep() {
        return currentRunningStep;
    }

    public void setCurrentStep(final int stepIndex) {
        currentRunningStep = stepIndex;
        int stepToSet = Math.max(0, currentRunningStep);
    }

    /**
     * @param currentTestPlan
     *            the currentTestPlan to set
     */
    public void setCurrentTestPlan(HDTestPlan currentTestPlan) {
        this.currentTestPlan = currentTestPlan;
        steps.clear();
        currentRunningStep = -1;
        populateDebugStepsList(currentTestPlan);
        if (!steps.isEmpty()) {
            fireStepChanged(0);
        }
        fireScriptChanged();
    }

    private void populateDebugStepsList(HDTestPlan currentTestPlan) {
        if (currentTestPlan != null) {
            for (HDScriptGroup group : currentTestPlan.getGroup()) {
                for (HDScript script : group.getGroupSteps()) {
                    for (HDScriptUseCase useCase : script.getUseCase()) {
                        for (TestStep step : useCase.getScriptSteps()) {
                            steps.add(new DebugStep(step));
                        }
                    }
                }
            }
        }
    }

    private boolean hostnameReplacementCheck() {
        LOG.info("Headless Agent Debugger - PRE-VALIDATION CHECK: HOSTNAME REPLACEMENT");
        int stepCount = steps.size();
        LOG.info("Total Script Steps: " + stepCount);

        long replacedCount = steps.stream()
                .map(step -> step.getStepRun().getInfo())
                .filter(url -> url.contains("#{"))
                .count();

        double replacementPercentage = (double) replacedCount / stepCount * 100;
        LOG.info(String.format("Steps with hostname replacement: %d (%.2f%%)", replacedCount, replacementPercentage));

        if (replacementPercentage >= 95.0) {
            LOG.info("Hostname replacement check PASSED: At least 95% of steps have been replaced.");
            return true;
        } else {
            if(stepCount < 200) {
                LOG.info("Hostname replacement check PASSED: UC7 Script -> less than 200 steps.");
                return true;
            }
            LOG.info("Hostname replacement check FAILED: Less than 95% of steps have been replaced.");
            return false;
        }
    }

    private HDWorkload buildHDWorkload() {
        HDWorkload workload = new HDWorkload();
        workload.setName(currentWorkload.getName());
        workload.setDescription(currentWorkload.getDescription());
        if (currentWorkload.getVariables() != null) {
            HDTestVariables variables = new HDTestVariables(currentWorkload.getVariables().isAllowOverride());
            for (Entry<String, String> entry : this.projectVariables.entrySet()) {
                variables.addVariable(entry.getKey(), entry.getValue());
            }
            workload.setVariables(variables);
        }
        workload.getPlans().add(currentTestPlan);
        return workload;
    }

    public void setProjectVariables(ProjectTO project) {
        projectVariables.clear();
        for (KeyPair pair : project.getVariables()) {
            projectVariables.put(pair.getKey(), pair.getValue());
        }
    }

    void fireScriptChanged() {
        for (ScriptChangedListener l : scriptChangedListeners) {
            l.scriptChanged(currentTestPlan);
        }
    }

    void fireStepChanged(int stepIndex) {
        DebugStep step = null;
        if (stepIndex >= 0 && stepIndex < steps.size()) {
            step = steps.get(stepIndex);
        }
        for (StepListener l : stepChangedListeners) {
            l.stepChanged(step);
        }
    }

    void fireStepStarted(int stepIndex) {
        DebugStep step = null;
        if (stepIndex >= 0 && stepIndex < steps.size()) {
            step = steps.get(stepIndex);
        }
        for (StepListener l : stepChangedListeners) {
            l.stepEntered(step);
        }
    }

    void fireStepExited(int stepIndex) {
        DebugStep step = null;
        if (stepIndex >= 0 && stepIndex < steps.size()) {
            step = steps.get(stepIndex);
        }
        for (StepListener l : stepChangedListeners) {
            l.stepExited(step);
        }
    }

    public void next() {
        flowController.doNext();
    }

    public void stop() {
        try {
            if (harness != null) {
                harness.setCommand(AgentCommand.kill);
            }
            if (runningThread != null) {
                runningThread.interrupt();
            }
        } finally {
            testFinished();
            runningThread = null;
            harness = null;
        }
    }

    public Thread getRunningThread() {
        return runningThread;
    }

    public void start() {
        flowController = new AutomationFlowController(this);

        fireStepChanged(-1);
        if (!steps.isEmpty()) {
            steps.forEach(DebugStep::clear);
            setCurrentStep(-1);

            // start apiHarness and get the variables....
            try {
                createHarness();
                runningThread = new Thread(() -> harness.runConcurrentTestPlans());
                runningThread.start();
            } catch (Exception e) {
                LOG.error("Error starting test: " + e);
            }

        }

    }

    private void createHarness() {
        APITestHarness.destroyCurrentInstance();
        harness = APITestHarness.getInstance();
        harness.setFlowControllerTemplate(flowController);
        harness.setTankHttpClientClass(currentWorkload.getTankHttpClientClass());
        harness.getAgentRunData().setActiveProfile(LoggingProfile.VERBOSE);
        harness.getAgentRunData().setInstanceId("HeadlessDebuggerInstance");
        harness.getAgentRunData().setMachineName("headlessDebugger");
        harness.getAgentRunData().setNumUsers(1);
        harness.getAgentRunData().setJobId("headlessDebuggerJob");
        harness.getAgentRunData().setTotalAgents(1);
        harness.setDebug(true);

        TestPlanSingleton.getInstance().setTestPlan(buildHDWorkload());
        JexlIOFunctions.resetStatics();
        JexlStringFunctions.resetStatics();
    }

    public void setNextStep(TestStepContext context) {
        setCurrentStep(context.getTestStep().getStepIndex());
    }

    public void stepStarted(final TestStepContext context) {
        try {
            int stepIndex = context.getTestStep().getStepIndex();
            setCurrentStep(stepIndex);
            DebugStep debugStep = steps.get(currentRunningStep);
            if (debugStep != null) {
                debugStep.setEntryVariables(context.getVariables().getVariableValues());
                debugStep.setRequest(context.getRequest());
                debugStep.setResponse(context.getResponse());
            }
            fireStepChanged(stepIndex);
            fireStepStarted(stepIndex);
        } catch  (Exception e) {
            LOG.error("Error starting step: " + e.getMessage(), e);
        }
    }

    private void setAuthIdUsernameAndSRS(DebugStep debugStep) {
        final Map<String, String> entryVars = debugStep.getEntryVariables();
        this.authId = entryVars.get("authid");
        this.username = entryVars.get("username");
        this.srsVersion = entryVars.get("srsversion");
    }

    public void stepFinished(final TestStepContext context) { // important - step error checking
        boolean success_status = true;
        try {
            DebugStep debugStep = steps.get(currentRunningStep);
            if (debugStep != null) {
                debugStep.setExitVariables(context.getVariables().getVariableValues());
                debugStep.setRequest(context.getRequest());
                debugStep.setResponse(context.getResponse());
                setAuthIdUsernameAndSRS(debugStep);
            }

            try {
                StringBuilder sb = new StringBuilder();
                if(debugStep != null && debugStep.getStepRun() != null) {
                    sb.append(debugStep.getStepRun().getStepIndex()+1)
                            .append(",")
                            .append(debugStep.getRequest().getMethod())
                            .append(",")
                            .append(debugStep.getStepRun().getInfo())
                            .append(",");
                }

                if (context.getResponse() != null) {
                    if ((context.getResponse().getHttpCode() >= 400 || context.getResponse().getHttpCode() == -1) || !context.getErrors().isEmpty()) { // ERROR - log all step info
                        sb.append("FAILURE"); // set error status
                        success_status = false;
                    } else {
                        sb.append("SUCCESS"); // set success status
                    }
                } else {
                    sb.append("FAILURE"); // set error status
                    success_status = false;
                }

                stepExecuted();
                LOG.info(sb);

                if(debugStep != null && debugStep.getStepRun() != null) {
                    if(debugStep.getStepRun().getInfo().contains("filing-return")) {
                        LOG.info("filing-return response body: {}", debugStep.getResponse().getBody());

                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String responseBody = debugStep.getResponse().getBody();
                            JsonNode rootNode = objectMapper.readTree(responseBody);
                            JsonNode filingStatusNode = rootNode.path("data").path("transmitResponse").path("TransmitStatus");
                            if (filingStatusNode.asText().equals("success")) {
                                filingStatus = true;
                            }
                        } catch (Exception e) {
                            LOG.error("Error parsing JSON response body", e);
                        }
                    }
                    if(debugStep.getStepRun().getInfo().contains("taxhub")) {
                        taxhubFlow = true;
                    }
                }

                if(!success_status){
                    BaseRequest request = context.getRequest();
                    context.getRequest().logRequest(request.getRequestUrl(),
                                                    request.getBody(),
                                                    request.getMethod(),
                                                    request.getHeaderInformation(),
                                                    request.getCookies(),
                                                    true);
                    context.getResponse().logResponse(); // log full response
                    variablesOutput.displayVars(); // log variables
                }
                
                // Record step data for JSON report with full validation error details
                if (reportData != null && debugStep != null) {
                    reportData.recordStep(debugStep, success_status, context.getErrors());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            fireStepExited(context.getTestStep().getStepIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testFinished() {
        runningThread = null;
        harness = null;
    }

    public TestStep getStep(int stepIndex) {
        return steps.get(stepIndex).getStepRun();
    }

    public static File createWorkingDir(String baseUrl, String token) {
        try {
            File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
            temp.delete();
            temp = new File(temp.getAbsolutePath() + ".d");
            temp.mkdir();
            workingDir = temp;
            // create settings.xml
            writeSettings(workingDir);
            System.setProperty("WATS_PROPERTIES", workingDir.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("HeadlessDebugger - Error creating temp working dir: " + e);
        }
        return workingDir;
    }

    private static void writeSettings(File workingDir) throws IOException {
        StringBuilder s = new StringBuilder();
        s.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NEWLINE);
        s.append("<tank-settings>").append(NEWLINE);
        s.append("<agent-config>");

        // <!-- Where to store csv files on the agent -->
        s.append("<agent-data-file-storage>" + workingDir.getAbsolutePath() + "</agent-data-file-storage>");
        // <!-- Mime type rgex for logging of response body on error. -->
        s.append("<valid-mime-types>").append(NEWLINE);
        s.append("<mime-type-regex>.*text.*</mime-type-regex>").append(NEWLINE);
        s.append("<mime-type-regex>.*json.*</mime-type-regex>").append(NEWLINE);
        s.append("<mime-type-regex>.*xml.*</mime-type-regex>").append(NEWLINE);
        s.append("</valid-mime-types>").append(NEWLINE);
        s.append("</agent-config>");
        s.append("<logic-step>").append(NEWLINE);
        s.append("<insert-before><![CDATA[").append(NEWLINE);
        String js = IOUtils.toString(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("insert.js"), StandardCharsets.UTF_8);
        s.append(js).append(NEWLINE);
        s.append("]]>").append(NEWLINE);
        s.append("</insert-before>").append(NEWLINE);
        s.append("<append-after><![CDATA[").append(NEWLINE);
        s.append("]]>").append(NEWLINE);
        s.append("</append-after>").append(NEWLINE);
        s.append("</logic-step>").append(NEWLINE);

        s.append("</tank-settings>").append(NEWLINE);
        File settingsFile = new File(workingDir, "settings.xml");
        FileUtils.writeStringToFile(settingsFile, s.toString(), StandardCharsets.UTF_8);
        LOG.info("Writing settings file to " + settingsFile.getAbsolutePath());
    }

    private void setFromString(String scriptXml) {
        if (StringUtils.isBlank(scriptXml)) {
            setCurrentWorkload(null);
            LOG.error("ERROR - No workload found");
        } else {
            HDWorkload workload = unmarshalWorkload(scriptXml);
            if (workload != null) {
                setCurrentWorkload(workload);
                if(!hostnameReplacementCheck()) { // check if prod filters applied; < 95% of hostname should be replaced to pass
                    LOG.error("ERROR - script failed hostname replacement check and is not properly filtered, exiting...");
                    System.exit(1);
                }
                LOG.info("\n");
                LOG.info("------------------------------------------------------");
                LOG.info("Headless Agent Debugger - VALIDATION RESULTS");
                LOG.info("workload=" + workload.getName() + NEWLINE);
                LOG.info("total_steps=" + steps.size());
                LOG.info("format:step_number,method,path,validation_status");
                LOG.info("------------------------------------------------------");
                
                // Initialize rich JSON report data collector
                reportData = new ValidationReportData(workload.getName(), projectId);
            }
        }
    }

    private HDWorkload unmarshalWorkload(String xml) {
        try {
            return JaxbUtil.unmarshall(xml, HDWorkload.class);
        } catch (JAXBException | ParserConfigurationException | SAXException e) {
            LOG.error("Error unmarshalling xml: " + e.getMessage(), e);
        }
        return null;
    }

    public void stepExecuted() {
        executedStepCounter++;
    }

    /**
     * Records a skipped step (ThinkTime, SleepTime, or Logic step) to the JSON report.
     */
    public void recordSkippedStep(TestStep testStep) {
        if (reportData != null) {
            reportData.recordSkippedStep(testStep);
        }
    }

}
