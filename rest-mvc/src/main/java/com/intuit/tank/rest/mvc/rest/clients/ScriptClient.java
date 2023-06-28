/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.script.ExternalScriptContainer;
import com.intuit.tank.script.ExternalScriptTO;
import com.intuit.tank.script.ScriptDescriptionContainer;
import com.intuit.tank.script.ScriptTO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ScriptClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/v2/scripts";

    public ScriptClient(String serviceUrl)  {
        super(serviceUrl, null, null);
    }

    public ScriptClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public ScriptDescriptionContainer getScripts() {
        return client.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(ScriptDescriptionContainer.class)
                .block();
    }

    public ScriptTO getScript(Integer scriptId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", scriptId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(ScriptTO.class)
                .block();
    }

    public ScriptTO createScript(ScriptTO scriptTo) {
        return client.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(scriptTo), ScriptTO.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(ScriptTO.class)
                .block();
    }

    public Mono<DataBuffer> downloadScript(Integer scriptId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/download", scriptId))
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataBuffer.class);
    }

    public String downloadHarnessScript(Integer scriptId) {
        Flux<DataBuffer> dataBuffers = client.get()
                        .uri(urlBuilder.buildUrl("/harness/download", scriptId))
                        .retrieve()
                        .onStatus(status -> status.isError(),
                                response -> response.bodyToMono(String.class)
                                        .flatMap(body -> Mono.error(new ClientException(body,
                                                response.statusCode().value()))))
                        .bodyToFlux(DataBuffer.class)
                        .cache();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dataBuffers.doOnNext(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                baos.write(bytes, 0, bytes.length);
            }).blockLast();

        return baos.toString();
    }

    public Map<String, String> uploadScript(String name, Integer existingScriptId, MultipartFile file) {
        String finalName = name == null ? "" : name;
        Integer finalId = existingScriptId == null ? 0 :existingScriptId;
        return WebClient.create(urlBuilder.buildUrl("")) // need webclient.create for query params
                .get()
                .uri(uriBuilder -> uriBuilder.path("/upload")
                        .queryParam("name", finalName)
                        .queryParam("id", finalId)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Map.class)
                .block();
    }

    public String deleteScript(Integer scriptId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", scriptId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(String.class)
                .block();
    }

    // External Scripts

    public ExternalScriptContainer getExternalScripts() {
        return client.get()
                .uri(urlBuilder.buildUrl("/external"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(ExternalScriptContainer.class)
                .block();
    }

    public ExternalScriptTO getExternalScript(Integer externalScriptId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/external", externalScriptId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(ExternalScriptTO.class)
                .block();
    }

    public ExternalScriptTO createExternalScript(ExternalScriptTO script) {
        return client.post()
                .uri(urlBuilder.buildUrl("/external"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(script), ExternalScriptTO.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(ExternalScriptTO.class)
                .block();
    }

    public Mono<DataBuffer> downloadExternalScript(Integer externalScriptId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/external/download", externalScriptId))
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataBuffer.class);
    }

    public String deleteExternalScript(Integer externalScriptId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("/external", externalScriptId))
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
