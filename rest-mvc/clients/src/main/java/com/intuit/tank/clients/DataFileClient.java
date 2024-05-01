/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.clients.util.ClientException;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.datafiles.models.DataFileDescriptorContainer;

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

public class DataFileClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/v2/datafiles";

    public DataFileClient(String serviceUrl, String token)  {
        super(serviceUrl, token, null, null);
    }

    public DataFileClient(String serviceUrl, String token, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, token, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public DataFileDescriptorContainer getDatafiles() throws IOException, InterruptedException {
        HttpRequest request = requestBuilder("")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, DataFileDescriptorContainer.class);
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

    public DataFileDescriptor getDatafile(Integer datafileId) {
        HttpRequest request = requestBuilder("", datafileId)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if(checkStatusCode(response.statusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                try(InputStream is = response.body()) {
                    return objectMapper.readValue(is, DataFileDescriptor.class);
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

    public String getDatafileContent(Integer datafileId) {
            URI uri = URI.create(urlBuilder.buildUrl("/content") + "?id=" + datafileId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
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

    public String downloadDatafile(Integer datafileId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.buildUrl("/download", datafileId)))
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
        } catch (Exception e2) {
            handleError(request, e2);
        }
        return null;
    }

    public Map<String, String> uploadDatafile(Integer id, Path filepath) throws IOException {
        URI uri;

        if(id == null) {
            uri = URI.create(urlBuilder.buildUrl("/upload"));
        } else {
            uri = URI.create(urlBuilder.buildUrl("/upload") + "?id=" + id);
        }

        String boundary = "Boundary-" + Long.toHexString(System.currentTimeMillis());

        var byteArrays = new ArrayList<byte[]>();
        byteArrays.add(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"" + filepath.getFileName() + "\"\r\nContent-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(filepath));
        byteArrays.add(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest.BodyPublisher bodyPublishers = HttpRequest.BodyPublishers.ofByteArrays(byteArrays);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(bodyPublishers)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(checkStatusCode(response.statusCode())) {
                return new ObjectMapper().readValue(response.body(), Map.class);
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

    public String deleteDatafile(Integer datafileId) {
        HttpRequest request = requestBuilder("", datafileId)
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
