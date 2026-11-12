package com.intuit.tank.vm.common.util;

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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * The class <code>ReportUtilCpTest</code> contains tests for the class <code>{@link ReportUtil}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class ReportUtilCpTest {
    /**
     * Run the ReportUtil() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testReportUtil_1()
            throws Exception {
        ReportUtil result = new ReportUtil();
        assertNotNull(result);
    }

    /**
     * Run the String getTimestamp(Date) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetTimestamp_1()
            throws Exception {
        Date date = new Date();

        String result = ReportUtil.getTimestamp(date);

    }
}