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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

public class AgentTestStartDataTest {

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testMarshal() throws Exception {
        AgentTestStartData agentTestStartData = new AgentTestStartData();
        agentTestStartData.setAgentInstanceNum(1);
        agentTestStartData.setJobId("TestJobId");
        agentTestStartData.setSimulationTime(50000);
        agentTestStartData.setRampTime(2000);
        agentTestStartData.setStartUsers(0);
        agentTestStartData.setConcurrentUsers(4000);
        agentTestStartData.setTotalAgents(2);
        agentTestStartData.setUserIntervalIncrement(1);
        String script = "TestScriptUrl_1.xml";
        agentTestStartData.setScriptUrl(script);
        DataFileRequest[] dfr = new DataFileRequest[2];
        dfr[0] = new DataFileRequest("dataFile_1.csv", true, "http://getDataFile_1.csv");
        dfr[1] = new DataFileRequest("dataFile_2.csv", false, "http://getDataFile_2.csv");
        agentTestStartData.setDataFiles(dfr);

        String marshalled = JaxbUtil.marshall(agentTestStartData);
        System.out.println(marshalled);
        Assert.assertNotNull(marshalled);
        AgentTestStartData unmarshalled = JaxbUtil.unmarshall(marshalled, AgentTestStartData.class);
        Assert.assertNotNull(unmarshalled);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(unmarshalled, agentTestStartData, new String[] { "dataFile",
                "scriptUrl" }));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(unmarshalled.getDataFiles()[0],
                agentTestStartData.getDataFiles()[0]));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(unmarshalled.getDataFiles()[1],
                agentTestStartData.getDataFiles()[1]));
        Assert.assertEquals(unmarshalled.getScriptUrl(), script);

    }

}
