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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;
import com.intuit.tank.test.TestGroups;

public class StandaloneAgentTrackerTest {
    StandaloneAgentTracker tracker = new StandaloneAgentTracker();

    @Test(groups = TestGroups.FUNCTIONAL)
    public void addAvailability() {
        for (int i = 0; i < 4; i++) {
            AgentAvailability a = new AgentAvailability("instance-id-" + i, "url", 500,
                    AgentAvailabilityStatus.AVAILABLE);
            tracker.addAvailability(a);
            Assert.assertSame(a, tracker.getAvailabilityForAgent(a.getInstanceId()));
        }
        Assert.assertEquals(4, tracker.getAvailabilities().size());

        List<AgentAvailability> agents = tracker.getAgents(1000);
        Assert.assertEquals(2, agents.size());
        for (AgentAvailability a : agents) {
            Assert.assertEquals(AgentAvailabilityStatus.DELEGATING, a.getAvailabilityStatus());
        }
        int availableAgents = 0;
        for (AgentAvailability a : tracker.getAvailabilities()) {
            if (a.getAvailabilityStatus() == AgentAvailabilityStatus.AVAILABLE) {
                availableAgents++;
            }
        }
        Assert.assertEquals(2, availableAgents);

        agents = tracker.getAgents(2000);
        Assert.assertNull(agents);
    }

}
