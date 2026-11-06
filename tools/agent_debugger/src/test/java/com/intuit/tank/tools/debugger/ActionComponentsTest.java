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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import com.intuit.tank.harness.data.HDTestPlan;

import javax.swing.*;

import java.awt.Component;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class ActionComponentsTest {

    private static ActionComponents actionComponents;
    private static ActionProducer actions;

    @BeforeAll
    public static void setup() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        actions = new ActionProducer(new AgentDebuggerFrame(true, null, null), null, null);
        actionComponents = new ActionComponents(
                true,            // standalone
                new JComboBox(),                        //testPlanChooser
                new JComboBox<TankClientChoice>(),      //tankClientChooser
                actions);                           //actions
    }

    @Test
    public void testPopupMenu() {
        JPopupMenu jPopupMenu = actionComponents.getPopupMenu();
        assertNotNull(jPopupMenu);
    }

    @Test
    public void testToolBar() {
        JToolBar jToolBar = actionComponents.getToolBar();
        assertNotNull(jToolBar);
    }

    @Test
    public void testMenuBar() {
        JMenuBar jMenuBar = actionComponents.getMenuBar();
        assertNotNull(jMenuBar);
    }

    @Test
    public void testRunSleepStepsCB() {
        JCheckBox jCheckBox = actionComponents.getRunSleepStepsCB();
        assertNotNull(jCheckBox);
    }

    @Test
    public void testRunThinkStepsCB() {
        JCheckBox jCheckBox = actionComponents.getRunThinkStepsCB();
        assertNotNull(jCheckBox);
    }

    @Test
    public void testStart() {
        actionComponents.start();
        assertFalse(actions.getStartAction().isEnabled());
        assertTrue(actions.getEndDebugAction().isEnabled());
        assertFalse(actions.getNextStepAction().isEnabled());
        assertFalse(actions.getRunToAction().isEnabled());
        assertFalse(actions.getSkipAction().isEnabled());
    }

    @Test
    public void testStop() {
        actionComponents.start();
        //assertTrue(actions.getStartAction().isEnabled());
        assertTrue(actions.getEndDebugAction().isEnabled());
        assertFalse(actions.getPauseAction().isEnabled());
        assertFalse(actions.getNextStepAction().isEnabled());
        assertFalse(actions.getRunToAction().isEnabled());
        assertFalse(actions.getSkipAction().isEnabled());
    }

    @Test
    public void testSkipTo() {
        actionComponents.skipTo();
        assertTrue(actions.getPauseAction().isEnabled());
    }

    @Test
    public void testDoneSkipping() {
        actionComponents.doneSkipping();
        assertFalse(actions.getPauseAction().isEnabled());
    }

    @Test
    public void testStepping() {
        actionComponents.stepping();
        assertFalse(actions.getNextStepAction().isEnabled());
        assertFalse(actions.getRunToAction().isEnabled());
        assertFalse(actions.getSkipAction().isEnabled());
    }

    @Test
    public void testStopEnablesStartOnly() {
        actionComponents.start();
        actionComponents.stop();
        assertTrue(actions.getStartAction().isEnabled());
        assertFalse(actions.getEndDebugAction().isEnabled());
        assertFalse(actions.getPauseAction().isEnabled());
        assertFalse(actions.getNextStepAction().isEnabled());
    }

    @Test
    public void testDoneSteppingWhileRunning() {
        actionComponents.start();
        actionComponents.doneStepping();
        assertTrue(actions.getNextStepAction().isEnabled());
        assertTrue(actions.getRunToAction().isEnabled());
        assertTrue(actions.getSkipAction().isEnabled());
    }

    @Test
    public void testDoneSteppingWhenNotRunning() {
        actionComponents.stop();
        actionComponents.doneStepping();
        assertFalse(actions.getNextStepAction().isEnabled());
        assertFalse(actions.getRunToAction().isEnabled());
        assertFalse(actions.getSkipAction().isEnabled());
    }

    @Test
    public void testScriptChanged() {
        actionComponents.scriptChanged(new HDTestPlan());
        assertTrue(actions.getStartAction().isEnabled());
        assertTrue(actions.getFindAction().isEnabled());
        assertFalse(actions.getEndDebugAction().isEnabled());

        actionComponents.scriptChanged(null);
        assertFalse(actions.getStartAction().isEnabled());
        assertFalse(actions.getFindAction().isEnabled());
        assertFalse(actions.getEndDebugAction().isEnabled());
        assertFalse(actions.getNextStepAction().isEnabled());
    }

    @Test
    public void testSetCurrentTitle() {
        actionComponents.setCurrentTitle("Workload: UC1");
        boolean found = false;
        for (Component c : actionComponents.getToolBar().getComponents()) {
            found |= c instanceof JLabel && "Workload: UC1".equals(((JLabel) c).getText());
        }
        assertTrue(found);
    }
}
