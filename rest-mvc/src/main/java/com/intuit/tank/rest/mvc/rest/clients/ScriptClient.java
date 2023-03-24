/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.scripts.ExternalScriptContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ExternalScriptTO;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptDescriptionContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptTO;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ScriptClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/api/v2/projects";

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
