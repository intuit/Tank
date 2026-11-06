package com.intuit.tank.tools.headless;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

/**
 * Collects the messages a class logs, so tests can check the headless output (the summary and step lines).
 */
class LogCapture extends AbstractAppender implements AutoCloseable {

    private final List<String> messages = new CopyOnWriteArrayList<>();
    private final Logger logger;

    LogCapture(Class<?> loggingClass) {
        super("capture-" + loggingClass.getSimpleName(), null, null, true, Property.EMPTY_ARRAY);
        logger = (Logger) LogManager.getLogger(loggingClass);
        start();
        logger.addAppender(this);
    }

    @Override
    public void append(LogEvent event) {
        messages.add(event.getMessage().getFormattedMessage());
    }

    List<String> messages() {
        return messages;
    }

    boolean contains(String message) {
        return messages.contains(message);
    }

    boolean anyContains(String fragment) {
        return messages.stream().anyMatch(m -> m.contains(fragment));
    }

    @Override
    public void close() {
        logger.removeAppender(this);
        stop();
    }
}
