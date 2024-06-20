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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.intuit.tank.clients.util.ClientException;
import com.intuit.tank.projects.models.ProjectContainer;
import com.intuit.tank.jobs.models.*;
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.script.models.ExternalScriptTO;
import com.intuit.tank.vm.agent.messages.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class JobClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/v2/jobs";

    public JobClient(String serviceUrl, String token)  {
        super(serviceUrl, token, null, null);
    }

    public JobClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, token, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public JobContainer getAllJobs() {
        HttpRequest request = requestBuilder("")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, JobContainer.class);
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

    public JobTO getJob(Integer jobId) {
        HttpRequest request = requestBuilder("", jobId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, JobTO.class);
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

    public JobContainer getJobsByProject(Integer projectId) {
        HttpRequest request = requestBuilder("/project", projectId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, JobContainer.class);
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

    public Map<String, String> createJob(CreateJobRequest jobRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(jobRequest);
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
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public List<Map<String, String>> getAllJobStatus() {
        HttpRequest request = requestBuilder("/status")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, List.class);
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

    public String getJobStatus(Integer jobId) {
        HttpRequest request = requestBuilder("/status", jobId)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (checkStatusCode(response.statusCode())) {
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

    public String getJobVMStatuses(String jobId) {
        HttpRequest request = requestBuilder("/instance-status", jobId)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (checkStatusCode(response.statusCode())) {
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

    // Job Status Setters

    public String startJob(Integer jobId) {
        HttpRequest request = requestBuilder("/start", jobId)
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

    public String stopJob(Integer jobId) {
        HttpRequest request = requestBuilder("/stop", jobId)
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

    public String pauseJob(Integer jobId) {
        HttpRequest request = requestBuilder("/pause", jobId)
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

    public String resumeJob(Integer jobId) {
        HttpRequest request = requestBuilder("/resume", jobId)
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

    public String killJob(Integer jobId) {
        HttpRequest request = requestBuilder("/kill", jobId)
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
