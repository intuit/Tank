package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers the controller startup paths of {@link APITestHarness}: the HTTP ready handshake and the
 * controller-initiated WebSocket bootstrap. The controller is a local HTTP server.
 */
public class APITestHarnessStartHttpTest {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final File SCRIPT_FILE = new File("script.xml");
    private static final String TEST_PLAN_NAME = " project Apache 5 min (id186)";

    private HttpServer server;
    private String baseUrl;
    private String scriptXml;
    private String originalScript;
    private File dataFile;
    private final AtomicReference<String> readyAuthorization = new AtomicReference<>();
    private final AtomicReference<String> readyBody = new AtomicReference<>();
    private final AtomicReference<String> scriptAuthorization = new AtomicReference<>();

    @BeforeEach
    public void init() throws IOException {
        scriptXml = FileUtils.readFileToString(new File("src/test/resources/TEST_H.xml"), StandardCharsets.UTF_8);
        originalScript = SCRIPT_FILE.exists() ? FileUtils.readFileToString(SCRIPT_FILE, StandardCharsets.UTF_8) : null;
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    public void cleanup() throws IOException {
        server.stop(0);
        APITestHarness.destroyCurrentInstance();
        ThreadContext.clearMap();
        if (originalScript != null) {
            FileUtils.writeStringToFile(SCRIPT_FILE, originalScript, StandardCharsets.UTF_8);
        } else {
            FileUtils.deleteQuietly(SCRIPT_FILE);
        }
        if (dataFile != null) {
            FileUtils.deleteQuietly(dataFile);
        }
    }

    /**
     * Stubs AmazonUtil as an EC2 agent of job 777 would see it.
     */
    private static MockedStatic<AmazonUtil> mockAmazonUtil(boolean wsEnabled) {
        MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class);
        amazonUtil.when(AmazonUtil::getJobId).thenReturn("777");
        amazonUtil.when(AmazonUtil::getProjectName).thenReturn("Test Project");
        amazonUtil.when(AmazonUtil::getZone).thenReturn("us-east-1a");
        amazonUtil.when(AmazonUtil::getControllerBaseUrl).thenReturn("http://localhost:1");
        amazonUtil.when(AmazonUtil::getLoggingProfile).thenReturn(LoggingProfile.STANDARD);
        amazonUtil.when(AmazonUtil::usingEip).thenReturn(Boolean.FALSE);
        amazonUtil.when(AmazonUtil::getStopBehavior).thenReturn(StopBehavior.END_OF_SCRIPT);
        amazonUtil.when(AmazonUtil::getUserDataAsMap)
                .thenReturn(Map.of(TankConstants.KEY_COMMAND_WS_ENABLED, String.valueOf(wsEnabled)));
        amazonUtil.when(AmazonUtil::getCapacity).thenReturn(4);
        amazonUtil.when(AmazonUtil::getAgentToken).thenReturn("userdata-token");
        amazonUtil.when(AmazonUtil::getVMRegion).thenReturn(VMRegion.US_EAST);
        amazonUtil.when(AmazonUtil::getPublicIp).thenReturn("10.0.0.1");
        return amazonUtil;
    }

    private static AgentTestStartData startData(String scriptUrl) {
        AgentTestStartData data = new AgentTestStartData(scriptUrl, 20, 60000);
        data.setStartUsers(2);
        data.setJobId("777");
        data.setIncrementStrategy(IncrementStrategy.increasing);
        data.setUserIntervalIncrement(3);
        data.setSimulationTime(120000);
        data.setAgentInstanceNum(1);
        data.setTotalAgents(2);
        data.setTargetRampRate(0.5);
        return data;
    }

    private static void respond(HttpExchange exchange, byte[] body) throws IOException {
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    private static byte[] gzip(String content) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bytes)) {
            gzip.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return bytes.toByteArray();
    }

    private static void assertStartDataApplied(APITestHarness harness) {
        AgentRunData runData = harness.getAgentRunData();
        assertEquals(20, runData.getNumUsers());
        assertEquals(2, runData.getNumStartUsers());
        assertEquals(60000L, runData.getRampTimeMillis());
        assertEquals("777", runData.getJobId());
        assertEquals(IncrementStrategy.increasing, runData.getIncrementStrategy());
        assertEquals(3, runData.getUserInterval());
        assertEquals(120000L, runData.getSimulationTimeMillis());
        assertEquals(1, runData.getAgentInstanceNum());
        assertEquals(2, runData.getTotalAgents());
        assertEquals(0.5, runData.getTargetRampRate());
        assertEquals(StopBehavior.END_OF_SCRIPT, runData.getStopBehavior());
        assertEquals(IncrementStrategy.increasing.getDisplay(), ThreadContext.get("workloadType"));
        List<HDWorkload> workloads = TestPlanSingleton.getInstance().getTestPlans();
        assertEquals(1, workloads.size());
        assertEquals(TEST_PLAN_NAME, workloads.get(0).getName());
    }

    @Test
    public void testStartHttpRegistersWithControllerAndDownloadsFiles() throws Exception {
        String dataDir = new TankConfig().getAgentConfig().getAgentDataFileStorageDir();
        String dataFileName = "apitestharness-" + UUID.randomUUID() + ".csv";
        dataFile = new File(dataDir, dataFileName);
        AgentTestStartData startData = startData(baseUrl + "/script.xml");
        startData.setDataFiles(new DataFileRequest[]{
                new DataFileRequest(dataFileName, true, baseUrl + "/data/" + dataFileName)});

        server.createContext("/v2/agent/ready", exchange -> {
            readyAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            readyBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            respond(exchange, JSON_MAPPER.writeValueAsBytes(startData));
        });
        server.createContext("/script.xml", exchange -> {
            scriptAuthorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.getResponseHeaders().add("Content-Encoding", "gzip");
            respond(exchange, gzip(scriptXml));
        });
        server.createContext("/data/", exchange -> {
            exchange.getResponseHeaders().add("Content-Encoding", "gzip");
            respond(exchange, gzip("user,pass\na,b\n"));
        });

        ResultsReporter reporter = mock(ResultsReporter.class);
        try (MockedStatic<AmazonUtil> amazonUtil = mockAmazonUtil(false);
             MockedStatic<ReportingFactory> reportingFactory = Mockito.mockStatic(ReportingFactory.class);
             MockedStatic<CommandListener> commandListener = Mockito.mockStatic(CommandListener.class);
             MockedConstruction<StartedChecker> checkers = Mockito.mockConstruction(StartedChecker.class);
             // keep the default data file copy from overwriting the shared csv-data.txt
             MockedStatic<FileUtils> fileUtils = Mockito.mockStatic(FileUtils.class, invocation ->
                     "copyFile".equals(invocation.getMethod().getName()) && invocation.getArguments().length == 2
                             ? null : invocation.callRealMethod())) {
            reportingFactory.when(ReportingFactory::getResultsReporter).thenReturn(reporter);
            // unreadable user data leaves the WebSocket setting to the agent config, which keeps it off
            amazonUtil.when(AmazonUtil::getUserDataAsMap).thenThrow(new IllegalStateException("no user data"));
            // HostInfo in main() and startHttp() looks the host up first; the agent url lookup then fails once
            amazonUtil.when(AmazonUtil::getPublicHostName)
                    .thenReturn("agent-host", "agent-host")
                    .thenThrow(new IOException("metadata not ready"))
                    .thenReturn("agent-host");

            APITestHarness.main(new String[]{"-http=" + baseUrl, "-token=agent-token", "-instanceId=i-agent"});

            APITestHarness harness = APITestHarness.getInstance();
            int agentPort = harness.getTankConfig().getAgentConfig().getAgentPort();
            assertFalse(harness.isLocal());
            assertSame(reporter, harness.getResultsReporter());
            commandListener.verify(() -> CommandListener.startHttpServer(agentPort));

            // the ready call identifies this agent to the controller
            assertEquals("bearer agent-token", readyAuthorization.get());
            JsonNode agentData = JSON_MAPPER.readTree(readyBody.get());
            assertEquals("http://agent-host:" + agentPort, agentData.get("instanceUrl").asString());
            assertEquals("i-agent", agentData.get("instanceId").asString());
            assertEquals("777", agentData.get("jobId").asString());
            assertEquals(4, agentData.get("capacity").asInt());

            // the gzipped script is unpacked, saved and loaded as the test plan
            assertEquals("bearer agent-token", scriptAuthorization.get());
            assertEquals(scriptXml, FileUtils.readFileToString(SCRIPT_FILE, StandardCharsets.UTF_8));
            assertStartDataApplied(harness);

            // the data file is saved, and copied to the default name because it is the default file
            assertEquals("user,pass\na,b\n", FileUtils.readFileToString(dataFile, StandardCharsets.UTF_8));
            fileUtils.verify(() -> FileUtils.copyFile(dataFile, new File(dataDir, TankConstants.DEFAULT_CSV_FILE_NAME)));

            // the startup watchdog is armed
            assertEquals(1, checkers.constructed().size());
            assertFalse(harness.isStarted());
        }
    }

    @Test
    public void testStartHttpBootstrapsOverCommandWebSocket() throws Exception {
        FileUtils.writeStringToFile(SCRIPT_FILE, scriptXml, StandardCharsets.UTF_8);
        AgentTestStartData startData = startData("unused");
        List<List<?>> serverArgs = new ArrayList<>();

        try (MockedStatic<AmazonUtil> ignored = mockAmazonUtil(true);
             MockedStatic<ReportingFactory> reportingFactory = Mockito.mockStatic(ReportingFactory.class);
             MockedStatic<CommandListener> commandListener = Mockito.mockStatic(CommandListener.class);
             MockedConstruction<StartedChecker> checkers = Mockito.mockConstruction(StartedChecker.class);
             MockedConstruction<AgentCommandWebSocketServer> wsServers =
                     Mockito.mockConstruction(AgentCommandWebSocketServer.class, (wsServer, context) -> {
                         serverArgs.add(context.arguments());
                         when(wsServer.awaitInitialTransfer(anyLong())).thenReturn(true);
                         when(wsServer.getReceivedJobConfig()).thenReturn(startData);
                     })) {
            reportingFactory.when(ReportingFactory::getResultsReporter).thenReturn(mock(ResultsReporter.class));

            // no -token, so the agent token comes from the user data
            APITestHarness.main(new String[]{"-http=" + baseUrl, "-instanceId=i-ws", "-capacity=6"});

            APITestHarness harness = APITestHarness.getInstance();
            int agentPort = harness.getTankConfig().getAgentConfig().getAgentPort();
            assertEquals(1, wsServers.constructed().size());
            AgentCommandWebSocketServer wsServer = wsServers.constructed().get(0);
            assertEquals(List.of(agentPort, "i-ws", "777", 6, "userdata-token"), serverArgs.get(0));
            verify(wsServer).start();
            verify(wsServer).awaitInitialTransfer(Math.max(60_000L,
                    harness.getTankConfig().getAgentConfig().getMaxAgentWaitTime()));
            verify(wsServer).markInitialBootstrapReady();
            assertSame(wsServer, harness.getCommandWebSocketServer());

            // the job config and local script.xml pushed over the socket drive the test
            assertStartDataApplied(harness);
            assertFalse(harness.isLocal());
            assertEquals(1, checkers.constructed().size());
            // the HTTP command listener is not used on the WebSocket path
            commandListener.verify(() -> CommandListener.startHttpServer(anyInt()), Mockito.never());
        }
    }
}
