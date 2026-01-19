package com.intuit.tank.httpclientjdk;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * Netty-based WebSocket client
 * 
 * @author Zak Kofiro
 */
public class TankWebSocketClient {
    
    private static final Logger LOG = LogManager.getLogger(TankWebSocketClient.class);
    
    private final EventLoopGroup workerGroup;
    private final boolean shutdownGroupOnDisconnect;
    
    // Connection state
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicReference<Channel> channel = new AtomicReference<>();
    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();
    
    // Passive listening for server-pushed messages (legacy - kept for backward compatibility)
    private final ConcurrentLinkedQueue<CompletableFuture<String>> awaitNextMessageFutures = new ConcurrentLinkedQueue<>();
    
    // Pattern-based message matching infrastructure
    private static final int MAX_BUFFER_SIZE = 1000;
    private final ConcurrentLinkedDeque<String> messageBuffer = new ConcurrentLinkedDeque<>();
    private final CopyOnWriteArrayList<PendingExpect> pendingExpects = new CopyOnWriteArrayList<>();
    
    /**
     * Represents a pending EXPECT operation waiting for a message matching its predicate
     */
    public record PendingExpect(
        Predicate<String> matcher,
        CompletableFuture<String> future,
        long createdAt
    ) {
        public PendingExpect(Predicate<String> matcher, CompletableFuture<String> future) {
            this(matcher, future, System.currentTimeMillis());
        }
    }
    
    // Configuration
    private final URI uri;
    private final boolean ssl;
    private final String host;
    private final int port;
    private final String path;
    
    // Performance monitoring
    private volatile long connectionStartTime;
    private volatile long messagesReceived = 0;
    private volatile long messagesSent = 0;
    
    /**
     * Constructor with dependency injection for EventLoopGroup
     * @param url WebSocket URL
     * @param workerGroup EventLoopGroup for connection management
     * @param shutdownGroupOnDisconnect Whether this client should shutdown the EventLoopGroup on disconnect
     */
    public TankWebSocketClient(String url, EventLoopGroup workerGroup, boolean shutdownGroupOnDisconnect) {
        try {
            this.uri = URI.create(url);
            this.ssl = "wss".equalsIgnoreCase(uri.getScheme());
            this.host = uri.getHost();
            this.port = uri.getPort() != -1 ? uri.getPort() : (ssl ? 443 : 80);
            this.path = uri.getPath().isEmpty() ? "/" : uri.getPath();
            this.workerGroup = workerGroup;
            this.shutdownGroupOnDisconnect = shutdownGroupOnDisconnect;
            
            LOG.debug("Initialized WebSocket client for {}://{}:{}{}", 
                ssl ? "wss" : "ws", host, port, path);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid WebSocket URL: " + url, e);
        }
    }
    
    /**
     * Convenience constructor that creates its own EventLoopGroup
     * @param url WebSocket URL
     */
    public TankWebSocketClient(String url) {
        this(url, new NioEventLoopGroup(Math.max(1, Runtime.getRuntime().availableProcessors() * 2)), true);
    }
    
    /**
     * Connect to WebSocket server with high performance
     * @return CompletableFuture<Boolean> that completes when connection is established
     */
    public CompletableFuture<Boolean> connect() {
        return connect(30000);
    }
    
    /**
     * Connect with custom timeout
     * @param timeoutMs Connection timeout in milliseconds
     * @return CompletableFuture<Boolean> that completes when connection is established
     */
    public CompletableFuture<Boolean> connect(int timeoutMs) {
        if (connected.get()) {
            return CompletableFuture.completedFuture(true);
        }
        
        connectionStartTime = System.nanoTime();
        LOG.info("Connecting to WebSocket server: {}://{}:{}{}", 
            ssl ? "wss" : "ws", host, port, path);
        
        CompletableFuture<Boolean> connectionFuture = new CompletableFuture<>();
        
        try {
            // Create SSL context if needed (make final for lambda)
            final SslContext finalSslCtx = ssl ? 
                SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE) // For testing - use proper certs in production
                    .build() : null;
            
            // Make variables final for lambda
            final String finalHost = this.host;
            final int finalPort = this.port;
            
            // WebSocket handshaker
            WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
            );
            
            // Bootstrap configuration for high performance
            Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMs)
                .option(ChannelOption.SO_RCVBUF, 65536) // 64KB receive buffer
                .option(ChannelOption.SO_SNDBUF, 65536) // 64KB send buffer
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        // SSL handler (if needed)
                        if (finalSslCtx != null) {
                            pipeline.addLast(finalSslCtx.newHandler(ch.alloc(), finalHost, finalPort));
                        }
                        
                        // HTTP codec
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        
                        // Idle state handler for connection health
                        pipeline.addLast(new IdleStateHandler(60, 30, 0)); // Read: 60s, Write: 30s
                        
                        // Our WebSocket handler
                        pipeline.addLast(new WebSocketClientHandler(handshaker, connectionFuture));
                    }
                });
            
            // Connect asynchronously
            ChannelFuture connectFuture = bootstrap.connect(finalHost, finalPort);
            connectFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    channel.set(future.channel());
                    LOG.debug("TCP connection established to {}:{}", finalHost, finalPort);
                } else {
                    LOG.error("Failed to connect to {}:{}", finalHost, finalPort, future.cause());
                    connectionFuture.completeExceptionally(future.cause());
                }
            });
            
        } catch (Exception e) {
            LOG.error("Error setting up WebSocket connection", e);
            connectionFuture.completeExceptionally(e);
        }
        
        return connectionFuture;
    }
    
    /**
     * Send message and wait for response (with proper correlation for concurrency)
     * @param message Message to send
     * @return CompletableFuture<String> with the response
     */
    public CompletableFuture<String> sendAndAwaitResponse(String message) {
        return sendAndAwaitResponse(message, 5000); // 5 second timeout
    }
    
    /**
     * Send message and wait for response with custom timeout
     * Handles concurrent requests properly using correlation IDs
     * @param message Message to send
     * @param timeoutMs Response timeout in milliseconds
     * @return CompletableFuture<String> with the response
     */
    public CompletableFuture<String> sendAndAwaitResponse(String message, int timeoutMs) {
        if (!connected.get()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("WebSocket not connected"));
        }
        
        // Generate unique correlation ID for this request
        String correlationId = java.util.UUID.randomUUID().toString();
        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        
        // Store the future with correlation ID
        pendingRequests.put(correlationId, responseFuture);
        
        // Create message with correlation ID (for echo servers, we'll parse it back)
        String correlatedMessage = correlationId + ":" + message;
        
        // Send message
        Channel ch = channel.get();
        if (ch != null && ch.isActive()) {
            WebSocketFrame frame = new TextWebSocketFrame(correlatedMessage);
            ch.writeAndFlush(frame).addListener(future -> {
                if (future.isSuccess()) {
                    messagesSent++;
                    LOG.info("[WebSocket SENT]: \"{}\"", message);
                    LOG.debug("Sent correlated message: {} (ID: {})", message, correlationId);
                } else {
                    LOG.error("Failed to send message: {} (ID: {})", message, correlationId, future.cause());
                    // Remove from pending and fail the future
                    CompletableFuture<String> failedFuture = pendingRequests.remove(correlationId);
                    if (failedFuture != null) {
                        failedFuture.completeExceptionally(future.cause());
                    }
                }
            });
        } else {
            pendingRequests.remove(correlationId);
            responseFuture.completeExceptionally(
                new IllegalStateException("Channel not active"));
        }
        
        // Set timeout using CompletableFuture.orTimeout (Java 9+) - race-free!
        responseFuture.orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .whenComplete((result, throwable) -> {
                if (throwable instanceof TimeoutException) {
                    // Clean up on timeout
                    pendingRequests.remove(correlationId);
                    LOG.warn("Request timeout for message: {} (ID: {})", message, correlationId);
                }
            });
        
        return responseFuture;
    }
    
    /**
     * Send message without waiting for response
     * @param message Message to send
     * @return CompletableFuture<Void> that completes when message is sent
     */
    public CompletableFuture<Void> sendMessage(String message) {
        if (!connected.get()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("WebSocket not connected"));
        }
        
        CompletableFuture<Void> sendFuture = new CompletableFuture<>();
        
        Channel ch = channel.get();
        if (ch != null && ch.isActive()) {
            WebSocketFrame frame = new TextWebSocketFrame(message);
            ch.writeAndFlush(frame).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    messagesSent++;
                    LOG.info("[WebSocket SENT]: \"{}\"", message);
                    LOG.debug("Sent message: {}", message);  // Keep for backward compatibility
                    sendFuture.complete(null);
                } else {
                    LOG.error("Failed to send message: {}", message, future.cause());
                    sendFuture.completeExceptionally(future.cause());
                }
            });
        } else {
            sendFuture.completeExceptionally(
                new IllegalStateException("Channel not active"));
        }
        
        return sendFuture;
    }
    
    /**
     * Passively await the next message from the server (for EXPECT actions)
     * This does NOT send anything - it just listens for server-pushed messages
     * 
     * @param timeoutMs Timeout in milliseconds
     * @return CompletableFuture<String> that completes when next message arrives
     */
    public CompletableFuture<String> awaitNextMessage(int timeoutMs) {
        if (!connected.get()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("WebSocket not connected"));
        }
        
        // Check if there's a buffered message first
        String bufferedMessage = messageBuffer.poll();
        if (bufferedMessage != null) {
            LOG.debug("Returning buffered message: {}", bufferedMessage);
            return CompletableFuture.completedFuture(bufferedMessage);
        }
        
        // Create future for next message
        CompletableFuture<String> messageFuture = new CompletableFuture<>();
        awaitNextMessageFutures.add(messageFuture);
        
        // Set timeout
        workerGroup.next().schedule(() -> {
            if (!messageFuture.isDone()) {
                awaitNextMessageFutures.remove(messageFuture);
                messageFuture.completeExceptionally(
                    new TimeoutException("No message received within " + timeoutMs + "ms"));
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);
        
        LOG.debug("Awaiting next server message with {}ms timeout", timeoutMs);
        return messageFuture;
    }
    
    /**
     * Wait for a message matching the given predicate (for EXPECT actions).
     * This is the preferred method for pattern-based message matching.
     *
     * First checks the existing message buffer for a match, then registers
     * a pending expect if no match is found.
     *
     * @param matcher Predicate to test incoming messages against
     * @param timeoutMs Timeout in milliseconds
     * @return CompletableFuture<String> that completes when a matching message arrives
     */
    public CompletableFuture<String> awaitMessage(Predicate<String> matcher, int timeoutMs) {
        if (!connected.get()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("WebSocket not connected"));
        }

        // 1. Check existing buffer first (FIFO scan)
        synchronized (messageBuffer) {
            for (Iterator<String> it = messageBuffer.iterator(); it.hasNext();) {
                String msg = it.next();
                if (matcher.test(msg)) {
                    it.remove();
                    LOG.debug("Found matching message in buffer: {}", msg);
                    return CompletableFuture.completedFuture(msg);
                }
            }
        }

        // 2. No match in buffer - register for future delivery
        CompletableFuture<String> future = new CompletableFuture<>();
        PendingExpect pending = new PendingExpect(matcher, future);
        pendingExpects.add(pending);

        LOG.debug("Registered pending EXPECT with {}ms timeout ({} pending total)",
            timeoutMs, pendingExpects.size());

        // 3. Set timeout - clean up on expiration
        future.orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .whenComplete((result, throwable) -> {
                pendingExpects.remove(pending);
                if (throwable instanceof TimeoutException) {
                    LOG.warn("EXPECT timeout after {}ms, no matching message received", timeoutMs);
                }
            });

        return future;
    }

    /**
     * Convenience method: await message containing a specific substring
     */
    public CompletableFuture<String> awaitMessageContaining(String substring, int timeoutMs) {
        return awaitMessage(msg -> msg.contains(substring), timeoutMs);
    }

    /**
     * Convenience method: await message matching a regex pattern
     */
    public CompletableFuture<String> awaitMessageMatching(String regex, int timeoutMs) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        return awaitMessage(msg -> pattern.matcher(msg).find(), timeoutMs);
    }
    
        /**
         * Disconnect from WebSocket server
         */
        public void disconnect() {
            Channel ch = channel.get();
            if (ch != null && ch.isActive()) {
                LOG.info("Disconnecting WebSocket...");
                ch.writeAndFlush(new CloseWebSocketFrame()).addListener(ChannelFutureListener.CLOSE);
            }
            connected.set(false);
            
            // Shutdown EventLoopGroup if this client owns it
            if (shutdownGroupOnDisconnect) {
                workerGroup.shutdownGracefully();
            }
        }
    
    /**
     * Check if WebSocket is connected
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        Channel ch = channel.get();
        return connected.get() && ch != null && ch.isActive();
    }
    
    /**
     * Get performance statistics
     * @return Performance info string
     */
    public String getPerformanceStats() {
        long connectionTime = connectionStartTime > 0 ? 
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - connectionStartTime) : 0;
        
        return String.format("WebSocket Stats: Connected=%s, ConnectionTime=%dms, " +
                           "MessagesSent=%d, MessagesReceived=%d, Channel=%s",
            connected.get(), connectionTime, messagesSent, messagesReceived,
            channel.get() != null ? "Active" : "Inactive");
    }
    
    /**
     * WebSocket Client Handler - handles the WebSocket protocol
     */
    private class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
        
        private final WebSocketClientHandshaker handshaker;
        private final CompletableFuture<Boolean> connectionFuture;
        private ChannelPromise handshakeFuture;
        
        public WebSocketClientHandler(WebSocketClientHandshaker handshaker, 
                                    CompletableFuture<Boolean> connectionFuture) {
            this.handshaker = handshaker;
            this.connectionFuture = connectionFuture;
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            LOG.debug("Channel active, starting WebSocket handshake");
            handshakeFuture = ctx.newPromise();
            handshaker.handshake(ctx.channel());
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            LOG.debug("WebSocket channel inactive");
            connected.set(false);
        }
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            Channel ch = ctx.channel();
            
            if (!handshaker.isHandshakeComplete()) {
                try {
                    handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                    connected.set(true);
                    
                    long connectionTime = TimeUnit.NANOSECONDS.toMillis(
                        System.nanoTime() - connectionStartTime);
                    LOG.info("WebSocket handshake complete in {}ms", connectionTime);
                    connectionFuture.complete(true);
                    
                } catch (WebSocketHandshakeException e) {
                    LOG.error("WebSocket handshake failed", e);
                    connectionFuture.completeExceptionally(e);
                }
                return;
            }
            
            // ASYNCHRONOUS PASSIVE LISTENING:
            // This handler is attached during CONNECT and remains active for the life of the connection.
            // All incoming messages are immediately:
            // 1. Logged to the debugger (for demo/monitoring visibility)
            // 2. Queued in messageBuffer or delivered to awaitNextMessage futures (for EXPECT steps)
            // This is thread-safe as it executes within the Netty event loop.
            //
            // TODO: REPLACE THIS TO HANDLE MORE THAN ECHO SERVERS (Binary, Ping handling, etc)
            // This will require a new class to handle the different types of frames
            if (msg instanceof WebSocketFrame) {
                WebSocketFrame frame = (WebSocketFrame) msg;
                
                if (frame instanceof TextWebSocketFrame) {
                    String text = ((TextWebSocketFrame) frame).text();
                    messagesReceived++;
                    
                    // Log prominently for Tank Debugger visibility
                    LOG.info("[WebSocket RECEIVED]: \"{}\"", text);
                    
                    // Route through centralized message handler
                    handleIncomingMessage(text);
                    
                } else if (frame instanceof CloseWebSocketFrame) {
                    LOG.info("WebSocket close frame received");
                    ch.close();
                    
                } else if (frame instanceof PongWebSocketFrame) {
                    LOG.debug("Pong frame received");
                }
            }
        }
        
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent idleEvent = (IdleStateEvent) evt;
                if (idleEvent.state() == io.netty.handler.timeout.IdleState.WRITER_IDLE) {
                    // Send ping frame to keep connection alive
                    LOG.debug("Sending ping frame");
                    ctx.writeAndFlush(new PingWebSocketFrame());
                }
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOG.error("WebSocket exception", cause);
            
            if (!connectionFuture.isDone()) {
                connectionFuture.completeExceptionally(cause);
            }
            
            // Complete all pending requests with error
            pendingRequests.values().forEach(future -> {
                if (!future.isDone()) {
                    future.completeExceptionally(cause);
                }
            });
            pendingRequests.clear();
            
            ctx.close();
        }
    }
    
    /**
     * Get the number of pending requests (for monitoring)
     * @return Number of requests waiting for response
     */
    public int getPendingRequestCount() {
        return pendingRequests.size();
    }
    
    /**
     * Get the number of pending EXPECT operations
     * @return Number of expects waiting for matching messages
     */
    public int getPendingExpectCount() {
        return pendingExpects.size();
    }
    
    /**
     * Get the current message buffer size
     * @return Number of unmatched messages in buffer
     */
    public int getBufferedMessageCount() {
        return messageBuffer.size();
    }
    
    /**
     * Central message routing handler.
     * Routes incoming messages through this priority order:
     * 1. Correlation-based responses (for sendAndAwaitResponse - echo server compatibility)
     * 2. Pattern-based EXPECT matching (new)
     * 3. Legacy awaitNextMessage futures (backward compatibility)
     * 4. Buffer for future retrieval
     *
     * @param text The incoming message text
     */
    private void handleIncomingMessage(String text) {
        // Priority 1: Check for correlated responses (sendAndAwaitResponse with echo servers)
        if (text.startsWith("Echo: ")) {
            String echoContent = text.substring(6);
            int colonIndex = echoContent.indexOf(':');
            if (colonIndex > 0) {
                String correlationId = echoContent.substring(0, colonIndex);
                CompletableFuture<String> responseFuture = pendingRequests.remove(correlationId);
                if (responseFuture != null) {
                    String originalMessage = echoContent.substring(colonIndex + 1);
                    responseFuture.complete("Echo: " + originalMessage);
                    LOG.debug("Completed correlated response for ID: {}", correlationId);
                    return;
                }
            }
        }

        // Priority 2: Check pending EXPECTs for pattern match (new pattern-matching system)
        for (PendingExpect pending : pendingExpects) {
            try {
                if (pending.matcher().test(text)) {
                    pendingExpects.remove(pending);
                    pending.future().complete(text);
                    LOG.debug("Delivered message to pending EXPECT: {}", text);
                    return;
                }
            } catch (Exception e) {
                LOG.warn("Error testing message against EXPECT predicate: {}", e.getMessage());
            }
        }

        // Priority 3: Legacy awaitNextMessage support (backward compatibility)
        CompletableFuture<String> legacyFuture = awaitNextMessageFutures.poll();
        if (legacyFuture != null) {
            legacyFuture.complete(text);
            LOG.debug("Delivered message to legacy awaitNextMessage listener: {}", text);
            return;
        }

        // Priority 4: Buffer for future retrieval
        synchronized (messageBuffer) {
            messageBuffer.addLast(text);
            // Prevent unbounded growth - remove oldest messages if over limit
            while (messageBuffer.size() > MAX_BUFFER_SIZE) {
                String dropped = messageBuffer.pollFirst();
                LOG.warn("Message buffer overflow, dropped oldest message: {}",
                    dropped != null ? dropped.substring(0, Math.min(50, dropped.length())) + "..." : "null");
            }
        }
        LOG.debug("Buffered message for future EXPECT ({} buffered): {}",
            messageBuffer.size(), text);
    }
}
