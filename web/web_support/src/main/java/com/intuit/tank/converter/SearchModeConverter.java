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

import com.intuit.tank.script.replace.SearchMode;

/**
 * ListConverter
 * 
 * @author dangleton
 * 
 */
@FacesConverter(value = "tsSearchModeConverter")
public class SearchModeConverter implements Converter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        return SearchMode.valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        if (obj instanceof SearchMode) {
            return ((SearchMode) obj).name();
        }
        return obj.toString();
    }

}
