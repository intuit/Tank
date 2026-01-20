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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    
    // Session-based message collection
    private volatile MessageStream messageStream;
    
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
     * Send message without waiting for response (fire-and-forget)
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
            
            // SESSION-BASED PASSIVE LISTENING:
            // This handler is attached during CONNECT and remains active for the life of the connection.
            // All incoming messages are immediately:
            // 1. Logged for Tank Debugger visibility
            // 2. Routed to MessageStream for collection and fail-on pattern checking
            // 3. Available for ASSERT/DISCONNECT assertions against the full message history
            // This is thread-safe as it executes within the Netty event loop.
            //
            // TODO: Add support for BinaryWebSocketFrame handling
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
            
            ctx.close();
        }
    }
    
    /**
     * Create a MessageStream for this connection to collect all incoming messages.
     * Should be called during CONNECT action.
     * @param connectionId The connection identifier
     * @return The created MessageStream
     */
    public MessageStream createMessageStream(String connectionId) {
        this.messageStream = new MessageStream(connectionId);
        LOG.info("Created MessageStream for WebSocket connection: {}", connectionId);
        return this.messageStream;
    }
    
    /**
     * Get the MessageStream for this connection
     * @return The MessageStream or null if not created
     */
    public MessageStream getMessageStream() {
        return messageStream;
    }
    
    /**
     * Check if the connection has failed due to a fail-on pattern match
     * @return true if failed
     */
    public boolean hasFailed() {
        return messageStream != null && messageStream.hasFailed();
    }
    
    /**
     * Central message routing handler.
     * All incoming messages are routed to MessageStream for:
     * - Fail-on pattern checking (abort test immediately if matched)
     * - Collection for end-of-session assertions
     *
     * @param text The incoming message text
     */
    private void handleIncomingMessage(String text) {
        // Route to MessageStream for session-based collection
        // Messages are checked against fail-on patterns and buffered for assertions
        if (messageStream != null) {
            try {
                messageStream.addMessage(text);
                LOG.debug("Message collected: {}", MessageStream.truncateForLog(text));
            } catch (WebSocketException e) {
                LOG.error("[WebSocket] Connection {} failed on pattern '{}': {}",
                    messageStream.getConnectionId(), e.getPattern(), e.getFailedMessage());
                throw e;
            }
        } else {
            LOG.warn("Message received but no MessageStream attached - message ignored: {}", 
                MessageStream.truncateForLog(text));
        }
    }
}
