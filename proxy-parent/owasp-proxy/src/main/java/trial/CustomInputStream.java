package trial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomInputStream {

    private static String startTag = "<RootWrapper>";
    private static String endTag = "</RootWrapper>";

    public static File getNewFile(File file) throws IOException {

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        List<String> contents = new ArrayList<String>();
        String s;
        int i = 0;
        while ((s = br.readLine()) != null) {
            contents.add(s);

            if (s.endsWith(">"))
            {
                if (i == 0) {
                    contents.add(startTag);
                }
                i++;
            }
        }
        contents.add(endTag);
        br.close();
        fr.close();

        File tmpFile = File.createTempFile("temporary", ".xml");

        FileWriter fw = new FileWriter(tmpFile);
        BufferedWriter bw = new BufferedWriter(fw);
        for (String string : contents) {
            bw.write(string);
            System.out.println(string);
        }
        bw.close();
        fw.close();

        return tmpFile;
    }

}
