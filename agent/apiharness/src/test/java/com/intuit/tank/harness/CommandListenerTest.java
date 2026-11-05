package com.intuit.tank.harness;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.settings.TankConfig;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandListenerTest {

    private APITestHarness harness;
    private APITestHarness originalHarness;
    private Runnable originalKillAction;
    private boolean originalStarted;
    private final AtomicInteger kills = new AtomicInteger();
    private final WatsAgentStatusResponse status = new WatsAgentStatusResponse(1000, 0, 0, 0, 0, 0, 0, 3, 10, 0);

    @BeforeEach
    public void setUp() throws Exception {
        harness = mock(APITestHarness.class);
        AgentRunData agentRunData = new AgentRunData();
        agentRunData.setJobId("42");
        when(harness.getAgentRunData()).thenReturn(agentRunData);
        when(harness.getStatus()).thenReturn(status);
        // what a log event reads from the harness, should one be built on a thread new to logging
        when(harness.getTankConfig()).thenReturn(mock(TankConfig.class));
        when(harness.isLocal()).thenReturn(true);
        Field instance = staticField(APITestHarness.class, "instance");
        originalHarness = (APITestHarness) instance.get(null);
        instance.set(null, null);
        LogUtil.getLogEvent(); // build this thread's log event from a real harness, as other tests would
        instance.set(null, harness);
        originalStarted = staticField(CommandListener.class, "started").getBoolean(null);
        originalKillAction = CommandListener.killAction;
        CommandListener.killAction = kills::incrementAndGet;
    }

    @AfterEach
    public void tearDown() throws Exception {
        CommandListener.killAction = originalKillAction;
        staticField(CommandListener.class, "started").setBoolean(null, originalStarted);
        staticField(APITestHarness.class, "instance").set(null, originalHarness);
    }

    @Test
    public void testApplyCommandUnknownThrows() {
        assertThrows(UnsupportedOperationException.class, () -> CommandListener.applyCommand("not-a-real-command"));
    }

    @Test
    public void testApplyCommandNullThrows() {
        assertThrows(UnsupportedOperationException.class, () -> CommandListener.applyCommand(null));
    }

    @ParameterizedTest
    @CsvSource({"stop, stop", "pause, pause", "pause_ramp, pause_ramp", "resume_ramp, resume_ramp", "resume, resume_ramp"})
    public void testApplyCommandSetsHarnessCommand(String command, AgentCommand expected) {
        CommandListener.applyCommand(command);

        verify(harness).setCommand(expected);
    }

    @ParameterizedTest
    @CsvSource({"start", "run"})
    public void testApplyCommandStartLaunchesTestPlansInBackground(String command) {
        CommandListener.applyCommand(command);

        verify(harness, timeout(5000)).runConcurrentTestPlans();
    }

    @Test
    public void testApplyCommandKillRunsKillAction() {
        CommandListener.applyCommand("kill");

        assertEquals(1, kills.get());
        verify(harness, never()).setCommand(any());
    }

    @Test
    public void testStartHttpServerStartsOnlyOnce() {
        try (MockedStatic<HttpServer> httpServer = mockStatic(HttpServer.class)) {
            HttpServer server = stubServer(httpServer);

            CommandListener.startHttpServer(8090);
            CommandListener.startHttpServer(8090);

            httpServer.verify(() -> HttpServer.create(argThat(address -> address.getPort() == 8090), eq(0)), times(1));
            verify(server).createContext("/");
            verify(server, times(1)).start();
        }
    }

    @Test
    public void testMainListensOnPort8080() {
        try (MockedStatic<HttpServer> httpServer = mockStatic(HttpServer.class)) {
            HttpServer server = stubServer(httpServer);

            CommandListener.main(new String[0]);

            httpServer.verify(() -> HttpServer.create(argThat(address -> address.getPort() == 8080), eq(0)));
            verify(server).start();
        }
    }

    @Test
    public void testStartHttpServerRetriesWhilePortInUse() {
        try (MockedStatic<HttpServer> httpServer = mockStatic(HttpServer.class)) {
            HttpServer server = mock(HttpServer.class);
            when(server.createContext("/")).thenReturn(mock(HttpContext.class));
            AtomicInteger attempts = new AtomicInteger();
            httpServer.when(() -> HttpServer.create(any(InetSocketAddress.class), anyInt())).thenAnswer(invocation -> {
                if (attempts.incrementAndGet() < 3) {
                    throw portInUse();
                }
                return server;
            });

            CommandListener.startHttpServer(8090);

            assertEquals(3, attempts.get());
            verify(server).start();
        }
    }

    @Test
    public void testStartHttpServerGivesUpWhenPortStaysInUse() {
        try (MockedStatic<HttpServer> httpServer = mockStatic(HttpServer.class)) {
            AtomicInteger attempts = new AtomicInteger();
            httpServer.when(() -> HttpServer.create(any(InetSocketAddress.class), anyInt())).thenAnswer(invocation -> {
                attempts.incrementAndGet();
                throw portInUse();
            });

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> CommandListener.startHttpServer(8090));

            assertTrue(thrown.getMessage().startsWith("Maximum attempts reached"));
            assertEquals(180, attempts.get());
        }
    }

    @Test
    public void testStartHttpServerFailsFastOnOtherIoErrors() {
        try (MockedStatic<HttpServer> httpServer = mockStatic(HttpServer.class)) {
            httpServer.when(() -> HttpServer.create(any(InetSocketAddress.class), anyInt()))
                    .thenThrow(new IOException("no network"));

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> CommandListener.startHttpServer(8090));

            assertInstanceOf(IOException.class, thrown.getCause());
            httpServer.verify(() -> HttpServer.create(any(InetSocketAddress.class), anyInt()), times(1));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "/stop, 'Received command /stop, Stopping Test JobId=42', stop",
            "/pause, 'Received command /pause, Pausing Test JobId=42', pause",
            "/ramp_pause, 'Received command /ramp_pause, Pausing Ramp for Test JobId=42', pause_ramp",
            "/resume, 'Received command /resume, Resume Test JobId=42', resume_ramp"
    })
    public void testHttpCommandSetsHarnessCommandAndIsAccepted(String path, String expectedBody, AgentCommand expected) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        Headers headers = new Headers();
        HttpExchange exchange = exchange(path, headers, body);

        captureHandler().handle(exchange);

        verify(harness).setCommand(expected);
        verify(exchange).sendResponseHeaders(202, expectedBody.length());
        assertEquals(expectedBody, body.toString(StandardCharsets.UTF_8));
        assertEquals("text/plain", headers.getFirst("Content-Type"));
        assertEquals("Intuit Tank Agent/4.0.0", headers.getFirst("Server"));
        verify(exchange).close();
    }

    @Test
    public void testHttpStartLaunchesTestPlans() throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        HttpExchange exchange = exchange("/start", new Headers(), body);

        captureHandler().handle(exchange);

        verify(harness, timeout(5000)).runConcurrentTestPlans();
        verify(exchange).sendResponseHeaders(eq(202), anyLong());
        assertEquals("Received command /start, Starting Test JobId=42", body.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testHttpKillRepliesBeforeRunningKillAction() throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        HttpExchange exchange = exchange("/kill", new Headers(), body);
        HttpHandler handler = captureHandler();
        // the real kill action exits the JVM, so the reply must already be on its way
        List<String> bodyAtKill = new ArrayList<>();
        CommandListener.killAction = () -> {
            bodyAtKill.add(body.toString(StandardCharsets.UTF_8));
            verify(exchange).close();
        };

        handler.handle(exchange);

        assertEquals(List.of("Received command /kill, Killing Test JobId=42"), bodyAtKill);
        verify(exchange).sendResponseHeaders(eq(202), anyLong());
    }

    @Test
    public void testHttpKillStillKillsWhenTheReplyFails() throws IOException {
        OutputStream broken = mock(OutputStream.class);
        doThrow(new IOException("connection reset")).when(broken).write(any(byte[].class));
        HttpExchange exchange = exchange("/kill", new Headers(), broken);

        captureHandler().handle(exchange);

        assertEquals(1, kills.get());
    }

    @ParameterizedTest
    @CsvSource({"/status", "/staus"})
    public void testHttpStatusRepliesWithHarnessStatusWithoutChangingIt(String path) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        HttpExchange exchange = exchange(path, new Headers(), body);

        captureHandler().handle(exchange);

        verify(exchange).sendResponseHeaders(eq(202), anyLong());
        assertEquals(status.toString(), body.toString(StandardCharsets.UTF_8));
        // asking for the status of a paused agent must not resume it
        verify(harness, never()).setCommand(any());
    }

    @Test
    public void testApplyCommandStatusChangesNothing() {
        CommandListener.applyCommand("status");

        verify(harness, never()).setCommand(any());
        assertEquals(0, kills.get());
    }

    @Test
    public void testHttpUnknownPathIsNotFound() throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        HttpExchange exchange = exchange("/not-a-command", new Headers(), body);

        captureHandler().handle(exchange);

        verify(exchange).sendResponseHeaders(404, "Not Found".length());
        assertEquals("Not Found", body.toString(StandardCharsets.UTF_8));
        verify(harness, never()).setCommand(any());
    }

    @Test
    public void testHttpWriteFailureIsSwallowed() throws IOException {
        OutputStream broken = mock(OutputStream.class);
        doThrow(new IOException("connection reset")).when(broken).write(any(byte[].class));
        HttpExchange exchange = exchange("/stop", new Headers(), broken);

        captureHandler().handle(exchange);

        verify(harness).setCommand(AgentCommand.stop);
        verify(exchange, never()).close();
    }

    /** Starts the listener against a mocked server and returns the handler it registered. */
    private HttpHandler captureHandler() {
        try (MockedStatic<HttpServer> httpServer = mockStatic(HttpServer.class)) {
            HttpServer server = mock(HttpServer.class);
            HttpContext context = mock(HttpContext.class);
            when(server.createContext("/")).thenReturn(context);
            httpServer.when(() -> HttpServer.create(any(InetSocketAddress.class), anyInt())).thenReturn(server);

            CommandListener.startHttpServer(8090);

            ArgumentCaptor<HttpHandler> handler = ArgumentCaptor.forClass(HttpHandler.class);
            verify(context).setHandler(handler.capture());
            return handler.getValue();
        }
    }

    private static HttpServer stubServer(MockedStatic<HttpServer> httpServer) {
        HttpServer server = mock(HttpServer.class);
        when(server.createContext("/")).thenReturn(mock(HttpContext.class));
        httpServer.when(() -> HttpServer.create(any(InetSocketAddress.class), anyInt())).thenReturn(server);
        return server;
    }

    private static HttpExchange exchange(String path, Headers headers, OutputStream body) {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestURI()).thenReturn(URI.create("http://localhost:8090" + path));
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(exchange.getResponseBody()).thenReturn(body);
        return exchange;
    }

    /** A bind failure that also interrupts this thread, so the listener's retry sleep returns at once. */
    private static BindException portInUse() {
        Thread.currentThread().interrupt();
        return new BindException("Address already in use");
    }

    private static Field staticField(Class<?> type, String name) throws NoSuchFieldException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
