package com.intuit.tank.tools.debugger;


import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.fife.ui.rtextarea.GutterIconInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import com.intuit.tank.clients.DataFileClient;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.data.Header;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.projects.models.KeyPair;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Builds a non-standalone frame for each test, so that disposing it doesn't exit the JVM. The frame is never shown.
 */
@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class AgentDebuggerFrameTest {

    private AgentDebuggerFrame agentDebuggerFrame;
    private File workingDir;
    private final RecordingListener listener = new RecordingListener();
    private final AtomicInteger activeLine = new AtomicInteger(-1);

    @BeforeAll
    public static void needsADisplay() throws IOException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        keepTheHarnessSettings();
    }

    /**
     * TankConfig reads the settings.xml of the first working dir it sees for the rest of the JVM, and disposing a frame
     * deletes the frame's working dir. So before any frame is disposed, point TankConfig at a working dir no frame owns.
     */
    static void keepTheHarnessSettings() throws IOException {
        PanelBuilder.createWorkingDir(null, null);
        assertNotNull(new TankConfig().getAgentConfig());
    }

    @BeforeEach
    public void setup() throws Exception {
        onEdt(() -> agentDebuggerFrame = new AgentDebuggerFrame(false, null, null));
        assertNotNull(agentDebuggerFrame);
        // the frame's working dir is where it pointed the harness's settings
        workingDir = new File(System.getProperty("WATS_PROPERTIES"));
        agentDebuggerFrame.addStepChangedListener(listener);
        agentDebuggerFrame.addScriptChangedListener(listener);
        agentDebuggerFrame.getScriptEditorTA().addActiveLineRangeListener(e -> activeLine.set(e.getMin()));
    }

    @AfterEach
    public void tearDown() throws Exception {
        // nothing the test did may have put a window, such as an error dialog, on screen
        for (Window window : Window.getWindows()) {
            assertFalse(window.isShowing(), () -> "showing: " + window);
        }
        if (agentDebuggerFrame != null) {
            onEdt(agentDebuggerFrame::dispose);
        }
    }

    @Test
    public void testDebuggerActions() {
        assertNotNull(agentDebuggerFrame.getDebuggerActions());
        assertNotNull(agentDebuggerFrame.getActionComponents());
        assertNotNull(agentDebuggerFrame.getRequestResponsePanel());
        assertNotNull(agentDebuggerFrame.getLoggerTA());
        assertNotNull(agentDebuggerFrame.getScriptEditorScrollPane());
        assertFalse(agentDebuggerFrame.isStandalone());
        assertFalse(agentDebuggerFrame.isVisible());
    }

    @Test
    public void testTankClientChooser() {
        // without a controller to ask, the frame offers the built-in clients and picks HttpClient 4.5
        JComboBox<TankClientChoice> chooser = agentDebuggerFrame.getTankClientChooser();
        assertEquals(3, chooser.getItemCount());
        assertEquals("com.intuit.tank.httpclient4.TankHttpClient4",
                ((TankClientChoice) chooser.getSelectedItem()).getClientClass());
    }

    @Test
    public void testCurrentTestPlan() {
        HDTestPlan hdTestPlan = new HDTestPlan();
        agentDebuggerFrame.setCurrentTestPlan(hdTestPlan);
        assertEquals(hdTestPlan, agentDebuggerFrame.getCurrentTestPlan());
        assertEquals(-1, agentDebuggerFrame.getCurrentRunningStep());
        assertEquals(0, agentDebuggerFrame.getScriptEditorTA().getCaretPosition());
    }

    @Test
    public void aTestPlanPutsOneStepOnEachLineAndSelectsTheFirst() throws Exception {
        HDTestPlan plan = plan("plan", variable("a", "1"), variable("b", "2"), variable("c", "3"));
        onEdt(() -> agentDebuggerFrame.setCurrentTestPlan(plan));

        assertEquals("Set_Variable(a->1)\nSet_Variable(b->2)\nSet_Variable(c->3)",
                agentDebuggerFrame.getScriptEditorTA().getText());
        assertEquals(3, agentDebuggerFrame.getSteps().size());
        assertSame(plan, listener.lastPlan.get());
        assertSame(agentDebuggerFrame.getSteps().get(0), listener.lastChanged.get());
        assertSame(plan.getGroup().get(0).getGroupSteps().get(0).getUseCase().get(0).getScriptSteps().get(1),
                agentDebuggerFrame.getStep(1));
        assertTrue(agentDebuggerFrame.getDebuggerActions().getStartAction().isEnabled());
    }

    @Test
    public void aWorkloadOffersItsPlansAndStartsWithTheFirst() throws Exception {
        HDTestPlan first = plan("first", variable("a", "1"));
        HDTestPlan second = plan("second", variable("b", "2"), variable("c", "3"));
        HDWorkload workload = workload(first, second);
        onEdt(() -> agentDebuggerFrame.setCurrentWorkload(workload));

        assertSame(workload, agentDebuggerFrame.getCurrentWorkload());
        assertSame(first, agentDebuggerFrame.getCurrentTestPlan());
        JComboBox<?> planChooser = planChooser();
        assertEquals(2, planChooser.getItemCount());
        assertSame(first, planChooser.getSelectedItem());

        // picking the other plan loads it
        onEdt(() -> planChooser.setSelectedItem(second));
        assertSame(second, agentDebuggerFrame.getCurrentTestPlan());
        assertEquals(2, agentDebuggerFrame.getSteps().size());

        onEdt(() -> agentDebuggerFrame.setCurrentWorkload(null));
        assertNull(agentDebuggerFrame.getCurrentWorkload());
        assertNull(agentDebuggerFrame.getCurrentTestPlan());
        assertEquals(0, planChooser.getItemCount());
        assertEquals("", agentDebuggerFrame.getScriptEditorTA().getText());
        assertFalse(agentDebuggerFrame.getDebuggerActions().getStartAction().isEnabled());
    }

    @Test
    public void aWorkloadWithoutPlansHasNoCurrentPlan() throws Exception {
        onEdt(() -> agentDebuggerFrame.setCurrentWorkload(workload()));
        assertNull(agentDebuggerFrame.getCurrentTestPlan());
        assertEquals(0, planChooser().getItemCount());
    }

    @Test
    public void scriptSourceEnablesReload() {
        agentDebuggerFrame.setScriptSource(new ScriptSource("1", SourceType.script));
        assertEquals("1", agentDebuggerFrame.getScriptSource().getId());
        assertTrue(agentDebuggerFrame.getDebuggerActions().getReloadAction().isEnabled());

        agentDebuggerFrame.setScriptSource(null);
        assertNull(agentDebuggerFrame.getScriptSource());
        assertFalse(agentDebuggerFrame.getDebuggerActions().getReloadAction().isEnabled());
    }

    @Test
    public void sleepAndThinkStepsRunWhenTheirBoxesAreTicked() throws Exception {
        ActionComponents components = agentDebuggerFrame.getActionComponents();
        onEdt(() -> {
            components.getRunSleepStepsCB().setSelected(true);
            components.getRunThinkStepsCB().setSelected(false);
        });
        assertTrue(agentDebuggerFrame.runSleepSteps());
        assertFalse(agentDebuggerFrame.runThinkSteps());

        onEdt(() -> {
            components.getRunSleepStepsCB().setSelected(false);
            components.getRunThinkStepsCB().setSelected(true);
        });
        assertFalse(agentDebuggerFrame.runSleepSteps());
        assertTrue(agentDebuggerFrame.runThinkSteps());
    }

    @Test
    public void projectVariablesAreReplaced() {
        agentDebuggerFrame.setProjectVariables(Map.of("a", "1"));
        agentDebuggerFrame.setProjectVariables(Map.of("b", "2"));
        assertEquals(Map.of("b", "2"), agentDebuggerFrame.getProjectVariables());

        agentDebuggerFrame.setProjectVariables(null);
        assertTrue(agentDebuggerFrame.getProjectVariables().isEmpty());
    }

    @Test
    public void aProjectBringsItsVariables() {
        agentDebuggerFrame.setProjectVariables(Map.of("old", "x"));
        ProjectTO project = new ProjectTO();
        project.setVariables(List.of(new KeyPair("host", "example.com")));
        project.setDataFiles(List.of(5));

        agentDebuggerFrame.setDataFromProject(project);

        assertEquals(Map.of("host", "example.com"), agentDebuggerFrame.getProjectVariables());
    }

    @Test
    public void waitingCoversTheFrameWithTheProgressPanel() throws Exception {
        InfiniteProgressPanel progress = (InfiniteProgressPanel) agentDebuggerFrame.getGlassPane();
        onEdt(agentDebuggerFrame::startWaiting);
        assertTrue(progress.isVisible());

        // stopping before the panel has started fading in would leave it up (see the report on InfiniteProgressPanel)
        await(() -> progress.alphaLevel > 0);
        onEdt(agentDebuggerFrame::stopWaiting);
        await(() -> !agentDebuggerFrame.getGlassPane().isVisible());
    }

    @Test
    public void currentTitleIsShownInTheToolbar() throws Exception {
        onEdt(() -> agentDebuggerFrame.setCurrentTitle("My Project"));
        assertNotNull(VariablesPanelTest.find(agentDebuggerFrame.getContentPane(), javax.swing.JLabel.class,
                l -> "My Project".equals(l.getText())));
    }

    @Test
    public void breakpointIsToggledOnTheCaretLine() throws Exception {
        loadPlan(3);
        onEdt(() -> {
            caretToLine(1);
            agentDebuggerFrame.toggleBreakPoint();
        });
        assertTrue(agentDebuggerFrame.hasBreakPoint(1));
        assertFalse(agentDebuggerFrame.hasBreakPoint(0));

        onEdt(agentDebuggerFrame::toggleBreakPoint);
        assertFalse(agentDebuggerFrame.hasBreakPoint(1));

        onEdt(() -> {
            caretToLine(0);
            agentDebuggerFrame.toggleBreakPoint();
            caretToLine(2);
            agentDebuggerFrame.toggleBreakPoint();
            agentDebuggerFrame.clearBookmarks();
        });
        assertFalse(agentDebuggerFrame.hasBreakPoint(0));
        assertFalse(agentDebuggerFrame.hasBreakPoint(2));
        assertEquals(0, agentDebuggerFrame.getScriptEditorScrollPane().getGutter().getBookmarks().length);
    }

    @Test
    public void skipIsToggledOnTheCaretLine() throws Exception {
        loadPlan(3);
        onEdt(() -> {
            caretToLine(2);
            agentDebuggerFrame.toggleSkip();
        });
        assertEquals(List.of(2), trackedLines());

        onEdt(agentDebuggerFrame::toggleSkip);
        assertEquals(List.of(), trackedLines());

        onEdt(() -> {
            caretToLine(0);
            agentDebuggerFrame.toggleSkip();
            caretToLine(1);
            agentDebuggerFrame.toggleSkip();
        });
        assertEquals(List.of(0, 1), trackedLines());
        onEdt(agentDebuggerFrame::clearSkips);
        assertEquals(List.of(), trackedLines());
    }

    @Test
    public void clickingALineSelectsItsStep() throws Exception {
        loadPlan(3);
        onEdt(() -> press(2, 0));
        assertSame(agentDebuggerFrame.getSteps().get(2), listener.lastChanged.get());
        onEdt(() -> press(0, 0));
        assertSame(agentDebuggerFrame.getSteps().get(0), listener.lastChanged.get());
    }

    @Test
    public void shiftClickSelectsSeveralLinesForBreakpointsAndSkips() throws Exception {
        loadPlan(4);
        onEdt(() -> {
            caretToLine(0);
            press(0, 0);
            // a release at the same spot is ignored
            release(0, 0);
            caretToLine(2);
            press(2, InputEvent.SHIFT_DOWN_MASK);
        });
        assertEquals(agentDebuggerFrame.getScriptEditorTA().getLineStartOffset(0),
                agentDebuggerFrame.getScriptEditorTA().getSelectionStart());
        assertEquals(agentDebuggerFrame.getScriptEditorTA().getLineEndOffset(2),
                agentDebuggerFrame.getScriptEditorTA().getSelectionEnd());

        onEdt(() -> {
            agentDebuggerFrame.toggleBreakPoint();
            agentDebuggerFrame.toggleSkip();
        });
        assertTrue(agentDebuggerFrame.hasBreakPoint(0));
        assertTrue(agentDebuggerFrame.hasBreakPoint(1));
        assertTrue(agentDebuggerFrame.hasBreakPoint(2));
        assertFalse(agentDebuggerFrame.hasBreakPoint(3));
        assertEquals(3, ((ScriptEditorTextArea) agentDebuggerFrame.getScriptEditorTA()).getIconRowHeader().getAllTrackingIcons().length
                - agentDebuggerFrame.getScriptEditorScrollPane().getGutter().getBookmarks().length);

        // a plain click ends the multi-line selection
        onEdt(() -> {
            caretToLine(3);
            press(3, 0);
            agentDebuggerFrame.toggleBreakPoint();
        });
        assertTrue(agentDebuggerFrame.hasBreakPoint(3));
        assertTrue(agentDebuggerFrame.hasBreakPoint(2));
    }

    @Test
    public void shiftClickOnTheNextLineIsNotAMultiLineSelection() throws Exception {
        loadPlan(3);
        onEdt(() -> {
            caretToLine(0);
            press(0, 0);
            caretToLine(1);
            press(1, InputEvent.SHIFT_DOWN_MASK);
            agentDebuggerFrame.toggleBreakPoint();
        });
        assertFalse(agentDebuggerFrame.hasBreakPoint(0));
        assertTrue(agentDebuggerFrame.hasBreakPoint(1));
    }

    @Test
    public void moveCursorStepsThroughTheLines() throws Exception {
        loadPlan(3);
        onEdt(() -> agentDebuggerFrame.moveCursor(false));
        assertEquals(1, agentDebuggerFrame.getScriptEditorTA().getCaretLineNumber());
        assertSame(agentDebuggerFrame.getSteps().get(1), listener.lastChanged.get());

        onEdt(() -> {
            agentDebuggerFrame.moveCursor(false);
            // already on the last line
            agentDebuggerFrame.moveCursor(false);
        });
        assertEquals(2, agentDebuggerFrame.getScriptEditorTA().getCaretLineNumber());

        onEdt(() -> agentDebuggerFrame.moveCursor(true));
        assertEquals(1, agentDebuggerFrame.getScriptEditorTA().getCaretLineNumber());
        assertSame(agentDebuggerFrame.getSteps().get(1), listener.lastChanged.get());
    }

    @Test
    public void arrowKeysMoveTheCursorUnlessAComboBoxHasThem() throws Exception {
        loadPlan(3);
        onEdt(() -> {
            JPanel elsewhere = new JPanel();
            elsewhere.dispatchEvent(key(elsewhere, KeyEvent.VK_DOWN));
            elsewhere.dispatchEvent(key(elsewhere, KeyEvent.VK_DOWN));
        });
        assertEquals(2, agentDebuggerFrame.getScriptEditorTA().getCaretLineNumber());

        onEdt(() -> {
            JPanel elsewhere = new JPanel();
            elsewhere.dispatchEvent(key(elsewhere, KeyEvent.VK_UP));
            elsewhere.dispatchEvent(key(elsewhere, KeyEvent.VK_LEFT));
            JComboBox<String> combo = new JComboBox<>();
            combo.dispatchEvent(key(combo, KeyEvent.VK_UP));
        });
        assertEquals(1, agentDebuggerFrame.getScriptEditorTA().getCaretLineNumber());
    }

    @Test
    public void anEditedStepReplacesTheOneInThePlan() throws Exception {
        HDTestPlan plan = plan("plan", variable("a", "1"), request("/old", List.of()));
        onEdt(() -> agentDebuggerFrame.setCurrentTestPlan(plan));
        RequestStep edited = request("/new", List.of(header("q", "1"), header("r", "2")));
        edited.setStepIndex(1);
        String xml = JaxbUtil.marshall(edited);

        AtomicReference<Boolean> result = new AtomicReference<>();
        onEdt(() -> result.set(agentDebuggerFrame.setStepfromString(xml)));

        assertTrue(result.get());
        String label = "https://example.com/new?q=1&r=2";
        assertEquals("Set_Variable(a->1)\n" + label, agentDebuggerFrame.getScriptEditorTA().getText());
        TestStep inPlan = plan.getGroup().get(0).getGroupSteps().get(0).getUseCase().get(0).getScriptSteps().get(1);
        assertEquals(label, ((RequestStep) inPlan).getRequest().getLabel());
        assertEquals("/new", ((RequestStep) agentDebuggerFrame.getStep(1)).getRequest().getPath());
        // the edited line is marked as modified
        assertEquals(List.of(1), trackedLines());
    }

    @Test
    public void startingWithoutStepsStopsAgain() throws Exception {
        onEdt(agentDebuggerFrame::start);
        await(() -> agentDebuggerFrame.getDebuggerActions().getStartAction().isEnabled());
        assertNull(listener.lastChanged.get());
        assertEquals("", agentDebuggerFrame.getLoggerTA().getText());
        assertFalse(agentDebuggerFrame.getDebuggerActions().getEndDebugAction().isEnabled());
    }

    @Test
    public void currentStepIsTheActiveLine() throws Exception {
        startWithoutSteps();
        loadPlan(3);
        onEdt(() -> agentDebuggerFrame.setCurrentStep(1));
        assertEquals(1, agentDebuggerFrame.getCurrentRunningStep());
        assertEquals(1, agentDebuggerFrame.getScriptEditorTA().getCaretLineNumber());
        assertFalse(agentDebuggerFrame.getDebuggerActions().getPauseAction().isEnabled());

        onEdt(() -> agentDebuggerFrame.setCurrentStep(-1));
        assertEquals(-1, agentDebuggerFrame.getCurrentRunningStep());
        assertEquals(0, agentDebuggerFrame.getScriptEditorTA().getCaretLineNumber());
    }

    @Test
    public void runToBreakpointCanBePaused() throws Exception {
        startWithoutSteps();
        loadPlan(3);
        onEdt(agentDebuggerFrame::runToBreakpoint);
        assertTrue(agentDebuggerFrame.getDebuggerActions().getPauseAction().isEnabled());
        // while skipping, the current step keeps pause available
        onEdt(() -> agentDebuggerFrame.setCurrentStep(1));
        assertTrue(agentDebuggerFrame.getDebuggerActions().getPauseAction().isEnabled());

        onEdt(agentDebuggerFrame::pause);
        assertFalse(agentDebuggerFrame.getDebuggerActions().getPauseAction().isEnabled());
    }

    @Test
    public void skipMarksTheNextStep() throws Exception {
        startWithoutSteps();
        loadPlan(3);
        onEdt(() -> {
            agentDebuggerFrame.setCurrentStep(0);
            agentDebuggerFrame.skip();
            // skipping it again doesn't mark it twice
            agentDebuggerFrame.skip();
        });
        assertEquals(List.of(1), trackedLines());

        // a skip toggled while debugging is passed to the flow controller
        onEdt(() -> {
            caretToLine(2);
            agentDebuggerFrame.toggleSkip();
            agentDebuggerFrame.toggleSkip();
            agentDebuggerFrame.clearSkips();
        });
        assertEquals(List.of(), trackedLines());
    }

    @Test
    public void steppingRecordsWhatTheStepDid() throws Exception {
        startWithoutSteps();
        loadPlan(3);
        BaseRequest request = mock(BaseRequest.class);
        BaseResponse response = mock(BaseResponse.class);
        when(response.getHttpCode()).thenReturn(500);
        TestStepContext context = context(1, Map.of("in", "1"), request, response, List.of());

        agentDebuggerFrame.stepStarted(context);

        DebugStep step = agentDebuggerFrame.getSteps().get(1);
        assertEquals(1, agentDebuggerFrame.getCurrentRunningStep());
        assertEquals(Map.of("in", "1"), step.getEntryVariables());
        assertSame(request, step.getRequest());
        assertSame(response, step.getResponse());
        assertSame(step, listener.lastEntered.get());

        ErrorContainer error = new ErrorContainer("body", null, null, "missing");
        TestStepContext after = context(1, Map.of("out", "2"), request, response, List.of(error));
        agentDebuggerFrame.stepFinished(after);

        assertEquals(Map.of("out", "2"), step.getExitVariables());
        assertEquals(List.of(error), step.getErrors());
        assertSame(step, listener.lastExited.get());
        // the failed request is underlined and the line marked with an error
        assertEquals(1, agentDebuggerFrame.getScriptEditorTA().getHighlighter().getHighlights().length);
        assertEquals(List.of(1), trackedLines());
    }

    @Test
    public void aSuccessfulStepIsNotMarked() throws Exception {
        startWithoutSteps();
        loadPlan(2);
        BaseResponse response = mock(BaseResponse.class);
        when(response.getHttpCode()).thenReturn(200);
        agentDebuggerFrame.stepStarted(context(0, Map.of(), null, response, List.of()));
        agentDebuggerFrame.stepFinished(context(0, Map.of(), null, response, List.of()));
        agentDebuggerFrame.stepFinished(context(0, Map.of(), null, null, List.of()));

        assertEquals(0, agentDebuggerFrame.getScriptEditorTA().getHighlighter().getHighlights().length);
        assertEquals(List.of(), trackedLines());
    }

    @Test
    public void flowControllerCallbacksMoveTheActiveLine() throws Exception {
        startWithoutSteps();
        loadPlan(3);
        onEdt(() -> agentDebuggerFrame.moveCursor(context(2, Map.of(), null, null, List.of())));
        assertEquals(2, activeLine.get());

        onEdt(() -> agentDebuggerFrame.setNextStep(context(1, Map.of(), null, null, List.of())));
        assertEquals(1, agentDebuggerFrame.getCurrentRunningStep());
    }

    @Test
    public void testStartedAndFinishedSwitchTheRunningActions() throws Exception {
        agentDebuggerFrame.testStarted();
        onEdt(() -> { });
        assertTrue(agentDebuggerFrame.getDebuggerActions().getNextStepAction().isEnabled());
        assertTrue(agentDebuggerFrame.getDebuggerActions().getSkipAction().isEnabled());

        agentDebuggerFrame.testFinished();
        onEdt(() -> { });
        assertFalse(agentDebuggerFrame.getDebuggerActions().getNextStepAction().isEnabled());
        assertTrue(agentDebuggerFrame.getDebuggerActions().getStartAction().isEnabled());
        assertEquals(-1, activeLine.get());
    }

    @Test
    public void stopWithNothingRunningResetsTheActions() throws Exception {
        onEdt(agentDebuggerFrame::stop);
        onEdt(() -> { });
        assertTrue(agentDebuggerFrame.getDebuggerActions().getStartAction().isEnabled());
        assertFalse(agentDebuggerFrame.getDebuggerActions().getEndDebugAction().isEnabled());
    }

    @Test
    public void startRunsTheScriptInTheHarness() throws Exception {
        // a first, empty run gives the frame a flow controller to mark an error with
        startWithoutSteps();
        HDTestPlan plan = plan("plan", variable("greeting", "hello"), variable("skipped", "yes"));
        HDWorkload workload = workload(plan);
        workload.setName("debug project");
        workload.setVariables(new HDTestVariables(true));
        onEdt(() -> agentDebuggerFrame.setCurrentWorkload(workload));
        agentDebuggerFrame.setProjectVariables(Map.of("user", "bob"));
        agentDebuggerFrame.setDataFiles(List.of(dataFile(7, "users.csv")));
        DataFileClient client = mock(DataFileClient.class);
        when(client.getDatafile(7)).thenReturn(dataFile(7, "users.csv"));
        when(client.getDatafileContent(7)).thenReturn("name\nbob\n");
        Field clientField = ActionProducer.class.getDeclaredField("dataFileClient");
        clientField.setAccessible(true);
        clientField.set(agentDebuggerFrame.getDebuggerActions(), client);
        onEdt(() -> agentDebuggerFrame.setCurrentStep(0));
        agentDebuggerFrame.stepFinished(context(0, Map.of(), null, null,
                List.of(new ErrorContainer("body", null, null, "old error"))));
        onEdt(() -> {
            caretToLine(1);
            agentDebuggerFrame.toggleSkip();
        });
        assertEquals(List.of(0, 1), trackedLines());

        // if the harness can't be created, start() shows a modal error dialog, so check its settings load first
        assertNotNull(new TankConfig().getAgentConfig());
        try {
            onEdt(agentDebuggerFrame::start);
            DebugStep first = agentDebuggerFrame.getSteps().get(0);
            // keep pressing next until the script has run to the end
            await(() -> {
                agentDebuggerFrame.next();
                return first.getExitVariables() != null
                        && agentDebuggerFrame.getDebuggerActions().getStartAction().isEnabled();
            });
        } finally {
            onEdt(agentDebuggerFrame::stop);
        }

        DebugStep first = agentDebuggerFrame.getSteps().get(0);
        assertEquals("hello", first.getExitVariables().get("greeting"));
        assertEquals("bob", first.getExitVariables().get("user"));
        // the skipped step never ran, and the old error mark is gone
        assertNull(agentDebuggerFrame.getSteps().get(1).getExitVariables());
        assertEquals(List.of(1), trackedLines());
        assertEquals("name\nbob\n", Files.readString(new File(workingDir, "users.csv").toPath()));
    }

    @Test
    public void exportCSVWritesEachResponseWithItsUrl() throws Exception {
        loadPlan(4);
        List<DebugStep> steps = agentDebuggerFrame.getSteps();
        respond(steps.get(0), "http://example.com/a", "200,OK");
        respond(steps.get(1), "http://example.com/b?x=1,2", "404,Not Found");
        respond(steps.get(2), null, "500,Error");
        respond(steps.get(3), "http://example.com/\"q\"", "200,OK");
        File csv = File.createTempFile("debugger", ".csv");
        csv.deleteOnExit();

        agentDebuggerFrame.exportCSV(csv);

        assertEquals(List.of("URL,HTTP Code,HTTP Msg,Response Time,Proxy Response Time,Response Size,Headers...,Cookies...",
                "http://example.com/a,200,OK",
                "\"http://example.com/b?x=1,2\",404,Not Found",
                ",500,Error",
                "\"http://example.com/\"\"q\"\"\",200,OK"),
                Files.readAllLines(csv.toPath(), StandardCharsets.UTF_8));
    }

    @Test
    public void exportCSVToAnUnwritablePlaceIsLogged() throws Exception {
        loadPlan(1);
        // a directory can't be opened as a file
        agentDebuggerFrame.exportCSV(workingDir);
        assertTrue(workingDir.isDirectory());
    }

    @Test
    public void stepEventsOutsideTheScriptHaveNoStep() throws Exception {
        loadPlan(1);
        listener.lastEntered.set(agentDebuggerFrame.getSteps().get(0));
        listener.lastExited.set(agentDebuggerFrame.getSteps().get(0));
        agentDebuggerFrame.fireStepStarted(5);
        agentDebuggerFrame.fireStepExited(-1);
        assertNull(listener.lastEntered.get());
        assertNull(listener.lastExited.get());

        agentDebuggerFrame.fireStepStarted(0);
        agentDebuggerFrame.fireStepExited(0);
        assertSame(agentDebuggerFrame.getSteps().get(0), listener.lastEntered.get());
        assertSame(agentDebuggerFrame.getSteps().get(0), listener.lastExited.get());
    }

    @Test
    public void closingTheFrameDeletesItsFiles() throws Exception {
        File defaultFile = new File(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                .getAgentDataFileStorageDir(), TankConstants.DEFAULT_CSV_FILE_NAME);
        defaultFile.getParentFile().mkdirs();
        Files.writeString(defaultFile.toPath(), "a,b");
        assertTrue(workingDir.isDirectory());

        onEdt(agentDebuggerFrame::dispose);
        agentDebuggerFrame = null;

        await(() -> !workingDir.exists() && !defaultFile.exists());
    }

    private void startWithoutSteps() throws Exception {
        onEdt(agentDebuggerFrame::start);
        await(() -> agentDebuggerFrame.getDebuggerActions().getStartAction().isEnabled());
    }

    private void loadPlan(int numSteps) throws Exception {
        TestStep[] steps = new TestStep[numSteps];
        for (int i = 0; i < numSteps; i++) {
            steps[i] = variable("v" + i, Integer.toString(i));
        }
        HDTestPlan plan = plan("plan", steps);
        onEdt(() -> agentDebuggerFrame.setCurrentTestPlan(plan));
    }

    private JComboBox<?> planChooser() {
        JComboBox<?> chooser = VariablesPanelTest.find(agentDebuggerFrame.getContentPane(), JComboBox.class,
                c -> c != agentDebuggerFrame.getTankClientChooser());
        assertNotNull(chooser);
        return chooser;
    }

    /** @return the lines of the gutter's tracking icons that aren't bookmarks */
    private List<Integer> trackedLines() throws Exception {
        List<Integer> lines = new ArrayList<>();
        List<GutterIconInfo> bookmarks = Arrays.asList(agentDebuggerFrame.getScriptEditorScrollPane().getGutter().getBookmarks());
        for (GutterIconInfo info : ((ScriptEditorTextArea) agentDebuggerFrame.getScriptEditorTA()).getIconRowHeader().getAllTrackingIcons()) {
            if (!bookmarks.contains(info)) {
                lines.add(agentDebuggerFrame.getScriptEditorTA().getLineOfOffset(info.getMarkedOffset()));
            }
        }
        lines.sort(null);
        return lines;
    }

    private void caretToLine(int line) {
        try {
            agentDebuggerFrame.getScriptEditorTA().setCaretPosition(agentDebuggerFrame.getScriptEditorTA().getLineStartOffset(line));
        } catch (Exception e) {
            fail(e);
        }
    }

    private void press(int line, int modifiers) {
        mouse(MouseEvent.MOUSE_PRESSED, line, modifiers);
    }

    private void release(int line, int modifiers) {
        mouse(MouseEvent.MOUSE_RELEASED, line, modifiers);
    }

    private void mouse(int id, int line, int modifiers) {
        try {
            var ta = agentDebuggerFrame.getScriptEditorTA();
            Rectangle bounds = ta.modelToView2D(ta.getLineStartOffset(line)).getBounds();
            Point p = new Point(bounds.x + 2, bounds.y + bounds.height / 2);
            ta.dispatchEvent(new MouseEvent(ta, id, System.currentTimeMillis(), modifiers | InputEvent.BUTTON1_DOWN_MASK,
                    p.x, p.y, 1, false, MouseEvent.BUTTON1));
        } catch (Exception e) {
            fail(e);
        }
    }

    private static KeyEvent key(java.awt.Component source, int keyCode) {
        return new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    private static void respond(DebugStep step, String url, String csv) {
        BaseRequest request = mock(BaseRequest.class);
        when(request.getRequestUrl()).thenReturn(url);
        BaseResponse response = mock(BaseResponse.class);
        when(response.convertToCSV()).thenReturn(csv);
        step.setRequest(request);
        step.setResponse(response);
    }

    private static TestStepContext context(int stepIndex, Map<String, String> variables, BaseRequest request,
            BaseResponse response, List<ErrorContainer> errors) {
        VariableStep step = variable("k", "v");
        step.setStepIndex(stepIndex);
        Variables vars = mock(Variables.class);
        when(vars.getVariableValues()).thenReturn(variables);
        TestStepContext context = mock(TestStepContext.class);
        when(context.getTestStep()).thenReturn(step);
        when(context.getVariables()).thenReturn(vars);
        when(context.getRequest()).thenReturn(request);
        when(context.getResponse()).thenReturn(response);
        when(context.getErrors()).thenReturn(errors);
        return context;
    }

    private static DataFileDescriptor dataFile(int id, String name) {
        DataFileDescriptor descriptor = new DataFileDescriptor();
        descriptor.setId(id);
        descriptor.setName(name);
        return descriptor;
    }

    static HDTestPlan plan(String name, TestStep... steps) {
        HDScriptUseCase useCase = new HDScriptUseCase();
        for (int i = 0; i < steps.length; i++) {
            steps[i].setStepIndex(i);
            useCase.getScriptSteps().add(steps[i]);
        }
        HDScript script = new HDScript();
        script.setLoop(1);
        script.getUseCase().add(useCase);
        HDScriptGroup group = new HDScriptGroup();
        group.setLoop(1);
        group.getGroupSteps().add(script);
        HDTestPlan plan = new HDTestPlan();
        plan.setTestPlanName(name);
        plan.setUserPercentage(100);
        plan.getGroup().add(group);
        return plan;
    }

    private static HDWorkload workload(HDTestPlan... plans) {
        HDWorkload workload = new HDWorkload();
        workload.getPlans().addAll(Arrays.asList(plans));
        return workload;
    }

    static VariableStep variable(String key, String value) {
        VariableStep step = new VariableStep();
        step.setKey(key);
        step.setValue(value);
        return step;
    }

    private static RequestStep request(String path, List<Header> queryString) {
        HDRequest request = new HDRequest();
        request.setProtocol("https");
        request.setHost("example.com");
        request.setPath(path);
        request.setLabel("https://example.com" + path);
        request.setQueryString(new ArrayList<>(queryString));
        RequestStep step = new RequestStep();
        step.setRequest(request);
        return step;
    }

    private static Header header(String key, String value) {
        Header header = new Header();
        header.setKey(key);
        header.setValue(value);
        return header;
    }

    private static void onEdt(ThrowingRunnable r) throws Exception {
        AtomicReference<Throwable> failure = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                r.run();
            } catch (Throwable t) {
                failure.set(t);
            }
        });
        if (failure.get() instanceof Exception e) {
            throw e;
        } else if (failure.get() instanceof Error e) {
            throw e;
        }
    }

    private static void await(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
        while (!condition.getAsBoolean()) {
            assertTrue(System.nanoTime() < deadline, "timed out waiting");
            Thread.sleep(25);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    /** Remembers the last step each event was for */
    private static class RecordingListener implements StepListener, ScriptChangedListener {
        final AtomicReference<DebugStep> lastChanged = new AtomicReference<>();
        final AtomicReference<DebugStep> lastEntered = new AtomicReference<>();
        final AtomicReference<DebugStep> lastExited = new AtomicReference<>();
        final AtomicReference<HDTestPlan> lastPlan = new AtomicReference<>();

        @Override
        public void stepChanged(DebugStep step) {
            lastChanged.set(step);
        }

        @Override
        public void stepEntered(DebugStep step) {
            lastEntered.set(step);
        }

        @Override
        public void stepExited(DebugStep step) {
            lastExited.set(step);
        }

        @Override
        public void scriptChanged(HDTestPlan plan) {
            lastPlan.set(plan);
        }
    }
}
