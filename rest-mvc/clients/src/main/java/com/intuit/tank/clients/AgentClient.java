/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.clients.util.ClientException;
import com.intuit.tank.agent.models.TankHttpClientDefinitionContainer;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AgentClient extends BaseClient {

    private static final String SERVICE_BASE_URL = "/v2/agent";

    public AgentClient(String serviceUrl, String token)  {
        super(serviceUrl, token, null, null);
    }

    public AgentClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, token, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public String getSettings() {
        HttpRequest request = requestBuilder("/settings")
                .header("Accept", "application/xml")
                .GET()
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

    public InputStream getSupportFiles() {
        HttpRequest request = requestBuilder("/support-files")
                .header("Accept", "application/octet-stream")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (checkStatusCode(response.statusCode())) {
                return response.body();
            } else {
                try (InputStream errorStream = response.body()) {
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


    public AgentTestStartData agentReady(AgentData agentData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = requestBuilder("/ready")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(agentData)))
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (checkStatusCode(response.statusCode())) {
                try (InputStream is = response.body()) {
                    return objectMapper.readValue(is, AgentTestStartData.class);
                }
            } else {
                try (InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to process JSON object: ", e);
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public Headers getHeaders() {
        HttpRequest request = requestBuilder("/headers")
                .header("Accept", "application/xml")
                .GET()
                .build();
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (checkStatusCode(response.statusCode())) {
                XmlMapper xmlMapper = new XmlMapper();
                try (InputStream is = response.body()) {
                    return xmlMapper.readValue(is, Headers.class);
                }
            } else {
                try (InputStream errorStream = response.body()) {
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

    public TankHttpClientDefinitionContainer getClients() {
        HttpRequest request = requestBuilder("/clients")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try (InputStream is = response.body()) {
                    return objectMapper.readValue(is, TankHttpClientDefinitionContainer.class);
                }
            } else {
                try (InputStream errorStream = response.body()) {
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

    public void setStandaloneAgentAvailability(AgentAvailability availability) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = requestBuilder("/availability")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(availability)))
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if(!checkStatusCode(response.statusCode())) {
                try (InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to process JSON object: ", e);
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
    }

    public CloudVmStatus getInstanceStatus(String instanceId) {
        HttpRequest request = requestBuilder("/instance/status/", instanceId)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, CloudVmStatus.class);
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

    public Void setInstanceStatus(String instanceId, CloudVmStatus VmStatus) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = requestBuilder("/instance/status/", instanceId)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(VmStatus)))
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(!checkStatusCode(response.statusCode())) {
                try (InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to process JSON object: ", e);
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public String stopInstance(String instanceId) {
        HttpRequest request = requestBuilder("/instance/stop/", instanceId)
                .header("Accept", "application/json")
                .GET()
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

    public String pauseInstance(String instanceId) {
        HttpRequest request = requestBuilder("/instance/pause/", instanceId)
                .header("Accept", "application/json")
                .GET()
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

    public String resumeInstance(String instanceId) {
        HttpRequest request = requestBuilder("/instance/resume/", instanceId)
                .header("Accept", "application/json")
                .GET()
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

    public String killInstance(String instanceId) {
        HttpRequest request = requestBuilder("/instance/kill/", instanceId)
                .header("Accept", "application/json")
                .GET()
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
