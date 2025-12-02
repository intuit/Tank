package com.intuit.tank.httpclient3;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.intuit.tank.http.AuthCredentials;
import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.http.TankCookie;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.test.TestGroups;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TankHttpClient3Test {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort()
                .withRootDirectory("src/test/resources"));
        wireMockServer.start();
        wireMockServer.resetAll();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void addBasicAuth() {

        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/basic-auth/test/test_pass");
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withRealm("bogus").withScheme(AuthScheme.Basic).build());
        request.addHeader("X-Test-Mode", "Fail");

        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(401, response.getHttpCode());


        request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/basic-auth/test/test_pass");
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withRealm("Fake Realm").withScheme(AuthScheme.Basic).build());
        request.addHeader("X-Test-Mode", "Pass");

        request.doGet(null);
        response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertNotNull(response.getBody());

    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void addDigestAuth() {
        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/digest-auth/auth/test/test_pass");
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withScheme(AuthScheme.Basic).build());
        request.addHeader("X-Test-Mode", "Fail");

        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(401, response.getHttpCode());

        request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/digest-auth/auth/test/test_pass");
        request.getHttpclient().addAuth(AuthCredentials.builder().withUserName("test").withPassword("test_pass").withScheme(AuthScheme.Digest).build());
        request.addHeader("X-Test-Mode", "Pass");

        request.doGet(null);
        response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertNotNull(response.getBody());

    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void doDelete() {
        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/delete");
        request.doDelete(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void doGet() {
        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/get");
        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void doPost() {
        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/post");
        request.setBody("{\"title\":\"Direct deposit with Credit Karma Money™ checking account¹\"}");
        request.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        request.doPost(null);
        BaseResponse response = request.getResponse();
        verify(exactly(1), postRequestedFor(urlEqualTo("/post"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing("Money™")));
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void doPut() {
        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/put");
        request.setBody("{\"title\":\"Direct deposit with Credit Karma Money™ checking account¹\"}");
        request.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        request.doPut(null);
        BaseResponse response = request.getResponse();
        verify(exactly(1), putRequestedFor(urlEqualTo("/put"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing("Money™")));
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void clearSession() {
        wireMockServer.stubFor(get(urlEqualTo("/cookies"))
                .withCookie("test-cookie", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"cookies\": {\"test-cookie\": \"test-value\"}}"))
                .atPriority(1)
        );

        wireMockServer.stubFor(get(urlEqualTo("/cookies"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"cookies\": {}}"))
                .atPriority(2)
        );

        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/cookies");
        request.getHttpclient().setCookie(TankCookie.builder().withName("test-cookie").withValue("test-value").withDomain("localhost").withPath("/").build());
        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertTrue(response.getCookies().get("test-cookie").equals("test-value"));
        request.getHttpclient().clearSession();

        request.doGet(null);
        response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertTrue(response.getCookies().isEmpty());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void setCookie() {
        wireMockServer.stubFor(get(urlEqualTo("/cookies"))
                .withCookie("test-cookie", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"cookies\": {\"test-cookie\": \"test-value\"}}"))
                .atPriority(1)
        );

        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/cookies");
        request.getHttpclient().setCookie(TankCookie.builder().withName("test-cookie").withValue("test-value").withDomain("localhost").withPath("/").build());
        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertEquals("test-value", response.getCookies().get("test-cookie"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testBrotliEncoding() {
        // Pre-compressed brotli data for "Hello, Brotli!"
        byte[] brotliCompressed = java.util.Base64.getDecoder().decode("jwaASGVsbG8sIEJyb3RsaSED");
        String expectedText = "Hello, Brotli!";
        
        wireMockServer.stubFor(get(urlEqualTo("/brotli"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Encoding", "br")
                        .withHeader("Content-Type", "text/plain")
                        .withBody(brotliCompressed))
        );

        BaseRequest request = getRequest(new TankHttpClient3(), wireMockServer.baseUrl() + "/brotli");
        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertEquals("br", response.getHttpHeader("Content-Encoding"));
        // Verify the body is properly decoded
        assertNotNull(response.getBody());
        assertEquals(expectedText, response.getBody());
    }

    @Test
    @Disabled
    @Tag(TestGroups.FUNCTIONAL)
    public void setProxy() {
        BaseRequest request = getRequest(new TankHttpClient3(), "http://httpbin.org/ip/");
        request.getHttpclient().setProxy("168.9.128.152", 8080);
        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        String body = response.getBody();

        request.doGet(null);
        response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertEquals(body, response.getBody());

        // unset proxy
        request.getHttpclient().setProxy(null, -1);
        request.doGet(null);
        response = request.getResponse();
        assertNotNull(response);
        assertEquals(200, response.getHttpCode());
        assertNotEquals(body, response.getBody());
    }

    @Test
    @Tag(TestGroups.MANUAL)
    public void testSSL() {
//        System.setProperty("jsse.enableSNIExtension", "false");
        BaseRequest request = getRequest(new TankHttpClient3(), "https://turbotax.intuit.com/");
        request.doGet(null);
        BaseResponse response = request.getResponse();
        assertNotNull(response);
//        assertEquals(response.getHttpCode(), 403);

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

