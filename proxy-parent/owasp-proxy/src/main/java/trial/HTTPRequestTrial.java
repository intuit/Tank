package trial;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTPRequestTrial {

    public static void main(String[] args) throws URISyntaxException, HttpException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://www.google.com/");
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            int l;
            byte[] tmp = new byte[2048];
            while ((l = instream.read(tmp)) != -1) {
                String s = new String(tmp);
                System.out.println(s);
            }
        }

        System.out.println("**************************");
        Header[] elements = response.getAllHeaders();
        for (Header header : elements) {
            System.out.println(header.getName() + " : " + header.getValue());
        }
        System.out.println();
        System.out.println("**************************");

    }

}
