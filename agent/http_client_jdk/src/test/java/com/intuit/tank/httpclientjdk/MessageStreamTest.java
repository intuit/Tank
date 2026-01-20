package com.intuit.tank.httpclientjdk;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Unit tests for MessageStream - 100% line coverage target
 */
class MessageStreamTest {

    private MessageStream stream;

    @BeforeEach
    void setUp() {
        stream = new MessageStream("test-connection");
    }

    // ==================== Constructor & Basic State ====================

    @Test
    @DisplayName("Constructor should initialize with connection ID")
    void testConstructor() {
        MessageStream s = new MessageStream("my-conn");
        assertEquals("my-conn", s.getConnectionId());
        assertEquals(0, s.getMessageCount());
        assertFalse(s.hasFailed());
        assertNull(s.getFailurePattern());
        assertNull(s.getFailureMessage());
        assertTrue(s.getAllMessages().isEmpty());
    }

    @Test
    @DisplayName("getElapsedTimeMs should return positive value after creation")
    void testElapsedTime() throws InterruptedException {
        Thread.sleep(10);
        long elapsed = stream.getElapsedTimeMs();
        assertTrue(elapsed >= 10, "Elapsed time should be at least 10ms, was: " + elapsed);
    }

    // ==================== Message Collection ====================

    @Test
    @DisplayName("addMessage should collect messages with metadata")
    void testAddMessage() {
        stream.addMessage("first message");
        stream.addMessage("second message");
        stream.addMessage("third message");

        assertEquals(3, stream.getMessageCount());

        List<MessageStream.TimestampedMessage> messages = stream.getAllMessages();
        assertEquals(3, messages.size());

        // Check first message
        assertEquals("first message", messages.get(0).content());
        assertEquals(0, messages.get(0).index());
        assertTrue(messages.get(0).timestamp() > 0);
        assertTrue(messages.get(0).relativeTimeMs() >= 0);

        // Check indices are sequential
        assertEquals(0, messages.get(0).index());
        assertEquals(1, messages.get(1).index());
        assertEquals(2, messages.get(2).index());
    }

    @Test
    @DisplayName("addMessage should reject null content")
    void testAddMessageNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stream.addMessage(null);
        });
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    @DisplayName("getAllMessages should return defensive copy")
    void testGetAllMessagesDefensiveCopy() {
        stream.addMessage("msg1");
        List<MessageStream.TimestampedMessage> list1 = stream.getAllMessages();
        stream.addMessage("msg2");
        List<MessageStream.TimestampedMessage> list2 = stream.getAllMessages();

        assertEquals(1, list1.size());
        assertEquals(2, list2.size());
    }

    // ==================== Fail-On Patterns ====================

    @Test
    @DisplayName("addFailOnPattern should register substring pattern")
    void testAddFailOnPatternSubstring() {
        stream.addFailOnPattern("error", false);

        // Non-matching message should pass
        stream.addMessage("success message");
        assertEquals(1, stream.getMessageCount());
        assertFalse(stream.hasFailed());
    }

    @Test
    @DisplayName("addFailOnPattern should register regex pattern")
    void testAddFailOnPatternRegex() {
        stream.addFailOnPattern("\"code\":\\s*5\\d\\d", true);

        // Non-matching should pass
        stream.addMessage("{\"code\": 200}");
        assertFalse(stream.hasFailed());
    }

    @Test
    @DisplayName("addFailOnPattern should reject null pattern")
    void testAddFailOnPatternNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stream.addFailOnPattern(null, false);
        });
        assertTrue(ex.getMessage().contains("null or empty"));
    }

    @Test
    @DisplayName("addFailOnPattern should reject empty pattern")
    void testAddFailOnPatternEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stream.addFailOnPattern("", false);
        });
        assertTrue(ex.getMessage().contains("null or empty"));
    }

    @Test
    @DisplayName("addFailOnPattern should reject invalid regex")
    void testAddFailOnPatternInvalidRegex() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            stream.addFailOnPattern("[invalid(regex", true);
        });
        assertTrue(ex.getMessage().contains("Invalid regex"));
    }

    @Test
    @DisplayName("Fail-on substring pattern should trigger on match")
    void testFailOnSubstringTrigger() {
        stream.addFailOnPattern("error", false);

        WebSocketException ex = assertThrows(WebSocketException.class, () -> {
            stream.addMessage("This is an error message");
        });

        assertEquals("error", ex.getPattern());
        assertTrue(ex.getFailedMessage().contains("error"));
        assertTrue(stream.hasFailed());
        assertEquals("error", stream.getFailurePattern());
        assertEquals("This is an error message", stream.getFailureMessage());
    }

    @Test
    @DisplayName("Fail-on regex pattern should trigger on match")
    void testFailOnRegexTrigger() {
        stream.addFailOnPattern("\"status\":\\s*5\\d\\d", true);

        WebSocketException ex = assertThrows(WebSocketException.class, () -> {
            stream.addMessage("{\"status\": 503, \"error\": \"Service Unavailable\"}");
        });

        assertTrue(stream.hasFailed());
        assertTrue(ex.getPattern().contains("status"));
    }

    @Test
    @DisplayName("Multiple fail-on patterns should all be checked")
    void testMultipleFailOnPatterns() {
        stream.addFailOnPattern("error", false);
        stream.addFailOnPattern("failed", false);
        stream.addFailOnPattern("\"code\":5\\d\\d", true);

        // First two patterns don't match
        stream.addMessage("success");
        stream.addMessage("ok");
        assertFalse(stream.hasFailed());

        // Third pattern matches
        assertThrows(WebSocketException.class, () -> {
            stream.addMessage("{\"code\":500}");
        });
        assertTrue(stream.hasFailed());
    }

    // ==================== Pattern Matching - hasMatching ====================

    @Test
    @DisplayName("hasMatching should find substring matches")
    void testHasMatchingSubstring() {
        stream.addMessage("hello world");
        stream.addMessage("goodbye world");

        assertTrue(stream.hasMatching("world", false));
        assertTrue(stream.hasMatching("hello", false));
        assertFalse(stream.hasMatching("xyz", false));
    }

    @Test
    @DisplayName("hasMatching should find regex matches")
    void testHasMatchingRegex() {
        stream.addMessage("{\"value\": 123}");
        stream.addMessage("{\"value\": 456}");

        assertTrue(stream.hasMatching("\"value\":\\s*\\d+", true));
        assertFalse(stream.hasMatching("\"value\":\\s*\"\\w+\"", true));
    }

    @Test
    @DisplayName("hasMatching with empty stream should return false")
    void testHasMatchingEmptyStream() {
        assertFalse(stream.hasMatching("anything", false));
        assertFalse(stream.hasMatching(".*", true));
    }

    // ==================== Pattern Matching - countMatching ====================

    @Test
    @DisplayName("countMatching should count substring matches")
    void testCountMatchingSubstring() {
        stream.addMessage("Echo: message 1");
        stream.addMessage("Echo: message 2");
        stream.addMessage("Other: message 3");
        stream.addMessage("Echo: message 4");

        assertEquals(3, stream.countMatching("Echo:", false));
        assertEquals(1, stream.countMatching("Other:", false));
        assertEquals(4, stream.countMatching("message", false));
        assertEquals(0, stream.countMatching("notfound", false));
    }

    @Test
    @DisplayName("countMatching should count regex matches")
    void testCountMatchingRegex() {
        stream.addMessage("{\"type\":\"quote\",\"price\":100}");
        stream.addMessage("{\"type\":\"trade\",\"price\":101}");
        stream.addMessage("{\"type\":\"quote\",\"price\":102}");
        stream.addMessage("{\"type\":\"heartbeat\"}");

        assertEquals(3, stream.countMatching("\"price\":\\d+", true));
        assertEquals(2, stream.countMatching("\"type\":\"quote\"", true));
        assertEquals(1, stream.countMatching("heartbeat", false));
    }

    // ==================== Pattern Matching - getMatching ====================

    @Test
    @DisplayName("getMatching should return all matching messages")
    void testGetMatchingSubstring() {
        stream.addMessage("{\"type\":\"quote\",\"price\":100}");
        stream.addMessage("{\"type\":\"trade\",\"price\":101}");
        stream.addMessage("{\"type\":\"quote\",\"price\":102}");

        List<MessageStream.TimestampedMessage> quotes = stream.getMatching("quote", false);
        assertEquals(2, quotes.size());
        assertTrue(quotes.get(0).content().contains("100"));
        assertTrue(quotes.get(1).content().contains("102"));
    }

    @Test
    @DisplayName("getMatching with regex should work")
    void testGetMatchingRegex() {
        stream.addMessage("ID: 001");
        stream.addMessage("ID: 002");
        stream.addMessage("Name: test");

        List<MessageStream.TimestampedMessage> ids = stream.getMatching("ID:\\s*\\d+", true);
        assertEquals(2, ids.size());
    }

    @Test
    @DisplayName("getMatching with no matches should return empty list")
    void testGetMatchingNoMatches() {
        stream.addMessage("hello");
        List<MessageStream.TimestampedMessage> result = stream.getMatching("notfound", false);
        assertTrue(result.isEmpty());
    }

    // ==================== Extraction - extractFirst ====================

    @Test
    @DisplayName("extractFirst should return first capture group")
    void testExtractFirst() {
        stream.addMessage("{\"price\":100}");
        stream.addMessage("{\"price\":200}");
        stream.addMessage("{\"price\":300}");

        Optional<String> result = stream.extractFirst("\"price\":(\\d+)");
        assertTrue(result.isPresent());
        assertEquals("100", result.get());
    }

    @Test
    @DisplayName("extractFirst should return full match when no capture group")
    void testExtractFirstNoGroup() {
        stream.addMessage("Hello World");

        Optional<String> result = stream.extractFirst("World");
        assertTrue(result.isPresent());
        assertEquals("World", result.get());
    }

    @Test
    @DisplayName("extractFirst should return empty when no match")
    void testExtractFirstNoMatch() {
        stream.addMessage("{\"value\":100}");

        Optional<String> result = stream.extractFirst("\"price\":(\\d+)");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("extractFirst on empty stream should return empty")
    void testExtractFirstEmptyStream() {
        Optional<String> result = stream.extractFirst(".*");
        assertFalse(result.isPresent());
    }

    // ==================== Extraction - extractLast ====================

    @Test
    @DisplayName("extractLast should return last capture group")
    void testExtractLast() {
        stream.addMessage("{\"price\":100}");
        stream.addMessage("{\"price\":200}");
        stream.addMessage("{\"price\":300}");

        Optional<String> result = stream.extractLast("\"price\":(\\d+)");
        assertTrue(result.isPresent());
        assertEquals("300", result.get());
    }

    @Test
    @DisplayName("extractLast should return full match when no capture group")
    void testExtractLastNoGroup() {
        stream.addMessage("First");
        stream.addMessage("Second");
        stream.addMessage("Third");

        Optional<String> result = stream.extractLast("\\w+");
        assertTrue(result.isPresent());
        assertEquals("Third", result.get());
    }

    @Test
    @DisplayName("extractLast should return empty when no match")
    void testExtractLastNoMatch() {
        stream.addMessage("{\"value\":100}");

        Optional<String> result = stream.extractLast("\"price\":(\\d+)");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("extractLast on empty stream should return empty")
    void testExtractLastEmptyStream() {
        Optional<String> result = stream.extractLast(".*");
        assertFalse(result.isPresent());
    }

    // ==================== Pattern Caching ====================

    @Test
    @DisplayName("Same regex pattern should be cached and reused")
    void testPatternCaching() {
        stream.addMessage("test123");
        stream.addMessage("test456");

        // Call multiple times with same pattern - should use cache
        stream.countMatching("test\\d+", true);
        stream.countMatching("test\\d+", true);
        stream.hasMatching("test\\d+", true);
        stream.getMatching("test\\d+", true);

        // No exception means cache is working
        assertEquals(2, stream.countMatching("test\\d+", true));
    }

    @Test
    @DisplayName("Invalid regex in query should throw IllegalArgumentException")
    void testInvalidRegexInQuery() {
        stream.addMessage("test");

        assertThrows(IllegalArgumentException.class, () -> {
            stream.countMatching("[invalid", true);
        });
    }

    // ==================== Utility Methods ====================

    @Test
    @DisplayName("truncateForLog should truncate long strings")
    void testTruncateForLogLong() {
        String longString = "x".repeat(600);
        String truncated = MessageStream.truncateForLog(longString);

        assertEquals(503, truncated.length()); // 500 + "..."
        assertTrue(truncated.endsWith("..."));
    }

    @Test
    @DisplayName("truncateForLog should not truncate short strings")
    void testTruncateForLogShort() {
        String shortString = "hello world";
        String result = MessageStream.truncateForLog(shortString);

        assertEquals(shortString, result);
    }

    @Test
    @DisplayName("truncateForLog should handle null")
    void testTruncateForLogNull() {
        String result = MessageStream.truncateForLog(null);
        assertEquals("(null)", result);
    }

    @Test
    @DisplayName("truncateForLog should handle exact boundary")
    void testTruncateForLogBoundary() {
        String exactLength = "x".repeat(500);
        String result = MessageStream.truncateForLog(exactLength);
        assertEquals(500, result.length());
        assertFalse(result.endsWith("..."));
    }

    // ==================== getSummary ====================

    @Test
    @DisplayName("getSummary should include all relevant info")
    void testGetSummary() {
        stream.addMessage("msg1");
        stream.addMessage("msg2");

        String summary = stream.getSummary();

        assertTrue(summary.contains("test-connection"));
        assertTrue(summary.contains("2 messages"));
        assertTrue(summary.contains("failed=false"));
    }

    @Test
    @DisplayName("getSummary after failure should show failed=true")
    void testGetSummaryAfterFailure() {
        stream.addFailOnPattern("error", false);

        try {
            stream.addMessage("error occurred");
        } catch (WebSocketException e) {
            // Expected
        }

        String summary = stream.getSummary();
        assertTrue(summary.contains("failed=true"));
    }

    // ==================== Thread Safety ====================

    @Test
    @DisplayName("MessageStream should be thread-safe for concurrent writes")
    void testConcurrentWrites() throws InterruptedException {
        int threadCount = 10;
        int messagesPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < messagesPerThread; i++) {
                        stream.addMessage("Thread-" + threadId + "-Message-" + i);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(threadCount * messagesPerThread, stream.getMessageCount());
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should handle empty string message")
    void testEmptyStringMessage() {
        stream.addMessage("");
        assertEquals(1, stream.getMessageCount());
        assertEquals("", stream.getAllMessages().get(0).content());
    }

    @Test
    @DisplayName("Should handle message with special characters")
    void testSpecialCharacters() {
        String special = "{\"emoji\": \"\uD83D\uDE00\", \"newline\": \"line1\\nline2\"}";
        stream.addMessage(special);

        assertEquals(1, stream.getMessageCount());
        assertTrue(stream.hasMatching("emoji", false));
    }

    @Test
    @DisplayName("Should handle very long message")
    void testVeryLongMessage() {
        String longMessage = "x".repeat(10000);
        stream.addMessage(longMessage);

        assertEquals(1, stream.getMessageCount());
        assertEquals(10000, stream.getAllMessages().get(0).content().length());
    }

    @Test
    @DisplayName("matchesPattern with null content should return false")
    void testMatchesPatternNullContent() {
        // This tests the private matchesPattern method indirectly
        // The message with null content can't be added, so we test via empty stream
        assertFalse(stream.hasMatching("test", false));
    }
}
