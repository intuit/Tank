package com.intuit.tank.tools.debugger;


import com.intuit.tank.harness.data.HDTestPlan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class AgentDebuggerFrameTest {

    private static AgentDebuggerFrame agentDebuggerFrame;

    @BeforeAll
    public static void setup() {
        agentDebuggerFrame = new AgentDebuggerFrame(true, null, null);
        assertNotNull(agentDebuggerFrame);
    }

    @Test
    public void testDebuggerActions() {
        assertNotNull(agentDebuggerFrame.getDebuggerActions());
    }

    @Test
    public void testTankClientChooser() {
        assertNotNull(agentDebuggerFrame.getTankClientChooser());
    }

    @Test
    public void testCurrentTestPlan() {
        HDTestPlan hdTestPlan = new HDTestPlan();
        agentDebuggerFrame.setCurrentTestPlan(hdTestPlan);
        assertEquals(hdTestPlan, agentDebuggerFrame.getCurrentTestPlan());
        assertEquals(-1, agentDebuggerFrame.getCurrentRunningStep());
        assertEquals(0, agentDebuggerFrame.getScriptEditorTA().getCaretPosition());
    }
}
