/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * MailMessage
 * 
 * @author dangleton
 * 
 */
public class MailMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private String body;
    private String subject;
    private String cssStyle;

    /**
     * @param body
     * @param subject
     */
    public MailMessage(String body, String subject, String cssStyle) {
        this.body = body;
        this.subject = subject;
        this.cssStyle = cssStyle;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the cssStyle
     */
    public String getCssStyle() {
        return cssStyle;
    }

    public String getPlainTextBody() {
        String ret = body.replace("&nbsp;", " ");
        return ret.replaceAll("<(?!\\/?a(?=>|\\s.*>))\\/?.*?>", "");
    }

    /**
     * @return
     */
    public String getHtmlBody() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">")
                .append('\n');
        sb.append("<html>").append('\n');
        sb.append("<head>").append('\n');
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        if (StringUtils.isNotBlank(cssStyle)) {
            sb.append("<style type=\"text/css\">").append('\n');
            sb.append(cssStyle).append('\n');
            sb.append("</style>").append('\n');
        }
        sb.append("</head>").append('\n');
        sb.append("<body>").append('\n');
        sb.append(body);
        sb.append("</body>").append('\n');
        sb.append("</html>").append('\n');
        return sb.toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
