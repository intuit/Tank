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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.intuit.tank.agent.models.TankHttpClientDefinition;
import com.intuit.tank.agent.models.TankHttpClientDefinitionContainer;
import com.intuit.tank.clients.AgentClient;
import com.intuit.tank.clients.DataFileClient;
import com.intuit.tank.clients.ProjectClient;
import com.intuit.tank.clients.ScriptClient;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.datafiles.models.DataFileDescriptorContainer;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.script.models.ScriptDescription;
import com.intuit.tank.script.models.ScriptDescriptionContainer;

/**
 * Drives the actions against a mocked frame and mocked REST clients. Choosers and dialogs are replaced with
 * Mockito construction mocks, and {@link JOptionPane} with a static mock, so nothing is ever shown on screen.
 */
@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class ActionProducerTest {

    private static final String WORKLOAD_XML = "<?xml version=\"1.0\"?>"
            + "<workload xmlns=\"urn:com/intuit/tank/harness/data/v1\" name=\"UC1\"/>";
    private static final long TIMEOUT_MS = 5000;

    @TempDir
    Path tempDir;

    private AgentDebuggerFrame frame;
    private ActionProducer producer;
    private ScriptClient scriptClient;
    private ProjectClient projectClient;
    private DataFileClient dataFileClient;
    private AgentClient agentClient;
    private MockedStatic<SwingUtilities> swing;
    private final List<Runnable> queuedErrors = new ArrayList<>();

    @BeforeAll
    public static void requireDisplay() {
        // ActionProducer reads the menu shortcut mask from the toolkit, which needs a display
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @BeforeEach
    public void setup() {
        // ActionProducer.showError queues a JOptionPane on the event dispatch thread. Hold those tasks back so no
        // dialog is shown; tests that expect an error run them against a mocked JOptionPane.
        swing = mockStatic(SwingUtilities.class, invocation -> {
            if ("invokeLater".equals(invocation.getMethod().getName())
                    && invocation.getArgument(0).getClass().getName().startsWith(ActionProducer.class.getName())) {
                queuedErrors.add(invocation.getArgument(0));
                return null;
            }
            return invocation.callRealMethod();
        });
        frame = mock(AgentDebuggerFrame.class);
        try (MockedConstruction<ScriptClient> scripts = mockConstruction(ScriptClient.class);
                MockedConstruction<ProjectClient> projects = mockConstruction(ProjectClient.class);
                MockedConstruction<AgentClient> agents = mockConstruction(AgentClient.class);
                MockedConstruction<DataFileClient> dataFiles = mockConstruction(DataFileClient.class)) {
            producer = new ActionProducer(frame, null, null);
            agentClient = agents.constructed().get(0);
        }
        scriptClient = producer.getScriptClient();
        projectClient = producer.getProjectClient();
        dataFileClient = producer.getDataFileClient();
    }

    @AfterEach
    public void checkNoUnexpectedErrors() {
        swing.close();
        List<String> unexpected = new ArrayList<>();
        for (Runnable queued : queuedErrors) {
            unexpected.add(errorShownBy(queued));
        }
        assertTrue(unexpected.isEmpty(), "unexpected errors: " + unexpected);
    }

    @Test
    public void testActionsAreCreatedOnce() {
        assertSame(producer.getStartAction(), producer.getStartAction());
        assertSame(producer.getQuitAction(), producer.getQuitAction());
        assertSame(producer.getClearLogOutputAction(), producer.getClearLogOutputAction());
        assertEquals(ActionProducer.ACTION_START, producer.getStartAction().getValue(Action.NAME));
    }

    @Test
    public void testFindAndReloadStartDisabled() {
        assertFalse(producer.getFindAction().isEnabled());
        assertFalse(producer.getReloadAction().isEnabled());
        assertNotNull(producer.getFindAction().getValue(Action.ACCELERATOR_KEY));
    }

    @Test
    public void testDebugActionsDelegateToTheFrame() {
        producer.getStartAction().actionPerformed(null);
        verify(frame).start();
        producer.getNextStepAction().actionPerformed(null);
        verify(frame).next();
        producer.getEndDebugAction().actionPerformed(null);
        verify(frame).stop();
        producer.getRunToAction().actionPerformed(null);
        verify(frame).runToBreakpoint();
        producer.getPauseAction().actionPerformed(null);
        verify(frame).pause();
        producer.getSkipAction().actionPerformed(null);
        verify(frame).skip();
    }

    @Test
    public void testEditingActionsDelegateToTheFrame() {
        producer.getClearBookmarksAction().actionPerformed(null);
        verify(frame).clearBookmarks();
        producer.getClearSkipsAction().actionPerformed(null);
        verify(frame).clearSkips();
        producer.getSkipStepAction().actionPerformed(null);
        verify(frame).toggleSkip();
        producer.getToggleBreakpointAction().actionPerformed(null);
        verify(frame).toggleBreakPoint();
    }

    @Test
    public void testQuitAction() {
        producer.getQuitAction().actionPerformed(null);
        verify(frame).quit();
    }

    @Test
    public void testClearLogOutput() {
        RSyntaxTextArea logger = new RSyntaxTextArea("line one\nline two");
        when(frame.getLoggerTA()).thenReturn(logger);

        producer.getClearLogOutputAction().actionPerformed(null);

        assertEquals("", logger.getText());
        assertEquals(0, logger.getCaretPosition());
    }

    @Test
    public void testFindActionReusesItsDialog() {
        try (MockedConstruction<FindReplaceDialog> dialogs = mockConstruction(FindReplaceDialog.class)) {
            Action find = producer.getFindAction();
            find.actionPerformed(null);
            find.actionPerformed(null);

            assertEquals(1, dialogs.constructed().size());
            verify(dialogs.constructed().get(0), times(2)).setVisible(true);
        }
    }

    @Test
    public void testShowVariablesOpensDialogWithProjectVariables() {
        Map<String, String> variables = Map.of("host", "example.com");
        when(frame.getProjectVariables()).thenReturn(variables);
        List<Object> dialogArgs = new ArrayList<>();
        try (MockedConstruction<VariableDialog> dialogs = mockConstruction(VariableDialog.class,
                (mock, context) -> dialogArgs.addAll(context.arguments()))) {
            producer.getShowVariablesAction().actionPerformed(null);

            assertEquals(List.of(frame, variables), dialogArgs);
            verify(dialogs.constructed().get(0)).setVisible(true);
        }
    }

    @Test
    public void testShowVariablesReportsDialogFailure() {
        try (MockedConstruction<VariableDialog> dialogs = mockConstruction(VariableDialog.class,
                (mock, context) -> doThrow(new IllegalStateException("boom")).when(mock).setVisible(true))) {
            String error = captureError(() -> producer.getShowVariablesAction().actionPerformed(null));
            assertTrue(error.startsWith("Error downloading datafiles: "), error);
            assertTrue(error.contains("boom"), error);
        }
    }

    @Test
    public void testOpenLoadsWorkloadFromFile() throws IOException {
        File script = writeFile("script_H.xml", WORKLOAD_XML);
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.APPROVE_OPTION, script)) {
            producer.getOpenAction().actionPerformed(null);
        }

        verify(frame).setCurrentWorkload(argThat(w -> w != null && "UC1".equals(w.getName())));
        verify(frame).setCurrentTitle("Workload: UC1");
        verify(frame).setScriptSource(argThat(s -> s.getSource() == SourceType.file
                && script.getAbsolutePath().equals(s.getId())));
    }

    @Test
    public void testOpenFiltersAgentXmlFiles() {
        ArgumentCaptor<FileFilter> filter = ArgumentCaptor.forClass(FileFilter.class);
        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class)) {
            producer.getOpenAction();
            verify(choosers.constructed().get(0)).setFileFilter(filter.capture());
        }

        assertEquals("Agent XML Files", filter.getValue().getDescription());
        assertTrue(filter.getValue().accept(new File("my_script_H.xml")));
        assertFalse(filter.getValue().accept(new File("notes.txt")));
        assertTrue(filter.getValue().accept(tempDir.toFile()));
    }

    @Test
    public void testOpenCancelledDoesNothing() {
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.CANCEL_OPTION, null)) {
            producer.getOpenAction().actionPerformed(null);
        }

        verify(frame, never()).setCurrentWorkload(any());
        verify(frame, never()).setScriptSource(any());
    }

    @Test
    public void testOpenMissingFileShowsError() {
        File missing = tempDir.resolve("missing_H.xml").toFile();
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.APPROVE_OPTION, missing);
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            producer.getOpenAction().actionPerformed(null);

            pane.verify(() -> JOptionPane.showMessageDialog(eq(frame), any(), eq("Error unmarshalling xml"),
                    eq(JOptionPane.ERROR_MESSAGE)));
        }
        verify(frame, never()).setScriptSource(any());
    }

    @Test
    public void testOpenInvalidXmlShowsError() throws IOException {
        File script = writeFile("bad_H.xml", "<workload");
        try (MockedConstruction<JFileChooser> choosers = mockChooser(JFileChooser.APPROVE_OPTION, script);
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            producer.getOpenAction().actionPerformed(null);

            pane.verify(() -> JOptionPane.showMessageDialog(eq(frame), any(), eq("Error unmarshalling xml"),
                    eq(JOptionPane.ERROR_MESSAGE)));
        }
        verify(frame, never()).setCurrentWorkload(any());
    }

    @Test
    public void testReloadWithoutSourceShowsMessage() {
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            producer.getReloadAction().actionPerformed(null);

            pane.verify(() -> JOptionPane.showMessageDialog(frame,
                    "Scripts can only be reloaded if they have been loaded first.", "Load Script from source",
                    JOptionPane.ERROR_MESSAGE));
        }
        verify(frame, never()).startWaiting();
    }

    @Test
    public void testReloadFromFile() throws IOException {
        File script = writeFile("script_H.xml", WORKLOAD_XML);
        when(frame.getScriptSource()).thenReturn(new ScriptSource(script.getAbsolutePath(), SourceType.file));

        producer.getReloadAction().actionPerformed(null);

        verify(frame).startWaiting();
        verify(frame).setCurrentWorkload(isNull());
        verifyWorkloadLoadedInBackground();
    }

    @Test
    public void testReloadFromScript() throws Exception {
        when(frame.getScriptSource()).thenReturn(new ScriptSource("7", SourceType.script));
        when(scriptClient.downloadHarnessScript(7)).thenReturn(WORKLOAD_XML);

        producer.getReloadAction().actionPerformed(null);

        verifyWorkloadLoadedInBackground();
    }

    @Test
    public void testReloadFromProject() throws Exception {
        when(frame.getScriptSource()).thenReturn(new ScriptSource("9", SourceType.project));
        when(projectClient.downloadTestScriptForProject(9)).thenReturn(WORKLOAD_XML);

        producer.getReloadAction().actionPerformed(null);

        verifyWorkloadLoadedInBackground();
    }

    @Test
    public void testOpenScriptSortsNewestFirstAndLoadsSelection() throws Exception {
        ScriptDescription older = ScriptDescription.builder().withId(1).withName("older")
                .withCreated(new Date(1000)).build();
        ScriptDescription newer = ScriptDescription.builder().withId(2).withName("newer")
                .withCreated(new Date(2000)).build();
        when(scriptClient.getScripts()).thenReturn(ScriptDescriptionContainer.builder()
                .withScripts(List.of(older, newer)).build()); // unmodifiable; the action sorts a copy
        when(scriptClient.downloadHarnessScript(2)).thenReturn(WORKLOAD_XML);
        List<Object> dialogArgs = new ArrayList<>();

        try (MockedConstruction<SelectDialog> dialogs = mockSelectDialog(dialogArgs,
                dialog -> when(dialog.getSelectedObject()).thenReturn(newer))) {
            producer.getOpenScriptAction().actionPerformed(null);
            verify(dialogs.constructed().get(0)).setVisible(true);
        }

        assertEquals(List.of(newer, older), dialogArgs.get(1));
        verify(frame).startWaiting();
        verify(frame, timeout(TIMEOUT_MS)).setCurrentTitle("Selected Script: newer");
        verify(frame, timeout(TIMEOUT_MS)).setScriptSource(argThat(s -> s.getSource() == SourceType.script
                && "2".equals(s.getId())));
        verify(frame, timeout(TIMEOUT_MS)).stopWaiting();
    }

    @Test
    public void testOpenScriptWithoutSelectionDoesNothing() throws Exception {
        when(scriptClient.getScripts()).thenReturn(ScriptDescriptionContainer.builder()
                .withScripts(new ArrayList<>()).build());

        try (MockedConstruction<SelectDialog> dialogs = mockSelectDialog(new ArrayList<>(), dialog -> { })) {
            producer.getOpenScriptAction().actionPerformed(null);
        }

        verify(frame, never()).startWaiting();
        verify(scriptClient, never()).downloadHarnessScript(anyInt());
    }

    @Test
    public void testOpenScriptReportsInvalidUrl() throws Exception {
        when(scriptClient.getScripts()).thenThrow(new IOException("no host"));

        assertEquals("Error: Empty or Invalid Tank URL",
                captureError(() -> producer.getOpenScriptAction().actionPerformed(null)));
    }

    @Test
    public void testOpenScriptReportsOtherFailures() throws Exception {
        when(scriptClient.getScripts()).thenThrow(new IllegalStateException("bad"));

        assertTrue(captureError(() -> producer.getOpenScriptAction().actionPerformed(null))
                .startsWith("Error downloading scripts: "));
    }

    @Test
    public void testSelectDataFilesSetsSelectionOnFrame() throws Exception {
        DataFileDescriptor a = DataFileDescriptor.builder().withId(1).withName("a.csv").build();
        DataFileDescriptor b = DataFileDescriptor.builder().withId(2).withName("b.csv").build();
        when(dataFileClient.getDatafiles()).thenReturn(dataFiles(a, b));
        List<Object> dialogArgs = new ArrayList<>();

        try (MockedConstruction<SelectDialog> dialogs = mockSelectDialog(dialogArgs,
                dialog -> when(dialog.getSelectedObjects()).thenReturn(List.of(a)))) {
            producer.getSelectDataFileAction().actionPerformed(null);
        }

        // sorted by name, descending, and offered for multiple selection
        assertEquals(List.of(b, a), dialogArgs.get(1));
        assertEquals(Boolean.FALSE, dialogArgs.get(3));
        verify(frame).setDataFiles(List.of(a));
    }

    @Test
    public void testSelectDataFilesWithoutSelectionKeepsCurrentFiles() throws Exception {
        when(dataFileClient.getDatafiles()).thenReturn(dataFiles());

        try (MockedConstruction<SelectDialog> dialogs = mockSelectDialog(new ArrayList<>(),
                dialog -> when(dialog.getSelectedObjects()).thenReturn(List.of()))) {
            producer.getSelectDataFileAction().actionPerformed(null);
        }

        verify(frame, never()).setDataFiles(any());
    }

    @Test
    public void testSelectDataFilesReportsErrors() throws Exception {
        when(dataFileClient.getDatafiles()).thenThrow(new IOException("no host"));
        assertEquals("Error: Empty or Invalid Tank URL",
                captureError(() -> producer.getSelectDataFileAction().actionPerformed(null)));

        reset(dataFileClient);
        when(dataFileClient.getDatafiles()).thenThrow(new IllegalStateException("bad"));
        assertTrue(captureError(() -> producer.getSelectDataFileAction().actionPerformed(null))
                .startsWith("Error downloading datafiles: "));
    }

    @Test
    public void testOpenProjectLoadsSelectedProject() throws Exception {
        ProjectTO project = ProjectTO.builder().withId(3).withName("Checkout").build();
        // when two projects share a name, the first one listed is loaded
        Map<Integer, String> names = new LinkedHashMap<>();
        names.put(3, "Checkout");
        names.put(4, "Checkout");
        when(projectClient.getProjectNames()).thenReturn(names);
        when(projectClient.getProject(3)).thenReturn(project);
        when(projectClient.downloadTestScriptForProject(3)).thenReturn(WORKLOAD_XML);

        try (MockedConstruction<SelectDialog> dialogs = mockSelectDialog(new ArrayList<>(),
                dialog -> when(dialog.getSelectedObject()).thenReturn("Checkout"))) {
            producer.getOpenProjectAction().actionPerformed(null);
        }

        verify(projectClient, never()).getProject(4);
        verify(frame).startWaiting();
        verify(frame, timeout(TIMEOUT_MS)).setDataFromProject(project);
        verify(frame, timeout(TIMEOUT_MS)).setCurrentTitle("Selected Project: Checkout");
        verify(frame, timeout(TIMEOUT_MS)).setScriptSource(argThat(s -> s.getSource() == SourceType.project
                && "3".equals(s.getId())));
        verify(frame, timeout(TIMEOUT_MS).atLeastOnce()).stopWaiting();
    }

    @Test
    public void testOpenProjectWithoutSelectionDoesNothing() throws Exception {
        when(projectClient.getProjectNames()).thenReturn(Map.of(3, "Checkout"));

        try (MockedConstruction<SelectDialog> dialogs = mockSelectDialog(new ArrayList<>(), dialog -> { })) {
            producer.getOpenProjectAction().actionPerformed(null);
        }

        verify(projectClient, never()).getProject(anyInt());
        verify(frame, never()).startWaiting();
    }

    @Test
    public void testOpenProjectReportsErrors() throws Exception {
        when(projectClient.getProjectNames()).thenThrow(new IOException("no host"));
        assertEquals("Error: Empty or Invalid Tank URL",
                captureError(() -> producer.getOpenProjectAction().actionPerformed(null)));

        reset(projectClient);
        when(projectClient.getProjectNames()).thenThrow(new IllegalStateException("bad"));
        assertTrue(captureError(() -> producer.getOpenProjectAction().actionPerformed(null))
                .startsWith("Error downloading projects: "));
    }

    @Test
    public void testExportWritesCsvToChosenFile() {
        File csv = tempDir.resolve("out.csv").toFile();
        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class,
                (mock, context) -> {
                    when(mock.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
                    when(mock.getSelectedFile()).thenReturn(csv);
                })) {
            producer.getCSVAction().actionPerformed(null);
        }

        verify(frame).exportCSV(csv);
    }

    @Test
    public void testExportCancelledDoesNothing() {
        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class,
                (mock, context) -> when(mock.showSaveDialog(any())).thenReturn(JFileChooser.CANCEL_OPTION))) {
            producer.getCSVAction().actionPerformed(null);
        }

        verify(frame, never()).exportCSV(any());
    }

    @Test
    public void testSaveLogWritesLoggerText() throws IOException {
        RSyntaxTextArea logger = new RSyntaxTextArea("log output");
        when(frame.getLoggerTA()).thenReturn(logger);
        File target = tempDir.resolve("debuggerLog.txt").toFile();

        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class,
                (mock, context) -> {
                    when(mock.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
                    when(mock.getSelectedFile()).thenReturn(target);
                })) {
            Action saveLog = producer.getSaveLogAction();
            assertSame(saveLog, producer.getSaveLogAction());
            saveLog.actionPerformed(null);
        }

        assertEquals("log output", Files.readString(target.toPath()));
    }

    @Test
    public void testSaveLogWithEmptyLogShowsMessage() {
        RSyntaxTextArea logger = new RSyntaxTextArea("");
        when(frame.getLoggerTA()).thenReturn(logger);

        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class);
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            producer.getSaveLogAction().actionPerformed(null);

            pane.verify(() -> JOptionPane.showMessageDialog(any(), eq("There is no data to write.")));
            verify(choosers.constructed().get(0), never()).showSaveDialog(any());
        }
    }

    @Test
    public void testSaveRequestResponseUsesPanelAsSource() {
        RequestResponsePanel panel = mock(RequestResponsePanel.class);
        when(frame.getRequestResponsePanel()).thenReturn(panel);

        try (MockedConstruction<JFileChooser> choosers = mockConstruction(JFileChooser.class);
                MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            Action save = producer.getSaveReqResponseAction();
            assertSame(save, producer.getSaveReqResponseAction());
            assertEquals("Save Requests/Responses As...", save.getValue(Action.NAME));
            save.actionPerformed(null);

            // the panel has no data, so nothing is offered for saving
            verify(panel).hasData();
            pane.verify(() -> JOptionPane.showMessageDialog(any(), eq("There is no data to write.")));
        }
    }

    @Test
    public void testSelectTankConnectsToChosenInstance() {
        JComboBox<TankClientChoice> clientChooser = new JComboBox<>();
        when(frame.getTankClientChooser()).thenReturn(clientChooser);
        Action selectTank = producer.getSelectTankAction();
        List<List<?>> scriptClientArgs = new ArrayList<>();

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class);
                MockedStatic<PanelBuilder> panelBuilder = mockStatic(PanelBuilder.class);
                MockedConstruction<ScriptClient> scripts = mockConstruction(ScriptClient.class,
                        (mock, context) -> scriptClientArgs.add(context.arguments()));
                MockedConstruction<ProjectClient> projects = mockConstruction(ProjectClient.class);
                MockedConstruction<AgentClient> agents = mockConstruction(AgentClient.class);
                MockedConstruction<DataFileClient> dataFiles = mockConstruction(DataFileClient.class)) {
            pane.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);

            selectTank.actionPerformed(null);

            // debugger.properties lists "TANK (http://tank-url)"; its URL is pinged, then used
            assertEquals(List.of("http://tank-url", ""), scriptClientArgs.get(0));
            verify(scripts.constructed().get(0)).ping();
            panelBuilder.verify(() -> PanelBuilder.updateServiceUrl("http://tank-url", ""));
            assertSame(scripts.constructed().get(1), producer.getScriptClient());
            assertSame(projects.constructed().get(0), producer.getProjectClient());
            assertSame(dataFiles.constructed().get(0), producer.getDataFileClient());
        }
        // the new agent client returns no definitions, so the default client choices are offered
        assertEquals(3, clientChooser.getItemCount());
        assertEquals(1, clientChooser.getSelectedIndex());
    }

    @Test
    public void testSelectTankAddsNoListenerEachTimeItIsUsed() throws Exception {
        Action selectTank = producer.getSelectTankAction();
        JComboBox<?> comboBox = comboBoxOf(selectTank);
        int listeners = comboBox.getActionListeners().length;

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            pane.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.CANCEL_OPTION);

            selectTank.actionPerformed(null);
            selectTank.actionPerformed(null);
        }

        assertEquals(listeners, comboBox.getActionListeners().length);
    }

    @Test
    public void testSelectTankNormalizesTypedUrl() throws Exception {
        JComboBox<TankClientChoice> clientChooser = new JComboBox<>();
        when(frame.getTankClientChooser()).thenReturn(clientChooser);
        Action selectTank = producer.getSelectTankAction();
        comboBoxOf(selectTank).setSelectedItem("myhost:8080/");
        List<List<?>> scriptClientArgs = new ArrayList<>();

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class);
                MockedStatic<PanelBuilder> panelBuilder = mockStatic(PanelBuilder.class);
                MockedConstruction<ScriptClient> scripts = mockConstruction(ScriptClient.class,
                        (mock, context) -> scriptClientArgs.add(context.arguments()));
                MockedConstruction<ProjectClient> projects = mockConstruction(ProjectClient.class);
                MockedConstruction<AgentClient> agents = mockConstruction(AgentClient.class);
                MockedConstruction<DataFileClient> dataFiles = mockConstruction(DataFileClient.class)) {
            pane.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);

            selectTank.actionPerformed(null);

            assertEquals(List.of("http://myhost:8080", ""), scriptClientArgs.get(0));
            panelBuilder.verify(() -> PanelBuilder.updateServiceUrl("http://myhost:8080", ""));
        }
    }

    @Test
    public void testSelectTankReportsUnreachableInstance() {
        Action selectTank = producer.getSelectTankAction();
        Runnable queued;

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class);
                MockedStatic<PanelBuilder> panelBuilder = mockStatic(PanelBuilder.class);
                MockedConstruction<ScriptClient> scripts = mockConstruction(ScriptClient.class,
                        (mock, context) -> doThrow(new IllegalStateException("refused")).when(mock).ping())) {
            pane.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.OK_OPTION);

            selectTank.actionPerformed(null);
            queued = takeQueuedError();

            panelBuilder.verifyNoInteractions();
        }
        String error = errorShownBy(queued);
        assertTrue(error.startsWith("Cannot connect to Tank at the url http://tank-url."), error);
        assertSame(scriptClient, producer.getScriptClient());
    }

    @Test
    public void testSelectTankCancelledKeepsCurrentInstance() {
        Action selectTank = producer.getSelectTankAction();

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class);
                MockedConstruction<ScriptClient> scripts = mockConstruction(ScriptClient.class)) {
            pane.when(() -> JOptionPane.showConfirmDialog(any(), any(), anyString(), anyInt(), anyInt()))
                    .thenReturn(JOptionPane.CANCEL_OPTION);

            selectTank.actionPerformed(null);

            assertTrue(scripts.constructed().isEmpty());
        }
        assertSame(scriptClient, producer.getScriptClient());
    }

    @Test
    public void testClientChoicesComeFromController() {
        when(agentClient.getClients()).thenReturn(new TankHttpClientDefinitionContainer(List.of(
                new TankHttpClientDefinition("Client A", "com.example.A"),
                new TankHttpClientDefinition("Client B", "com.example.B")), "com.example.B"));
        JComboBox<TankClientChoice> chooser = new JComboBox<>();
        chooser.addItem(new TankClientChoice("stale", "com.example.Stale"));

        producer.setChoiceComboBoxOptions(chooser);

        assertEquals(2, chooser.getItemCount());
        assertEquals("Client A", chooser.getItemAt(0).toString());
        assertEquals(1, chooser.getSelectedIndex());
    }

    @Test
    public void testClientChoicesFallBackWhenControllerUnavailable() {
        when(agentClient.getClients()).thenThrow(new IllegalStateException("offline"));
        JComboBox<TankClientChoice> chooser = new JComboBox<>();

        producer.setChoiceComboBoxOptions(chooser);

        assertEquals(3, chooser.getItemCount());
        assertEquals("Apache HttpClient 4.5", chooser.getSelectedItem().toString());
    }

    @Test
    public void testGetIcon() {
        assertNotNull(ActionProducer.getIcon("search"));
    }

    private void verifyWorkloadLoadedInBackground() {
        verify(frame, timeout(TIMEOUT_MS)).setCurrentWorkload(argThat(w -> w != null && "UC1".equals(w.getName())));
        verify(frame, timeout(TIMEOUT_MS)).setCurrentTitle("Workload: UC1");
        verify(frame, timeout(TIMEOUT_MS)).stopWaiting();
    }

    private static DataFileDescriptorContainer dataFiles(DataFileDescriptor... files) {
        // the builder's list is unmodifiable; the action sorts a copy of it
        DataFileDescriptorContainer.DataFileDescriptorContainerBuilder builder = DataFileDescriptorContainer.builder();
        for (DataFileDescriptor file : files) {
            builder.withDataFile(file);
        }
        return builder.build();
    }

    private File writeFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file.toFile();
    }

    private static MockedConstruction<JFileChooser> mockChooser(int option, File selected) {
        return mockConstruction(JFileChooser.class, (mock, context) -> {
            when(mock.showOpenDialog(any())).thenReturn(option);
            when(mock.getSelectedFile()).thenReturn(selected);
        });
    }

    @SuppressWarnings("rawtypes")
    private static MockedConstruction<SelectDialog> mockSelectDialog(List<Object> args, Consumer<SelectDialog> stubbing) {
        return mockConstruction(SelectDialog.class, (mock, context) -> {
            args.addAll(context.arguments());
            stubbing.accept(mock);
        });
    }

    @SuppressWarnings("unchecked")
    private static JComboBox<String> comboBoxOf(Action selectTank) throws ReflectiveOperationException {
        Field field = selectTank.getClass().getDeclaredField("comboBox");
        field.setAccessible(true);
        return (JComboBox<String>) field.get(selectTank);
    }

    /**
     * Runs an action that reports an error through ActionProducer.showError and returns the message it would have
     * shown.
     */
    private String captureError(Runnable action) {
        action.run();
        return errorShownBy(takeQueuedError());
    }

    /**
     * Removes and returns the single error task ActionProducer queued on the test thread.
     */
    private Runnable takeQueuedError() {
        assertEquals(1, queuedErrors.size(), "expected exactly one queued error");
        return queuedErrors.remove(0);
    }

    /**
     * Runs a queued error task against a mocked JOptionPane and returns the message it would have shown.
     */
    private String errorShownBy(Runnable queued) {
        ArgumentCaptor<Object> message = ArgumentCaptor.forClass(Object.class);
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            queued.run();
            pane.verify(() -> JOptionPane.showMessageDialog(eq(frame), message.capture(), eq("Error"),
                    eq(JOptionPane.ERROR_MESSAGE)));
        }
        return (String) message.getValue();
    }
}
