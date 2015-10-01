package com.intuit.tank.httpclient4;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testBasicAuth() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/basic-auth/test/test_pass");
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withRealm("bogus").withScheme(AuthScheme.Basic).build());

        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(401, response.getHttpCode());

        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withHost("httpbin.org").withRealm("Fake Realm").withScheme(AuthScheme.Basic).build());
        request.doGet(null);
        response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());

    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testDigestAuth() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/digest-auth/auth/test/test_pass");
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withRealm("bogus").withScheme(AuthScheme.Digest).build());

        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(401, response.getHttpCode());

        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withHost("httpbin.org").withScheme(AuthScheme.Digest).build());
        request.doGet(null);
        response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());

    }


    @Test(groups = TestGroups.FUNCTIONAL)
    public void doDelete() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/delete");
        request.doDelete(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void doGet() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/get");
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void doPost() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/post");
        request.doPost(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void doPut() {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/put");
        request.doPut(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
    }

    @Test(groups = TestGroups.FUNCTIONAL)
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

    @Test(groups = TestGroups.FUNCTIONAL)
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
        // BaseRequest request = getRequest(new TankHttpClient4(),
        // "http://httpbin.org/ip");
        // request.getHttpclient().setProxy("168.9.128.152", 8080);
        // request.doGet(null);
        // BaseResponse response = request.getResponse();
        // Assert.assertNotNull(response);
        // Assert.assertEquals(200, response.getHttpCode());
        // String body = response.getBody();
        //
        // request.doGet(null);
        // response = request.getResponse();
        // Assert.assertNotNull(response);
        // Assert.assertEquals(200, response.getHttpCode());
        // Assert.assertEquals(body, response.getBody());
        //
        // // unset proxy
        // request.getHttpclient().setProxy(null, -1);
        // request.doGet(null);
        // response = request.getResponse();
        // Assert.assertNotNull(response);
        // Assert.assertEquals(200, response.getHttpCode());
        // Assert.assertNotEquals(body, response.getBody());
    }

    @Test(groups = TestGroups.MANUAL)
    public void testSSL() {
        BaseRequest request = getRequest(new TankHttpClient4(), "https://www.pcwebshop.co.uk/");
        request.doGet(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
//        Assert.assertEquals(response.getHttpCode(), 403);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void doPostMultipart() throws IOException {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/post");
        request.setContentType(BaseRequest.CONTENT_TYPE_MULTIPART);
        request.setBody(createMultiPartBody());
        request.doPost(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void doPostMultipartwithFile() throws IOException {
        BaseRequest request = getRequest(new TankHttpClient4(), "http://httpbin.org/post");
        request.setContentType(BaseRequest.CONTENT_TYPE_MULTIPART);
        request.setBody(
                "LS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0xNzI2MTE1MzQ5Mjk4MjYNCkNvbnRlbnQtRGlzcG9zaXRpb246IGZvcm0tZGF0YTsgbmFtZT0iY3JlYXRlTmV3"
                + "U2NyaXB0Rm9ybSINCg0KY3JlYXRlTmV3U2NyaXB0Rm9ybQ0KLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0xNzI2MTE1MzQ5Mjk4MjYNCkNvbnRlbnQtRG"
                + "lzcG9zaXRpb246IGZvcm0tZGF0YTsgbmFtZT0iY3JlYXRlTmV3U2NyaXB0Rm9ybTpqX2lkdDUxOnNhdmVCdG4iDQoNCg0KLS0tLS0tLS0tLS0tLS0tLS0tLS0t"
                + "LS0tLS0tLS0xNzI2MTE1MzQ5Mjk4MjYNCkNvbnRlbnQtRGlzcG9zaXRpb246IGZvcm0tZGF0YTsgbmFtZT0iY3JlYXRlTmV3U2NyaXB0Rm9ybTpuYW1lVEYiDQo"
                + "NClRlc3RNdWx0aVBhcnQNCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tMTcyNjExNTM0OTI5ODI2DQpDb250ZW50LURpc3Bvc2l0aW9uOiBmb3JtLWRhdG"
                + "E7IG5hbWU9ImNyZWF0ZU5ld1NjcmlwdEZvcm06cHJvZHVjdE5hbWVDQl9mb2N1cyINCg0KDQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLTE3MjYxMTUzN"
                + "DkyOTgyNg0KQ29udGVudC1EaXNwb3NpdGlvbjogZm9ybS1kYXRhOyBuYW1lPSJjcmVhdGVOZXdTY3JpcHRGb3JtOnByb2R1Y3ROYW1lQ0JfaW5wdXQiDQoNCkNB"
                + "Uw0KLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0xNzI2MTE1MzQ5Mjk4MjYNCkNvbnRlbnQtRGlzcG9zaXRpb246IGZvcm0tZGF0YTsgbmFtZT0iY3JlYXR"
                + "lTmV3U2NyaXB0Rm9ybTpqX2lkdDg0Ig0KDQpVcGxvYWQgU2NyaXB0DQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLTE3MjYxMTUzNDkyOTgyNg0KQ29udG"
                + "VudC1EaXNwb3NpdGlvbjogZm9ybS1kYXRhOyBuYW1lPSJjcmVhdGVOZXdTY3JpcHRGb3JtOmpfaWR0OTEiOyBmaWxlbmFtZT0ic2FtcGxlLnhtbCINCkNvbnRlb"
                + "nQtVHlwZTogdGV4dC94bWwNCg0KPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzbnM6c2Vzc2lvbiB4bWxu"
                + "czpzbnM9InVybjpwcm94eS9jb252ZXJzYXRpb24vdjEiIGZvbGxvd1JlZGlyZWN0cz0idHJ1ZSI+Cjx0cmFuc2FjdGlvbiB4bWxucz0idXJuOnByb3h5L2NvbnZ"
                + "lcnNhdGlvbi92MSI+CiAgICA8cmVxdWVzdD4KICAgICAgICA8cHJvdG9jb2w+aHR0cDwvcHJvdG9jb2w+CiAgICAgICAgPGZpcnN0TGluZT5HRVQgL3Byb2plY3"
                + "RzIEhUVFAvMS4xPC9maXJzdExpbmU+CiAgICAgICAgPGhlYWRlcnM+CiAgICAgICAgICAgIDxoZWFkZXI+CiAgICAgICAgICAgICAgICA8a2V5Pkhvc3Q8L2tle"
                + "T4KICAgICAgICAgICAgICAgIDx2YWx1ZT5hVzUwWlhKdVlXd3RkR0Z1YXkxd2IyTXRNVEF4T1RneE56TTNPQzUxY3kxM1pYTjBMVEl1Wld4aUxtRnRZWHB2Ym1GM"
                + "2N5NWpiMjA9PC92YWx1ZT4KICAgICAgICAgICAgPC9oZWFkZXI+CiAgICAgICAgICAgIDxoZWFkZXI+CiAgICAgICAgICAgICAgICA8a2V5PlVzZXItQWdlbnQ8"
                + "L2tleT4KICAgICAgICAgICAgICAgIDx2YWx1ZT5UVzk2YVd4c1lTODFMakFnS0ZkcGJtUnZkM01nVGxRZ05pNHhPeUJYVDFjMk5Ec2djblk2TkRFdU1Da2dSMlZ"
                + "qYTI4dk1qQXhNREF4TURFZ1JtbHlaV1p2ZUM4ME1TNHc8L3ZhbHVlPgogICAgICAgICAgICA8L2hlYWRlcj4KICAgICAgICAgICAgPGhlYWRlcj4KICAgICAgIC"
                + "AgICAgICAgIDxrZXk+QWNjZXB0PC9rZXk+CiAgICAgICAgICAgICAgICA8dmFsdWU+ZEdWNGRDOW9kRzFzTEdGd2NHeHBZMkYwYVc5dUwzaG9kRzFzSzNodGJDe"
                + "GhjSEJzYVdOaGRHbHZiaTk0Yld3N2NUMHdMamtzS2k4cU8zRTlNQzQ0PC92YWx1ZT4KICAgICAgICAgICAgPC9oZWFkZXI+CiAgICAgICAgICAgIDxoZWFkZXI+"
                + "CiAgICAgICAgICAgICAgICA8a2V5PkFjY2VwdC1MYW5ndWFnZTwva2V5PgogICAgICAgICAgICAgICAgPHZhbHVlPlpXNHRWVk1zWlc0N2NUMHdMalU9PC92YWx1"
                + "ZT4KICAgICAgICAgICAgPC9oZWFkZXI+CiAgICAgICAgICAgIDxoZWFkZXI+CiAgICAgICAgICAgICAgICA8a2V5PkFjY2VwdC1FbmNvZGluZzwva2V5PgogICAg"
                + "ICAgICAgICAgICAgPHZhbHVlPlozcHBjQ3dnWkdWbWJHRjBaUT09PC92YWx1ZT4KICAgICAgICAgICAgPC9oZWFkZXI+CiAgICAgICAgICAgIDxoZWFkZXI+CiA"
                + "gICAgICAgICAgICAgICA8a2V5PlJlZmVyZXI8L2tleT4KICAgICAgICAgICAgICAgIDx2YWx1ZT5hSFIwY0RvdkwybHVkR1Z5Ym1Gc0xYUmhibXN0Y0c5akxURX"
                + "dNVGs0TVRjek56Z3VkWE10ZDJWemRDMHlMbVZzWWk1aGJXRjZiMjVoZDNNdVkyOXRMM05qY21sd2RITXZZM0psWVhSbFRtVjNVMk55YVhCMExtcHpaajlqYVdRO"
                + "U1RPT08L3ZhbHVlPgogICAgICAgICAgICA8L2hlYWRlcj4KICAgICAgICAgICAgPGhlYWRlcj4KICAgICAgICAgICAgICAgIDxrZXk+Q29va2llPC9rZXk+CiA"
                + "gICAgICAgICAgICAgICA8dmFsdWU+U2xORlUxTkpUMDVKUkQweU5VRXhNams1UVRBMU9EWkNSRVUwUkVNNFJrSTVNa1l5TVRCQlFqTkRRdz09PC92YWx1ZT4KIC"
                + "AgICAgICAgICAgPC9oZWFkZXI+CiAgICAgICAgICAgIDxoZWFkZXI+CiAgICAgICAgICAgICAgICA8a2V5PkNvbm5lY3Rpb248L2tleT4KICAgICAgICAgICAgIC"
                + "AgIDx2YWx1ZT5hMlZsY0MxaGJHbDJaUT09PC92YWx1ZT4KICAgICAgICAgICAgPC9oZWFkZXI+CiAgICAgICAgICAgIDxoZWFkZXI+CiAgICAgICAgICAgICAgIC"
                + "A8a2V5PlgtUFJPWFktQVBQPC9rZXk+CiAgICAgICAgICAgICAgICA8dmFsdWU+Y21Wa2FYSmxZM1JEYjJ4c1lYQnpaUT09PC92YWx1ZT4KICAgICAgICAgICAgPC9"
                + "oZWFkZXI+CiAgICAgICAgICAgIDxoZWFkZXI+CiAgICAgICAgICAgICAgICA8a2V5PlgtUmVkaXJlY3QtTG9jYXRpb248L2tleT4KICAgICAgICAgICAgICAgIDx2"
                + "YWx1ZT5hSFIwY0RvdkwybHVkR1Z5Ym1Gc0xYUmhibXN0Y0c5akxURXdNVGs0TVRjek56Z3VkWE10ZDJWemRDMHlMbVZzWWk1aGJXRjZiMjVoZDNNdVkyOXRMM0J5Y"
                + "jJwbFkzUnpMdz09PC92YWx1ZT4KICAgICAgICAgICAgPC9oZWFkZXI+CiAgICAgICAgPC9oZWFkZXJzPgogICAgPC9yZXF1ZXN0PgogICAgPHJlc3BvbnNlPgogIC"
                + "AgICAgIDxmaXJzdExpbmU+SFRUUC8xLjEgMzA0IE5vdCBNb2RpZmllZDwvZmlyc3RMaW5lPgogICAgICAgIDxoZWFkZXJzPgogICAgICAgICAgICA8aGVhZGVyPgo"
                + "gICAgICAgICAgICAgICAgPGtleT5EYXRlPC9rZXk+CiAgICAgICAgICAgICAgICA8dmFsdWU+VjJWa0xDQXpNQ0JUWlhBZ01qQXhOU0F4T0Rvd09Ub3dNU0JIVFZRP"
                + "TwvdmFsdWU+CiAgICAgICAgICAgIDwvaGVhZGVyPgogICAgICAgICAgICA8aGVhZGVyPgogICAgICAgICAgICAgICAgPGtleT5FVGFnPC9rZXk+CiAgICAgICAgICAg"
                + "ICAgICA8dmFsdWU+Vnk4aU1qWXdMVEUwTkRNd01qa3dPRFl3TURBaTwvdmFsdWU+CiAgICAgICAgICAgIDwvaGVhZGVyPgogICAgICAgICAgICA8aGVhZGVyPgogICA"
                + "gICAgICAgICAgICAgPGtleT5TZXJ2ZXI8L2tleT4KICAgICAgICAgICAgICAgIDx2YWx1ZT5RWEJoWTJobExVTnZlVzkwWlM4eExqRT08L3ZhbHVlPgogICAgICAgIC"
                + "AgICA8L2hlYWRlcj4KICAgICAgICAgICAgPGhlYWRlcj4KICAgICAgICAgICAgICAgIDxrZXk+Q29ubmVjdGlvbjwva2V5PgogICAgICAgICAgICAgICAgPHZhbHVl"
                + "PmEyVmxjQzFoYkdsMlpRPT08L3ZhbHVlPgogICAgICAgICAgICA8L2hlYWRlcj4KICAgICAgICA8L2hlYWRlcnM+CiAgICAgICAgPGJvZHk+PC9ib2R5PgogICAgPC9"
                + "yZXNwb25zZT4KPC90cmFuc2FjdGlvbj4KPC9zbnM6c2Vzc2lvbj4NCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tMTcyNjExNTM0OTI5ODI2DQpDb250ZW50LU"
                + "Rpc3Bvc2l0aW9uOiBmb3JtLWRhdGE7IG5hbWU9ImNyZWF0ZU5ld1NjcmlwdEZvcm06Z3JvdXBUYWJsZTpmaWx0ZXJHcm91cE5hbWU6ZmlsdGVyIg0KDQoNCi0tLS0"
                + "tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tMTcyNjExNTM0OTI5ODI2DQpDb250ZW50LURpc3Bvc2l0aW9uOiBmb3JtLWRhdGE7IG5hbWU9ImNyZWF0ZU5ld1Njcmlwd"
                + "EZvcm06Z3JvdXBUYWJsZTpmaWx0ZXJHcm91cFByb2R1Y3Q6ZmlsdGVyIg0KDQoNCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tMTcyNjExNTM0OTI5ODI2DQp"
                + "Db250ZW50LURpc3Bvc2l0aW9uOiBmb3JtLWRhdGE7IG5hbWU9ImNyZWF0ZU5ld1NjcmlwdEZvcm06ZmlsdGVyVGFibGVJZDpmaWx0ZXJOYW1lOmZpbHRlciINCg0KD"
                + "QotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLTE3MjYxMTUzNDkyOTgyNg0KQ29udGVudC1EaXNwb3NpdGlvbjogZm9ybS1kYXRhOyBuYW1lPSJjcmVhdGVOZXd"
                + "TY3JpcHRGb3JtOmZpbHRlclRhYmxlSWQ6ZmlsdGVyUHJvZHVjdDpmaWx0ZXIiDQoNCg0KLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0xNzI2MTE1MzQ5Mjk4M"
                + "jYNCkNvbnRlbnQtRGlzcG9zaXRpb246IGZvcm0tZGF0YTsgbmFtZT0iY3JlYXRlTmV3U2NyaXB0Rm9ybTpmaWx0ZXJUYWJsZUlkOjA6al9pZHQxMDJfaW5wdXQiDQo"
                + "NCm9uDQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLTE3MjYxMTUzNDkyOTgyNg0KQ29udGVudC1EaXNwb3NpdGlvbjogZm9ybS1kYXRhOyBuYW1lPSJjcmVhd"
                + "GVOZXdTY3JpcHRGb3JtOmZpbHRlclRhYmxlSWQ6MTpqX2lkdDEwMl9pbnB1dCINCg0Kb24NCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tMTcyNjExNTM0OTI"
                + "5ODI2DQpDb250ZW50LURpc3Bvc2l0aW9uOiBmb3JtLWRhdGE7IG5hbWU9ImNyZWF0ZU5ld1NjcmlwdEZvcm06ZmlsdGVyVGFibGVJZF9zY3JvbGxTdGF0ZSINCg0KM"
                + "CwwDQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLTE3MjYxMTUzNDkyOTgyNg0KQ29udGVudC1EaXNwb3NpdGlvbjogZm9ybS1kYXRhOyBuYW1lPSJqYXZheC5"
                + "mYWNlcy5WaWV3U3RhdGUiDQoNCi0yMDU5ODE2MTg5MDc2NDYzMzg5Oi01NDAzNTEzNDYxNzE3MjAzMDUNCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tMTcyN"
                + "jExNTM0OTI5ODI2LS0NCg==");
        request.doPost(null);
        BaseResponse response = request.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertNotNull(response.getBody());
    }

    private String createMultiPartBody() throws IOException {
        HttpEntity entity = MultipartEntityBuilder.create().addTextBody("textPart", "<xml>here is sample xml</xml>", ContentType.APPLICATION_XML).build();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        entity.writeTo(byteArrayOutputStream);
        String ret = new String(byteArrayOutputStream.toByteArray());

        System.out.println(ret);
        ret = toBase64(byteArrayOutputStream.toByteArray());
        System.out.println(ret);
        return ret;
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

    /**
     * Returns a string's base64 encoding
     * 
     * @param toEncode
     * @return base64 string
     */
    public String toBase64(byte[] bytes) {
        String ret = null;
        try {
            ret = new String(Base64.encodeBase64(bytes), Charset.forName("utf-8")).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

}
