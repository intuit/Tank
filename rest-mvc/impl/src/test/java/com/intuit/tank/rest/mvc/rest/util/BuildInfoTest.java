/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.vm.common.TankConstants;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.*;

class BuildInfoTest {

    private static Manifest manifest(String timestamp) throws Exception {
        String text = "Manifest-Version: 1.0\n" + (timestamp != null ? BuildInfo.TIMESTAMP_ATTRIBUTE + ": " + timestamp + "\n" : "");
        return new Manifest(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void versionIncludesMinutesSinceBaseDate() throws Exception {
        BuildInfo info = BuildInfo.fromManifest(manifest("2013-01-16T00:00:00Z"));
        assertEquals(TankConstants.TANK_BUILD_VERSION + "-1440", info.version());
        assertEquals(Instant.parse("2013-01-16T00:00:00Z"), info.buildDate().toInstant());
    }

    @Test
    void noTimestamp_plainVersion() throws Exception {
        assertEquals(new BuildInfo(TankConstants.TANK_BUILD_VERSION, null), BuildInfo.fromManifest(manifest(null)));
        assertEquals(new BuildInfo(TankConstants.TANK_BUILD_VERSION, null), BuildInfo.fromManifest(null));
    }

    @Test
    void unparseableTimestamp_appendedVerbatim() throws Exception {
        BuildInfo info = BuildInfo.fromManifest(manifest("yesterday"));
        assertEquals(TankConstants.TANK_BUILD_VERSION + "-yesterday", info.version());
        assertNull(info.buildDate());
    }

    @Test
    void read_usesGivenStream() {
        String text = "Manifest-Version: 1.0\n" + BuildInfo.TIMESTAMP_ATTRIBUTE + ": 2013-01-15T01:00:00Z\n";
        BuildInfo info = BuildInfo.read(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
        assertEquals(TankConstants.TANK_BUILD_VERSION + "-60", info.version());
    }
}
