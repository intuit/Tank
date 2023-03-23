package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.models.jobs.CreateJobRequest;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobContainer;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobTO;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class JobClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/api/v2/jobs";

    public JobClient(String serviceUrl)  {
        super(serviceUrl, null, null);
    }

    public JobClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public JobContainer getAllJobs() {
        return client.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JobContainer.class)
                .block();
    }

    public JobTO getJob(Integer jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", jobId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JobTO.class)
                .block();
    }

    public JobContainer getJobsByProject(Integer projectId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/project", projectId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JobContainer.class)
                .block();
    }

    public Map<String, String> createJob(CreateJobRequest request) {
        return client.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CreateJobRequest.class)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public List<Map<String, String>> getAllJobStatus() {
        return client.get()
                .uri(urlBuilder.buildUrl("/status"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }

    public String getJobStatus(Integer jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/status", jobId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getJobVMStatuses(String jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/instance-status", jobId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public CloudVmStatusContainer deleteProject(Integer projectId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", projectId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CloudVmStatusContainer.class)
                .block();
    }

    // Job Status Setters

    public Void startJob(Integer jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/start", jobId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void stopJob(Integer jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/stop", jobId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void pauseJob(Integer jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/pause", jobId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void resumeJob(Integer jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/resume", jobId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Void killJob(Integer jobId) {
        return client.get()
                .uri(urlBuilder.buildUrl("/kill", jobId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
