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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement(name = "response", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    @XmlElement(name = "firstLine", namespace = Namespace.NAMESPACE_V1)
    private String firstLine;

    @XmlElementWrapper(name = "headers", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "header", namespace = Namespace.NAMESPACE_V1)
    private List<Header> headers = new ArrayList<Header>();

    @XmlElement(name = "body", namespace = Namespace.NAMESPACE_V1)
    private byte[] body;

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

    public void setBody(byte[] body) {
        this.body = body;
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

    /**
     * @return the headers
     */
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * @param header
     *            the headers to set
     */
    public void addHeader(@Nonnull Header header) {
        this.headers.add(header);
    }

    /**
     * @param key
     * @param value
     */
    public void addHeader(@Nonnull String key, @Nonnull String value) {
        this.headers.add(new Header(key, value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 25).append(headers).append(body).toHashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
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
        Response o = (Response) obj;

        return new EqualsBuilder().append(o.getBody(), getBody()).append(o.getHeaders(), getHeaders()).isEquals();
    }

    public String extractContentType() {
        return headers.stream().filter(header -> header.getKey().equalsIgnoreCase("content-type")).findFirst().map(Header::getValue).orElse("");
    }

}
