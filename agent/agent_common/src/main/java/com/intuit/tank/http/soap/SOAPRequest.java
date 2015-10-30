package com.intuit.tank.http.soap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.TankHttpClient;
import com.intuit.tank.http.TankHttpLogger;

public class SOAPRequest extends BaseRequest {

    private SOAPBody soapBody = null;
    private SOAPEnvelope soapEnvelope = null;

    public SOAPRequest(TankHttpClient client, TankHttpLogger logUtil) {
        super(client, logUtil);

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();
            SOAPPart soapPart = message.getSOAPPart();

            this.soapEnvelope = soapPart.getEnvelope();
            this.soapBody = soapEnvelope.getBody();
        } catch (Exception ex) {

        }
    }

    public void setKey(String key, String value) {
        this.body = this.soapBody.toString();
    }

    public String getKey(String key) {

        return null;
    }

    public void setNamespace(String name, String value) {

    }
}
