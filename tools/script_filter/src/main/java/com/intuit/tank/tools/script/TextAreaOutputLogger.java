/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.script;

/*
 * #%L
 * script-filter
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.intuit.tank.tools.script.OutputLogger;

/**
 * TextAreaOutputLogger
 * 
 * @author dangleton
 * 
 */
public class TextAreaOutputLogger extends JTextArea implements OutputLogger {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private boolean autoScroll;
    private JScrollPane sp;

    /**
     * @param text
     */
    TextAreaOutputLogger(JScrollPane sp, String text) {
        super(text);
        this.sp = sp;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void setScrollContent(boolean autoScroll) {
        this.autoScroll = autoScroll;
        sp.setAutoscrolls(autoScroll);

    }

    /**
     * @{inheritDoc
     */
    @Override
    public void log(String text) {
        append(text);
        if (autoScroll) {
            setCaretPosition(this.getText().length() - 1);
        }
    }

    @Override
    public void debug(String text) {
        log(text);
    }

    @Override
    public void error(String text) {
        log("ERROR: " + text);
    }

}
