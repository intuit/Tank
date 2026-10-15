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
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

public class HeadlessDebuggerSetupTest {

    private static final String HEADERS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<headers xmlns=\"urn:wats/domain/agent/v1\"><headers>"
            + "<header><key>intuit_test</key><value>TtoPerf_QaController</value></header>"
            + "</headers></headers>";

    private final List<String> requestedPaths = new CopyOnWriteArrayList<>();
    private final List<String> authorizations = new CopyOnWriteArrayList<>();
    private HttpServer server;

    @BeforeEach
    public void startTank() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            requestedPaths.add(exchange.getRequestURI().getPath());
            authorizations.add(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] body = exchange.getRequestURI().getPath().equals("/v2/agent/headers")
                    ? HEADERS_XML.getBytes(StandardCharsets.UTF_8)
                    : new byte[0];
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
}
