package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class DebuggerLauncherTest {

    @Test
    public void headlessFlagIsRemovedFromTheCliArguments() {
        assertArrayEquals(new String[] { "-h=http://tank", "-p=1", "-t=tok" },
                DebuggerLauncher.headlessArgs(new String[] { "--headless", "-h=http://tank", "-p=1", "-t=tok" }));
    }

    @Test
    public void headlessFlagCanComeAfterTheOptions() {
        assertArrayEquals(new String[] { "-h=http://tank", "-p=1" },
                DebuggerLauncher.headlessArgs(new String[] { "-h=http://tank", "-p=1", "--headless" }));
    }

    @Test
    public void withoutTheFlagTheVisualDebuggerStarts() {
        assertNull(DebuggerLauncher.headlessArgs(new String[] { "http://tank", "tok" }));
        assertNull(DebuggerLauncher.headlessArgs(new String[0]));
    }

    @Test
    public void headlessLogConfigIsOnTheClasspath() {
        assertNotNull(DebuggerLauncher.class.getResource("/" + DebuggerLauncher.HEADLESS_LOG_CONFIG));
    }
}
