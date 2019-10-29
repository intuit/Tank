package com.intuit.tank.httpclient6.utils;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.TankHttpUtil;
import com.intuit.tank.logging.LogEventType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class TankHttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger LOG = LogManager.getLogger(TankHttpClientHandler.class);

    private BaseRequest request;
    private long startTimestamp;
    private List<DefaultCookie> cookieList;

    public void setRequest(BaseRequest request) {
        this.request = request;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setCookieList(List<DefaultCookie> cookieList) {
        this.cookieList = cookieList;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        long waitTime = System.currentTimeMillis() - this.startTimestamp;
        BaseResponse finalResponse = this.request.getResponse();
        try {
            if (msg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse) msg;

                Map<String, String> headers = new HashMap<>();

                String contentType = "";
                if (!response.headers().isEmpty()) {
                    for (CharSequence name : response.headers().names()) {
                        for (CharSequence value : response.headers().getAll(name)) {
                            if (name.equals("Content-Type")) {
                                contentType = value.toString();
                            }

                            headers.put(name.toString(), value.toString());
                        }
                    }
                }

                if (finalResponse == null) {
                    finalResponse = TankHttpUtil.newResponseObject(contentType);
                    request.setResponse(finalResponse);
                }
                finalResponse.setResponseTime(waitTime);

                finalResponse.setHttpMessage(response.getStatus().reasonPhrase());
                finalResponse.setHttpCode(response.getStatus().code());

                for (Map.Entry<String, String> header : headers.entrySet()) {
                    finalResponse.setHeader(header.getKey(), header.getValue());
                }

                if (cookieList != null) {
                    for (Cookie cookie : cookieList) {
                        finalResponse.setCookie(cookie.name(), cookie.value());
                    }
                }
            }
            if (msg instanceof HttpContent) {
                String contentType = finalResponse.getHttpHeader("Content-Type");
                String contentEncode = finalResponse.getHttpHeader("Content-Encoding");
                HttpContent content = (HttpContent) msg;
                byte[] bResponse = content.content().array();

                byte[] currentResponse = finalResponse.getResponseBytes();
                if (currentResponse == null) {
                    currentResponse = new byte[]{};
                }

                byte[] newResponse = new byte[currentResponse.length + bResponse.length];
                System.arraycopy(currentResponse, 0, newResponse, 0, currentResponse.length);
                System.arraycopy(bResponse, 0, newResponse, currentResponse.length, bResponse.length);
                finalResponse.setResponseBody(newResponse);

                if (content instanceof LastHttpContent) {
                    if (BaseResponse.isDataType(contentType) && contentEncode != null
                            && contentEncode.toLowerCase().contains("gzip")) {
                        // decode gzip for data types
                        try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(bResponse));
                             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                            IOUtils.copy(in, out);
                            bResponse = out.toByteArray();
                        } catch (IOException | NullPointerException ex) {
                            LOG.warn(request.getLogUtil().getLogMessage("cannot decode gzip stream: {}" + ex, LogEventType.System));
                        }
                    }

                    finalResponse.logResponse();
                    channelHandlerContext.close();
                }
            }
        } catch (Exception ex) {
            LOG.warn("Unable to get response: " + ex.getMessage());
        }
    }
}