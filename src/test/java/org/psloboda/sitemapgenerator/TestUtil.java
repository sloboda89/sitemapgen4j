package org.psloboda.sitemapgenerator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {
    public static String slurpFileAndDelete(File file) {
        file.deleteOnExit();
        StringBuilder sb = new StringBuilder();

        try {
            FileReader reader = new FileReader(file);
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(file.delete());

        return sb.toString();
    }
}
