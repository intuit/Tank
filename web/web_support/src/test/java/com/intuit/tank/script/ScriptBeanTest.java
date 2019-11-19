package com.intuit.tank.script;

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

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.Script;
import com.intuit.tank.script.ScriptBean;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableWrapper;
import org.picketlink.Identity;
import org.picketlink.extension.PicketLinkExtension;

/**
 * The class <code>ScriptBeanTest</code> contains tests for the class <code>{@link ScriptBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
@EnableAutoWeld
@AddPackages(Identity.class)
@AddExtensions(PicketLinkExtension.class)
@ActivateScopes(ViewScoped.class)
public class ScriptBeanTest {

    @Inject
    private ScriptBean scriptBean;

    /**
     * Run the ScriptBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testScriptBean_1()
        throws Exception {
        assertNotNull(scriptBean);
    }
}