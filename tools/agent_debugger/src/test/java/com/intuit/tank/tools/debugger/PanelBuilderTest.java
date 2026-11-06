package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PanelBuilderTest {

    private File workingDir;
    private String watsProperties;

    @BeforeEach
    public void rememberTheSettingsDir() {
        watsProperties = System.getProperty("WATS_PROPERTIES");
    }

    /**
     * Other tests in this JVM may still read the settings dir, so it's put back and only deleted when the JVM exits
     */
    @AfterEach
    public void cleanup() throws Exception {
        if (watsProperties != null) {
            System.setProperty("WATS_PROPERTIES", watsProperties);
        }
        if (workingDir != null) {
            FileUtils.forceDeleteOnExit(workingDir);
        }
    }

    @Test
    public void workingDirGetsSettingsForTheHarness() throws Exception {
        // without a service url there are no controller headers to fetch
        workingDir = PanelBuilder.createWorkingDir(null, null);
        TankConfigPin.deleteAtExit(workingDir);

        assertTrue(workingDir.isDirectory());
        assertEquals(workingDir.getAbsolutePath(), System.getProperty("WATS_PROPERTIES"));
        String settings = Files.readString(new File(workingDir, "settings.xml").toPath(), StandardCharsets.UTF_8);
        assertTrue(settings.contains("<agent-data-file-storage>" + workingDir.getAbsolutePath() + "</agent-data-file-storage>"));
        assertTrue(settings.contains("<insert-before><![CDATA["));
        assertFalse(settings.contains("<request-headers>"));
    }

    @Test
    public void updatingTheServiceUrlRewritesTheSettings() throws Exception {
        workingDir = PanelBuilder.createWorkingDir(null, null);
        TankConfigPin.deleteAtExit(workingDir);
        File settings = new File(workingDir, "settings.xml");
        Files.writeString(settings.toPath(), "stale");

        PanelBuilder.updateServiceUrl("", "token");

        assertTrue(Files.readString(settings.toPath(), StandardCharsets.UTF_8).startsWith("<?xml"));
    }
}
