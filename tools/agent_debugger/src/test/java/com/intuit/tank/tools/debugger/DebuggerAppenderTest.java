package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.Test;

/**
 * JTextArea is a lightweight component, so these run headless.
 */
public class DebuggerAppenderTest {

    @Test
    public void appenderWithoutANameIsNotCreated() {
        assertNull(DebuggerAppender.createAppender(null, null, null, true));
    }

    @Test
    public void appenderWithoutALayoutUsesTheDefault() {
        DebuggerAppender appender = DebuggerAppender.createAppender("debugger", null, null, true);

        assertNotNull(appender);
        assertEquals("debugger", appender.getName());
        assertNotNull(appender.getLayout());
    }

    @Test
    public void messagesAreWrittenAndThenAppendedToRegisteredTextAreas() throws Exception {
        DebuggerAppender appender = DebuggerAppender.createAppender("debugger",
                PatternLayout.newBuilder().withPattern("%m|").build(), null, true);
        JTextArea textArea = new JTextArea();
        DebuggerAppender.addTextArea(textArea);

        appender.append(event("first"));
        appender.append(event("second"));
        // the appender updates the text areas on the event thread; wait for it to drain
        SwingUtilities.invokeAndWait(() -> { });

        // the log4j2.xml appender also writes to registered areas, so other log lines may surround ours
        assertTrue(textArea.getText().contains("first|second|"));
    }

    private static LogEvent event(String message) {
        return Log4jLogEvent.newBuilder()
                .setLoggerName("test")
                .setLevel(Level.INFO)
                .setMessage(new SimpleMessage(message))
                .build();
    }
}
