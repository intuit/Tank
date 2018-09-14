package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.TimingResult;

/**
 * The class <code>TimingResultTest</code> contains tests for the class <code>{@link TimingResult}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class TimingResultTest {
    /**
     * Run the TimingResult() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testTimingResult_1()
        throws Exception {

        TimingResult result = new TimingResult();

        assertNotNull(result);
        assertEquals(null, result.getCreator());
        assertEquals(0, result.getId());
        assertEquals(null, result.getModified());
        assertEquals(null, result.getCreated());
    }
}