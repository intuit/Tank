package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.Header;
import com.intuit.tank.harness.data.HDResponse;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.tools.debugger.DebugStep;
import com.intuit.tank.tools.debugger.JaxbUtil;
import com.intuit.tank.tools.debugger.ScriptChangedListener;
import com.intuit.tank.tools.debugger.StepListener;
import com.intuit.tank.tools.debugger.TankConfigPin;
import com.sun.net.httpserver.HttpServer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class HeadlessDebuggerSetupTest {

    private static final String HEADERS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<headers xmlns=\"urn:wats/domain/agent/v1\"><headers>"
            + "<header><key>intuit_test</key><value>TtoPerf_QaController</value></header>"
            + "</headers></headers>";
    /** A response body that makes the loopback server drop the connection without answering */
    private static final String DROP_CONNECTION = "<drop>";

    private final List<String> requestedPaths = new CopyOnWriteArrayList<>();
    private final List<String> authorizations = new CopyOnWriteArrayList<>();
    private final Map<String, String> responses = new ConcurrentHashMap<>(Map.of("/v2/agent/headers", HEADERS_XML));
    private HttpServer server;
    private String userDir;
    private LogCapture log;

    @TempDir
    Path reportDir;

    @BeforeAll
    public static void loadTankConfig() throws IOException {
        StableTankConfig.load();
    }

    @BeforeEach
    public void startTank() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            requestedPaths.add(exchange.getRequestURI().getPath());
            authorizations.add(exchange.getRequestHeaders().getFirst("Authorization"));
            String response = responses.getOrDefault(exchange.getRequestURI().getPath(), "");
            if (DROP_CONNECTION.equals(response)) {
                exchange.close();
                return;
            }
            byte[] body = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(body.length == 0 ? 404 : 200, body.length == 0 ? -1 : body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        // quit() writes validation-report.json to the working directory; point it at a temp dir
        userDir = System.getProperty("user.dir");
        System.setProperty("user.dir", reportDir.toString());
        log = new LogCapture(HeadlessDebuggerSetup.class);
    }

    @AfterEach
    public void stopTank() {
        log.close();
        System.setProperty("user.dir", userDir);
        server.stop(0);
        // only quit() deletes a setup's working dir, and not every test quits
        TankConfigPin.deleteCurrentWorkingDirAtExit();
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

    @Test
    public void largeScriptWithNinetyFivePercentReplacedPassesTheHostnameCheck() throws Exception {
        List<TestStep> steps = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            steps.add(request(i < 190 ? "#{host}/" + i : "https://prod.example.com/" + i));
        }
        serveProject(script("p", null, steps));

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertTrue(setup.isReady());
        assertEquals(200, setup.getSteps().size());
    }

    @Test
    public void smallUnfilteredScriptPassesTheHostnameCheck() throws Exception {
        // scripts under 200 steps (UC7) are let through even when not filtered
        serveProject(scriptWithRequests(10, "https://prod.example.com/x"));

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertTrue(setup.isReady());
        assertFalse(setup.isHostnameCheckFailed());
    }

    @Test
    public void projectThatTankReturnsAsNullIsNotReady() {
        responses.put("/v2/projects/1", "null");

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertFalse(setup.isReady());
        assertNull(setup.getCurrentWorkload());
        assertFalse(requestedPaths.contains("/v2/projects/download/1"));
    }

    @Test
    public void blankScriptIsNotReady() {
        serveProject("   ");

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertFalse(setup.isReady());
        assertNull(setup.getCurrentWorkload());
        assertTrue(setup.getSteps().isEmpty());
    }

    @Test
    public void scriptThatIsNotXmlIsNotReady() {
        serveProject("<workload");

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertFalse(setup.isReady());
        assertNull(setup.getCurrentWorkload());
    }

    @Test
    public void droppedScriptDownloadIsNotReady() {
        serveProject(DROP_CONNECTION);

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertFalse(setup.isReady());
        assertTrue(requestedPaths.contains("/v2/projects/download/1"));
        assertTrue(log.contains("Error: Empty or Invalid Tank URL"));
    }

    @Test
    public void projectVariablesComeFromTheProject() throws Exception {
        responses.put("/v2/projects/1",
                "{\"id\":1,\"name\":\"p\",\"variables\":[{\"key\":\"authid\",\"value\":\"a1\"},{\"key\":\"env\",\"value\":\"qa\"}]}");
        responses.put("/v2/projects/download/1", scriptWithRequests(1, "#{host}/start"));

        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");

        assertEquals(Map.of("authid", "a1", "env", "qa"), setup.getProjectVariables());
        setup.setProjectVariables(Map.of("only", "one"));
        assertEquals(Map.of("only", "one"), setup.getProjectVariables());
        setup.setProjectVariables((Map<String, String>) null);
        assertTrue(setup.getProjectVariables().isEmpty());
    }

    @Test
    public void changingTheWorkloadNotifiesListeners() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/a"), request("#{host}/b"))));
        StepListener stepListener = mock(StepListener.class);
        ScriptChangedListener scriptListener = mock(ScriptChangedListener.class);
        setup.addStepChangedListener(stepListener);
        setup.addScriptChangedListener(scriptListener);
        HDWorkload workload = setup.getCurrentWorkload();

        setup.setCurrentWorkload(workload);

        assertSame(workload.getPlans().get(0), setup.getCurrentTestPlan());
        assertEquals(-1, setup.getCurrentRunningStep());
        verify(stepListener).stepChanged(setup.getSteps().get(0));
        verify(scriptListener).scriptChanged(workload.getPlans().get(0));

        // a workload without plans, and no workload, leave nothing to step through
        HDWorkload empty = new HDWorkload();
        setup.setCurrentWorkload(empty);
        assertNull(setup.getCurrentTestPlan());
        assertTrue(setup.getSteps().isEmpty());
        setup.setCurrentWorkload(null);
        assertNull(setup.getCurrentWorkload());
        assertNull(setup.getCurrentTestPlan());
    }

    @Test
    public void successfulStepIsLoggedAndRecorded() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/start"), request("#{host}/next"))));
        StepListener listener = mock(StepListener.class);
        setup.addStepChangedListener(listener);
        DebugStep first = setup.getSteps().get(0);
        TestStepContext context = context(setup.getStep(0), response(200, "{}"),
                Map.of("authid", "auth-1", "username", "user-1", "srsversion", "srs-1"));

        setup.stepStarted(context);
        assertEquals(0, setup.getCurrentRunningStep());
        assertEquals("auth-1", first.getEntryVariables().get("authid"));
        verify(listener).stepEntered(first);
        setup.stepFinished(context);

        assertTrue(log.contains("1,GET,#{host}/start,SUCCESS"));
        assertSame(context.getResponse(), first.getResponse());
        assertSame(context.getRequest(), first.getRequest());
        verify(listener).stepExited(first);
        assertFalse(log.contains("******** REQUEST *********"));

        setup.quit();

        assertTrue(log.contains("authid,auth-1"));
        assertTrue(log.contains("username,user-1"));
        assertTrue(log.contains("srsversion,srs-1"));
        assertTrue(log.contains("total_steps,2"));
        assertTrue(log.contains("total_executed,1"));
        assertTrue(log.contains("execution_percentage,50"));
        assertTrue(log.contains("filing_status,false"));
        assertTrue(log.anyContains("timestamp,"));
        JsonNode summary = report().path("summary");
        assertEquals("auth-1", summary.path("authId").asString());
        assertEquals(1, summary.path("successCount").asInt());
        assertEquals(1, report().path("steps").size());
        // quit cleans up the working dir that held settings.xml
        assertFalse(new File(System.getProperty("WATS_PROPERTIES")).exists());
    }

    @Test
    public void errorResponseLogsTheRequestResponseAndVariables() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/start"))));
        TestStepContext context = context(setup.getStep(0), response(500, "boom"), Map.of("k", "v"));
        when(context.getRequest().getLogMsg()).thenReturn("REQUEST LOG");
        when(context.getResponse().getLogMsg()).thenReturn("RESPONSE LOG");

        setup.stepStarted(context);
        setup.stepFinished(context);

        assertTrue(log.contains("1,GET,#{host}/start,FAILURE"));
        assertTrue(log.contains("******** REQUEST *********"));
        assertTrue(log.contains("REQUEST LOG"));
        assertTrue(log.contains("RESPONSE LOG"));
        setup.quit();
        assertEquals(1, report().path("summary").path("failureCount").asInt());
    }

    @Test
    public void failedStepWithoutARequestOrResponseIsStillRecorded() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/start"))));
        // the request runner threw before it built a request, so there is neither a request nor a response
        TestStepContext context = context(setup.getStep(0), null, Map.of());
        context.setRequest(null);

        setup.stepStarted(context);
        setup.stepFinished(context);

        assertTrue(log.contains("1,GET,#{host}/start,FAILURE"));
        assertTrue(log.contains("no request"));
        assertTrue(log.contains("no response"));
        setup.quit();
        JsonNode steps = report().path("steps");
        assertEquals(1, steps.size());
        assertFalse(steps.get(0).path("success").asBoolean());
    }

    @Test
    public void filingStatusIsLeftOutForUC2AndUC7Scripts() throws Exception {
        for (String name : List.of("Return UC2", "Return UC7")) {
            HeadlessDebuggerSetup setup = readySetup(script(name, null, List.of(request("#{host}/start"))));
            setup.quit();
            assertFalse(log.anyContains("filing_status,"), name);
        }
    }

    @Test
    public void validationErrorOrNoConnectionFailsTheStep() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/a"), request("#{host}/b"))));
        TestStepContext invalid = context(setup.getStep(0), response(200, "ok"), Map.of());
        invalid.addError(new ErrorContainer("body", null, null, "missing text"));
        TestStepContext noConnection = context(setup.getStep(1), response(-1, null), Map.of());

        setup.stepStarted(invalid);
        setup.stepFinished(invalid);
        setup.stepStarted(noConnection);
        setup.stepFinished(noConnection);

        assertTrue(log.contains("1,GET,#{host}/a,FAILURE"));
        assertTrue(log.contains("2,GET,#{host}/b,FAILURE"));
        setup.quit();
        JsonNode report = report();
        assertEquals(2, report.path("summary").path("failureCount").asInt());
        assertEquals("missing text", report.path("steps").get(0).path("errors").get(0).path("message").asString());
    }

    @Test
    public void stepWithoutAResponseIsLoggedAsFailure() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/start"))));
        TestStepContext context = context(setup.getStep(0), null, Map.of());

        setup.stepStarted(context);
        setup.stepFinished(context);

        assertTrue(log.contains("1,GET,#{host}/start,FAILURE"));
    }

    @Test
    public void successfulFilingReturnAndTaxhubAreReported() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null,
                List.of(request("#{host}/taxhub/start"), request("#{host}/filing-return"))));
        TestStepContext taxhub = context(setup.getStep(0), response(200, "ok"), Map.of());
        TestStepContext filing = context(setup.getStep(1),
                response(200, "{\"data\":{\"transmitResponse\":{\"TransmitStatus\":\"success\"}}}"), Map.of());

        setup.stepStarted(taxhub);
        setup.stepFinished(taxhub);
        setup.stepStarted(filing);
        setup.stepFinished(filing);
        setup.quit();

        assertTrue(log.contains("taxhub_flow,true"));
        assertTrue(log.contains("filing_status,true"));
        JsonNode summary = report().path("summary");
        assertTrue(summary.path("taxhubFlow").asBoolean());
        assertTrue(summary.path("filingStatus").asBoolean());
    }

    @Test
    public void filingReturnThatIsNotJsonLeavesFilingStatusFalse() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/filing-return"))));
        TestStepContext filing = context(setup.getStep(0), response(200, "not json"), Map.of());

        setup.stepStarted(filing);
        setup.stepFinished(filing);
        setup.quit();

        assertTrue(log.contains("filing_status,false"));
        assertFalse(report().path("summary").path("filingStatus").asBoolean());
    }

    @Test
    public void summaryUsesTheProjectNameAndIdFromTheWorkloadName() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("project My Return (id42)", null, List.of(request("#{host}/a"))));

        setup.quit();

        assertTrue(log.contains("project,My Return"));
        assertTrue(log.contains("project_id,42"));
        assertTrue(log.contains("total_executed,0"));
    }

    @Test
    public void summaryFallsBackToTheWorkloadNameAndProjectId() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("plain", null, List.of(request("#{host}/a"))));

        setup.quit();

        assertTrue(log.contains("project,plain"));
        assertTrue(log.contains("project_id,1"));
        assertEquals("plain", report().path("summary").path("workloadName").asString());
    }

    @Test
    public void skippedStepsAreRecordedInTheReport() throws Exception {
        ThinkTimeStep think = new ThinkTimeStep();
        think.setMinTime("1");
        think.setMaxTime("2");
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/a"), think)));

        setup.recordSkippedStep(setup.getStep(1));
        setup.stepExecuted();
        setup.quit();

        assertTrue(log.contains("total_executed,1"));
        JsonNode report = report();
        assertEquals(1, report.path("summary").path("skippedCount").asInt());
        assertEquals("THINK_TIME", report.path("steps").get(0).path("stepType").asString());
    }

    @Test
    public void stepEventsOutsideTheScriptAreIgnored() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/a"))));
        StepListener listener = mock(StepListener.class);
        setup.addStepChangedListener(listener);
        RequestStep outside = (RequestStep) request("#{host}/z");
        outside.setStepIndex(5);
        TestStepContext context = context(outside, response(200, "ok"), Map.of());

        setup.stepStarted(context);
        setup.stepFinished(context);

        verify(listener, never()).stepEntered(any());
        verify(listener, never()).stepExited(any());
        assertTrue(log.anyContains("Error starting step"));
        assertTrue(log.anyContains("Error finishing step"));
        // the listeners hear about indexes outside the script as a null step
        setup.fireStepChanged(-1);
        setup.fireStepStarted(9);
        setup.fireStepExited(9);
        verify(listener).stepChanged(null);
        verify(listener).stepEntered(null);
        verify(listener).stepExited(null);
    }

    @Test
    public void nextStepAndStopTrackTheRun() throws Exception {
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(request("#{host}/a"), request("#{host}/b"))));

        setup.setNextStep(context(setup.getStep(1), null, Map.of()));
        assertEquals(1, setup.getCurrentRunningStep());
        setup.setCurrentStep(0);
        assertEquals(0, setup.getCurrentRunningStep());

        // stopping before a run has started just clears the run state
        setup.stop();
        assertNull(setup.getRunningThread());
    }

    @Test
    public void startRunsTheScriptAgainstTheTarget() throws Exception {
        responses.put("/app/start", "started");
        ThinkTimeStep think = new ThinkTimeStep();
        think.setMinTime("0");
        think.setMaxTime("0");
        HDTestVariables variables = new HDTestVariables(true);
        HeadlessDebuggerSetup setup = readySetup(script("p", variables,
                List.of(target("#{host}/start", "/app/start"), think, target("#{host}/missing", "/app/missing"))));
        setup.setProjectVariables(Map.of("authid", "run-auth"));

        setup.start();
        Thread run = setup.getRunningThread();
        assertNotNull(run);
        setup.next();
        run.join(TimeUnit.SECONDS.toMillis(60));
        assertFalse(run.isAlive());
        setup.stop();
        setup.quit();

        assertTrue(requestedPaths.contains("/app/start"));
        assertTrue(log.contains("1,GET,#{host}/start,SUCCESS"));
        assertTrue(log.contains("3,GET,#{host}/missing,FAILURE"));
        assertTrue(log.contains("authid,run-auth"));
        assertTrue(log.contains("total_executed,3"));
        JsonNode summary = report().path("summary");
        assertEquals(1, summary.path("successCount").asInt());
        assertEquals(1, summary.path("failureCount").asInt());
        assertEquals(1, summary.path("skippedCount").asInt());
    }

    @Test
    public void stopKillsARunningScript() throws Exception {
        responses.put("/app/start", "started");
        HeadlessDebuggerSetup setup = readySetup(script("p", null, List.of(target("#{host}/start", "/app/start"))));

        setup.start();
        Thread run = setup.getRunningThread();
        setup.stop();

        assertNull(setup.getRunningThread());
        run.join(TimeUnit.SECONDS.toMillis(60));
        assertFalse(run.isAlive());
    }

    private String tankUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    private void serveProject(String scriptXml) {
        responses.put("/v2/projects/1", "{\"id\":1,\"name\":\"p\",\"variables\":[]}");
        responses.put("/v2/projects/download/1", scriptXml);
    }

    private HeadlessDebuggerSetup readySetup(String scriptXml) {
        serveProject(scriptXml);
        HeadlessDebuggerSetup setup = new HeadlessDebuggerSetup(tankUrl(), 1, "tok");
        assertTrue(setup.isReady());
        return setup;
    }

    private JsonNode report() {
        File json = reportDir.resolve("validation-report.json").toFile();
        assertTrue(json.isFile());
        return JsonMapper.builder().build().readTree(json);
    }

    private static TestStepContext context(TestStep step, BaseResponse response, Map<String, String> vars) {
        Variables variables = new Variables();
        vars.forEach(variables::addVariable);
        TestStepContext context = new TestStepContext(step, variables, "plan", "user", null, mock(TestPlanRunner.class));
        context.setRequest(mock(BaseRequest.class));
        context.setResponse(response);
        return context;
    }

    private static BaseResponse response(int code, String body) {
        BaseResponse response = mock(BaseResponse.class);
        when(response.getHttpCode()).thenReturn(code);
        when(response.getBody()).thenReturn(body);
        return response;
    }

    static TestStep request(String label) {
        HDRequest request = new HDRequest();
        request.setLabel(label);
        request.setMethod("GET");
        RequestStep step = new RequestStep();
        step.setRequest(request);
        return step;
    }

    private TestStep target(String label, String path) {
        return target(server.getAddress().getPort(), label, path);
    }

    /** A request the harness can send to a loopback server; also used by HeadlessDebuggerCliTest */
    static TestStep target(int port, String label, String path) {
        RequestStep step = (RequestStep) request(label);
        step.getRequest().setProtocol("http");
        step.getRequest().setHost("localhost");
        step.getRequest().setPort(Integer.toString(port));
        step.getRequest().setPath(path);
        // JAXB drops empty lists, and the request runner expects each list to be present
        step.getRequest().setQueryString(new ArrayList<>(List.of(header("q", "1"))));
        step.getRequest().setRequestHeaders(new ArrayList<>(List.of(header("Accept", "text/plain"))));
        step.getRequest().setPostDatas(new ArrayList<>(List.of(header("p", "1"))));
        step.setResponse(new HDResponse());
        step.setOnFail("abort");
        return step;
    }

    private static Header header(String key, String value) {
        Header header = new Header();
        header.setKey(key);
        header.setValue(value);
        header.setAction("set");
        return header;
    }

    private static String scriptWithRequests(int count, String label) throws Exception {
        List<TestStep> steps = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            steps.add(request(label));
        }
        return script("p", null, steps);
    }

    /** Steps are numbered in order, as Tank does when it builds the script */
    static String script(String name, HDTestVariables variables, List<TestStep> steps) throws Exception {
        HDScriptUseCase useCase = new HDScriptUseCase();
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).setStepIndex(i);
            useCase.getScriptSteps().add(steps.get(i));
        }
        HDScript script = new HDScript();
        script.setLoop(1);
        script.getUseCase().add(useCase);
        HDScriptGroup group = new HDScriptGroup();
        group.setLoop(1);
        group.getGroupSteps().add(script);
        HDTestPlan plan = new HDTestPlan();
        plan.getGroup().add(group);
        HDWorkload workload = new HDWorkload();
        workload.setName(name);
        workload.setVariables(variables);
        workload.getPlans().add(plan);
        return JaxbUtil.marshall(workload);
    }
}
