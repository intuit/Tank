package com.intuit.tank.vm.perfManager;

/*
 * #%L
 * Intuit Tank Api
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

import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class StandaloneAgentTrackerTest {
    StandaloneAgentTracker tracker = new StandaloneAgentTracker();

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void addAvailability() {
        for (int i = 0; i < 4; i++) {
            AgentAvailability a = new AgentAvailability("instance-id-" + i, "url", 500,
                    AgentAvailabilityStatus.AVAILABLE);
            tracker.addAvailability(a);
            assertSame(a, tracker.getAvailabilityForAgent(a.getInstanceId()));
        }
        assertEquals(4, tracker.getAvailabilities().size());

        List<AgentAvailability> agents = tracker.getAgents(1000);
        assertEquals(2, agents.size());
        for (AgentAvailability a : agents) {
            assertEquals(AgentAvailabilityStatus.DELEGATING, a.getAvailabilityStatus());
        }
        int availableAgents = 0;
        for (AgentAvailability a : tracker.getAvailabilities()) {
            if (a.getAvailabilityStatus() == AgentAvailabilityStatus.AVAILABLE) {
                availableAgents++;
            }
        }
        assertEquals(2, availableAgents);

        agents = tracker.getAgents(2000);
        assertNull(agents);
    }

}
