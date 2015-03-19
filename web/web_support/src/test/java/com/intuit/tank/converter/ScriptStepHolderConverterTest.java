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

import com.intuit.tank.converter.ScriptStepHolder;
import com.intuit.tank.converter.ScriptStepHolderConverter;

/**
 * The class <code>ScriptStepHolderConverterTest</code> contains tests for the class <code>{@link ScriptStepHolderConverter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ScriptStepHolderConverterTest {
    /**
     * Run the ScriptStepHolderConverter() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testScriptStepHolderConverter_1()
        throws Exception {
        ScriptStepHolderConverter result = new ScriptStepHolderConverter();
        assertNotNull(result);
    }

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
        ScriptStepHolderConverter fixture = new ScriptStepHolderConverter();
        ScriptStepHolder obj = new ScriptStepHolder(0, "uuid", "Label");
        String value = fixture.getAsString(null, null, obj);

        Object result = fixture.getAsObject(null, null, value);

        assertNotNull(result);
    }


}