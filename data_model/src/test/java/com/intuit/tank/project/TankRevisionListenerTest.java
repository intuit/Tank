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

import org.junit.jupiter.api.Test;

/**
 * The class <code>WatsRevisionListenerTest</code> contains tests for the class <code>{@link TankRevisionListener}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class TankRevisionListenerTest {
    /**
     * Run the void newRevision(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testNewRevision_1()
        throws Exception {
        TankRevisionListener fixture = new TankRevisionListener();
        Object revisionEntity = new TankRevisionInfo();

        fixture.newRevision(revisionEntity);

    }
}