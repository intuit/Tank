package trial.pojos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "header")
@XmlAccessorType(XmlAccessType.NONE)
public class Header {

    @XmlElement(name = "key")
    private String key;
    @XmlElement(name = "value")
    private String value;

    /**
     * @param key
     * @param value
     */
    public Header(@NotNull String key, @NotNull String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Default Constructor
     * 
     * Framework use only
     */
    protected Header() {
        this("", "");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
