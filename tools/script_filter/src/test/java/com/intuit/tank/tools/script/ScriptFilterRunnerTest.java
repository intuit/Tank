package com.intuit.tank.tools.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class ScriptFilterRunnerTest {

    @Test
    public void testConstructorExit() {
        ScriptFilterRunner scriptFilterRunner = new ScriptFilterRunner(Boolean.TRUE, "http://localhost:8080/");
        assertNotNull(scriptFilterRunner);
    }

    @Test
    public void testConstructor() {
        ScriptFilterRunner scriptFilterRunner = new ScriptFilterRunner(Boolean.FALSE, "http://localhost:8080/");
        assertNotNull(scriptFilterRunner);
    }
}
