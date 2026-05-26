package com.intuit.tank.rest.mvc.rest.services.scripts;

import com.intuit.tank.dao.ExternalScriptDao;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.Script;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
import com.intuit.tank.script.models.*;
import com.intuit.tank.script.processor.ScriptProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ScriptServiceV2ImplTest {

    @InjectMocks
    private ScriptServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    // =====================================================================
    // ping
    // =====================================================================

    @Test
    void ping_returnsPong() {
        String result = service.ping();
        assertTrue(result.startsWith("PONG"));
        assertTrue(result.contains("ScriptServiceV2"));
    }

    // =====================================================================
    // getScript
    // =====================================================================

    @Test
    void getScript_returnsScriptDescription() {
        Script script = createScript(1, "TestScript");
        ScriptDescription desc = new ScriptDescription();

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(script));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.scriptToScriptDescription(script)).thenReturn(desc);

            ScriptDescription result = service.getScript(1);
            assertNotNull(result);
        }
    }

    @Test
    void getScript_returnsNullWhenNotFound() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            ScriptDescription result = service.getScript(999);
            assertNull(result);
        }
    }

    @Test
    void getScript_throwsOnError() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("DB error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getScript(1));
        }
    }

    // =====================================================================
    // getScripts
    // =====================================================================

    @Test
    void getScripts_returnsAllScripts() {
        Script s1 = createScript(1, "Script1");
        Script s2 = createScript(2, "Script2");
        ScriptDescription d1 = new ScriptDescription();
        ScriptDescription d2 = new ScriptDescription();

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(List.of(s1, s2)));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.scriptToScriptDescription(s1)).thenReturn(d1);
            utilMock.when(() -> ScriptServiceUtil.scriptToScriptDescription(s2)).thenReturn(d2);

            ScriptDescriptionContainer result = service.getScripts();
            assertNotNull(result);
            assertEquals(2, result.getScripts().size());
        }
    }

    // =====================================================================
    // getAllScriptNames
    // =====================================================================

    @Test
    void getAllScriptNames_returnsNameMap() {
        Script s1 = createScript(1, "Alpha");
        Script s2 = createScript(2, "Beta");

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(List.of(s1, s2)))) {

            Map<Integer, String> result = service.getAllScriptNames();
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Alpha", result.get(1));
            assertEquals("Beta", result.get(2));
        }
    }

    // =====================================================================
    // deleteScript
    // =====================================================================

    @Test
    void deleteScript_deletesExistingScript() {
        Script script = createScript(1, "ToDelete");

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> {
                    when(mock.findById(1)).thenReturn(script);
                })) {

            String result = service.deleteScript(1);
            assertEquals("", result);

            ScriptDao dao = daoMock.constructed().get(0);
            verify(dao).delete(script);
        }
    }

    @Test
    void deleteScript_returnsMessageWhenNotFound() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            String result = service.deleteScript(999);
            assertTrue(result.contains("does not exist"));
        }
    }

    @Test
    void deleteScript_throwsOnError() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceDeleteException.class, () -> service.deleteScript(1));
        }
    }

    // =====================================================================
    // downloadScript
    // =====================================================================

    @Test
    void downloadScript_returnsStreamingBody() {
        Script script = createScript(1, "DownloadMe");
        ScriptTO scriptTO = new ScriptTO();
        StreamingResponseBody body = mock(StreamingResponseBody.class);

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(script));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class);
             MockedStatic<ResponseUtil> responseMock = Mockito.mockStatic(ResponseUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.scriptToTransferObject(script)).thenReturn(scriptTO);
            responseMock.when(() -> ResponseUtil.getXMLStream(scriptTO)).thenReturn(body);

            Map<String, StreamingResponseBody> result = service.downloadScript(1);
            assertNotNull(result);
            assertTrue(result.containsKey("DownloadMe_TS.xml"));
        }
    }

    @Test
    void downloadScript_returnsNullWhenNotFound() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            Map<String, StreamingResponseBody> result = service.downloadScript(999);
            assertNull(result);
        }
    }

    // =====================================================================
    // createScript - copy path
    // =====================================================================

    @Test
    void createScript_copyPath_copiesExistingScript() throws IOException {
        Script sourceScript = createScript(10, "SourceScript");
        Script copiedScript = createScript(11, "CopiedScript");

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> {
                    when(mock.findById(10)).thenReturn(sourceScript);
                    when(mock.saveOrUpdate(any(Script.class))).thenReturn(copiedScript);
                });
             MockedStatic<ScriptUtil> scriptUtilMock = Mockito.mockStatic(ScriptUtil.class)) {
            scriptUtilMock.when(() -> ScriptUtil.copyScript("System", "CopiedScript", sourceScript))
                    .thenReturn(copiedScript);

            Map<String, String> result = service.createScript("CopiedScript", null, null, "true", 10, null, null);
            assertNotNull(result);
            assertTrue(result.get("message").contains("created successfully"));
        }
    }

    @Test
    void createScript_copyPath_throwsWhenNoSourceId() {
        assertThrows(GenericServiceCreateOrUpdateException.class,
                () -> service.createScript("NewCopy", null, null, "true", null, null, null));
    }

    @Test
    void createScript_copyPath_throwsWhenNoName() {
        assertThrows(GenericServiceCreateOrUpdateException.class,
                () -> service.createScript("", null, null, "true", 10, null, null));
    }

    @Test
    void createScript_copyPath_throwsWhenSourceNotFound() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            assertThrows(GenericServiceCreateOrUpdateException.class,
                    () -> service.createScript("CopyName", null, null, "true", 999, null, null));
        }
    }

    // =====================================================================
    // createScript - recording/proxy upload path
    // =====================================================================

    @Test
    void createScript_recordingPath_createsNewScript() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes(StandardCharsets.UTF_8)));

        Script newScript = createScript(0, "New");
        Script savedScript = createScript(5, "RecordedScript");

        ScriptProcessor mockProcessor = mock(ScriptProcessor.class);
        MessageEventSender mockSender = mock(MessageEventSender.class);

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> {
                    when(mock.findById(0)).thenReturn(null);
                    when(mock.saveOrUpdate(any(Script.class))).thenReturn(savedScript);
                });
             MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> {
                    when(mock.getManagedBean(eq(servletContext), eq(ScriptProcessor.class))).thenReturn(mockProcessor);
                    when(mock.getManagedBean(eq(servletContext), eq(MessageEventSender.class))).thenReturn(mockSender);
                })) {

            Map<String, String> result = service.createScript("RecordedScript", null, "recording", null, null, null, file);
            assertNotNull(result);
            assertTrue(result.containsKey("scriptId"));
        }
    }

    @Test
    void createScript_recordingPath_overwritesExistingScript() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes(StandardCharsets.UTF_8)));

        Script existingScript = createScript(5, "Existing");
        ScriptProcessor mockProcessor = mock(ScriptProcessor.class);
        MessageEventSender mockSender = mock(MessageEventSender.class);

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> {
                    when(mock.findById(5)).thenReturn(existingScript);
                    when(mock.saveOrUpdate(any(Script.class))).thenReturn(existingScript);
                });
             MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> {
                    when(mock.getManagedBean(eq(servletContext), eq(ScriptProcessor.class))).thenReturn(mockProcessor);
                    when(mock.getManagedBean(eq(servletContext), eq(MessageEventSender.class))).thenReturn(mockSender);
                })) {

            Map<String, String> result = service.createScript("Updated", 5, "recording", null, null, null, file);
            assertNotNull(result);
            assertTrue(result.get("message").contains("overwritten"));
        }
    }

    // =====================================================================
    // createScript - XML update path
    // =====================================================================

    @Test
    void createScript_updatePath_updatesExistingScript() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("<xml/>".getBytes(StandardCharsets.UTF_8)));

        ScriptTO scriptTO = new ScriptTO();
        Script parsedScript = createScript(5, "ExistingName");
        Script existingScript = createScript(5, "ExistingName");
        existingScript.setSerializedScriptStepId(99);

        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> {
                    when(mock.findById(5)).thenReturn(existingScript);
                    when(mock.saveOrUpdate(any(Script.class))).thenReturn(parsedScript);
                });
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.parseXMLtoScriptTO(any(InputStream.class))).thenReturn(scriptTO);
            utilMock.when(() -> ScriptServiceUtil.transferObjectToScript(scriptTO)).thenReturn(parsedScript);

            Map<String, String> result = service.createScript(null, null, null, null, null, null, file);
            assertNotNull(result);
            assertTrue(result.get("message").contains("updated successfully"));
        }
    }

    // =====================================================================
    // External Scripts
    // =====================================================================

    @Test
    void getExternalScripts_returnsAll() {
        ExternalScript es = new ExternalScript();
        es.setName("ExtScript");
        ExternalScriptTO to = new ExternalScriptTO();

        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(List.of(es)));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.externalScriptToTO(es)).thenReturn(to);

            ExternalScriptContainer result = service.getExternalScripts();
            assertNotNull(result);
            assertEquals(1, result.getScripts().size());
        }
    }

    @Test
    void getExternalScript_returnsScript() {
        ExternalScript es = new ExternalScript();
        ExternalScriptTO to = new ExternalScriptTO();

        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(es));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.externalScriptToTO(es)).thenReturn(to);

            ExternalScriptTO result = service.getExternalScript(1);
            assertNotNull(result);
        }
    }

    @Test
    void getExternalScript_returnsNullWhenNotFound() {
        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            ExternalScriptTO result = service.getExternalScript(999);
            assertNull(result);
        }
    }

    @Test
    void createExternalScript_savesAndReturns() {
        ExternalScriptTO requestTO = new ExternalScriptTO();
        ExternalScript entity = new ExternalScript();
        ExternalScript saved = new ExternalScript();
        saved.setName("Saved");
        ExternalScriptTO resultTO = new ExternalScriptTO();

        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.saveOrUpdate(any(ExternalScript.class))).thenReturn(saved));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.TOToExternalScript(requestTO)).thenReturn(entity);
            utilMock.when(() -> ScriptServiceUtil.externalScriptToTO(saved)).thenReturn(resultTO);

            ExternalScriptTO result = service.createExternalScript(requestTO);
            assertNotNull(result);
        }
    }

    @Test
    void deleteExternalScript_deletesExisting() {
        ExternalScript es = new ExternalScript();
        es.setName("ToDelete");

        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(es))) {

            String result = service.deleteExternalScript(1);
            assertEquals("", result);

            ExternalScriptDao dao = daoMock.constructed().get(0);
            verify(dao).delete(es);
        }
    }

    @Test
    void deleteExternalScript_returnsMessageWhenNotFound() {
        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            String result = service.deleteExternalScript(999);
            assertTrue(result.contains("does not exist"));
        }
    }

    @Test
    void deleteExternalScript_throwsOnError() {
        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceDeleteException.class, () -> service.deleteExternalScript(1));
        }
    }

    // =====================================================================
    // downloadHarnessScript
    // =====================================================================

    @Test
    void downloadHarnessScript_returnsNullWhenNotFound() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            assertNull(service.downloadHarnessScript(999));
        }
    }

    // =====================================================================
    // downloadExternalScript
    // =====================================================================

    @Test
    void downloadExternalScript_returnsNullWhenNotFound() {
        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            assertNull(service.downloadExternalScript(999));
        }
    }

    @Test
    void downloadExternalScript_returnsStreamWhenFound() {
        ExternalScript es = new ExternalScript();
        es.setName("MyExt");
        es.setCreated(new java.util.Date());
        es.setModified(new java.util.Date());
        ExternalScriptTO to = ExternalScriptTO.builder().withId(1).withName("MyExt").build();
        StreamingResponseBody body = mock(StreamingResponseBody.class);

        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(es));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class);
             MockedStatic<ResponseUtil> responseMock = Mockito.mockStatic(ResponseUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.externalScriptToTO(es)).thenReturn(to);
            responseMock.when(() -> ResponseUtil.getXMLStream(to)).thenReturn(body);

            Map<String, StreamingResponseBody> result = service.downloadExternalScript(1);
            assertNotNull(result);
            assertTrue(result.containsKey("MyExt_ETS.xml"));
        }
    }

    // =====================================================================
    // Error paths
    // =====================================================================

    @Test
    void getScripts_throwsOnError() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findAll()).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getScripts());
        }
    }

    @Test
    void getAllScriptNames_throwsOnError() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findAll()).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getAllScriptNames());
        }
    }

    @Test
    void downloadScript_throwsOnError() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.downloadScript(1));
        }
    }

    @Test
    void downloadHarnessScript_throwsOnError() {
        try (MockedConstruction<ScriptDao> daoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.downloadHarnessScript(1));
        }
    }

    @Test
    void getExternalScripts_throwsOnError() {
        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findAll()).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getExternalScripts());
        }
    }

    @Test
    void getExternalScript_throwsOnError() {
        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getExternalScript(1));
        }
    }

    @Test
    void createExternalScript_throwsOnError() {
        ExternalScriptTO requestTO = new ExternalScriptTO();

        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.saveOrUpdate(any(ExternalScript.class))).thenThrow(new RuntimeException("error")));
             MockedStatic<ScriptServiceUtil> utilMock = Mockito.mockStatic(ScriptServiceUtil.class)) {
            utilMock.when(() -> ScriptServiceUtil.TOToExternalScript(requestTO)).thenReturn(new ExternalScript());

            assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.createExternalScript(requestTO));
        }
    }

    @Test
    void downloadExternalScript_throwsOnError() {
        try (MockedConstruction<ExternalScriptDao> daoMock = Mockito.mockConstruction(ExternalScriptDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.downloadExternalScript(1));
        }
    }

    // =====================================================================
    // Helper
    // =====================================================================

    private static Script createScript(int id, String name) {
        Script script = new Script();
        script.setId(id);
        script.setName(name);
        script.setCreated(new Date());
        script.setModified(new Date());
        return script;
    }
}
