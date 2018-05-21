package com.intuit.tank.conversation;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "transaction", namespace = Namespace.NAMESPACE_V1)
@XmlType(name = "transaction", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class Transaction {

    /**
     * 
     */
    private static final char NEWLINE = '\n';
    @XmlElement(namespace = Namespace.NAMESPACE_V1)
    private Request request;
    @XmlElement(namespace = Namespace.NAMESPACE_V1)
    private Response response;

    /**
     * @return the request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * @param request
     *            the request to set
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * @return the response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (request != null) {
            sb.append("------------ Request ------------").append(NEWLINE)
                    .append("    ").append(request.getFirstLine()).append(NEWLINE);
            sb.append(buildHeaderString("    ", request.getHeaders())).append(NEWLINE);
            sb.append("------------ Body ------------").append(NEWLINE);
            sb.append(request.getBodyAsString()).append(NEWLINE).append(NEWLINE);
        }
        if (response != null) {
            sb.append("------------ Response ------------").append(NEWLINE)
                    .append("    ").append(response.getFirstLine()).append(NEWLINE);
            sb.append(buildHeaderString("    ", response.getHeaders())).append(NEWLINE);
            sb.append("------------ Body ------------").append(NEWLINE);
            sb.append(response.getBodyAsString()).append(NEWLINE).append(NEWLINE);
        }
        return sb.toString();
    }

    /**
     * @param headers
     * @return
     */
    public String buildHeaderString(String prefix, List<Header> headers) {
        return headers.stream().map(h -> prefix + h.getKey() + ": " + h.getValue() + NEWLINE).collect(Collectors.joining());
    }

}
