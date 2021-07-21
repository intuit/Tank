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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ListConverter
 * 
 * @author dangleton
 * 
 */
@FacesConverter(value = "tsScriptStepHolderConverter")
public class ScriptStepHolderConverter implements Converter {
    private static final Logger LOG = LogManager.getLogger(ScriptStepHolderConverter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        try {
            return ScriptStepHolder.fromCannonicalForm(value);
        } catch (Exception e) {
            // throw new IllegalArgumentException("Passed in value was not a valid date format in the pattern of " +
            // PATTERN);
            LOG.error("Cannot parse datafile value of" + value + ".");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        return ((ScriptStepHolder) obj).toCanonicalForm();
    }

}
