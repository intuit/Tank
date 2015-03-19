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

import org.junit.*;

import static org.junit.Assert.*;

import org.primefaces.extensions.component.dynaform.DynaForm;

import com.intuit.tank.converter.ReplaceModeConverter;
import com.intuit.tank.script.replace.ReplaceMode;

/**
 * The class <code>ReplaceModeConverterTest</code> contains tests for the class <code>{@link ReplaceModeConverter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ReplaceModeConverterTest {
    /**
     * Run the Object getAsObject(FacesContext,UIComponent,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAsObject_1()
        throws Exception {
        ReplaceModeConverter fixture = new ReplaceModeConverter();

        Object result = fixture.getAsObject(null, null, ReplaceMode.KEY.name());
        assertNotNull(result);
    }

    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAsString_1()
        throws Exception {
        ReplaceModeConverter fixture = new ReplaceModeConverter();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent uiComponent = new DynaForm();
        Object obj = ReplaceMode.KEY;

        String result = fixture.getAsString(facesContext, uiComponent, obj);
        assertNotNull(result);
    }

}