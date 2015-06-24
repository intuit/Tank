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

import java.text.ParseException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

import com.intuit.tank.PreferencesBean;
import com.intuit.tank.vm.common.TankConstants;

/**
 * ListConverter
 * 
 * @author dangleton
 * 
 */
@FacesConverter(value = "tsDateConverter")
@Named
public class DateConverter implements Converter {
    private static final Logger LOG = Logger.getLogger(DateConverter.class);

    private static final String PATTERN = TankConstants.DATE_FORMAT;
    private static FastDateFormat DF = FastDateFormat.getInstance(DateConverter.PATTERN);
    
    @Inject
    private PreferencesBean preferencesBean;

    /**
     * @{inheritDoc
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        try {
            FastDateFormat fmt = DF;
            if (preferencesBean != null) {
                fmt = preferencesBean.getDateTimeFormat();
            }
            return fmt.parseObject(value);
        } catch (ParseException e) {
            // throw new IllegalArgumentException("Passed in value was not a valid date format in the pattern of " +
            // PATTERN);
            LOG.error("Cannot parse date value of" + value + ". Must be in the pattern of " + PATTERN);
        }
        return null;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        FastDateFormat fmt = DF;
        if (preferencesBean != null) {
            fmt = preferencesBean.getDateTimeFormat();
        }
        return fmt.format(obj);
    }

}
