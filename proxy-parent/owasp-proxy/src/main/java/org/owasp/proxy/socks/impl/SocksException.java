/*
 * This file is part of the OWASP Proxy, a free intercepting proxy library.
 * Copyright (C) 2008-2010 Rogan Dawes <rogan@dawes.za.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to:
 * The Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.owasp.proxy.socks.impl;

/**
 * Exception thrown by various socks classes to indicate errors with protocol or unsuccessful server responses.
 */
public class SocksException extends java.io.IOException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a SocksException with given errorcode.
     * <p>
     * Tries to look up message which corresponds to this error code.
     * 
     * @param errCode
     *            Error code for this exception.
     */
    public SocksException(int errCode) {
        this.errCode = errCode;
        if ((errCode >> 16) == 0) {
            // Server reply error message
            errString = errCode <= serverReplyMessage.length ? serverReplyMessage[errCode]
                    : UNASSIGNED_ERROR_MESSAGE;
        } else {
            // Local error
            errCode = (errCode >> 16) - 1;
            errString = errCode <= localErrorMessage.length ? localErrorMessage[errCode]
                    : UNASSIGNED_ERROR_MESSAGE;
        }
    }

    /**
     * Constructs a SocksException with given error code and message.
     * 
     * @param errCode
     *            Error code.
     * @param errString
     *            Error Message.
     */
    public SocksException(int errCode, String errString) {
        this.errCode = errCode;
        this.errString = errString;
    }

    /**
     * Get the error code associated with this exception.
     * 
     * @return Error code associated with this exception.
     */
    public int getErrorCode() {
        return errCode;
    }

    /**
     * Get human readable representation of this exception.
     * 
     * @return String represntation of this exception.
     */
    public String toString() {
        return errString;
    }

    static final String UNASSIGNED_ERROR_MESSAGE = "Unknown error message";

    static final String serverReplyMessage[] = { "Succeeded",
            "General SOCKS server failure",
            "Connection not allowed by ruleset", "Network unreachable",
            "Host unreachable", "Connection refused", "TTL expired",
            "Command not supported", "Address type not supported" };

    static final String localErrorMessage[] = { "SOCKS server not specified",
            "Unable to contact SOCKS server", "IO error",
            "None of Authentication methods are supported",
            "Authentication failed", "General SOCKS fault" };

    String errString;

    int errCode;

}// End of SocksException class

