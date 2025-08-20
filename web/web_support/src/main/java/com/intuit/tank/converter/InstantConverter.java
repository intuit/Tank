/*
 * Copyright (c) 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * JSF Converter for Java 8 Instant objects.
 * Converts Instant to formatted date/time strings for display.
 */
@FacesConverter("instantConverter")
public class InstantConverter implements Converter<Instant> {

    private static final String DEFAULT_PATTERN = "MMM dd, yyyy HH:mm:ss";

    @Override
    public Instant getAsObject(FacesContext context, UIComponent component, String value) {
        // This converter is primarily for display purposes, 
        // so we don't need to implement string -> Instant conversion
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Instant.parse(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Instant value) {
        if (value == null) {
            return "";
        }

        // Get pattern from f:attribute if specified, otherwise use default
        String pattern = DEFAULT_PATTERN;
        Map<String, Object> attributes = component.getAttributes();
        if (attributes.containsKey("pattern")) {
            pattern = (String) attributes.get("pattern");
        }

        // Convert Instant to formatted string using system default timezone
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
            .withZone(ZoneId.systemDefault());
        
        return formatter.format(value);
    }
}