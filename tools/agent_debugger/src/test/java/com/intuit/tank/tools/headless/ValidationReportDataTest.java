package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.tools.debugger.DebugStep;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

public class ValidationReportDataTest {

    @TempDir
    Path tempDir;

    @Test
    public void successfulRequestIsRecordedWithRequestResponseAndChangedVariables() throws Exception {
        ValidationReportData report = new ValidationReportData("project p (id7)", 7);
        DebugStep step = requestStep(4, "GET", "#{host}/start");
        step.setRequest(request("http://h/start", "text/plain", Map.of("Accept", "*/*"), "req"));
        step.setResponse(response(200, "OK", 12L, Map.of("Server", "x"), "resp"));
        step.setEntryVariables(Map.of("same", "1", "changed", "a"));
        step.setExitVariables(Map.of("same", "1", "changed", "b", "added", "c"));

        report.recordStep(step, true, List.of());
        report.setFinalMetadata("auth", "user", "srs", true, false);
        JsonNode json = write(report);

        JsonNode summary = json.path("summary");
        assertEquals("project p (id7)", summary.path("workloadName").asString());
        assertEquals(7, summary.path("projectId").asInt());
        assertEquals("auth", summary.path("authId").asString());
        assertEquals("user", summary.path("username").asString());
        assertEquals("srs", summary.path("srsVersion").asString());
        assertTrue(summary.path("filingStatus").asBoolean());
        assertFalse(summary.path("taxhubFlow").asBoolean());
        assertEquals(1, summary.path("totalSteps").asInt());
        assertEquals(1, summary.path("executedCount").asInt());
        assertEquals(1, summary.path("successCount").asInt());
        assertEquals(100, summary.path("executionPercentage").asInt());
        assertTrue(summary.has("startTime"));
        assertTrue(summary.has("endTime"));

        JsonNode recorded = json.path("steps").get(0);
        assertEquals(5, recorded.path("stepIndex").asInt());
        assertEquals("#{host}/start", recorded.path("info").asString());
        assertEquals("REQUEST", recorded.path("stepType").asString());
        assertTrue(recorded.path("success").asBoolean());
        assertFalse(recorded.path("skipped").asBoolean());
        assertEquals("GET", recorded.path("request").path("method").asString());
        assertEquals("http://h/start", recorded.path("request").path("url").asString());
        assertEquals("text/plain", recorded.path("request").path("contentType").asString());
        assertEquals("*/*", recorded.path("request").path("headers").path("Accept").asString());
        assertEquals("req", recorded.path("request").path("body").asString());
        assertEquals(200, recorded.path("response").path("httpCode").asInt());
        assertEquals("OK", recorded.path("response").path("httpMessage").asString());
        assertEquals(12, recorded.path("response").path("responseTime").asLong());
        assertEquals("x", recorded.path("response").path("headers").path("Server").asString());
        assertEquals("resp", recorded.path("response").path("body").asString());

        // only the variables whose values changed are reported
        JsonNode changes = recorded.path("variableChanges");
        assertEquals(2, changes.size());
        assertEquals("a", changes.path("changed").path("before").asString());
        assertEquals("b", changes.path("changed").path("after").asString());
        assertFalse(changes.path("added").has("before"));
        assertEquals("c", changes.path("added").path("after").asString());
        assertFalse(recorded.has("errors"));
    }

    @Test
    public void failedRequestRecordsEveryValidationError() throws Exception {
        ValidationReportData report = new ValidationReportData("w", 1);
        DebugStep step = requestStep(0, "POST", "/save");
        step.setRequest(request("http://h/save", null, null, null));
        step.setResponse(response(500, "Server Error", 3L, null, null));
        ValidationData original = validation("status", "=", "#{expected}", RequestDataPhase.PRE_REQUEST);
        ValidationData interpreted = validation("status", "=", "200", RequestDataPhase.PRE_REQUEST);
        ErrorContainer full = new ErrorContainer("response", original, interpreted, "status was 500");
        ErrorContainer bare = new ErrorContainer("header", null, null, "missing");

        report.recordStep(step, false, List.of(full, bare));
        report.setFinalMetadata(null, null, null, false, true);
        JsonNode json = write(report);

        assertEquals(1, json.path("summary").path("failureCount").asInt());
        assertEquals(0, json.path("summary").path("successCount").asInt());
        assertTrue(json.path("summary").path("taxhubFlow").asBoolean());
        JsonNode recorded = json.path("steps").get(0);
        assertFalse(recorded.path("success").asBoolean());
        assertFalse(recorded.path("request").has("headers"));
        assertFalse(recorded.path("response").has("headers"));
        JsonNode errors = recorded.path("errors");
        assertEquals(2, errors.size());
        assertEquals("response", errors.get(0).path("location").asString());
        assertEquals("Pre Request", errors.get(0).path("phase").asString());
        assertEquals("status = #{expected}", errors.get(0).path("rawValidation").asString());
        assertEquals("status = 200", errors.get(0).path("interpretedValidation").asString());
        assertEquals("status was 500", errors.get(0).path("message").asString());
        // a container without validations reports only its location and reason
        assertEquals("header", errors.get(1).path("location").asString());
        assertFalse(errors.get(1).has("phase"));
        assertFalse(errors.get(1).has("rawValidation"));
        assertFalse(errors.get(1).has("interpretedValidation"));
        assertEquals("missing", errors.get(1).path("message").asString());
    }

    @Test
    public void stepsWithoutARealRequestCountAsSkipped() throws Exception {
        ValidationReportData report = new ValidationReportData("w", 1);
        // no request at all
        DebugStep noRequest = new DebugStep(null);
        // a request whose method is the literal "null", as logged for skipped steps
        DebugStep nullMethod = requestStep(1, "null", "/x");
        nullMethod.setRequest(request("http://h/x", null, null, null));
        // unchanged variables are left out of the report
        nullMethod.setEntryVariables(Map.of("k", "v"));
        nullMethod.setExitVariables(Map.of("k", "v"));

        report.recordStep(noRequest, true, null);
        report.recordStep(nullMethod, true, List.of());
        report.setFinalMetadata("a", "u", "s", false, false);
        JsonNode json = write(report);

        assertEquals(2, json.path("summary").path("skippedCount").asInt());
        assertEquals(0, json.path("summary").path("executedCount").asInt());
        assertEquals(0, json.path("summary").path("executionPercentage").asInt());
        assertEquals(0, json.path("steps").get(0).path("stepIndex").asInt());
        assertFalse(json.path("steps").get(0).has("info"));
        assertFalse(json.path("steps").get(1).has("variableChanges"));
    }

    @Test
    public void skippedThinkSleepAndLogicStepsAreTypedAndCounted() throws Exception {
        ValidationReportData report = new ValidationReportData("w", 1);
        ThinkTimeStep think = new ThinkTimeStep();
        think.setStepIndex(0);
        think.setMinTime("1s");
        think.setMaxTime("2s");
        SleepTimeStep sleep = new SleepTimeStep();
        sleep.setStepIndex(1);
        sleep.setValue("500");
        LogicStep logic = new LogicStep();
        logic.setStepIndex(2);
        logic.setName("EFE Logic Step");

        report.recordSkippedStep(think);
        report.recordSkippedStep(sleep);
        report.recordSkippedStep(logic);
        report.setFinalMetadata("a", "u", "s", false, false);
        JsonNode json = write(report);

        assertEquals(3, json.path("summary").path("skippedCount").asInt());
        assertEquals(3, json.path("summary").path("totalSteps").asInt());
        JsonNode steps = json.path("steps");
        assertEquals("THINK_TIME", steps.get(0).path("stepType").asString());
        assertEquals("1s", steps.get(0).path("minTime").asString());
        assertEquals("2s", steps.get(0).path("maxTime").asString());
        assertEquals("ThinkTime(1s, 2s)", steps.get(0).path("info").asString());
        assertEquals("SLEEP_TIME", steps.get(1).path("stepType").asString());
        assertEquals("500", steps.get(1).path("sleepTime").asString());
        assertEquals(2, steps.get(1).path("stepIndex").asInt());
        assertEquals("LOGIC", steps.get(2).path("stepType").asString());
        assertEquals("Logic Step: EFE Logic Step", steps.get(2).path("info").asString());
        for (JsonNode step : steps) {
            assertTrue(step.path("skipped").asBoolean());
            // success is null for steps that were not executed, so it is left out
            assertFalse(step.has("success"));
        }
    }

    @Test
    public void largeBodiesAreTruncated() throws Exception {
        ValidationReportData report = new ValidationReportData("w", 1);
        DebugStep step = requestStep(0, "GET", "/big");
        String exact = "a".repeat(10000);
        String tooBig = "b".repeat(10001);
        step.setRequest(request("http://h/big", null, null, exact));
        step.setResponse(response(200, "OK", 1L, null, tooBig));

        report.recordStep(step, true, List.of());
        JsonNode recorded = write(report).path("steps").get(0);

        assertEquals(exact, recorded.path("request").path("body").asString());
        assertEquals("b".repeat(10000) + "...[truncated]", recorded.path("response").path("body").asString());
    }

    @Test
    public void writeToMissingDirectoryLogsInsteadOfThrowing() {
        ValidationReportData report = new ValidationReportData("w", 1);
        File missing = tempDir.resolve("does/not/exist").toFile();

        report.writeToFile(missing);

        assertFalse(new File(missing, "validation-report.json").exists());
    }

    private JsonNode write(ValidationReportData report) {
        report.writeToFile(tempDir.toFile());
        File json = tempDir.resolve("validation-report.json").toFile();
        assertTrue(json.isFile());
        return JsonMapper.builder().build().readTree(json);
    }

    private static DebugStep requestStep(int index, String method, String label) {
        HDRequest request = new HDRequest();
        request.setMethod(method);
        request.setLabel(label);
        RequestStep step = new RequestStep();
        step.setRequest(request);
        step.setStepIndex(index);
        return new DebugStep(step);
    }

    private static BaseRequest request(String url, String contentType, Map<String, String> headers, String body) {
        BaseRequest request = mock(BaseRequest.class);
        when(request.getRequestUrl()).thenReturn(url);
        when(request.getContentType()).thenReturn(contentType);
        when(request.getHeaderInformation()).thenReturn(headers == null ? null : new HashMap<>(headers));
        when(request.getBody()).thenReturn(body);
        return request;
    }

    private static BaseResponse response(int code, String message, long time, Map<String, String> headers, String body) {
        BaseResponse response = mock(BaseResponse.class);
        when(response.getHttpCode()).thenReturn(code);
        when(response.getHttpMsg()).thenReturn(message);
        when(response.getResponseTime()).thenReturn(time);
        when(response.getHeaders()).thenReturn(headers);
        when(response.getBody()).thenReturn(body);
        return response;
    }

    private static ValidationData validation(String key, String condition, String value, RequestDataPhase phase) {
        ValidationData data = new ValidationData();
        data.setKey(key);
        data.setCondition(condition);
        data.setValue(value);
        data.setPhase(phase);
        return data;
    }

    @Test
    public void unchangedResultsKeepNullFieldsOutOfTheReport() {
        ValidationReportData report = new ValidationReportData(null, 0);
        report.writeToFile(tempDir.toFile());

        JsonNode json = JsonMapper.builder().build().readTree(tempDir.resolve("validation-report.json").toFile());
        assertFalse(json.path("summary").has("workloadName"));
        assertNull(json.path("steps").get(0));
    }
}
