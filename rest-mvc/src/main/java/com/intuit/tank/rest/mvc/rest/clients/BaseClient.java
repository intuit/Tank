package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.*;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

public abstract class BaseClient {
    private static final Logger LOGGER = LogManager.getLogger(BaseClient.class);

    protected String baseUrl;

    protected WebClient client;
    protected RestExceptionHandler exceptionHandler = new RestExceptionHandler();
    protected RestUrlBuilder urlBuilder;

    public BaseClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        setBaseUrl(serviceUrl);
        if (StringUtils.isNotEmpty(proxyServer)) {
            int port = proxyPort != null ? proxyPort : 80;
            client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(build(proxyServer, proxyPort))).build();
        } else {
            HttpClient httpClient = HttpClient.create().compress(true);
            client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();

        }
        LOGGER.info(new ObjectMessage(ImmutableMap.of("Message", "client for url " + baseUrl + ": proxy="
                + (proxyServer != null ? proxyServer + ":" + proxyPort : "none"))));
    }

    private HttpClient build(String proxyServer, Integer proxyPort) {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient ->
                        tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).host(proxyServer).port(proxyPort)));
        return httpClient;
    }

    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl + getServiceBaseUrl();
            urlBuilder = new RestUrlBuilder(this.baseUrl);
        }
    }

    protected abstract String getServiceBaseUrl();

    public String ping() throws RestServiceException {
        return client.get()
                .uri(urlBuilder.buildUrl("/ping"))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
