package com.intuit.tank.http.multipart;

import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Base64;
import java.util.stream.Stream;

public class MultiPartRequestTest {
    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of(
                        "-----------------------------10681713301939738037227942752"
                                + "\r\n"
                                + "Content-Disposition: form-data; name=\"createNewScriptForm\""
                                + "\r\n"
                                + "\r\n"
                                + "createNewScriptForm "
                                + "\r\n"
                                + "-----------------------------10681713301939738037227942752"
                                + "\r\n"
                                + "Content-Disposition: form-data; name=\"createNewScriptForm:j_idt56\"; filename=\"test.csv\""
                                + "\r\n"
                                + "Content-Type: text/csv" + "\r\n"
                                + "\r\n"
                                + "1" + "\r\n"
                                + "2" + "\r\n"
                                + "3" + "\r\n"
                                + "-----------------------------10681713301939738037227942752" + "\r\n"
                                + "Content-Disposition: form-data; name=\"createNewScriptForm:saveBtn\"" + "\r\n"
                                + "\r\n"
                                + "\r\n"
                                + "-----------------------------10681713301939738037227942752" + "\r\n"
                                + "Content-Disposition: form-data; name=\"javax.faces.ViewState\"" + "\r\n"
                                + "\r\n"
                                + "-1346138416504364622:5284803266475066700" + "\r\n"
                                + "-----------------------------10681713301939738037227942752--" + "\r\n", 4 ),
                Arguments.of(
                        "----AaB03x\r\n"
                        + "Content-Disposition: form-data; name=\"submit-name\"\r\n"
                        + "\r\n"
                        + "Larry\r\n"
                        + "----AaB03x\r\n"
                        + "Content-Disposition: form-data; name=\"files\"; filename=\"file1.txt\"\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "HELLO WORLD!\r\n"
                        + "----AaB03x--\r\n", 2 )
        );
    }

    @ParameterizedTest
    @Tag(TestGroups.FUNCTIONAL)
    @MethodSource("data")
    public void test(String body, int numParts) {
        MultiPartRequest multiPartRequest = new MultiPartRequest(null, null);
        multiPartRequest.setBody(Base64.getEncoder().encodeToString(body.getBytes()));
    }

}
