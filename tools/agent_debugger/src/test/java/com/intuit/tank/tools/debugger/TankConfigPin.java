package com.intuit.tank.tools.debugger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.intuit.tank.vm.settings.TankConfig;

/**
 * TankConfig reads the settings.xml of the first working dir it sees for the rest of the JVM, and disposing a frame
 * (or quitting a headless run) deletes that run's working dir. Surefire reuses its forked JVMs, so whichever test first
 * loaded TankConfig decided whether every later test in that JVM could still read its settings.
 * <p>
 * Registered for every test class through META-INF/services and junit-platform.properties, this points TankConfig at a
 * working dir no frame or run owns, once per JVM and before any test class runs. That dir, and the working dirs of
 * frames tests leave behind (see {@link #deleteAtExit(File)}), are deleted when the JVM exits.
 */
public class TankConfigPin implements BeforeAllCallback {

    private static final List<File> dirsToDelete = new ArrayList<>();
    private static boolean pinned;

    @Override
    public void beforeAll(ExtensionContext context) throws IOException {
        pin();
    }

    public static synchronized void pin() throws IOException {
        if (pinned) {
            return;
        }
        File dir = PanelBuilder.createWorkingDir(null, null);
        new TankConfig(); // loads TankConfig, fixing its settings path to this dir
        Runtime.getRuntime().addShutdownHook(new Thread(TankConfigPin::deleteDirs));
        pinned = true;
        deleteAtExit(dir);
    }

    /**
     * Deletes a working dir when the JVM exits. Only quitting a frame deletes its working dir, and tests dispose frames
     * instead (quitting a standalone frame exits the JVM), so they register the frame's dir here.
     */
    public static synchronized void deleteAtExit(File dir) {
        dirsToDelete.add(dir);
    }

    /**
     * Registers the working dir of the frame or headless run just built, which each names in WATS_PROPERTIES as it is
     * built.
     */
    public static void deleteCurrentWorkingDirAtExit() {
        deleteAtExit(new File(System.getProperty("WATS_PROPERTIES")));
    }

    private static synchronized void deleteDirs() {
        dirsToDelete.forEach(FileUtils::deleteQuietly);
    }
}
