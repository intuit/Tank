/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.tools.headless;


import com.google.common.collect.ImmutableMap;
import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.TestPlanSingleton;
import com.intuit.tank.harness.data.*;
import com.intuit.tank.harness.functions.JexlIOFunctions;
import com.intuit.tank.harness.functions.JexlStringFunctions;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.rest.mvc.rest.clients.ProjectClient;
import com.intuit.tank.rest.mvc.rest.models.projects.KeyPair;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


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
    private static final String HEADERS_PATH = "/v2/agent/headers";
    private static final char NEWLINE = '\n';
    private ProjectClient projectClient;

    public HeadlessDebuggerSetup(String serviceUrl, Integer projectId) {
        try {
            workingDir = createWorkingDir(serviceUrl);
            variablesOutput = new VariablesOutput(this);
            this.projectClient = new ProjectClient(serviceUrl);

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
        if (args.length < 2) {
            usage();
            return;
        }

        LOG.info("Headless Agent Debugger - STARTING EXECUTION");

        String url = null;
        int projectId = -1;

        for (String argument : args) {
            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "checking arg " + argument)));

            String[] values = argument.split("=");
            if (values[0].equalsIgnoreCase("-p")) {
                projectId = Integer.parseInt(values[1]);
            } else if(values[0].equalsIgnoreCase("-h")) {
                url = values[1];
            }
        }

        if (url == null || projectId == -1) {
            usage();
            return;
        }

        Thread.sleep(3000);
        HeadlessDebuggerSetup automationRunner = new HeadlessDebuggerSetup(url, projectId);

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
        System.out.println("java -jar Headless-Debugger-all.jar -h=<controller_base_url> -p=<projectId>");
        System.out.println("-h=<controller_base_url>: The url of the controller to get test info from");
        System.out.println("-p=<projectId>:  The projectId of the Tank project with test plan file to execute");
    }

    public void setProject(Integer projectId) {
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
            } catch (WebClientRequestException e1) {
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
                debugStep.setEntryVariables(context.getVariables().getVaribleValues());
                debugStep.setRequest(context.getRequest());
                debugStep.setResponse(context.getResponse());
            }
            fireStepChanged(stepIndex);
            fireStepStarted(stepIndex);
        } catch  (Exception e) {
            LOG.error("Error starting step: " + e.getMessage(), e);
        }
    }

    public void stepFinished(final TestStepContext context) { //TODO: implement differently VERY IMPORTANT - error checking
        try {
            DebugStep debugStep = steps.get(currentRunningStep);
            if (debugStep != null) {
                debugStep.setExitVariables(context.getVariables().getVaribleValues());
                debugStep.setRequest(context.getRequest());
                debugStep.setResponse(context.getResponse());
            }

            try {
                StringBuilder sb = new StringBuilder();
                if(debugStep.getStepRun() != null) {
                    sb.append(debugStep.getStepRun().getStepIndex()+1).append(": ").append(debugStep.getStepRun().getInfo()).append(" ");
                }

                if (context.getResponse() != null) {
                    if ((context.getResponse().getHttpCode() >= 400 || context.getResponse().getHttpCode() == -1) || !context.getErrors().isEmpty()) { // ERROR - print entire response
                        sb.append(0).append(NEWLINE); // set error status (0)

                        // log full response
                        context.getResponse().logResponse(false);

                        // log variables
                        variablesOutput.displayVars();
                    } else {
                        sb.append(1).append(NEWLINE); // set success status (1)
                        context.getResponse().logResponse(true); // log shorter success response
                    }
                } else {
                    sb.append(0).append(NEWLINE); // set error status (0)
                }
                LOG.info("******** STEP INFO *********"); // log  step info and status
                LOG.info(sb);
                LOG.info("------------------------------------------------------");
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

    public static File createWorkingDir(String baseUrl) {
        try {
            File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
            temp.delete();
            temp = new File(temp.getAbsolutePath() + ".d");
            temp.mkdir();
            workingDir = temp;
            // create settings.xml
            writeSettings(workingDir, getHeaders(baseUrl));
            System.setProperty("WATS_PROPERTIES", workingDir.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("HeadlessDebugger - Error creating temp working dir: " + e);
        }
        return workingDir;
    }

    private static Headers getHeaders(String serviceUrl) {
        if (StringUtils.isNotBlank(serviceUrl)) {
            try ( InputStream settingsStream = new URL(serviceUrl + HEADERS_PATH).openStream() ) {
                URL url = new URL(serviceUrl + HEADERS_PATH);
                LOG.info("Starting up: making call to tank service url to get settings.xml "
                        + url.toExternalForm());

                //Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

                Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(settingsStream));

                JAXBContext ctx = JAXBContext.newInstance(Headers.class.getPackage().getName());
                return (Headers) ctx.createUnmarshaller().unmarshal(xmlSource);
            } catch (Exception e) {
                LOG.error("Error gettting headers: " + e, e);
            }
        }
        return null;
    }

    private static void writeSettings(File workingDir, Headers headers) throws IOException {
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
        if (headers != null) {
            s.append("<request-headers>").append(NEWLINE);
            // <header key="intuit_test">intuit_test</header>
            for (com.intuit.tank.vm.agent.messages.Header h : headers.getHeaders()) {
                s.append("<header key=\"").append(h.getKey()).append("\">").append(h.getValue()).append("</header>")
                        .append(NEWLINE);
            }

            s.append("</request-headers>").append(NEWLINE);
        }
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
                LOG.info("Workload: " + workload.getName() + NEWLINE);
                LOG.info("------------------------------------------------------");
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


}
