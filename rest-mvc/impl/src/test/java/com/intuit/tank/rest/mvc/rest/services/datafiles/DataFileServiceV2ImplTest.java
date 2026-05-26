package com.intuit.tank.rest.mvc.rest.services.datafiles;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.datafiles.models.DataFileDescriptorContainer;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.util.DataFileServiceUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DataFileServiceV2ImplTest {

    private final DataFileServiceV2Impl service = new DataFileServiceV2Impl();

    @Test
    void ping_returnsPong() {
        assertTrue(service.ping().contains("PONG"));
    }

    // =====================================================================
    // getDatafile
    // =====================================================================

    @Test
    void getDatafile_returnsDescriptor() {
        DataFile df = createDataFile(1, "test.csv");
        DataFileDescriptor desc = new DataFileDescriptor();

        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(df));
             MockedStatic<DataFileServiceUtil> utilMock = Mockito.mockStatic(DataFileServiceUtil.class)) {
            utilMock.when(() -> DataFileServiceUtil.dataFileToDescriptor(df)).thenReturn(desc);

            DataFileDescriptor result = service.getDatafile(1);
            assertNotNull(result);
        }
    }

    @Test
    void getDatafile_returnsNullWhenNotFound() {
        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            assertNull(service.getDatafile(999));
        }
    }

    @Test
    void getDatafile_throwsOnError() {
        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getDatafile(1));
        }
    }

    // =====================================================================
    // getDatafiles
    // =====================================================================

    @Test
    void getDatafiles_returnsAll() {
        DataFile df1 = createDataFile(1, "file1.csv");
        DataFile df2 = createDataFile(2, "file2.csv");
        DataFileDescriptor d1 = new DataFileDescriptor();
        DataFileDescriptor d2 = new DataFileDescriptor();

        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(List.of(df1, df2)));
             MockedStatic<DataFileServiceUtil> utilMock = Mockito.mockStatic(DataFileServiceUtil.class)) {
            utilMock.when(() -> DataFileServiceUtil.dataFileToDescriptor(df1)).thenReturn(d1);
            utilMock.when(() -> DataFileServiceUtil.dataFileToDescriptor(df2)).thenReturn(d2);

            DataFileDescriptorContainer result = service.getDatafiles();
            assertNotNull(result);
            assertEquals(2, result.getDataFiles().size());
        }
    }

    // =====================================================================
    // getAllDatafileNames
    // =====================================================================

    @Test
    void getAllDatafileNames_returnsNameMap() {
        DataFile df = createDataFile(1, "myfile.csv");

        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findAll()).thenReturn(List.of(df)))) {

            Map<Integer, String> result = service.getAllDatafileNames();
            assertEquals(1, result.size());
            assertEquals("myfile.csv", result.get(1));
        }
    }

    // =====================================================================
    // getDatafileContent
    // =====================================================================

    @Test
    void getDatafileContent_returnsNullWhenNotFound() {
        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            assertNull(service.getDatafileContent(999, null, null));
        }
    }

    // =====================================================================
    // downloadDatafile
    // =====================================================================

    @Test
    void downloadDatafile_returnsNullWhenNotFound() {
        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            assertNull(service.downloadDatafile(999));
        }
    }

    // =====================================================================
    // uploadDatafile
    // =====================================================================

    @Test
    void uploadDatafile_createsNewDatafile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("line1\nline2".getBytes(StandardCharsets.UTF_8)));
        when(file.getOriginalFilename()).thenReturn("data.csv");

        DataFile savedDf = createDataFile(5, "data.csv");

        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> {
                    when(mock.findById(0)).thenReturn(null);
                    doAnswer(invocation -> {
                        DataFile df = invocation.getArgument(0);
                        df.setId(5);
                        return null;
                    }).when(mock).storeDataFile(any(DataFile.class), any());
                })) {

            Map<String, String> result = service.uploadDatafile(null, null, file);
            assertNotNull(result);
            assertTrue(result.get("message").contains("uploaded"));
            assertEquals("5", result.get("datafileId"));
        }
    }

    @Test
    void uploadDatafile_overwritesExisting() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        when(file.getOriginalFilename()).thenReturn("existing.csv");

        DataFile existing = createDataFile(3, "existing.csv");

        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> {
                    when(mock.findById(3)).thenReturn(existing);
                })) {

            Map<String, String> result = service.uploadDatafile(3, null, file);
            assertTrue(result.get("message").contains("overwritten"));
        }
    }

    // =====================================================================
    // deleteDatafile
    // =====================================================================

    @Test
    void deleteDatafile_deletesExisting() {
        DataFile df = createDataFile(1, "todelete.csv");

        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(df))) {

            String result = service.deleteDatafile(1);
            assertEquals("", result);

            verify(daoMock.constructed().get(0)).delete(df);
        }
    }

    @Test
    void deleteDatafile_returnsMessageWhenNotFound() {
        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(999)).thenReturn(null))) {

            String result = service.deleteDatafile(999);
            assertTrue(result.contains("does not exist"));
        }
    }

    @Test
    void deleteDatafile_throwsOnError() {
        try (MockedConstruction<DataFileDao> daoMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceDeleteException.class, () -> service.deleteDatafile(1));
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private static DataFile createDataFile(int id, String path) {
        DataFile df = new DataFile();
        df.setId(id);
        df.setPath(path);
        df.setCreated(new Date());
        df.setModified(new Date());
        return df;
    }
}
