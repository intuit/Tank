package com.intuit.tank.runner;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.http.TankHttpClient;

/**
 * @author denisa
 *
 */
public class TestHttpClient implements TankHttpClient {

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#doGet(com.intuit.tank.http.BaseRequest)
     */
    @Override
    public void doGet(BaseRequest request) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#doPut(com.intuit.tank.http.BaseRequest)
     */
    @Override
    public void doPut(BaseRequest request) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#doDelete(com.intuit.tank.http.BaseRequest)
     */
    @Override
    public void doDelete(BaseRequest request) {
    }


    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#doOptions(com.intuit.tank.http.BaseRequest)
     */
    @Override
    public void doOptions(BaseRequest request) {
    }
    
    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#doPost(com.intuit.tank.http.BaseRequest)
     */
    @Override
    public void doPost(BaseRequest request) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#doPatch(com.intuit.tank.http.BaseRequest)
     */
    @Override
    public void doPatch(BaseRequest request) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#addAuth(com.intuit.tank.http.AuthCredentials)
     */
    @Override
    public void addAuth(AuthCredentials creds) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#clearSession()
     */
    @Override
    public void clearSession() {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#setCookie(com.intuit.tank.http.TankCookie)
     */
    @Override
    public void setCookie(TankCookie cookie) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#setProxy(java.lang.String, int)
     */
    @Override
    public void setProxy(String proxyhost, int proxyport) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#setConnectionTimeout(long)
     */
    @Override
    public void setConnectionTimeout(long connectionTimeout) {
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#createHttpClient()
     */
    @Override
    public Object createHttpClient() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.intuit.tank.http.TankHttpClient#setHttpClient(Object)
     */
    @Override
    public void setHttpClient(Object httpClient) {}
}
