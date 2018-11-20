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

import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.Script;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 * ListConverter
 * 
 * @author dangleton
 * 
 */
@FacesConverter(value = "tsScriptConverter")
public class ScriptConverter implements Converter {
    private static final Logger LOG = LogManager.getLogger(ScriptConverter.class);

    /**
     * @inheritDoc
     */
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        try {
            DualListModel<Script> model = (DualListModel<Script>) ((PickList) uiComponent).getValue();
            for (Script script : model.getSource()) {
                if (Integer.toString(script.getId()).equals(value)) {
                    return script;
                }
            }
        } catch (Exception e) {
            // throw new IllegalArgumentException("Passed in value was not a valid date format in the pattern of " +
            // PATTERN);
            LOG.error("Cannot parse script id value of" + value + ".");
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        return Integer.toString(((Script) obj).getId());
    }

}
