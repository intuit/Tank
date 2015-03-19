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

public class IconContainer {

    private int line;
    private Icon icon;

    public IconContainer(int line, Icon icon) {
        super();
        this.line = line;
        this.icon = icon;
    }

    /**
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * @return the icon
     */
    public Icon getIcon() {
        return icon;
    }

}
