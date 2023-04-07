/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.projects.AutomationRequest;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ProjectClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/api/v2/projects";

    public ProjectClient(String serviceUrl)  {
        super(serviceUrl, null, null);
    }

    public ProjectClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public ProjectContainer getProjects() {
        return client.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(ProjectContainer.class)
                .block();
    }

    public Map<Integer, String> getProjectNames() {
        return WebClient.create(urlBuilder.buildUrl("/names"))
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Map.class)
                .block();
    }

    public ProjectTO getProject(Integer projectId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", projectId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                         .flatMap(body -> Mono.error(new ClientException(body,
                                 response.statusCode().value()))))
                .bodyToMono(ProjectTO.class)
                .block();
    }

    public Map<String, String> createProject(AutomationRequest request) {
        return client.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AutomationRequest.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, String> updateProject(AutomationRequest request) {
        return client.put()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), AutomationRequest.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Map.class)
                .block();
    }

    public String downloadTestScriptForProject(Integer projectId) {
        Mono<DataBuffer> dataBuffer = client.get()
                        .uri(urlBuilder.buildUrl("/download", projectId))
                        .retrieve()
                        .onStatus(status -> status.isError(),
                                response -> response.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new ClientException(body,
                                                response.statusCode().value()))))
                        .bodyToMono(DataBuffer.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataBufferUtils.write(dataBuffer, baos)
                .share().blockLast();

        return baos.toString();
    }

    public String deleteProject(Integer projectId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", projectId))
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
