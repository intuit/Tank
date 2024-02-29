package com.intuit.tank.rest.mvc.rest.controllers.services;


import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.services.datafiles.DataFileServiceV2Impl;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;
import com.intuit.tank.rest.mvc.rest.util.DataFileServiceUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class DataFileServiceV2ImplTest {

    @Test
    public void testPing() {
        DataFileServiceV2Impl service = new DataFileServiceV2Impl();
        assertEquals("PONG DataFileServiceV2", service.ping());
    }

    @Test
    public void testGetDatafile() {
        DataFileDao mockDataFileDao = mock(DataFileDao.class);
        DataFile mockDataFile = mock(DataFile.class);
        DataFileDescriptor mockDescriptor = mock(DataFileDescriptor.class);

        when(mockDataFileDao.findById(anyInt())).thenReturn(mockDataFile);

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDataFileDao;
            }
        };

        try (MockedStatic<DataFileServiceUtil> mockedDataFileServiceUtil = Mockito.mockStatic(DataFileServiceUtil.class)) {
            mockedDataFileServiceUtil.when(() -> DataFileServiceUtil.dataFileToDescriptor(mockDataFile))
                    .thenReturn(mockDescriptor);

            DataFileDescriptor result = service.getDatafile(1);
            assertEquals(mockDescriptor, result);
        }
    }

    @Test
    public void testGetDatafileNotFound() {
        DataFileDao mockDataFileDao = mock(DataFileDao.class);
        when(mockDataFileDao.findById(anyInt())).thenReturn(null);

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDataFileDao;
            }
        };

        DataFileDescriptor result = service.getDatafile(7);
        assertNull(result);
    }

    @Test
    public void testGetDatafileException() {
        DataFileDao mockDataFileDao = mock(DataFileDao.class);
        when(mockDataFileDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDataFileDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getDatafile(2));
    }

    @Test
    public void testGetDatafiles() {
        DataFileDao mockDao = mock(DataFileDao.class);
        DataFile dataFile = mock(DataFile.class);
        List<DataFile> allDataFiles = Arrays.asList(dataFile);
        when(mockDao.findAll()).thenReturn(allDataFiles);

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };

        DataFileDescriptorContainer container = service.getDatafiles();
        assertNotNull(container);
        assertEquals(1, container.getDataFiles().size());
    }


    @Test
    public void testGetDatafilesException() {
        DataFileDao mockDao = mock(DataFileDao.class);

        when(mockDao.findAll()).thenThrow(new RuntimeException("trigger exception"));

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };
        assertThrows(GenericServiceResourceNotFoundException.class, service::getDatafiles);
    }

    @Test
    public void testGetAllDatafileNames() {
        DataFileDao mockDao = mock(DataFileDao.class);
        DataFile dataFile = mock(DataFile.class);

        when(dataFile.getId()).thenReturn(9);
        when(dataFile.getPath()).thenReturn("testDatafile.csv");

        List<DataFile> allDataFiles = Arrays.asList(dataFile);

        when(mockDao.findAll()).thenReturn(allDataFiles);

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };

        Map<Integer, String> dataFileNames = service.getAllDatafileNames();

        assertNotNull(dataFileNames);
        assertEquals(1, dataFileNames.size());
        assertEquals("testDatafile.csv", dataFileNames.get(9));
    }

    @Test
    public void testGetAllDatafileNamesException() {
        DataFileDao mockDao = mock(DataFileDao.class);

        when(mockDao.findAll()).thenThrow(new RuntimeException("trigger exception"));

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, service::getAllDatafileNames);
    }

    @Test
    public void testGetDatafileContent() {
        DataFileDao mockDao = mock(DataFileDao.class);
        DataFile mockDataFile = mock(DataFile.class);
        FileStorage mockFileStorage = mock(FileStorage.class);
        FileData mockFileData = mock(FileData.class);

        when(mockDao.findById(any())).thenReturn(mockDataFile);
        when(mockFileData.getFileName()).thenReturn("testDatafile.csv");

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }

            @Override
            protected FileStorage createFileStorage() {
                return mockFileStorage;
            }
        };

        StreamingResponseBody responseBody = service.getDatafileContent(1, 0, 10);
        assertNotNull(responseBody);
    }

    @Test
    public void testGetDatafileContentNullDataFile() {
        DataFileDao mockDao = mock(DataFileDao.class);

        when(mockDao.findById(any())).thenReturn(null);

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };

        StreamingResponseBody responseBody = service.getDatafileContent(1, 0, 10);
        assertNull(responseBody);
    }

    @Test
    public void testGetDatafileContentException() throws IOException {
        DataFileDao mockDao = mock(DataFileDao.class);
        DataFile mockDataFile = mock(DataFile.class);
        FileData mockFileData = mock(FileData.class);
        FileStorage mockFileStorage = mock(FileStorage.class);
        InputStream mockInputStream = mock(InputStream.class);

        when(mockDao.findById(any())).thenReturn(mockDataFile);
        when(mockFileData.getFileName()).thenReturn("testDatafile.csv");
        when(mockFileStorage.readFileData(any())).thenReturn(mockInputStream);
        when(mockInputStream.read(any(byte[].class), anyInt(), anyInt())).thenThrow(IOException.class);

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }

            @Override
            protected FileStorage createFileStorage() {
                return mockFileStorage;
            }

            @Override
            protected FileData getFileData(DataFile dataFile) {
                return mockFileData;
            }
        };

        StreamingResponseBody responseBody = service.getDatafileContent(1, 0, 10);

        assertThrows(GenericServiceResourceNotFoundException.class, () -> {
            responseBody.writeTo(mock(OutputStream.class));
        });
    }



    @Test
    public void testDownloadDatafile() {
        DataFileDao mockDao = mock(DataFileDao.class);
        DataFile mockDataFile = mock(DataFile.class);
        FileStorage mockFileStorage = mock(FileStorage.class);
        FileData mockFileData = mock(FileData.class);

        when(mockDao.findById(any())).thenReturn(mockDataFile);
        when(mockDataFile.getPath()).thenReturn("testDatafile.csv");
        when(mockFileData.getFileName()).thenReturn("testDatafile.csv");

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }

            @Override
            protected FileStorage createFileStorage() {
                return mockFileStorage;
            }
        };

        Map<String, StreamingResponseBody> result = service.downloadDatafile(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testDatafile.csv", result.keySet().iterator().next());
    }

    @Test
    public void testDownloadDatafileNotFound() {
        DataFileDao mockDao = mock(DataFileDao.class);
        when(mockDao.findById(any())).thenReturn(null);

        DataFileServiceV2Impl service = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };

        Map<String, StreamingResponseBody> result = service.downloadDatafile(1);
        assertNull(result);
    }

    @Test
    public void testUploadDatafile() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        DataFileDao mockDao = mock(DataFileDao.class);
        BufferedReader mockBufferedReader = mock(BufferedReader.class);
        InputStream mockInputStream = mock(InputStream.class);

        when(mockFile.getOriginalFilename()).thenReturn("testDatafile.csv");
        when(mockDao.findById(anyInt())).thenReturn(null);
        when(mockBufferedReader.readLine()).thenReturn("line1", "line2", "line3", null);

        DataFileServiceV2Impl dataFileService = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }

            @Override
            protected InputStream createInputStream(BufferedReader bufferedReader) {
                return mockInputStream;
            }

            @Override
            protected BufferedReader createBufferedReader(String contentEncoding, MultipartFile file) throws IOException {
                return mockBufferedReader;
            }
        };

        Map<String, String> result = dataFileService.uploadDatafile(0, "gzip", mockFile);
        assertTrue(result.get("message").contains("has been uploaded"));
    }

    @Test
    public void testUploadDatafileAlreadyExists() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        DataFileDao mockDao = mock(DataFileDao.class);
        BufferedReader mockBufferedReader = mock(BufferedReader.class);
        InputStream mockInputStream = mock(InputStream.class);
        DataFile existingDataFile = new DataFile();

        when(mockFile.getOriginalFilename()).thenReturn("testDatafile.csv");
        when(mockDao.findById(anyInt())).thenReturn(existingDataFile);
        when(mockBufferedReader.readLine()).thenReturn("line1", "line2", "line3", null);

        DataFileServiceV2Impl dataFileService = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }

            @Override
            protected InputStream createInputStream(BufferedReader bufferedReader) {
                return mockInputStream;
            }

            @Override
            protected BufferedReader createBufferedReader(String contentEncoding, MultipartFile file) throws IOException {
                return mockBufferedReader;
            }
        };
        Map<String, String> result = dataFileService.uploadDatafile(1, "gzip", mockFile);
        assertTrue(result.get("message").contains("overwritten with new datafile"));
    }


    @Test
    public void testUploadDatafileException() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        DataFileDao mockDao = mock(DataFileDao.class);
        BufferedReader mockBufferedReader = mock(BufferedReader.class);
        InputStream mockInputStream = mock(InputStream.class);

        when(mockBufferedReader.readLine()).thenThrow(new IOException("trigger exception"));

        DataFileServiceV2Impl dataFileService = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }

            @Override
            protected InputStream createInputStream(BufferedReader bufferedReader) {
                return mockInputStream;
            }

            @Override
            protected BufferedReader createBufferedReader(String contentEncoding, MultipartFile file) throws IOException {
                return mockBufferedReader;
            }
        };

        assertThrows(GenericServiceCreateOrUpdateException.class, () -> {
            dataFileService.uploadDatafile(0, "gzip", mockFile);
        });
    }

    @Test
    public void testDeleteDatafile() {
        DataFileDao mockDao = mock(DataFileDao.class);
        DataFile mockDataFile = mock(DataFile.class);
        when(mockDao.findById(anyInt())).thenReturn(mockDataFile);

        DataFileServiceV2Impl dataFileService = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };

        String result = dataFileService.deleteDatafile(1);

        assertTrue(result.isEmpty());
        verify(mockDao, times(1)).delete(mockDataFile);
    }

    @Test
    public void testDeleteDatafileNotExist() {
        DataFileDao mockDao = mock(DataFileDao.class);
        when(mockDao.findById(anyInt())).thenReturn(null);

        DataFileServiceV2Impl dataFileService = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                return mockDao;
            }
        };

        String result = dataFileService.deleteDatafile(1);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("does not exist"));
        verify(mockDao, times(0)).delete(1);
    }

    @Test
    public void testDeleteDatafileException() {
        DataFileDao mockDao = mock(DataFileDao.class);
        DataFile mockDataFile = mock(DataFile.class);

        when(mockDao.findById(anyInt())).thenReturn(mockDataFile);

        DataFileServiceV2Impl dataFileService = new DataFileServiceV2Impl() {
            @Override
            protected DataFileDao createDataFileDao() {
                throw new RuntimeException("trigger exception");
            }
        };

        assertThrows(GenericServiceDeleteException.class, () -> {
            dataFileService.deleteDatafile(1);
        });
    }
}
