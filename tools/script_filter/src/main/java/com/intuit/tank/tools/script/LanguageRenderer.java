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

import java.awt.Component;

import javax.script.ScriptEngineFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * LanguageRenderer
 * 
 * @author dangleton
 * 
 */
public class LanguageRenderer implements ListCellRenderer {

    JLabel label = new JLabel();

    /**
     * @inheritDoc
     */
    @Override
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        String text = null;
        if (value != null && value instanceof ScriptEngineFactory) {
            ScriptEngineFactory f = (ScriptEngineFactory) value;
            text = f.getLanguageName();
        }
        label.setText(text != null ? text : "");
        return label;
    }

}
