package com.intuit.tank.entity;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.owasp.proxy.http.BufferedRequest;
import org.owasp.proxy.http.MessageFormatException;

import com.intuit.tank.conversation.Header;
import com.intuit.tank.conversation.Request;
import com.intuit.tank.conversation.Response;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.proxy.config.ConfigInclusionExclusionRule;
import com.intuit.tank.proxy.config.ProxyConfiguration;
import com.intuit.tank.proxy.config.TransactionPart;
import com.intuit.tank.proxy.table.TransactionRecordedListener;
import com.intuit.tank.util.HeaderParser;

public final class Application {

    // private Properties properties;
    private JAXBContext context;
    private Marshaller marshaller;
    private OutputStreamWriter osw;
    private boolean sessionStarted = false;
    private TransactionRecordedListener listener;
    private boolean paused;
    private Map<String, Transaction> redirectMap;
    public static final Header REDIRECT_MARKER = new Header("X-PROXY-APP", "redirectCollapse");

    private ProxyConfiguration proxyConfiguration;

    public Application(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
        this.redirectMap = new HashMap<String, Transaction>();
    }

    public void resumeSession() {
        paused = false;
    }

    public void setConfig(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
    }

    /**
     * @return the paused
     */
    public boolean isPaused() {
        return paused;
    }

    public void startSession(TransactionRecordedListener l) {

        this.listener = l;
        try {
            context = JAXBContext.newInstance(Request.class.getPackage().getName());
            marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            osw = new OutputStreamWriter(new FileOutputStream(new File(proxyConfiguration.getOutputFile())));
            osw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            osw.write("<sns:session xmlns:sns=\"urn:proxy/conversation/v1\"" +
                    " followRedirects=\"" + proxyConfiguration.isFollowRedirects() + "\">\n");
            sessionStarted = true;
            paused = false;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public synchronized Transaction setRequestForCurrentTransaction(Request request, BufferedRequest req) {
        // check if url is in redirectMap
        String path = null;
        try {
            path = getLocation(req);
        } catch (MessageFormatException e) {
            e.printStackTrace();
        }
        if (proxyConfiguration.isFollowRedirects() && path != null && redirectMap.containsKey(path)) {
            System.out.println("Found redirection for locaiton " + path);
            return redirectMap.get(path);
        } else {
            Transaction t = new Transaction();
            t.setRequest(request);
            return t;
        }
    }

    /**
     * Checks whether the content-type should be handled by the framework
     * 
     * @param checkStatusCode
     * 
     * @param extractContentType
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static boolean shouldInclude(Transaction t, Set<ConfigInclusionExclusionRule> inclusionRules,
            Set<ConfigInclusionExclusionRule> exclusionRules, boolean checkStatusCode) {
        boolean ret = true;
        int statusCode = HeaderParser.extractStatusCode(t.getResponse().getFirstLine());
        if (checkStatusCode && statusCode >= 300 && statusCode < 400) {
            ret = false;
        } else {
            for (ConfigInclusionExclusionRule rule : inclusionRules) {
                List<Header> headers = new ArrayList<Header>();
                if (rule.getTransactionPart() == TransactionPart.request
                        || rule.getTransactionPart() == TransactionPart.both) {
                    headers.addAll(t.getRequest().getHeaders());
                }
                if (rule.getTransactionPart() == TransactionPart.response
                        || rule.getTransactionPart() == TransactionPart.both) {
                    headers.addAll(t.getResponse().getHeaders());

                }
                ret = evaluateRule(rule, headers, HeaderParser.extractPath(t.getRequest().getFirstLine()));
                if (ret) {
                    break;
                }
            }
            if (ret) {
                for (ConfigInclusionExclusionRule rule : exclusionRules) {
                    List<Header> headers = new ArrayList<Header>();
                    if (rule.getTransactionPart() == TransactionPart.request
                            || rule.getTransactionPart() == TransactionPart.both) {
                        headers.addAll(t.getRequest().getHeaders());
                    }
                    if (rule.getTransactionPart() == TransactionPart.response
                            || rule.getTransactionPart() == TransactionPart.both) {
                        headers.addAll(t.getResponse().getHeaders());

                    }
                    ret = !evaluateRule(rule, headers, HeaderParser.extractPath(t.getRequest().getFirstLine()));
                    if (!ret) {
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * @param headers
     * @param extractPath
     * @return
     */
    private static boolean evaluateRule(ConfigInclusionExclusionRule rule, List<Header> headers, String extractPath) {
        boolean ret = false;
        if ("path".equalsIgnoreCase(rule.getHeader())) {
            ret = rule.matches(extractPath);
        } else {
            for (Header header : headers) {
                if (rule.getHeader().equalsIgnoreCase("all") || header.getKey().equalsIgnoreCase(rule.getHeader())) {
                    ret = rule.matches(header.getValue());
                    if (ret) {
                        break;
                    }
                }
            }
        }
        return ret;
    }

    public synchronized void setResponseForCurrentTransaction(Transaction transaction, Response response,
            BufferedRequest req)
            throws JAXBException {
        if (transaction != null && sessionStarted && !paused) {
            int statusCode = HeaderParser.extractStatusCode(response.getFirstLine());
            HeaderParser hp = new HeaderParser(response);
            if (proxyConfiguration.isFollowRedirects() && statusCode == 302) { // redirect
                String location = hp.getRedirectLocation();
                try {
                    String oldLocation = getLocation(req);
                    Transaction remove = redirectMap.remove(oldLocation);
                    if (remove != null) {
                        System.out.println("removing location " + oldLocation + " got transaction "
                                + remove.getRequest().getFirstLine());
                    } else {
                        System.out.println("could not remove location " + oldLocation);
                    }
                } catch (MessageFormatException e) {
                    System.out.println("Error extracting path from first line: " + e);
                }
                System.out.println("Pushing redirect location " + location + " with transaction firstline "
                        + transaction.getRequest().getFirstLine());
                if (!transaction.getRequest().getHeaders().contains(REDIRECT_MARKER)) {
                    transaction.getRequest().addHeader(REDIRECT_MARKER);
                }
                transaction.getRequest().addHeader(new Header("X-Redirect-Location", location));
                redirectMap.put(location, transaction);
            } else {
                transaction.setResponse(response);
                boolean filtered = true;
                if (!shouldInclude(transaction, proxyConfiguration.getBodyInclusions(),
                        proxyConfiguration.getBodyExclusions(), true)) {
                    response.setBody(new byte[0]);
                }
                if (shouldInclude(transaction, proxyConfiguration.getInclusions(),
                        proxyConfiguration.getExclusions(), false)) {
                    marshaller.marshal(transaction, osw);
                    filtered = false;
                }
                if (listener != null) {
                    listener.transactionProcessed(transaction, filtered);
                }
            }
        }
    }

    /**
     * @param req
     * @throws MessageFormatException
     */
    private String getLocation(BufferedRequest req) throws MessageFormatException {
        StringBuilder sb = new StringBuilder().append(req.isSsl() ? "https://" : "http://");
        sb.append(req.getHeader("Host"));
        sb.append(HeaderParser.extractLocation(req.getStartLine()));
        return sb.toString();
    }

    public void endSession() throws JAXBException, IOException {
        if (sessionStarted) {
            osw.write("\n");
            osw.write("</sns:session>");
            osw.flush();
            osw.close();
        }
        sessionStarted = false;
        System.out.println("Finishing up");
    }

    /**
     * 
     */
    public void pauseSession() {
        paused = true;
    }

}
