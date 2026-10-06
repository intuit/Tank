package com.intuit.tank.rest.mvc.rest.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    void listFileNames_tankLogsFirstWithoutDuplicates() throws IOException {
        previousCatalinaBase = System.getProperty("catalina.base");
        Path catalina = tempDir.resolve("catalina");
        Path logs = Files.createDirectories(catalina.resolve("logs"));
        Path binLogs = Files.createDirectories(catalina.resolve("bin").resolve("logs"));
        Files.writeString(logs.resolve("zz-access-test.txt"), "");
        Files.writeString(logs.resolve("Tank-test-agent.log"), "");
        Files.createDirectories(logs.resolve("archive-test"));
        Files.writeString(binLogs.resolve("tank.log"), "");
        Files.writeString(binLogs.resolve("aa-catalina-test.out"), "");
        Files.writeString(binLogs.resolve("zz-access-test.txt"), "");
        System.setProperty("catalina.base", catalina.toString());

        // other roots (such as a relative logs/) may hold files too; check only the ones made here
        List<String> mine = List.of("tank.log", "Tank-test-agent.log", "aa-catalina-test.out", "zz-access-test.txt");
        List<String> listed = LogDirectory.listFileNames().stream().filter(mine::contains).toList();
        assertEquals(mine, listed);
        assertFalse(LogDirectory.listFileNames().contains("archive-test"));
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
