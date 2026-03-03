package com.intuit.tank.http.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.json.JsonMapper;

/**
 * Global shared, threads-safe JsonMapper instance.
 */
public class GenericJsonHandler {
    private static final Logger logger = LogManager.getLogger(GenericJsonHandler.class);
    private static final JsonMapper jsonMapper = JsonMapper.builder().build();

    /**
     * Converts a JSON string to a Java object of a specified class.
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return jsonMapper.readValue(json, clazz);
        } catch (Exception ex) {
            logger.warn("Unable to parse the response string as a JSON object: {}", json, ex);
        }
        return null;
    }
}
