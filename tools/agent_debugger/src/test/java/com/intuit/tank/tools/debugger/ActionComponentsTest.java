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

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class ActionComponentsTest {

    private static ActionComponents actionComponents;
    private static ActionProducer actions;

    @BeforeAll
    public static void setup() {
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
    public void testShowMessageStreamsAction() {
        assertNotNull(actions.getShowMessageStreamsAction());
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
}