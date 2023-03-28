/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.filters.*;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptTO;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Map;

public class FilterClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/api/v2/filters";

    public FilterClient(String serviceUrl)  {
        super(serviceUrl, null, null);
    }

    public FilterClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public FilterContainer getFilters() {
        return client.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(FilterContainer.class)
                .block();
    }

    public FilterGroupContainer getFilterGroups() {
        return client.get()
                .uri(urlBuilder.buildUrl("/groups"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(FilterGroupContainer.class)
                .block();
    }

    public FilterTO getFilter(Integer filterId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", filterId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(FilterTO.class)
                .block();
    }

    public FilterGroupTO getFilterGroup(Integer filterGroupId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", filterGroupId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(FilterGroupTO.class)
                .block();
    }

    public String applyFilters(Integer scriptId, ApplyFiltersRequest request) {
        return client.post()
                .uri(urlBuilder.buildUrl("/apply-filters", scriptId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .body(Mono.just(request), ApplyFiltersRequest.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    public String deleteFilter(Integer filterId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", filterId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    public String deleteFilterGroup(Integer filterGroupId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", filterGroupId))
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
