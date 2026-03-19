package com.intuit.tank.vm.vmManager.models;

/*
 * #%L
 * Cloud Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>VMStatusTest</code> contains tests for the class <code>{@link VMStatus}</code>.
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
        assertEquals(VMStatus.ready, VMStatus.fromString("ready"));
        assertEquals(VMStatus.rebooting, VMStatus.fromString("rebooting"));
        assertEquals(VMStatus.terminated, VMStatus.fromString("terminated"));
        assertEquals(VMStatus.stopped, VMStatus.fromString("stopped"));
        assertEquals(VMStatus.stopping, VMStatus.fromString("stopping"));
        assertEquals(VMStatus.rampPaused, VMStatus.fromString("rampPaused"));
    }

    @Test
    @DisplayName("fromString returns replaced for 'replaced' value")
    public void testFromString_replaced() {
        VMStatus result = VMStatus.fromString("replaced");
        
        assertNotNull(result);
        assertEquals(VMStatus.replaced, result);
    }

    @Test
    @DisplayName("fromString throws for invalid values (original behavior)")
    public void testFromString_invalidValueThrows() {
        // Original behavior: valueOf throws IllegalArgumentException for invalid values
        assertThrows(IllegalArgumentException.class, () -> VMStatus.fromString("garbage-value"));
    }

    @Test
    @DisplayName("fromString throws for null input (original behavior)")
    public void testFromString_nullThrows() {
        // Original behavior: valueOf throws NullPointerException for null
        assertThrows(NullPointerException.class, () -> VMStatus.fromString(null));
    }

    @Test
    @DisplayName("replaced is a terminal state (for documentation)")
    public void testReplaced_isTerminalState() {
        // This test documents that 'replaced' is intended as a terminal state
        // like 'terminated', used by AgentWatchdog when replacing failed agents
        assertNotNull(VMStatus.replaced);
        assertEquals("replaced", VMStatus.replaced.name());
    }
}