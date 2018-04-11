package com.intuit.tank.conversation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "request", namespace = Namespace.NAMESPACE_V1)
public class Request {

    @XmlElement(name = "protocol", namespace = Namespace.NAMESPACE_V1)
    private Protocol protocol;

    @XmlElement(name = "firstLine", namespace = Namespace.NAMESPACE_V1)
    private String firstLine;

    @XmlElementWrapper(name = "headers", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "header", namespace = Namespace.NAMESPACE_V1)
    private List<Header> headers = new ArrayList<Header>();

    @XmlElement(name = "body", namespace = Namespace.NAMESPACE_V1)
    private byte[] body;

    /**
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the firstLine
     */
    public String getFirstLine() {
        return firstLine;
    }

    /**
     * @param firstLine
     *            the firstLine to set
     */
    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public byte[] getBody() {
        return body;
    }

    /**
     * 
     * @return
     */
    @XmlTransient
    public String getBodyAsString() {
        String ret = null;
        if (body != null) {
            try {
                ret = new String(body, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // swallow this should never occur.
            }
        }
        return ret;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * @return the headers
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * @param headers
     *            the headers to set
     */
    public void addHeader(@Nonnull Header header) {
        this.headers.add(header);
    }

    /**
     * @param headers
     *            the headers to set
     */
    public void addHeader(@Nonnull String key, @Nonnull String value) {
        this.headers.add(new Header(key, value));
    }

    /**
     * 
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(headers).append(body)
                .toHashCode();
    }

    /**
     * 
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Request o = (Request) obj;

        return new EqualsBuilder()
                .append(o.getHeaders(), getHeaders()).append(o.getBody(), getBody())
                .append(o.getHeaders(), getHeaders()).isEquals();
    }

}
