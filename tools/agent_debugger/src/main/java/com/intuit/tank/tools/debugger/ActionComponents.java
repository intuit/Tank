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

import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import com.intuit.tank.harness.data.HDTestPlan;

public class ActionComponents implements ScriptChangedListener {

    private JToolBar toolBar;
    private JMenuBar menuBar;
    private JPopupMenu popupMenu;
    private ActionProducer actions;
    private JCheckBox runTimingStepsCB;
    private JLabel titleLabel;

    /**
     * 
     * @param testPlanChooser
     * @param tankClientChooser 
     * @param actions
     */
    public ActionComponents(boolean standalone, JComboBox testPlanChooser, JComboBox<TankClientChoice> tankClientChooser, ActionProducer actions) {
        super();
        this.actions = actions;
        runTimingStepsCB = new JCheckBox("Run Timing Steps", false);
        createMenuBar(actions, standalone);
        createToolBar(testPlanChooser, tankClientChooser, actions, standalone);
        createPopupMenu();

    }

    /**
     * @return the popupMenu
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.add(actions.getStartAction());
        popupMenu.add(actions.getEndDebugAction());
        popupMenu.add(actions.getSkipAction());
        popupMenu.add(actions.getNextStepAction());
        popupMenu.add(actions.getRunToAction());
        popupMenu.add(actions.getPauseAction());

        popupMenu.addSeparator();
        popupMenu.add(new JMenuItem(actions.getSkipStepAction()));
        popupMenu.add(new JMenuItem(actions.getToggleBreakpointAction()));
        popupMenu.addSeparator();
        popupMenu.add(actions.getClearBookmarksAction());
        popupMenu.add(actions.getClearSkipsAction());
        popupMenu.addSeparator();
        popupMenu.add(actions.getFindAction());
    }

    /**
     * @return the toolBar
     */
    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * @return the menuBar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public void start() {
        actions.getStartAction().setEnabled(false);
        actions.getEndDebugAction().setEnabled(true);
        setRunningActions(false);
    }

    public void stop() {
        actions.getStartAction().setEnabled(true);
        actions.getEndDebugAction().setEnabled(false);
        actions.getPauseAction().setEnabled(false);
        setRunningActions(false);
    }

    public void skipTo() {
        actions.getPauseAction().setEnabled(true);
    }

    public void doneSkipping() {
        actions.getPauseAction().setEnabled(false);
    }

    public void stepping() {
        setRunningActions(false);
    }

    public void setRunningActions(boolean b) {
        actions.getNextStepAction().setEnabled(b);
        actions.getRunToAction().setEnabled(b);
        actions.getSkipAction().setEnabled(b);

    }

    public void doneStepping() {
        if (!actions.getStartAction().isEnabled()) {
            setRunningActions(true);
        }
    }

    @Override
    public void scriptChanged(HDTestPlan plan) {
        if (plan != null) {
            actions.getStartAction().setEnabled(true);
            actions.getEndDebugAction().setEnabled(false);
            actions.getFindAction().setEnabled(true);
        } else {
            actions.getStartAction().setEnabled(false);
            actions.getEndDebugAction().setEnabled(false);
            actions.getFindAction().setEnabled(false);
        }
        setRunningActions(false);
    }

    /**
     * 
     * @param tankClientChooser 
     * @param actions
     * @return
     */
    private void createToolBar(JComboBox testPlanChooser, JComboBox<TankClientChoice> tankClientChooser, ActionProducer actions, boolean standalone) {
        if (toolBar == null) {
            toolBar = new JToolBar("Toolbar");
            toolBar.setMargin(new Insets(5, 5, 5, 5));
            if (standalone) {
                toolBar.add(actions.getOpenAction());
                toolBar.add(new JLabel(" "));
            }
            toolBar.add(actions.getOpenScriptAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getOpenProjectAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getReloadAction());

            toolBar.addSeparator();
            toolBar.add(actions.getSelectDataFileAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getShowVariablesAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getFindAction());

            toolBar.addSeparator();

            toolBar.add(actions.getStartAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getEndDebugAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getSkipAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getNextStepAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getRunToAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getPauseAction());
            toolBar.addSeparator();

            toolBar.add(actions.getClearBookmarksAction());
            toolBar.add(new JLabel(" "));
            toolBar.add(actions.getClearSkipsAction());

            actions.getStartAction().setEnabled(false);
            actions.getEndDebugAction().setEnabled(false);
            actions.getSkipAction().setEnabled(false);
            actions.getNextStepAction().setEnabled(false);
            actions.getRunToAction().setEnabled(false);
            actions.getPauseAction().setEnabled(false);

            toolBar.addSeparator();
            titleLabel = new JLabel("");
            toolBar.add(titleLabel);

            JPanel clientWrapper = new JPanel(new FlowLayout(FlowLayout.TRAILING));
            clientWrapper.add(new JLabel("Http Client: "));
            clientWrapper.add(tankClientChooser);
            toolBar.add(clientWrapper);
            
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.TRAILING));
            wrapper.add(new JLabel("Test Plan: "));
            wrapper.add(testPlanChooser);
            toolBar.add(wrapper);
        }
    }

    /**
     * @return the runTimingStepsCB
     */
    public JCheckBox getRunTimingStepsCB() {
        return runTimingStepsCB;
    }

    /**
     * 
     * @param testPlanChooser
     * @param actions
     * @param standalone
     * @return
     */
    private void createMenuBar(ActionProducer actions, boolean standalone) {
        if (menuBar == null) {
            menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            if (standalone) {
                fileMenu.add(actions.getOpenAction());
            }
            fileMenu.add(actions.getOpenScriptAction());
            fileMenu.add(actions.getOpenProjectAction());
            fileMenu.add(actions.getReloadAction());

            fileMenu.addSeparator();
            fileMenu.add(actions.getFindAction());
            fileMenu.addSeparator();

            if (standalone) {
                fileMenu.add(actions.getSelectTankAction());
                fileMenu.addSeparator();
            }

            fileMenu.add(actions.getQuitAction());
            menuBar.add(fileMenu);

            JMenu runMenu = new JMenu("Run");
            runMenu.add(actions.getStartAction());
            runMenu.add(actions.getEndDebugAction());
            runMenu.add(actions.getSkipAction());
            runMenu.add(actions.getNextStepAction());
            runMenu.add(actions.getRunToAction());
            runMenu.addSeparator();
            runMenu.add(actions.getClearBookmarksAction());
            runMenu.add(actions.getClearSkipsAction());

            menuBar.add(runMenu);

            JMenu actionsMenu = new JMenu("Actions");
            actionsMenu.add(runTimingStepsCB);
            actionsMenu.addSeparator();
            actionsMenu.add(actions.getSaveReqResponseAction());
            actionsMenu.add(actions.getSaveLogAction());
            actionsMenu.addSeparator();
            actionsMenu.add(actions.getClearLogOutputAction());
            menuBar.add(actionsMenu);
        }
    }

    public void setCurrentTitle(String string) {
        this.titleLabel.setText(string);

    }

}
