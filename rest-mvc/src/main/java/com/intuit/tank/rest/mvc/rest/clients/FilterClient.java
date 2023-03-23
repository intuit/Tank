package com.intuit.tank.rest.mvc.rest.clients;

import com.intuit.tank.rest.mvc.rest.models.filters.FilterContainer;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterGroupContainer;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterGroupTO;
import com.intuit.tank.rest.mvc.rest.models.filters.FilterTO;
import com.intuit.tank.rest.mvc.rest.models.projects.AutomationRequest;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Map;

public class FilterClient extends BaseClient{

    private static final String SERVICE_BASE_URL = "/api/v2/filters";

    public FilterClient(String serviceUrl)  {
        super(serviceUrl, null, null);
    }

    public FilterClient(String serviceUrl, final String proxyServer, final Integer proxyPort) {
        super(serviceUrl, proxyServer, proxyPort);
    }

    protected String getServiceBaseUrl() {
        return SERVICE_BASE_URL;
    }


    public FilterContainer getFilters() {
        return client.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FilterContainer.class)
                .block();
    }

    public FilterGroupContainer getFilterGroups() {
        return client.get()
                .uri(urlBuilder.buildUrl("/groups"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FilterGroupContainer.class)
                .block();
    }

    public FilterTO getFilter(Integer filterId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", filterId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FilterTO.class)
                .block();
    }

    public FilterGroupTO getFilterGroup(Integer filterGroupId) {
        return client.get()
                .uri(urlBuilder.buildUrl("", filterGroupId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FilterGroupTO.class)
                .block();
    }

    public String deleteFilter(Integer filterId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", filterId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String deleteFilterGroup(Integer filterGroupId) {
        return client.delete()
                .uri(urlBuilder.buildUrl("", filterGroupId))
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
