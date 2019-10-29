package com.intuit.tank.httpclient6;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.httpclient6.utils.TankHttpClientHandler;
import com.intuit.tank.httpclient6.utils.TankHttpClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TankHttpClient6 implements TankHttpClient {

    private static final Logger LOG = LogManager.getLogger(TankHttpClient6.class);

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private TankHttpClientHandler httpClientHandler;
    private Channel channel;
    private HttpProxyHandler httpProxyHandler;

    private String host;
    private String username;
    private String password;
    private final List<DefaultCookie> cookies;

    public TankHttpClient6() {
        this.cookies = new ArrayList<>();
    }

    @Override
    public Object createHttpClient() {
        this.httpClientHandler = new TankHttpClientHandler();
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class);
        return this.bootstrap;
    }

    @Override
    public void setHttpClient(Object httpClient) {

    }

    @Override
    public void setConnectionTimeout(long connectionTimeout) {
        SocketChannelConfig config = (SocketChannelConfig) this.channel.config();
        config.setConnectTimeoutMillis(Long.valueOf(connectionTimeout).intValue());
    }

    private Pair<String, Integer> getHostPortFromUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String scheme = uri.getScheme() == null ? "http": uri.getScheme();

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            LOG.error("Only HTTP(S) is supported.");
            throw new URISyntaxException("URL is incorrect", "");
        }

        String host = uri.getHost() == null ? "127.0.0.1": uri.getHost();
        int port = uri.getPort();

        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        return Pair.of(host, port);
    }

    private SslContext useSslContext(String url) throws URISyntaxException, SSLException {
        URI uri = new URI(url);
        String scheme = uri.getScheme() == null ? "http": uri.getScheme();
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslContext;
        if (ssl) {
            sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslContext = null;
        }
        return sslContext;
    }

    @Override
    public void doGet(BaseRequest request) {
        try {
            createHttpClient();
            this.httpClientHandler.setRequest(request);
            Pair<String, Integer> hostPortPair = this.getHostPortFromUrl(request.getRequestUrl());

            SslContext sslContext = this.useSslContext(request.getRequestUrl());

            this.bootstrap.handler(new TankHttpClientInitializer(this.httpProxyHandler, sslContext, this.httpClientHandler));

            this.channel = this.bootstrap.connect(hostPortPair.getLeft(), hostPortPair.getRight()).sync().channel();
            this.setConnectionTimeout(60000);

            HttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.GET, new URI(request.getRequestUrl()).getRawPath(), Unpooled.EMPTY_BUFFER);
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            httpRequest.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            if (this.cookies.size() > 0) {
                this.httpClientHandler.setCookieList(this.cookies);
                httpRequest.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.STRICT.encode(this.cookies));
            }

            if (this.host != null) {
                httpRequest.headers().set(HttpHeaderNames.HOST, host);
            } else {
                httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            }
            if (this.username != null && this.password != null) {
                httpRequest.headers().set(HttpHeaderNames.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((this.username + ":" + this.password).getBytes()));
            }

            long waitTime = 0L;
            this.httpClientHandler.setStartTimestamp(waitTime);
            this.channel.writeAndFlush(httpRequest);
            this.channel.closeFuture().sync();
        } catch (URISyntaxException | SSLException | InterruptedException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void doPut(BaseRequest request) {
        try {
            createHttpClient();
            this.httpClientHandler.setRequest(request);
            Pair<String, Integer> hostPortPair = this.getHostPortFromUrl(request.getRequestUrl());

            SslContext sslContext = this.useSslContext(request.getRequestUrl());
            this.bootstrap.handler(new TankHttpClientInitializer(this.httpProxyHandler, sslContext, this.httpClientHandler));

            this.channel = this.bootstrap.connect(hostPortPair.getLeft(), hostPortPair.getRight()).sync().channel();
            this.setConnectionTimeout(60000);

            FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.PUT, new URI(request.getRequestUrl()).getRawPath());
            httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            httpRequest.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            if (this.cookies.size() > 0) {
                this.httpClientHandler.setCookieList(this.cookies);
                httpRequest.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.STRICT.encode(this.cookies));
            }

            if (this.host != null) {
                httpRequest.headers().set(HttpHeaderNames.HOST, host);
            } else {
                httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            }
            if (this.username != null && this.password != null) {
                httpRequest.headers().set(HttpHeaderNames.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((this.username + ":" + this.password).getBytes()));
            }

            String requestBody = request.getBody();
            ByteBuf byteBuf = Unpooled.copiedBuffer(requestBody, StandardCharsets.UTF_8);
            httpRequest.content().clear().writeBytes(byteBuf);

            long waitTime = 0L;
            this.httpClientHandler.setStartTimestamp(waitTime);
            this.channel.writeAndFlush(httpRequest);
            this.channel.closeFuture().sync();
        } catch (URISyntaxException | SSLException | InterruptedException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void doDelete(BaseRequest request) {
        try {
            createHttpClient();
            this.httpClientHandler.setRequest(request);
            Pair<String, Integer> hostPortPair = this.getHostPortFromUrl(request.getRequestUrl());

            SslContext sslContext = this.useSslContext(request.getRequestUrl());
            this.bootstrap.handler(new TankHttpClientInitializer(this.httpProxyHandler, sslContext, this.httpClientHandler));

            this.channel = this.bootstrap.connect(hostPortPair.getLeft(), hostPortPair.getRight()).sync().channel();
            this.setConnectionTimeout(60000);

            HttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.DELETE, new URI(request.getRequestUrl()).getRawPath(), Unpooled.EMPTY_BUFFER);
            httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            httpRequest.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            if (this.cookies.size() > 0) {
                this.httpClientHandler.setCookieList(this.cookies);
                httpRequest.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.STRICT.encode(this.cookies));
            }

            if (this.host != null) {
                httpRequest.headers().set(HttpHeaderNames.HOST, host);
            } else {
                httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            }
            if (this.username != null && this.password != null) {
                httpRequest.headers().set(HttpHeaderNames.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((this.username + ":" + this.password).getBytes()));
            }

            long waitTime = 0L;
            this.httpClientHandler.setStartTimestamp(waitTime);
            this.channel.writeAndFlush(httpRequest);
            this.channel.closeFuture().sync();
        } catch (URISyntaxException | SSLException | InterruptedException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void doOptions(BaseRequest request) {
        try {
            createHttpClient();
            this.httpClientHandler.setRequest(request);
            Pair<String, Integer> hostPortPair = this.getHostPortFromUrl(request.getRequestUrl());

            SslContext sslContext = this.useSslContext(request.getRequestUrl());
            this.bootstrap.handler(new TankHttpClientInitializer(this.httpProxyHandler, sslContext, this.httpClientHandler));

            this.channel = this.bootstrap.connect(hostPortPair.getLeft(), hostPortPair.getRight()).sync().channel();
            this.setConnectionTimeout(60000);

            HttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.OPTIONS, new URI(request.getRequestUrl()).getRawPath(), Unpooled.EMPTY_BUFFER);
            httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            httpRequest.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            if (this.cookies.size() > 0) {
                this.httpClientHandler.setCookieList(this.cookies);
                httpRequest.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.STRICT.encode(this.cookies));
            }

            if (this.host != null) {
                httpRequest.headers().set(HttpHeaderNames.HOST, host);
            } else {
                httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            }
            if (this.username != null && this.password != null) {
                httpRequest.headers().set(HttpHeaderNames.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((this.username + ":" + this.password).getBytes()));
            }

            long waitTime = 0L;
            this.httpClientHandler.setStartTimestamp(waitTime);
            this.channel.writeAndFlush(httpRequest);
            this.channel.closeFuture().sync();
        } catch (URISyntaxException | SSLException | InterruptedException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void doPost(BaseRequest request) {
        try {
            createHttpClient();
            this.httpClientHandler.setRequest(request);
            Pair<String, Integer> hostPortPair = this.getHostPortFromUrl(request.getRequestUrl());

            SslContext sslContext = this.useSslContext(request.getRequestUrl());
            this.bootstrap.handler(new TankHttpClientInitializer(this.httpProxyHandler, sslContext, this.httpClientHandler));

            this.channel = this.bootstrap.connect(hostPortPair.getLeft(), hostPortPair.getRight()).sync().channel();
            this.setConnectionTimeout(60000);

            FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.POST, new URI(request.getRequestUrl()).getRawPath());
            httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            httpRequest.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

            if (this.cookies.size() > 0) {
                this.httpClientHandler.setCookieList(this.cookies);
                httpRequest.headers().set(
                        HttpHeaderNames.COOKIE,
                        ClientCookieEncoder.STRICT.encode(this.cookies));
            }

            if (this.host != null) {
                httpRequest.headers().set(HttpHeaderNames.HOST, host);
            } else {
                httpRequest.headers().set(HttpHeaderNames.HOST, hostPortPair.getKey());
            }
            if (this.username != null && this.password != null) {
                httpRequest.headers().set(HttpHeaderNames.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((this.username + ":" + this.password).getBytes()));
            }

            String requestBody = request.getBody();
            ByteBuf byteBuf = Unpooled.copiedBuffer(requestBody, StandardCharsets.UTF_8);
            httpRequest.content().clear().writeBytes(byteBuf);

            long waitTime = 0L;
            this.httpClientHandler.setStartTimestamp(waitTime);
            this.channel.writeAndFlush(httpRequest);
            this.channel.closeFuture().sync();
        } catch (URISyntaxException | SSLException | InterruptedException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void addAuth(AuthCredentials creds) {
        this.username = creds.getUserName();
        this.password = creds.getPassword();
        this.host = creds.getHost();
    }

    @Override
    public void clearSession() {
        if (this.cookies.size() > 0) {
            this.cookies.clear();
        }

        if (this.group != null) {
            this.group.shutdownGracefully();
        }
    }

    @Override
    public void setCookie(TankCookie cookie) {
         DefaultCookie defaultCookie = new DefaultCookie(cookie.getName(), cookie.getValue());
         defaultCookie.setDomain(cookie.getDomain());
         defaultCookie.setPath(cookie.getPath());
         this.cookies.add(defaultCookie);
    }

    @Override
    public void setProxy(String proxyhost, int proxyport) {
        if (StringUtils.isNotBlank(proxyhost)) {
            this.httpProxyHandler = new HttpProxyHandler(new InetSocketAddress(proxyhost, proxyport));
        }
    }
}