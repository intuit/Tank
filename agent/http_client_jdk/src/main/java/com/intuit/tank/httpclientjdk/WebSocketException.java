package com.intuit.tank.httpclientjdk;

/**
 * Exception thrown when an incoming WebSocket message matches a fail-on pattern.
 */
public class WebSocketException extends RuntimeException {

    private final String pattern;
    private final String failedMessage;

    public WebSocketException(String pattern, String failedMessage) {
        super("Fail-on pattern matched: '" + pattern + "' in message: " + (failedMessage != null ? failedMessage : "(null)"));
        this.pattern = pattern;
        this.failedMessage = failedMessage;
    }

    public String getPattern() {
        return pattern;
    }

    public String getFailedMessage() {
        return failedMessage;
    }
}
