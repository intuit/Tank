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

/**
 * ListConverter
 * 
 * @author dangleton
 * 
 */
public abstract class BaseEnumConverter<T extends Enum<T>> implements Converter {

    private Class<T> enumType;

    /**
     * @param enumType
     */
    public BaseEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        return Enum.valueOf(enumType, value);
    }

    /**
     * @inheritDoc
     */
    @SuppressWarnings("unchecked")
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        if (enumType.equals(obj.getClass())) {
            return ((T) obj).name();
        }
        return obj.toString();
    }

}
