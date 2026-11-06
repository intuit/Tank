package com.intuit.tank.harness.functions;

import org.apache.logging.log4j.Level;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.test.data.Variables;

import static org.junit.jupiter.api.Assertions.*;

public class DateFunctionsTest {

    private Variables variables;

    @BeforeEach
    public void setUp() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        variables = new Variables();
    }

    @Test
    public void testIsValid() {
        // Generic Tests
        assertFalse(FunctionHandler.validFunction("#function.date"));
        assertFalse(FunctionHandler.validFunction("#function.date.bogusFunction"));

        // Current Date Tests
        assertTrue(FunctionHandler.validFunction("#function.date.currentDate"));
        assertTrue(FunctionHandler.validFunction("#function.date.currentDate.MMddyyyy"));

        // Add Days Tests
        assertFalse(FunctionHandler.validFunction("#function.date.addDays"));
        assertTrue(FunctionHandler.validFunction("#function.date.addDays.MMddyyyy"));
        assertTrue(FunctionHandler.validFunction("#function.date.addDays.14.MMddyyyy"));
    }

    @Test
    public void testGetCurrentDate() {
        String command = "#function.date.currentDate.MM-dd-yyyy";
        String date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);
    }

    @Test
    public void testAddDays() {
        String command = "#function.date.addDays.1.MM-dd-yyyy";
        String date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);

        command = "#function.date.addDays.50.MM-dd-yyyy";
        date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);

        command = "#function.date.addDays.-50.MM-dd-yyyy";
        date = FunctionHandler.executeFunction(command, variables);
        assertNotNull(date);
    }

    @Test
    public void testExecuteFunctionFail_1() {
        String command = "null";
        String date = FunctionHandler.executeFunction(command, variables);
        assertNull(date);
    }

    @Test
    public void testExecuteFunctionFail_2() {
       assertNull(DateFunctions.executeFunction(null));
    }

    @Test
    public void testIsValidUtcAndDateTime() {
        assertTrue(FunctionHandler.validFunction("#function.date.currentDateTime"));
        assertTrue(FunctionHandler.validFunction("#function.date.currentUTC"));
        assertTrue(FunctionHandler.validFunction("#function.date.currentUTC.ISO_UTC"));
        assertTrue(FunctionHandler.validFunction("#function.date.addDaysUTC.1.ISO_UTC"));
        assertFalse(FunctionHandler.validFunction("#function.date.addDaysUTC"));
    }

    @Test
    public void testExecuteFunctionUnknownReturnsNull() {
        assertNull(DateFunctions.executeFunction(new String[] { "#function", "date", "bogus", "x" }));
    }

    @Test
    public void testCurrentDateWithoutFormatUsesDefaultFormat() {
        assertFormattedNow(() -> DateFormat.getDateInstance().format(new Date()),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentdate", null }));
    }

    @Test
    public void testCurrentDateWithInvalidFormatUsesDefaultFormat() {
        assertFormattedNow(() -> DateFormat.getDateInstance().format(new Date()),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentdate", "qqq" }));
    }

    @Test
    public void testCurrentDateTimeDefaultsFormat() {
        assertFormattedNow(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentdatetime", "" }));
    }

    @Test
    public void testCurrentDateTimeWithFormat() {
        assertFormattedNow(() -> new SimpleDateFormat("HH:mm:ss").format(new Date()),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentDateTime", "HH:mm:ss" }));
    }

    @Test
    public void testCurrentUtcWithNamedFormat() {
        assertFormattedNow(() -> utcNow(0, "yyyy-MM-dd'T'HH:mm:ss'Z'"),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentUTC", "ISO_UTC_NO_MS" }));
    }

    @Test
    public void testCurrentUtcWithCustomFormat() {
        assertFormattedNow(() -> utcNow(0, "yyyy-MM-dd HH:mm:ss"),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentutc", "yyyy-MM-dd HH:mm:ss" }));
    }

    @Test
    public void testCurrentUtcWithoutFormatIsIsoInstant() {
        assertInstantNow(0, () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentutc", null }));
    }

    @Test
    public void testCurrentUtcWithInvalidFormatIsIsoInstant() {
        assertInstantNow(0, () -> DateFunctions.executeFunction(new String[] { "#function", "date", "currentutc", "yyyy#" }));
    }

    @Test
    public void testAddDaysWithFormat() {
        assertFormattedNow(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 2);
            return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        }, () -> DateFunctions.executeFunction(new String[] { "#function", "date", "adddays", "2", "yyyy-MM-dd" }));
    }

    @Test
    public void testAddDaysUtcWithNamedFormat() {
        assertFormattedNow(() -> utcNow(3, "yyyy-MM-dd"),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "addDaysUTC", "3", "ISO_UTC_DATE" }));
    }

    @Test
    public void testAddDaysUtcWithCustomFormat() {
        assertFormattedNow(() -> utcNow(-2, "yyyy-MM-dd HH:mm"),
                () -> DateFunctions.executeFunction(new String[] { "#function", "date", "adddaysutc", "-2", "yyyy-MM-dd HH:mm" }));
    }

    @Test
    public void testAddDaysUtcWithoutFormatIsIsoInstant() {
        assertInstantNow(-86400, () -> DateFunctions.executeFunction(new String[] { "#function", "date", "adddaysutc", "-1", null }));
    }

    @Test
    public void testAddDaysUtcWithInvalidFormatIsIsoInstant() {
        assertInstantNow(86400, () -> DateFunctions.executeFunction(new String[] { "#function", "date", "adddaysutc", "1", "yyyy#" }));
    }

    @Test
    public void testAddDaysUtcWithInvalidDaysReturnsNull() {
        assertNull(DateFunctions.executeFunction(new String[] { "#function", "date", "adddaysutc", "one", "ISO_UTC" }));
    }

    private static String utcNow(int plusDays, String pattern) {
        return ZonedDateTime.now(ZoneOffset.UTC).plusDays(plusDays).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Asserts the result matches the expected value computed just before or just after the call, so a clock tick
     * during the call does not fail the test.
     */
    static void assertFormattedNow(Supplier<String> expected, Supplier<String> call) {
        String before = expected.get();
        String result = call.get();
        String after = expected.get();
        assertTrue(result.equals(before) || result.equals(after),
                "expected " + before + " or " + after + " but was " + result);
    }

    /**
     * Asserts the result is an ISO instant between the times taken just before and just after the call, shifted by
     * the given number of seconds.
     */
    static void assertInstantNow(long offsetSeconds, Supplier<String> call) {
        Instant before = Instant.now().plusSeconds(offsetSeconds);
        Instant result = Instant.parse(call.get());
        Instant after = Instant.now().plusSeconds(offsetSeconds);
        assertFalse(result.isBefore(before), result + " is before " + before);
        assertFalse(result.isAfter(after), result + " is after " + after);
    }
}
