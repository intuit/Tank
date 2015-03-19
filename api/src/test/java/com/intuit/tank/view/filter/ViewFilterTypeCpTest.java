package com.intuit.tank.view.filter;

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

import java.text.DateFormat;
import java.util.Date;

import org.junit.*;

import com.intuit.tank.view.filter.ViewFilterType;

import static org.junit.Assert.*;

/**
 * The class <code>ViewFilterTypeCpTest</code> contains tests for the class <code>{@link ViewFilterType}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class ViewFilterTypeCpTest {
    /**
     * Run the ViewFilterType(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testViewFilterType_1()
            throws Exception {
        String display = "";

        ViewFilterType result = ViewFilterType.LAST_60_DAYS;

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoSuchMethodException: com.intuit.tank.view.filter.ViewFilterType.<init>(java.lang.String)
        // at java.lang.Class.getConstructor0(Class.java:2810)
        // at java.lang.Class.getDeclaredConstructor(Class.java:2053)
        // at
        // com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.findConstructor(InstanceCreationExpression.java:572)
        // at
        // com.instantiations.eclipse.analysis.expression.model.InstanceCreationExpression.execute(InstanceCreationExpression.java:452)
        // at com.instantiations.assist.eclipse.junit.execution.core.ExecutionRequest.execute(ExecutionRequest.java:286)
        // at
        // com.instantiations.assist.eclipse.junit.execution.communication.LocalExecutionClient$1.run(LocalExecutionClient.java:158)
        // at java.lang.Thread.run(Thread.java:745)
        assertNotNull(result);
        assertEquals("Last 60 Days", result.getDisplay());
    }

    /**
     * Run the String getDisplay() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDisplay_1()
            throws Exception {
        ViewFilterType fixture = ViewFilterType.ALL;

        String result = fixture.getDisplay();

        assertEquals("All", result);
    }

    /**
     * Run the Date getViewFilterDate(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetViewFilterDate_1()
            throws Exception {
        ViewFilterType viewFilter = ViewFilterType.ALL;

        Date result = ViewFilterType.getViewFilterDate(viewFilter);

        assertNotNull(result);
    }

    /**
     * Run the Date getViewFilterDate(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetViewFilterDate_2()
            throws Exception {
        ViewFilterType viewFilter = ViewFilterType.ALL;

        Date result = ViewFilterType.getViewFilterDate(viewFilter);

        assertNotNull(result);
    }

    /**
     * Run the Date getViewFilterDate(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetViewFilterDate_3()
            throws Exception {
        ViewFilterType viewFilter = ViewFilterType.ALL;

        Date result = ViewFilterType.getViewFilterDate(viewFilter);

        assertNotNull(result);
    }

    /**
     * Run the Date getViewFilterDate(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetViewFilterDate_4()
            throws Exception {
        ViewFilterType viewFilter = ViewFilterType.ALL;

        Date result = ViewFilterType.getViewFilterDate(viewFilter);

        assertNotNull(result);
    }
}