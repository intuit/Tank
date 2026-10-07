/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.vm.common.TankConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * The build version and timestamp of the deployed controller, read from the war's manifest.
 *
 * <p>The version is {@link TankConstants#TANK_BUILD_VERSION} followed by a build number: the minutes
 * between 2013-01-15 and the manifest's {@code Implementation-Build-timestamp}.</p>
 *
 * @param version   the build version
 * @param buildDate the build timestamp, or null when the manifest has none
 */
public record BuildInfo(String version, Date buildDate) {

    private static final Logger LOG = LogManager.getLogger(BuildInfo.class);
    static final String TIMESTAMP_ATTRIBUTE = "Implementation-Build-timestamp";
    private static final String BASE_DATE = "2013-01-15T00:00:00Z";
    private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";

    /**
     * @param manifest the war or jar manifest, or null when it could not be read
     */
    public static BuildInfo fromManifest(Manifest manifest) {
        String version = TankConstants.TANK_BUILD_VERSION;
        Attributes attributes = manifest != null ? manifest.getMainAttributes() : null;
        String timestamp = attributes != null ? attributes.getValue(TIMESTAMP_ATTRIBUTE) : null;
        if (timestamp == null) {
            return new BuildInfo(version, null);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        try {
            Date buildDate = sdf.parse(timestamp);
            int buildNum = (int) ((buildDate.getTime() - sdf.parse(BASE_DATE).getTime()) / 60000);
            return new BuildInfo(version + "-" + buildNum, buildDate);
        } catch (ParseException e) {
            LOG.error("Error parsing date {}: {}", timestamp, e, e);
            return new BuildInfo(version + "-" + timestamp, null);
        }
    }

    /**
     * Reads the manifest from the given stream, falling back to the one on this class's classpath.
     *
     * @param warManifest the war's {@code /META-INF/MANIFEST.MF}, or null when unavailable
     */
    public static BuildInfo read(InputStream warManifest) {
        Manifest manifest = null;
        try (InputStream in = warManifest) {
            if (in != null) {
                manifest = new Manifest(in);
            }
        } catch (IOException e) {
            LOG.error("Error reading Manifest from war: {}", e, e);
        }
        if (manifest == null) {
            try (InputStream in = BuildInfo.class.getResourceAsStream(MANIFEST_PATH)) {
                if (in != null) {
                    manifest = new Manifest(in);
                }
            } catch (IOException e) {
                LOG.error("Error reading Manifest from jar: {}", e, e);
            }
        }
        return fromManifest(manifest);
    }
}
