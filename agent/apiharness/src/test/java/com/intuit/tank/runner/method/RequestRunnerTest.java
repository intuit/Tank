package com.intuit.tank.runner.method;

import java.util.*;

import com.intuit.tank.harness.*;
import com.intuit.tank.harness.data.*;

import com.intuit.tank.harness.test.MockFlowController;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.json.JsonResponse;
import com.intuit.tank.http.keyvalue.KeyValueRequest;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.runner.TestHttpClient;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.vm.common.ValidationTypeConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class RequestRunnerTest {

    private RequestStep step;

    private Variables variables;

    private TimerMap timerMap;

    private TestPlanRunner testPlanRunner;

    private TestStepContext testStepContext;

    private HDRequest request;

    private MockHDResponse response;

    private MockHDValidation validation;

    private ValidationData validationData;

    private JsonResponse jsonResponse;

    @BeforeEach
    public void init() {
        step = new RequestStep();
        variables = new Variables();
        variables.addVariable("TANK_HTTP_PROXY", "https://localhost:8080");
        variables.addVariable("THREAD_ID", "443");
        variables.addVariable("testKey", "testValue");
        timerMap = new TimerMap();
        testPlanRunner = new TestPlanRunner(new HDTestPlan(),
                10, "com.intuit.tank.httpclient4.TankHttpClient4");

        request = new HDRequest();
        response = new MockHDResponse();

        request.setPostDatas(new ArrayList<>());
        request.setQueryString(new ArrayList<>());
        request.setRequestHeaders(new ArrayList<>());
        request.setMethod("TEST");
        request.setPort("8080");

        validationData = new ValidationData();
        validation = new MockHDValidation();
        validationData.setValue("testValue");
        validationData.setKey("testKey");
        validationData.setCondition("testCondition");

        APITestHarness instance = APITestHarness.getInstance();
        FlowController mockFlowController = new MockFlowController();
        instance.setFlowControllerTemplate(mockFlowController);
        instance.runConcurrentTestPlans();
    }

    @AfterEach
    public void cleanup() {
        APITestHarness.destroyCurrentInstance();
    }

    /**
     * Testing base execute with TEST method and POST_REQUEST phase
     * Also tests processValidations() via validation set up
     * Also tests validateHTTP(), and validateHeader()
     */
    @Test
    public void testRequestRunner_1(){
        validationData.setPhase(RequestDataPhase.POST_REQUEST);
        validationData.setCondition(ValidationTypeConstants.GREATER_THAN);
        validationData.setValue("300");
        validation.addHeaderValidation(validationData);
        validation.addCookieValidation(validationData);
        validation.addBodyValidation(validationData);
        response.setValidation(validation);

        validationData.setKey("HTTPRESPONSECODE");
        step.setRequest(request);
        step.setResponse(response);

        testStepContext = new TestStepContext(step, variables, "testPlanName",
                "testUniqueName", timerMap, testPlanRunner);
        testStepContext.setHttpClient(new TestHttpClient());

        RequestRunner requestRunner = new RequestRunner(testStepContext);
        assertNotNull(requestRunner);
        jsonResponse = new JsonResponse();
        jsonResponse.setHttpCode(200);
        jsonResponse.setHttpMessage("HTTPRESPONSECODE");
        jsonResponse.setCookie("HTTPRESPONSECODE", "200");
        requestRunner.setBaseResponse(jsonResponse);
        String result = requestRunner.execute();
        assertEquals("FAIL", result);
    }

    /**
     * Tests validateHeader()
     */
    @Test
    public void testRequestRunner_2(){
        validationData.setPhase(RequestDataPhase.POST_REQUEST);
        validationData.setCondition(ValidationTypeConstants.GREATER_THAN);
        validationData.setValue("300");
        validation.addHeaderValidation(validationData);
        validation.addCookieValidation(validationData);
        validation.addBodyValidation(validationData);
        response.setValidation(validation);

        step.setRequest(request);
        step.setResponse(response);

        testStepContext = new TestStepContext(step, variables, "testPlanName",
                "testUniqueName", timerMap, testPlanRunner);
        testStepContext.setHttpClient(new TestHttpClient());

        RequestRunner requestRunner = new RequestRunner(testStepContext);
        assertNotNull(requestRunner);
        jsonResponse = new JsonResponse();
        jsonResponse.setHttpCode(200);
        jsonResponse.setHeader("testKey", "200");
        jsonResponse.setCookie("testKey", "200");
        requestRunner.setBaseResponse(jsonResponse);
        String result = requestRunner.execute();
        assertEquals("FAIL", result);
    }

    /**
     * Testing checkPreValidations()
     */
    @Test
    public void testRequestRunner_3(){
        validationData.setPhase(RequestDataPhase.PRE_REQUEST);
        validationData.setKey("");
        validationData.setCondition(ValidationTypeConstants.EMPTY);
        validation.addCookieValidation(validationData);
        response.setValidation(validation);

        step.setRequest(request);
        step.setResponse(response);

        testStepContext = new TestStepContext(step, variables, "testPlanName",
                "testUniqueName", timerMap, testPlanRunner);
        testStepContext.setHttpClient(new TestHttpClient());

        RequestRunner requestRunner = new RequestRunner(testStepContext);
        assertNotNull(requestRunner);
        jsonResponse = new JsonResponse();
        jsonResponse.setHttpCode(200);
        requestRunner.setBaseResponse(jsonResponse);
        String result = requestRunner.execute();
        assertEquals("PASS", result);
    }

    /**
     * Testing processVariables()
     */
    @Test
    public void testRequestRunner_4(){
        MockHDAssignment assignment = new MockHDAssignment();
        AssignmentData assignmentData = new AssignmentData();
        assignmentData.setPhase(RequestDataPhase.POST_REQUEST);
        assignmentData.setKey("testKey");
        assignmentData.setValue("testValue");
        assignment.addHeaderVariable(assignmentData);
        assignment.addCookieVariable(assignmentData);
        assignment.addBodyVariable(assignmentData);
        response.setAssignment(assignment);

        request.setMethod("GET");
        step.setRequest(request);
        step.setResponse(response);

        testStepContext = new TestStepContext(step, variables, "testPlanName",
                "testUniqueName", timerMap, testPlanRunner);
        testStepContext.setHttpClient(new TestHttpClient());

        RequestRunner requestRunner = new RequestRunner(testStepContext);
        assertNotNull(requestRunner);
        jsonResponse = new JsonResponse();
        jsonResponse.setHttpCode(200);
        requestRunner.setBaseResponse(jsonResponse);
        String result = requestRunner.execute();
        assertEquals("PASS", result);
    }

    /**
     * Testing:
     *  - processQueryString()
     *  - populateHeaders()
     *  - populatePostData()
     *  - processHost()
     *  - processProtocol()
     */
    @Test
    public void testRequestRunner_5(){
        Header header = new Header();
        header.setAction("add");
        header.setKey("Content-Type");
        header.setValue("@text/html");
        List<Header> headers = new ArrayList<>();
        headers.add(header);
        request.setQueryString(headers);
        request.setRequestHeaders(headers);
        Header postDatasHeader = new Header();
        postDatasHeader.setKey("ns:");
        postDatasHeader.setValue("testValue");
        headers.add(postDatasHeader);
        request.setPostDatas(headers);
        request.setReqFormat("xml");
        request.setHost("@Variable");
        request.setProtocol("@Variable");
        step.setRequest(request);
        step.setResponse(response);

        testStepContext = new TestStepContext(step, variables, "testPlanName",
                "testUniqueName", timerMap, testPlanRunner);
        testStepContext.setHttpClient(new TestHttpClient());

        RequestRunner requestRunner = new RequestRunner(testStepContext);
        assertNotNull(requestRunner);
    }

	@Test
	public void equalsAnyBodyValidationPassesWhenFirstValueMatches() {
		String result = executeBodyValidation("state", ValidationTypeConstants.EQUALS_ANY,
				"input-required||completed", "{\"state\":\"input-required\"}");

		assertEquals("PASS", result);
	}

	@Test
	public void equalsAnyBodyValidationPassesWhenSecondValueMatches() {
		String result = executeBodyValidation("state", ValidationTypeConstants.EQUALS_ANY,
				"input-required||completed", "{\"state\":\"completed\"}");

		assertEquals("PASS", result);
	}

	@Test
	public void equalsAnyBodyValidationFailsWhenNeitherValueMatches() {
		String result = executeBodyValidation("state", ValidationTypeConstants.EQUALS_ANY,
				"input-required||completed", "{\"state\":\"working\"}");

		assertEquals("FAIL", result);
	}

	@Test
	public void equalsBodyValidationDoesNotTreatDelimiterAsOr() {
		String result = executeBodyValidation("state", ValidationTypeConstants.EQUALS,
				"input-required||completed", "{\"state\":\"input-required\"}");

		assertEquals("FAIL", result);
	}

	@Test
	public void lastSubstringBodyValidationWorksWithEqualsAny() {
		String result = executeBodyValidation("#function.string.lastsubstring.\"status\":{\"state\":\".\"",
				ValidationTypeConstants.EQUALS_ANY,
				"input-required||completed",
				"""
				data:{"jsonrpc":"2.0","result":{"status":{"state":"submitted"}}}

				data:{"jsonrpc":"2.0","result":{"status":{"state":"completed"},"final":true,"metadata":{}}}
				""");

		assertEquals("PASS", result);
	}

	@Test
	public void testRawResponseBodyFunctionValidation() {
		validationData.setPhase(RequestDataPhase.POST_REQUEST);
		validationData.setKey("#function.string.lastsubstring.\"status\":{\"state\":\".\"");
		validationData.setCondition(ValidationTypeConstants.EQUALS);
		validationData.setValue("input-required");
		validation.addBodyValidation(validationData);

		ValidationData finalValidation = new ValidationData();
		finalValidation.setPhase(RequestDataPhase.POST_REQUEST);
		finalValidation.setKey("#function.string.lastsubstring.\"final\":.,");
		finalValidation.setCondition(ValidationTypeConstants.EQUALS);
		finalValidation.setValue("true");
		validation.addBodyValidation(finalValidation);
		response.setValidation(validation);

		step.setRequest(request);
		step.setResponse(response);

		testStepContext = new TestStepContext(step, variables, "testPlanName",
				"testUniqueName", timerMap, testPlanRunner);
		testStepContext.setHttpClient(new TestHttpClient());

		RequestRunner requestRunner = new RequestRunner(testStepContext);
		jsonResponse = new JsonResponse();
		jsonResponse.setHttpCode(200);
		jsonResponse.setResponseBody("""
				data:{"jsonrpc":"2.0","result":{"status":{"state":"submitted"}}}

				data:{"jsonrpc":"2.0","result":{"status":{"state":"working"},"final":false}}

				data:{"jsonrpc":"2.0","result":{"status":{"state":"input-required"},"final":true,"metadata":{}}}
				""");
		requestRunner.setBaseResponse(jsonResponse);

		assertEquals("PASS", requestRunner.execute());

		validationData.setValue("completed");
		assertEquals("FAIL", requestRunner.execute());
	}

	@ParameterizedTest
	@ValueSource(strings = { "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS" })
	public void testExecuteSendsRequestWithMethod(String method) {
		request.setMethod(method);
		request.setProtocol("https");
		request.setHost("example.com");
		request.setPort("8443");
		request.setPath("/api");
		RecordingHttpClient client = new RecordingHttpClient();

		assertEquals("PASS", createRunner(client).execute());
		assertEquals(method, client.method);
		assertEquals("https://example.com:8443/api", client.request.getRequestUrl());
		assertSame(client.request, testStepContext.getRequest());
	}

	@Test
	public void testExecuteWithoutPortUsesProtocolDefault() {
		request.setMethod("GET");
		request.setProtocol("https");
		request.setHost("example.com");
		request.setPort(null);
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client).execute();
		assertEquals("https://example.com/", client.request.getRequestUrl());
	}

	@Test
	public void testUrlPartsFromFunctionsAndVariables() {
		variables.addVariable("apiPath", "/v1/items");
		request.setMethod("GET");
		request.setProtocol("#function.string.concat.https");
		request.setHost("#function.string.concat.localhost");
		request.setPort("#function.string.concat.8443");
		request.setPath("@apiPath");
		request.setQueryString(new ArrayList<>(List.of(
				header("add", "drop", "x"),
				header("set", "fn", "#function.string.concat.a.b"),
				header("add", "var", "@testKey"),
				header("remove", "drop", null))));
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client).execute();
		String url = client.request.getRequestUrl();
		assertTrue(url.startsWith("https://localhost:8443/v1/items?"), url);
		// the query parameters are kept in a hash map, so their order is not defined
		assertEquals(Set.of("fn=ab", "var=testValue"), Set.of(url.substring(url.indexOf('?') + 1).split("&")));
	}

	@Test
	public void testPathFromFunction() {
		request.setMethod("GET");
		request.setHost("localhost");
		request.setPath("#function.string.concat./status");
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client).execute();
		assertEquals("http://localhost:8080/status", client.request.getRequestUrl());
	}

	@Test
	public void testRequestHeaders() {
		request.setMethod("GET");
		request.setHost("localhost");
		request.setRequestHeaders(new ArrayList<>(List.of(
				header("add", "X-Fn", "#function.string.concat.v.1"),
				header("set", "X-Var", "@testKey"),
				header("add", "X-Removed", "x"),
				header("remove", "X-Removed", null),
				header("add", "Content-Type", "application/json; charset=UTF-8"))));
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client).execute();
		BaseRequest sent = client.request;
		assertEquals("v1", sent.getHeaderInformation().get("x-fn"));
		assertEquals("testValue", sent.getHeaderInformation().get("x-var"));
		assertFalse(sent.getHeaderInformation().containsKey("x-removed"));
		assertEquals("application/json", sent.getContentType());
		assertEquals("UTF-8", sent.getContentTypeCharSet());
	}

	@Test
	public void testPostDataFromFunctionsAndVariables() {
		request.setMethod("POST");
		request.setHost("localhost");
		request.setPostDatas(new ArrayList<>(List.of(
				header("add", "fn", "#function.string.concat.x.y"),
				header("set", "var", "@testKey"),
				header("remove", "ignored", null))));
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client).execute();
		KeyValueRequest sent = (KeyValueRequest) client.request;
		assertEquals("xy", sent.getKey("fn"));
		assertEquals("testValue", sent.getKey("var"));
	}

	@Test
	public void testPayloadIsSentForNonFormRequests() {
		request.setMethod("POST");
		request.setHost("localhost");
		request.setReqFormat("json");
		request.setPayload("{\"name\":\"tank\"}");
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client).execute();
		assertEquals("{\"name\":\"tank\"}", client.request.getBody());
	}

	@Test
	public void testProxyFromVariable() {
		variables.addVariable("TANK_HTTP_PROXY", "http://proxy.example.com:3128");
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client);
		assertEquals("proxy.example.com", client.proxyHost);
		assertEquals(3128, client.proxyPort);
	}

	@Test
	public void testProxyWithoutAPortUsesTheSchemeDefault() {
		RecordingHttpClient client = new RecordingHttpClient();

		variables.addVariable("TANK_HTTP_PROXY", "https://secure.example.com");
		createRunner(client);
		assertEquals("secure.example.com", client.proxyHost);
		assertEquals(443, client.proxyPort);

		variables.addVariable("TANK_HTTP_PROXY", "plain.example.com");
		createRunner(client);
		assertEquals("plain.example.com", client.proxyHost);
		assertEquals(80, client.proxyPort);
	}

	@Test
	public void testProxyWithTooManyPartsIsNotSet() {
		variables.addVariable("TANK_HTTP_PROXY", "proxy.example.com:3128:extra");
		RecordingHttpClient client = new RecordingHttpClient();

		createRunner(client);
		assertNull(client.proxyHost);
	}

	@Test
	public void testProxyErrorDoesNotFailTheRequest() {
		request.setMethod("GET");
		request.setHost("localhost");
		RecordingHttpClient client = new RecordingHttpClient();
		client.failOnProxy = true;

		assertEquals("PASS", createRunner(client).execute());
		assertEquals("GET", client.method);
	}

	@Test
	public void testClientErrorDoesNotFailTheRequest() {
		request.setMethod("GET");
		request.setHost("localhost");
		RecordingHttpClient client = new RecordingHttpClient();
		client.failOnSend = true;

		assertEquals("PASS", createRunner(client).execute());
		assertNotNull(testStepContext.getRequest());
	}

	@Test
	public void testExecuteWithTraceAndVerboseProfiles() {
		request.setMethod("GET");
		request.setHost("localhost");
		APITestHarness.getInstance().getAgentRunData().setActiveProfile(LoggingProfile.TRACE);
		RecordingHttpClient client = new RecordingHttpClient();
		assertEquals("PASS", createRunner(client).execute());
		assertEquals("GET", client.method);

		APITestHarness.getInstance().getAgentRunData().setActiveProfile(LoggingProfile.VERBOSE);
		client = new RecordingHttpClient();
		assertEquals("PASS", createRunner(client).execute());
		assertEquals("GET", client.method);
	}

	@Test
	public void testPreValidationSkipsRequestWithVerboseProfile() {
		APITestHarness.getInstance().getAgentRunData().setActiveProfile(LoggingProfile.VERBOSE);
		validation.addHeaderValidation(validation(RequestDataPhase.PRE_REQUEST, "@testKey", ValidationTypeConstants.EQUALS,
				"#function.string.concat.test.Value"));
		response.setValidation(validation);
		request.setMethod("GET");
		RecordingHttpClient client = new RecordingHttpClient();

		assertEquals("PASS", createRunner(client).execute());
		assertNull(client.method);
	}

	@Test
	public void testPreValidationWithFunctionKeyAndVariableValue() {
		variables.addVariable("expected", "ab");
		validation.addBodyValidation(validation(RequestDataPhase.PRE_REQUEST, "#function.string.concat.a.b",
				ValidationTypeConstants.EQUALS, "@expected"));
		response.setValidation(validation);
		request.setMethod("GET");
		RecordingHttpClient client = new RecordingHttpClient();

		assertEquals("PASS", createRunner(client).execute());
		// the pre validation matched, so the request is skipped
		assertNull(client.method);
	}

	@Test
	public void testPreValidationThatDoesNotMatchSendsRequest() {
		validation.addCookieValidation(validation(RequestDataPhase.PRE_REQUEST, "@testKey", ValidationTypeConstants.EQUALS,
				"other"));
		response.setValidation(validation);
		request.setMethod("GET");
		request.setHost("localhost");
		RecordingHttpClient client = new RecordingHttpClient();

		assertEquals("PASS", createRunner(client).execute());
		assertEquals("GET", client.method);
	}

	@Test
	public void testIoErrorFailsValidation() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Status", ValidationTypeConstants.EQUALS,
				"ok"));
		response.setValidation(validation);
		JsonResponse ioError = new JsonResponse();
		ioError.setHttpCode(-1);

		assertEquals("FAIL", execute(ioError));
	}

	@Test
	public void testHeaderValidationWithVariableAndFunctionValues() {
		variables.addVariable("expected", "200");
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Status", ValidationTypeConstants.EQUALS,
				"@expected"));
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Status", ValidationTypeConstants.EQUALS,
				"#function.string.concat.20.0"));
		response.setValidation(validation);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setHeader("Status", "200");

		assertEquals("PASS", execute(jsonResponse));
		assertTrue(testStepContext.getErrors().isEmpty());
	}

	@Test
	public void testComparingAMissingValueFailsValidation() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Missing", ValidationTypeConstants.LESS_THAN,
				"5"));
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Missing",
				ValidationTypeConstants.GREATER_THAN, "5"));
		response.setValidation(validation);

		assertEquals("FAIL", execute(okResponse()));
		assertEquals(2, testStepContext.getErrors().size());
	}

	@Test
	public void testHttpResponseMessageValidation() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "HTTPRESPONSEMESSAGE",
				ValidationTypeConstants.EQUALS, "OK"));
		response.setValidation(validation);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setHttpMessage("OK");

		assertEquals("PASS", execute(jsonResponse));
	}

	@Test
	public void testHttpResponseCodeValidationPasses() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "HTTPRESPONSECODE",
				ValidationTypeConstants.EQUALS, "200"));
		response.setValidation(validation);

		assertEquals("PASS", execute(okResponse()));
	}

	@Test
	public void testFailedValidationIsRecordedAsError() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Status", ValidationTypeConstants.EQUALS,
				"200"));
		response.setValidation(validation);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setHeader("Status", "500");

		assertEquals("FAIL", execute(jsonResponse));
		assertEquals(1, testStepContext.getErrors().size());
	}

	@Test
	public void testBodyValidationWithVariableAndFunctionValues() {
		variables.addVariable("expected", "tank");
		validation.addBodyValidation(validation(RequestDataPhase.POST_REQUEST, "name", ValidationTypeConstants.EQUALS,
				"@expected"));
		validation.addBodyValidation(validation(RequestDataPhase.POST_REQUEST, "name", ValidationTypeConstants.EQUALS,
				"#function.string.concat.ta.nk"));
		response.setValidation(validation);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setResponseBody("{\"name\":\"tank\"}");

		assertEquals("PASS", execute(jsonResponse));
	}

	@Test
	public void testCookieValidationWithVariableAndFunctionValues() {
		variables.addVariable("expected", "abc");
		validation.addCookieValidation(validation(RequestDataPhase.POST_REQUEST, "session", ValidationTypeConstants.EQUALS,
				"@expected"));
		validation.addCookieValidation(validation(RequestDataPhase.POST_REQUEST, "session", ValidationTypeConstants.EQUALS,
				"#function.string.concat.a.bc"));
		response.setValidation(validation);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setCookie("session", "abc");

		assertEquals("PASS", execute(jsonResponse));
	}

	@Test
	public void testCookieValidationFails() {
		validation.addCookieValidation(validation(RequestDataPhase.POST_REQUEST, "session", ValidationTypeConstants.EQUALS,
				"abc"));
		response.setValidation(validation);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setCookie("session", "xyz");

		assertEquals("FAIL", execute(jsonResponse));
	}

	@Test
	public void testValidationWithoutConditionFails() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Status", null, "200"));
		response.setValidation(validation);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setHeader("Status", "200");

		assertEquals("FAIL", execute(jsonResponse));
	}

	@Test
	public void testEmptyValidationPassesForMissingHeader() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Missing", ValidationTypeConstants.EMPTY,
				""));
		response.setValidation(validation);

		assertEquals("PASS", execute(okResponse()));
	}

	@Test
	public void testEqualsAnyValidationFailsForMissingHeader() {
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "Missing", ValidationTypeConstants.EQUALS_ANY,
				"a||b"));
		response.setValidation(validation);

		assertEquals("FAIL", execute(okResponse()));
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
			ValidationTypeConstants.NOTEMPTY + "|anything|working|PASS",
			ValidationTypeConstants.NOTEQUALS + "|completed|working|PASS",
			ValidationTypeConstants.NOTEQUALS + "|working|working|FAIL",
			ValidationTypeConstants.CONTAINS + "|ORK|working|PASS",
			ValidationTypeConstants.CONTAINS + "|done|working|FAIL",
			ValidationTypeConstants.DOESNOTCONTAIN + "|done|working|PASS",
			ValidationTypeConstants.DOESNOTCONTAIN + "|ork|working|FAIL",
			// numbers compare as numbers, everything else as text
			ValidationTypeConstants.LESS_THAN + "|10|5|PASS",
			ValidationTypeConstants.GREATER_THAN + "|10|5|FAIL",
			ValidationTypeConstants.LESS_THAN + "|b|a|PASS",
			ValidationTypeConstants.GREATER_THAN + "|b|a|FAIL" })
	public void testBodyValidationConditions(String condition, String expected, String actual, String result) {
		assertEquals(result, executeBodyValidation("state", condition, expected, "{\"state\":\"" + actual + "\"}"));
	}

	@Test
	public void testAssignmentsFromHeadersCookiesAndBody() {
		variables.addVariable("headerName", "X-Token");
		variables.addVariable("cookieName", "session");
		MockHDAssignment assignment = new MockHDAssignment();
		assignment.addHeaderVariable(assignment("fromHeaderVariable", "=@headerName"));
		assignment.addHeaderVariable(assignment("fromHeaderFunction", "#function.string.concat.X-Id"));
		assignment.addHeaderVariable(assignment("fromMissingHeader", "X-Missing"));
		assignment.addCookieVariable(assignment("fromCookieVariable", "@cookieName"));
		assignment.addCookieVariable(assignment("fromCookieFunction", "#function.string.concat.sess.ion"));
		assignment.addCookieVariable(assignment("fromMissingCookie", "missing"));
		assignment.addBodyVariable(assignment("fromBody", "=name"));
		assignment.addBodyVariable(assignment("fromBodyFunction", "#function.string.concat.fixed"));
		response.setAssignment(assignment);
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setHeader("X-Token", "token");
		jsonResponse.setHeader("X-Id", "42");
		jsonResponse.setCookie("session", "abc");
		jsonResponse.setResponseBody("{\"name\":\"tank\"}");

		assertEquals("PASS", execute(jsonResponse));
		assertEquals("token", variables.getVariable("fromHeaderVariable"));
		assertEquals("42", variables.getVariable("fromHeaderFunction"));
		assertEquals("", variables.getVariable("fromMissingHeader"));
		assertEquals("abc", variables.getVariable("fromCookieVariable"));
		assertEquals("abc", variables.getVariable("fromCookieFunction"));
		assertEquals("", variables.getVariable("fromMissingCookie"));
		assertEquals("tank", variables.getVariable("fromBody"));
		assertEquals("fixed", variables.getVariable("fromBodyFunction"));
		assertTrue(variables.variableExists("fromMissingHeader"));
	}

	@Test
	public void testBodyAssignmentWithoutValueIsEmpty() {
		MockHDAssignment assignment = new MockHDAssignment();
		assignment.addBodyVariable(assignment("fromBody", "name"));
		response.setAssignment(assignment);
		MockResponse mockResponse = new MockResponse();
		mockResponse.setHttpCode(200);

		execute(mockResponse);
		assertTrue(variables.variableExists("fromBody"));
		assertEquals("", variables.getVariable("fromBody"));
	}

	@Test
	public void testNoAssignmentsAfterIoError() {
		MockHDAssignment assignment = new MockHDAssignment();
		assignment.addBodyVariable(assignment("fromBody", "name"));
		response.setAssignment(assignment);
		JsonResponse ioError = new JsonResponse();
		ioError.setHttpCode(-1);

		execute(ioError);
		assertFalse(variables.variableExists("fromBody"));
	}

	@Test
	public void testTimingResultIsAddedToActiveTimer() {
		request.setLoggingKey("login");
		timerMap.start("timer");
		JsonResponse jsonResponse = okResponse();
		jsonResponse.setResponseTime(25);

		assertEquals("PASS", execute(jsonResponse));
		TankResult timer = timerMap.end("timer");
		assertEquals(200, timer.getStatusCode());
		assertEquals(25, timer.getResponseTime());
		assertFalse(timer.isError());
	}

	@Test
	public void testFailedRequestMarksTimerAsError() {
		timerMap.start("timer");
		validation.addHeaderValidation(validation(RequestDataPhase.POST_REQUEST, "HTTPRESPONSECODE",
				ValidationTypeConstants.EQUALS, "201"));
		response.setValidation(validation);

		assertEquals("FAIL", execute(okResponse()));
		assertTrue(timerMap.end("timer").isError());
	}

	/**
	 * Records the method and request of each call instead of sending it, and can fail on demand.
	 */
	private static class RecordingHttpClient extends TestHttpClient {
		private String method;
		private BaseRequest request;
		private String proxyHost;
		private int proxyPort;
		private boolean failOnProxy;
		private boolean failOnSend;

		private void record(String method, BaseRequest request) {
			if (failOnSend) {
				throw new IllegalStateException("connection refused");
			}
			this.method = method;
			this.request = request;
		}

		@Override
		public void doGet(BaseRequest request) {
			record("GET", request);
		}

		@Override
		public void doPost(BaseRequest request) {
			record("POST", request);
		}

		@Override
		public void doPut(BaseRequest request) {
			record("PUT", request);
		}

		@Override
		public void doPatch(BaseRequest request) {
			record("PATCH", request);
		}

		@Override
		public void doDelete(BaseRequest request) {
			record("DELETE", request);
		}

		@Override
		public void doOptions(BaseRequest request) {
			record("OPTIONS", request);
		}

		@Override
		public void setProxy(String proxyhost, int proxyport) {
			// the runner clears the proxy with a null host after building the request
			if (proxyhost != null) {
				if (failOnProxy) {
					throw new IllegalStateException("bad proxy");
				}
				this.proxyHost = proxyhost;
				this.proxyPort = proxyport;
			}
		}
	}

	private RequestRunner createRunner(TestHttpClient client) {
		step.setRequest(request);
		step.setResponse(response);
		testStepContext = new TestStepContext(step, variables, "testPlanName",
				"testUniqueName", timerMap, testPlanRunner);
		testStepContext.setHttpClient(client);
		return new RequestRunner(testStepContext);
	}

	private String execute(BaseResponse baseResponse) {
		RequestRunner requestRunner = createRunner(new TestHttpClient());
		requestRunner.setBaseResponse(baseResponse);
		return requestRunner.execute();
	}

	private static JsonResponse okResponse() {
		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setHttpCode(200);
		return jsonResponse;
	}

	private static Header header(String action, String key, String value) {
		Header header = new Header();
		header.setAction(action);
		header.setKey(key);
		header.setValue(value);
		return header;
	}

	private static ValidationData validation(RequestDataPhase phase, String key, String condition, String value) {
		ValidationData data = new ValidationData();
		data.setPhase(phase);
		data.setKey(key);
		data.setCondition(condition);
		data.setValue(value);
		return data;
	}

	private static AssignmentData assignment(String key, String value) {
		AssignmentData data = new AssignmentData();
		data.setPhase(RequestDataPhase.POST_REQUEST);
		data.setKey(key);
		data.setValue(value);
		return data;
	}

	private String executeBodyValidation(String key, String condition, String expectedValue, String responseBody) {
		ValidationData data = new ValidationData();
		data.setPhase(RequestDataPhase.POST_REQUEST);
		data.setKey(key);
		data.setCondition(condition);
		data.setValue(expectedValue);
		validation.addBodyValidation(data);
		response.setValidation(validation);

		step.setRequest(request);
		step.setResponse(response);

		testStepContext = new TestStepContext(step, variables, "testPlanName",
				"testUniqueName", timerMap, testPlanRunner);
		testStepContext.setHttpClient(new TestHttpClient());

		RequestRunner requestRunner = new RequestRunner(testStepContext);
		jsonResponse = new JsonResponse();
		jsonResponse.setHttpCode(200);
		jsonResponse.setResponseBody(responseBody);
		requestRunner.setBaseResponse(jsonResponse);

		return requestRunner.execute();
	}
}
