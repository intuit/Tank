/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TimeZoneConverter
 * 
 * @author dangleton
 * 
 */
@FacesConverter(value = "tsTimeZoneConverter")
public class TimeZoneConverter implements Converter {
    private static final Logger LOG = LogManager.getLogger(TimeZoneConverter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        try {
            return TimeZone.getTimeZone(value);
        } catch (Exception e) {
            LOG.error("Cannot parse timezone value of " + value);
        }
        return TimeZone.getDefault();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        String ret = "GMT";
        if (obj instanceof TimeZone) {
            TimeZone tz = (TimeZone) obj;
            ret = tz.getDisplayName();
        }
        return ret;
    }

}
