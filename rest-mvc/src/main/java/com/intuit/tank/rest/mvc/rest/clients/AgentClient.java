package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.Headers;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Map;

public class AgentClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/api/v2/agent";

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
                .bodyToMono(String.class)
                .block();
    }

    public Mono<DataBuffer> getSupportFiles() {
        return client.get()
                .uri(urlBuilder.buildUrl("/support-files"))
                .retrieve()
                .bodyToMono(DataBuffer.class);
    }

    public AgentTestStartData agentReady(AgentData agentData) {
        return client.post()
                .uri(urlBuilder.buildUrl("/ready"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(agentData), AgentData.class)
                .retrieve()
                .bodyToMono(AgentTestStartData.class)
                .block();
    }

    public Headers getHeaders() {
        return client.get()
                .uri(urlBuilder.buildUrl("/headers"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Headers.class)
                .block();
    }

    public TankHttpClientDefinitionContainer getClients() {
        return client.get()
                .uri(urlBuilder.buildUrl("/clients"))
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(TankHttpClientDefinitionContainer.class)
                .block();
    }

    public CloudVmStatus getInstanceStatus(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/status/", instanceId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CloudVmStatus.class)
                .block();
    }

    public Void setInstanceStatus(String instanceId, CloudVmStatus status) {
        return client.put()
                .uri(urlBuilder.buildUrl("/instance/status/", instanceId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(status), CloudVmStatus.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void stopInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/stop/", instanceId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void pauseInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/pause/", instanceId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void resumeInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/resume/", instanceId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void killInstance(String instanceId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance/kill/", instanceId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
