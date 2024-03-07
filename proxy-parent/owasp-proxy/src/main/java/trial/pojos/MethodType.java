package trial.pojos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

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
