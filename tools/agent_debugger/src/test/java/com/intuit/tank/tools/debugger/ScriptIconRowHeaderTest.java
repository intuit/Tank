package com.intuit.tank.tools.debugger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class ScriptIconRowHeaderTest {

    private ScriptEditorTextArea textArea;
    private ScriptIconRowHeader header;

    @BeforeEach
    public void setup() {
        textArea = new ScriptEditorTextArea();
        textArea.setText("step 0\nstep 1\nstep 2\n");
        new RTextScrollPane(textArea).setIconRowHeaderEnabled(true);
        header = textArea.getIconRowHeader();
        assertNotNull(header);
    }

    @Test
    public void testGetAllTrackingIconsIncludesBookmarks() throws Exception {
        assertEquals(0, header.getAllTrackingIcons().length);
        Icon bookmark = new SquareIcon(Color.BLUE, 0);
        header.setBookmarkIcon(bookmark);
        header.setBookmarkingEnabled(true);
        header.toggleBookmark(1);
        GutterIconInfo skip = header.addOffsetTrackingIcon(textArea.getLineStartOffset(1), new SquareIcon(Color.RED, 0));

        GutterIconInfo[] all = header.getAllTrackingIcons();
        assertEquals(2, all.length);
        assertEquals(1, header.getBookmarks().length);

        header.removeTrackingIcon(skip);
        assertEquals(1, header.getAllTrackingIcons().length);
    }

    @Test
    public void testPaintsEveryIconOnALineAndTheCurrentLineIcon() throws Exception {
        // side by side so both are visible if both are painted
        header.addOffsetTrackingIcon(textArea.getLineStartOffset(1), new SquareIcon(Color.RED, 0));
        header.addOffsetTrackingIcon(textArea.getLineStartOffset(1), new SquareIcon(Color.GREEN, 4));
        header.setCurrentLineIcon(new SquareIcon(Color.BLUE, 8));
        textArea.setActiveLineRange(2, 2);

        BufferedImage image = paint();
        int line1 = iconMiddle(1);
        int line2 = iconMiddle(2);
        assertEquals(Color.RED.getRGB(), image.getRGB(1, line1));
        assertEquals(Color.GREEN.getRGB(), image.getRGB(5, line1));
        assertEquals(Color.BLUE.getRGB(), image.getRGB(9, line2));

        textArea.setActiveLineRange(-1, -1);
        assertNotEquals(Color.BLUE.getRGB(), paint().getRGB(9, line2));
    }

    private int iconMiddle(int line) {
        int cellHeight = textArea.getLineHeight();
        return textArea.getInsets().top + line * cellHeight + (cellHeight - 3) / 2 + 1;
    }

    private BufferedImage paint() {
        header.setSize(16, textArea.getLineHeight() * 4);
        BufferedImage image = new BufferedImage(header.getWidth(), header.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setClip(0, 0, image.getWidth(), image.getHeight());
        header.paint(g);
        g.dispose();
        return image;
    }

    /** A 3x3 square drawn {@code x} pixels in, so icons on the same line don't cover each other */
    private static class SquareIcon implements Icon {
        private final Color color;
        private final int x;

        SquareIcon(Color color, int x) {
            this.color = color;
            this.x = x;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x + this.x, y, 3, 3);
        }

        @Override
        public int getIconWidth() {
            return 3;
        }

        @Override
        public int getIconHeight() {
            return 3;
        }
    }
}
