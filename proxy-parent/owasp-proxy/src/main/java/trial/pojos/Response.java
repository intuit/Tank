package trial.pojos;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {

    @XmlAttribute(name = "method", required = true)
    private MethodType method;
    @XmlElement(name = "header")
    private List<Header> headers;
    @XmlElement(name = "body")
    private byte[] body;
    @XmlElement(name = "mimetype")
    private String mimeType;
    @XmlElement(name = "status")
    private String statusCode;
    @XmlElement(name = "content-length")
    private String contentLength;
    @XmlElementWrapper(name = "cookies")
    @XmlElement(name = "cookie")
    private List<Cookie> cookies;

    // TODO: add mimetype, content-length , status code, namespace, cookies

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
