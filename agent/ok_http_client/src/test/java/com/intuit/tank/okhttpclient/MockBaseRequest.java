package com.intuit.tank.okhttpclient;

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankHttpClient;

public class MockBaseRequest extends BaseRequest {

    protected MockBaseRequest(TankHttpClient httpclient) {
        super(httpclient, new MockLogUtil());
    }

    @Override
    public void setKey(String key, String value) {}

    @Override
    public String getKey(String key) {
        return null;
    }

    @Override
    public void setNamespace(String name, String value) {

    }

}
