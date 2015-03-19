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

package org.owasp.proxy.http;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jcifs.ntlmssp.NtlmFlags;
import jcifs.ntlmssp.NtlmMessage;
import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;

import org.owasp.proxy.util.Base64;

public class HttpAuthenticator {

    // Flag values determined empirically through observation of successful
    // authentication using Firefox and WireShark
    private static int NTLMV2_FLAGS = NtlmFlags.NTLMSSP_NEGOTIATE_NTLM2
            | NtlmFlags.NTLMSSP_NEGOTIATE_ALWAYS_SIGN
            | NtlmFlags.NTLMSSP_NEGOTIATE_NTLM
            | NtlmFlags.NTLMSSP_REQUEST_TARGET
            | NtlmFlags.NTLMSSP_NEGOTIATE_OEM
            | NtlmFlags.NTLMSSP_NEGOTIATE_UNICODE;

    private static int NTLMV2_FLAGS_TYPE3 = NTLMV2_FLAGS
            & ~NtlmFlags.NTLMSSP_NEGOTIATE_OEM;

    public static boolean requiresAuthentication(ResponseHeader response)
            throws MessageFormatException {
        String status = response.getStatus();
        return "401".equals(status) || "407".equals(status);
    }

    public boolean authenticate(MutableRequestHeader request,
            ResponseHeader response, ResponseHeader response2)
            throws IOException, MessageFormatException {
        if (response == null) {
            // TODO: preemptive authentication, not yet supported
            return false;
        }
        String status = response.getStatus();
        String authHeader = null;
        List<String> challenges = null;
        if ("407".equals(status)) {
            // proxy authentication required
            authHeader = HttpConstants.PROXY_AUTHORIZATION;
            challenges = getChallenges(response,
                    HttpConstants.PROXY_AUTHENTICATE);
        } else if ("401".equals(status)) {
            // www authentication required
            authHeader = HttpConstants.AUTHORIZATION;
            challenges = getChallenges(response, HttpConstants.AUTHENTICATE);
        }
        if (challenges == null || challenges.size() == 0)
            return false;

        String challenge = selectChallenge(challenges);
        if (challenge == null)
            return false;
        String authResponse = constructResponse(request.getTarget(), challenge);
        if (authResponse == null)
            return false;
        request.setHeader(authHeader, authResponse);
        return true;
    }

    private static List<String> getChallenges(ResponseHeader response,
            String header) throws MessageFormatException {
        List<String> challenges = new ArrayList<String>();
        NamedValue[] headers = response.getHeaders();
        for (int i = 0; i < headers.length; i++) {
            if (header.equalsIgnoreCase(headers[i].getName())) {
                challenges.add(headers[i].getValue());
            }
        }
        return challenges;
    }

    protected String constructResponse(InetSocketAddress target,
            String challenge) throws IOException {
        if (challenge.startsWith("NTLM")) {
            return performNtlmAuthentication(target, challenge);
        } else if (challenge.startsWith("Digest")) {
            return performDigestAuthentication(target, challenge);
        } else if (challenge.startsWith("Basic")) {
            return performBasicAuthentication(target, challenge);
        }
        return null;
    }

    protected String performBasicAuthentication(InetSocketAddress target,
            String challenge) throws IOException {
        int index = challenge.indexOf("realm=");
        if (index == -1)
            return null;
        String realm = challenge.substring(index + 6); // "realm="
        String hostname = target.getHostName();
        InetAddress addr = target.isUnresolved() ? null : target.getAddress();
        PasswordAuthentication pa = Authenticator
                .requestPasswordAuthentication(hostname, addr,
                        target.getPort(), "HTTP", realm, "Basic Authentication");
        if (pa == null)
            return null;

        String auth = pa.getUserName() + ":" + new String(pa.getPassword());

        return "Basic "
                + Base64.encodeBytes(auth.getBytes(), Base64.NO_OPTIONS);
    }

    protected String performDigestAuthentication(InetSocketAddress target,
            String challenge) throws IOException {
        return null;
    }

    protected String performNtlmAuthentication(InetSocketAddress target,
            String challenge) throws IOException {
        if (challenge.length() == 4) {
            NtlmMessage type1 = new Type1Message(NTLMV2_FLAGS, null, null);
            return "NTLM "
                    + Base64
                            .encodeBytes(type1.toByteArray(), Base64.NO_OPTIONS);
        } else {
            challenge = challenge.substring(5); // "NTLM "
            Type2Message type2 = new Type2Message(Base64.decode(challenge,
                    Base64.NO_OPTIONS));
            String domain = type2.getTarget();
            String hostname = target.getHostName();
            InetAddress addr = target.isUnresolved() ? null : target
                    .getAddress();
            PasswordAuthentication pa = Authenticator
                    .requestPasswordAuthentication(hostname, addr, target
                            .getPort(), "HTTP", domain, "NTLM");
            if (pa == null)
                return null;

            String username = pa.getUserName();
            String password = new String(pa.getPassword());
            int slash = username.indexOf('\\');
            if (slash > -1) {
                domain = username.substring(0, slash);
                username = username.substring(slash + 1);
            }
            Type3Message type3 = new Type3Message(type2, password, domain,
                    username, null, NTLMV2_FLAGS_TYPE3);
            return "NTLM "
                    + Base64
                            .encodeBytes(type3.toByteArray(), Base64.NO_OPTIONS);
        }
    }

    protected String selectChallenge(List<String> challenges) {
        if (challenges.size() == 1)
            return challenges.get(0);
        Iterator<String> it = challenges.iterator();
        while (it.hasNext()) {
            String challenge = it.next();
            if (challenge.toLowerCase().startsWith("basic"))
                return challenge;
        }
        if (challenges.contains("NTLM")) {
            return "NTLM";
        }
        return null;
    }

}
