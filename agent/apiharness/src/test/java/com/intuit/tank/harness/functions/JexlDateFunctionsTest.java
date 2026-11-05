package com.intuit.tank.harness.functions;

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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.jexl3.MapContext;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.intuit.tank.harness.functions.DateFunctionsTest.assertFormattedNow;
import static com.intuit.tank.harness.functions.DateFunctionsTest.assertInstantNow;
import static org.junit.jupiter.api.Assertions.*;

public class JexlDateFunctionsTest {

    Variables variables;

    @BeforeEach
    public void setUp() {
        variables = new Variables();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testGetCurrentDate() {
        String date = variables.evaluate("#{dateFunctions.currentDate('yyyy-MM-dd HH:mm:ssZ')}");
        assertNotNull(date);
        date = variables.evaluate("#{dateFunctions.currentDate('yyyy-MM-dd HH:mm:ssZ', 'PST')}");
        assertNotNull(date);
        date = variables.evaluate("#{dateFunctions.currentDate('yyyy-MM-dd HH:mm:ssZ', 'America/Los_Angeles')}");
        assertNotNull(date);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testCurrentTimeMilis() {
        long currentTime = System.currentTimeMillis();
        String time = variables.evaluate("#{dateFunctions.currentTimeMilis()}");
        assertTrue(currentTime <= Long.parseLong(time));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testAddDays() {

        String date = variables.evaluate("#{dateFunctions.addDays(1, '07-05-2012')}");
        assertNotNull(date);

        date = variables.evaluate("#{dateFunctions.addDays(5, '07-05-2012')}");
        assertNotNull(date);

        date = variables.evaluate("#{dateFunctions.addDays(-50, '07-05-2012')}");
        assertNotNull(date);
    }

    @Test
    public void testVisitRegistersDateFunctions() {
        JexlDateFunctions functions = new JexlDateFunctions();
        MapContext context = new MapContext();
        functions.visit(context);
        assertSame(functions, context.get("dateFunctions"));
    }

    @Test
    public void testCurrentDateInTimezone() {
        assertFormattedNow(() -> format("yyyy-MM-dd HH:mm", "Asia/Tokyo"),
                () -> new JexlDateFunctions().currentDate("yyyy-MM-dd HH:mm", "Asia/Tokyo"));
    }

    @Test
    public void testCurrentDateWithBlankTimezoneUsesDefault() {
        assertFormattedNow(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()),
                () -> new JexlDateFunctions().currentDate("yyyy-MM-dd HH:mm", " "));
    }

    @Test
    public void testCurrentDateWithInvalidFormatUsesDefaultFormat() {
        assertFormattedNow(() -> DateFormat.getDateInstance().format(new Date()),
                () -> new JexlDateFunctions().currentDate("qqq"));
    }

    @Test
    public void testCurrentDateTimeDefaultsFormat() {
        assertFormattedNow(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                () -> new JexlDateFunctions().currentDateTime(""));
    }

    @Test
    public void testCurrentDateTimeWithFormat() {
        assertFormattedNow(() -> new SimpleDateFormat("HH:mm:ss").format(new Date()),
                () -> variables.evaluate("#{dateFunctions.currentDateTime('HH:mm:ss')}"));
    }

    @Test
    public void testCurrentDateTimeInTimezone() {
        assertFormattedNow(() -> format("yyyy-MM-dd HH:mm:ss", "UTC"),
                () -> new JexlDateFunctions().currentDateTime(null, "UTC"));
        assertFormattedNow(() -> format("HH:mm:ss", "America/New_York"),
                () -> new JexlDateFunctions().currentDateTime("HH:mm:ss", "America/New_York"));
    }

    @Test
    public void testCurrentDateTimeWithBlankTimezoneUsesDefault() {
        assertFormattedNow(() -> new SimpleDateFormat("HH:mm:ss").format(new Date()),
                () -> new JexlDateFunctions().currentDateTime("HH:mm:ss", ""));
    }

    @Test
    public void testCurrentUtcWithNamedFormat() {
        assertFormattedNow(() -> ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                () -> new JexlDateFunctions().currentUTC("ISO_UTC_DATE"));
    }

    @Test
    public void testCurrentUtcWithCustomFormat() {
        assertFormattedNow(
                () -> ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                () -> new JexlDateFunctions().currentUTC("yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    public void testCurrentUtcWithoutFormatIsIsoInstant() {
        assertInstantNow(0, () -> new JexlDateFunctions().currentUTC(""));
        assertInstantNow(0, () -> new JexlDateFunctions().currentUTC());
    }

    @Test
    public void testCurrentUtcWithInvalidFormatIsIsoInstant() {
        assertInstantNow(0, () -> new JexlDateFunctions().currentUTC("yyyy#"));
    }

    private static String format(String pattern, String timezone) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        return formatter.format(new Date());
    }
}
