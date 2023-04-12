/**
 *  Copyright 2015-2023 Intuit Inc.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.ServletContext;
import java.io.File;

@Service
public class LogServiceV2Impl implements LogServiceV2 {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LogManager.getLogger(LogServiceV2Impl.class);

    @Override
    public StreamingResponseBody getFile(String filePath, String start) {
        StreamingResponseBody streamingResponse;
        start = start == null ? "0" : start;
        try {
            if (filePath.contains("..") || filePath.startsWith("/")) {
                LOGGER.error("Error returning file: incorrect file path");
                throw new GenericServiceBadRequestException("report", "file path");
            } else {
                String rootDir = "logs";
                final File f = new File(rootDir, filePath);
                if (!f.exists()) {
                    LOGGER.error("Error returning file: file does not exist");
                    throw new GenericServiceResourceNotFoundException("report", "file", null);
                } else if (!f.isFile()) {
                    LOGGER.error("Error returning file: not a file");
                    throw new GenericServiceBadRequestException("report", "file path");
                } else if (!f.canRead()) {
                    LOGGER.error("Error returning file: user not authorized to access file");
                    throw new GenericServiceForbiddenAccessException("report", "file");
                } else {
                    long total = f.length();
                    streamingResponse = FileReader.getFileStreamingResponseBody(f, total, start);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error returning file: file could not be found");
            throw new GenericServiceResourceNotFoundException("report", "file", null);
        }
        return streamingResponse;
    }
}
