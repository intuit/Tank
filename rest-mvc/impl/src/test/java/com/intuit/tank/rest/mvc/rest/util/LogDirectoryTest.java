package com.intuit.tank.rest.mvc.rest.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LogDirectoryTest {

    @TempDir
    Path tempDir;

    private String previousCatalinaBase;

    @AfterEach
    void restoreCatalinaBase() {
        if (previousCatalinaBase == null) {
            System.clearProperty("catalina.base");
        } else {
            System.setProperty("catalina.base", previousCatalinaBase);
        }
    }

    @Test
    void findFile_prefersCatalinaBaseLogs() throws IOException {
        previousCatalinaBase = System.getProperty("catalina.base");
        Path catalina = tempDir.resolve("catalina");
        Path logs = catalina.resolve("logs");
        Files.createDirectories(logs);
        Path target = logs.resolve("preferred.log");
        Files.writeString(target, "from-catalina\n");
        System.setProperty("catalina.base", catalina.toString());

        File found = LogDirectory.findFile("preferred.log");
        assertNotNull(found);
        assertEquals(target.toAbsolutePath().normalize(), found.toPath().toAbsolutePath().normalize());
    }

    @Test
    void findFile_readsTankLogFromCatalinaBinLogs() throws IOException {
        previousCatalinaBase = System.getProperty("catalina.base");
        Path catalina = tempDir.resolve("catalina");
        Files.createDirectories(catalina.resolve("logs"));
        Path binLogs = catalina.resolve("bin").resolve("logs");
        Files.createDirectories(binLogs);
        Path tankLog = binLogs.resolve("tank.log");
        Files.writeString(tankLog, "tank-app\n");
        System.setProperty("catalina.base", catalina.toString());

        assertTrue(LogDirectory.candidateRoots().stream()
                .anyMatch(root -> root.toPath().endsWith(Path.of("bin", "logs"))));
        File found = LogDirectory.findFile("tank.log");
        assertNotNull(found);
        assertEquals(tankLog.toAbsolutePath().normalize(), found.toPath().toAbsolutePath().normalize());
    }

    @Test
    void candidateRoots_includesRelativeFallback() {
        previousCatalinaBase = System.getProperty("catalina.base");
        System.clearProperty("catalina.base");
        assertFalse(LogDirectory.candidateRoots().isEmpty());
        assertTrue(LogDirectory.candidateRoots().stream()
                .anyMatch(root -> root.getAbsolutePath().endsWith("logs")));
    }
}
