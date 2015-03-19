package com.intuit.tank.harness.test.validation;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.testng.annotations.Test;

import com.intuit.tank.harness.test.validation.RegularExpressions;
import com.intuit.tank.test.TestGroups;

import junit.framework.TestCase;

public class RegularExpressionsTest {

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testMatch() {
        // 2009-02-26T12:44:02.053-08:00
        String regExp = "(20)\\d\\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])(T)((0)\\d|(1)\\d|(2)[0-4])[:][0-6]\\d[:][0-6]\\d\\.\\d\\d\\d[-]((0)\\d|(1)\\d|(2)[1-4])[:][0-6]\\d";

        TestCase.assertTrue(RegularExpressions.validFormat(regExp, "2009-02-26T12:44:02.053-08:00"));
    }
}
