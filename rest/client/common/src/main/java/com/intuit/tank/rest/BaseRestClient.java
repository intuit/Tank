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

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public abstract class BaseRestClient {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseRestClient.class);
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
        if (proxyServer != null) {
            URLConnectionClientHandler ch = new URLConnectionClientHandler(new HttpURLConnectionFactory() {
                private Proxy proxy;

                private void initializeProxy() {
                    int port = proxyPort != null ? proxyPort : 80;
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, port));
                }

                public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
                    initializeProxy();
                    return (HttpURLConnection) url.openConnection(proxy);
                }
            });
            client = new Client(ch);
        } else {
            client = Client.create();
        }
        client.setConnectTimeout(5000);
        client.setFollowRedirects(true);
        LOG.info("client for url " + baseUrl + ": proxy="
                + (proxyServer != null ? proxyServer + ":" + proxyPort : "none"));
    }
    
    public void addAuth(String user, String token) {
        client.addFilter(new HTTPBasicAuthFilter(user, token));
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
     * @{inheritDoc
     */
    public String ping() throws RestServiceException, UniformInterfaceException {
        WebResource webResource = client.resource(baseUrl + METHOD_PING);
        ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE)
                .get(ClientResponse.class);
        exceptionHandler.checkStatusCode(response);
        return response.getEntity(String.class);
    }
}
