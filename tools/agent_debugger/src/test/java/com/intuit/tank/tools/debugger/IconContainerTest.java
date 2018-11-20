package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.junit.jupiter.api.*;

import com.intuit.tank.tools.debugger.IconContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>IconContainerTest</code> contains tests for the class <code>{@link IconContainer}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:41 PM
 */
public class IconContainerTest {
    /**
     * Run the IconContainer(int,Icon) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testIconContainer_1()
        throws Exception {
        int line = 1;
        Icon icon = new ImageIcon();

        IconContainer result = new IconContainer(line, icon);

        assertNotNull(result);
        assertEquals(1, result.getLine());
    }

    /**
     * Run the Icon getIcon() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testGetIcon_1()
        throws Exception {
        IconContainer fixture = new IconContainer(1, new ImageIcon());

        Icon result = fixture.getIcon();

        assertNotNull(result);
        assertEquals(-1, result.getIconWidth());
        assertEquals(-1, result.getIconHeight());
    }

    /**
     * Run the int getLine() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testGetLine_1()
        throws Exception {
        IconContainer fixture = new IconContainer(1, new ImageIcon());

        int result = fixture.getLine();

        assertEquals(1, result);
    }
}