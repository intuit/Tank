/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;

public abstract class BaseClient {
    private static final Logger LOGGER = LogManager.getLogger(BaseClient.class);

    protected String baseUrl;

    protected String token;

    protected WebClient client;
    protected RestUrlBuilder urlBuilder;

    public BaseClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        setBaseUrl(serviceUrl, token);
        if (StringUtils.isNotEmpty(proxyServer)) {
            int port = proxyPort != null ? proxyPort : 80;
            client = WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer ->
                            configurer.defaultCodecs()
                                    .maxInMemorySize(200 * 1024 * 1024)
                    ).build())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "bearer "+token)
                    .clientConnector(new JdkClientHttpConnector(build(proxyServer, port))).build();
        } else {
            client = WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer ->
                            configurer.defaultCodecs()
                                    .maxInMemorySize(200 * 1024 * 1024)
                    ).build())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "bearer "+token)
                    .clientConnector(new JdkClientHttpConnector()).build();

        }
        LOGGER.info(new ObjectMessage(ImmutableMap.of("Message", "client for url " + baseUrl + ": proxy="
                + (proxyServer != null ? proxyServer + ":" + proxyPort : "none"))));
    }

    private HttpClient build(String proxyServer, Integer proxyPort) {
        return HttpClient.newBuilder().proxy(ProxySelector.of(InetSocketAddress.createUnresolved(proxyServer, proxyPort))).build();
    }

    public void setBaseUrl(String baseUrl, String token) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl + getServiceBaseUrl();
            urlBuilder = new RestUrlBuilder(this.baseUrl);
        }
        this.token = (token == null) ? this.token : token;
    }

    protected abstract String getServiceBaseUrl();

    public String ping() {
        return client.get()
                .uri(urlBuilder.buildUrl("/ping"))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> status.isError(),
                           response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }
}
