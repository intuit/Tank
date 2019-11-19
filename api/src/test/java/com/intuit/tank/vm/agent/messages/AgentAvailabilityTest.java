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

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentAvailabilityTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshal() throws Exception {
        AgentAvailability data = new AgentAvailability("i-instanceID", "http://instanceUrl", 4000,
                AgentAvailabilityStatus.AVAILABLE);
        String marshall = JaxbUtil.marshall(data);
        System.out.println(marshall);
        assertNotNull(marshall);
        AgentAvailability unmarshalled = JaxbUtil.unmarshall(marshall, AgentAvailability.class);
        assertEquals(data, unmarshalled);
        assertTrue(EqualsBuilder.reflectionEquals(data, unmarshalled));
    }

}
