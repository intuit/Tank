package com.intuit.tank.http;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.http.TankHttpClient;

public class MockTankHttpClient implements TankHttpClient {

    @Override
    public void doGet(BaseRequest request) {

    }

    @Override
    public void doPut(BaseRequest request) {

    }

    @Override
    public void doDelete(BaseRequest request) {

    }

    @Override
    public void doOptions(BaseRequest request) {

    }

    @Override
    public void doPost(BaseRequest request) {

    }

    @Override
    public void addAuth(AuthCredentials creds) {

    }

    @Override
    public void clearSession() {

    }

    @Override
    public void setCookie(TankCookie cookie) {

    }

    @Override
    public void setProxy(String proxyhost, int proxyport) {

    }

    @Override
    public void setConnectionTimeout(long connectionTimeout) {

    }

    @Override
    public Object createHttpClient() {
        return null;
    }

    @Override
    public void setHttpClient(Object httpClient) {

    }
}
