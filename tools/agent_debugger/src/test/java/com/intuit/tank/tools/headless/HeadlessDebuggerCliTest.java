package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.intuit.tank.harness.data.TestStep;
import com.sun.net.httpserver.HttpServer;

import picocli.CommandLine;

public class HeadlessDebuggerCliTest {

    private static final String HEADERS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<headers xmlns=\"urn:wats/domain/agent/v1\"><headers>"
            + "<header><key>intuit_test</key><value>TtoPerf_QaController</value></header>"
            + "</headers></headers>";

    private final StringWriter out = new StringWriter();
    private final StringWriter err = new StringWriter();
    private final Map<String, String> responses = new ConcurrentHashMap<>();

    @TempDir
    Path reportDir;

    @BeforeAll
    public static void loadTankConfig() throws IOException {
        StableTankConfig.load();
    }

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

    @Test
    public void unfilteredScriptExitsWithHostnameCheckFailed() throws Exception {
        List<TestStep> steps = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            steps.add(HeadlessDebuggerSetupTest.request("https://prod.example.com/" + i));
        }
        HttpServer tank = tank(Map.of());
        try {
            serve(HeadlessDebuggerSetupTest.script("p", null, steps));

            assertEquals(HeadlessDebuggerCli.EXIT_HOSTNAME_CHECK_FAILED,
                    HeadlessDebuggerCli.run(new String[] { "-h=" + url(tank), "-p=1", "-t=tok" }));
        } finally {
            tank.stop(0);
        }
    }

    @Test
    public void completedRunExitsOkAndWritesTheReport() throws Exception {
        HttpServer tank = tank(Map.of("/app/start", "started"));
        String userDir = System.getProperty("user.dir");
        // the run writes validation-report.json to the working directory
        System.setProperty("user.dir", reportDir.toString());
        try {
            int port = tank.getAddress().getPort();
            serve(HeadlessDebuggerSetupTest.script("p", null,
                    List.of(HeadlessDebuggerSetupTest.target(port, "#{host}/start", "/app/start"))));

            assertEquals(HeadlessDebuggerCli.EXIT_OK,
                    HeadlessDebuggerCli.run(new String[] { "-h=" + url(tank), "-p=1", "-t=tok" }));

            String report = Files.readString(reportDir.resolve("validation-report.json"));
            assertTrue(report.contains("\"successCount\" : 1"));
        } finally {
            System.setProperty("user.dir", userDir);
            tank.stop(0);
        }
    }

    /** A loopback Tank controller (and request target) answering the given paths, and 404 for anything else */
    private HttpServer tank(Map<String, String> extra) throws IOException {
        responses.put("/v2/agent/headers", HEADERS_XML);
        responses.putAll(extra);
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            byte[] body = responses.getOrDefault(exchange.getRequestURI().getPath(), "").getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(body.length == 0 ? 404 : 200, body.length == 0 ? -1 : body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        return server;
    }

    private void serve(String scriptXml) {
        responses.put("/v2/projects/1", "{\"id\":1,\"name\":\"p\",\"variables\":[]}");
        responses.put("/v2/projects/download/1", scriptXml);
    }

    private static String url(HttpServer server) {
        return "http://localhost:" + server.getAddress().getPort();
    }
}
