package com.intuit.tank.agent.models;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the agent-side VMStatus enum.
 * This enum is used by agents and doesn't include the 'replaced' status
 * (which is only meaningful on the controller side).
 */
public class VMStatusTest {

    @Test
    @DisplayName("fromString handles shutting-down special case")
    public void testFromString_shuttingDown() {
        VMStatus result = VMStatus.fromString("shutting-down");
        
        assertNotNull(result);
        assertEquals(VMStatus.shutting_down, result);
    }

    @Test
    @DisplayName("fromString returns correct enum for valid values")
    public void testFromString_validValues() {
        assertEquals(VMStatus.running, VMStatus.fromString("running"));
        assertEquals(VMStatus.pending, VMStatus.fromString("pending"));
        assertEquals(VMStatus.starting, VMStatus.fromString("starting"));
        assertEquals(VMStatus.rebooting, VMStatus.fromString("rebooting"));
        assertEquals(VMStatus.terminated, VMStatus.fromString("terminated"));
        assertEquals(VMStatus.stopped, VMStatus.fromString("stopped"));
        assertEquals(VMStatus.stopping, VMStatus.fromString("stopping"));
        assertEquals(VMStatus.rampPaused, VMStatus.fromString("rampPaused"));
    }

    @Test
    @DisplayName("fromString returns unknown for null input")
    public void testFromString_nullReturnsUnknown() {
        VMStatus result = VMStatus.fromString(null);
        
        assertNotNull(result);
        assertEquals(VMStatus.unknown, result);
    }

    @Test
    @DisplayName("fromString returns unknown for empty string")
    public void testFromString_emptyReturnsUnknown() {
        VMStatus result = VMStatus.fromString("");
        
        assertNotNull(result);
        assertEquals(VMStatus.unknown, result);
    }

    @Test
    @DisplayName("fromString returns unknown for unrecognized values")
    public void testFromString_unknownValueReturnsUnknown() {
        // Should not throw IllegalArgumentException - gracefully returns unknown
        VMStatus result = VMStatus.fromString("garbage-value");
        
        assertNotNull(result);
        assertEquals(VMStatus.unknown, result);
    }

    @Test
    @DisplayName("fromString handles 'replaced' from controller gracefully (returns unknown)")
    public void testFromString_replacedFromControllerReturnsUnknown() {
        // The agent-side VMStatus doesn't have a 'replaced' enum value
        // When the controller sends 'replaced', the agent should handle it gracefully
        VMStatus result = VMStatus.fromString("replaced");
        
        assertNotNull(result);
        assertEquals(VMStatus.unknown, result);
    }
}

