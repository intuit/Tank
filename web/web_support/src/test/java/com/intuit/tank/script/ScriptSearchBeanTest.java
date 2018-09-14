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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptSearchBean;
import com.intuit.tank.script.replace.ReplaceEntity;
import com.intuit.tank.script.replace.ReplaceMode;
import com.intuit.tank.search.script.Section;

/**
 * The class <code>ScriptSearchBeanTest</code> contains tests for the class <code>{@link ScriptSearchBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ScriptSearchBeanTest {
    /**
     * Run the ScriptSearchBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testScriptSearchBean_1()
        throws Exception {
        ScriptSearchBean result = new ScriptSearchBean();
        assertNotNull(result);
    }
}