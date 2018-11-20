package com.intuit.tank.rest;

/*
 * #%L
 * Rest Client Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.message.ObjectMessage;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseRestClient {

    private static final Logger LOG = LogManager.getLogger(BaseRestClient.class);
    private static final String METHOD_PING = "/ping";

    protected String baseUrl;
    protected Client client;
    protected RestExceptionHandler exceptionHandler = new RestExceptionHandler();
    protected RestUrlBuilder urlBuilder;

    /**
     * 
     * @param serviceUrl
     */
    public BaseRestClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        setBaseUrl(serviceUrl);
        if (StringUtils.isNotEmpty(proxyServer)) {
        	ConnectorProvider connectorprovider = new HttpUrlConnectorProvider() {
                private Proxy proxy;

                private void initializeProxy() {
                    int port = proxyPort != null ? proxyPort : 80;
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, port));
                }

                public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
                    initializeProxy();
                    return (HttpURLConnection) url.openConnection(proxy);
                }
            };
            client = ClientBuilder.newBuilder().register(connectorprovider).build();
        } else {
            client = ClientBuilder.newClient();
        }
//        client.setConnectTimeout(5000);
//        client.setFollowRedirects(true);
        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "client for url " + baseUrl + ": proxy="
                + (proxyServer != null ? proxyServer + ":" + proxyPort : "none"))));
    }
    
    public void addAuth(String user, String token) {
        client.register(HttpAuthenticationFeature.basic(user, token));
    }

    /**
     * @param baseUrl
     *            the baseUrl to set
     */
    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl + getServiceBaseUrl();
            urlBuilder = new RestUrlBuilder(this.baseUrl);
        }
    }

    /**
     * 
     * @return
     */
    protected abstract String getServiceBaseUrl();

    /**
     * @inheritDoc
     */
    public String ping() throws RestServiceException {
        WebTarget webTarget = client.target(baseUrl + METHOD_PING);
    	webTarget.property(ClientProperties.FOLLOW_REDIRECTS, true);
    	webTarget.property(ClientProperties.CONNECT_TIMEOUT, 5000);
        Response response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get();
        exceptionHandler.checkStatusCode(response);
        return response.readEntity(String.class);
    }
}
