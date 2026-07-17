package com.intuit.tank.rest.mvc.rest.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void getFileStreamingResponseBody_readsEntireFile() throws IOException {
        File testFile = createTestFile("line1\nline2\nline3\n");
        long total = testFile.length();

        StreamingResponseBody body = FileReader.getFileStreamingResponseBody(testFile, total, "0");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        body.writeTo(out);

        String content = out.toString(StandardCharsets.UTF_8);
        assertTrue(content.contains("line1"));
        assertTrue(content.contains("line3"));
    }

    @Test
    void getFileStreamingResponseBody_readsFromOffset() throws IOException {
        File testFile = createTestFile("AAAA\nBBBB\nCCCC\n");
        long total = testFile.length();

        // Start at offset 5 (after "AAAA\n")
        StreamingResponseBody body = FileReader.getFileStreamingResponseBody(testFile, total, "5");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        body.writeTo(out);

        String content = out.toString(StandardCharsets.UTF_8);
        assertFalse(content.startsWith("AAAA"));
        assertTrue(content.contains("BBBB"));
    }

    @Test
    void getFileStreamingResponseBody_handlesNullStart() throws IOException {
        File testFile = createTestFile("content\n");
        long total = testFile.length();

        StreamingResponseBody body = FileReader.getFileStreamingResponseBody(testFile, total, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        body.writeTo(out);

        assertTrue(out.toString(StandardCharsets.UTF_8).contains("content"));
    }

    @Test
    void getFileStreamingResponseBody_handlesInvalidStart() throws IOException {
        File testFile = createTestFile("data\n");
        long total = testFile.length();

        // Invalid start string should default to 0
        StreamingResponseBody body = FileReader.getFileStreamingResponseBody(testFile, total, "not-a-number");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        body.writeTo(out);

        assertTrue(out.toString(StandardCharsets.UTF_8).contains("data"));
    }

    @Test
    void getFileStreamingResponseBody_negativeStart_readsLastNLines() throws IOException {
        File testFile = createTestFile("line1\nline2\nline3\nline4\nline5\n");
        long total = testFile.length();

        FileReader.FileStream stream = FileReader.getFileStream(testFile, total, "-2");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.body().writeTo(out);

        String content = out.toString(StandardCharsets.UTF_8);
        assertEquals("line4\nline5\n", content);
        assertEquals(total, stream.totalLength());
        assertEquals("line1\nline2\nline3\n".getBytes(StandardCharsets.UTF_8).length,
                stream.startOffset());
    }

    @Test
    void getFileStream_usesByteOffsetsForUtf8Content() throws IOException {
        File testFile = createTestFile("alpha\ncafé\n最後\n");
        long total = testFile.length();

        FileReader.FileStream stream = FileReader.getFileStream(testFile, total, "-1");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.body().writeTo(out);

        assertEquals("最後\n", out.toString(StandardCharsets.UTF_8));
        assertEquals("alpha\ncafé\n".getBytes(StandardCharsets.UTF_8).length,
                stream.startOffset());
    }

    @Test
    void getFileStream_returnsEmptySliceWhenOffsetPastEof() throws IOException {
        File testFile = createTestFile("new content\n");
        long total = testFile.length();

        FileReader.FileStream stream = FileReader.getFileStream(testFile, total, "500");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.body().writeTo(out);

        assertEquals(total, stream.startOffset());
        assertEquals("", out.toString(StandardCharsets.UTF_8));
    }

    private File createTestFile(String content) throws IOException {
        Path file = tempDir.resolve("test.log");
        Files.writeString(file, content);
        return file.toFile();
    }
}
