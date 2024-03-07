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
        assertEquals(200, result.getStatusCodeValue());
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
        ScriptDescriptionContainer input = new ScriptDescriptionContainer(allScripts);
        when(scriptService.getScripts()).thenReturn(input);
        ResponseEntity<ScriptDescriptionContainer> result = scriptController.getScripts();
        List<ScriptDescription> expected = result.getBody().getScripts();
        assertEquals("testName", expected.get(0).getName());
        assertEquals(5, expected.get(1).getRuntime());
        assertEquals(200, result.getStatusCodeValue());
        verify(scriptService).getScripts();
    }

    @Test
    public void testGetAllScriptNames() {
        Map<Integer, String> scriptMap = new HashMap<>();
        scriptMap.put(3, "testScript");
        when(scriptService.getAllScriptNames()).thenReturn(scriptMap);
        ResponseEntity<Map<Integer, String>> result = scriptController.getAllScriptNames();
        assertEquals("testScript", result.getBody().get(3));
        assertEquals(200, result.getStatusCodeValue());
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
        assertEquals(200, result.getStatusCodeValue());
        verify(scriptService).getScript(2);

        when(scriptService.getScript(2)).thenReturn(null);
        ResponseEntity<ScriptDescription> notFound = scriptController.getScript(2);
        assertEquals(404, notFound.getStatusCodeValue());
    }

    @Test
    public void testCreateScript() {
        ScriptTO payload = new ScriptTO();
        payload.setId(4);
        payload.setCreator("Test");
        payload.setName("testName");
        payload.setRuntime(3);
        when(scriptService.createScript(payload)).thenReturn(payload);
        ResponseEntity<ScriptTO> result = scriptController.createScript(payload);
        assertEquals(4, result.getBody().getId());
        assertEquals("testName", result.getBody().getName());
        assertEquals(3, result.getBody().getRuntime());
        assertEquals(201, result.getStatusCodeValue());
        assertNotNull(result.getHeaders().getLocation());
        verify(scriptService).createScript(payload);
    }

    @Test
    public void testDownloadScript() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ScriptTO scriptTO = new ScriptTO();
        scriptTO.setId(4);
        scriptTO.setCreator("Test");
        scriptTO.setName("testName");
        scriptTO.setRuntime(3);
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(scriptTO);
        String filename = "test_TS.xml";
        payload.put(filename, streamingResponse);
        when(scriptService.downloadScript(4)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = scriptController.downloadScript(4);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCodeValue());
        verify(scriptService).downloadScript(4);
    }

    @Test
    public void testDownloadHarnessScript() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ScriptTO scriptTO = new ScriptTO();
        scriptTO.setId(4);
        scriptTO.setCreator("Test");
        scriptTO.setName("testName");
        scriptTO.setRuntime(3);
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(scriptTO);
        String filename = "test_H.xml";
        payload.put(filename, streamingResponse);
        when(scriptService.downloadHarnessScript(4)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = scriptController.downloadHarnessScript(4);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCodeValue());
        verify(scriptService).downloadHarnessScript(4);
    }

    @Test
    public void testUploadScript() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("testScript.xml");

        Map<String, String> payload = new HashMap<>();
        payload.put("scriptId", "7");
        payload.put("message", "Script with new script ID 7 has been uploaded");
        when(scriptService.uploadProxyScript("testName", 0, "gzip", mockMultipartFile)).thenReturn(payload);
        ResponseEntity<Map<String, String>> result = scriptController.uploadScript("gzip", "testName", 0, mockMultipartFile);
        Map<String, String> response = result.getBody();
        assertEquals("7", response.get("scriptId"));
        assertTrue(response.get("message").contains("uploaded"));
        assertEquals(201, result.getStatusCodeValue());
        verify(scriptService).uploadProxyScript("testName", 0, "gzip", mockMultipartFile);

        when(scriptService.uploadProxyScript("testName", 0, "", mockMultipartFile)).thenReturn(payload);
        result = scriptController.uploadScript("", "testName", 0, mockMultipartFile);
        response = result.getBody();
        assertEquals("7", response.get("scriptId"));
        assertTrue(response.get("message").contains("uploaded"));
        assertEquals(201, result.getStatusCodeValue());
        verify(scriptService).uploadProxyScript("testName", 0, "", mockMultipartFile);

        // File Not Found
        when(scriptService.uploadProxyScript("testName", 0, "", mockMultipartFile))
                .thenThrow(new IOException("Error updating script file: script.xml (No such file or directory)"));
        Exception exception = assertThrows(IOException.class, () -> {
            scriptController.uploadScript("", "testName", 0, mockMultipartFile);
        });
        assertEquals("Error updating script file: script.xml (No such file or directory)", exception.getMessage());
    }

    @Test
    public void testUpdateTankScript() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("testScript.xml");

        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Script with script ID 7 updated successfully");
        when(scriptService.updateTankScript("gzip",  mockMultipartFile)).thenReturn(payload);
        ResponseEntity<Map<String, String>> result = scriptController.updateTankScript("gzip",mockMultipartFile);
        Map<String, String> response = result.getBody();
        assertTrue(response.get("message").contains("Script with script ID 7 updated successfully"));
        assertEquals(201, result.getStatusCodeValue());
        verify(scriptService).updateTankScript( "gzip", mockMultipartFile);

        when(scriptService.updateTankScript("", mockMultipartFile)).thenReturn(payload);
        result = scriptController.updateTankScript("", mockMultipartFile);
        response = result.getBody();
        assertTrue(response.get("message").contains("Script with script ID 7 updated successfully"));
        assertEquals(201, result.getStatusCodeValue());
        verify(scriptService).updateTankScript( "", mockMultipartFile);

        // File Not Found
        when(scriptService.updateTankScript("",  mockMultipartFile))
                .thenThrow(new IOException("Error updating script file: script.xml (No such file or directory)"));
        Exception exception = assertThrows(IOException.class, () -> {
            scriptController.updateTankScript("", mockMultipartFile);
        });
        assertEquals("Error updating script file: script.xml (No such file or directory)", exception.getMessage());

        MultipartFile notFoundMultipartFile = mock(MultipartFile.class);
        when(notFoundMultipartFile.getOriginalFilename()).thenReturn("testNonexistentScript.xml");

        // Script does not exist
        when(scriptService.updateTankScript("", notFoundMultipartFile))
                .thenThrow(new GenericServiceBadRequestException("scripts", "updating script", "Cannot update a script that does not exist (script id 99)"));
        exception = assertThrows(GenericServiceBadRequestException.class, () -> {
            scriptController.updateTankScript("", notFoundMultipartFile);
        });
        assertEquals("Cannot update a script that does not exist (script id 99)", exception.getMessage());

        MultipartFile changeNameMultipartFile = mock(MultipartFile.class);
        when(changeNameMultipartFile.getOriginalFilename()).thenReturn("testChangeNameScript.xml");

        // Can't change name of script
        when(scriptService.updateTankScript("", changeNameMultipartFile))
                .thenThrow(new GenericServiceBadRequestException("scripts", "updating script", "Cannot change the name of the existing script testScript"));
        exception = assertThrows(GenericServiceBadRequestException.class, () -> {
            scriptController.updateTankScript("", changeNameMultipartFile);
        });
        assertEquals("Cannot change the name of the existing script testScript", exception.getMessage());

    }

    @Test
    public void testDeleteScript() {
        when(scriptService.deleteScript(1)).thenReturn("");
        ResponseEntity<String> result = scriptController.deleteScript(1);
        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCodeValue());
        verify(scriptService).deleteScript(1);

        when(scriptService.deleteScript(1)).thenReturn("Script with script id 1 does not exist");
        ResponseEntity<String> error = scriptController.deleteScript(1);
        assertTrue(error.getBody().contains("not exist"));
        assertEquals(404, error.getStatusCodeValue());
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
        assertEquals(200, result.getStatusCodeValue());
        verify(scriptService).getExternalScript(2);

        when(scriptService.getExternalScript(2)).thenReturn(null);
        result = scriptController.getExternalScript(2);
        assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    public void testGetExternalScripts() {
        List<ExternalScript> all = new ArrayList<>();
        ExternalScript firstScript = new ExternalScript();
        ExternalScript secondScript = new ExternalScript();
        firstScript.setName("testName");
        secondScript.setProductName("testProduct");
        all.add(firstScript);
        all.add(secondScript);
        ExternalScriptContainer ret = new ExternalScriptContainer();
        for (ExternalScript s : all) {
            ret.getScripts().add(ScriptServiceUtil.externalScriptToTO(s));
        }
        when(scriptService.getExternalScripts()).thenReturn(ret);
        ResponseEntity<ExternalScriptContainer> result = scriptController.getExternalScripts();
        List<ExternalScriptTO> expected = result.getBody().getScripts();
        assertEquals("testName", expected.get(0).getName());
        assertEquals("testProduct", expected.get(1).getProductName());
        assertEquals(200, result.getStatusCodeValue());
        verify(scriptService).getExternalScripts();
    }

    @Test
    public void testCreateExternalScript() {
        ExternalScriptTO payload = new ExternalScriptTO();
        payload.setScript("testScript");
        payload.setName("testName");
        payload.setProductName("testProduct");
        when(scriptService.createExternalScript(payload)).thenReturn(payload);
        ResponseEntity<ExternalScriptTO> result = scriptController.createExternalScript(payload);
        assertEquals("testScript", result.getBody().getScript());
        assertEquals("testName", result.getBody().getName());
        assertEquals("testProduct", result.getBody().getProductName());
        assertEquals(201, result.getStatusCodeValue());
        assertNotNull(result.getHeaders().getLocation());
        verify(scriptService).createExternalScript(payload);
    }

    @Test
    public void testDownloadExternalScript() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        ExternalScriptTO externalScriptTO = new ExternalScriptTO();
        externalScriptTO.setId(5);
        externalScriptTO.setCreator("Test");
        externalScriptTO.setName("testExternalName");
        StreamingResponseBody streamingResponse = ResponseUtil.getXMLStream(externalScriptTO);
        String filename = "test_ETS.xml";
        payload.put(filename, streamingResponse);
        when(scriptService.downloadExternalScript(5)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = scriptController.downloadExternalScript(5);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCodeValue());
        verify(scriptService).downloadExternalScript(5);
    }

    @Test
    public void testDeleteExternalScript() {
        when(scriptService.deleteExternalScript(2)).thenReturn("");
        ResponseEntity<String> result = scriptController.deleteExternalScript(2);
        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCodeValue());
        verify(scriptService).deleteExternalScript(2);

        when(scriptService.deleteExternalScript(2)).thenReturn("External script with external script id 2 does not exist");
        ResponseEntity<String> error = scriptController.deleteExternalScript(2);
        assertTrue(error.getBody().contains("not exist"));
        assertEquals(404, error.getStatusCodeValue());
    }
}
