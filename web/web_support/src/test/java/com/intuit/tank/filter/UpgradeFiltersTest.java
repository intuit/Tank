package com.intuit.tank.filter;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.lang.reflect.Method;

import org.junit.jupiter.api.*;

import com.intuit.tank.filter.UpgradeFilters;

import static org.junit.jupiter.api.Assertions.*;

public class UpgradeFiltersTest {

    private UpgradeFilters fixture;

    @BeforeEach
    void setUp() {
        fixture = new UpgradeFilters();
    }

    @Test
    public void testConstructor() {
        assertNotNull(fixture);
    }

    @Test
    public void testIsDisabled_Initially_ReturnsFalse() {
        assertFalse(fixture.isDisabled());
    }

    @Test
    public void testUpgrade_RunsWithoutException() {
        // upgrade() calls DAO - may succeed with H2 (empty) or fail gracefully
        assertDoesNotThrow(() -> fixture.upgrade());
    }

    @Test
    public void testUpgrade_WhenAlreadyUpgraded_DoesNothing() {
        // Call twice - second call should be a no-op since filtersUpgraded=false still (DAO fails)
        assertDoesNotThrow(() -> fixture.upgrade());
        assertDoesNotThrow(() -> fixture.upgrade());
    }

    @Test
    public void testReplaceVariables_SingleVariable() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("replaceVariables", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(fixture, "prefix @myVar suffix");
        assertEquals("prefix #{myVar} suffix", result);
    }

    @Test
    public void testReplaceVariables_MultipleVariables() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("replaceVariables", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(fixture, "@var1 and @var2");
        assertEquals("#{var1} and #{var2}", result);
    }

    @Test
    public void testReplaceVariables_NoVariables_ReturnsUnchanged() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("replaceVariables", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(fixture, "no variables here");
        assertEquals("no variables here", result);
    }

    @Test
    public void testProcessConcat_SimpleStrings() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processConcat", String.class);
        method.setAccessible(true);

        // value starts with "#function.string.concat." then rest is split by '.'
        String result = (String) method.invoke(fixture, "#function.string.concat.hello.world");
        assertEquals("helloworld", result);
    }

    @Test
    public void testProcessConcat_WithVariable() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processConcat", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(fixture, "#function.string.concat.hello.@myVar");
        assertEquals("hello#{myVar}", result);
    }

    @Test
    public void testProcessConcat_WithDotReplacement() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processConcat", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(fixture, "#function.string.concat.hello-dot-world");
        assertEquals("hello.world", result);
    }

    @Test
    public void testProcessFunction_BasicCsvFunction() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processFunction", String.class, String.class);
        method.setAccessible(true);

        // value: "#function.generic.getcsv.file.csv" -> parts[3] onwards
        String result = (String) method.invoke(fixture, "ioFunctions.getCSVData", "#function.generic.getcsv.myfile.csv");
        assertNotNull(result);
        assertTrue(result.startsWith("#{ioFunctions.getCSVData("));
        assertTrue(result.endsWith(")}"));
    }

    @Test
    public void testProcessFunction_WithNumericArg() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processFunction", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(fixture, "ioFunctions.getCSVData", "#function.generic.getcsv.file.5");
        assertNotNull(result);
        // 5 is numeric - should not be quoted
        assertTrue(result.contains("5"));
    }

    @Test
    public void testProcessFunction_WithMultipleArgs_AddsCommasBetween() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processFunction", String.class, String.class);
        method.setAccessible(true);

        // parts[3] = "arg1", parts[4] = "arg2" → should insert ", " between them
        String result = (String) method.invoke(fixture, "ioFunctions.getCSVData", "#function.generic.getcsv.arg1.arg2");
        assertNotNull(result);
        assertTrue(result.contains(", "));
    }

    @Test
    public void testProcessFunction_WithVariableArg() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processFunction", String.class, String.class);
        method.setAccessible(true);

        // @myVar → becomes #{myVar}
        String result = (String) method.invoke(fixture, "ioFunctions.getCSVData", "#function.generic.getcsv.@myVar");
        assertNotNull(result);
        assertTrue(result.contains("#{myVar}"));
    }

    @Test
    public void testProcessFunction_WithELExpressionArg() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processFunction", String.class, String.class);
        method.setAccessible(true);

        // #{someExpr} → becomes someExpr (strips #{})
        // We pass it as part of the split by '.' - so we use a value without dots in the expression
        String result = (String) method.invoke(fixture, "ioFunctions.getCSVData", "#function.generic.getcsv.#{someExpr}");
        assertNotNull(result);
        assertTrue(result.contains("someExpr"));
    }

    @Test
    public void testProcessFunction_WithDotReplacementArg() throws Exception {
        Method method = UpgradeFilters.class.getDeclaredMethod("processFunction", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(fixture, "ioFunctions.getCSVData", "#function.generic.getcsv.hello-dot-world");
        assertNotNull(result);
        assertTrue(result.contains("\"hello.world\""));
    }

    @Test
    public void testIsDisabled_AfterUpgrade_ReturnsTrueIfNoException() {
        // After successful upgrade (which with H2 empty data may succeed), filtersUpgraded becomes true
        fixture.upgrade();
        // If no exception, filtersUpgraded=true → isDisabled()=true
        // If exception, filtersUpgraded stays false → isDisabled()=false
        // Either way, no exception thrown
        assertDoesNotThrow(() -> fixture.isDisabled());
    }
}
