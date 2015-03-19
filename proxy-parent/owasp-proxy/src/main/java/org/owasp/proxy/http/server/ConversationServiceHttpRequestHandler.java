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

package org.owasp.proxy.http.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.NamedValue;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.ResponseHeader;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.http.dao.ConversationSummary;
import org.owasp.proxy.http.dao.MessageDAO;
import org.owasp.proxy.util.AsciiString;
import org.springframework.dao.DataAccessException;

@SuppressWarnings("serial")
public class ConversationServiceHttpRequestHandler implements
        HttpRequestHandler {

    private static final byte[] SUCCESS_XML = AsciiString
            .getBytes("HTTP/1.0 200 Ok\r\nContent-Type: text/xml\r\n\r\n");
    private static final byte[] SUCCESS_OCTET = AsciiString
            .getBytes("HTTP/1.0 200 Ok\r\nContent-Type: application/octet-stream\r\n\r\n");
    private static final byte[] SUCCESS_HTML = AsciiString
            .getBytes("HTTP/1.0 200 Ok\r\nContent-Type: text/html\r\n\r\n");

    private static final String CONVERSATIONS = "/conversations";
    private static final String SUMMARIES = "/summaries";
    private static final String SUMMARY = "/summary";
    private static final String REQUEST_HEADER = "/requestHeader";
    private static final String RESPONSE_HEADER = "/responseHeader";
    private static final String REQUEST_CONTENT = "/requestContent";
    private static final String RESPONSE_CONTENT = "/responseContent";

    private static final String INDEX_PAGE = "<html><a href='" + CONVERSATIONS
            + "'>Conversations</a><p>" + "<form target='" + CONVERSATIONS
            + "'>Since: <input type='text' name='since'></input></form><p>"
            + "</html>";

    private String hostname;
    private MessageDAO dao;
    private HttpRequestHandler next;

    private Map<Integer, ConversationSummary> summaryCache = new LinkedHashMap<Integer, ConversationSummary>() {
        protected boolean removeEldestEntry(
                Map.Entry<Integer, ConversationSummary> eldest) {
            return size() > 1000;
        }
    };

    public ConversationServiceHttpRequestHandler(String hostname,
            MessageDAO dao, HttpRequestHandler next) {
        this.hostname = hostname;
        this.dao = dao;
        this.next = next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.HttpRequestHandler#dispose()
     */
    public void dispose() throws IOException {
        if (next != null)
            next.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.HttpRequestHandler#handleRequest(java.net.InetAddress ,
     * org.owasp.httpclient.StreamingRequest)
     */
    public StreamingResponse handleRequest(InetAddress source,
            StreamingRequest request, boolean isContinue) throws IOException,
            MessageFormatException {
        if (next == null
                || (hostname != null && hostname.equals(request.getTarget()
                        .getHostName()))) {
            return handleLocalRequest(request);
        }
        return next.handleRequest(source, request, isContinue);
    }

    private StreamingResponse handleLocalRequest(StreamingRequest request) {
        try {
            String resource = request.getResource();
            if ("/".equals(resource))
                return getIndexPage();
            int q = resource.indexOf('?');
            NamedValue[] parameters = null;
            if (q > -1) {
                parameters = NamedValue.parse(resource.substring(q + 1), "&",
                        "=");
                resource = resource.substring(0, q);
            }
            if (resource.equals(CONVERSATIONS)) {
                String since = NamedValue.findValue(parameters, "since");
                if (since != null)
                    return listConversations(Integer.parseInt(since));
                return listConversations(0);
            } else if (resource.equals(SUMMARIES)) {
                String since = NamedValue.findValue(parameters, "since");
                if (since != null)
                    return getSummaries(Integer.parseInt(since));
                return getSummaries(0);
            } else if (resource.equals(SUMMARY)) {
                String id = NamedValue.findValue(parameters, "id");
                if (id != null)
                    return getSummary(Integer.parseInt(id));
            } else if (resource.equals(REQUEST_HEADER)) {
                String id = NamedValue.findValue(parameters, "id");
                if (id != null)
                    return getRequestHeader(Integer.parseInt(id));
            } else if (resource.equals(RESPONSE_HEADER)) {
                String id = NamedValue.findValue(parameters, "id");
                if (id != null)
                    return getResponseHeader(Integer.parseInt(id));
            } else if (resource.equals(REQUEST_CONTENT)) {
                String id = NamedValue.findValue(parameters, "id");
                String decode = NamedValue.findValue(parameters, "decode");
                if (id != null)
                    return getRequestContent(Integer.parseInt(id), "true"
                            .equals(decode));
            } else if (resource.equals(RESPONSE_CONTENT)) {
                String id = NamedValue.findValue(parameters, "id");
                String decode = NamedValue.findValue(parameters, "decode");
                if (id != null)
                    return getResponseContent(Integer.parseInt(id), "true"
                            .equals(decode));
            }
        } catch (MessageFormatException mfe) {
            mfe.printStackTrace();
            return err_400();
        } catch (DataAccessException dae) {
            dae.printStackTrace();
            return err_500();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return err_400();
        }
        return err_404();
    }

    private StreamingResponse getIndexPage() {
        return conversation(SUCCESS_HTML, INDEX_PAGE);
    }

    private ConversationSummary loadConversationSummary(int id) {
        ConversationSummary cs = summaryCache.get(id);
        if (cs != null)
            return cs;

        cs = dao.getConversationSummary(id);
        summaryCache.put(id, cs);
        return cs;
    }

    private Iterator<ConversationSummary> getConversationSummaries(
            Iterator<Integer> ids) {
        List<ConversationSummary> conversations = new LinkedList<ConversationSummary>();
        while (ids.hasNext()) {
            conversations.add(loadConversationSummary(ids.next()));
        }
        return conversations.iterator();
    }

    private StreamingResponse listConversations(int since) {
        Iterator<Integer> it = dao.listConversationsSince(since).iterator();
        StringBuilder buff = new StringBuilder();
        buff.append("<conversations>");
        while (it.hasNext()) {
            buff.append("<conversation>").append(it.next()).append(
                    "</conversation>");
        }
        buff.append("</conversations>");
        return conversation(SUCCESS_XML, buff.toString());
    }

    private StreamingResponse getSummary(int id) {
        ConversationSummary summary = loadConversationSummary(id);
        StringBuilder buff = new StringBuilder();
        buff.append("<summaries>");
        xml(buff, summary);
        buff.append("</summaries>");
        return conversation(SUCCESS_XML, buff.toString());
    }

    private StreamingResponse getSummaries(int since) {
        Iterator<Integer> ids = dao.listConversationsSince(since).iterator();
        Iterator<ConversationSummary> it = getConversationSummaries(ids);
        StringBuilder buff = new StringBuilder();
        buff.append("<summaries>");
        while (it.hasNext()) {
            xml(buff, it.next());
        }
        buff.append("</summaries>");
        return conversation(SUCCESS_XML, buff.toString());
    }

    private void xml(StringBuilder buff, ConversationSummary summary) {
        buff.append("<summary id=\"");
        buff.append(summary.getId());
        buff.append("\" requestTime=\"").append(summary.getRequestSubmissionTime());
        buff.append("\" responseHeaderTime=\"").append(
                summary.getResponseHeaderTime());
        buff.append("\" responseContentTime=\"").append(
                summary.getResponseContentTime());
        buff.append("\">");
        tag(buff, "host", summary.getTarget().getHostName());
        tag(buff, "port", summary.getTarget().getPort());
        tag(buff, "ssl", summary.isSsl());
        tag(buff, "resource", summary.getRequestResource());
        tag(buff, "RequestContentType", summary.getRequestContentType());
        tag(buff, "RequestContentSize", summary.getRequestContentSize());
        tag(buff, "status", summary.getResponseStatus());
        tag(buff, "reason", summary.getResponseReason());
        tag(buff, "ResponseContentType", summary.getResponseContentType());
        tag(buff, "RequestContentSize", summary.getRequestContentSize());
        buff.append("</summary>");
    }

    private void tag(StringBuilder buff, String tagname, String content) {
        if (content == null)
            return;
        buff.append("<").append(tagname).append(">");
        buff.append(e(content));
        buff.append("</").append(tagname).append(">");
    }

    private void tag(StringBuilder buff, String tagname, boolean content) {
        buff.append("<").append(tagname).append(">");
        buff.append(content);
        buff.append("</").append(tagname).append(">");
    }

    private void tag(StringBuilder buff, String tagname, int content) {
        if (content <= 0)
            return;
        buff.append("<").append(tagname).append(">");
        buff.append(content);
        buff.append("</").append(tagname).append(">");
    }

    private static String e(String s) {
        StringBuilder buf = new StringBuilder();
        int len = (s == null ? -1 : s.length());

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0'
                    && c <= '9') {
                buf.append(c);
            } else if (c == '<') {
                buf.append("&lt;");
            } else if (c == '>') {
                buf.append("&gt;");
            } else if (c == '\'') {
                buf.append("&apos;");
            } else if (c == '"') {
                buf.append("&quot;");
            } else {
                buf.append("&#" + (int) c + ";");
            }
        }
        return buf.toString();

    }

    private StreamingResponse getRequestHeader(int id)
            throws MessageFormatException {
        RequestHeader r = dao.loadRequestHeader(id);
        if (r == null)
            return err_404();

        return conversation(SUCCESS_OCTET, r.getHeader());
    }

    private StreamingResponse getResponseHeader(int id)
            throws MessageFormatException {
        ResponseHeader r = dao.loadResponseHeader(id);
        if (r == null)
            return err_404();

        return conversation(SUCCESS_OCTET, r.getHeader());
    }

    private StreamingResponse getRequestContent(int id, boolean decode)
            throws MessageFormatException {
        int contentId = dao.getMessageContentId(id);
        if (contentId == -1)
            return err_404();

        byte[] content = dao.loadMessageContent(contentId);
        if (decode) {
            RequestHeader request = dao.loadRequestHeader(id);
            try {
                return conversation(SUCCESS_OCTET, MessageUtils.decode(request,
                        new ByteArrayInputStream(content)));
            } catch (IOException ioe) {
                return err_500();
            }
        }
        return conversation(SUCCESS_OCTET, content);
    }

    private StreamingResponse getResponseContent(int id, boolean decode)
            throws MessageFormatException {
        int contentId = dao.getMessageContentId(id);
        if (contentId == -1)
            return err_404();

        byte[] content = dao.loadMessageContent(contentId);
        if (decode) {
            ResponseHeader response = dao.loadResponseHeader(id);
            try {
                return conversation(SUCCESS_OCTET, MessageUtils.decode(
                        response, new ByteArrayInputStream(content)));
            } catch (IOException ioe) {
                return err_500();
            }
        }
        return conversation(SUCCESS_OCTET, content);
    }

    private StreamingResponse err_400() {
        return conversation("HTTP/1.0 400 Bad request\r\n\r\n", "Bad request");
    }

    private StreamingResponse err_404() {
        return conversation("HTTP/1.0 404 Resource not found\r\n\r\n",
                "Resource not found");
    }

    private StreamingResponse err_500() {
        return conversation("HTTP/1.0 500 Error processing request\r\n\r\n",
                "Error processing request");
    }

    private StreamingResponse conversation(String header, String content) {
        return conversation(AsciiString.getBytes(header), content);
    }

    private StreamingResponse conversation(byte[] header, String content) {
        return conversation(header, stream(content));
    }

    private StreamingResponse conversation(byte[] header, byte[] content) {
        return conversation(header, stream(content));
    }

    private StreamingResponse conversation(byte[] header, InputStream content) {
        StreamingResponse response = new StreamingResponse.Impl();
        response.setHeader(header);
        response.setContent(content);
        return response;
    }

    private InputStream stream(byte[] content) {
        return new ByteArrayInputStream(content);
    }

    private InputStream stream(String content) {
        return stream(AsciiString.getBytes(content));
    }

}
