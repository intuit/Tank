package com.intuit.tank.rest.mvc.rest.services.logs;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
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

    @InjectMocks
    private LogServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        // Should default start to "0" and throw because file doesn't exist
        assertThrows(GenericServiceResourceNotFoundException.class,
                () -> service.getFile("nonexistent.log", null));
    }

    @Test
    void getFile_readsExistingFile() throws IOException {
        // Create a real "logs" directory with a test file
        File logsDir = new File("logs");
        logsDir.mkdirs();
        File testFile = new File(logsDir, "test-log-for-coverage.log");
        try {
            Files.writeString(testFile.toPath(), "log line 1\nlog line 2\n");

            StreamingResponseBody body = service.getFile("test-log-for-coverage.log", "0");
            assertNotNull(body);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            body.writeTo(out);
            String content = out.toString();
            assertTrue(content.contains("log line"));
        } finally {
            testFile.delete();
        }
    }
}
