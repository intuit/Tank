package com.intuit.tank.http.json;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpLogger;
import com.intuit.tank.vm.common.util.JSONBuilder;

public class JsonRequest extends BaseRequest {

    private static Logger logger = LogManager.getLogger(JsonRequest.class);
    private static final String CONTENT_TYPE = "application/json";

    private JSONBuilder builder;

    public JsonRequest(TankHttpClient client, TankHttpLogger logUtil) {
        super(client, logUtil);
        builder = new JSONBuilder();
        setContentType(CONTENT_TYPE);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getBody() {
        String result = body;
        if (StringUtils.isBlank(result)) {
            try {
                result = builder.toJsonString();
                return result;
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        return result != null ? result : "";
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void setKey(String key, String value) {
        builder.add(key, value);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void setNamespace(String name, String value) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey(String key) {
        return builder.getValue(key);
    }

}
