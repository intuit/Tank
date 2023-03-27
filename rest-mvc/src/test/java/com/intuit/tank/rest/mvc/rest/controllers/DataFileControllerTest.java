/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;
import com.intuit.tank.rest.mvc.rest.models.scripts.ScriptTO;
import com.intuit.tank.rest.mvc.rest.services.datafiles.DataFileServiceV2;
import com.intuit.tank.rest.mvc.rest.util.DataFileServiceUtil;

import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class DataFileControllerTest {
    @InjectMocks
    private  DataFileController  datafileController;

    @Mock
    private DataFileServiceV2 datafileService;

    @Mock
    HttpServletRequest request;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGetPing() {
        when(datafileService.ping()).thenReturn("PONG");
        ResponseEntity<String> result = datafileController.ping();
        assertEquals("PONG", result.getBody());
        assertEquals(200, result.getStatusCodeValue());
        verify(datafileService).ping();
    }

    @Test
    public void testGetDatafiles() {
        List<DataFile> all = new ArrayList<>();
        DataFile firstDatafile = new DataFile();
        DataFile secondDatafile = new DataFile();
        firstDatafile.setComments("testComments");
        secondDatafile.setPath("testName");
        all.add(firstDatafile);
        all.add(secondDatafile);
        List<DataFileDescriptor> allDatafiles = all.stream().map(DataFileServiceUtil::dataFileToDescriptor).collect(Collectors.toList());
        DataFileDescriptorContainer input = new DataFileDescriptorContainer(allDatafiles);
        when(datafileService.getDatafiles()).thenReturn(input);
        ResponseEntity<DataFileDescriptorContainer> result = datafileController.getDatafiles();
        List<DataFileDescriptor> expected = result.getBody().getDataFiles();
        assertEquals("testComments", expected.get(0).getComments());
        assertEquals("testName", expected.get(1).getName());
        assertEquals(200, result.getStatusCodeValue());
        verify(datafileService).getDatafiles();
    }

    @Test
    public void testGetDatafile() {
        DataFile datafile = new DataFile();
        datafile.setPath("testPath");
        datafile.setComments("testComments");
        when(datafileService.getDatafile(2)).thenReturn(DataFileServiceUtil.dataFileToDescriptor(datafile));
        ResponseEntity<DataFileDescriptor> result = datafileController.getDatafile(2);
        assertEquals("testPath", result.getBody().getName());
        assertEquals("testComments", result.getBody().getComments());
        assertEquals(200, result.getStatusCodeValue());
        verify(datafileService).getDatafile(2);

        when(datafileService.getDatafile(2)).thenReturn(null);
        ResponseEntity<DataFileDescriptor> notFound = datafileController.getDatafile(2);
        assertEquals(404, notFound.getStatusCodeValue());
    }

    @Test
    public void testGetDatafileContent() throws IOException {
        File testCSV = new File("src/test/resources/test.csv");
        StreamingResponseBody responseBody = outputStream -> {
            Files.copy(testCSV.toPath(), outputStream);
        };
        when(datafileService.getDatafileContent(2, 5, 10)).thenReturn(responseBody);
        ResponseEntity<String> result = datafileController.getDatafileContent(2, 5, 10);
        String response = result.getBody();
        assertEquals("test, 1, 2\n" +
                "csv, 3, 4\n" +
                "file, 5, 6\n", response);
        assertEquals(200, result.getStatusCodeValue());
        verify(datafileService).getDatafileContent(2, 5, 10);
    }

    @Test
    public void testDownloadDatafile() throws IOException {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        File testCSV = new File("src/test/resources/test.csv");
        StreamingResponseBody responseBody = outputStream -> {
            Files.copy(testCSV.toPath(), outputStream);
        };
        String filename = "test.csv";
        payload.put(filename, responseBody);
        when(datafileService.downloadDatafile(4)).thenReturn(payload);
        ResponseEntity<StreamingResponseBody> result = datafileController.downloadDatafile(4);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, result.getHeaders().getContentType());
        assertEquals(filename, result.getHeaders().getContentDisposition().getFilename());
        assertEquals(200, result.getStatusCodeValue());
        verify(datafileService).downloadDatafile(4);
    }

    @Test
    public void testUploadDatafile() throws IOException {
        Map<String, String> payload = new HashMap<>();
        payload.put("datafileId", "6");
        payload.put("message", "Datafile with new datafile ID 6 has been uploaded");
        when(datafileService.uploadDatafile(0, "gzip", null)).thenReturn(payload);
        ResponseEntity<Map<String, String>> result = datafileController.uploadDatafile("gzip", 0, null);
        Map<String, String> response = result.getBody();
        assertEquals("6", response.get("datafileId"));
        assertTrue(response.get("message").contains("uploaded"));
        assertEquals(201, result.getStatusCodeValue());
        verify(datafileService).uploadDatafile(0, "gzip", null);
    }

    @Test
    public void testDeleteDatafile() {
        when(datafileService.deleteDatafile(1)).thenReturn("");
        ResponseEntity<String> result = datafileController.deleteDatafile(1);
        assertTrue(result.getBody().contains(""));
        assertEquals(204, result.getStatusCodeValue());
        verify(datafileService).deleteDatafile(1);

        when(datafileService.deleteDatafile(1)).thenReturn("Datafile with datafile id 1 does not exist");
        ResponseEntity<String> error = datafileController.deleteDatafile(1);
        assertTrue(error.getBody().contains("not exist"));
        assertEquals(404, error.getStatusCodeValue());
    }

}
