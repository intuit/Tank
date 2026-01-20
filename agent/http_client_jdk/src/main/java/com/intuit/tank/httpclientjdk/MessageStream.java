package com.intuit.tank.httpclientjdk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Collects all incoming WebSocket messages for a connection.
 * Supports fail-on patterns for immediate failure detection and
 * query API for assertions at session end.
 */
public class MessageStream {

    private static final Logger LOG = LogManager.getLogger(MessageStream.class);
    
    // Logging constants
    private static final int LOG_TRUNCATE_LENGTH = 500;  // Enough to see most JSON/errors
    private static final String TRUNCATE_SUFFIX = "...";
    public static final int DEFAULT_MIN_COUNT = 1;

    private final String connectionId;
    private final CopyOnWriteArrayList<TimestampedMessage> messages = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<FailOnMatcher> failOnMatchers = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, Pattern> patternCache = new ConcurrentHashMap<>();
    private final AtomicInteger messageCounter = new AtomicInteger(0);
    private final long startTime;

    // Failure state
    private final AtomicBoolean failed = new AtomicBoolean(false);
    private volatile String failurePattern;
    private volatile String failureMessage;

    /**
     * Represents a collected message with metadata
     */
    public record TimestampedMessage(
        String content,
        long timestamp,
        int index,
        long relativeTimeMs
    ) {}

    /**
     * Internal matcher for fail-on patterns
     */
    private record FailOnMatcher(String patternString, Pattern compiledPattern, boolean isRegex) {
        boolean matches(String message) {
            if (isRegex) {
                return compiledPattern.matcher(message).find();
            }
            return message.contains(patternString);
        }
    }

    public MessageStream(String connectionId) {
        this.connectionId = connectionId;
        this.startTime = System.currentTimeMillis();
        LOG.debug("Created MessageStream for connection: {}", connectionId);
    }

    /**
     * Add a fail-on pattern. Messages matching this pattern will trigger immediate failure.
     * @param pattern The pattern to match
     * @param isRegex If true, pattern is treated as regex; otherwise substring match
     * @throws IllegalArgumentException if pattern is null/empty or invalid regex
     */
    public void addFailOnPattern(String pattern, boolean isRegex) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Fail-on pattern cannot be null or empty");
        }
        
        Pattern compiled = null;
        if (isRegex) {
            try {
                compiled = Pattern.compile(pattern);
                patternCache.put(pattern, compiled);  // Cache for later use
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException(
                    "Invalid regex pattern for fail-on: '" + pattern + "': " + e.getMessage(), e);
            }
        }
        
        failOnMatchers.add(new FailOnMatcher(pattern, compiled, isRegex));
        LOG.debug("Added fail-on pattern for {}: '{}' (regex={})", connectionId, pattern, isRegex);
    }

    /**
     * Add a message to the stream. Checks against fail-on patterns first.
     * @param content The message content
     * @throws WebSocketException if message matches a fail-on pattern
     * @throws IllegalArgumentException if content is null
     */
    public void addMessage(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Message content cannot be null");
        }
        
        // Check fail-on patterns FIRST - abort immediately if matched
        for (FailOnMatcher matcher : failOnMatchers) {
            if (matcher.matches(content)) {
                failed.set(true);
                failurePattern = matcher.patternString();
                failureMessage = content;
                LOG.error("Fail-on pattern '{}' matched in message: {}",
                    matcher.patternString(), content);
                throw new WebSocketException(matcher.patternString(), content);
            }
        }

        // Add to collection
        long now = System.currentTimeMillis();
        int index = messageCounter.getAndIncrement();
        TimestampedMessage msg = new TimestampedMessage(content, now, index, now - startTime);
        messages.add(msg);

        LOG.debug("Collected message #{} for {} ({}ms): {}",
            index, connectionId, msg.relativeTimeMs(), truncateForLog(content));
    }

    // ==================== Utility Methods ====================

    /**
     * Truncate message for debug logging (500 chars max for readability)
     */
    public static String truncateForLog(String text) {
        if (text == null) {
            return "(null)";
        }
        return text.length() > LOG_TRUNCATE_LENGTH 
            ? text.substring(0, LOG_TRUNCATE_LENGTH) + TRUNCATE_SUFFIX 
            : text;
    }

    /**
     * Extract first capture group if regex has groups, otherwise full match
     */
    private static String extractCaptureGroup(Matcher matcher) {
        return matcher.groupCount() > 0 ? matcher.group(1) : matcher.group(0);
    }

    // ==================== Query API for Assertions ====================

    /**
     * Helper method to check if a message matches a pattern.
     * Used by both fail-on checks and assertion queries.
     */
    private boolean matchesPattern(String content, String pattern, boolean isRegex, Pattern compiled) {
        if (content == null) {
            return false;
        }
        if (isRegex && compiled != null) {
            return compiled.matcher(content).find();
        }
        return content.contains(pattern);
    }

    /**
     * Get or compile a pattern, using cache for performance.
     */
    private Pattern getOrCompilePattern(String regex) {
        return patternCache.computeIfAbsent(regex, p -> {
            try {
                LOG.debug("Compiling and caching pattern for {}: {}", connectionId, regex);
                return Pattern.compile(p);
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException("Invalid regex pattern: '" + regex + "'", e);
            }
        });
    }

    /**
     * Check if any message matches the pattern
     */
    public boolean hasMatching(String pattern, boolean isRegex) {
        return countMatching(pattern, isRegex) > 0;
    }

    /**
     * Count messages matching the pattern
     */
    public int countMatching(String pattern, boolean isRegex) {
        Pattern compiled = isRegex ? getOrCompilePattern(pattern) : null;
        int count = 0;
        for (TimestampedMessage msg : messages) {
            if (matchesPattern(msg.content(), pattern, isRegex, compiled)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get all messages matching the pattern
     */
    public List<TimestampedMessage> getMatching(String pattern, boolean isRegex) {
        Pattern compiled = isRegex ? getOrCompilePattern(pattern) : null;
        List<TimestampedMessage> result = new ArrayList<>();
        for (TimestampedMessage msg : messages) {
            if (matchesPattern(msg.content(), pattern, isRegex, compiled)) {
                result.add(msg);
            }
        }
        return result;
    }

    /**
     * Extract first capture group from first matching message
     */
    public Optional<String> extractFirst(String regexPattern) {
        Pattern compiled = getOrCompilePattern(regexPattern);
        for (TimestampedMessage msg : messages) {
            Matcher matcher = compiled.matcher(msg.content());
            if (matcher.find()) {
                return Optional.of(extractCaptureGroup(matcher));
            }
        }
        return Optional.empty();
    }

    /**
     * Extract first capture group from last matching message
     */
    public Optional<String> extractLast(String regexPattern) {
        Pattern compiled = getOrCompilePattern(regexPattern);
        String lastMatch = null;
        for (TimestampedMessage msg : messages) {
            Matcher matcher = compiled.matcher(msg.content());
            if (matcher.find()) {
                lastMatch = extractCaptureGroup(matcher);
            }
        }
        return Optional.ofNullable(lastMatch);
    }

    // ==================== Status API ====================

    public boolean hasFailed() {
        return failed.get();
    }

    public String getFailurePattern() {
        return failurePattern;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public int getMessageCount() {
        return messages.size();
    }

    public List<TimestampedMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public String getConnectionId() {
        return connectionId;
    }

    public long getElapsedTimeMs() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Get summary for logging
     */
    public String getSummary() {
        return String.format("MessageStream[%s]: %d messages collected over %dms, failed=%s",
            connectionId, messages.size(), getElapsedTimeMs(), failed.get());
    }
}
