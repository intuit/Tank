/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class DataFileClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/api/v2/datafiles";

    public DataFileClient(String serviceUrl)  {
        super(serviceUrl, null, null);
    }

    public DataFileClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public DataFileDescriptorContainer getDatafiles() {
        return client.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataFileDescriptorContainer.class)
                .block();
    }

    public DataFileDescriptor getDatafile(Integer datafileId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", datafileId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataFileDescriptor.class)
                .block();
    }

    public String getDatafileContent(Integer id, Integer offset, Integer lines) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                    .path(urlBuilder.buildUrl("/content"))
                    .queryParam("id", id)
                    .queryParam("offset", offset)
                    .queryParam("lines", lines)
                    .build())
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    public String deleteDatafile(Integer datafileID) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", datafileID))
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
