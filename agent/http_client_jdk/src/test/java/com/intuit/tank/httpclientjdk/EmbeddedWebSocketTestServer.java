package com.intuit.tank.httpclientjdk;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Embedded WebSocket Test Server for Isolated Testing
 * 
 * This eliminates the need for external WebSocket servers in unit tests.
 * Features:
 * - Echo mode: Returns "Echo: " + message
 * - Configurable behavior (delays, errors, etc.)
 * - Thread-safe and lightweight
 * - Automatic port selection (no port conflicts)
 * 
 * @author Zak Kofiro
 */
public class EmbeddedWebSocketTestServer {
    
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int port;
    private final CountDownLatch startLatch = new CountDownLatch(1);
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    private final AtomicInteger messageCount = new AtomicInteger(0);
    
    // Configurable behavior
    private volatile boolean echoEnabled = true;
    private volatile int responseDelayMs = 0;
    private volatile boolean failNextConnection = false;
    private volatile boolean dropNextMessage = false;
    
    public EmbeddedWebSocketTestServer() {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(2);
    }
    
    /**
     * Start the server on an available port
     * @return The port the server is listening on
     */
    public int start() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new HttpObjectAggregator(65536));
                    pipeline.addLast(new WebSocketServerHandler());
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
        
        // Bind to any available port
        ChannelFuture future = bootstrap.bind(0).sync();
        serverChannel = future.channel();
        
        // Get the actual port
        InetSocketAddress addr = (InetSocketAddress) serverChannel.localAddress();
        this.port = addr.getPort();
        
        // Signal that server is ready
        startLatch.countDown();
        
        System.out.println("🚀 Test WebSocket Server started on port: " + port);
        return port;
    }
    
    /**
     * Stop the server gracefully
     */
    public void stop() {
        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("🛑 Test WebSocket Server stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Wait for server to be ready
     */
    public boolean awaitStartup(long timeout, TimeUnit unit) throws InterruptedException {
        return startLatch.await(timeout, unit);
    }
    
    /**
     * Get the WebSocket URL for this server
     */
    public String getWebSocketUrl() {
        return "ws://localhost:" + port + "/ws/test";
    }
    
    // Test control methods
    public void setEchoEnabled(boolean enabled) {
        this.echoEnabled = enabled;
    }
    
    public void setResponseDelayMs(int delayMs) {
        this.responseDelayMs = delayMs;
    }
    
    public void failNextConnection() {
        this.failNextConnection = true;
    }
    
    public void dropNextMessage() {
        this.dropNextMessage = true;
    }
    
    public int getConnectionCount() {
        return connectionCount.get();
    }
    
    public int getMessageCount() {
        return messageCount.get();
    }
    
    /**
     * Reset server state for clean test isolation
     */
    public void resetState() {
        connectionCount.set(0);
        messageCount.set(0);
        echoEnabled = true;
        responseDelayMs = 0;
        failNextConnection = false;
        dropNextMessage = false;
    }
    
    /**
     * WebSocket Handler - Handles the test server logic
     */
    private class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
        
        private WebSocketServerHandshaker handshaker;
        
        @Override
        public void channelRead0(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
        }
        
        private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
            // Handle WebSocket handshake
            if (!req.decoderResult().isSuccess()) {
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }
            
            // Simulate connection failure if configured
            if (failNextConnection) {
                failNextConnection = false;
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.SERVICE_UNAVAILABLE));
                ctx.close();
                return;
            }
            
            // Handshake
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true, 5 * 1024 * 1024);
            handshaker = wsFactory.newHandshaker(req);
            
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
                connectionCount.incrementAndGet();
            }
        }
        
        private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
            // Handle close frame
            if (frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }
            
            // Handle ping frame
            if (frame instanceof PingWebSocketFrame) {
                ctx.write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            
            // Handle text frame (our main logic)
            if (frame instanceof TextWebSocketFrame) {
                String request = ((TextWebSocketFrame) frame).text();
                messageCount.incrementAndGet();
                
                // Simulate dropping message if configured
                if (dropNextMessage) {
                    dropNextMessage = false;
                    return; // Don't respond
                }
                
                // Simulate delay if configured
                if (responseDelayMs > 0) {
                    try {
                        Thread.sleep(responseDelayMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                // Echo the message if enabled
                if (echoEnabled) {
                    String response = "Echo: " + request;
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(response));
                }
            }
        }
        
        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, 
                                     FullHttpResponse res) {
            if (res.status().code() != 200) {
                res.content().writeBytes(res.status().toString().getBytes(CharsetUtil.UTF_8));
                HttpUtil.setContentLength(res, res.content().readableBytes());
            }
            
            ChannelFuture f = ctx.channel().writeAndFlush(res);
            if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }
        
        private String getWebSocketLocation(FullHttpRequest req) {
            String location = req.headers().get(HttpHeaderNames.HOST) + "/ws/test";
            return "ws://" + location;
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
