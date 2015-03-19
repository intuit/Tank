package trial;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
public class Root {

    @XmlAttribute(name = "name")
    private String naam;

    @Override
    public String toString() {
        return naam + " unmarshall";
    }

}
