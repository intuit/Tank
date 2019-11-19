package trial.pojos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {

    @Test
    public void testRequestMarshalUnmarshal() throws JAXBException, IOException {

        Request request = new Request();
        request.setMethod(MethodType.GET);

        List<Header> headerList = new ArrayList<Header>();

        headerList.add(new Header("key1", "value1"));
        headerList.add(new Header("key2", "value2"));
        headerList.add(new Header("key3", "value3"));
        headerList.add(new Header("key4", "value4"));
        headerList.add(new Header("key5", "value5"));
        headerList.add(new Header("key6", "value6"));

        // request.getHeaders(headerList);

        String body = "This is my body.";

        request.setBody(body.getBytes());

        JAXBContext context = JAXBContext.newInstance(RequestTest.class.getPackage().getName());
        Marshaller createMarshaller = context.createMarshaller();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        createMarshaller.marshal(request, new File("./request.xml"));
        Request requestUnmarshalled = (Request) unmarshaller.unmarshal(new File("./request.xml"));

        assertEquals(request, requestUnmarshalled);
    }

    @Test
    public void testResponseMarshalUnmarshal() throws JAXBException, IOException {

        Response response = new Response();
        response.setMethod(MethodType.GET);

        List<Header> headerList = new ArrayList<Header>();

        headerList.add(new Header("key1", "value1"));
        headerList.add(new Header("key2", "value2"));
        headerList.add(new Header("key3", "value3"));
        headerList.add(new Header("key4", "value4"));
        headerList.add(new Header("key5", "value5"));
        headerList.add(new Header("key6", "value6"));

        // response.setHeaders((Header[]) headerList.toArray(new Header[0]));

        String body = "This is my body.";

        response.setBody(body.getBytes());

        JAXBContext context = JAXBContext.newInstance(RequestTest.class.getPackage().getName());
        Marshaller createMarshaller = context.createMarshaller();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        createMarshaller.marshal(response, new File("./response.xml"));
        Response responseUnmarshalled = (Response) unmarshaller.unmarshal(new File("./response.xml"));

        assertEquals(response, responseUnmarshalled);
    }

}
