package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.tools.debugger.JaxbUtil;
import com.sun.net.httpserver.HttpServer;

public class HeadlessDebuggerSetupTest {

    private static final String HEADERS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<headers xmlns=\"urn:wats/domain/agent/v1\"><headers>"
            + "<header><key>intuit_test</key><value>TtoPerf_QaController</value></header>"
            + "</headers></headers>";

    private final List<String> requestedPaths = new CopyOnWriteArrayList<>();
    private final List<String> authorizations = new CopyOnWriteArrayList<>();
    private final Map<String, String> responses = new ConcurrentHashMap<>(Map.of("/v2/agent/headers", HEADERS_XML));
    private HttpServer server;

    @BeforeEach
    public void startTank() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            requestedPaths.add(exchange.getRequestURI().getPath());
            authorizations.add(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] body = responses.getOrDefault(exchange.getRequestURI().getPath(), "").getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(body.length == 0 ? 404 : 200, body.length == 0 ? -1 : body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    public void stopTank() {
        server.stop(0);
    }

    @Test
    public void agentHeadersFromTankAreWrittenToSettings() throws IOException {
        new HeadlessDebuggerSetup("http://localhost:" + server.getAddress().getPort(), 1, "tok");

        File settings = new File(System.getProperty("WATS_PROPERTIES"), "settings.xml");
        String xml = Files.readString(settings.toPath());
        assertTrue(xml.contains("<header key=\"intuit_test\">TtoPerf_QaController</header>"));
        assertEquals("bearer tok", authorizations.get(0));
    }

    @Test
    public void trailingSlashOnHostDoesNotDoubleTheApiPath() {
        new HeadlessDebuggerSetup("http://localhost:" + server.getAddress().getPort() + "/", 1, "tok");

        assertTrue(requestedPaths.contains("/v2/agent/headers"));
        assertTrue(requestedPaths.contains("/v2/projects/1"));
        assertFalse(requestedPaths.stream().anyMatch(path -> path.startsWith("//")));
    }

    @Test
    public void filteredScriptIsReady() throws Exception {
        serveProject(scriptWithRequests(1, "#{host}/start"));

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertTrue(setup.isReady());
    }

    @Test
    public void requestsWithoutLabelsFailTheHostnameCheck() throws Exception {
        serveProject(scriptWithRequests(200, null));

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertTrue(setup.isHostnameCheckFailed());
        assertFalse(setup.isReady());
    }

    private String tankUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    private void serveProject(String scriptXml) {
        responses.put("/v2/projects/1", "{\"id\":1,\"name\":\"p\",\"variables\":[]}");
        responses.put("/v2/projects/download/1", scriptXml);
    }

    private static String scriptWithRequests(int count, String label) throws Exception {
        HDScriptUseCase useCase = new HDScriptUseCase();
        for (int i = 0; i < count; i++) {
            HDRequest request = new HDRequest();
            request.setLabel(label);
            request.setMethod("GET");
            RequestStep step = new RequestStep();
            step.setRequest(request);
            useCase.getScriptSteps().add(step);
        }
        HDScript script = new HDScript();
        script.getUseCase().add(useCase);
        HDScriptGroup group = new HDScriptGroup();
        group.getGroupSteps().add(script);
        HDTestPlan plan = new HDTestPlan();
        plan.getGroup().add(group);
        HDWorkload workload = new HDWorkload();
        workload.setName("p");
        workload.getPlans().add(plan);
        return JaxbUtil.marshall(workload);
    }
}
