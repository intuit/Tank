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
import com.intuit.tank.projects.models.ProjectTO;
import com.intuit.tank.script.models.ExternalScriptContainer;
import com.intuit.tank.script.models.ExternalScriptTO;
import com.intuit.tank.script.models.ScriptDescriptionContainer;
import com.intuit.tank.script.models.ScriptTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;


public class ScriptClient extends BaseClient {

    private static final String SERVICE_BASE_URL = "/v2/scripts";

    public ScriptClient(String serviceUrl, String token)  {
        super(serviceUrl, token, null, null);
    }

    public ScriptClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, token, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public ScriptDescriptionContainer getScripts() throws IOException, InterruptedException {
        HttpRequest request = requestBuilder("")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, ScriptDescriptionContainer.class);
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


    public ScriptTO getScript(Integer scriptId) {
        HttpRequest request = requestBuilder("", scriptId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, ScriptTO.class);
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

    public String downloadScript(Integer scriptId) {
        HttpRequest request = requestBuilder("/download", scriptId)
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

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
                try(InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e) {
            handleError(request, e);
        }
        return null;
    }

    public String downloadHarnessScript(Integer scriptId) throws IOException, InterruptedException {
        HttpRequest request = requestBuilder("/harness/download", scriptId)
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

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
                try(InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e) {
            handleError(request, e);
        }
        return null;
    }

    public String deleteScript(Integer scriptId) {
        HttpRequest request = requestBuilder("", scriptId)
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

    // External Scripts

    public ExternalScriptContainer getExternalScripts() throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = requestBuilder("/external")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(checkStatusCode(response.statusCode())) {
                return objectMapper.readValue(response.body(), ExternalScriptContainer.class);
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


    public ExternalScriptTO getExternalScript(Integer externalScriptId) {
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest request = requestBuilder("/external", externalScriptId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(checkStatusCode(response.statusCode())) {
                return objectMapper.readValue(response.body(), ExternalScriptTO.class);
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

    public ExternalScriptTO createExternalScript(ExternalScriptTO script) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody;

        try {
            requestBody = objectMapper.writeValueAsString(script);
        } catch (JsonProcessingException e) {
        throw new IllegalArgumentException("Failed to serialize JSON object: ", e);
        }

        HttpRequest request = requestBuilder("/external")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(checkStatusCode(response.statusCode())) {
                return objectMapper.readValue(response.body(), ExternalScriptTO.class);
            } else {
                throw new ClientException(response.body(), response.statusCode());
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (InterruptedException | IOException e) {
            handleError(request, e);
        }
        return null;
    }


    public String downloadExternalScript(Integer externalScriptId) {
        HttpRequest request = requestBuilder("/external/download", externalScriptId)
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

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
                try(InputStream errorStream = response.body()) {
                    String responseBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    throw new ClientException(responseBody, response.statusCode());
                }
            }
        } catch (ClientException e1) {
            throw e1;
        } catch (Exception e) {
            handleError(request, e);
        }
        return null;
    }

    public String deleteExternalScript(Integer externalScriptId) {
        HttpRequest request = requestBuilder("/external", externalScriptId)
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
