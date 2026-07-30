package com.intuit.tank.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Canonical job-creation request payloads used by the system tests.
 */
public final class JobCreationContract {

    static final int INCREASING_CONFIGURED_USERS = 2800;
    static final String STANDARD_TARGET_RAMP_RATE = "0.382";
    static final String STANDARD_TARGET_RATE_PER_AGENT = "0.211";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JobCreationContract() {
    }

    enum Profile {
        INCREASING("contracts/increasing-job.json"),
        STANDARD("contracts/standard-job.json");

        private final String resourcePath;

        Profile(String resourcePath) {
            this.resourcePath = resourcePath;
        }
    }

    static String buildRequestJson(Profile profile, int projectId, String jobInstanceName)
            throws IOException {
        String template = loadResource(profile.resourcePath);
        return template
                .replace("${projectId}", Integer.toString(projectId))
                .replace("${jobInstanceName}", jobInstanceName);
    }

    static JsonNode parseRequest(String requestJson) throws IOException {
        return MAPPER.readTree(requestJson);
    }

    static void assertMatchesContract(String actualRequestJson, Profile profile,
                                      int projectId, String jobInstanceName) throws IOException {
        JsonNode expected = parseRequest(buildRequestJson(profile, projectId, jobInstanceName));
        JsonNode actual = parseRequest(actualRequestJson);
        assertJsonEquals(expected, actual, "");
    }

    private static void assertJsonEquals(JsonNode expected, JsonNode actual, String path)
            throws IOException {
        if (expected.equals(actual)) {
            return;
        }
        if (expected.isObject() && actual.isObject()) {
            ObjectNode expectedObject = (ObjectNode) expected;
            expectedObject.fieldNames().forEachRemaining(field -> {
                String childPath = path.isEmpty() ? field : path + "." + field;
                if (!actual.has(field)) {
                    throw new AssertionError("Missing field " + childPath);
                }
                try {
                    assertJsonEquals(expected.get(field), actual.get(field), childPath);
                } catch (IOException e) {
                    throw new AssertionError("Failed comparing " + childPath, e);
                }
            });
            actual.fieldNames().forEachRemaining(field -> {
                if (!expected.has(field)) {
                    throw new AssertionError("Unexpected field " + path + "." + field);
                }
            });
            return;
        }
        if (expected.isArray() && actual.isArray()) {
            if (expected.size() != actual.size()) {
                throw new AssertionError("Array size mismatch at " + path + ": expected "
                        + expected.size() + " got " + actual.size());
            }
            for (int i = 0; i < expected.size(); i++) {
                assertJsonEquals(expected.get(i), actual.get(i), path + "[" + i + "]");
            }
            return;
        }
        throw new AssertionError("JSON mismatch at " + path + ": expected "
                + expected + " got " + actual);
    }

    private static String loadResource(String classpathPath) throws IOException {
        try (InputStream in = JobCreationContract.class.getClassLoader()
                .getResourceAsStream(classpathPath)) {
            if (in == null) {
                throw new IOException("Missing classpath resource: " + classpathPath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
