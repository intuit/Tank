/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.mail;

/*
 * #%L
 * Mail
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.mail.MailService;
import com.intuit.tank.vm.settings.MailConfig;
import com.intuit.tank.vm.settings.MailMessage;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * TankMailer
 * 
 * @author dangleton
 * 
 */
public class TankMailer implements MailService, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(TankMailer.class);

    /**
     * @{inheritDoc
     */
    @Override
    public void sendMail(MailMessage message, String... emailAddresses) {
        MailConfig mailConfig = new TankConfig().getMailConfig();
        Properties props = new Properties();
        props.put("mail.smtp.host", mailConfig.getSmtpHost());
        props.put("mail.smtp.port", mailConfig.getSmtpPort());

        Session mailSession = Session.getDefaultInstance(props);
        Message simpleMessage = new MimeMessage(mailSession);

        InternetAddress fromAddress = null;
        InternetAddress toAddress = null;
        try {
            fromAddress = new InternetAddress(mailConfig.getMailFrom());
            simpleMessage.setFrom(fromAddress);
            for (String email : emailAddresses) {
                try {
                    toAddress = new InternetAddress(email);
                    simpleMessage.addRecipient(RecipientType.TO, toAddress);
                } catch (AddressException e) {
                    LOG.warn("Error with recipient " + email + ": " + e.toString());
                }
            }

            simpleMessage.setSubject(message.getSubject());
            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(message.getPlainTextBody(), "text/plain");
            textPart.setHeader("MIME-Version", "1.0");
            textPart.setHeader("Content-Type", textPart.getContentType());
            // HTML version
            final MimeBodyPart htmlPart = new MimeBodyPart();
            // htmlPart.setContent(message.getHtmlBody(), "text/html");
            htmlPart.setDataHandler(new DataHandler(new HTMLDataSource(message.getHtmlBody())));
            htmlPart.setHeader("MIME-Version", "1.0");
            htmlPart.setHeader("Content-Type", "text/html");
            // Create the Multipart. Add BodyParts to it.
            final Multipart mp = new MimeMultipart("alternative");
            mp.addBodyPart(textPart);
            mp.addBodyPart(htmlPart);
            // Set Multipart as the message's content
            simpleMessage.setContent(mp);
            simpleMessage.setHeader("MIME-Version", "1.0");
            simpleMessage.setHeader("Content-Type", mp.getContentType());
            logMsg(mailConfig.getSmtpHost(), simpleMessage);
            if (simpleMessage.getRecipients(RecipientType.TO) != null && simpleMessage.getRecipients(RecipientType.TO).length > 0) {
                Transport.send(simpleMessage);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void logMsg(String host, Message m) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("To: ").append(StringUtils.join(m.getAllRecipients(), ',')).append('\n');
            sb.append("From: ").append(StringUtils.join(m.getFrom(), ',')).append('\n');
            sb.append("Subject: ").append(m.getSubject()).append('\n');
            sb.append("Body: ").append(m.getContent()).append('\n');
            LOG.info("Sending email to server (" + host + "):\n" + sb.toString());
        } catch (Exception e) {
            LOG.error("Error generating log msg: " + e);
        }

    }

    /*
     * Inner class to act as a JAF datasource to send HTML e-mail content
     */
    static class HTMLDataSource implements DataSource {
        private String html;

        public HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        // Return html string in an InputStream.
        // A new stream must be returned each time.
        public InputStream getInputStream() throws IOException {
            if (html == null)
                throw new IOException("Null HTML");
            return new ByteArrayInputStream(html.getBytes());
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        public String getContentType() {
            return "text/html";
        }

        public String getName() {
            return "text/html dataSource to send e-mail only";
        }
    }

}
