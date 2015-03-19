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

package org.owasp.proxy.http.dao;

import java.io.InputStream;
import java.util.Collection;

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.BufferedResponse;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.ResponseHeader;
import org.springframework.dao.DataAccessException;

public interface MessageDAO {

    void saveRequest(MutableBufferedRequest request) throws DataAccessException;

    void saveRequestHeader(MutableRequestHeader requestHeader, int contentId)
            throws DataAccessException;

    void saveResponse(MutableBufferedResponse response)
            throws DataAccessException;

    void saveResponseHeader(MutableResponseHeader responseHeader, int contentId)
            throws DataAccessException;

    int saveMessageContent(InputStream messageContent)
            throws DataAccessException;

    int saveMessageContent(byte[] messageContent) throws DataAccessException;

    BufferedRequest loadRequest(int id) throws DataAccessException;

    RequestHeader loadRequestHeader(int id) throws DataAccessException;

    BufferedResponse loadResponse(int id) throws DataAccessException;

    ResponseHeader loadResponseHeader(int id) throws DataAccessException;

    byte[] loadMessageContent(int id) throws DataAccessException;

    int getMessageContentSize(int id) throws DataAccessException;

    int getMessageContentId(int headerId) throws DataAccessException;

    int saveConversation(int requestId, int responseId)
            throws DataAccessException;

    Conversation getConversation(int id) throws DataAccessException;

    Collection<Integer> listConversations() throws DataAccessException;

    Collection<Integer> listConversationsSince(int conversationId)
            throws DataAccessException;

    boolean deleteConversation(int id) throws DataAccessException;

    ConversationSummary getConversationSummary(int id)
            throws DataAccessException;

}
