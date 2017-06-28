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
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.xml.sax.InputSource;

import com.intuit.tank.tools.debugger.ActionProducer.IconSize;
import com.intuit.tank.vm.agent.messages.Header;
import com.intuit.tank.vm.agent.messages.Headers;

public class PanelBuilder {
    private static Logger LOG = LogManager.getLogger(PanelBuilder.class);

    private static File workingDir;

    private static final String HEADERS_PATH = "/rest/v1/agent-service/headers";
    private static final char NEWLINE = '\n';

    public static File createWorkingDir(AgentDebuggerFrame frame, String baseUrl) {
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
            LOG.error("Error creating temp working dir: " + e);
            frame.showError("Error creating temp working dir: " + e);
        }
        return workingDir;
    }

    public static void updateServiceUrl(String serviceUrl) {
        try {
            Headers h = getHeaders(serviceUrl);
            writeSettings(workingDir, h);
        } catch (IOException e) {
            LOG.error("Error getting headers: " + e);
        }
    }

    private static Headers getHeaders(String serviceUrl) {
        Headers ret = null;
        if (StringUtils.isNotBlank(serviceUrl)) {
            InputStream settingsStream = null;
            try {
                URL url = new URL(serviceUrl + HEADERS_PATH);
                LOG.info("Starting up: making call to tank service url to get settings.xml "
                        + url.toExternalForm());
                settingsStream = url.openStream();
                
            	//Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
            	SAXParserFactory spf = SAXParserFactory.newInstance();
            	spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            	spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            	
            	Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(settingsStream));
            	
                JAXBContext ctx = JAXBContext.newInstance(Headers.class.getPackage().getName());
                ret = (Headers) ctx.createUnmarshaller().unmarshal(xmlSource);
            } catch (Exception e) {
                LOG.error("Error gettting headers: " + e, e);
            } finally {
                IOUtils.closeQuietly(settingsStream);
            }
        }
        return ret;
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
            for (Header h : headers.getHeaders()) {
                s.append("<header key=\"").append(h.getKey()).append("\">").append(h.getValue()).append("</header>")
                        .append(NEWLINE);
            }

            s.append("</request-headers>").append(NEWLINE);
        }
        s.append("</agent-config>");
        s.append("<logic-step>").append(NEWLINE);
        s.append("<insert-before><![CDATA[").append(NEWLINE);
        String js = IOUtils.toString(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("insert.js"));
        s.append(js).append(NEWLINE);
        s.append("]]>").append(NEWLINE);
        s.append("</insert-before>").append(NEWLINE);
        s.append("<append-after><![CDATA[").append(NEWLINE);
        s.append("]]>").append(NEWLINE);
        s.append("</append-after>").append(NEWLINE);
        s.append("</logic-step>").append(NEWLINE);

        s.append("</tank-settings>").append(NEWLINE);
        File settingsFile = new File(workingDir, "settings.xml");
        FileUtils.writeStringToFile(settingsFile, s.toString());
        System.out.println("Writing settings file to " + settingsFile.getAbsolutePath());
    }

    /**
     * @param debuggerActions
     * @return
     */
    static Component createContentPanel(final AgentDebuggerFrame frame) {
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        final RSyntaxTextArea scriptEditorTA = new RSyntaxTextArea();
        frame.setScriptEditorTA(scriptEditorTA);
        scriptEditorTA.setSelectionColor(scriptEditorTA.getCurrentLineHighlightColor());
        scriptEditorTA.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        scriptEditorTA.setHyperlinksEnabled(false);
        scriptEditorTA.setEditable(false);
        scriptEditorTA.setEnabled(false);
        scriptEditorTA.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                scriptEditorTA.grabFocus();
                try {
                    int offs = scriptEditorTA.viewToModel(e.getPoint());
                    if (offs > -1) {
                        int line = scriptEditorTA.getLineOfOffset(offs);
                        if (frame.getSteps().size() > line) {

                            frame.fireStepChanged(line);
                            if (e.getClickCount() == 2 && !e.isPopupTrigger()) {
                                // show step xml
                                try {
                                    DebugStep debugStep = frame.getSteps().get(line);
                                    String text = JaxbUtil.marshall(debugStep.getStepRun());
                                    StepDialog dlg = new StepDialog(frame, text,
                                            SyntaxConstants.SYNTAX_STYLE_XML);
                                    dlg.setVisible(true);
                                } catch (JAXBException e1) {
                                    frame.showError("Error showing step xml: " + e);
                                }
                            }
                        }
                    }
                } catch (BadLocationException ble) {
                    ble.printStackTrace(); // Never happens
                }
            }
        });
        RTextScrollPane scriptEditorScrollPane = new RTextScrollPane(scriptEditorTA);
        frame.setScriptEditorScrollPane(scriptEditorScrollPane);
        scriptEditorScrollPane.setIconRowHeaderEnabled(true);
        scriptEditorScrollPane.getGutter().setBookmarkIcon(ActionProducer.getIcon("bullet_blue.png", IconSize.SMALL));
        scriptEditorScrollPane.getGutter().setCurrentLineIcon(
                ActionProducer.getIcon("current_line.png", IconSize.SMALL));
        scriptEditorScrollPane.getGutter().setBookmarkingEnabled(true);
        pane.setLeftComponent(scriptEditorScrollPane);

        pane.setRightComponent(createRightPanel(frame));
        pane.setDividerLocation(300);
        pane.setResizeWeight(0.4D);
        return pane;
    }

    static Component createRightPanel(final AgentDebuggerFrame frame) {
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(BorderLayout.NORTH, new InfoHeaderPanel(frame));
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        pane.setTopComponent(new VariablesPanel(frame));
        pane.setBottomComponent(frame.getRequestResponsePanel());
        pane.setDividerLocation(250);
        pane.setResizeWeight(0.25D);
        ret.add(BorderLayout.CENTER, pane);
        return ret;
    }

    /**
     * @param debuggerActions
     * @return
     */
    static Component createTopPanel(ActionComponents actionComponents) {
        JPanel topPanel = new JPanel(new BorderLayout());
        JToolBar toolbaBar = actionComponents.getToolBar();
        topPanel.add(BorderLayout.NORTH, toolbaBar);
        return topPanel;
    }

    /**
     * @param debuggerActions
     * @param loadChooser
     * @return
     */
    static Component createBottomPanel(final AgentDebuggerFrame frame) {
        JPanel panel = new JPanel(new BorderLayout());

        RSyntaxTextArea loggerTA = new RSyntaxTextArea();
        frame.setLoggerTA(loggerTA);
        loggerTA.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        loggerTA.setHyperlinksEnabled(false);
        loggerTA.setEditable(false);
        loggerTA.setHighlightCurrentLine(false);
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(frame.getDebuggerActions().getSaveLogAction());
        popupMenu.add(frame.getDebuggerActions().getClearLogOutputAction());
        loggerTA.setPopupMenu(popupMenu);

        RTextScrollPane sp = new RTextScrollPane(loggerTA);
        sp.setIconRowHeaderEnabled(false);
        sp.getGutter().setBookmarkingEnabled(false);

        panel.add(sp, BorderLayout.CENTER);

        DebuggerAppender.addTextArea(loggerTA);

        return panel;
    }
}
