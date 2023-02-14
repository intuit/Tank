package com.intuit.tank.runner.method;

import java.util.*;

import com.intuit.tank.harness.*;
import com.intuit.tank.harness.data.*;

import com.intuit.tank.harness.test.MockFlowController;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.json.JsonResponse;
import com.intuit.tank.runner.TestHttpClient;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.vm.common.ValidationTypeConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
