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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstantConverterTest {

    private InstantConverter converter;

    @Mock
    private FacesContext facesContext;

    @Mock
    private UIComponent component;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = org.mockito.MockitoAnnotations.openMocks(this);
        converter = new InstantConverter();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testGetAsObject_NullValue_ReturnsNull() {
        assertNull(converter.getAsObject(facesContext, component, null));
    }

    @Test
    public void testGetAsObject_EmptyValue_ReturnsNull() {
        assertNull(converter.getAsObject(facesContext, component, ""));
        assertNull(converter.getAsObject(facesContext, component, "   "));
    }

    @Test
    public void testGetAsObject_ValidIso_ReturnsInstant() {
        String isoString = "2024-01-15T10:30:00Z";
        Instant result = converter.getAsObject(facesContext, component, isoString);
        assertNotNull(result);
        assertEquals(Instant.parse(isoString), result);
    }

    @Test
    public void testGetAsString_NullValue_ReturnsEmpty() {
        Map<String, Object> attrs = new HashMap<>();
        when(component.getAttributes()).thenReturn(attrs);
        assertEquals("", converter.getAsString(facesContext, component, null));
    }

    @Test
    public void testGetAsString_ValidInstant_ReturnsFormattedString() {
        Map<String, Object> attrs = new HashMap<>();
        when(component.getAttributes()).thenReturn(attrs);

        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        String result = converter.getAsString(facesContext, component, instant);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testGetAsString_WithCustomPattern_UsesPattern() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("pattern", "yyyy-MM-dd");
        when(component.getAttributes()).thenReturn(attrs);

        Instant instant = Instant.parse("2024-01-15T10:30:00Z");
        String result = converter.getAsString(facesContext, component, instant);
        assertNotNull(result);
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}
