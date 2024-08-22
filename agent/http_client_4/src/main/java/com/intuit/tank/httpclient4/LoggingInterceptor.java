package com.intuit.tank.httpclient4;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

public class LoggingInterceptor implements HttpRequestInterceptor {

    private static final Logger LOG = LogManager.getLogger(LoggingInterceptor.class);

    @Override
    public void process(HttpRequest request, HttpContext context) throws IOException {
        LOG.info("Sending request: " + request.getRequestLine());
        Arrays.stream(request.getAllHeaders()).forEach(header -> LOG.info("Header: " + header));
    }

}
