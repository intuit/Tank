package trial.pojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
@XmlAccessorType(XmlAccessType.NONE)
public enum MethodType {

    @XmlEnumValue(value = "GET")
    GET("GET"),
    @XmlEnumValue(value = "POST")
    POST("POST");

    private String value;

    private MethodType(String value) {
        this.value = value;
    }
}
