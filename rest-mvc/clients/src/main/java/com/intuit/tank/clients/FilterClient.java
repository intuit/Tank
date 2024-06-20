/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.clients.util.ClientException;
import com.intuit.tank.filters.models.*;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class FilterClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/v2/filters";

    public FilterClient(String serviceUrl, String token)  {
        super(serviceUrl, token, null, null);
    }

    public FilterClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, token, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public FilterContainer getFilters() {
        HttpRequest request = requestBuilder("")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, FilterContainer.class);
                }
            } else {
                try(InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public FilterGroupContainer getFilterGroups() {
        HttpRequest request = requestBuilder("/groups")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, FilterGroupContainer.class);
                }
            } else {
                try(InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public FilterTO getFilter(Integer filterId) {
        HttpRequest request = requestBuilder("", filterId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, FilterTO.class);
                }
            } else {
                try(InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public FilterGroupTO getFilterGroup(Integer filterGroupId) {
        HttpRequest request = requestBuilder("", filterGroupId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, FilterGroupTO.class);
                }
            } else {
                try(InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public String applyFilters(Integer scriptId, ApplyFiltersRequest filtersRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(filtersRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize JSON object: ", e);
        }

        HttpRequest request = requestBuilder("/apply-filters", scriptId)
                .header("Content-Type", "application/json")
                .header("Accept", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

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

    public String deleteFilter(Integer filterId) {
        HttpRequest request = requestBuilder("", filterId)
                .header("Accept", "text/plain")
                .DELETE()
                .build();

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

    public String deleteFilterGroup(Integer filterGroupId) {
        HttpRequest request = requestBuilder("", filterGroupId)
                .header("Accept", "text/plain")
                .DELETE()
                .build();

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
