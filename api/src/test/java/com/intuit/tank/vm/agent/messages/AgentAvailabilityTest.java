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
import org.testng.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;
import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

public class AgentAvailabilityTest {

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testMarshal() throws Exception {
        AgentAvailability data = new AgentAvailability("i-instanceID", "http://instanceUrl", 4000,
                AgentAvailabilityStatus.AVAILABLE);
        String marshall = JaxbUtil.marshall(data);
        System.out.println(marshall);
        Assert.assertNotNull(marshall);
        AgentAvailability unmarshalled = JaxbUtil.unmarshall(marshall, AgentAvailability.class);
        Assert.assertEquals(data, unmarshalled);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(data, unmarshalled));
    }

}
