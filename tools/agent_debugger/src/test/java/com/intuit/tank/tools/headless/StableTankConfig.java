package com.intuit.tank.tools.headless;

import java.io.IOException;

import com.intuit.tank.tools.debugger.TankConfigPin;

/**
 * TankConfig fixes its settings.xml location the first time the class loads, from WATS_PROPERTIES. If that happens
 * while a HeadlessDebuggerSetup's working dir is current, the setup's quit() deletes the file and every later
 * harness in the JVM fails to start. {@link TankConfigPin} points it at a dir that lasts until the JVM exits, normally
 * before any test class runs; calling this makes sure of it.
 */
final class StableTankConfig {

    private StableTankConfig() {
    }

    static void load() throws IOException {
        TankConfigPin.pin();
    }
}
