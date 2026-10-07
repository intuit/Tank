/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class UploadedArchiveTest {

    private static InputStream zip(Map<String, String> entries) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(bytes)) {
            for (Map.Entry<String, String> e : entries.entrySet()) {
                out.putNextEntry(new ZipEntry(e.getKey()));
                if (e.getValue() != null) {
                    out.write(e.getValue().getBytes(StandardCharsets.UTF_8));
                }
                out.closeEntry();
            }
        }
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    private static List<String> readAll(UploadedArchive archive) throws IOException {
        List<String> read = new ArrayList<>();
        for (UploadedArchive.UploadedFile f = archive.next(); f != null; f = archive.next()) {
            try (InputStream in = f.content()) {
                read.add(f.name() + "=" + IOUtils.toString(in, StandardCharsets.UTF_8));
            }
        }
        return read;
    }

    @Test
    void singleAcceptedFile() throws IOException {
        try (UploadedArchive archive = new UploadedArchive("dir/users.CSV",
                new ByteArrayInputStream("a,b".getBytes(StandardCharsets.UTF_8)), "csv")) {
            assertEquals(List.of("users.CSV=a,b"), readAll(archive));
            assertEquals(List.of(), archive.skipped());
        }
    }

    @Test
    void singleRejectedFileIsSkipped() throws IOException {
        try (UploadedArchive archive = new UploadedArchive("notes.pdf", new ByteArrayInputStream(new byte[0]), "csv", "txt")) {
            assertNull(archive.next());
            assertEquals(List.of("notes.pdf"), archive.skipped());
        }
    }

    @Test
    void zipEntriesAreFilteredAndFoldersDropped() throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("data/users.csv", "u1");
        entries.put("__MACOSX/data/._users.csv", "junk");
        entries.put(".DS_Store", "junk");
        entries.put("data/.hidden.csv", "junk");
        entries.put("data/readme.md", "docs");
        entries.put("data/", null);
        entries.put("ids.txt", "1\n2");
        try (UploadedArchive archive = new UploadedArchive("bundle.ZIP", zip(entries), "csv", "txt")) {
            assertEquals(List.of("users.csv=u1", "ids.txt=1\n2"), readAll(archive),
                    "closing an entry's stream must not close the archive");
            assertEquals(List.of("__MACOSX/data/._users.csv", ".DS_Store", "data/.hidden.csv", "data/readme.md"),
                    archive.skipped());
        }
    }

    @Test
    void tooManyEntries() throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        for (int i = 0; i <= UploadedArchive.MAX_ENTRIES; i++) {
            entries.put("f" + i + ".md", "");
        }
        try (UploadedArchive archive = new UploadedArchive("big.zip", zip(entries), "csv")) {
            assertThrows(IOException.class, archive::next);
        }
    }

    @Test
    void isZip() {
        assertTrue(UploadedArchive.isZip("A.Zip"));
        assertFalse(UploadedArchive.isZip("a.csv"));
        assertFalse(UploadedArchive.isZip(null));
    }
}
