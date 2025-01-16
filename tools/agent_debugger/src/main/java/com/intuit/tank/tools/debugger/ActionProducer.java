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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.intuit.tank.clients.AgentClient;
import com.intuit.tank.clients.DataFileClient;
import com.intuit.tank.clients.ProjectClient;
import com.intuit.tank.clients.ScriptClient;
import com.intuit.tank.agent.models.TankHttpClientDefinition;
import com.intuit.tank.agent.models.TankHttpClientDefinitionContainer;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.script.models.ScriptDescription;
import com.intuit.tank.script.models.ScriptDescriptionContainer;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.tools.debugger.FindReplaceDialog.DialogType;

public class ActionProducer {
    private static final Logger LOG = LogManager.getLogger(ActionProducer.class);

    private static final String DEBUGGER_PROPERTIES = "debugger.properties";
    private static final String TS_INSTANCE_START = "tank.instance.";
    public static final String ACTION_OPEN = "Open File";
    public static final String ACTION_CHOOSE_SCRIPT = "Choose Script";
    public static final String ACTION_CHOOSE_DATAFILE = "Choose Datafile";
    public static final String ACTION_SHOW_VARIABLES = "Show Project Variables";
    public static final String ACTION_CHOOSE_PROJECT = "Choose Project";
    public static final String ACTION_RELOAD = "Reload";
    public static final String ACTION_QUIT = "Quit";
    public static final String ACTION_START = "Start";
    public static final String ACTION_SELECT_TANK = "Select Tank Instance...";
    public static final String ACTION_NEXT = "Next Step";
    public static final String ACTION_STOP = "Stop";
    public static final String ACTION_RUN_TO = "Run to Breakpoint...";
    private static final String ACTION_PAUSE = "Pause";
    private static final String ACTION_EXPORT = "Export Responses";
    private static final String ACTION_FIND = "Find...";
    private static final String ACTION_SKIP = "Skip";
    private static final String ACTION_SKIP_STEP = "Toggle Skip Step";
    private static final String ACTION_TOGGLE_BREAKPOINT = "Toggle Breakpoint";
    private static final String ACTION_REMOVE_BOOKMARKS = "Clear All Breakpoints";
    private static final String ACTION_REMOVE_SKIP = "Clear All Skips";
    private static final String ACTION_SAVE_LOG = "Save Log As...";
    private static final String ACTION_SAVE_OUTPUT = "Save Requests/Responses As...";
    private static final String SAVE_OUTPUT_DEFAULT_FILE = "debuggerOutput.txt";
    private static final String SAVE_LOG_DEFAULT_FILE = "debuggerLog.txt";
    private static final String ACTION_CLEAR_LOG = "Clear Log Output";
    private static final String ACTION_SELECT_CLIENT = "Select Client...";

    private AgentDebuggerFrame debuggerFrame;
    private Map<String, Action> actionMap = new HashMap<String, Action>();
    private int menuActionMods = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    private JFileChooser jFileChooser = null;
    private ScriptClient scriptClient;
    private AgentClient agentClient;
    private ProjectClient projectClient;
    private DataFileClient dataFileClient;

    /**
     * 
     * @param debuggerFrame
     * @param serviceUrl
     */
    public ActionProducer(AgentDebuggerFrame debuggerFrame, String serviceUrl, String token) {
        super();
        this.debuggerFrame = debuggerFrame;
        this.scriptClient = new ScriptClient(serviceUrl, token);
        this.projectClient = new ProjectClient(serviceUrl, token);
        this.agentClient = new AgentClient(serviceUrl, token);
        this.dataFileClient = new DataFileClient(serviceUrl, token);
    }

    /**
     * @return the scriptClient
     */
    public ScriptClient getScriptClient() {
        return scriptClient;
    }

    /**
     * @return the projectClient
     */
    public ProjectClient getProjectClient() {
        return projectClient;
    }

    /**
     * @return the dataFileClient
     */
    public DataFileClient getDataFileClient() {
        return dataFileClient;
    }

    /**
     * 
     * @param serviceUrl
     */
    public void setServiceUrl(String serviceUrl, String token) {
        this.scriptClient = new ScriptClient(serviceUrl, token);
        this.projectClient = new ProjectClient(serviceUrl, token);
        this.dataFileClient = new DataFileClient(serviceUrl, token);
        this.agentClient = new AgentClient(serviceUrl, token);
        PanelBuilder.updateServiceUrl(serviceUrl, token);
        setChoiceComboBoxOptions(debuggerFrame.getTankClientChooser());
    }

    /**
     * 
     * @return
     */
    public Action getSaveLogAction() {
        Action ret = actionMap.get(ACTION_SAVE_LOG);
        if (ret == null) {
            OutputTextWriter loggerOutputWriter = new OutputTextWriter() {
                @Override
                public void writeText(Writer w) {
                    try {
                        w.write(debuggerFrame.getLoggerTA().getText());
                    } catch (IOException e) {
                        System.err.println("Error writing log");
                        e.printStackTrace();
                    }
                }

                @Override
                public boolean hasData() {
                    return debuggerFrame.getLoggerTA() != null
                            && StringUtils.isNotBlank(debuggerFrame.getLoggerTA().getText());
                }
            };
            ret = new SaveTextAction(debuggerFrame, ACTION_SAVE_LOG, SAVE_LOG_DEFAULT_FILE, loggerOutputWriter);
            actionMap.put(ACTION_SAVE_LOG, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getClearLogOutputAction() {
        Action ret = actionMap.get(ACTION_CLEAR_LOG);
        if (ret == null) {
            ret = new AbstractAction(ACTION_CLEAR_LOG, getIcon("bin_closed.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        debuggerFrame.getLoggerTA().setText("");
                        debuggerFrame.getLoggerTA().setCaretPosition(0);
                    } catch (HeadlessException e) {
                        showError("Error opening file: " + e);
                    }
                }
            };
            actionMap.put(ACTION_CLEAR_LOG, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getSaveReqResponseAction() {
        Action ret = actionMap.get(ACTION_SAVE_OUTPUT);
        if (ret == null) {
            ret = new SaveTextAction(debuggerFrame, ACTION_SAVE_OUTPUT, SAVE_OUTPUT_DEFAULT_FILE,
                    debuggerFrame.getRequestResponsePanel());
            actionMap.put(ACTION_SAVE_OUTPUT, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getFindAction() {
        Action ret = actionMap.get(ACTION_FIND);
        if (ret == null) {
            ret = new AbstractAction(ACTION_FIND, getIcon("find.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;
                private FindReplaceDialog dialog;

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        if (dialog == null) {
                            dialog = new FindReplaceDialog(debuggerFrame, DialogType.SEARCH);
                        }
                        dialog.setVisible(true);
                    } catch (HeadlessException e) {
                        showError("Error opening file: " + e);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Find in script.");
            ret.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', menuActionMods));
            ret.putValue(Action.MNEMONIC_KEY, new Integer('F'));
            ret.setEnabled(false);
            actionMap.put(ACTION_FIND, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getOpenAction() {
        Action ret = actionMap.get(ACTION_OPEN);
        if (ret == null) {
            jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileFilter() {

                @Override
                public String getDescription() {
                    return "Agent XML Files";
                }

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith("_h.xml");
                }
            });
            ret = new AbstractAction(ACTION_OPEN, getIcon("script_go.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        int option = jFileChooser.showOpenDialog(debuggerFrame);
                        if (option != JFileChooser.CANCEL_OPTION) {
                            File selectedFile = jFileChooser.getSelectedFile();
                            try {
                                String scriptXml = FileUtils.readFileToString(selectedFile, StandardCharsets.UTF_8);
                                setFromString(scriptXml);
                                debuggerFrame.setScriptSource(new ScriptSource(selectedFile.getAbsolutePath(),
                                        SourceType.file));
                            } catch (Exception e) {
                                LOG.error("Error reading file " + selectedFile.getName() + ": " + e);
                                JOptionPane.showMessageDialog(debuggerFrame, e.getMessage(), "Error unmarshalling xml",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (HeadlessException e) {
                        showError("Error opening file: " + e);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Open Agent xml from filesystem.");
            ret.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('O', menuActionMods));
            ret.putValue(Action.MNEMONIC_KEY, new Integer('O'));
            actionMap.put(ACTION_OPEN, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getReloadAction() {
        Action ret = actionMap.get(ACTION_RELOAD);
        if (ret == null) {
            ret = new AbstractAction(ACTION_RELOAD, getIcon("refresh.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        final ScriptSource scriptSource = debuggerFrame.getScriptSource();
                        if (scriptSource != null) {
                            debuggerFrame.startWaiting();
                            setFromString(null);
                            // get script in thread
                            new Thread( () ->{
                                try {
                                    String scriptXml = null;
                                    if (scriptSource.getSource() == SourceType.file) {
                                        scriptXml = FileUtils.readFileToString(new File(scriptSource.getId()), StandardCharsets.UTF_8);
                                    } else if (scriptSource.getSource() == SourceType.script) {
                                        scriptXml = scriptClient.downloadHarnessScript(Integer
                                                .parseInt(scriptSource
                                                        .getId()));
                                    } else if (scriptSource.getSource() == SourceType.project) {
                                        scriptXml = projectClient.downloadTestScriptForProject(Integer
                                                .parseInt(scriptSource.getId()));
                                    }
                                    if (scriptXml != null) {
                                        setFromString(scriptXml);
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                    debuggerFrame.stopWaiting();
                                    showError("Error opening from source: " + e1);
                                } finally {
                                    debuggerFrame.stopWaiting();
                                }
                            }).start();

                        } else {
                            JOptionPane.showMessageDialog(debuggerFrame,
                                    "Scripts can only be reloaded if they have been loaded first.",
                                    "Load Script from source",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (HeadlessException e) {
                        showError("Error opening file: " + e);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Reload scrpt from source.");
            ret.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', menuActionMods));
            ret.putValue(Action.MNEMONIC_KEY, new Integer('R'));
            ret.setEnabled(false);
            actionMap.put(ACTION_RELOAD, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getSelectTankAction() {
        Action ret = actionMap.get(ACTION_SELECT_TANK);
        if (ret == null) {
            ret = new AbstractAction(ACTION_SELECT_TANK) {
                private static final long serialVersionUID = 1L;
                JComboBox<String> comboBox = getComboBox();
                @Override
                public void actionPerformed(ActionEvent event) {
                    JPanel panel = new JPanel();
                    panel.setPreferredSize(new Dimension(400,125));
                    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
                    JPanel baseURLPanel = new JPanel();
                    baseURLPanel.setBorder(BorderFactory.createTitledBorder("Intuit/Tank Base URL"));
                    JPanel tokenPanel = new JPanel();
                    comboBox.setPreferredSize(new Dimension(375,25));
                    tokenPanel.setBorder(BorderFactory.createTitledBorder("Intuit/Tank Token*"));
                    tokenPanel.setToolTipText("Generate a Tank API token by logging into Tank and going to 'Account Settings' on the upper right to create a token for this specific host. ");
                    final JTextField tokenField = new JTextField();
                    tokenField.setPreferredSize(new Dimension(375,25));
                    baseURLPanel.add(comboBox);
                    panel.add(baseURLPanel);
                    tokenPanel.add(tokenField);
                    panel.add(tokenPanel);
                    try {
                        int selected = JOptionPane.showConfirmDialog(debuggerFrame, panel,
                                "Enter the Intuit/Tank URL & Token", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                        if (selected == JOptionPane.OK_OPTION) {
                            String url = (String) comboBox.getSelectedItem();
                            String token = tokenField.getText().trim();
                            if (url != null) {
                                int startInd = url.indexOf('(');
                                int endInd = url.indexOf(')');
                                if (startInd != -1 && endInd != -1) {
                                    url = url.substring(startInd + 1, endInd);
                                }
                                url = StringUtils.removeEndIgnoreCase(url, "/");
                                if (!url.startsWith("http")) {
                                    url = "http://" + url;
                                }
                                try {
                                    new ScriptClient(url, token).ping();
                                    setServiceUrl(url, token);
                                } catch (Exception e) {
                                    showError("Cannot connect to Tank at the url " + url
                                            + ". \nExample: http://tank.mysite.com/");
                                }
                            }
                        }
                    } catch (HeadlessException e) {
                        showError("Error opening file: " + e);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Enter a Tank URL.");
            actionMap.put(ACTION_SELECT_TANK, ret);
        }
        return ret;
    }
   
    
    public void setChoiceComboBoxOptions(JComboBox<TankClientChoice> cb) {
        cb.removeAllItems();
        try {
            TankHttpClientDefinitionContainer clientDefinitions = agentClient.getClients();
            for(TankHttpClientDefinition def : clientDefinitions.getDefinitions()) {
                TankClientChoice c = new TankClientChoice(def.getName(), def.getClassName());
                cb.addItem(c);
                if (def.getClassName().equals(clientDefinitions.getDefaultDefinition())) {
                    cb.setSelectedItem(c);
                }
            }
        } catch (Exception e) {
            // This is just a filler for the UI before you select a tank instance to import from.
            cb.addItem(new TankClientChoice("Apache HttpClient 3.1", "com.intuit.tank.httpclient3.TankHttpClient3"));
            cb.addItem(new TankClientChoice("Apache HttpClient 4.5", "com.intuit.tank.httpclient4.TankHttpClient4"));
            cb.addItem(new TankClientChoice("JDK Http Client", "com.intuit.tank.httpclientjdk.TankHttpClientJDK"));
            cb.setSelectedIndex(2);
        }
    }

    private static JComboBox<String> getComboBox() {
        JComboBox<String> cb = new JComboBox<String>();
        cb.setEditable(true);
        Properties props = new Properties();
        File f = new File(DEBUGGER_PROPERTIES);
        if (!f.exists()) { // load default
            try (   InputStream in = ActionProducer.class.getClassLoader().getResourceAsStream(DEBUGGER_PROPERTIES);
                    OutputStream out = new FileOutputStream(f) ) {
                IOUtils.copy(in, out);
            } catch (Exception e) {
                LOG.error("Cannot write properties: " + e, e);
            }
        }
        try ( InputStream in = new FileInputStream(f) ) {
            props.load(in);
            for (Object o : props.keySet()) {
                String key = (String) o;
                if (key.startsWith(TS_INSTANCE_START)) {
                    String name = key.substring(TS_INSTANCE_START.length());
                    String value = props.getProperty(key);
                    cb.addItem(name + " (" + value + ")");
                }
            }
        } catch (Exception e) {
            LOG.error("Cannot read properties: " + e, e);
        }
        return cb;
    }

    /**
     * 
     * @return
     */
    public Action getOpenScriptAction() {
        Action ret = actionMap.get(ACTION_CHOOSE_SCRIPT);
        if (ret == null) {
            ret = new AbstractAction(ACTION_CHOOSE_SCRIPT, getIcon("script_lightning.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        ScriptDescriptionContainer scriptDescriptions = scriptClient.getScripts();
                        List<ScriptDescription> scripts = scriptDescriptions.getScripts();
                        scripts.sort((ScriptDescription o1, ScriptDescription o2) ->
                                o2.getCreated().compareTo(o1.getCreated()));
                        SelectDialog<ScriptDescription> selectDialog = new SelectDialog<ScriptDescription>(
                                debuggerFrame,
                                scripts, "script");
                        selectDialog.setVisible(true);
                        final ScriptDescription scriptSelected = selectDialog.getSelectedObject();
                        if (scriptSelected != null) {
                            debuggerFrame.startWaiting();
                            setFromString(null);
                            // get script in thread
                            new Thread( () -> {
                                try {
                                    String scriptXml = scriptClient.downloadHarnessScript(scriptSelected
                                            .getId());
                                    setFromString(scriptXml);
                                    debuggerFrame.setCurrentTitle("Selected Script: " + scriptSelected.getName());
                                    debuggerFrame.setScriptSource(new ScriptSource(scriptSelected.getId()
                                            .toString(), SourceType.script));
                                } catch (Exception e1) {
                                    debuggerFrame.stopWaiting();
                                    showError("Error downloading script: " + e1);
                                } finally {
                                    debuggerFrame.stopWaiting();
                                }
                            }).start();
                        }
                    } catch (InterruptedException | IOException e1) {
                        showError("Error: Empty or Invalid Tank URL");
                        LOG.error("Error: Empty or Invalid Tank URL");
                    } catch (Exception e2) {
                        showError("Error downloading scripts: " + e2);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Choose Script from Tank.");
            actionMap.put(ACTION_CHOOSE_SCRIPT, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getSelectDataFileAction() {
        Action ret = actionMap.get(ACTION_CHOOSE_DATAFILE);
        if (ret == null) {
            ret = new AbstractAction(ACTION_CHOOSE_DATAFILE, getIcon("table_lightning.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        List<DataFileDescriptor> dataFiles = dataFileClient.getDatafiles().getDataFiles();
                        dataFiles.sort((DataFileDescriptor o1, DataFileDescriptor o2) ->
                                o2.getName().compareTo(o1.getName()));
                        SelectDialog<DataFileDescriptor> selectDialog = new SelectDialog<DataFileDescriptor>(
                                debuggerFrame,
                                dataFiles, "datafiles", false);
                        selectDialog.setVisible(true);
                        List<DataFileDescriptor> selectedObjects = selectDialog.getSelectedObjects();
                        if (!selectedObjects.isEmpty()) {
                            debuggerFrame.setDataFiles(selectedObjects);
                        }
                    } catch (InterruptedException | IOException e1) {
                        showError("Error: Empty or Invalid Tank URL");
                        LOG.error("Error: Empty or Invalid Tank URL");
                    } catch (Exception e2) {
                        showError("Error downloading datafiles: " + e2);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Choose Datafiles from Tank.");
            actionMap.put(ACTION_CHOOSE_DATAFILE, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getShowVariablesAction() {
        Action ret = actionMap.get(ACTION_SHOW_VARIABLES);
        if (ret == null) {
            ret = new AbstractAction(ACTION_SHOW_VARIABLES, getIcon("data_grid.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        new VariableDialog(debuggerFrame, debuggerFrame.getProjectVariables()).setVisible(true);
                    } catch (Exception e1) {
                        showError("Error downloading datafiles: " + e1);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Display and edit project variables.");
            actionMap.put(ACTION_SHOW_VARIABLES, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getOpenProjectAction() {
        Action ret = actionMap.get(ACTION_CHOOSE_PROJECT);
        if (ret == null) {
            ret = new AbstractAction(ACTION_CHOOSE_PROJECT, getIcon("application_lightning.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Map<Integer, String> projectMap = projectClient.getProjectNames();
                        Map<String, Integer> reversedProjectMap = projectMap.entrySet()
                                .stream()
                                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey,
                                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                        List<String> projectNames = new ArrayList<>(projectMap.values());
                        SelectDialog<String> selectDialog = new SelectDialog<>(debuggerFrame,
                                projectNames, "project");
                        selectDialog.setVisible(true);
                        final String projectName = selectDialog.getSelectedObject();
                        if(reversedProjectMap.get(projectName) != null){
                            String projectId = String.valueOf(reversedProjectMap.get(projectName));
                            ProjectTO selected = projectClient.getProject(Integer.parseInt(projectId));
                            if (selected != null) {
                                debuggerFrame.startWaiting();
                                setFromString(null);
                                // get script in thread
                                new Thread( () -> {
                                    try {
                                        String scriptXml = projectClient.downloadTestScriptForProject(selected
                                                .getId());
                                        debuggerFrame.setDataFromProject(selected);
                                        setFromString(scriptXml);
                                        debuggerFrame.setCurrentTitle("Selected Project: " + selected.getName());
                                        debuggerFrame.setScriptSource(new ScriptSource(selected.getId().toString(),
                                                SourceType.project));
                                        debuggerFrame.stopWaiting();
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                        debuggerFrame.stopWaiting();
                                        showError("Error downloading project: " + e1);
                                    } finally {
                                        debuggerFrame.stopWaiting();
                                    }
                                }).start();
                            }
                        }
                    } catch (InterruptedException | IOException e1) {
                        showError("Error: Empty or Invalid Tank URL");
                        LOG.error("Error: Empty or Invalid Tank URL");
                    } catch (Exception e2) {
                        showError("Error downloading projects: " + e2);
                    }
                }

            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Choose Project from Tank.");
            actionMap.put(ACTION_CHOOSE_PROJECT, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getQuitAction() {
        Action ret = actionMap.get(ACTION_QUIT);
        if (ret == null) {
            ret = new AbstractAction(ACTION_QUIT) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.quit();

                }
            };
            ret.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(Character.valueOf('Q'), menuActionMods));
            ret.putValue(Action.MNEMONIC_KEY, new Integer('Q'));
            actionMap.put(ACTION_QUIT, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getStartAction() {
        Action ret = actionMap.get(ACTION_START);
        if (ret == null) {
            ret = new AbstractAction(ACTION_START, getIcon("bug_go.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.start();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Start Debugging Script.");
            actionMap.put(ACTION_START, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getNextStepAction() {
        Action ret = actionMap.get(ACTION_NEXT);
        if (ret == null) {
            ret = new AbstractAction(ACTION_NEXT, getIcon("control_play_blue.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.next();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Execute the next step.");
            actionMap.put(ACTION_NEXT, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getClearBookmarksAction() {
        Action ret = actionMap.get(ACTION_REMOVE_BOOKMARKS);
        if (ret == null) {
            ret = new AbstractAction(ACTION_REMOVE_BOOKMARKS, getIcon("bookmark-remove.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.clearBookmarks();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_REMOVE_BOOKMARKS);
            actionMap.put(ACTION_REMOVE_BOOKMARKS, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getClearSkipsAction() {
        Action ret = actionMap.get(ACTION_REMOVE_SKIP);
        if (ret == null) {
            ret = new AbstractAction(ACTION_REMOVE_SKIP, getIcon("remove-all-skips.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.clearSkips();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_REMOVE_SKIP);
            actionMap.put(ACTION_REMOVE_SKIP, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getEndDebugAction() {
        Action ret = actionMap.get(ACTION_STOP);
        if (ret == null) {
            ret = new AbstractAction(ACTION_STOP, getIcon("control_stop_blue.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.stop();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, "Stop Debugging.");
            actionMap.put(ACTION_STOP, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getRunToAction() {
        Action ret = actionMap.get(ACTION_RUN_TO);
        if (ret == null) {
            ret = new AbstractAction(ACTION_RUN_TO, getIcon("control_fastforward_blue.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.runToBreakpoint();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_RUN_TO);
            actionMap.put(ACTION_RUN_TO, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getPauseAction() {
        Action ret = actionMap.get(ACTION_PAUSE);
        if (ret == null) {
            ret = new AbstractAction(ACTION_PAUSE, getIcon("control_pause_blue.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.pause();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_PAUSE);
            actionMap.put(ACTION_PAUSE, ret);
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public Action getCSVAction() {
        Action ret = actionMap.get(ACTION_EXPORT);
        if (ret == null) {
            jFileChooser = new JFileChooser();
            ret = new AbstractAction(ACTION_EXPORT, getIcon("database_tabs.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    int option = jFileChooser.showSaveDialog(debuggerFrame);
                    if (option != JFileChooser.CANCEL_OPTION) {
                        File selectedFile = jFileChooser.getSelectedFile();
                        debuggerFrame.exportCSV(selectedFile);
                    }
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_EXPORT);
            actionMap.put(ACTION_EXPORT, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getSkipAction() {
        Action ret = actionMap.get(ACTION_SKIP);
        if (ret == null) {
            ret = new AbstractAction(ACTION_SKIP, getIcon("control_repeat_blue.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.skip();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_SKIP);
            actionMap.put(ACTION_SKIP, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getSkipStepAction() {
        Action ret = actionMap.get(ACTION_SKIP_STEP);
        if (ret == null) {
            ret = new AbstractAction(ACTION_SKIP_STEP, getIcon("skip.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.toggleSkip();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_SKIP_STEP);
            actionMap.put(ACTION_SKIP_STEP, ret);
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public Action getToggleBreakpointAction() {
        Action ret = actionMap.get(ACTION_TOGGLE_BREAKPOINT);
        if (ret == null) {
            ret = new AbstractAction(ACTION_TOGGLE_BREAKPOINT, getIcon("bullet_blue.png", IconSize.SMALL)) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    debuggerFrame.toggleBreakPoint();
                }
            };
            ret.putValue(Action.SHORT_DESCRIPTION, ACTION_TOGGLE_BREAKPOINT);
            actionMap.put(ACTION_TOGGLE_BREAKPOINT, ret);
        }
        return ret;
    }

    public static Icon getIcon(String string, IconSize size) {
        String resourcePath = (size == IconSize.SMALL ? "gfx/16/" : "gfx/32/") + string;
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL url = cl.getResource(resourcePath);
            // System.out.println("URL is: " + url);
            return new ImageIcon(ImageIO.read(url));
        } catch (Exception e) {
            // System.out.println("URL is: " + url);
            LOG.error("Error loading image " + resourcePath + ": " + e);
        }
        return null;
    }

    private void showError(final String msg) {
        LOG.error(msg);
        SwingUtilities.invokeLater( () -> JOptionPane.showMessageDialog(debuggerFrame, msg, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void setFromString(String scriptXml) {
        if (StringUtils.isBlank(scriptXml)) {
            debuggerFrame.setCurrentWorkload(null);
            debuggerFrame.setCurrentTitle("");
        } else {
            HDWorkload workload = unmarshalWorkload(scriptXml);
            if (workload != null) {
                debuggerFrame.setCurrentWorkload(workload);
                debuggerFrame.setCurrentTitle("Workload: " + workload.getName());
            }
        }
    }

    private HDWorkload unmarshalWorkload(String xml) {
        try {
            return JaxbUtil.unmarshall(xml, HDWorkload.class);
        } catch (JAXBException | ParserConfigurationException | SAXException e) {
            JOptionPane.showMessageDialog(debuggerFrame, e.getMessage(), "Error unmarshalling xml",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public enum IconSize {
        SMALL,
        LARGE
    }

}
