package com.intuit.tank.util;

import org.apache.logging.log4j.Level;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import org.junit.jupiter.api.BeforeEach;

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestParamUtilTest {
    private static final long TEST_VALUE = 30000L;

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("1.5ET", 45000L ),
                Arguments.of( ".5ET", 15000L ),
                Arguments.of( "3 * ET + 10", 90010L ),
                Arguments.of( "3ET + 10", 90010L ),
                Arguments.of( "10", 10L ),
                Arguments.of( "4 * RT / 1000", 120L ),
                Arguments.of( "100 * RT / 1000", 3000L ),
                Arguments.of( "10h 3m 4S", 36184000L ),
                Arguments.of( "(3ET + 2RT) / 2", 75000L ),
                Arguments.of( "(ET3 + RT2) / ST", 5L ),
                Arguments.of( "(3ET + RT2) / (2 * ST)", 3L ),
                Arguments.of( "(3ET + RT2) / (2 * 2ST)", 1L )
        );
    }

    static Stream<Arguments> invalidData() {
        return Stream.of(
                Arguments.of( "3 * XT + 10" ),
                Arguments.of( "" ),
                Arguments.of( "10X" ),
                Arguments.of( "((3ET + 2RT) / 2" ),
                Arguments.of( "22% * 5" )
        );
    }

    @BeforeEach
    public void before() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void testEvaluateExpression(String expression, long expectedResult) {
        assertEquals(expectedResult,
                TestParamUtil.evaluateExpression(expression, TEST_VALUE, TEST_VALUE, TEST_VALUE));
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void testValidExpression(String expression, long result) {
        assertTrue(TestParamUtil.isValidExpression(expression));
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("invalidData")
    public void testInvalidExpression(String expression) {
        assertFalse(TestParamUtil.isValidExpression(expression));
    }

}
