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
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableBufferedRequest;
import org.owasp.proxy.http.MutableBufferedResponse;
import org.owasp.proxy.http.MutableMessageHeader;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.io.CountingInputStream;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class JdbcMessageDAO extends NamedParameterJdbcDaoSupport implements
        MessageDAO {

    private static final String SSL = "ssl";
    private static final String PORT = "port";
    private static final String HOST = "host";
    private static final String ID = "id";
    private static final String HEADER = "header";
    private static final String SIZE = "size";
    private static final String CONTENT = "content";
    private static final String CONTENTID = "contentId";
    private static final String REQUESTID = "requestId";
    private static final String RESPONSEID = "responseId";
    private static final String REQUEST_SUBMISSION_TIME = "submissionTime";
    private static final String RESPONSE_HEADER_TIME = "headerTime";
    private static final String RESPONSE_CONTENT_TIME = "contentTime";

    private static final ParameterizedRowMapper<MutableBufferedRequest> REQUEST_MAPPER = new RequestMapper();
    private static final ParameterizedRowMapper<MutableBufferedResponse> RESPONSE_MAPPER = new ResponseMapper();
    private static final ParameterizedRowMapper<byte[]> CONTENT_MAPPER = new ContentMapper();
    private static final ParameterizedRowMapper<Integer> ID_MAPPER = new IdMapper();
    private static final ParameterizedRowMapper<Conversation> CONVERSATION_MAPPER = new ConversationMapper();

    private final static String INSERT_CONTENT = "INSERT INTO contents (content, size) VALUES (:content, :size)";

    private final static String UPDATE_CONTENT_SIZE = "UPDATE contents SET size = :size";

    private final static String SELECT_CONTENT = "SELECT content FROM contents WHERE id = :id";

    private final static String SELECT_CONTENT_SIZE = "SELECT size FROM contents WHERE id = :id";

    private final static String INSERT_HEADER = "INSERT INTO headers (header, contentId) VALUES (:header, :contentId)";

    private final static String SELECT_CONTENT_ID = "SELECT contentId FROM headers WHERE id = :id";

    private final static String INSERT_REQUEST = "INSERT INTO requests (id, host, port, ssl, submissionTime) VALUES (:id, :host, :port, :ssl, :submissionTime)";

    private final static String SELECT_REQUEST = "SELECT requests.id AS id, host, port, ssl, submissionTime, header FROM requests, headers WHERE requests.id = headers.id AND headers.id = :id";

    private final static String INSERT_RESPONSE = "INSERT INTO responses (id, headerTime, contentTime) VALUES (:id, :headerTime, :contentTime)";

    private final static String SELECT_RESPONSE = "SELECT responses.id AS id, headerTime, contentTime, header FROM responses, headers WHERE responses.id = headers.id AND headers.id = :id";

    private final static String INSERT_CONVERSATION = "INSERT INTO conversations (requestId, responseId) VALUES (:requestId, :responseId)";

    private final static String DELETE_CONVERSATION = "DELETE FROM conversations WHERE id = :id";

    private final static String SELECT_SUMMARY = "SELECT id, requestId, responseId FROM conversations WHERE id = :id";

    private final static String SELECT_CONVERSATIONS = "SELECT id FROM conversations WHERE id > :id";

    private final static String CREATE_CONTENTS_TABLE = "CREATE TABLE contents ("
            + "id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "content LONGVARBINARY NOT NULL," + "size INTEGER NOT NULL)";

    private final static String CREATE_HEADERS_TABLE = "CREATE TABLE headers ("
            + "id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "header LONGVARBINARY NOT NULL,"
            + "contentId INTEGER,"
            + "CONSTRAINT content_fk FOREIGN KEY (contentId) REFERENCES contents(id) ON DELETE CASCADE)";

    private final static String CREATE_RESPONSES_TABLE = "CREATE TABLE responses ("
            + "id INTEGER NOT NULL PRIMARY KEY,"
            + "headerTime TIMESTAMP, "
            + "contentTime TIMESTAMP, "
            + "CONSTRAINT response_header_fk FOREIGN KEY (id) REFERENCES headers(id) ON DELETE CASCADE)";

    private final static String CREATE_REQUESTS_TABLE = "CREATE TABLE requests ("
            + "id INTEGER NOT NULL PRIMARY KEY,"
            + "host VARCHAR(255) NOT NULL,"
            + "port INTEGER NOT NULL,"
            + "ssl BIT NOT NULL,"
            + "submissionTime TIMESTAMP, "
            + "CONSTRAINT request_header_fk FOREIGN KEY (id) REFERENCES headers(id) ON DELETE CASCADE)";

    private final static String CREATE_CONVERSATIONS_TABLE = "CREATE TABLE conversations ("
            + "id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "requestId INTEGER NOT NULL,"
            + "responseId INTEGER NOT NULL,"
            + "CONSTRAINT request_fk FOREIGN KEY (requestId) REFERENCES requests(id) ON DELETE CASCADE,"
            + "CONSTRAINT response_fk FOREIGN KEY (responseId) REFERENCES headers(id) ON DELETE CASCADE)";

    public void createTables() throws DataAccessException {
        JdbcTemplate template = getJdbcTemplate();
        template.execute(CREATE_CONTENTS_TABLE);
        template.execute(CREATE_HEADERS_TABLE);
        template.execute(CREATE_REQUESTS_TABLE);
        template.execute(CREATE_RESPONSES_TABLE);
        template.execute(CREATE_CONVERSATIONS_TABLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#addConversation(int, int)
     */
    public int saveConversation(int requestId, int responseId)
            throws DataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(REQUESTID, requestId, Types.INTEGER);
        params.addValue(RESPONSEID, responseId, Types.INTEGER);

        KeyHolder key = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate()
                .update(INSERT_CONVERSATION, params, key);
        return key.getKey().intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#deleteConversation(int)
     */
    public boolean deleteConversation(int id) throws DataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, id, Types.INTEGER);
        return getNamedParameterJdbcTemplate().update(DELETE_CONVERSATION,
                params) > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#getConversations()
     */
    public Collection<Integer> listConversations() throws DataAccessException {
        return listConversationsSince(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#getConversationsSince(int)
     */
    public Collection<Integer> listConversationsSince(int id)
            throws DataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            params.addValue(ID, id, Types.INTEGER);
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(
                    getNamedParameterJdbcTemplate());
            return template.query(SELECT_CONVERSATIONS, ID_MAPPER, params);
        } catch (EmptyResultDataAccessException erdae) {
            return Collections.emptyList();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#getMessageContentId(int)
     */
    public int getMessageContentId(int headerId) throws DataAccessException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(ID, headerId, Types.INTEGER);
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(
                    getNamedParameterJdbcTemplate());
            return template.queryForInt(SELECT_CONTENT_ID, params);
        } catch (EmptyResultDataAccessException erdae) {
            return -1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#getMessageContentSize(int)
     */
    public int getMessageContentSize(int id) throws DataAccessException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(ID, id, Types.INTEGER);
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(
                    getNamedParameterJdbcTemplate());
            return template.queryForInt(SELECT_CONTENT_SIZE, params);
        } catch (EmptyResultDataAccessException erdae) {
            return -1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#getRequestId(int)
     */
    public Conversation getConversation(int id) throws DataAccessException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(ID, id, Types.INTEGER);
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(
                    getNamedParameterJdbcTemplate());
            return template.queryForObject(SELECT_SUMMARY, CONVERSATION_MAPPER,
                    params);
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#getConversationSummary(int)
     */
    public ConversationSummary getConversationSummary(int id)
            throws DataAccessException {
        Conversation c = getConversation(id);
        if (c == null)
            return null;
        ConversationSummary cs = new ConversationSummary();
        cs.setId(id);
        RequestHeader rqh = loadRequestHeader(c.getRequestId());
        try {
            cs.summarizeRequest(rqh, getMessageContentSize(c.getRequestId()));
        } catch (MessageFormatException ignored) {
            // Return an empty request summary if it cannot be parsed
            // The detailed request is still available
        }
        MutableResponseHeader rph = loadResponseHeader(c.getResponseId());
        try {
            cs.summarizeResponse(rph, getMessageContentSize(c.getResponseId()));
        } catch (MessageFormatException ignored) {
            // Return an empty response summary if it cannot be parsed
            // The detailed response is still available
        }
        return cs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#loadMessageContent(int)
     */
    public byte[] loadMessageContent(int id) throws DataAccessException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(ID, id, Types.INTEGER);
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(
                    getNamedParameterJdbcTemplate());
            return template.queryForObject(SELECT_CONTENT, CONTENT_MAPPER,
                    params);
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#loadRequest(int)
     */
    public BufferedRequest loadRequest(int id) throws DataAccessException {
        MutableBufferedRequest request = (MutableBufferedRequest) loadRequestHeader(id);
        if (request == null)
            return null;
        int contentId = getMessageContentId(id);
        if (contentId > 0)
            request.setContent(loadMessageContent(contentId));
        return request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#loadRequestHeader(int)
     */
    public RequestHeader loadRequestHeader(int id) throws DataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            params.addValue(ID, id, Types.INTEGER);
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(
                    getNamedParameterJdbcTemplate());
            return template.queryForObject(SELECT_REQUEST, REQUEST_MAPPER,
                    params);
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#loadResponse(int)
     */
    public MutableBufferedResponse loadResponse(int id)
            throws DataAccessException {
        MutableBufferedResponse response = (MutableBufferedResponse) loadResponseHeader(id);
        if (response == null)
            return null;
        int contentId = getMessageContentId(id);
        if (contentId > 0)
            response.setContent(loadMessageContent(contentId));
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#loadResponseHeader(int)
     */
    public MutableResponseHeader loadResponseHeader(int id)
            throws DataAccessException {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(ID, id, Types.INTEGER);
            SimpleJdbcTemplate template = new SimpleJdbcTemplate(
                    getNamedParameterJdbcTemplate());
            return template.queryForObject(SELECT_RESPONSE, RESPONSE_MAPPER,
                    params);
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#saveMessageContent(byte[])
     */
    public int saveMessageContent(byte[] messageContent)
            throws DataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(CONTENT, messageContent, Types.LONGVARBINARY);
        params.addValue(SIZE, messageContent.length, Types.INTEGER);
        KeyHolder key = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(INSERT_CONTENT, params, key);
        return key.getKey().intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#saveMessageContentAsStream(java.io . InputStream)
     */
    public int saveMessageContent(InputStream messageContent)
            throws DataAccessException {
        CountingInputStream cis = new CountingInputStream(messageContent);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(CONTENT, cis, Types.LONGVARBINARY);
        params.addValue(SIZE, 0, Types.INTEGER);
        KeyHolder key = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(INSERT_CONTENT, params, key);
        int id = key.getKey().intValue();

        params = new MapSqlParameterSource();
        params.addValue(SIZE, cis.getCount(), Types.INTEGER);
        getNamedParameterJdbcTemplate().update(UPDATE_CONTENT_SIZE, params);
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#saveRequest(org.owasp.httpclient. Request)
     */
    public void saveRequest(MutableBufferedRequest request)
            throws DataAccessException {
        int contentId = -1;
        if (request.getContent() != null)
            contentId = saveMessageContent(request.getContent());
        saveRequestHeader(request, contentId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#saveRequestHeader(org.owasp.httpclient .RequestHeader, int)
     */
    public void saveRequestHeader(MutableRequestHeader requestHeader,
            int contentId) throws DataAccessException {
        saveMessageHeader(requestHeader, contentId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, requestHeader.getId(), Types.INTEGER);
        params.addValue(HOST, requestHeader.getTarget().getHostName(),
                Types.VARCHAR);
        params.addValue(PORT, requestHeader.getTarget().getPort(),
                Types.INTEGER);
        params.addValue(SSL, requestHeader.isSsl(), Types.BIT);
        addTimestamp(params, REQUEST_SUBMISSION_TIME, requestHeader.getTime());
        getNamedParameterJdbcTemplate().update(INSERT_REQUEST, params);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#saveResponse(org.owasp.httpclient .Response)
     */
    public void saveResponse(MutableBufferedResponse response)
            throws DataAccessException {
        int contentId = -1;
        if (response.getContent() != null)
            contentId = saveMessageContent(response.getContent());
        saveResponseHeader(response, contentId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.httpclient.dao.MessageDAO#saveResponseHeader(org.owasp.httpclient .ResponseHeader, int)
     */
    public void saveResponseHeader(MutableResponseHeader responseHeader,
            int contentId) throws DataAccessException {
        saveMessageHeader(responseHeader, contentId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, responseHeader.getId(), Types.INTEGER);
        addTimestamp(params, RESPONSE_HEADER_TIME, responseHeader
                .getHeaderTime());
        addTimestamp(params, RESPONSE_CONTENT_TIME, responseHeader
                .getContentTime());
        getNamedParameterJdbcTemplate().update(INSERT_RESPONSE, params);
    }

    private void addTimestamp(MapSqlParameterSource params, String name,
            long time) {
        params.addValue(name, time == 0 ? null : new Timestamp(time),
                Types.TIMESTAMP);
    }

    private void saveMessageHeader(MutableMessageHeader header, int contentId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(HEADER, header.getHeader(), Types.LONGVARBINARY);
        params.addValue(CONTENTID, contentId != -1 ? contentId : null,
                Types.INTEGER);
        KeyHolder key = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(INSERT_HEADER, params, key);
        header.setId(key.getKey().intValue());
    }

    private static class RequestMapper implements
            ParameterizedRowMapper<MutableBufferedRequest> {

        public MutableBufferedRequest mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int id = rs.getInt(ID);

            String host = rs.getString(HOST);
            int port = rs.getInt(PORT);
            boolean ssl = rs.getBoolean(SSL);

            MutableBufferedRequest request = new MutableBufferedRequest.Impl();
            request.setId(id);
            request.setTarget(InetSocketAddress.createUnresolved(host, port));
            request.setSsl(ssl);
            request.setHeader(rs.getBytes(HEADER));

            Timestamp t;
            if ((t = rs.getTimestamp(REQUEST_SUBMISSION_TIME)) != null)
                request.setTime(t.getTime());

            return request;
        }

    }

    private static class ResponseMapper implements
            ParameterizedRowMapper<MutableBufferedResponse> {

        public MutableBufferedResponse mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            int id = rs.getInt(ID);

            MutableBufferedResponse response = new MutableBufferedResponse.Impl();
            response.setId(id);
            response.setHeader(rs.getBytes(HEADER));

            Timestamp t;
            long ht = 0, ct = 0;
            if ((t = rs.getTimestamp(RESPONSE_HEADER_TIME)) != null)
                ht = t.getTime();
            if ((t = rs.getTimestamp(RESPONSE_CONTENT_TIME)) != null)
                ct = t.getTime();
            response.setHeaderTime(ht);
            response.setContentTime(ct);

            return response;
        }

    }

    private static class ContentMapper implements
            ParameterizedRowMapper<byte[]> {

        public byte[] mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getBytes(CONTENT);
        }
    }

    private static class IdMapper implements ParameterizedRowMapper<Integer> {

        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Integer.valueOf(rs.getInt(ID));
        }
    }

    private static class ConversationMapper implements
            ParameterizedRowMapper<Conversation> {

        public Conversation mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            Conversation c = new Conversation();
            c.setId(rs.getInt(ID));
            c.setRequestId(rs.getInt(REQUESTID));
            c.setResponseId(rs.getInt(RESPONSEID));

            return c;
        }
    }
}
