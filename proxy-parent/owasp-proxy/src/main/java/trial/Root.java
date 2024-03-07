package trial;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
public class Root {

    @XmlAttribute(name = "name")
    private String naam;

    @Override
    public String toString() {
        return naam + " unmarshall";
    }

}
