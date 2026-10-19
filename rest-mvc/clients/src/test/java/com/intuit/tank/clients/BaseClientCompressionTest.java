package com.intuit.tank.clients;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseClientCompressionTest {

    private static final String SCRIPT_XML = "<scriptGroup><scriptStep name=\"café\"/></scriptGroup>";
    private static final String NAMES_JSON = "{\"1\":\"café\"}";

    private HttpServer server;
    private final Map<String, String> acceptEncodingByPath = new ConcurrentHashMap<>();
    private volatile boolean compress;

    @BeforeEach
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/v2/projects/download/7", exchange -> respond(exchange, "application/xml", SCRIPT_XML));
        server.createContext("/v2/projects/names", exchange -> respond(exchange, "application/json", NAMES_JSON));
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }

    private void respond(HttpExchange exchange, String contentType, String body) throws IOException {
        String acceptEncoding = exchange.getRequestHeaders().getFirst("Accept-Encoding");
        acceptEncodingByPath.put(exchange.getRequestURI().getPath(), String.valueOf(acceptEncoding));
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        if (compress) {
            ByteArrayOutputStream gzipped = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(gzipped)) {
                gzip.write(bytes);
            }
            bytes = gzipped.toByteArray();
            exchange.getResponseHeaders().add("Content-Encoding", "gzip");
        }
        exchange.getResponseHeaders().add("Content-Type", contentType + ";charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private ProjectClient client() {
        return new ProjectClient("http://127.0.0.1:" + server.getAddress().getPort(), "token");
    }

    @Test
    public void requestsGzipAndDecodesCompressedStream() throws Exception {
        compress = true;
        assertEquals(SCRIPT_XML, client().downloadTestScriptForProject(7));
        assertEquals("gzip", acceptEncodingByPath.get("/v2/projects/download/7"));
    }

    @Test
    public void decodesCompressedString() throws Exception {
        compress = true;
        assertEquals(Map.of("1", "café"), client().getProjectNames());
        assertEquals("gzip", acceptEncodingByPath.get("/v2/projects/names"));
    }

    @Test
    public void readsUncompressedResponses() throws Exception {
        compress = false;
        assertEquals(SCRIPT_XML, client().downloadTestScriptForProject(7));
        assertEquals(Map.of("1", "café"), client().getProjectNames());
    }
}
