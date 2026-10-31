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

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.FoldingAwareIconRowHeader;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.GutterIconInfo;

/**
 * Icon row of the script editor's gutter. Unlike the stock row, which paints one icon per line, it paints every
 * tracking icon on a line (bookmark, skip, error, modified) and marks the active line range (the running step) with
 * an icon instead of hatching.
 */
public class ScriptIconRowHeader extends FoldingAwareIconRowHeader {

    private static final long serialVersionUID = 1L;

    private Icon currentLineIcon;

    public ScriptIconRowHeader(RSyntaxTextArea textArea) {
        super(textArea);
    }

    /**
     * @param currentLineIcon
     *            the icon painted on the active line
     */
    public void setCurrentLineIcon(Icon currentLineIcon) {
        this.currentLineIcon = currentLineIcon;
        repaint();
    }

    /**
     * @return every tracking icon in the gutter, bookmarks included, ordered by offset
     */
    public GutterIconInfo[] getAllTrackingIcons() {
        if (trackingIcons == null) {
            return new GutterIconInfo[0];
        }
        GutterIconInfo[] ret = new GutterIconInfo[trackingIcons.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getTrackingIcon(i);
        }
        return ret;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (textArea == null) {
            return;
        }
        visibleRect = g.getClipBounds(visibleRect);
        if (visibleRect == null) {
            visibleRect = getVisibleRect();
        }
        paintBackgroundImpl(g, visibleRect);

        Element root = textArea.getDocument().getDefaultRootElement();
        int docLength = textArea.getDocument().getLength();
        textAreaInsets = textArea.getInsets(textAreaInsets);
        int cellHeight = textArea.getLineHeight();
        int topLine = Math.max(0, (visibleRect.y - textAreaInsets.top) / cellHeight);
        int bottomLine = topLine + visibleRect.height / cellHeight + 1;

        if (trackingIcons != null) {
            // last to first, so that of the icons on a line the earliest added ends up on top
            for (int i = trackingIcons.size() - 1; i >= 0; i--) {
                GutterIconInfo ti = getTrackingIcon(i);
                int offs = ti.getMarkedOffset();
                if (offs >= 0 && offs <= docLength) {
                    paintLineIcon(g, ti.getIcon(), root.getElementIndex(offs), topLine, bottomLine, cellHeight);
                }
            }
        }
        if (activeLineRangeStart > -1) {
            paintLineIcon(g, currentLineIcon, activeLineRangeStart, topLine, bottomLine, cellHeight);
        }
    }

    private void paintLineIcon(Graphics g, Icon icon, int line, int topLine, int bottomLine, int cellHeight) {
        if (icon != null && line >= topLine && line <= bottomLine) {
            int y = textAreaInsets.top + line * cellHeight + (cellHeight - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g, 0, y);
        }
    }

}
