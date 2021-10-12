package com.intuit.tank.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgentConstantsTest {
    @Test
    public void testConstants() {
        // for coverage
        AgentConstants agentConstants = new AgentConstants();
        assertEquals(AgentConstants.TOTAL_TIME_KEY, "_totalTime");
        assertEquals(AgentConstants.START_TIME_KEY, "_startTime");
    }
}

