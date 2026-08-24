/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

    /**
     * Resolves the Tank/Tomcat logs directory across local and packaged deployments.
     * Prefer {@code ${catalina.base}/logs}, then {@code ${catalina.base}/bin/logs}
     * (Log4j relative {@code logs/} when Tomcat's CWD is {@code bin/}), then common
     * install paths, then a relative {@code logs/}.
     */
    public final class LogDirectory {

        private LogDirectory() {
        }

        public static List<File> candidateRoots() {
            Set<File> roots = new LinkedHashSet<>();
            String catalinaBase = System.getProperty("catalina.base");
            if (catalinaBase != null && !catalinaBase.isBlank()) {
                roots.add(new File(catalinaBase, "logs"));
                // Tank Log4j writes logs/tank.log relative to the process CWD; starting
                // Tomcat from bin/ lands app logs here rather than catalina.base/logs.
                roots.add(new File(catalinaBase, "bin/logs"));
            }
            roots.add(new File("/opt/tomcat/logs"));
            roots.add(new File("/opt/tomcat/bin/logs"));
            roots.add(new File("logs"));
            List<File> existing = new ArrayList<>();
            for (File root : roots) {
                if (root.isDirectory()) {
                    existing.add(root.getAbsoluteFile());
                }
            }
            if (existing.isEmpty()) {
                existing.add(new File("logs").getAbsoluteFile());
            }
            return existing;
        }

    public static File primaryRoot() {
        return candidateRoots().get(0);
    }

    public static File findFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        for (File root : candidateRoots()) {
            File candidate = new File(root, fileName);
            if (candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }
}
