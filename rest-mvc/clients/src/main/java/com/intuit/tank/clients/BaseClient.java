/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.clients;

import com.intuit.tank.clients.util.*;

import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public abstract class BaseClient {
    private static final Logger LOGGER = LogManager.getLogger(BaseClient.class);

    protected String baseUrl;

    protected String token;

    protected HttpClient client;
    protected RestUrlBuilder urlBuilder;

    public BaseClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        setBaseUrl(serviceUrl, token);
        client = createHttpClient(proxyServer, proxyPort);
        LOGGER.info(new ObjectMessage(ImmutableMap.of("Message", "HttpClient created for URL " + baseUrl + ": proxy="
                + (proxyServer != null ? proxyServer + ":" + proxyPort : "none"))));
    }

    private HttpClient createHttpClient(final String proxyServer, final Integer proxyPort) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3));

        if(proxyServer != null && proxyPort != null) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(proxyServer, proxyPort)));
        }

        return builder.build();
    }

    protected HttpRequest.Builder requestBuilder(String path) {
        String url = urlBuilder.buildUrl(path);

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .timeout(Duration.ofSeconds(3));
    }

    protected HttpRequest.Builder requestBuilder(String path,  @Nullable Object... parameters) {
        String url = urlBuilder.buildUrl(path, parameters);

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .timeout(Duration.ofSeconds(30));
    }

    public void setBaseUrl(String baseUrl, String token) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl + getServiceBaseUrl();
            urlBuilder = new RestUrlBuilder(this.baseUrl);
        }
        this.token = (token == null) ? this.token : token;
    }

    protected abstract String getServiceBaseUrl();

    public static boolean checkStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    protected void handleError(HttpRequest request, Exception e){
        LOGGER.error("Failed to {} {}: {}", request.method(), request.uri(), e.getMessage());
        throw new RuntimeException("Error during " + request.method() + " to " + request.uri() + ": " + e.getMessage(), e);
    }

    public String ping() {
        HttpRequest request = requestBuilder("/ping").GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(checkStatusCode(response.statusCode())) {
                return response.body();
            } else {
                throw new ClientException(response.body(), response.statusCode());
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }
}
