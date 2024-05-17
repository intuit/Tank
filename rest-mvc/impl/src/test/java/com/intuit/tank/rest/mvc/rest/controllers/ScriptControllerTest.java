/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.project.Script;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.script.models.ScriptTO;
import com.intuit.tank.script.models.ExternalScriptTO;
import com.intuit.tank.script.models.ScriptDescriptionContainer;
import com.intuit.tank.script.models.ExternalScriptContainer;
import com.intuit.tank.script.models.ScriptDescription;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.services.scripts.ScriptServiceV2;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptControllerTest {
    @InjectMocks
    private ScriptController scriptController;

    @Mock
    private ScriptServiceV2 scriptService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGetPing() {
        when(scriptService.ping()).thenReturn("PONG");
        ResponseEntity<String> result = scriptController.ping();
        assertEquals("PONG", result.getBody());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).ping();
    }

    @Test
    public void testGetScripts() {
        List<Script> all = new ArrayList<>();
        Script firstScript = new Script();
        Script secondScript = new Script();
        firstScript.setName("testName");
        secondScript.setRuntime(5);
        all.add(firstScript);
        all.add(secondScript);
        List<ScriptDescription> allScripts = all.stream().map(ScriptServiceUtil::scriptToScriptDescription).collect(Collectors.toList());
        ScriptDescriptionContainer input = ScriptDescriptionContainer.builder().withScripts(allScripts).build();
        when(scriptService.getScripts()).thenReturn(input);
        ResponseEntity<ScriptDescriptionContainer> result = scriptController.getScripts();
        List<ScriptDescription> expected = result.getBody().getScripts();
        assertEquals("testName", expected.get(0).getName());
        assertEquals(5, expected.get(1).getRuntime());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).getScripts();
    }

    @Test
    public void testGetAllScriptNames() {
        Map<Integer, String> scriptMap = new HashMap<>();
        scriptMap.put(3, "testScript");
        when(scriptService.getAllScriptNames()).thenReturn(scriptMap);
        ResponseEntity<Map<Integer, String>> result = scriptController.getAllScriptNames();
        assertEquals("testScript", result.getBody().get(3));
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).getAllScriptNames();
    }

    @Test
    public void testGetScript() {
        Script script = new Script();
        script.setName("testName");
        script.setProductName("testProductName");
        when(scriptService.getScript(2)).thenReturn(ScriptServiceUtil.scriptToScriptDescription(script));
        ResponseEntity<ScriptDescription> result = scriptController.getScript(2);
        assertEquals("testName", result.getBody().getName());
        assertEquals("testProductName", result.getBody().getProductName());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).getScript(2);

        when(scriptService.getScript(2)).thenReturn(null);
        ResponseEntity<ScriptDescription> notFound = scriptController.getScript(2);
        assertEquals(404, notFound.getStatusCode().value());
    }

    @Test
    public void testCreateScriptRecording() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("testScript.xml");

        Map<String, String> payload = new HashMap<>();
        payload.put("scriptId", "7");
        payload.put("message", "Script with new script ID 7 has been uploaded");
        when(scriptService.createScript("testName", 0, "true", null, null, "gzip", mockMultipartFile)).thenReturn(payload);
        ResponseEntity<Map<String, String>> result = scriptController.createScript("gzip", "testName", 0, "true", null, null, mockMultipartFile);
        Map<String, String> response = result.getBody();
        assertEquals("7", response.get("scriptId"));
        assertTrue(response.get("message").contains("uploaded"));
        assertEquals(201, result.getStatusCode().value());
        verify(scriptService).createScript("testName", 0, "true", null, null, "gzip", mockMultipartFile);

        when(scriptService.createScript("testName", 0, "true", null, null, null, mockMultipartFile)).thenReturn(payload);
        result = scriptController.createScript(null, "testName", 0, "true", null, null, mockMultipartFile);
        response = result.getBody();
        assertEquals("7", response.get("scriptId"));
        assertTrue(response.get("message").contains("uploaded"));
        assertEquals(201, result.getStatusCode().value());
        verify(scriptService).createScript("testName", 0, "true", null, null, null, mockMultipartFile);

        // File Not Found
        when(scriptService.createScript("testName", 0, "true", null, null, null, mockMultipartFile))
                .thenThrow(new IOException("Error updating script file: script.xml (No such file or directory)"));
        Exception exception = assertThrows(IOException.class, () -> {
            scriptController.createScript(null, "testName", 0, "true", null, null, mockMultipartFile);
        });
        assertEquals("Error updating script file: script.xml (No such file or directory)", exception.getMessage());
    }

    @Test
    public void testCreateScriptTankScript() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("testScript.xml");

        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Script with script ID 7 updated successfully");
        when(scriptService.createScript("testName", 0, null, null, null, "gzip", mockMultipartFile)).thenReturn(payload);
        ResponseEntity<Map<String, String>> result = scriptController.createScript("gzip", "testName", 0, null, null, null, mockMultipartFile);
        Map<String, String> response = result.getBody();
        assertTrue(response.get("message").contains("Script with script ID 7 updated successfully"));
        assertEquals(201, result.getStatusCode().value());
        verify(scriptService).createScript("testName", 0, null, null, null, "gzip", mockMultipartFile);

        when(scriptService.createScript("testName", 0, null, null, null, null, mockMultipartFile)).thenReturn(payload);
        result = scriptController.createScript(null, "testName", 0, null, null, null, mockMultipartFile);
        response = result.getBody();
        assertTrue(response.get("message").contains("Script with script ID 7 updated successfully"));
        assertEquals(201, result.getStatusCode().value());
        verify(scriptService).createScript("testName", 0, null, null, null, null, mockMultipartFile);

        // File Not Found
        when(scriptService.createScript("testName", 0, null, null, null, null, mockMultipartFile))
                .thenThrow(new IOException("Error updating script file: script.xml (No such file or directory)"));
        Exception exception = assertThrows(IOException.class, () -> {
            scriptController.createScript(null, "testName", 0, null, null, null, mockMultipartFile);
        });
        assertEquals("Error updating script file: script.xml (No such file or directory)", exception.getMessage());

        MultipartFile notFoundMultipartFile = mock(MultipartFile.class);
        when(notFoundMultipartFile.getOriginalFilename()).thenReturn("testNonexistentScript.xml");

//        // Script does not exist
        when(scriptService.createScript("testName", 0, null, null, null, null, notFoundMultipartFile))
                .thenThrow(new GenericServiceBadRequestException("scripts", "updating script", "Cannot update a script that does not exist (script id 99)"));
        exception = assertThrows(GenericServiceBadRequestException.class, () -> {
            scriptController.createScript(null, "testName", 0, null, null, null, notFoundMultipartFile);
        });
        assertEquals("Cannot update a script that does not exist (script id 99)", exception.getMessage());

        MultipartFile changeNameMultipartFile = mock(MultipartFile.class);
        when(changeNameMultipartFile.getOriginalFilename()).thenReturn("testChangeNameScript.xml");

        // Can't change name of script
        when(scriptService.createScript("testName", 0, null, null, null, null, changeNameMultipartFile))
                .thenThrow(new GenericServiceBadRequestException("scripts", "updating script", "Cannot change the name of the existing script testScript"));
        exception = assertThrows(GenericServiceBadRequestException.class, () -> {
            scriptController.createScript(null, "testName", 0, null, null, null, changeNameMultipartFile);
        });
        assertEquals("Cannot change the name of the existing script testScript", exception.getMessage());
    }

    @Test
    public void testCreateScriptCopyFrom() throws IOException {
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Script copyScript with script ID 7 created successfully (copied from script ID 6 - Original Script");
        when(scriptService.createScript("testName", 0, null, null, null, null, null)).thenReturn(payload);
        ResponseEntity<Map<String, String>> result = scriptController.createScript(null, "testName", 0, null, null, null, null);
        Map<String, String> response = result.getBody();
        assertTrue(response.get("message").contains("Script copyScript with script ID 7 created successfully (copied from script ID 6 - Original Script"));
        assertEquals(201, result.getStatusCode().value());
        verify(scriptService).createScript("testName", 0, null, null, null, null, null);

        // No Script Name
        when(scriptService.createScript("testName", 0, null, null, null, null, null)).thenThrow(new IllegalArgumentException("Must provide a script name to copy from existing script"));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            scriptController.createScript(null, "testName", 0, null, null, null, null);
        });
        assertEquals("Must provide a script name to copy from existing script", exception.getMessage());

        // No Source ID
        when(scriptService.createScript("testName2", 0, null, null, null, null, null)).thenThrow(new IllegalArgumentException("Source script cannot be found"));
        exception = assertThrows(IllegalArgumentException.class, () -> {
            scriptController.createScript(null, "testName2", 0, null, null, null, null);
        });
        assertEquals("Source script cannot be found", exception.getMessage());
    }

    @Test
    public void testDownloadScript() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ScriptTO scriptTO = ScriptTO.builder()
                .withId(4)
                .withCreator("Test")
                .withName("testName")
                .withRuntime(3)
                .build();
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(scriptTO);
        String filename = "test_TS.xml";
        payload.put(filename, streamingResponse);
        when(scriptService.downloadScript(4)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = scriptController.downloadScript(4);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).downloadScript(4);
    }

    @Test
    public void testDownloadHarnessScript() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ScriptTO scriptTO = ScriptTO.builder()
                .withId(4)
                .withCreator("Test")
                .withName("testName")
                .withRuntime(3)
                .build();
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(scriptTO);
        String filename = "test_H.xml";
        payload.put(filename, streamingResponse);
        when(scriptService.downloadHarnessScript(4)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = scriptController.downloadHarnessScript(4);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).downloadHarnessScript(4);
    }

    @Test
    public void testDeleteScript() {
        when(scriptService.deleteScript(1)).thenReturn("");
        ResponseEntity<String> result = scriptController.deleteScript(1);
        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCode().value());
        verify(scriptService).deleteScript(1);

        when(scriptService.deleteScript(1)).thenReturn("Script with script id 1 does not exist");
        ResponseEntity<String> error = scriptController.deleteScript(1);
        assertTrue(error.getBody().contains("not exist"));
        assertEquals(404, error.getStatusCode().value());
    }

    // External Scripts

    @Test
    public void testGetExternalScript() {
        ExternalScript script = new ExternalScript();
        script.setName("testName");
        script.setProductName("testProductName");
        when(scriptService.getExternalScript(2)).thenReturn(ScriptServiceUtil.externalScriptToTO(script));
        ResponseEntity<ExternalScriptTO> result = scriptController.getExternalScript(2);
        assertEquals("testName", result.getBody().getName());
        assertEquals("testProductName", result.getBody().getProductName());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).getExternalScript(2);

        when(scriptService.getExternalScript(2)).thenReturn(null);
        result = scriptController.getExternalScript(2);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    public void testGetExternalScripts() {
        ExternalScript firstScript = new ExternalScript();
        ExternalScript secondScript = new ExternalScript();
        firstScript.setName("testName");
        secondScript.setProductName("testProduct");
        List<ExternalScript> all = List.of(firstScript, secondScript);
        ExternalScriptContainer ret = ExternalScriptContainer.builder()
                .withScripts(all.stream()
                        .map(ScriptServiceUtil::externalScriptToTO)
                        .collect(Collectors.toList()))
                .build();

        when(scriptService.getExternalScripts()).thenReturn(ret);
        ResponseEntity<ExternalScriptContainer> result = scriptController.getExternalScripts();
        List<ExternalScriptTO> expected = result.getBody().getScripts();
        assertEquals("testName", expected.get(0).getName());
        assertEquals("testProduct", expected.get(1).getProductName());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).getExternalScripts();
    }

    @Test
    public void testCreateExternalScript() {
        ExternalScriptTO payload = ExternalScriptTO.builder()
                .withScript("testScript")
                .withName("testName")
                .withProductName("testProduct")
                .build();
        when(scriptService.createExternalScript(payload)).thenReturn(payload);
        ResponseEntity<ExternalScriptTO> result = scriptController.createExternalScript(payload);
        assertEquals("testScript", result.getBody().getScript());
        assertEquals("testName", result.getBody().getName());
        assertEquals("testProduct", result.getBody().getProductName());
        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getHeaders().getLocation());
        verify(scriptService).createExternalScript(payload);
    }

    @Test
    public void testDownloadExternalScript() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ExternalScriptTO externalScriptTO = ExternalScriptTO.builder()
                .withId(5)
                .withCreator("Test")
                .withName("testExternalName")
                .build();
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(externalScriptTO);
        String filename = "test_ETS.xml";
        payload.put(filename, streamingResponse);
        when(scriptService.downloadExternalScript(5)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = scriptController.downloadExternalScript(5);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCode().value());
        verify(scriptService).downloadExternalScript(5);
    }

    @Test
    public void testDeleteExternalScript() {
        when(scriptService.deleteExternalScript(2)).thenReturn("");
        ResponseEntity<String> result = scriptController.deleteExternalScript(2);
        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCode().value());
        verify(scriptService).deleteExternalScript(2);

        when(scriptService.deleteExternalScript(2)).thenReturn("External script with external script id 2 does not exist");
        ResponseEntity<String> error = scriptController.deleteExternalScript(2);
        assertTrue(error.getBody().contains("not exist"));
        assertEquals(404, error.getStatusCode().value());
    }
}
