package com.intuit.tank.httpclient6.utils;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.ssl.SslContext;

public class TankHttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpProxyHandler httpProxyHandler;
    private final SslContext sslContext;
    private final TankHttpClientHandler httpClientHandler;

    public TankHttpClientInitializer(HttpProxyHandler httpProxyHandler, SslContext sslContext,
                                     TankHttpClientHandler httpClientHandler) {
        this.httpProxyHandler = httpProxyHandler;
        this.sslContext = sslContext;
        this.httpClientHandler = httpClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();

        if (sslContext != null) {
            channelPipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }

        if (httpProxyHandler != null) {
            channelPipeline.addLast(httpProxyHandler);
        }

        channelPipeline.addLast(new HttpClientCodec());

        channelPipeline.addLast(new HttpContentDecompressor());

        channelPipeline.addLast(this.httpClientHandler);
    }
}