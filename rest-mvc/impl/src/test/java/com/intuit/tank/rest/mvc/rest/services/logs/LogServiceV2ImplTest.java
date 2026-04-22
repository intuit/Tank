package com.intuit.tank.rest.mvc.rest.services.logs;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceV2ImplTest {

    private static final String TEST_LOG_FILENAME = "test-log-for-coverage.log";

    @InjectMocks
    private LogServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private File testFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void getFile_rejectsPathTraversal() {
        assertThrows(GenericServiceResourceNotFoundException.class,
                () -> service.getFile("../etc/passwd", "0"));
    }

    @Test
    void getFile_rejectsAbsolutePath() {
        assertThrows(GenericServiceResourceNotFoundException.class,
                () -> service.getFile("/var/log/syslog", "0"));
    }

    @Test
    void getFile_throwsWhenFileNotFound() {
        assertThrows(GenericServiceResourceNotFoundException.class,
                () -> service.getFile("nonexistent-file.log", "0"));
    }

    @Test
    void getFile_handlesNullStart() {
        assertThrows(GenericServiceResourceNotFoundException.class,
                () -> service.getFile("nonexistent.log", null));
    }

    @Test
    void getFile_readsExistingFile() throws IOException {
        File logsDir = new File("logs");
        logsDir.mkdirs();
        testFile = new File(logsDir, TEST_LOG_FILENAME);
        Files.writeString(testFile.toPath(), "log line 1\nlog line 2\n");

        StreamingResponseBody body = service.getFile(TEST_LOG_FILENAME, "0");
        assertNotNull(body);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        body.writeTo(out);
        String content = out.toString();
        assertTrue(content.contains("log line 1"));
        assertTrue(content.contains("log line 2"));
    }
}
