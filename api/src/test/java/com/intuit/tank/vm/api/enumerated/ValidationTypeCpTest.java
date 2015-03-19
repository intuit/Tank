package com.intuit.tank.vm.api.enumerated;

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

import org.junit.*;

import com.intuit.tank.vm.api.enumerated.ValidationType;

import static org.junit.Assert.*;

/**
 * The class <code>ValidationTypeCpTest</code> contains tests for the class <code>{@link ValidationType}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class ValidationTypeCpTest {
    /**
     * Run the String getRepresentation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRepresentation_1()
            throws Exception {
        ValidationType fixture = ValidationType.contains;

        String result = fixture.getRepresentation();

        assertEquals("Contains", result);
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_1()
            throws Exception {
        String value = "==";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_2()
            throws Exception {
        String value = "!=";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Not Equals", result.getRepresentation());
        assertEquals("!=", result.getValue());
        assertEquals("notequals", result.name());
        assertEquals("notequals", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_3()
            throws Exception {
        String value = "Empty";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Empty", result.getRepresentation());
        assertEquals("Empty", result.getValue());
        assertEquals("empty", result.name());
        assertEquals("empty", result.toString());
        assertEquals(2, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_4()
            throws Exception {
        String value = "Not empty";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Not empty", result.getRepresentation());
        assertEquals("Not empty", result.getValue());
        assertEquals("notempty", result.name());
        assertEquals("notempty", result.toString());
        assertEquals(3, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_5()
            throws Exception {
        String value = "Contains";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Contains", result.getRepresentation());
        assertEquals("Contains", result.getValue());
        assertEquals("contains", result.name());
        assertEquals("contains", result.toString());
        assertEquals(4, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_6()
            throws Exception {
        String value = "Does not contain";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Does not contain", result.getRepresentation());
        assertEquals("Does not contain", result.getValue());
        assertEquals("doesnotcontain", result.name());
        assertEquals("doesnotcontain", result.toString());
        assertEquals(5, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_7()
            throws Exception {
        String value = "Greater Than";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Greater Than", result.getRepresentation());
        assertEquals("Greater Than", result.getValue());
        assertEquals("greaterthan", result.name());
        assertEquals("greaterthan", result.toString());
        assertEquals(7, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_8()
            throws Exception {
        String value = "Less Than";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Less Than", result.getRepresentation());
        assertEquals("Less Than", result.getValue());
        assertEquals("lessthan", result.name());
        assertEquals("lessthan", result.toString());
        assertEquals(6, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationType(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationType_9()
            throws Exception {
        String value = "";

        ValidationType result = ValidationType.getValidationType(value);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_1()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_2()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_3()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_4()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_5()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_6()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_7()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_8()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_9()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ValidationType getValidationTypeFromRepresentation(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValidationTypeFromRepresentation_10()
            throws Exception {
        String representation = "";

        ValidationType result = ValidationType.getValidationTypeFromRepresentation(representation);

        assertNotNull(result);
        assertEquals("Equals", result.getRepresentation());
        assertEquals("==", result.getValue());
        assertEquals("equals", result.name());
        assertEquals("equals", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        ValidationType fixture = ValidationType.contains;

        String result = fixture.getValue();

        assertEquals("Contains", result);
    }
}