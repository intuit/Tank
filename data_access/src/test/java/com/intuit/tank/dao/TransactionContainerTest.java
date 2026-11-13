/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionContainerTest {

    @AfterEach
    public void clearDialect() {
        System.clearProperty(TransactionContainer.DIALECT_PROPERTY);
    }

    @Test
    public void testPersistenceXmlDialectWithoutOverride() {
        assertEquals(Map.of(), TransactionContainer.persistenceOverrides());
        System.setProperty(TransactionContainer.DIALECT_PROPERTY, " ");
        assertEquals(Map.of(), TransactionContainer.persistenceOverrides());
    }

    @Test
    public void testDialectOverride() {
        System.setProperty(TransactionContainer.DIALECT_PROPERTY, "org.hibernate.dialect.H2Dialect");
        assertEquals(Map.of("hibernate.dialect", "org.hibernate.dialect.H2Dialect"),
                TransactionContainer.persistenceOverrides());
    }
}
