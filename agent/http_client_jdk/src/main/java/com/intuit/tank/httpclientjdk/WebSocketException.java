package com.intuit.tank.httpclientjdk;

/**
 * Exception thrown when an incoming WebSocket message matches a fail-on pattern.
 */
public class WebSocketException extends RuntimeException {

    private final String pattern;
    private final String message;

    public WebSocketException(String pattern, String message) {
        super("Fail-on pattern matched: '" + pattern + "' in message: " + (message != null ? message : "(null)"));
        this.pattern = pattern;
        this.message = message;
    }

    public String getPattern() {
        return pattern;
    }

    public String getFailedMessage() {
        return message;
    }
}
