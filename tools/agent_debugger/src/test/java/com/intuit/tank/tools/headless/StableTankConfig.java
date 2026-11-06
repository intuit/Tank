package com.intuit.tank.tools.headless;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.intuit.tank.vm.settings.TankConfig;

/**
 * TankConfig fixes its settings.xml location the first time the class loads, from WATS_PROPERTIES. If that happens
 * while a HeadlessDebuggerSetup's working dir is current, the setup's quit() deletes the file and every later
 * harness in the JVM fails to start. Loading TankConfig here first points it at a dir that lasts until the JVM exits.
 */
final class StableTankConfig {

    private static boolean loaded;

    private StableTankConfig() {
    }

    static synchronized void load() throws IOException {
        if (loaded) {
            return;
        }
        File dir = Files.createTempDirectory("tank-config").toFile();
        dir.deleteOnExit();
        File settings = new File(dir, "settings.xml");
        Files.writeString(settings.toPath(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<tank-settings><agent-config></agent-config></tank-settings>\n");
        settings.deleteOnExit();
        String previous = System.getProperty("WATS_PROPERTIES");
        System.setProperty("WATS_PROPERTIES", dir.getAbsolutePath());
        try {
            new TankConfig();
        } finally {
            if (previous != null) {
                System.setProperty("WATS_PROPERTIES", previous);
            }
        }
        loaded = true;
    }
}
