package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.intuit.tank.harness.data.AssertionBlock;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.SaveOccurrence;
import com.intuit.tank.harness.data.WebSocketAction;
import com.intuit.tank.harness.data.WebSocketAssertion;
import com.intuit.tank.harness.data.WebSocketRequest;
import com.intuit.tank.harness.data.WebSocketStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.httpclientjdk.MessageStream;
import com.intuit.tank.httpclientjdk.TankWebSocketClient;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

/**
 * Unit tests for WebSocketRunner assertion functionality
 */
public class WebSocketRunnerAssertionTest {

    private TestPlanRunner testPlanRunner;

    @BeforeEach
    public void setUp() {
        testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "test-http-client");
    }

    // ==================== ASSERT Action Tests ====================

    @Test
    @DisplayName("ASSERT should pass when pattern is found")
    public void testAssertPassWhenPatternFound() throws Exception {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("success")
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        // Mock client with MessageStream containing matching message
        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("{\"status\":\"success\"}");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
    }

    @Test
    @DisplayName("ASSERT should fail when pattern is not found")
    public void testAssertFailWhenPatternNotFound() throws Exception {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("notfound")
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("{\"status\":\"success\"}");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    @DisplayName("ASSERT should fail when connection not found")
    public void testAssertFailNoConnection() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("nonexistent");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder().pattern("test").build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    @DisplayName("ASSERT should fail when connection has failed")
    public void testAssertFailWhenConnectionFailed() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder().pattern("test").build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = mock(MessageStream.class);
        when(client.hasFailed()).thenReturn(true);
        when(client.getMessageStream()).thenReturn(stream);
        when(stream.getFailurePattern()).thenReturn("error");
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    @DisplayName("ASSERT should pass with no assertions defined")
    public void testAssertPassNoAssertions() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");
        // No assertions set

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        when(client.hasFailed()).thenReturn(false);
        when(client.getMessageStream()).thenReturn(new MessageStream("conn-1"));
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
    }

    @Test
    @DisplayName("ASSERT should fail when MessageStream is null")
    public void testAssertFailNullStream() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder().pattern("test").build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        when(client.hasFailed()).thenReturn(false);
        when(client.getMessageStream()).thenReturn(null);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    // ==================== minCount/maxCount Tests ====================

    @Test
    @DisplayName("ASSERT should pass when count meets minCount")
    public void testAssertMinCountPass() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("msg")
            .minCount(3)
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("msg1");
        stream.addMessage("msg2");
        stream.addMessage("msg3");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
    }

    @Test
    @DisplayName("ASSERT should fail when count below minCount")
    public void testAssertMinCountFail() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("msg")
            .minCount(5)
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("msg1");
        stream.addMessage("msg2");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    @DisplayName("ASSERT should fail when count exceeds maxCount")
    public void testAssertMaxCountFail() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("msg")
            .maxCount(2)
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("msg1");
        stream.addMessage("msg2");
        stream.addMessage("msg3");
        stream.addMessage("msg4");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    // ==================== Save Assertion Tests ====================

    @Test
    @DisplayName("Save assertion should extract and set variable (LAST)")
    public void testSaveAssertionLast() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getSaves().add(WebSocketAssertion.builder()
            .pattern("\"price\":(\\d+)")
            .regex(true)
            .variable("lastPrice")
            .occurrence(SaveOccurrence.LAST)
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("{\"price\":100}");
        stream.addMessage("{\"price\":200}");
        stream.addMessage("{\"price\":300}");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
        assertEquals("300", variables.getVariable("lastPrice"));
    }

    @Test
    @DisplayName("Save assertion should extract and set variable (FIRST)")
    public void testSaveAssertionFirst() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getSaves().add(WebSocketAssertion.builder()
            .pattern("\"id\":(\\d+)")
            .regex(true)
            .variable("firstId")
            .occurrence(SaveOccurrence.FIRST)
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("{\"id\":111}");
        stream.addMessage("{\"id\":222}");
        stream.addMessage("{\"id\":333}");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
        assertEquals("111", variables.getVariable("firstId"));
    }

    @Test
    @DisplayName("Save assertion with no variable name should be skipped")
    public void testSaveAssertionNoVariable() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        WebSocketAssertion save = new WebSocketAssertion();
        save.setPattern("test");
        // No variable set
        assertions.getSaves().add(save);
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("test message");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
    }

    @Test
    @DisplayName("Save assertion with no match should not set variable")
    public void testSaveAssertionNoMatch() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getSaves().add(WebSocketAssertion.builder()
            .pattern("\"notfound\":(\\d+)")
            .regex(true)
            .variable("myVar")
            .occurrence(SaveOccurrence.LAST)
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("{\"other\":123}");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
        assertEquals("", variables.getVariable("myVar")); // Variables returns empty string for unset variables
    }

    // ==================== Fail-Fast Tests ====================

    @Test
    @DisplayName("SEND should fail-fast when connection has failed")
    public void testSendFailFast() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.SEND);
        step.setConnectionId("conn-1");

        WebSocketRequest request = new WebSocketRequest();
        request.setPayload("test");
        step.setRequest(request);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = mock(MessageStream.class);
        when(client.hasFailed()).thenReturn(true);
        when(client.getMessageStream()).thenReturn(stream);
        when(stream.getFailurePattern()).thenReturn("error");
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
        // Verify sendMessage was never called
        verify(client, never()).sendMessage(anyString());
    }

    // ==================== DISCONNECT with Assertions ====================

    @Test
    @DisplayName("DISCONNECT should run assertions before closing")
    public void testDisconnectWithAssertions() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("Echo:")
            .minCount(2)
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("Echo: msg1");
        stream.addMessage("Echo: msg2");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
        verify(client).disconnect();
    }

    @Test
    @DisplayName("DISCONNECT should fail when assertion fails")
    public void testDisconnectAssertionFails() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("notfound")
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("something else");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
        verify(client).disconnect(); // Should still disconnect
    }

    @Test
    @DisplayName("DISCONNECT should fail when connection already failed")
    public void testDisconnectConnectionFailed() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("conn-1");

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = mock(MessageStream.class);
        when(client.hasFailed()).thenReturn(true);
        when(client.getMessageStream()).thenReturn(stream);
        when(stream.getFailurePattern()).thenReturn("error");
        when(stream.getSummary()).thenReturn("MessageStream[conn-1]: 5 messages, failed=true");
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
        verify(client).disconnect();
    }

    // ==================== Variable Substitution ====================

    @Test
    @DisplayName("Pattern should support variable substitution")
    public void testPatternVariableSubstitution() {
        WebSocketStep step = new WebSocketStep();
        step.setAction(WebSocketAction.ASSERT);
        step.setConnectionId("conn-1");

        AssertionBlock assertions = new AssertionBlock();
        assertions.getExpects().add(WebSocketAssertion.builder()
            .pattern("#{expectedPattern}")
            .build());
        step.setAssertions(assertions);

        Variables variables = new Variables();
        variables.addVariable("expectedPattern", "success", true);
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        MessageStream stream = new MessageStream("conn-1");
        stream.addMessage("{\"status\":\"success\"}");
        when(client.getMessageStream()).thenReturn(stream);
        when(client.hasFailed()).thenReturn(false);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
    }
}
