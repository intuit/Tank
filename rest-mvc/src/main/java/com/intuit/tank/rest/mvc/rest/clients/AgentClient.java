/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.rest.mvc.rest.models.common.CloudVmStatus;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.Headers;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Map;

public class AgentClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/v2/agent";

    public AgentClient(String serviceUrl)  {
        super(serviceUrl, null, null);
    }

    public AgentClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public String getSettings() {
        return client.get()
                .uri(urlBuilder.buildUrl("/settings"))
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    public Mono<DataBuffer> getSupportFiles() {
        return client.get()
                .uri(urlBuilder.buildUrl("/support-files"))
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataBuffer.class);
    }

    public AgentTestStartData agentReady(AgentData agentData) {
        return client.post()
                .uri(urlBuilder.buildUrl("/ready"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(agentData), AgentData.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(AgentTestStartData.class)
                .block();
    }

    public Headers getHeaders() {
        return client.get()
                .uri(urlBuilder.buildUrl("/headers"))
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Headers.class)
                .block();
    }

    public TankHttpClientDefinitionContainer getClients() {
        return client.get()
                .uri(urlBuilder.buildUrl("/clients"))
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(TankHttpClientDefinitionContainer.class)
                .block();
    }

    public Void setStandaloneAgentAvailability(AgentAvailability availability) {
        return client.post()
                .uri(urlBuilder.buildUrl("/availability"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(availability), AgentAvailability.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Void.class)
                .block();
    }

    public CloudVmStatus getInstanceStatus(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/status/", instanceId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(CloudVmStatus.class)
                .block();
    }

    public Void setInstanceStatus(String instanceId, CloudVmStatus VmStatus) {
        return client.put()
                .uri(urlBuilder.buildUrl("/instance/status/", instanceId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(VmStatus), CloudVmStatus.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Void.class)
                .block();
    }

    public String stopInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/stop/", instanceId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    public String pauseInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/pause/", instanceId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    public String resumeInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/resume/", instanceId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    public String killInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/kill/", instanceId))
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
