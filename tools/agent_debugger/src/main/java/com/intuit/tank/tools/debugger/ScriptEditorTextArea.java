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

import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaUI;

/**
 * Script editor text area whose gutter uses a {@link ScriptIconRowHeader}. The gutter asks the text area's editor
 * kit for its icon row, so the kit is swapped for one that builds ours.
 */
public class ScriptEditorTextArea extends RSyntaxTextArea {

    private static final long serialVersionUID = 1L;

    private static final EditorKit EDITOR_KIT = new RSyntaxTextAreaEditorKit() {
        private static final long serialVersionUID = 1L;

        @Override
        public IconRowHeader createIconRowHeader(RTextArea textArea) {
            ScriptIconRowHeader header = new ScriptIconRowHeader((RSyntaxTextArea) textArea);
            ((ScriptEditorTextArea) textArea).iconRowHeader = header;
            return header;
        }
    };

    private ScriptIconRowHeader iconRowHeader;

    /**
     * @return the gutter's icon row; null until the text area is put in an RTextScrollPane
     */
    public ScriptIconRowHeader getIconRowHeader() {
        return iconRowHeader;
    }

    @Override
    protected RTextAreaUI createRTextAreaUI() {
        return new RSyntaxTextAreaUI(this) {
            @Override
            public EditorKit getEditorKit(JTextComponent tc) {
                return EDITOR_KIT;
            }
        };
    }

}
