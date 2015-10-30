package com.intuit.tank.http;

public class MockBaseRequest extends BaseRequest {

    public MockBaseRequest(TankHttpClient httpclient) {
        super(httpclient, new MockLogUtil());
    }

    @Override
    public void setKey(String key, String value) {

    }

    @Override
    public String getKey(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNamespace(String name, String value) {

    }

}
