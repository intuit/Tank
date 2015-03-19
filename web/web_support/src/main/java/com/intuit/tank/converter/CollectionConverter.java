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

import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * ListConverter
 * 
 * @author dangleton
 * 
 */
@FacesConverter(value = "tsCollectionConverter")
public class CollectionConverter implements Converter {

    /**
     * @{inheritDoc
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        throw new IllegalArgumentException("CAnnot convert to object.");
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        StringBuilder sb = new StringBuilder();
        if (obj instanceof Collection) {
            @SuppressWarnings("unchecked") Collection<? extends Object> c = (Collection<? extends Object>) obj;
            for (Object o : c) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(o.toString());
            }
        } else {
            throw new IllegalArgumentException("Value must be a Collection type.");
        }
        return sb.toString();
    }

}
