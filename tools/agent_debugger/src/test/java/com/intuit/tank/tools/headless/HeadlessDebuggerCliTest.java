package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;

public class HeadlessDebuggerCliTest {

    private final StringWriter out = new StringWriter();
    private final StringWriter err = new StringWriter();

    private int run(String... args) {
        CommandLine cmd = HeadlessDebuggerCli.newCommandLine();
        cmd.setOut(new PrintWriter(out));
        cmd.setErr(new PrintWriter(err));
        return cmd.execute(args);
    }

    @Test
    public void helpListsOptionsAndExitCodes() {
        assertEquals(0, run("--help"));
        String usage = out.toString();
        assertTrue(usage.contains("java -jar Tank-Debugger-all.jar --headless"));
        assertTrue(usage.contains("-h, --host"));
        assertTrue(usage.contains("-p, --project"));
        assertTrue(usage.contains("-t, --token"));
        assertTrue(usage.contains("Exit codes:"));
        assertTrue(usage.contains("The script failed the hostname replacement check"));
    }

    @Test
    public void versionPrintsBuildVersionAndGitCommit() {
        assertEquals(0, run("--version"));
        assertTrue(out.toString().contains("Tank Debugger"));
        assertTrue(out.toString().contains("git commit"));
    }

    @Test
    public void missingArgumentsExitWithInvalidArguments() {
        assertEquals(HeadlessDebuggerCli.EXIT_INVALID_ARGUMENTS, run("-h=http://localhost:8080/tank"));
        assertTrue(err.toString().contains("Missing required options"));
    }

    @Test
    public void unreachableTankExitsWithTankApiError() {
        assertEquals(HeadlessDebuggerCli.EXIT_TANK_API_ERROR, run("-h=http://localhost:1/tank", "-p=1", "-t=tok"));
    }

    @Test
    public void nonNumericProjectExitsWithInvalidArguments() {
        assertEquals(HeadlessDebuggerCli.EXIT_INVALID_ARGUMENTS, run("-h=http://x", "-p=abc", "-t=tok"));
    }
}
