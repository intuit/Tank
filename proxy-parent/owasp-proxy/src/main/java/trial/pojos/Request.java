package trial.pojos;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.NONE)
public class Request {

    @XmlAttribute(name = "method", required = true)
    private MethodType method;
    @XmlElementWrapper(name = "headers", namespace = "")
    @XmlElement(name = "header")
    private List<Header> headers;
    @XmlElement(name = "body")
    private byte[] body;
    @XmlElement(name = "mimetype")
    private String mimeType;
    @XmlElement(name = "url")
    private String url;
    @XmlElementWrapper(name = "cookies")
    @XmlElement(name = "cookie")
    private List<Cookie> cookies;

    // TODO : add mimetype, request url, namespace, cookies

    public MethodType getMethod() {
        return method;
    }

    public void setMethod(MethodType method) {
        this.method = method;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}
