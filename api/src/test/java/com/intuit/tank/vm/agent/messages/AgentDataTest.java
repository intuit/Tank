package com.intuit.tank.vm.agent.messages;

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

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AgentDataTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshal() throws Exception {
        AgentData agentData = new AgentData("1", "i-instanceID", "http://instanceUrl", 4000, VMRegion.US_EAST,
                "us-east-1c");
        String marshall = JaxbUtil.marshall(agentData);
        System.out.println(marshall);
        assertNotNull(marshall);
        AgentData unmarshalled = JaxbUtil.unmarshall(marshall, AgentData.class);
        assertEquals(agentData, unmarshalled);
    }

}
