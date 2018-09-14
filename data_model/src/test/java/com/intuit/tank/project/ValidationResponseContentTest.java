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

import com.intuit.tank.project.ValidationResponseContent;
import com.intuit.tank.vm.api.enumerated.ValidationType;

/**
 * The class <code>ValidationResponseContentTest</code> contains tests for the class <code>{@link ValidationResponseContent}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ValidationResponseContentTest {
    /**
     * Run the ValidationType getOperator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetOperator_1()
        throws Exception {
        ValidationResponseContent fixture = new ValidationResponseContent();
        fixture.setOperator(ValidationType.contains);

        ValidationType result = fixture.getOperator();

        assertNotNull(result);
        assertEquals("Contains", result.getValue());
        assertEquals("Contains", result.getRepresentation());
        assertEquals("contains", result.name());
        assertEquals("contains", result.toString());
        assertEquals(4, result.ordinal());
    }

    /**
     * Run the void setOperator(ValidationType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetOperator_1()
        throws Exception {
        ValidationResponseContent fixture = new ValidationResponseContent();
        fixture.setOperator(ValidationType.contains);
        ValidationType operator = ValidationType.contains;

        fixture.setOperator(operator);

    }
}