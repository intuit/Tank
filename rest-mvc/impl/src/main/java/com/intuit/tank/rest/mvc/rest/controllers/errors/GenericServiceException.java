/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers.errors;

/**
 * Base exception for all services
 *
 */
public class GenericServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs exception without message or cause.
     */
    public GenericServiceException() {
        super();
    }

    /**
     * Construct with a message {@code String} that is returned by the inherited
     * {@code Throwable.getMessage}.
     *
     * @param message
     *            the message that is returned by the inherited
     *            {@code Throwable.getMessage}
     */
    public GenericServiceException(String message) {
        super(message);
    }

    /**
     * Construct with a {@code Throwable} cause that is returned by the
     * inherited {@code Throwable.getCause}. The {@code Throwable.getMessage}
     * will display the output from {@code toString} called on the {@code cause}
     * .
     *
     * @param cause
     *            the cause that is returned by the inherited
     *            {@code Throwable.getCause}
     */
    public GenericServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct with both a {@code String} message and a {@code Throwable}
     * cause. The {@code message} is returned by the inherited
     * {@code Throwable.getMessage}. The cause that is returned by the inherited
     * {@code Throwable.getCause}.
     *
     * @param message
     *            the message that is returned by the inherited
     *            {@code Throwable.getMessage}
     * @param cause
     *            the cause that is returned by the inherited
     *            {@code Throwable.getCause}
     */
    public GenericServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
