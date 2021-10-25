package com.intuit.tank.api.model.v1.automation;

/*
 * #%L
 * Automation Rest Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;
import com.intuit.tank.vm.api.enumerated.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests marshalling of automationrequest object, specifically the variables. Maybe expand later.
 * 
 * @author wlee5
 */
public class AutomationRequestTest {

    private AutomationRequest req;

    @BeforeEach
    public void setUp() throws Exception {
        HashMap<String, String> vars = new HashMap<String, String>();
        vars.put("var1", "val1");
        vars.put("var2", "val2");
        List<Integer> filters = new ArrayList<>();
        filters.add(2);
        req = AutomationRequest.builder().withVariables(vars).
                withScriptName("test").withAddedFilterId(1).withStopBehavior(StopBehavior.END_OF_TEST)
                .withAddedDataFileId(1).withAddedExternalScriptId(1).withAddedFilterGroupId(1)
                .withAddedJobRegion(new AutomationJobRegion()).withProductName("testP")
                .withLocation(Location.unspecified).withRampTime("5").withSimulationTime("10")
                .withUserIntervalIncrement(10).withExternalScriptIds(filters).withFilterGroupIds(filters)
                .withJobRegions(new HashSet<AutomationJobRegion>())
                .withDataFileIds(filters).withName("test").withFilterIds(filters).build();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMarshall() throws JAXBException {
        String marshalled = JaxbUtil.marshall(req);
        System.out.println(marshalled);
        AutomationRequest unmarshalled = JaxbUtil.unmarshall(marshalled, AutomationRequest.class);
        assertEquals(unmarshalled, req);
        assertEquals(unmarshalled.getScriptName(),"test");
        assertEquals(unmarshalled.getStopBehavior(),StopBehavior.END_OF_TEST);
        assertEquals(unmarshalled.getName(),"test");
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testautomationRequestBuilderBase() {
        AutomationRequest.AutomationRequestBuilderBase automationRequestBuilderBase = new AutomationRequest.AutomationRequestBuilderBase();
        automationRequestBuilderBase.withName("test");
        assertEquals(automationRequestBuilderBase.getInstance().getName(),"test");
    }
}
