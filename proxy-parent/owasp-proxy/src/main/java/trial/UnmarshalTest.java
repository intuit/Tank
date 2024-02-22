package trial;

import java.io.File;
import java.io.IOException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class UnmarshalTest {

    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(UnmarshalTest.class.getPackage().getName());
        Unmarshaller unmarhsaller = context.createUnmarshaller();

        Marshaller createMarshaller = context.createMarshaller();
        // createMarshaller.marshal

        File file = CustomInputStream.getNewFile(new File("./src/main/java/validating.xml"));
        Object unmarshal = unmarhsaller.unmarshal(file);
        System.out.println(unmarshal);

    }

}
