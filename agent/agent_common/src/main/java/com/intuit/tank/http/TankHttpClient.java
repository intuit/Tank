package com.intuit.tank.http;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.BaseRequest;

public interface TankHttpClient {

    /**
     * Execute the GET. Use this to base the response off of the content type
     */
    public void doGet(BaseRequest request);

    /**
     * Execute the post. Use this to force the type of response
     * 
     * @param newResponse
     *            The response object to populate
     */
    public void doPut(BaseRequest request);

    /**
     * Execute the delete request.
     * 
     * @param response
     *            The response object to populate
     */
    public void doDelete(BaseRequest request);

    /**
     * Execute the options request.
     * 
     * @param response
     *            The response object to populate
     */
    public void doOptions(BaseRequest request);
    
    /**
     * Execute the POST.
     */
    public void doPost(BaseRequest request);

    /**
     * Adds the authentication
     * 
     * @param creds
     *            the authentication creds and details.
     */
    public void addAuth(AuthCredentials creds);

    /**
     * resets the session data including cookies.
     */
    public void clearSession();

    /**
     * 
     * @param cookie
     */
    public void setCookie(TankCookie cookie);

    /**
     * 
     * @param proxyhost
     * @param proxyport
     */
    public void setProxy(String proxyhost, int proxyport);
    
    /**
     * 
     * @param connectionTimeout
     */
    public void setConnectionTimeout(long connectionTimeout);

}