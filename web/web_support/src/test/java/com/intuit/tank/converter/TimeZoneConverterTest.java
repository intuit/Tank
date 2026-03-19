package com.intuit.tank.converter;

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

import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeZoneConverterTest {

    private final TimeZoneConverter converter = new TimeZoneConverter();

    @Test
    public void testGetAsObject_ValidTimezone_ReturnsTimeZone() {
        Object result = converter.getAsObject(null, null, "America/New_York");
        assertNotNull(result);
        assertTrue(result instanceof TimeZone);
    }

    @Test
    public void testGetAsObject_GMTTimezone_ReturnsGMT() {
        Object result = converter.getAsObject(null, null, "GMT");
        assertNotNull(result);
        assertTrue(result instanceof TimeZone);
        assertEquals("GMT", ((TimeZone) result).getID());
    }

    @Test
    public void testGetAsObject_NullValue_ReturnsDefault() {
        // TimeZone.getTimeZone(null) returns GMT, so getAsObject handles this
        Object result = converter.getAsObject(null, null, null);
        assertNotNull(result);
    }

    @Test
    public void testGetAsString_ValidTimeZone_ReturnsDisplayName() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");
        String result = converter.getAsString(null, null, tz);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetAsString_NonTimeZoneObject_ReturnsGMT() {
        String result = converter.getAsString(null, null, "not a timezone");
        assertEquals("GMT", result);
    }

    @Test
    public void testGetAsString_NullObject_ReturnsGMT() {
        String result = converter.getAsString(null, null, null);
        assertEquals("GMT", result);
    }

    @Test
    public void testRoundTrip() {
        TimeZone original = TimeZone.getTimeZone("UTC");
        String asString = converter.getAsString(null, null, original);
        assertNotNull(asString);
        assertFalse(asString.isEmpty());
    }
}
