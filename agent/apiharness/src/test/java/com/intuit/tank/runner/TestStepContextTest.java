package com.intuit.tank.runner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.binary.BinaryRequest;
import com.intuit.tank.http.binary.BinaryResponse;
import com.intuit.tank.runner.method.TimerMap;

/**
 * The class <code>TestStepContextTest</code> contains tests for the class <code>{@link TestStepContext}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class TestStepContextTest {
    /**
     * Run the TestStepContext(TestStep,Variables,String,String,TimerMap,TestPlanRunner) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testTestStepContext_1()
        throws Exception {
        TestStep testStep = new ClearCookiesStep();
        Variables variables = new Variables();
        String testPlanName = "";
        String uniqueName = "";
        TimerMap timerMap = new TimerMap();
        TestPlanRunner parent = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        parent.setHttpClient(null);

        TestStepContext result = new TestStepContext(testStep, variables, testPlanName, uniqueName, timerMap, parent);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        //       at com.intuit.tank.runner.TestPlanRunner.<clinit>(TestPlanRunner.java:44)
        assertNotNull(result);
    }

    /**
     * Run the void addError(ErrorContainer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testAddError_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));
        ErrorContainer error = new ErrorContainer("", new ValidationData(), new ValidationData(), "");

        fixture.addError(error);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }

    /**
     * Run the List<ErrorContainer> getErrors() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetErrors_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        List<ErrorContainer> result = fixture.getErrors();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }



    /**
     * Run the TestPlanRunner getParent() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetParent_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        TestPlanRunner result = fixture.getParent();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the BaseRequest getRequest() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetRequest_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        BaseRequest result = fixture.getRequest();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the BaseResponse getResponse() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetResponse_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        BaseResponse result = fixture.getResponse();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the String getResult() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetResult_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        String result = fixture.getResult();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the String getTestPlanName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetTestPlanName_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        String result = fixture.getTestPlanName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the TestStep getTestStep() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetTestStep_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        TestStep result = fixture.getTestStep();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the TimerMap getTimerMap() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetTimerMap_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        TimerMap result = fixture.getTimerMap();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the String getUniqueName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetUniqueName_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        String result = fixture.getUniqueName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the Variables getVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetVariables_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));

        Variables result = fixture.getVariables();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }


    /**
     * Run the void setRequest(BaseRequest) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetRequest_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));
        BaseRequest request = new BinaryRequest(null, null);

        fixture.setRequest(request);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }

    /**
     * Run the void setResponse(BaseResponse) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetResponse_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));
        BaseResponse response = new BinaryResponse();

        fixture.setResponse(response);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }

    /**
     * Run the void setResult(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetResult_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));
        String result = "";

        fixture.setResult(result);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }

    /**
     * Run the void setTestPlanName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetTestPlanName_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));
        String testPlanName = "";

        fixture.setTestPlanName(testPlanName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }

    /**
     * Run the void setTestStep(TestStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetTestStep_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));
        TestStep testStep = new ClearCookiesStep();

        fixture.setTestStep(testStep);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }

    /**
     * Run the void setVariables(Variables) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetVariables_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext fixture = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        fixture.setRequest(new BinaryRequest(null, null));
        fixture.setHttpClient(null);
        fixture.setResult("");
        fixture.setResponse(new BinaryResponse());
        fixture.addError(new ErrorContainer("", new ValidationData(), new ValidationData(), ""));
        Variables variables = new Variables();

        fixture.setVariables(variables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }
}