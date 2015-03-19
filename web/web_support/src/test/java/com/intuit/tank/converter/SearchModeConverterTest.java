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

import com.intuit.tank.converter.SearchModeConverter;
import com.intuit.tank.script.replace.SearchMode;

/**
 * The class <code>SearchModeConverterTest</code> contains tests for the class <code>{@link SearchModeConverter}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class SearchModeConverterTest {
    /**
     * Run the Object getAsObject(FacesContext,UIComponent,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetAsObject_1()
            throws Exception {
        SearchModeConverter fixture = new SearchModeConverter();

        Object result = fixture.getAsObject(null, null, SearchMode.all.name());
        assertNotNull(result);
    }

    /**
     * Run the String getAsString(FacesContext,UIComponent,Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetAsString_1()
            throws Exception {
        SearchModeConverter fixture = new SearchModeConverter();

        String result = fixture.getAsString(null, null, SearchMode.all);
        assertNotNull(result);
    }
}