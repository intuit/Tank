package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for AgentDebuggerFrame.resolveConnectionId().
 * P1 #42: Debugger variable connectionId resolution.
 */
public class ResolveConnectionIdTest {

    @Test
    @DisplayName("Literal connectionId is returned unchanged")
    public void testLiteralConnectionId() {
        Map<String, String> variables = new HashMap<>();
        variables.put("connectionId", "echo-123");

        assertEquals("echo", AgentDebuggerFrame.resolveConnectionId("echo", variables));
    }

    @Test
    @DisplayName("Variable template is resolved from variables map")
    public void testVariableConnectionId() {
        Map<String, String> variables = new HashMap<>();
        variables.put("connectionId", "echo-123");

        assertEquals("echo-123", AgentDebuggerFrame.resolveConnectionId("#{connectionId}", variables));
    }

    @Test
    @DisplayName("Embedded variable template is resolved within string")
    public void testEmbeddedVariableConnectionId() {
        Map<String, String> variables = new HashMap<>();
        variables.put("userId", "42");

        assertEquals("conn-42", AgentDebuggerFrame.resolveConnectionId("conn-#{userId}", variables));
    }

    @Test
    @DisplayName("Unresolvable variable template is returned as-is")
    public void testUnresolvableVariable() {
        Map<String, String> variables = new HashMap<>();

        assertEquals("#{unknownVar}", AgentDebuggerFrame.resolveConnectionId("#{unknownVar}", variables));
    }

    @Test
    @DisplayName("Null connectionId returns null")
    public void testNullConnectionId() {
        Map<String, String> variables = new HashMap<>();
        assertNull(AgentDebuggerFrame.resolveConnectionId(null, variables));
    }

    @Test
    @DisplayName("Null variables map returns connectionId unchanged")
    public void testNullVariables() {
        assertEquals("#{connectionId}", AgentDebuggerFrame.resolveConnectionId("#{connectionId}", null));
    }

    @Test
    @DisplayName("Multiple variables in connectionId are all resolved")
    public void testMultipleVariables() {
        Map<String, String> variables = new HashMap<>();
        variables.put("env", "prod");
        variables.put("svc", "chat");

        assertEquals("prod-chat", AgentDebuggerFrame.resolveConnectionId("#{env}-#{svc}", variables));
    }
}
