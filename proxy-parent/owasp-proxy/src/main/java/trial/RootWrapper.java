package trial;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.stream.Collectors;

@XmlRootElement(name = "RootWrapper")
public class RootWrapper {

    @XmlElement(name = "root")
    private Root[] roots;

    @Override
    public String toString() {
        return Arrays.stream(roots).map(r -> r.toString() + "\n").collect(Collectors.joining());
    }

}
