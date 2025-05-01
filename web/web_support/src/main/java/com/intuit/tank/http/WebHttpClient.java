package com.intuit.tank.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * WebHttpClient
 *
 * @author Shawn Park
 */
public class WebHttpClient {
    private static final Logger LOG = LogManager.getLogger(WebHttpClient.class);

    private HttpClient _httpclient;

    public WebHttpClient() {
        _httpclient = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();
    }

    public HttpResponse<String> Post(String requestUri, Map<Object, Object> requestParameters)  {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(requestUri))
                    .POST(formBodyPublisher(requestParameters))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            LOG.debug("Web POST Request: {}", request.toString());
            return _httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch(Exception e) {
            LOG.error("Web Post Exception", e);
        }
        return null;
    }

    public static HttpRequest.BodyPublisher formBodyPublisher(Map<Object, Object> requestParameters) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : requestParameters.entrySet()) {
            if (!builder.isEmpty()) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
