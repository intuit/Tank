/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.harness;


import com.intuit.tank.harness.data.*;
import com.intuit.tank.harness.functions.JexlIOFunctions;
import com.intuit.tank.harness.functions.JexlStringFunctions;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.rest.mvc.rest.clients.AgentClient;
import com.intuit.tank.rest.mvc.rest.clients.DataFileClient;
import com.intuit.tank.rest.mvc.rest.clients.ProjectClient;
import com.intuit.tank.rest.mvc.rest.clients.ScriptClient;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.projects.KeyPair;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.juli.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
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


public class AutomationDebuggerSetup implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(AutomationDebuggerSetup.class);
    private HDWorkload currentWorkload;
    private HDTestPlan currentTestPlan;
    private final Map<String, String> projectVariables = new HashMap<String, String>();
    private final List<Integer> datafileList = new ArrayList<Integer>();
    private final List<DebugStep> steps = new ArrayList<DebugStep>();
    private int currentRunningStep;
    private AutomationFlowController flowController;
    private APITestHarness harness;
    private static File workingDir;
    private Thread runningThread;
    private static final String HEADERS_PATH = "/v2/agent/headers";
    private static final char NEWLINE = '\n';
    private ScriptClient scriptClient;
    private AgentClient agentClient;
    private ProjectClient projectClient;
    private DataFileClient dataFileClient;

    public AutomationDebuggerSetup(String serviceUrl, Integer projectId) {
        workingDir = createWorkingDir(serviceUrl);

        this.scriptClient = new ScriptClient(serviceUrl);
        this.projectClient = new ProjectClient(serviceUrl);
        this.agentClient = new AgentClient(serviceUrl);
        this.dataFileClient = new DataFileClient(serviceUrl);

        // get project workload

        String scriptXml = projectClient.downloadTestScriptForProject(projectId);

    }

    public static void main(String[] args) {
        String url = args.length > 0 ? args[0] : "";
        new AutomationDebuggerSetup(url);
    }

    public boolean runTimingSteps() {
        return true;
    } // TODO: see where this is used


    /**
     * @return the steps
     */
    public List<DebugStep> getSteps() {
        return steps;
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
        File defaultFile = new File(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                .getAgentDataFileStorageDir(), TankConstants.DEFAULT_CSV_FILE_NAME);
        if (defaultFile.exists()) {
            boolean deleted = defaultFile.delete();
            if (!deleted) {
                System.out.println("Error deleting default file " + defaultFile.getAbsolutePath());
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
            if (currentWorkload.getPlans().size() > 0) {
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

    public void setCurrentStep(final int stepIndex) {
        currentRunningStep = stepIndex;
        int stepToSet = Math.max(0, currentRunningStep);
    }

    /**
     * @return the currentRunningStep
     */
    public int getCurrentRunningStep() {
        return currentRunningStep;
    }

    /**
     * @param currentTestPlan
     *            the currentTestPlan to set
     */
    public void setCurrentTestPlan(HDTestPlan currentTestPlan) {
        this.currentTestPlan = currentTestPlan;
        steps.clear();
        currentRunningStep = -1;
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

    public void setDataFromProject(ProjectTO selected) {
        projectVariables.clear();
        datafileList.clear();
        for (KeyPair pair : selected.getVariables()) {
            projectVariables.put(pair.getKey(), pair.getValue());
        }
        datafileList.addAll(selected.getDataFileIds());
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

    public void start() {
        flowController = new AutomationFlowController(this);

        Runnable task = () -> {
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
        };
        new Thread(task).start();
    }

    private void createHarness() {
        APITestHarness.destroyCurrentInstance();
        harness = APITestHarness.getInstance();
        harness.setFlowControllerTemplate(flowController);
//        TankClientChoice choice = (TankClientChoice) tankClientChooser.getSelectedItem(); //TODO: default to project DONT hardcode 4.5
//        harness.setTankHttpClientClass(choice.getClientClass());
        harness.getAgentRunData().setActiveProfile(LoggingProfile.VERBOSE);
        harness.getAgentRunData().setInstanceId("AutomationDebuggerInstance");
        harness.getAgentRunData().setMachineName("automationDebugger");
        harness.getAgentRunData().setNumUsers(1);
        harness.getAgentRunData().setJobId("automationDebuggerJob");
        harness.getAgentRunData().setTotalAgents(1);
        harness.setDebug(true);

        TestPlanSingleton.getInstance().setTestPlan(buildHDWorkload());
        downloadDataFiles();
        JexlIOFunctions.resetStatics();
        JexlStringFunctions.resetStatics();
    }

    private void downloadDataFiles() {
        if (!datafileList.isEmpty()) {
            DataFileClient client = dataFileClient;
            for (Integer id : datafileList) {
                DataFileDescriptor dataFile = client.getDatafile(id);
                if (dataFile != null) {
                    saveDataFile(client, dataFile, datafileList.size() == 1);
                }
            }
        }
    }

    private void saveDataFile(DataFileClient client, DataFileDescriptor dataFileDescriptor, boolean isDefault) {
        File dataFile = new File(workingDir, dataFileDescriptor.getName());
        LOG.info("writing file " + dataFileDescriptor.getName() + " to " + dataFile.getAbsolutePath());
        try (   FileOutputStream fos = new FileOutputStream(dataFile);
                InputStream is = IOUtils.toInputStream(client.getDatafileContent(dataFileDescriptor.getId()), StandardCharsets.UTF_8) ) {
            IOUtils.copy(is, fos);
            if (isDefault && !dataFileDescriptor.getName().equals(TankConstants.DEFAULT_CSV_FILE_NAME)) {
                File defaultFile = new File(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                        .getAgentDataFileStorageDir(), TankConstants.DEFAULT_CSV_FILE_NAME);
                LOG.info("copying file " + dataFileDescriptor.getName() + " to default file " + defaultFile.getAbsolutePath() + " to read");
                FileUtils.copyFile(dataFile, defaultFile);
            }
        } catch (Exception e) {
            LOG.error("Error downloading csv file: " + e, e);
            throw new RuntimeException(e);
        }

    }

    public void setNextStep(TestStepContext context) {
        setCurrentStep(context.getTestStep().getStepIndex());
    }

    public void stepStarted(final TestStepContext context) { //TODO: implement differently
        try {
                int stepIndex = context.getTestStep().getStepIndex();
                setCurrentStep(stepIndex);
                DebugStep debugStep = steps.get(currentRunningStep);
                if (debugStep != null) {
                    debugStep.setEntryVariables(context.getVariables().getVaribleValues());
                    debugStep.setRequest(context.getRequest());
                    debugStep.setResponse(context.getResponse());
                }
        } catch (Exception e) {
            e.printStackTrace();
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
                if (context.getResponse() != null && (context.getResponse().getHttpCode() >= 400 || context.getResponse().getHttpCode() == -1)) {
                    // highlight the line
//                    int lineStartOffset = scriptEditorTA.getLineStartOffset(currentRunningStep);
//                    int lineEndOffset = scriptEditorTA.getLineEndOffset(currentRunningStep);
//
//                    scriptEditorTA.getHighlighter().addHighlight(lineStartOffset, lineEndOffset, new SquiggleUnderlineHighlightPainter(Color.RED)); //TODO: squiggle underline
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (!context.getErrors().isEmpty()) {
                try {
                    debugStep.setErrors(context.getErrors());
//                    scriptEditorScrollPane.getGutter().addOffsetTrackingIcon(scriptEditorTA.getLineStartOffset(currentRunningStep), errorIcon); //TODO: triangle icon
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testFinished() {
        runningThread = null;
        harness = null;
    }

    public void setDataFiles(List<DataFileDescriptor> selectedObjects) {
        this.datafileList.clear();
        for (DataFileDescriptor d : selectedObjects) {
            datafileList.add(d.getId());
        }

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
            LOG.error("AutomationDebugger - Error creating temp working dir: " + e);
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
        System.out.println("Writing settings file to " + settingsFile.getAbsolutePath());
    }

    private void setFromString(String scriptXml) {
        if (StringUtils.isBlank(scriptXml)) {
            setCurrentWorkload(null);
            LOG.error("ERROR - No workload found");
        } else {
            HDWorkload workload = unmarshalWorkload(scriptXml);
            if (workload != null) {
                setCurrentWorkload(workload);
                LOG.info("Workload: " + workload.getName());
            }
        }
    }

    private HDWorkload unmarshalWorkload(String xml) {
        try {
            return JaxbUtil.unmarshall(xml, HDWorkload.class);
        } catch (JAXBException | ParserConfigurationException | SAXException e) {
            LOG
        }
        return null;
    }


}
