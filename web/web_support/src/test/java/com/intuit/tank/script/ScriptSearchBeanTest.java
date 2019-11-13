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

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;
import org.picketlink.Identity;
import org.picketlink.extension.PicketLinkExtension;

import static org.junit.jupiter.api.Assertions.*;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

/**
 * The class <code>ScriptSearchBeanTest</code> contains tests for the class <code>{@link ScriptSearchBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
@EnableAutoWeld
@AddPackages(Identity.class)
@AddExtensions(PicketLinkExtension.class)
@ActivateScopes(ConversationScoped.class)
public class ScriptSearchBeanTest {

    @Inject
    private ScriptSearchBean scriptSearchBean;

    /**
     * Run the ScriptSearchBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testScriptSearchBean_1()
        throws Exception {
        assertNotNull(scriptSearchBean);
    }
}