/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.datafiles;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.dao.PagedResult;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.DataFileBatchResult;
import com.intuit.tank.rest.mvc.rest.models.DataFilePreview;
import com.intuit.tank.rest.mvc.rest.models.DataFileSummary;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DataFileServiceV2ImplUiTest {

    @InjectMocks
    private DataFileServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;
    private final List<AutoCloseable> closeables = new ArrayList<>();
    private final MessageEventSender events = mock(MessageEventSender.class);
    private final Map<Integer, DataFile> stored = new HashMap<>();
    private final Map<Integer, String> contents = new HashMap<>();
    private final List<Integer> deleted = new ArrayList<>();
    private PagedQuery lastQuery;
    private int nextId = 100;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        useConfig(true, Map.of(AccessRight.CREATE_DATAFILE, List.of("loaders")));
        actAs(user("alice", "loaders"));
        put(1, "users.csv", "alice", "h1\nr1\nr2\nr3\nr4");
        put(2, "notes.txt", "carol", "a\nb\nc");

        closeables.add(Mockito.mockConstruction(DataFileDao.class, (dao, ctx) -> {
            when(dao.findById(anyInt())).thenAnswer(i -> stored.get(i.<Integer>getArgument(0)));
            when(dao.storeDataFile(any(), any())).thenAnswer(i -> {
                DataFile f = i.getArgument(0);
                f.setId(nextId++);
                f.setFileName(f.getPath());
                contents.put(f.getId(), IOUtils.toString(i.<InputStream>getArgument(1), StandardCharsets.UTF_8));
                stored.put(f.getId(), f);
                return f;
            });
            doAnswer(i -> deleted.add(i.getArgument(0))).when(dao).delete(anyInt());
            when(dao.findPaged(any())).thenAnswer(i -> {
                lastQuery = i.getArgument(0);
                return new PagedResult<>(List.of(stored.get(1)), 7);
            });
        }));
        FileStorage storage = mock(FileStorage.class);
        when(storage.readFileData(any(FileData.class))).thenAnswer(i -> {
            FileData fd = i.getArgument(0);
            return new ByteArrayInputStream(contents.get(Integer.valueOf(fd.getPath())).getBytes(StandardCharsets.UTF_8));
        });
        MockedStatic<FileStorageFactory> factory = Mockito.mockStatic(FileStorageFactory.class);
        factory.when(() -> FileStorageFactory.getFileStorage(any(), anyBoolean())).thenReturn(storage);
        closeables.add(factory);
        closeables.add(Mockito.mockConstruction(TankConfig.class));
        closeables.add(Mockito.mockConstruction(ServletInjector.class, (injector, ctx) ->
                when(injector.getManagedBean(eq(servletContext), eq(MessageEventSender.class))).thenReturn(events)));
    }

    @AfterEach
    void tearDown() throws Exception {
        for (AutoCloseable c : closeables) {
            c.close();
        }
        mocks.close();
        reset();
    }

    private void put(int id, String name, String owner, String content) {
        DataFile f = new DataFile();
        f.setId(id);
        f.setPath(name);
        f.setFileName(name);
        f.setCreator(owner);
        stored.put(id, f);
        contents.put(id, content);
    }

    private static MultipartFile file(String name, byte[] content) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(name);
        try {
            when(file.getInputStream()).thenAnswer(i -> new ByteArrayInputStream(content));
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
        return file;
    }

    private static byte[] zip(Map<String, String> entries) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(bytes)) {
            for (Map.Entry<String, String> e : entries.entrySet()) {
                out.putNextEntry(new ZipEntry(e.getKey()));
                out.write(e.getValue().getBytes(StandardCharsets.UTF_8));
                out.closeEntry();
            }
        }
        return bytes.toByteArray();
    }

    // list

    @Test
    void list() {
        PageResponse<DataFileSummary> page = service.listDatafiles(1, 10, "name,asc", "alice", "user");
        assertEquals(7, page.total());
        assertEquals("users.csv", page.items().get(0).name());
        assertEquals("path", lastQuery.sortProperty());
        assertEquals(Map.of("creator", "alice"), lastQuery.equalTo());
        assertEquals(List.of("path", "comments"), lastQuery.searchProperties());
        assertThrows(GenericServiceBadRequestException.class, () -> service.listDatafiles(0, 10, "fileName", null, null));
    }

    @Test
    void needsAUser() {
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.listDatafiles(0, 10, null, null, null));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.previewDatafile(1, 0, 10));
    }

    // batch upload

    @Test
    void batch_createsFilesFromPlainUploadsAndZips() throws Exception {
        Map<String, String> entries = new java.util.LinkedHashMap<>();
        entries.put("a.csv", "x,y");
        entries.put("__MACOSX/._a.csv", "junk");
        entries.put("b.txt", "1");
        DataFileBatchResult result = service.uploadDatafiles(List.of(
                file("ids.csv", "1\n2".getBytes(StandardCharsets.UTF_8)),
                file("photo.png", new byte[] { 1 }),
                file("bundle.zip", zip(entries))));

        assertEquals(List.of(new DataFileBatchResult.Created(100, "ids.csv"), new DataFileBatchResult.Created(101, "a.csv"),
                new DataFileBatchResult.Created(102, "b.txt")), result.created());
        assertEquals(List.of("photo.png", "__MACOSX/._a.csv"), result.skipped());
        assertEquals("x,y", contents.get(101));
        assertEquals("alice", stored.get(100).getCreator());
        ArgumentCaptor<ModifiedEntityMessage> msg = ArgumentCaptor.forClass(ModifiedEntityMessage.class);
        verify(events, times(3)).sendEvent(msg.capture());
        assertEquals(ModificationType.ADD, msg.getValue().getType());
    }

    @Test
    void batch_needsCreateRight() {
        actAs(user("bob"));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.uploadDatafiles(
                List.of(file("a.csv", new byte[0]))));
    }

    @Test
    void batch_limitsAndBadArchives() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.uploadDatafiles(List.of()));
        List<MultipartFile> many = new ArrayList<>();
        for (int i = 0; i <= DataFileServiceV2Impl.MAX_BATCH_FILES; i++) {
            many.add(file(i + ".csv", new byte[0]));
        }
        assertThrows(GenericServiceBadRequestException.class, () -> service.uploadDatafiles(many));
    }

    // preview and content

    @Test
    void preview_pagesAnyFileTypeWithTotal() {
        DataFilePreview preview = service.previewDatafile(2, 1, 1);
        assertEquals(new DataFilePreview(2, "notes.txt", 1, List.of("b"), 3), preview);
        assertEquals(List.of(), service.previewDatafile(2, 10, 5).lines());
        assertEquals(5, service.previewDatafile(1, null, null).lines().size(), "default page size covers the file");
    }

    @Test
    void preview_validation() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.previewDatafile(1, -1, 10));
        assertThrows(GenericServiceBadRequestException.class, () -> service.previewDatafile(1, 0, 0));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.previewDatafile(1, 0, DataFileServiceV2Impl.MAX_PREVIEW_LINES + 1));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.previewDatafile(404, 0, 10));
    }

    @Test
    void content_keepsItsCsvOnlyLimitAndCountsLines() {
        String nl = System.lineSeparator();
        DataFileServiceV2.ContentPage csv = service.readDatafileContent(1, 1, 2);
        assertEquals("r1" + nl + "r2" + nl, csv.text());
        assertEquals(5, csv.totalLines());
        DataFileServiceV2.ContentPage txt = service.readDatafileContent(2, 1, 1);
        assertEquals("b" + nl + "c" + nl, txt.text(), "as before, lines limits CSV files only");
        assertEquals(3, txt.totalLines());
        assertNull(service.readDatafileContent(404, 0, 1));
    }

    // bulk delete

    @Test
    void bulkDelete() {
        actAs(user("alice"));
        BulkDeleteResult result = service.deleteDatafiles(List.of(1, 404));
        assertEquals(List.of(1), result.deleted());
        assertEquals(List.of(404), result.notFound());
        assertEquals(List.of(1), deleted);
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.deleteDatafiles(List.of(1, 2)),
                "carol's file is not alice's");
        assertEquals(List.of(1), deleted, "the forbidden request deleted nothing");
        assertThrows(GenericServiceBadRequestException.class, () -> service.deleteDatafiles(List.of()));
    }
}
