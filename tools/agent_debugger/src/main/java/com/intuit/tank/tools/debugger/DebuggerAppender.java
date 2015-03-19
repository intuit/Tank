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

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class DebuggerAppender extends AppenderSkeleton {
    private JTextArea textArea;

    public DebuggerAppender(JTextArea textArea) {
        super();
        this.textArea = textArea;
        setLayout(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
    }

    @Override
    protected void append(LoggingEvent event) {
        textArea.append(this.layout.format(event));

        if (layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null) {
                int len = s.length;
                for (int i = 0; i < len; i++) {
                    textArea.append(s[i]);
                    textArea.append(Layout.LINE_SEP);
                }
            }
        }
        try {
            int lineStartOffset = 0;
            if (textArea.getLineCount() > 0) {
                lineStartOffset = textArea.getLineStartOffset(textArea.getLineCount() - 1);
            }
            textArea.setCaretPosition(lineStartOffset);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

}
