package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.tools.debugger.DebugStep;

public class VariablesOutputTest {

    private HeadlessDebuggerSetup setup;
    private VariablesOutput output;
    private LogCapture log;

    @BeforeEach
    public void createOutput() {
        setup = mock(HeadlessDebuggerSetup.class);
        output = new VariablesOutput(setup);
        log = new LogCapture(VariablesOutput.class);
    }

    @AfterEach
    public void stopCapture() {
        log.close();
    }

    @Test
    public void registersForScriptAndStepChanges() {
        verify(setup).addScriptChangedListener(output);
        verify(setup).addStepChangedListener(output);
    }

    @Test
    public void displaysEntryAndExitVariablesSortedByName() {
        DebugStep step = new DebugStep(null);
        step.setEntryVariables(Map.of("b", "2", "a", "1"));
        step.setExitVariables(Map.of("c", "3"));

        output.stepChanged(step);
        output.displayVars();

        assertEquals(List.of("******** VARIABLES *********",
                "Initial Variable Values", "a = 1\nb = 2\n",
                "Completed Variable Values", "c = 3\n"), log.messages());
    }

    @Test
    public void enteringAndExitingSeparateStepsTracksEachSide() {
        DebugStep entered = new DebugStep(null);
        entered.setEntryVariables(Map.of("in", "x"));
        DebugStep exited = new DebugStep(null);
        exited.setExitVariables(Map.of("out", "y"));

        output.stepEntered(entered);
        output.stepExited(exited);
        output.displayVars();

        assertEquals("in = x\n", log.messages().get(2));
        assertEquals("out = y\n", log.messages().get(4));
    }

    @Test
    public void noStepOrNewScriptClearsTheVariables() {
        DebugStep step = new DebugStep(null);
        step.setEntryVariables(Map.of("a", "1"));
        step.setExitVariables(Map.of("a", "2"));

        output.stepChanged(step);
        output.stepChanged(null);
        output.displayVars();
        assertEquals("", log.messages().get(2));
        assertEquals("", log.messages().get(4));

        output.stepChanged(step);
        output.scriptChanged(null);
        output.displayVars();
        assertEquals("", log.messages().get(7));
        assertEquals("", log.messages().get(9));
    }
}
