/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
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


    public DataFileDescriptorContainer getDatafiles() {
        return client.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataFileDescriptorContainer.class)
                .block();
    }

    public DataFileDescriptor getDatafile(Integer datafileId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", datafileId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataFileDescriptor.class)
                .block();
    }

    public String getDatafileContent(Integer datafileId) {
        Flux<DataBuffer> dataBufferFlux = WebClient.create(urlBuilder.buildUrl("")) // need webclient.create for query params
                .get()
                .uri(uriBuilder -> uriBuilder.path("/content")
                        .queryParam("id", datafileId.toString())
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToFlux(DataBuffer.class);

        DataBuffer dataBuffer = DataBufferUtils.join(dataBufferFlux).block();
        String datafileContent = dataBuffer.toString(StandardCharsets.UTF_8);

        DataBufferUtils.release(dataBuffer);

        return datafileContent;
    }

    public Mono<DataBuffer> downloadDatafile(Integer datafileId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/download", datafileId))
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(DataBuffer.class);
    }

    public Map<String, String> uploadDatafile(Integer id, MultipartFile file) {
        return client.post()
                .uri(uriBuilder -> uriBuilder
                        .path(urlBuilder.buildUrl("/upload"))
                        .queryParam("id", id)
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(file), MultipartFile.class)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ClientException(body,
                                        response.statusCode().value()))))
                .bodyToMono(Map.class)
                .block();
    }

    public String deleteDatafile(Integer datafileID) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", datafileID))
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
