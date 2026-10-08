/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.dao.PagedResult;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.ScriptFilterDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.CopyRequest;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ScriptDocument;
import com.intuit.tank.rest.mvc.rest.models.ScriptSummary;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.NewScriptRequest;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.script.processor.ScriptProcessor;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ScriptServiceV2ImplEditorTest {

    private static final Date MODIFIED = new Date(1_700_000_000_000L);

    @InjectMocks
    private ScriptServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;
    private final List<MockedConstruction<?>> constructions = new ArrayList<>();
    private final MessageEventSender events = mock(MessageEventSender.class);
    private final ScriptProcessor processor = mock(ScriptProcessor.class);
    private final Map<Integer, Script> stored = new HashMap<>();
    private final List<Script> saved = new ArrayList<>();
    private PagedQuery lastQuery;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        useConfig(true, Map.of(AccessRight.CREATE_SCRIPT, List.of("authors")));
        actAs(user("alice", "authors"));
        stored.put(1, script(1, "Checkout", "alice"));
        stored.put(2, script(2, "Other", "carol"));

        constructions.add(Mockito.mockConstruction(ScriptDao.class, (dao, ctx) -> {
            when(dao.findById(anyInt())).thenAnswer(i -> fresh(i.getArgument(0)));
            when(dao.saveOrUpdate(any(Script.class))).thenAnswer(i -> {
                Script s = i.getArgument(0);
                if (s.getId() == 0) {
                    s.setId(99);
                }
                s.setModified(new Date(MODIFIED.getTime() + 60_000));
                saved.add(s);
                stored.put(s.getId(), s);
                return s;
            });
            when(dao.findPaged(any())).thenAnswer(i -> {
                lastQuery = i.getArgument(0);
                return new PagedResult<>(List.of(stored.get(1)), 3);
            });
        }));
        constructions.add(Mockito.mockConstruction(ScriptFilterDao.class, (dao, ctx) ->
                when(dao.findForIds(anyList())).thenAnswer(i -> ((List<Integer>) i.getArgument(0)).stream()
                        .filter(id -> id < 10).map(id -> {
                            ScriptFilter f = new ScriptFilter();
                            f.setId(id);
                            return f;
                        }).toList())));
        constructions.add(Mockito.mockConstruction(ServletInjector.class, (injector, ctx) -> {
            when(injector.getManagedBean(eq(servletContext), eq(MessageEventSender.class))).thenReturn(events);
            when(injector.getManagedBean(eq(servletContext), eq(ScriptProcessor.class))).thenReturn(processor);
        }));
    }

    @AfterEach
    void tearDown() throws Exception {
        constructions.forEach(MockedConstruction::close);
        mocks.close();
        reset();
    }

    private static Script script(int id, String name, String owner) {
        Script s = new Script();
        s.setId(id);
        s.setName(name);
        s.setCreator(owner);
        s.setModified(MODIFIED);
        ScriptStep step = new ScriptStep();
        step.setUuid("u" + id);
        step.setType(ScriptConstants.REQUEST);
        step.setResponse("resp" + id);
        s.getScriptSteps().add(step);
        return s;
    }

    /** a copy of what is stored, as each DAO lookup would return */
    private Script fresh(Integer id) {
        Script s = stored.get(id);
        if (s == null) {
            return null;
        }
        Script copy = script(s.getId(), s.getName(), s.getCreator());
        copy.setModified(s.getModified());
        copy.getScriptSteps().clear();
        copy.getScriptSteps().addAll(s.getScriptSteps());
        return copy;
    }

    private ScriptDocument edited(int id, Date modified) {
        ScriptDocument doc = service.getScriptDocument(id);
        return new ScriptDocument(doc.id(), "Renamed", "Shop", null, doc.owner(), null, modified, doc.steps(), null);
    }

    private ModificationType lastEvent() {
        ArgumentCaptor<ModifiedEntityMessage> msg = ArgumentCaptor.forClass(ModifiedEntityMessage.class);
        verify(events, atLeastOnce()).sendEvent(msg.capture());
        return msg.getValue().getType();
    }

    @Test
    void list() {
        PageResponse<ScriptSummary> page = service.listScripts(0, 10, "runtime,desc", "alice", " check ");
        assertEquals(3, page.total());
        assertEquals("Checkout", page.items().get(0).name());
        assertEquals("runtime", lastQuery.sortProperty());
        assertEquals(Map.of("creator", "alice"), lastQuery.equalTo());
        assertEquals("check", lastQuery.search());
        assertEquals(List.of("name", "productName", "comments"), lastQuery.searchProperties());
    }

    @Test
    void needsAUser() {
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.listScripts(0, 10, null, null, null));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.getScriptDocument(1));
    }

    @Test
    void getDocument() {
        ScriptDocument doc = service.getScriptDocument(1);
        assertEquals("Checkout", doc.name());
        assertEquals(new ScriptDocument.Permissions(true, true), doc.permissions());
        assertNull(doc.steps().get(0).getResponse());
        assertEquals(new ScriptDocument.Permissions(false, false), service.getScriptDocument(2).permissions());
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getScriptDocument(404));
    }

    @Test
    void update_saves() {
        ScriptDocument result = service.updateScriptDocument(1, edited(1, MODIFIED));
        assertEquals("Renamed", result.name());
        assertEquals("resp1", saved.get(0).getScriptSteps().get(0).getResponse(), "the recorded response is kept");
        assertEquals(1, saved.get(0).getScriptSteps().get(0).getStepIndex());
        assertEquals(ModificationType.UPDATE, lastEvent());
    }

    @Test
    void update_rejections() {
        assertThrows(GenericServiceConflictException.class, () -> service.updateScriptDocument(1,
                edited(1, new Date(MODIFIED.getTime() - 5000))));
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateScriptDocument(1, edited(1, null)));
        ScriptDocument blank = new ScriptDocument(1, " ", null, null, null, null, MODIFIED, List.of(), null);
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateScriptDocument(1, blank));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.updateScriptDocument(2, edited(2, MODIFIED)));
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateScriptDocument(1, null));
        assertEquals(List.of(), saved);
    }

    @Test
    void copy() {
        ScriptSummary copy = service.copyScript(1, new CopyRequest(" Checkout copy "));
        assertEquals(99, copy.id());
        assertEquals("Checkout copy", copy.name());
        assertEquals("alice", copy.owner());
        assertNotEquals("u1", saved.get(0).getScriptSteps().get(0).getUuid(), "the copy's steps get their own uuids");
        assertEquals(ModificationType.ADD, lastEvent());

        actAs(user("bob"));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.copyScript(1, new CopyRequest("x")));
        actAs(user("alice", "authors"));
        assertThrows(GenericServiceBadRequestException.class, () -> service.copyScript(1, new CopyRequest(" ")));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.copyScript(404, new CopyRequest("y")));
    }

    @Test
    void createBlank() {
        ScriptSummary created = service.createBlankScript(new NewScriptRequest(" Checkout v2 ", "Shop", " "));
        assertEquals(99, created.id());
        assertEquals("Checkout v2", created.name());
        assertEquals("Shop", created.productName());
        assertEquals("alice", created.owner());
        assertNull(saved.get(0).getComments(), "blank comments are not stored");
        assertTrue(saved.get(0).getScriptSteps().isEmpty());
        assertEquals(ModificationType.ADD, lastEvent());

        assertThrows(GenericServiceBadRequestException.class, () -> service.createBlankScript(new NewScriptRequest(" ", null, null)));
        assertThrows(GenericServiceBadRequestException.class, () -> service.createBlankScript(null));
        actAs(user("bob"));
        assertThrows(GenericServiceForbiddenAccessException.class,
                () -> service.createBlankScript(new NewScriptRequest("x", null, null)));
    }

    @Test
    void deleteSeveral() {
        BulkDeleteResult result = service.deleteScripts(List.of(1, 404, 1));
        assertEquals(List.of(1), result.deleted());
        assertEquals(List.of(404), result.notFound());
        assertEquals(ModificationType.DELETE, lastEvent());
    }

    @Test
    void deleteSeveralIsAllOrNothing() {
        // alice owns script 1 but not carol's script 2, and has no DELETE_SCRIPT right
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.deleteScripts(List.of(1, 2)));
        for (MockedConstruction<?> construction : constructions) {
            for (Object dao : construction.constructed()) {
                if (dao instanceof ScriptDao scriptDao) {
                    verify(scriptDao, never()).delete(any(Script.class));
                }
            }
        }
        assertThrows(GenericServiceBadRequestException.class, () -> service.deleteScripts(List.of()));
    }

    @Test
    void stepResponse() {
        assertEquals("resp1", service.getStepResponse(1, "u1"));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getStepResponse(1, "missing"));
        stored.get(1).getScriptSteps().get(0).setResponse(null);
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getStepResponse(1, "u1"));
    }

    @Test
    void recordingUploadAppliesProductAndFiltersInOrder() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("<recording/>".getBytes()));

        Map<String, String> result = service.createScript("Recorded", null, "", null, null, null, file, "Shop", List.of(3, 1, 3));

        assertEquals("99", result.get("scriptId"));
        assertEquals("Shop", saved.get(0).getProductName());
        ArgumentCaptor<Collection<ScriptFilter>> filters = ArgumentCaptor.forClass(Collection.class);
        verify(processor).getScriptSteps(any(), filters.capture());
        assertEquals(List.of(3, 1), filters.getValue().stream().map(ScriptFilter::getId).toList());
    }

    @Test
    void recordingUploadWithUnknownFilter() {
        MultipartFile file = mock(MultipartFile.class);
        GenericServiceBadRequestException e = assertThrows(GenericServiceBadRequestException.class,
                () -> service.createScript("Recorded", null, "", null, null, null, file, null, List.of(1, 42)));
        assertTrue(e.getMessage().contains("[42]"), e.getMessage());
        verifyNoInteractions(processor);
    }

    @Test
    void sameSecond() {
        assertTrue(ScriptServiceV2Impl.sameSecond(new Date(1000), new Date(1999)));
        assertFalse(ScriptServiceV2Impl.sameSecond(new Date(1000), null));
    }
}
