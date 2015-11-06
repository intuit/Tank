package com.intuit.tank.http.multipart;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpLogger;

public class MultiPartRequest extends BaseRequest {


    public MultiPartRequest(TankHttpClient client, TankHttpLogger logUtil) {
        super(client, logUtil);
        setContentType(CONTENT_TYPE_MULTIPART);
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
