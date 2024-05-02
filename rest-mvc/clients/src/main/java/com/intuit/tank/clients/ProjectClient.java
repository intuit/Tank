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
import com.intuit.tank.projects.models.AutomationRequest;
import com.intuit.tank.projects.models.ProjectContainer;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.script.models.ExternalScriptTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ProjectClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/v2/projects";

    public ProjectClient(String serviceUrl, String token)  {
        super(serviceUrl, token, null, null);
    }

    public ProjectClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, token, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public ProjectContainer getProjects() {
        HttpRequest request = requestBuilder("")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, ProjectContainer.class);
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

    public Map<Integer, String> getProjectNames() throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = requestBuilder("/names")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(checkStatusCode(response.statusCode())) {
                return objectMapper.readValue(response.body(), Map.class);
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

    public ProjectTO getProject(Integer projectId) throws IOException, InterruptedException {
        HttpRequest request = requestBuilder("", projectId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, ProjectTO.class);
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

    public Map<String, String> createProject(AutomationRequest projectRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(projectRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize JSON object: ", e);
        }

        HttpRequest request = requestBuilder("")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(checkStatusCode(response.statusCode())) {
                return objectMapper.readValue(response.body(), Map.class);
            } else {
                throw new ClientException(response.body(), response.statusCode());
            }
        } catch (InterruptedException | IOException e2) {
            handleError(request, e2);
        }
        return null;
    }


    public Map<String, String> updateProject(AutomationRequest projectRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(projectRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize JSON object: ", e);
        }

        HttpRequest request = requestBuilder("")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(checkStatusCode(response.statusCode())) {
                return objectMapper.readValue(response.body(), Map.class);
            } else {
                throw new ClientException(response.body(), response.statusCode());
            }
        } catch (InterruptedException | IOException e2) {
            handleError(request, e2);
        }
        return null;
    }

    public String downloadTestScriptForProject(Integer projectId) throws IOException, InterruptedException {
        HttpRequest request = requestBuilder("/download", projectId)
                .GET()
                .build();

        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try {
            if (checkStatusCode(response.statusCode())) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (InputStream is = response.body()) {
                    byte[] buffer = new byte[1024];
                    for (int len; (len = is.read(buffer)) != -1; ) {
                        baos.write(buffer, 0, len);
                    }
                }
                return baos.toString();
            } else {
                String responseBody = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                throw new ClientException(responseBody, response.statusCode());
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e) {
            handleError(request, e);
        }
        return null;
    }


    public String deleteProject(Integer projectId) {
        HttpRequest request = requestBuilder("", projectId)
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
