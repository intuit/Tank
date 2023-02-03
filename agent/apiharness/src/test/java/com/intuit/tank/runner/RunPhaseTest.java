package com.intuit.tank.runner;

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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RunPhaseTest {

    @Test
    public void testRunPhaseTest() {
        assertEquals("step", RunPhase.step.name());
        assertEquals("script", RunPhase.script.name());
        assertEquals("group", RunPhase.group.name());
        assertEquals("test", RunPhase.test.name());
    }
}
