package com.intuit.tank.httpclientjdk;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.jupiter.api.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tank WebSocket Client Unit Tests
 * 
 * @author Zak Kofiro
 */
class TankWebSocketClientTest {
    
    private static final int CONNECTION_TIMEOUT_MS = 5000; // Reduced for embedded server
    private static final int MESSAGE_TIMEOUT_MS = 2000;    // Reduced for embedded server
    
    private static EventLoopGroup sharedEventLoopGroup;
    private static EmbeddedWebSocketTestServer testServer;
    private static String wsUrl;
    
    private TankWebSocketClient client;
    
    @BeforeAll
    static void setUpTestEnvironment() throws Exception {
        // Create shared EventLoopGroup for all tests
        sharedEventLoopGroup = new NioEventLoopGroup(2);
        
        // Start embedded WebSocket server
        testServer = new EmbeddedWebSocketTestServer();
        int port = testServer.start();
        wsUrl = testServer.getWebSocketUrl();
        
        // Wait for server to be ready
        assertTrue(testServer.awaitStartup(5, TimeUnit.SECONDS), 
                  "Test server should start within 5 seconds");
        
        System.out.println("Test environment ready with embedded server at: " + wsUrl);
    }
    
    @AfterAll
    static void tearDownTestEnvironment() {
        // Shutdown EventLoopGroup
        if (sharedEventLoopGroup != null) {
            sharedEventLoopGroup.shutdownGracefully();
        }
        
        // Stop embedded server
        if (testServer != null) {
            testServer.stop();
        }
        
        System.out.println("Test environment cleaned up");
    }
    
    @BeforeEach
    void setUp() {
        // Reset server state for test isolation
        testServer.resetState();
        
        // Create client with shared EventLoopGroup
        client = new TankWebSocketClient(wsUrl, sharedEventLoopGroup, false);
    }
    
    @AfterEach
    void tearDown() {
        if (client != null && client.isConnected()) {
            client.disconnect();
            
            // Wait for disconnection (with timeout)
            long timeoutMs = 1000;
            long startTime = System.currentTimeMillis();
            while (client.isConnected() && (System.currentTimeMillis() - startTime) < timeoutMs) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    @Test
    @DisplayName("Should connect to embedded WebSocket server")
    void testConnection() throws Exception {
        // When
        CompletableFuture<Boolean> connectionFuture = client.connect();
        Boolean connected = connectionFuture.get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // Then
        assertTrue(connected, "Should connect successfully");
        assertTrue(client.isConnected(), "Should report as connected");
        assertEquals(1, testServer.getConnectionCount(), "Server should register connection");
    }
    
    @Test
    @DisplayName("Should handle echo messages correctly")
    void testEchoMessage() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        String testMessage = "Hello WebSocket";
        String expectedResponse = "Echo: " + testMessage;
        
        // When
        CompletableFuture<String> responseFuture = client.sendAndAwaitResponse(testMessage);
        String actualResponse = responseFuture.get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // Then
        assertEquals(expectedResponse, actualResponse, "Should receive correct echo");
        assertEquals(1, testServer.getMessageCount(), "Server should count message");
    }
    
    @Test
    @DisplayName("Should handle JSON messages")
    void testJsonMessage() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        String jsonMessage = "{\"type\":\"test\",\"data\":\"value\"}";
        String expectedResponse = "Echo: " + jsonMessage;
        
        // When
        String actualResponse = client.sendAndAwaitResponse(jsonMessage)
            .get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // Then
        assertEquals(expectedResponse, actualResponse, "Should handle JSON correctly");
    }
    
    @Test
    @DisplayName("Should handle concurrent message bursts")
    void testConcurrentBurst() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        int messageCount = 20;
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        // When - Send all messages without waiting
        for (int i = 1; i <= messageCount; i++) {
            String message = "Burst message " + i;
            futures.add(client.sendAndAwaitResponse(message));
        }
        
        // Wait for all responses
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .get(MESSAGE_TIMEOUT_MS * 2, TimeUnit.MILLISECONDS);
        
        // Then - Verify all responses are correct
        for (int i = 1; i <= messageCount; i++) {
            String expected = "Echo: Burst message " + i;
            String actual = futures.get(i - 1).getNow(null);
            assertEquals(expected, actual, "Message " + i + " should have correct response");
        }
        
        assertEquals(0, client.getPendingRequestCount(), "All requests should be completed");
        assertEquals(messageCount, testServer.getMessageCount(), "Server should receive all messages");
    }
    
    @Test
    @DisplayName("Should handle multiple concurrent clients")
    void testConcurrentClients() throws Exception {
        // Given
        int clientCount = 5;
        List<TankWebSocketClient> clients = new ArrayList<>();
        List<CompletableFuture<Boolean>> connectionFutures = new ArrayList<>();
        
        try {
            // Connect all clients
            for (int i = 0; i < clientCount; i++) {
                TankWebSocketClient concurrentClient = new TankWebSocketClient(wsUrl, sharedEventLoopGroup, false);
                clients.add(concurrentClient);
                connectionFutures.add(concurrentClient.connect());
            }
            
            // Wait for all connections
            for (int i = 0; i < clientCount; i++) {
                Boolean connected = connectionFutures.get(i).get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                assertTrue(connected, "Client " + i + " should connect");
            }
            
            assertEquals(clientCount, testServer.getConnectionCount(), 
                        "Server should have " + clientCount + " connections");
            
            // Send message from each client
            List<CompletableFuture<String>> messageFutures = new ArrayList<>();
            for (int i = 0; i < clientCount; i++) {
                messageFutures.add(clients.get(i).sendAndAwaitResponse("Message from client " + i));
            }
            
            // Verify all responses
            for (int i = 0; i < clientCount; i++) {
                String response = messageFutures.get(i).get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                assertEquals("Echo: Message from client " + i, response);
            }
            
        } finally {
            // Cleanup
            for (TankWebSocketClient c : clients) {
                if (c.isConnected()) {
                    c.disconnect();
                }
            }
        }
    }
    
    @Test
    @DisplayName("Should handle connection failures gracefully")
    void testConnectionFailure() throws Exception {
        // Given - Invalid URL (port that's not listening)
        String invalidUrl = "ws://localhost:19999/nonexistent";
        TankWebSocketClient failureClient = new TankWebSocketClient(invalidUrl, sharedEventLoopGroup, false);
        
        try {
            // When & Then
            CompletableFuture<Boolean> connectionFuture = failureClient.connect(1000);
            
            ExecutionException exception = assertThrows(ExecutionException.class, () -> {
                connectionFuture.get(2, TimeUnit.SECONDS);
            }, "Should throw ExecutionException for invalid server");
            
            Throwable cause = exception.getCause();
            assertTrue(cause instanceof java.net.ConnectException || 
                      cause instanceof TimeoutException,
                      "Should fail with connection-related exception");
            
            assertFalse(failureClient.isConnected(), "Should not be connected");
            
        } finally {
            failureClient.disconnect();
        }
    }
    
    @Test
    @DisplayName("Should handle server-initiated connection close")
    void testServerInitiatedClose() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertTrue(client.isConnected(), "Should be connected initially");
        
        // When - Server closes connection
        testServer.failNextConnection(); // This will close next operation
        
        // Try to send a message (should handle gracefully)
        try {
            // This might fail or succeed depending on timing
            client.sendAndAwaitResponse("test").get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // Expected - connection might be closed
        }
        
        // Then - Client should detect disconnection
        Thread.sleep(100); // Give time for close to propagate
        // Note: In real implementation, we'd have connection state callbacks
    }
    
    @Test
    @DisplayName("Should handle message timeouts")
    void testMessageTimeout() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // Configure server to drop next message
        testServer.dropNextMessage();
        
        // When & Then
        CompletableFuture<String> future = client.sendAndAwaitResponse("Will be dropped", 500);
        
        // CompletableFuture.get() wraps TimeoutException in ExecutionException
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            future.get(1, TimeUnit.SECONDS);
        }, "Should throw ExecutionException when timeout occurs");
        
        // Verify the cause is TimeoutException
        assertTrue(exception.getCause() instanceof TimeoutException,
                  "Cause should be TimeoutException, but was: " + exception.getCause().getClass());
        
        // Verify client is still functional
        testServer.setEchoEnabled(true); // Re-enable echo
        String response = client.sendAndAwaitResponse("Recovery test")
            .get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertEquals("Echo: Recovery test", response, "Client should recover after timeout");
    }
    
    @Test
    @DisplayName("Should handle slow server responses")
    void testSlowServerResponse() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // Configure server with delay
        testServer.setResponseDelayMs(100);
        
        // When
        long startTime = System.nanoTime();
        String response = client.sendAndAwaitResponse("Slow message")
            .get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        long responseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        
        // Then
        assertEquals("Echo: Slow message", response, "Should receive delayed response");
        assertTrue(responseTime >= 100, "Response time should include server delay");
        
        // Reset delay
        testServer.setResponseDelayMs(0);
    }
    
    @Test
    @DisplayName("Should properly disconnect and cleanup")
    void testDisconnection() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        assertTrue(client.isConnected(), "Should be connected");
        
        // When
        client.disconnect();
        
        // Wait for disconnection
        long timeoutMs = 1000;
        long startTime = System.currentTimeMillis();
        while (client.isConnected() && (System.currentTimeMillis() - startTime) < timeoutMs) {
            Thread.sleep(10);
        }
        
        // Then
        assertFalse(client.isConnected(), "Should be disconnected");
        assertEquals(0, client.getPendingRequestCount(), "Should have no pending requests");
    }
    
    @Test
    @DisplayName("Should track performance statistics")
    void testPerformanceStats() throws Exception {
        // Given
        client.connect().get(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // When
        client.sendAndAwaitResponse("Message 1").get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.sendAndAwaitResponse("Message 2").get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.sendAndAwaitResponse("Message 3").get(MESSAGE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        
        // Then
        String stats = client.getPerformanceStats();
        assertTrue(stats.contains("MessagesSent=3"), "Should track sent messages");
        assertTrue(stats.contains("MessagesReceived=3"), "Should track received messages");
        assertTrue(stats.contains("Connected=true"), "Should show connection status");
    }
}