package com.intuit.tank.httpclient4;

import org.apache.tomcat.util.net.URL;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.test.TestGroups;

public class TankHttpClient4Test {

    @Test(groups=TestGroups.FUNCTIONAL)
    public void addAuth() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/basic-auth/test/test_pass");
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withRealm("bogus").withScheme(AuthScheme.BASIC).build());
       
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(401, response.getHttpCode());
        
        
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withRealm("Fake Realm").withScheme(AuthScheme.BASIC).build());
        request.doGet(null);
        response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());
        
    }


    @Test(groups=TestGroups.FUNCTIONAL)
    public void doDelete() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/delete");
        request.doDelete(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
    }

    @Test(groups=TestGroups.FUNCTIONAL)
    public void doGet() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://hc.apache.org/httpclient-3.x/authentication.html");
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());
    }

    

    @Test(groups=TestGroups.FUNCTIONAL)
    public void doPost() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/post");
        request.doPost(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());
    }

    @Test(groups=TestGroups.FUNCTIONAL)
    public void doPut() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/put");
        request.doPut(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
    }

    @Test(groups=TestGroups.FUNCTIONAL)
    public void clearSession() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/cookies");
        request.getHttpclient().setCookie(TankCookie.builder().withName("test-cookie").withValue("test-value").withDomain("httpbin.org").withPath("/").build());
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertTrue(response.getBody().contains("test-cookie"));
        request.getHttpclient().clearSession();
        
        request.doGet(null);
        response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertTrue(!response.getBody().contains("test-cookie"));
    }
    
    @Test(groups=TestGroups.FUNCTIONAL)
    public void setCookie() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/cookies");
        request.getHttpclient().setCookie(TankCookie.builder().withName("test-cookie").withValue("test-value").withDomain("httpbin.org").withPath("/").build());
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertTrue(response.getBody().contains("test-cookie"));
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void setProxy() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/ip");
        request.getHttpclient().setProxy("168.9.128.152", 8080);
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        String body = response.getBody();

        request.doGet(null);
        response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertEquals(body, response.getBody());

        // unset proxy
        request.getHttpclient().setProxy(null, -1);
        request.doGet(null);
        response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotEquals(body, response.getBody());
    }
    
    @Test(groups = TestGroups.FUNCTIONAL)
    public void testSSL() {
        BaseRequest request = getRequest(new TankHttpClient4(), "https://www.pcwebshop.co.uk/");
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getHttpCode(), 403);
    }


    
    private BaseRequest getRequest(TankHttpClient client, String url) {
        try {
            URL u = new URL(url);
            BaseRequest request = new MockBaseRequest(client);
            request.setHost(u.getHost());
            request.setPath(u.getPath());
            request.setProtocol(u.getProtocol());
            request.setPort(u.getPort());
            return request;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
