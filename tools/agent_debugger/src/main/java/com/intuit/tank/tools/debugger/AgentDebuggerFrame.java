/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.api.model.v1.project.KeyPair;
import com.intuit.tank.api.model.v1.project.ProjectTO;
import com.intuit.tank.client.v1.datafile.DataFileClient;
import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.TestPlanSingleton;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.data.HDVariable;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.data.Header;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.functions.JexlIOFunctions;
import com.intuit.tank.harness.functions.JexlStringFunctions;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.tools.debugger.ActionProducer.IconSize;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;
import com.intuit.tank.vm.common.TankConstants;

/**
 * ScrioptFilterRunner
 * 
 * @author dangleton
 * 
 */
public class AgentDebuggerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(AgentDebuggerFrame.class);

    private RSyntaxTextArea scriptEditorTA;
    private RTextScrollPane scriptEditorScrollPane;
    private boolean standalone;
    private ActionProducer debuggerActions;
    private HDWorkload currentWorkload;
    private HDTestPlan currentTestPlan;
    private JComboBox<HDTestPlan> testPlanChooser;
    private JComboBox<TankClientChoice> tankClientChooser;
    private List<StepListener> stepChangedListeners = new ArrayList<StepListener>();
    private List<ScriptChangedListener> scriptChangedListeners = new ArrayList<ScriptChangedListener>();
    private Map<String, String> projectVariables = new HashMap<String, String>();
    private List<Integer> datafileList = new ArrayList<Integer>();
    private List<DebugStep> steps = new ArrayList<DebugStep>();
    private int currentRunningStep;
    private ActionComponents actionComponents;
    private DebuggerFlowController flowController;
    private APITestHarness harness;
    private File workingDir;
    private Thread runningThread;
    private RSyntaxTextArea loggerTA;
    private InfiniteProgressPanel glassPane;

    private Icon errorIcon;
    private Icon modifiedIcon;
    private Icon skippedIcon;
    private RequestResponsePanel requestResponsePanel;
    private ScriptSource scriptSource;

    private int lastLine;
    private int multiSelectStart;
    private int multiSelectEnd;
    private boolean multiSelect;

    /**
     * @throws HeadlessException
     */
    public AgentDebuggerFrame(final boolean isStandalone, String serviceUrl) throws HeadlessException {
        super("Intuit Tank Agent Debugger");
        workingDir = PanelBuilder.createWorkingDir(this, serviceUrl);
        setSize(new Dimension(1024, 800));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        this.standalone = isStandalone;
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                quit();
            }
        });
        errorIcon = ActionProducer.getIcon("bullet_error.png", IconSize.SMALL);
        modifiedIcon = ActionProducer.getIcon("bullet_code_change.png", IconSize.SMALL);
        skippedIcon = ActionProducer.getIcon("skip.png", IconSize.SMALL);

        this.glassPane = new InfiniteProgressPanel();
        setGlassPane(glassPane);
        debuggerActions = new ActionProducer(this, serviceUrl);
        requestResponsePanel = new RequestResponsePanel(this);
        requestResponsePanel.init();
        testPlanChooser = new JComboBox();
        testPlanChooser.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getItem() != null) {
                    HDTestPlan selected = (HDTestPlan) event.getItem();
                    if (!selected.equals(currentTestPlan)) {
                        setCurrentTestPlan(selected);
                    }
                }

            }
        });

        tankClientChooser = new JComboBox<TankClientChoice>();
        debuggerActions.setChoiceComboBoxOptions(tankClientChooser);

        actionComponents = new ActionComponents(standalone, testPlanChooser, tankClientChooser, debuggerActions);
        addScriptChangedListener(actionComponents);
        setJMenuBar(actionComponents.getMenuBar());

        Component topPanel = PanelBuilder.createTopPanel(actionComponents);
        Component bottomPanel = PanelBuilder.createBottomPanel(this);
        Component contentPanel = PanelBuilder.createContentPanel(this);

        final JPopupMenu popup = actionComponents.getPopupMenu();
        scriptEditorTA.setPopupMenu(null);

        scriptEditorTA.addMouseListener(new MouseAdapter() {
            int lastHash;

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShow(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShow(e);
            }

            private void maybeShow(MouseEvent e) {
                if (lastHash == getHash(e)) {
                    return;
                }
                if (e.isPopupTrigger()) {
                    // select the line
                    try {
                        int offset = scriptEditorTA.viewToModel(e.getPoint());
                        Rectangle modelToView = scriptEditorTA.modelToView(offset);
                        Point point = new Point(modelToView.x + 1, e.getPoint().y);
                        if (modelToView.contains(point)) {
                            if (!multiSelect) {
                                int line = scriptEditorTA.getLineOfOffset(offset);
                                scriptEditorTA.setCurrentLine(line);
                            }
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                } else if (e.isShiftDown()) {
                    int line = scriptEditorTA.getCaretLineNumber();
                    int start = Math.min(line, lastLine);
                    int end = Math.max(line, lastLine);
                    multiSelect = end - start > 1;
                    if (multiSelect) {
                        multiSelectStart = start;
                        multiSelectEnd = end;
                        try {
                            scriptEditorTA.setEnabled(true);
                            scriptEditorTA.select(scriptEditorTA.getLineStartOffset(start), scriptEditorTA.getLineEndOffset(end));
                            scriptEditorTA.setEnabled(false);
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                            multiSelect = false;
                        }
                    }
                } else {
                    multiSelect = false;
                    lastLine = scriptEditorTA.getCaretLineNumber();
                }
                lastHash = getHash(e);
            }

            private int getHash(MouseEvent e) {
                return new HashCodeBuilder().append(e.getButton()).append(e.getSource().hashCode()).append(e.getPoint()).toHashCode();
            }

        });

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainSplit.setTopComponent(contentPanel);
        mainSplit.setBottomComponent(bottomPanel);
        mainSplit.setDividerLocation(600);
        mainSplit.setResizeWeight(0.8D);
        mainSplit.setDividerSize(5);

        add(topPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);

        WindowUtil.centerOnScreen(this);
        pack();

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    handleKeyEvent(e);
                }
                return false;
            }
        });
    }

    public ActionProducer getDebuggerActions() {
        return debuggerActions;
    }

    /**
     * @return the tankClientChooser
     */
    public JComboBox<TankClientChoice> getTankClientChooser() {
        return tankClientChooser;
    }

    private void handleKeyEvent(KeyEvent event) {
        if (!(event.getSource() instanceof JComboBox)) {
            if (event.getKeyCode() == KeyEvent.VK_UP) {
                moveCursor(true);
            } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                moveCursor(false);
            }
        }
    }

    protected void moveCursor(boolean moveUp) {
        try {
            scriptEditorTA.grabFocus();
            int caretLineNumber = this.scriptEditorTA.getCaretLineNumber() + (moveUp ? -1 : 1);
            if (caretLineNumber > 0 && moveUp) {
                scriptEditorTA.setCurrentLine(caretLineNumber);
                fireStepChanged(caretLineNumber);
            }
            int lastLine = scriptEditorTA.getLineOfOffset(this.scriptEditorTA.getText().length());
            if (!moveUp && caretLineNumber <= lastLine) {
                scriptEditorTA.setCurrentLine(caretLineNumber);
                fireStepChanged(caretLineNumber);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the scriptSource
     */
    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    /**
     * @param scriptSource
     *            the scriptSource to set
     */
    public void setScriptSource(ScriptSource scriptSource) {
        this.scriptSource = scriptSource;
        debuggerActions.getReloadAction().setEnabled(scriptSource != null);
    }

    public void startWaiting() {
        glassPane.start();
    }

    public void stopWaiting() {
        glassPane.stop();
    }

    public void addStepChangedListener(StepListener l) {
        stepChangedListeners.add(l);
    }

    public void addScriptChangedListener(ScriptChangedListener l) {
        scriptChangedListeners.add(l);
    }

    public boolean runTimingSteps() {
        return actionComponents.getRunTimingStepsCB().isSelected();
    }

    /**
     * @return the scriptEditorTA
     */
    public RSyntaxTextArea getScriptEditorTA() {
        return scriptEditorTA;
    }

    /**
     * @param scriptEditorTA
     *            the scriptEditorTA to set
     */
    public void setScriptEditorTA(RSyntaxTextArea scriptEditorTA) {
        this.scriptEditorTA = scriptEditorTA;
    }

    /**
     * @return the scriptEditorScrollPane
     */
    public RTextScrollPane getScriptEditorScrollPane() {
        return scriptEditorScrollPane;
    }

    /**
     * @param scriptEditorScrollPane
     *            the scriptEditorScrollPane to set
     */
    public void setScriptEditorScrollPane(RTextScrollPane scriptEditorScrollPane) {
        this.scriptEditorScrollPane = scriptEditorScrollPane;
    }

    /**
     * @return the steps
     */
    public List<DebugStep> getSteps() {
        return steps;
    }

    /**
     * @return the loggerTA
     */
    public RSyntaxTextArea getLoggerTA() {
        return loggerTA;
    }

    /**
     * @param loggerTA
     *            the loggerTA to set
     */
    public void setLoggerTA(RSyntaxTextArea loggerTA) {
        this.loggerTA = loggerTA;
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

    public void clearBookmarks() {
        GutterIconInfo[] bookmarks = this.scriptEditorScrollPane.getGutter().getBookmarks();
        for (GutterIconInfo gi : bookmarks) {
            try {
                int line = this.scriptEditorTA.getLineOfOffset(gi.getMarkedOffset());
                this.scriptEditorScrollPane.getGutter().toggleBookmark(line);
            } catch (BadLocationException e) {
                LOG.error("Error unsetting bookmark: " + e);
            }
        }
    }

    public void clearSkips() {
        GutterIconInfo[] bookmarks = this.scriptEditorScrollPane.getGutter().getAllTrackingIcons();
        for (GutterIconInfo gi : bookmarks) {
            this.scriptEditorScrollPane.getGutter().removeTrackingIcon(gi);
            if (flowController != null) {
                flowController.getSkipList().clear();
            }
        }
    }

    public boolean setStepfromString(String stepXML) {
        boolean ret = false;
        try {
            TestStep unmarshalledStep = JaxbUtil.unmarshall(stepXML, TestStep.class);
            scriptEditorScrollPane.getGutter().addOffsetTrackingIcon(scriptEditorTA.getLineStartOffset(unmarshalledStep.getStepIndex()), modifiedIcon);

            DebugStep debugStep = steps.get(unmarshalledStep.getStepIndex());
            debugStep.setStepRun(unmarshalledStep);
            if (currentTestPlan != null) {
                for (HDScriptGroup group : currentTestPlan.getGroup()) {
                    for (HDScript script : group.getGroupSteps()) {
                        for (HDScriptUseCase useCase : script.getUseCase()) {
                            for (int i = useCase.getScriptSteps().size(); --i >= 0;) {
                                TestStep step = useCase.getScriptSteps().get(i);
                                if (step.getStepIndex() == unmarshalledStep.getStepIndex()) {
                                    useCase.getScriptSteps().remove(i);
                                    updateStepLabel(unmarshalledStep);
                                    useCase.getScriptSteps().add(i, unmarshalledStep);
                                }
                            }
                        }
                    }
                }
                int pos = scriptEditorTA.getCaretPosition();
                buildEditorString(currentTestPlan);
                scriptEditorTA.setCaretPosition(pos);
            }
            ret = true;
        } catch (Exception e) {
            showError("Error unmarshalling step: " + e);
        }
        return ret;
    }

    private void updateStepLabel(TestStep testStep) {

        if (testStep instanceof RequestStep) {
            StringBuilder label = new StringBuilder();
            RequestStep step = (RequestStep) testStep;
            label.append(step.getRequest().getProtocol()).append("://").append(step.getRequest().getHost()).append(step.getRequest().getPath());

            int qsCount = 0;
            if (step.getRequest().getQueryString() != null) {
                for (Header qs : step.getRequest().getQueryString()) {
                    label.append(qsCount == 0 ? "?" : "&");
                    label.append(qs.getKey()).append("=").append(qs.getValue());
                    qsCount++;
                }
            }
            step.getRequest().setLabel(StringUtils.abbreviate(label.toString(), 1024));
        }
        return;
    }

    public void showError(String msg) {
        LOG.error("Error: " + msg);
        stopWaiting();
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Quits the app and attempts to delete the working dir
     */
    public void quit() {
        super.setVisible(false);
        if (workingDir.exists()) {
            try {
                FileUtils.deleteDirectory(workingDir);
            } catch (IOException e) {
                System.out.println("Error deleting directory " + workingDir.getAbsolutePath() + ": " + e);
            }
        }
        if (this.standalone) {
            System.exit(0);
        }
    }

    /**
     * @param currentWorkload
     *            the currentWorkload to set
     */
    public void setCurrentWorkload(HDWorkload currentWorkload) {
        setCurrentTestPlan(null);
        this.currentWorkload = currentWorkload;
        if (currentWorkload != null) {
            DefaultComboBoxModel<HDTestPlan> model = new DefaultComboBoxModel<HDTestPlan>(currentWorkload.getPlans().toArray(new HDTestPlan[currentWorkload.getPlans().size()]));
            if (currentWorkload.getPlans().size() > 0) {
                setCurrentTestPlan(currentWorkload.getPlans().get(0));
                model.setSelectedItem(currentTestPlan);
            }
            testPlanChooser.setModel(model);
        } else {
            setCurrentTestPlan(null);
            testPlanChooser.setModel(new DefaultComboBoxModel<HDTestPlan>());
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
        if (currentRunningStep < 0) {
            scriptEditorTA.setActiveLineRange(-1, -1);
        } else {
            scriptEditorTA.setActiveLineRange(currentRunningStep + 1, currentRunningStep + 1);
        }
        scriptEditorTA.setCurrentLine(stepToSet);
        repaint();
        if (flowController.isSkipping()) {
            actionComponents.skipTo();
        } else {
            actionComponents.doneSkipping();
        }
    }

    /**
     * @return the currentRunningStep
     */
    public int getCurrentRunningStep() {
        return currentRunningStep;
    }

    public ActionComponents getActionComponents() {
        return actionComponents;
    }

    /**
     * @param currentTestPlan
     *            the currentTestPlan to set
     */
    public void setCurrentTestPlan(HDTestPlan currentTestPlan) {
        this.currentTestPlan = currentTestPlan;
        steps.clear();
        currentRunningStep = -1;
        scriptEditorScrollPane.getGutter().removeAllTrackingIcons();
        buildEditorString(currentTestPlan);
        if (!steps.isEmpty()) {
            fireStepChanged(0);
        }
        scriptEditorTA.setCaretPosition(0);
        fireScriptChanged();
    }

    private void buildEditorString(HDTestPlan currentTestPlan) {
        StringBuilder sb = new StringBuilder();
        if (currentTestPlan != null) {
            for (HDScriptGroup group : currentTestPlan.getGroup()) {
                for (HDScript script : group.getGroupSteps()) {
                    for (HDScriptUseCase useCase : script.getUseCase()) {
                        for (TestStep step : useCase.getScriptSteps()) {
                            if (sb.length() != 0) {
                                sb.append('\n');
                            }
                            sb.append(step.getInfo());
                            steps.add(new DebugStep(step));
                        }
                    }
                }
            }
        }
        GutterIconInfo[] allTrackingIcons = scriptEditorScrollPane.getGutter().getAllTrackingIcons();
        List<IconContainer> icons = new ArrayList<IconContainer>(allTrackingIcons.length);
        for (GutterIconInfo gi : allTrackingIcons) {
            try {
                icons.add(new IconContainer(scriptEditorTA.getLineOfOffset(gi.getMarkedOffset()), gi.getIcon()));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        scriptEditorScrollPane.getGutter().removeAllTrackingIcons();
        scriptEditorTA.setText(sb.toString());
        for (IconContainer ic : icons) {
            try {
                scriptEditorScrollPane.getGutter().addOffsetTrackingIcon(scriptEditorTA.getLineStartOffset(ic.getLine()), ic.getIcon());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the standalone
     */
    public boolean isStandalone() {
        return standalone;
    }

    private String buildScriptString() throws JAXBException {
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
        return JaxbUtil.marshall(workload);
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

    public boolean hasBreakPoint(int i) {
        try {
            GutterIconInfo[] bookmarks = this.scriptEditorScrollPane.getGutter().getBookmarks();
            for (GutterIconInfo info : bookmarks) {
                int line = this.scriptEditorTA.getLineOfOffset(info.getMarkedOffset());
                if (line == i) {
                    return true;
                }
            }
        } catch (BadLocationException e) {
            LOG.warn("Error processing breakpoints: " + e);
        }
        return false;
    }

    public void runToBreakpoint() {
        flowController.setSkipping(true);
        actionComponents.skipTo();
        flowController.doNext();

    }

    public void stop() {
        try {
            if (harness != null) {
                harness.setCommand(WatsAgentCommand.kill);
            }
            if (runningThread != null) {
                runningThread.interrupt();
            }
        } finally {
            testFinished();
            runningThread = null;
            harness = null;
            actionComponents.stop();
        }
    }

    public void start() {
        startWaiting();
        flowController = new DebuggerFlowController(this);
        scriptEditorTA.getHighlighter().removeAllHighlights();
        actionComponents.start();
        loggerTA.setText("");
        loggerTA.setCaretPosition(0);
        loggerTA.repaint();
        new Thread(new Runnable() {
            public void run() {

                fireStepChanged(-1);
                if (!steps.isEmpty()) {
                    for (DebugStep step : steps) {
                        step.clear();
                    }
                    setCurrentStep(-1);
                    for (GutterIconInfo gi : scriptEditorScrollPane.getGutter().getAllTrackingIcons()) {
                        if (gi.getIcon() == errorIcon) {
                            scriptEditorScrollPane.getGutter().removeTrackingIcon(gi);
                        } else if (gi.getIcon() == skippedIcon) {
                            try {
                                flowController.skip(scriptEditorTA.getLineOfOffset(gi.getMarkedOffset()));
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // start apiHarness and get the variables....
                    try {
                        createHarness();
                        runningThread = new Thread(new Runnable() {
                            public void run() {
                                harness.runConcurrentTestPlans();
                            }
                        });

                        runningThread.start();
                    } catch (Exception e) {
                        showError("Error starting test: " + e);
                        actionComponents.stop();
                    }

                } else {
                    stopWaiting();
                    actionComponents.stop();
                }
            }
        }).start();
    }

    private void createHarness() throws JAXBException {
        APITestHarness.destroyCurrentInstance();
        harness = APITestHarness.getInstance();
        harness.setFlowControllerTemplate(flowController);
        TankClientChoice choice = (TankClientChoice) tankClientChooser.getSelectedItem();
        harness.setTankHttpClientClass(choice.getClientClass());
        harness.getAgentRunData().setActiveProfile(LoggingProfile.VERBOSE);
        harness.getAgentRunData().setInstanceId("DebuggerInstance");
        harness.getAgentRunData().setMachineName("debugger");
        harness.getAgentRunData().setNumUsers(1);
        harness.getAgentRunData().setJobId("debuggerJob");
        harness.getAgentRunData().setTotalAgents(1);
        harness.setDebug(true);

        List<String> testPlansXmls = new ArrayList<String>();
        testPlansXmls.add(buildScriptString());
        TestPlanSingleton.getInstance().setTestPlans(testPlansXmls);
        downloadDataFiles();
        JexlIOFunctions.resetStatics();
        JexlStringFunctions.resetStatics();
    }

    private void downloadDataFiles() {
        if (!datafileList.isEmpty()) {
            DataFileClient client = debuggerActions.getDataFileClient();
            for (Integer id : datafileList) {
                DataFileDescriptor dataFile = client.getDataFile(id);
                if (dataFile != null) {
                    saveDataFile(client, dataFile, datafileList.size() == 1);
                }
            }
        }
    }

    private void saveDataFile(DataFileClient client, DataFileDescriptor dataFileDescriptor, boolean isDefault) {
        File dataFile = new File(workingDir, dataFileDescriptor.getName());
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            LOG.info("writing file " + dataFileDescriptor.getName() + " to " + dataFile.getAbsolutePath());
            is = client.getDataFileDataStream(dataFileDescriptor.getId());
            fos = new FileOutputStream(dataFile);
            IOUtils.copy(is, fos);
            IOUtils.closeQuietly(fos);
            if (isDefault && !dataFileDescriptor.getName().equals(TankConstants.DEFAULT_CSV_FILE_NAME)) {
                File defaultFile = new File(workingDir, TankConstants.DEFAULT_CSV_FILE_NAME);
                FileUtils.copyFile(dataFile, defaultFile);
            }
        } catch (Exception e) {
            LOG.error("Error downloading csv file: " + e, e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }

    }

    public void setNextStep(TestStepContext context) {
        setCurrentStep(context.getTestStep().getStepIndex());
    }

    public void moveCursor(TestStepContext context) {
        int stepIndex = context.getTestStep().getStepIndex();
        scriptEditorTA.setActiveLineRange(stepIndex, stepIndex);
    }

    public void stepStarted(final TestStepContext context) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    actionComponents.stepping();
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
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void stepFinished(final TestStepContext context) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    actionComponents.doneStepping();
                    DebugStep debugStep = steps.get(currentRunningStep);
                    if (debugStep != null) {
                        debugStep.setExitVariables(context.getVariables().getVaribleValues());
                        debugStep.setRequest(context.getRequest());
                        debugStep.setResponse(context.getResponse());
                    }
                    try {
                        if (context.getResponse() != null && (context.getResponse().getHttpCode() >= 400 || context.getResponse().getHttpCode() == -1)) {
                            // highlight the line
                            int lineStartOffset = scriptEditorTA.getLineStartOffset(currentRunningStep);
                            int lineEndOffset = scriptEditorTA.getLineEndOffset(currentRunningStep);

                            scriptEditorTA.getHighlighter().addHighlight(lineStartOffset, lineEndOffset, new SquiggleUnderlineHighlightPainter(Color.RED));
                        }
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                    if (!context.getErrors().isEmpty()) {
                        try {
                            debugStep.setErrors(context.getErrors());
                            scriptEditorScrollPane.getGutter().addOffsetTrackingIcon(scriptEditorTA.getLineStartOffset(currentRunningStep), errorIcon);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                    fireStepExited(context.getTestStep().getStepIndex());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                stopWaiting();
                actionComponents.setRunningActions(true);
            }
        });

    }

    public void testFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                runningThread = null;
                harness = null;
                scriptEditorTA.setActiveLineRange(-1, -1);
                actionComponents.stop();
            }
        });

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

    public void setCurrentTitle(String string) {
        this.actionComponents.setCurrentTitle(string);

    }

    public void pause() {
        flowController.skipTo(-1);
        flowController.setSkipping(false);
        this.actionComponents.doneSkipping();

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
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
        String url = "";
        if (args.length > 0) {
            url = args[0];
        }
/*        Properties props = new Properties();
        try {
            InputStream configStream = AgentDebuggerFrame.class.getResourceAsStream("/log4j.properties");
            props.load(configStream);
            configStream.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot laod configuration file ");
        }
        props.setProperty("log4j.appender.agent.File", "debugger.log");
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
*/
        new AgentDebuggerFrame(true, url).setVisible(true);
    }

    public void skip() {
        flowController.skip(currentRunningStep + 1);
        try {
            int lineStartOffset = scriptEditorTA.getLineStartOffset(currentRunningStep + 1);
            for (GutterIconInfo info : scriptEditorScrollPane.getGutter().getAllTrackingIcons()) {
                if (info.getMarkedOffset() == lineStartOffset && info.getIcon() == skippedIcon) {
                    scriptEditorScrollPane.getGutter().removeTrackingIcon(info);
                }
            }
            scriptEditorScrollPane.getGutter().addOffsetTrackingIcon(scriptEditorTA.getLineStartOffset(currentRunningStep + 1), skippedIcon);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        flowController.doNext();
    }

    public void toggleBreakPoint() {
        try {
            if (multiSelect) {
                for (int line = multiSelectStart; line <= multiSelectEnd; line++) {
                    scriptEditorScrollPane.getGutter().toggleBookmark(line);
                }
            } else {
                int line = scriptEditorTA.getCaretLineNumber();
                scriptEditorScrollPane.getGutter().toggleBookmark(line);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void toggleSkip() {
        if (multiSelect) {
            for (int line = multiSelectStart; line <= multiSelectEnd; line++) {
                doToggleSkip(line);
            }
        } else {
            int line = scriptEditorTA.getCaretLineNumber();
            doToggleSkip(line);
        }
    }

    private void doToggleSkip(int line) {
        try {
            boolean found = false;
            for (GutterIconInfo info : scriptEditorScrollPane.getGutter().getAllTrackingIcons()) {
                if (scriptEditorTA.getLineOfOffset(info.getMarkedOffset()) == line && info.getIcon() == skippedIcon) {
                    scriptEditorScrollPane.getGutter().removeTrackingIcon(info);
                    found = true;
                }
            }
            if (!found) {
                scriptEditorScrollPane.getGutter().addOffsetTrackingIcon(scriptEditorTA.getLineStartOffset(line), skippedIcon);
            }
            if (flowController != null) {
                if (flowController.getSkipList().contains(line)) {
                    flowController.removeSkip(line);
                } else {
                    flowController.skip(line);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public RequestResponsePanel getRequestResponsePanel() {
        return requestResponsePanel;
    }

}
