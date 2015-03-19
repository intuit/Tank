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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.SocketAddress;

import org.owasp.proxy.daemon.ServerGroup;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MessageUtils;
import org.owasp.proxy.http.StreamingRequest;
import org.owasp.proxy.http.StreamingResponse;
import org.owasp.proxy.http.client.HttpClient;
import org.owasp.proxy.io.TimingInputStream;

public class DefaultHttpRequestHandler implements HttpRequestHandler {

    private ProxySelector proxySelector = null;

    private ServerGroup serverGroup = null;

    private ThreadLocal<HttpClient> client = new ThreadLocal<HttpClient>() {

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected HttpClient initialValue() {
            return createClient();
        }

    };

    public void setServerGroup(ServerGroup serverGroup) {
        this.serverGroup = serverGroup;
    }

    public void setProxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }

    protected HttpClient createClient() {
        HttpClient client = new HttpClient() {

            /*
             * (non-Javadoc)
             * 
             * @see org.owasp.httpclient.Client#checkLoop(java.net.SocketAddress)
             */
            @Override
            protected void validateTarget(SocketAddress target)
                    throws IOException {
                if (serverGroup != null && target instanceof InetSocketAddress
                        && serverGroup.wouldAccept((InetSocketAddress) target))
                    throw new IOException("Loop detected");
            }

        };
        client.setProxySelector(proxySelector);
        return client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.owasp.proxy.daemon.HttpRequestHandler#dispose()
     */
    public void dispose() throws IOException {
        client.get().disconnect();
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
        HttpClient client = this.client.get();
        if (isContinue) {
            client.sendRequestContent(request.getContent());
        } else {
            client.connect(request.getTarget(), request.isSsl());
            client.sendRequestHeader(request.getHeader());
            if (request.getContent() != null)
                client.sendRequestContent(request.getContent());
        }
        request.setTime(client.getRequestTime());
        StreamingResponse response = new StreamingResponse.Impl();

        response.setHeader(client.getResponseHeader());
        response.setHeaderTime(client.getResponseHeaderEndTime());
        // handle unsolicited 100-continue responses
        if (!MessageUtils.isExpectContinue(request)
                && "100".equals(response.getStatus())) {
            byte[] cont = response.getHeader();
            byte[] header = client.getResponseHeader();
            response.setHeaderTime(client.getResponseHeaderEndTime());
            byte[] both = new byte[cont.length + header.length];
            System.arraycopy(cont, 0, both, 0, cont.length);
            System.arraycopy(header, 0, both, cont.length + 1, header.length);
            response.setHeader(both);
        }
        InputStream content = client.getResponseContent();

        if (content != null)
            content = new TimingInputStream(content, response);
        response.setContent(content);
        return response;
    }

}
