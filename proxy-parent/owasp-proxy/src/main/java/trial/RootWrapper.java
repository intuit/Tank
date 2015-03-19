package trial;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RootWrapper")
public class RootWrapper {

    @XmlElement(name = "root")
    private Root[] roots;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Root r : roots) {
            sb.append(r.toString()).append("\n");
        }
        return sb.toString();
    }

}
