package com.intuit.tank.harness.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.UUID;

import com.intuit.tank.harness.MockResponse;
import com.intuit.tank.harness.data.*;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.binary.BinaryRequest;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.MockRequest;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.logging.SourceType;

public class LogEventTest {
    
    private final UUID test_UUID = UUID.randomUUID();

    @Test
    public void testLogEvent_1() {

        LogEvent result = new LogEvent();

        assertNotNull(result);
        assertEquals("Other", result.buildMessage().get("EventType"));
        assertEquals("agent", result.buildMessage().get("SourceType"));
    }

    @Test
    public void testBuildMessage_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(null);
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(null);

        Map<String,String> result = fixture.buildMessage();
        assertEquals("Http", result.get("EventType"));
        assertEquals("agent", result.get("SourceType"));
    }

    @Test
    public void testBuildMessage_2() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(null);
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(null);
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        Map<String,String> result = fixture.buildMessage();
        assertEquals("Http", result.get("EventType"));
        assertEquals("agent", result.get("SourceType"));
    }

    @Test
    public void testBuildMessage_3() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        LogEventType type = LogEventType.Http;
        String message = "";

        Map<String,String> result = fixture.buildMessage(type, message);
        assertEquals("Http", result.get("EventType"));
        assertEquals("agent", result.get("SourceType"));
    }

    /**
     * Run String buildMessage() method test and validate password user variable field obfuscated
     */
    @Test
    public void testBuildMessage_4() {
        LogEvent fixture = new LogEvent();
        Variables variables = new Variables();
        variables.addVariable("pwd", "LoginPassword12");
        variables.addVariable("password", "LoginPassword123");
        variables.addVariable("iamTicket", "V1-97-X2jl3di6qb3rqf4smvwsac");

        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.USER_VARIABLE);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(variables);
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep((TestStep) null);
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript((HDScript) null);

        Map<String,String> result = fixture.buildMessage();
        assertNotNull(result);
        assertTrue(result.containsKey("UserVariables"));
        assertEquals(" password=********  iamTicket=********  pwd=******** ", result.get("UserVariables"));
    }

    /**
     * Run String buildMessage() method test and validate password header response obfuscated
     */
    @Test
    public void testBuildMessage_5() {
        LogEvent fixture = new LogEvent();
        MockRequest mockRequest = new MockRequest();
        MockResponse mockResponse = new MockResponse();
        mockResponse.setHeader("ticket", "V1-12-D0bqsj12aaaa0aaa49aaaa");
        mockRequest.setResponse(mockResponse);

        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.VERBOSE);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(null);
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(null);
        fixture.setRequest(mockRequest);

        Map<String,String> result = fixture.buildMessage();
        assertNotNull(result);
        assertTrue(result.containsKey("HttpResponseHeaders"));
        assertEquals(" ticket : ******** ", result.get("HttpResponseHeaders"));
    }

    /**
     * Run String buildMessage() method test and validate ssn and birthDate user variable field obfuscated
     */
    @Test
    public void testBuildMessage_6() {
        LogEvent fixture = new LogEvent();
        Variables variables = new Variables();
        variables.addVariable("ssn", "000-00-0000");
        variables.addVariable("birthDate", "1965-06-07");

        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.USER_VARIABLE);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(variables);
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep((TestStep) null);
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript((HDScript) null);

        Map<String,String> result = fixture.buildMessage();
        assertNotNull(result);
        assertTrue(result.containsKey("UserVariables"));
        assertEquals(" birthDate=********  ssn=******** ", result.get("UserVariables"));
    }

    /**
     * Testing getValidationCriteria() through buildMessage()
     */
    @Test
    public void testBuildMessage_7() {
        LogEvent fixture = new LogEvent();
        RequestStep step = new RequestStep();
        step.setRequest(new HDRequest());
        HDResponse response = new HDResponse();
        step.setResponse(new HDResponse());
        step.setComments("");
        step.setName("");
        step.setStepIndex(1);
        step.setOnFail("");
        step.setScriptGroupName("");
        step.setParent(new HDScriptUseCase());
        fixture.setStep(step);
        LogEventType type = LogEventType.Http;
        String message = "";

        Map<String,String> result = fixture.buildMessage(type, message);
        assertEquals("Http", result.get("EventType"));
        assertEquals("agent", result.get("SourceType"));
    }

    /**
     * Testing getBody() and truncateBody() through buildMessage()
     */
    @Test
    public void testBuildMessage_8() {
        LogEvent fixture = new LogEvent();
        BaseRequest request = new MockRequest();
        BaseResponse response = new MockResponse();
        response.setResponseBody("testBody");
        response.setHeader("Content-Type", "text/html");
        request.setResponse(response);
        fixture.setRequest(request);

        LogEventType type = LogEventType.Http;
        String message = "";

        Map<String,String> result = fixture.buildMessage(type, message);
        assertEquals("Http", result.get("EventType"));
        assertEquals("agent", result.get("SourceType"));

        response.setHeader("Content-Type", "WrongMimeType");
        request.setResponse(response);
        fixture.setRequest(request);

        result = fixture.buildMessage(type, message);
        assertEquals("Http", result.get("EventType"));
        assertEquals("agent", result.get("SourceType"));

        String maxBodyPlus = "test" +"largerBody".repeat(500);
        response.setResponseBody(maxBodyPlus);
        response.setHeader("Content-Type", "text/html");
        request.setResponse(response);
        fixture.setRequest(request);

        result = fixture.buildMessage(type, message);
        assertEquals("Http", result.get("EventType"));
        assertEquals("agent", result.get("SourceType"));
    }

    @Test
    public void testGetActiveProfile_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(null);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        LoggingProfile result = fixture.getActiveProfile();
        assertEquals(LoggingProfile.STANDARD, result);
    }

    @Test
    public void testGetActiveProfile_2() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        LoggingProfile result = fixture.getActiveProfile();
        assertEquals(LoggingProfile.STANDARD, result);
    }

    @Test
    public void testGetEventType_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        LogEventType result = fixture.getEventType();
        assertEquals(LogEventType.Http, result);
    }

    @Test
    public void testGetGroup_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        HDScriptGroup group = new HDScriptGroup();
        group.setName("testGroup");
        fixture.setGroup(group);
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        HDScriptGroup result = fixture.getGroup();
        assertEquals("testGroup", result.getName());
    }

    @Test
    public void testGetHostname_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("testHostname");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getHostname();
        assertEquals("testHostname", result);
    }

    @Test
    public void testGetInstanceId_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("testInstanceId");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getInstanceId();
        assertEquals("testInstanceId", result);
    }

    @Test
    public void testGetIteration_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("testIteration");
        fixture.setScript(new HDScript());

        String result = fixture.getIteration();
        assertEquals("testIteration", result);
    }

    @Test
    public void testGetJobId_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("testJobId");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getJobId();
        assertEquals("testJobId", result);
    }

    @Test
    public void testGetLoggingKey_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        fixture.setLoggingKey("testLoggingKey");

        String result = fixture.getLoggingKey();
        assertEquals("testLoggingKey", result);
    }

    @Test
    public void testGetMessage_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("testMessage");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getMessage();
        assertEquals("testMessage", result);
    }

    @Test
    public void testGetProjectName_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("testProject");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getProjectName();
        assertEquals("testProject", result);
    }

    @Test
    public void testGetPublicIp_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("testPublicIp");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getPublicIp();
        assertEquals("testPublicIp", result);
    }

    @Test
    public void testGetScript_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        HDScript script = new HDScript();
        script.setName("testScript");
        fixture.setScript(script);

        HDScript result = fixture.getScript();
        assertEquals("testScript", result.getName());
    }

    @Test
    public void testGetSourceType_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        SourceType result = fixture.getSourceType();
        assertEquals(SourceType.agent, result);
    }

    @Test
    public void testGetStepIndex_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("testStepIndex");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getStepIndex();
        assertEquals("testStepIndex", result);
    }

    @Test
    public void testGetTestPlan_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        HDTestPlan testPlan = new HDTestPlan();
        testPlan.setTestPlanName("testPlan");
        fixture.setTestPlan(testPlan);
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        HDTestPlan result = fixture.getTestPlan();
        assertEquals("testPlan", result.getTestPlanName());
    }

    @Test
    public void testGetThreadId_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("testThreadId");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getThreadId();
        assertEquals("testThreadId", result);
    }

    @Test
    public void testGetVariables_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        Variables testVariables = new Variables();
        testVariables.addVariable("testKey", "testValue");
        fixture.setVariables(testVariables);
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        Variables result = fixture.getVariables();
        assertEquals("testValue", result.getVariable("testKey"));
    }

    @Test
    public void testGetRequest_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        Variables testVariables = new Variables();
        testVariables.addVariable("testKey", "testValue");
        fixture.setVariables(testVariables);
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        BaseRequest request = new BinaryRequest(null, null);
        fixture.setRequest(request);

        BaseRequest result = fixture.getRequest();
        assertEquals(request, result);
    }

    @Test
    public void testSetRequest_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        BaseRequest request = new MockRequest();
        request.setBody("testBody");

        fixture.setRequest(request);
        assertEquals("testBody", request.getBody());
    }

    @Test
    public void testSetStep_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        TestStep step = new ClearCookiesStep();
        step.setStepIndex(4);
        fixture.setStep(step);

        assertEquals(4, step.getStepIndex());
    }

    @Test
    public void testSetStepGroupName_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String stepGroupName = "testStepGroupName";

        fixture.setStepGroupName(stepGroupName);
        assertEquals("testStepGroupName", fixture.getStepGroupName());
    }

    @Test
    public void testSetTransactionId_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        UUID transactionId = UUID.fromString("45c8356b-6ae8-408b-96f3-9038cc358439");
        fixture.setTransactionId(transactionId);

        assertEquals(transactionId, fixture.getTransactionId());
    }

    @Test
    public void testSetValidationStatus_1() {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan(new HDTestPlan());
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup(new HDScriptGroup());
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId(test_UUID);
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String validationStatus = "testValidationStatus";
        fixture.setValidationStatus(validationStatus);

        assertEquals("testValidationStatus", fixture.getValidationStatus());
    }
}
