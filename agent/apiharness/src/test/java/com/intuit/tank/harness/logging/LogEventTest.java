package com.intuit.tank.harness.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.harness.MockRequest;
import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptGroup;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.logging.SourceType;

/**
 * The class <code>LogEventTest</code> contains tests for the class <code>{@link LogEvent}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class LogEventTest {
    /**
     * Run the LogEvent() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testLogEvent_1()
            throws Exception {

        LogEvent result = new LogEvent();

        assertNotNull(result);
        assertEquals(" EventType=\"Other\"  SourceType=\"agent\" null", result.buildMessage());
        assertEquals(null, result.getThreadId());
        assertEquals(null, result.getGroup());
        assertEquals(null, result.getInstanceId());
        assertEquals(null, result.getVariables());
        assertEquals(null, result.getMessage());
        assertEquals(null, result.getScript());
        assertEquals(null, result.getProjectName());
        assertEquals(null, result.getRequest());
        assertEquals(null, result.getHostname());
        assertEquals(null, result.getValidationStatus());
        assertEquals(null, result.getStep());
        assertEquals(null, result.getLoggingKey());
        assertEquals(null, result.getJobId());
        assertEquals(null, result.getTestPlan());
        assertEquals(null, result.getStepGroupName());
        assertEquals(null, result.getTransactionId());
        assertEquals(null, result.getPublicIp());
        assertEquals(null, result.getStepIndex());
        assertEquals(null, result.getIteration());
    }

    /**
     * Run the String buildMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testBuildMessage_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep((TestStep) null);
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript((HDScript) null);

        String result = fixture.buildMessage();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String buildMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testBuildMessage_2()
            throws Exception {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile(LoggingProfile.STANDARD);
        fixture.setEventType(LogEventType.Http);
        fixture.setTestPlan((HDTestPlan) null);
        fixture.setValidationStatus("");
        fixture.setVariables(new Variables());
        fixture.setThreadId("");
        fixture.setPublicIp("");
        fixture.setProjectName("");
        fixture.setGroup((HDScriptGroup) null);
        fixture.setJobId("");
        fixture.setRequest(new MockRequest());
        fixture.setLoggingKey("");
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.buildMessage();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String buildMessage(LogEventType,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testBuildMessage_3()
            throws Exception {
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
        fixture.setTransactionId("");
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

        String result = fixture.buildMessage(type, message);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        // at com.intuit.tank.harness.test.data.Variables.<clinit>(Variables.java:36)
        assertNotNull(result);
    }

    /**
     * Run the LoggingProfile getActiveProfile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetActiveProfile_1()
            throws Exception {
        LogEvent fixture = new LogEvent();
        fixture.setHostname("");
        fixture.setActiveProfile((LoggingProfile) null);
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        LoggingProfile result = fixture.getActiveProfile();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the LoggingProfile getActiveProfile() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetActiveProfile_2()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        LoggingProfile result = fixture.getActiveProfile();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the LogEventType getEventType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetEventType_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        LogEventType result = fixture.getEventType();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the HDScriptGroup getGroup() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetGroup_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        HDScriptGroup result = fixture.getGroup();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getHostname() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetHostname_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getHostname();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getInstanceId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetInstanceId_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getInstanceId();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getIteration() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetIteration_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getIteration();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getJobId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetJobId_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getJobId();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getLoggingKey() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetLoggingKey_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        fixture.setLoggingKey("test");

        String result = fixture.getLoggingKey();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetMessage_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getMessage();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getProjectName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetProjectName_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getProjectName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getPublicIp() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetPublicIp_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getPublicIp();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the HDScript getScript() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetScript_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        HDScript result = fixture.getScript();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the SourceType getSourceType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetSourceType_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        SourceType result = fixture.getSourceType();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getStepIndex() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetStepIndex_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getStepIndex();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the HDTestPlan getTestPlan() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetTestPlan_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        HDTestPlan result = fixture.getTestPlan();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the String getThreadId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetThreadId_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        String result = fixture.getThreadId();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the Variables getVariables() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testGetVariables_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());

        Variables result = fixture.getVariables();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }

    /**
     * Run the void setActiveProfile(LoggingProfile) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetActiveProfile_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        LoggingProfile activeProfile = LoggingProfile.STANDARD;

        fixture.setActiveProfile(activeProfile);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setEventType(LogEventType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetEventType_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        LogEventType eventType = LogEventType.Http;

        fixture.setEventType(eventType);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setGroup(HDScriptGroup) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetGroup_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        HDScriptGroup group = new HDScriptGroup();

        fixture.setGroup(group);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setHostname(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetHostname_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String hostname = "";

        fixture.setHostname(hostname);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setInstanceId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetInstanceId_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String instanceId = "";

        fixture.setInstanceId(instanceId);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setIteration(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetIteration_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String iteration = "";

        fixture.setIteration(iteration);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setJobId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetJobId_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String jobId = "";

        fixture.setJobId(jobId);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setLoggingKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetLoggingKey_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String loggingKey = "";

        fixture.setLoggingKey(loggingKey);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setMessage(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetMessage_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String message = "";

        fixture.setMessage(message);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setProjectName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetProjectName_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String projectName = "";

        fixture.setProjectName(projectName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setPublicIp(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetPublicIp_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String publicIp = "";

        fixture.setPublicIp(publicIp);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setRequest(BaseRequest) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetRequest_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        BaseRequest request = new MockRequest();

        fixture.setRequest(request);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setScript(HDScript) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetScript_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        HDScript script = new HDScript();

        fixture.setScript(script);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setSourceType(SourceType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetSourceType_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        SourceType sourceType = SourceType.agent;

        fixture.setSourceType(sourceType);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setStep(TestStep) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetStep_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        TestStep step = new ClearCookiesStep();

        fixture.setStep(step);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setStepGroupName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetStepGroupName_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String stepGroupName = "";

        fixture.setStepGroupName(stepGroupName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setStepIndex(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetStepIndex_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String stepIndex = "";

        fixture.setStepIndex(stepIndex);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setTestPlan(HDTestPlan) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetTestPlan_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        HDTestPlan testPlan = new HDTestPlan();

        fixture.setTestPlan(testPlan);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setThreadId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetThreadId_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String threadId = "";

        fixture.setThreadId(threadId);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setTransactionId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetTransactionId_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String transactionId = "";

        fixture.setTransactionId(transactionId);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setValidationStatus(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetValidationStatus_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        String validationStatus = "";

        fixture.setValidationStatus(validationStatus);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }

    /**
     * Run the void setVariables(Variables) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 9:20 PM
     */
    @Test
    public void testSetVariables_1()
            throws Exception {
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
        fixture.setTransactionId("");
        fixture.setStepGroupName("");
        fixture.setSourceType(SourceType.agent);
        fixture.setInstanceId("");
        fixture.setStep(new ClearCookiesStep());
        fixture.setStepIndex("");
        fixture.setMessage("");
        fixture.setIteration("");
        fixture.setScript(new HDScript());
        Variables variables = new Variables();

        fixture.setVariables(variables);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
    }
}