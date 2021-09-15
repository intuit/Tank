package com.intuit.tank.http.json;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpLogger;

public class PlainTextRequest extends BaseRequest {

    private static final String CONTENT_TYPE = "text/plain";

    public PlainTextRequest(TankHttpClient client, TankHttpLogger logUtil) {
        super(client, logUtil);
        setContentType(CONTENT_TYPE);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String getBody() {
        String result = body;

        return result != null ? result : "";
    }

    @Override
    public void setKey(String key, String value) {

    }

    @Override
    public String getKey(String key) {
        return null;
    }

    @Override
    public void setNamespace(String name, String value) {

    }

}
