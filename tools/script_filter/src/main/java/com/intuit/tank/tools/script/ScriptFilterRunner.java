/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.script;

/*
 * #%L
 * script-filter
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.intuit.tank.api.model.v1.script.ExternalScriptTO;
import com.intuit.tank.api.model.v1.script.ScriptTO;
import com.intuit.tank.client.v1.script.ScriptServiceClient;
import com.intuit.tank.tools.script.ScriptRunner;

/**
 * ScrioptFilterRunner
 * 
 * @author dangleton
 * 
 */
public class ScriptFilterRunner extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String INITIAL_OUTPUT_CONTENT = "Output:\n";

    private ScriptRunner runner = new ScriptRunner();

    private JComboBox languageSelector;
    private JLabel currentFileLabel;

    private RSyntaxTextArea scriptEditorTA;
    private XMlViewDialog xmlViewDialog;
    private ScriptTO tankScript;
    private TextAreaOutputLogger output;
    private File xmlFile;
    private JButton runBT;
    private JButton showXmlBT;
    private JButton saveBT;
    private JLabel localLB;
    private ScriptServiceClient scriptServiceClient;
    private ExternalScriptTO currentExternalScript;
    private JFileChooser loadChooser;
    private boolean local = true;

    /**
     * @throws HeadlessException
     */
    public ScriptFilterRunner(final boolean terminate, String serviceUrl) throws HeadlessException {
        super("Intuit Tank Script Filter Editor");
        setSize(new Dimension(1024, 800));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                if (terminate) {
                    System.exit(0);
                }
            }
        });
        scriptServiceClient = new ScriptServiceClient(serviceUrl);

        Component topPanel = createTopPanel();
        Component bottomPanel = createBottomPanel();
        Component contentPanel = createContentPanel();

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        WindowUtil.centerOnScreen(this);
        pack();
    }

    /**
     * @return
     */
    private Component createContentPanel() {
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        scriptEditorTA = new RSyntaxTextArea();
        setSyntaxStyle();
        RTextScrollPane sp = new RTextScrollPane(scriptEditorTA);
        pane.setTopComponent(sp);

        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel panel = new JPanel(new BorderLayout());
        output = new TextAreaOutputLogger(scrollPane, INITIAL_OUTPUT_CONTENT);
        output.setEditable(false);
        output.setAutoscrolls(true);
        output.setScrollContent(true);
        output.setWrapStyleWord(true);
        scrollPane.setViewportView(output);

        JToolBar toolBar = new JToolBar();
        JButton clearBT = new JButton("Clear");
        clearBT.addActionListener((ActionEvent e) -> {
            output.setText(INITIAL_OUTPUT_CONTENT);
        });

        final JCheckBox scrollCB = new JCheckBox("Auto Scroll Content");
        scrollCB.setSelected(true);
        scrollCB.addChangeListener( (ChangeEvent e) -> {
            output.setScrollContent(scrollCB.isSelected());
        });
        toolBar.add(clearBT);
        toolBar.add(scrollCB);

        panel.add(toolBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        pane.setBottomComponent(panel);
        pane.setDividerLocation(300);
        return pane;
    }

    /**
     * @return
     */
    private Component createTopPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        List<ConfiguredLanguage> configuredLanguages = ConfiguredLanguage.getConfiguredLanguages();
        languageSelector = new JComboBox(configuredLanguages.toArray());
        languageSelector.setSelectedIndex(0);
        languageSelector.addItemListener((ItemEvent e) -> {
            setSyntaxStyle();
        });

        currentFileLabel = new JLabel();

        final JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new FileFilter() {

            @Override
            public String getDescription() {
                return "Tank XML Files";
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith("_ts.xml");
            }
        });

        JButton button = new JButton("Select File...");
        button.addActionListener( (ActionEvent e) -> {
            int showOpenDialog = jFileChooser.showOpenDialog(ScriptFilterRunner.this);
            if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
                loadTSXml(jFileChooser.getSelectedFile());
            }
        });
        xmlViewDialog = new XMlViewDialog(this);
        xmlViewDialog.setSize(new Dimension(800, 500));
        showXmlBT = new JButton("Show XML");
        showXmlBT.setEnabled(false);
        showXmlBT.addActionListener( (ActionEvent e) -> {
            displayXml();
        });

        saveBT = new JButton("Save XML");
        saveBT.setEnabled(false);
        saveBT.addActionListener( (ActionEvent e) -> {
            saveXml();
        });

        JPanel xmlPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 20));
        xmlPanel.add(button);
        xmlPanel.add(showXmlBT);
        xmlPanel.add(saveBT);

        int y = 0;

        topPanel.add(new JLabel("Current Tank XML: "), getConstraints(0, y, GridBagConstraints.NONE));
        topPanel.add(currentFileLabel, getConstraints(1, y++, GridBagConstraints.HORIZONTAL));

        topPanel.add(new JLabel("Select Script Language: "), getConstraints(0, y, GridBagConstraints.NONE));
        topPanel.add(languageSelector, getConstraints(1, y++, GridBagConstraints.HORIZONTAL));

        topPanel.add(new JLabel("Select Tank XML: "), getConstraints(0, y, GridBagConstraints.NONE));
        topPanel.add(xmlPanel, getConstraints(1, y++, GridBagConstraints.HORIZONTAL));
        return topPanel;
    }

    /**
     * 
     */
    protected void toggleStorageMode() {
        local = !local;
        setLocalLable();
    }

    private void setLocalLable() {
        localLB.setText(local ? "Current Storage Mode: Local" : "Current Storage Mode: Remote");
    }

    /**
     * @return
     */
    public Component createBottomPanel() {
        loadChooser = new JFileChooser();
        loadChooser.setFileFilter(new FileFilter() {

            @Override
            public String getDescription() {
                return "Script Files";
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory()
                        || ConfiguredLanguage.getConfiguredExtensions().contains(
                                FilenameUtils.getExtension(f.getName()));
            }
        });
        runBT = new JButton("Run Script");
        runBT.setEnabled(false);
        runBT.addActionListener( (ActionEvent arg0) -> {
            runScript();
        });
        JButton chooseBT = new JButton("Open Script...");
        chooseBT.addActionListener( (ActionEvent arg0) -> {
            loadScript();
        });

        JButton saveScriptBT = new JButton("Save Script...");
        saveScriptBT.addActionListener( (ActionEvent arg0) -> {
            saveScript(false);
        });

        JButton saveAsScriptBT = new JButton("Save Script As...");
        saveAsScriptBT.addActionListener( (ActionEvent arg0) -> {
            saveScript(true);
        });

        JButton localBT = new JButton("Toggle Storage Mode");
        localBT.addActionListener( (ActionEvent e) -> {
            toggleStorageMode();
        });
        localLB = new JLabel();
        setLocalLable();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(runBT);
        bottomPanel.add(chooseBT);
        bottomPanel.add(saveScriptBT);
        bottomPanel.add(saveAsScriptBT);
        bottomPanel.add(localLB);
        bottomPanel.add(localBT);
        return bottomPanel;
    }

    /**
     * 
     */
    protected void saveScript(boolean saveAs) {
        if (local) {
            int showOpenDialog = loadChooser.showSaveDialog(ScriptFilterRunner.this);
            if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
                saveScript(loadChooser.getSelectedFile());
            }

        } else {
            storeScript(saveAs);
        }

    }

    /**
     * 
     */
    protected void loadScript() {
        if (local) {

            // open script from file system
            int showOpenDialog = loadChooser.showOpenDialog(ScriptFilterRunner.this);
            if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
                File file = loadChooser.getSelectedFile();
                ConfiguredLanguage languagebyExtension = ConfiguredLanguage.getLanguagebyExtension(file
                        .getName());
                if (languagebyExtension != null) {
                    try {
                        scriptEditorTA.setText(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                        currentExternalScript = null;
                        languageSelector.setSelectedItem(languagebyExtension);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(ScriptFilterRunner.this, e.getMessage(),
                                "Error loading script", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(ScriptFilterRunner.this,
                            "Script language extension not configured. valid extensions are: "
                                    + ConfiguredLanguage.getConfiguredExtensions(), "Error loading script",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } else {
            selectScript(new SelectDialog<ExternalScriptTO>(ScriptFilterRunner.this, scriptServiceClient
                    .getExternalScripts()));

        }

    }

    /**
     * 
     */
    protected void storeScript(boolean saveAs) {
        ConfiguredLanguage language = (ConfiguredLanguage) languageSelector.getSelectedItem();
        if (saveAs || currentExternalScript == null) {
            String retVal = JOptionPane.showInputDialog("Script Name: ", "<Script Name>");
            if (retVal != null) {
                retVal = retVal + "." + language.getDefaultExtension();
                currentExternalScript = new ExternalScriptTO();
                currentExternalScript.setName(retVal);
                currentExternalScript.setCreator("");
            } else {
                return;
            }
        } else {
            currentExternalScript.setName(FilenameUtils.getBaseName(currentExternalScript.getName()) + "."
                    + language.getDefaultExtension());
        }
        currentExternalScript.setScript(scriptEditorTA.getText());
        currentExternalScript = scriptServiceClient.saveOrUpdateExternalScript(currentExternalScript);
    }

    /**
     * 
     */
    protected void selectScript(SelectDialog<ExternalScriptTO> scriptSelectDialog) {
        scriptSelectDialog.setVisible(true);
        if (scriptSelectDialog.getSelectedObject() != null) {
            ExternalScriptTO selectedScript = scriptSelectDialog.getSelectedObject();
            ConfiguredLanguage languagebyExtension = ConfiguredLanguage.getLanguagebyExtension(selectedScript
                    .getName());
            if (languagebyExtension != null) {
                languageSelector.setSelectedItem(languagebyExtension);
                scriptEditorTA.setText(selectedScript.getScript());
                currentExternalScript = selectedScript;
            } else {
                JOptionPane.showMessageDialog(this, "Script language extension not configured. valid extensions are: "
                        + ConfiguredLanguage.getConfiguredExtensions(), "Error loading script",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @param selectedFile
     */
    protected void saveScript(File selectedFile) {
        try {
            FileUtils.writeStringToFile(selectedFile, this.scriptEditorTA.getText());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error writing script", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * 
     */
    protected void saveXml() {
        Writer fw = null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
            // fw = new FileWriter(xmlFile);
            JAXBContext ctx = JAXBContext.newInstance(ScriptTO.class.getPackage().getName());
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(tankScript, fw);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error writing file", JOptionPane.ERROR_MESSAGE);
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * 
     */
    protected void setSyntaxStyle() {
        ConfiguredLanguage item = (ConfiguredLanguage) languageSelector.getSelectedItem();
        scriptEditorTA.setSyntaxEditingStyle(item.getSyntaxStyle());
    }

    /**
     * 
     */
    protected void displayXml() {
        if (tankScript != null) {
            xmlViewDialog.setTitle(tankScript.getName());
            String xml = toXml(tankScript);
            xmlViewDialog.setText(xml, !xmlViewDialog.isVisible());
            if (!xmlViewDialog.isVisible()) {
                WindowUtil.centerOnParent(xmlViewDialog);
                xmlViewDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "You must select a Tank XML file.", "Data Needed",
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

    /**
     * @param x
     * @param y
     * @return
     */
    private GridBagConstraints getConstraints(int x, int y, int fill) {
        GridBagConstraints ret = new GridBagConstraints(x, y, 1, 1, x, .5D, GridBagConstraints.WEST, fill, new Insets(
                5, 5, 5, 5), 5, 5);
        return ret;
    }

    private void loadTSXml(File selectedFile) {
        try {
        	//Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
        	SAXParserFactory spf = SAXParserFactory.newInstance();
        	spf.setNamespaceAware(true);
        	spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        	spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        	
        	Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new FileInputStream(selectedFile)));
        	
            JAXBContext ctx = JAXBContext.newInstance(ScriptTO.class.getPackage().getName());
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            tankScript = (ScriptTO) unmarshaller.unmarshal(xmlSource);
            currentFileLabel.setText(selectedFile.getName());
            xmlFile = selectedFile;
            showXmlBT.setEnabled(true);
            runBT.setEnabled(true);
            saveBT.setEnabled(true);
        } catch (JAXBException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException fnfe) {
        	JOptionPane.showMessageDialog(this, fnfe.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        } catch (SAXException saxe) {
        	JOptionPane.showMessageDialog(this, saxe.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        } catch (ParserConfigurationException pce) {
        	JOptionPane.showMessageDialog(this, pce.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String toXml(ScriptTO turboScriptTO) {
        String ret = null;
        try {
            JAXBContext ctx = JAXBContext.newInstance(ScriptTO.class.getPackage().getName());
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(turboScriptTO, sw);
            ret = sw.toString();
        } catch (JAXBException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        }
        return ret;
    }

    private void runScript() {
        String text = scriptEditorTA.getText();
        if (tankScript == null) {
            JOptionPane.showMessageDialog(this, "You must select a Tank XML file.", "Data Needed",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (StringUtils.isEmpty(text)) {
            JOptionPane.showMessageDialog(this, "You need to enter your script.", "Data Needed",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                Map<String, Object> inputs = new HashMap<String, Object>();
                inputs.put("script", tankScript);
                runner.runScript(text,
                        ((ConfiguredLanguage) languageSelector.getSelectedItem()).getEngine(), inputs, output);

            } catch (ScriptException e) {
                output.append("Error executing script:\n");
                output.append(e.getMessage() + "\n");
                JOptionPane
                        .showMessageDialog(this, e.getMessage(), "Error executing Script", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String url = "http://localhost:8080/";
        if (args.length > 0) {
            url = args[0];
        }
        new ScriptFilterRunner(true, url).setVisible(true);
    }

}
