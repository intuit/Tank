package com.intuit.tank.util;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.intuit.tank.wrapper.FileInputStreamWrapper;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.junit.jupiter.api.Test;
import org.primefaces.model.file.UploadedFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link UploadedFileIterator}.
 */
public class UploadedFileIteratorTest {

    // -----------------------------------------------------------------------
    // Non-zip: valid extension
    // -----------------------------------------------------------------------

    @Test
    public void testNonZip_ValidExtension_GetNextReturnsWrapper() throws Exception {
        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("test.csv");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("a,b,c".getBytes()));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        FileInputStreamWrapper wrapper = iterator.getNext();

        assertNotNull(wrapper);
        assertEquals("test.csv", wrapper.getFileName());
        assertNotNull(wrapper.getInputStream());
    }

    @Test
    public void testNonZip_ValidExtension_SecondGetNextReturnsNull() throws Exception {
        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("test.csv");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("a,b,c".getBytes()));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        iterator.getNext(); // consume the one item
        FileInputStreamWrapper second = iterator.getNext();

        assertNull(second);
    }

    // -----------------------------------------------------------------------
    // Non-zip: invalid extension
    // -----------------------------------------------------------------------

    @Test
    public void testNonZip_InvalidExtension_GetNextReturnsNull() throws Exception {
        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("test.bin");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("binary".getBytes()));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv", "txt");
        FileInputStreamWrapper wrapper = iterator.getNext();

        assertNull(wrapper);
    }

    // -----------------------------------------------------------------------
    // Non-zip: case-insensitive extension matching
    // -----------------------------------------------------------------------

    @Test
    public void testNonZip_UpperCaseExtension_IsValidCaseInsensitive() throws Exception {
        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("DATA.CSV");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("x,y".getBytes()));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        FileInputStreamWrapper wrapper = iterator.getNext();

        assertNotNull(wrapper);
    }

    // -----------------------------------------------------------------------
    // Zip: valid entry inside zip
    // -----------------------------------------------------------------------

    @Test
    public void testZip_ValidEntry_GetNextReturnsWrapper() throws Exception {
        byte[] zipBytes = buildZip("data.csv", "a,b,c".getBytes());

        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("archive.zip");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        FileInputStreamWrapper wrapper = iterator.getNext();

        assertNotNull(wrapper);
        assertEquals("data.csv", wrapper.getFileName());
    }

    @Test
    public void testZip_ValidEntry_SecondGetNextReturnsNull() throws Exception {
        byte[] zipBytes = buildZip("data.csv", "a,b,c".getBytes());

        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("archive.zip");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        iterator.getNext(); // consume the one valid entry
        FileInputStreamWrapper second = iterator.getNext();

        assertNull(second);
    }

    @Test
    public void testZip_InvalidEntryExtension_GetNextReturnsNull() throws Exception {
        byte[] zipBytes = buildZip("data.bin", "binary".getBytes());

        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("archive.zip");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        FileInputStreamWrapper wrapper = iterator.getNext();

        assertNull(wrapper);
    }

    @Test
    public void testZip_EntryStartingWithUnderscore_IsSkipped() throws Exception {
        byte[] zipBytes = buildZip("_hidden.csv", "a,b".getBytes());

        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("archive.zip");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        FileInputStreamWrapper wrapper = iterator.getNext();

        assertNull(wrapper);
    }

    @Test
    public void testZip_EntryStartingWithDot_IsSkipped() throws Exception {
        byte[] zipBytes = buildZip(".hidden.csv", "a,b".getBytes());

        UploadedFile mockFile = mock(UploadedFile.class);
        when(mockFile.getFileName()).thenReturn("archive.zip");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(zipBytes));

        UploadedFileIterator iterator = new UploadedFileIterator(mockFile, "csv");
        FileInputStreamWrapper wrapper = iterator.getNext();

        assertNull(wrapper);
    }

    // -----------------------------------------------------------------------
    // MyInputStream inner class
    // -----------------------------------------------------------------------

    @Test
    public void testMyInputStream_ForceClose_DoesNotThrow() throws Exception {
        byte[] zipBytes = buildZip("data.csv", "a,b,c".getBytes());
        ArchiveInputStream archiveStream =
                new ArchiveStreamFactory().createArchiveInputStream("zip", new ByteArrayInputStream(zipBytes));
        UploadedFileIterator.MyInputStream myStream = new UploadedFileIterator.MyInputStream(archiveStream);

        assertDoesNotThrow(myStream::forceClose);
    }

    @Test
    public void testMyInputStream_Close_DoesNotThrow() throws Exception {
        byte[] zipBytes = buildZip("data.csv", "a,b,c".getBytes());
        ArchiveInputStream archiveStream =
                new ArchiveStreamFactory().createArchiveInputStream("zip", new ByteArrayInputStream(zipBytes));
        UploadedFileIterator.MyInputStream myStream = new UploadedFileIterator.MyInputStream(archiveStream);

        // close() is a no-op in the implementation
        assertDoesNotThrow(myStream::close);
    }

    @Test
    public void testMyInputStream_GetNextEntry_ReturnsFirstEntry() throws Exception {
        byte[] zipBytes = buildZip("data.csv", "a,b,c".getBytes());
        ArchiveInputStream archiveStream =
                new ArchiveStreamFactory().createArchiveInputStream("zip", new ByteArrayInputStream(zipBytes));
        UploadedFileIterator.MyInputStream myStream = new UploadedFileIterator.MyInputStream(archiveStream);

        assertDoesNotThrow(() -> {
            var entry = myStream.getNextEntry();
            assertNotNull(entry);
        });
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private static byte[] buildZip(String entryName, byte[] content) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(bos)) {
            zos.putNextEntry(new ZipEntry(entryName));
            zos.write(content);
            zos.closeEntry();
        }
        return bos.toByteArray();
    }
}
