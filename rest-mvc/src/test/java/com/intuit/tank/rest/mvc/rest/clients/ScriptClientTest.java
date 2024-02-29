package com.intuit.tank.rest.mvc.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
import com.intuit.tank.script.models.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptClientTest {
    MockWebServer mockWebServer;

    ScriptClient scriptClient;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String serviceUrl = mockWebServer.url("/").toString();
        scriptClient = new ScriptClient(serviceUrl, null, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testScriptClientDefaultConstructor_SetsFieldsCorrectly()  {
        String serviceUrl = "http://localhost:8080";
        ScriptClient scriptClient = new ScriptClient(serviceUrl);

        assertEquals("/v2/scripts", scriptClient.getServiceBaseUrl());
    }

    @Test
    void testGetScripts_WhenSuccess_ReturnsContainer() throws JsonProcessingException {
        ScriptDescriptionContainer expectedResponse = createScriptDescriptionContainer();
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );
        ScriptDescriptionContainer response = scriptClient.getScripts();
        assertEquals(expectedResponse.getScripts().get(0).getName(), response.getScripts().get(0).getName());
        assertEquals(expectedResponse.getScripts().get(1).getName(), response.getScripts().get(1).getName());
        assertEquals(expectedResponse.getScripts().get(0).getRuntime(), response.getScripts().get(0).getRuntime());
        assertEquals(expectedResponse.getScripts().get(1).getRuntime(), response.getScripts().get(1).getRuntime());
    }

    @Test
    void testGetScripts_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));
        Exception exception = assertThrows(ClientException.class, () -> scriptClient.getScripts());
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetScript_WhenSuccess_ReturnsScriptTO() throws JsonProcessingException {
        ScriptTO expectedResponse = createScriptTO();
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );
        ScriptTO response = scriptClient.getScript(expectedResponse.getId());

        assertScriptTO(expectedResponse, response);
    }

    @Test
    void testGetScript_WhenServerError_ThrowsClientException() {
        Integer scriptId = 1;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));
        Exception exception = assertThrows(ClientException.class, () -> scriptClient.getScript(scriptId));

        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testCreateScript_WhenSuccess_ReturnsScriptTO() throws JsonProcessingException {
        ScriptTO expectedResponse = createScriptTO();

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );
        ScriptTO response = scriptClient.createScript(expectedResponse);

        assertScriptTO(expectedResponse, response);
    }

    @Test
    void testCreateScript_WhenServerError_ThrowsClientException() {
        ScriptTO scriptTo = new ScriptTO();
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () -> scriptClient.createScript(scriptTo));
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDownloadScript_WhenSuccess_ReturnsDataBuffer() {
        Integer scriptId = 1;
        String expectedContent = "Test Script Content";
        DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        DataBuffer expectedResponse = bufferFactory.wrap(expectedContent.getBytes());

        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedContent)
                .addHeader("Content-Type", "application/octet-stream"));

        DataBuffer response = scriptClient.downloadScript(scriptId).block();

        assertEquals(expectedResponse.toString(StandardCharsets.UTF_8), response.toString(StandardCharsets.UTF_8));
    }

    @Test
    void testDownloadScript_WhenServerError_ThrowsClientException() {
        Integer scriptId = 92;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () -> scriptClient.downloadScript(scriptId).block());
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDownloadHarnessScript_WhenSuccess_ReturnsString() {
        Integer scriptId = 3;
        String expectedResponse = "Test Script Content";

        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedResponse)
                .addHeader("Content-Type", "text/plain")
        );

        String response = scriptClient.downloadHarnessScript(scriptId);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testDownloadHarnessScript_WhenServerError_ThrowsClientException() {
        Integer scriptId = 3;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> scriptClient.downloadHarnessScript(scriptId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testUploadScript_WhenSuccess_ReturnsMap() throws JsonProcessingException {
        String name = "uploadTest";
        int scriptId = 8;
        MultipartFile file = new MockMultipartFile("file.txt", new byte[0]);
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Script with new script ID " + scriptId + " has been uploaded");

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        Map<String, String> response = scriptClient.uploadScript(name, scriptId, file);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testUploadScript_WhenServerError_ThrowsClientException() {
        String name = "uploadTest";
        Integer scriptId = 1;
        MultipartFile file = new MockMultipartFile("file.txt", new byte[0]);
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () ->
                scriptClient.uploadScript(name, scriptId, file)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDeleteScript_WhenSuccess_ReturnsString() {
        Integer scriptId = 1;
        String expectedResponse = "Script deleted successfully";

        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedResponse)
                .addHeader("Content-Type", "text/plain")
        );

        String response = scriptClient.deleteScript(scriptId);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testDeleteScript_WhenServerError_ThrowsClientException() {
        Integer scriptId = 1;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> scriptClient.deleteScript(scriptId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetExternalScripts_WhenSuccess_ReturnsExternalScriptContainer() throws JsonProcessingException {
        ExternalScriptContainer expectedResponse = createExternalScriptContainer();

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        ExternalScriptContainer response = scriptClient.getExternalScripts();
        assertEquals(expectedResponse.getScripts().get(0).getName(), response.getScripts().get(0).getName());
        assertEquals(expectedResponse.getScripts().get(1).getScript(), response.getScripts().get(1).getScript());
    }

    @Test
    void testGetExternalScripts_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));
        Exception exception = assertThrows(ClientException.class,
                () -> scriptClient.getExternalScripts()
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetExternalScript_WhenSuccess_ReturnsExternalScriptTO() throws JsonProcessingException {
        Integer externalScriptId = 56;
        ExternalScriptTO expectedResponse = createExternalScriptTO();

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        ExternalScriptTO response = scriptClient.getExternalScript(externalScriptId);
        assertExternalScriptTO(expectedResponse, response);
    }

    @Test
    void testGetExternalScript_WhenServerError_ThrowsClientException() {
        Integer externalScriptId = 58;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> scriptClient.getExternalScript(externalScriptId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testCreateExternalScript_WhenSuccess_ReturnsExternalScriptTO() throws JsonProcessingException {
        ExternalScriptTO scriptTo = createExternalScriptTO();
        ExternalScriptTO expectedResponse = createExternalScriptTO();

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        ExternalScriptTO response = scriptClient.createExternalScript(scriptTo);
        assertExternalScriptTO(expectedResponse, response);
    }

    @Test
    void testCreateExternalScript_WhenServerError_ThrowsClientException() {
        ExternalScriptTO scriptTo = createExternalScriptTO();
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> scriptClient.createExternalScript(scriptTo)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDownloadExternalScript_WhenSuccess_ReturnsDataBuffer() {
        Integer externalScriptId = 78;
        String expectedContent = "Test External Script content";
        DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        DataBuffer expectedResponse = bufferFactory.wrap(expectedContent.getBytes());

        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedContent)
                .addHeader("Content-Type", "application/octet-stream"));

        DataBuffer response = scriptClient.downloadExternalScript(externalScriptId).block();

        assertEquals(expectedResponse.toString(StandardCharsets.UTF_8), response.toString(StandardCharsets.UTF_8));
    }

    @Test
    void testDownloadExternalScript_WhenServerError_ThrowsClientException() {
        Integer externalScriptId = 34;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () ->
                scriptClient.downloadExternalScript(externalScriptId).block()
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDeleteExternalScript_WhenSuccess_ReturnsString() {
        Integer externalScriptId = 24;
        String expectedResponse = "External Script deleted successfully";

        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedResponse)
                .addHeader("Content-Type", "text/plain")
        );

        String response = scriptClient.deleteExternalScript(externalScriptId);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testDeleteExternalScript_WhenServerError_ThrowsClientException() {
        Integer externalScriptId = 45;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> scriptClient.deleteExternalScript(externalScriptId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }


    private ScriptTO createScriptTO() {
        int scriptId = 19;
        Script firstScript = new Script();
        firstScript.setId(scriptId);
        firstScript.setName("testName");
        firstScript.setRuntime(5);
        firstScript.setComments("testComments");
        firstScript.setProductName("testProductName");
        List<ScriptStep> steps = new ArrayList<>();
        ScriptStep step = new ScriptStep();
        step.setName("testStepName");
        steps.add(step);
        firstScript.setSteps(steps);
        return ScriptServiceUtil.scriptToTransferObject(firstScript);
    }

    private ExternalScriptTO createExternalScriptTO() {
        int externalScriptId = 20;
        ExternalScript firstScript = new ExternalScript();
        firstScript.setId(externalScriptId);
        firstScript.setName("testName");
        firstScript.setScript("testScript");
        firstScript.setProductName("testProductName");
        firstScript.setCreator("admin");
        firstScript.setCreated(new Date());
        return ScriptServiceUtil.externalScriptToTO(firstScript);
    }

    private void assertScriptTO(ScriptTO expected, ScriptTO actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getRuntime(), actual.getRuntime());
        assertEquals(expected.getComments(), actual.getComments());
        assertEquals(expected.getProductName(), actual.getProductName());
        assertEquals(expected.getSteps().get(0).getName(), actual.getSteps().get(0).getName());
    }

    private void assertExternalScriptTO(ExternalScriptTO expected, ExternalScriptTO actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getScript(), actual.getScript());
        assertEquals(expected.getProductName(), actual.getProductName());
        assertEquals(expected.getCreator(), actual.getCreator());
        assertEquals(expected.getCreated(), actual.getCreated());
    }

    private ScriptDescriptionContainer createScriptDescriptionContainer() {
        List<Script> all = new ArrayList<>();
        Script firstScript = new Script();
        Script secondScript = new Script();
        firstScript.setName("testName");
        secondScript.setRuntime(5);
        all.add(firstScript);
        all.add(secondScript);
        List<ScriptDescription> allScripts = all.stream().map(ScriptServiceUtil::scriptToScriptDescription).collect(Collectors.toList());
        return new ScriptDescriptionContainer(allScripts);
    }

    private ExternalScriptContainer createExternalScriptContainer() {
        List<ExternalScript> all = new ArrayList<>();
        ExternalScript firstScript = new ExternalScript();
        ExternalScript secondScript = new ExternalScript();
        firstScript.setName("testName");
        secondScript.setScript("testScript");
        all.add(firstScript);
        all.add(secondScript);
        List<ExternalScriptTO> allScripts = all.stream().map(ScriptServiceUtil::externalScriptToTO).collect(Collectors.toList());
        ExternalScriptContainer container = new ExternalScriptContainer();
        container.setScripts(allScripts);
        return container;
    }
}
