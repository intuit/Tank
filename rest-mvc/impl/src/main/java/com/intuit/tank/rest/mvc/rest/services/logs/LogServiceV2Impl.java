/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.logs;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.util.FileReader;
import com.intuit.tank.rest.mvc.rest.util.LogDirectory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class LogServiceV2Impl implements LogServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(LogServiceV2Impl.class);

    @Override
    public LogFileResponse getFile(String filePath, String start) {
        start = start == null ? "0" : start;
        try {
            if (filePath == null || filePath.contains("..") || filePath.startsWith("/") || filePath.contains("\\")) {
                LOGGER.error("Error returning file: incorrect file path");
                throw new GenericServiceBadRequestException("logs", "file path", "incorrect file path");
            }

            final File f = LogDirectory.findFile(filePath);
            if (f == null || !f.exists()) {
                LOGGER.error("Error returning file: file does not exist in {}", LogDirectory.candidateRoots());
                throw new GenericServiceResourceNotFoundException("logs", "file", null);
            }
            if (!f.isFile()) {
                LOGGER.error("Error returning file: not a file");
                throw new GenericServiceBadRequestException("logs", "file path", "not a file");
            }
            if (!f.canRead()) {
                LOGGER.error("Error returning file: user not authorized to access file");
                throw new GenericServiceForbiddenAccessException("logs", "file");
            }

            long total = f.length();
            FileReader.FileStream stream = FileReader.getFileStream(f, total, start);
            return new LogFileResponse(stream.body(), stream.totalLength(), stream.startOffset());
        } catch (GenericServiceBadRequestException | GenericServiceResourceNotFoundException
                | GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error returning file: file could not be found", e);
            throw new GenericServiceResourceNotFoundException("logs", "file", null);
        }
    }
}
