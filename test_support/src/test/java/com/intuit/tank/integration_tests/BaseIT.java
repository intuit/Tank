package com.intuit.tank.integration_tests;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseIT {

    private static final Logger LOG = LogManager.getLogger(BaseIT.class);
    private static final String SSM_PARAMETER_NAME = "/Tank/qa/integration-tests/api/token";
    private static final String CONFIG_FILE = "test-config.properties";
    private static final String API_TOKEN_PROPERTY = "tank.api.token";

    // Required for API calls
    public static final String QA_BASE_URL = "https://qa-tank.perf.a.intuit.com";
    protected static final String API_TOKEN = getApiToken();
    protected static final String API_TOKEN_HEADER = "Bearer " + API_TOKEN;
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String CONTENT_TYPE_HEADER = "Content-Type";
    protected static final String CONTENT_TYPE_VALUE = "application/json";
    protected static final String ACCEPT_HEADER = "Accept";
    protected static final String ACCEPT_VALUE = "application/json";
    protected static final HttpClient httpClient = getHttpClient();


    protected static HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Gets the API token using two techniques:
     * 1. From test-config.properties file in resources
     * 2. If not found, from AWS SSM Parameter Store
     *
     * @return The API token or null if not found
     */
    private static String getApiToken() {
        String token = getTokenFromProperties();

        if (token == null || token.isEmpty()) {
            // If not found in properties, try SSM
            token = getTokenFromSSM();
        }

        return token;
    }

    /**
     * Attempts to read the API token from test-config.properties file
     * This will be used when running locally
     *
     * @return The API token or null if not found
     */
    private static String getTokenFromProperties() {
        try {
            Properties props = new Properties();
            InputStream is = BaseIT.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

            if (is != null) {
                props.load(is);
                is.close();

                String token = props.getProperty(API_TOKEN_PROPERTY);
                if (token != null && !token.isEmpty()) {
                    return token;
                }
            } else {
                LOG.debug("Properties file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            LOG.debug("Error loading properties file: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Attempts to read the API token from AWS SSM Parameter Store
     *
     * @return The API token or null if not found
     */
    private static String getTokenFromSSM() {
        try {
            try (SsmClient ssmClient = SsmClient.builder().build()) {
                GetParameterResponse response = ssmClient.getParameter(
                        GetParameterRequest.builder()
                                .name(SSM_PARAMETER_NAME)
                                .withDecryption(true)
                                .build());

                String token = response.parameter().value();
                if (token != null && !token.isEmpty()) {
                    return token;
                }
            }
        } catch (Exception e) {
            LOG.error("Error retrieving token from SSM: " + e.getMessage());
        }

        return null;
    }
}
