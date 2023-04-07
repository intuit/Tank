/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.report;

import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface ReportServiceV2 {

    /**
     * Returns a specified log file
     *
     * @param filePath
     *       file path
     *
     * @param start
     *        starting line number
     *
     * @throws GenericServiceResourceNotFoundException
     *         if the file does not exist in logs
     *
     * @throws GenericServiceBadRequestException
     *         if file path is not a file
     *
     * @throws GenericServiceForbiddenAccessException
     *         if user not authorized to access file
     *
     * @return streaming output of log file
     */
    public StreamingResponseBody getFile(String filePath, String start);

}
