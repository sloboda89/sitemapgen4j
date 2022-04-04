package org.psloboda.sitemapgenerator;

import org.psloboda.sitemapgenerator.generators.index.SitemapIndexGenerator;
import org.psloboda.sitemapgenerator.generators.index.SitemapIndexUrl;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SitemapIndexGeneratorTest {

    private static final String INDEX = """
            <?xml version="1.0" encoding="UTF-8"?>
            <sitemapindex xmlns="https://www.sitemaps.org/schemas/sitemap/0.9">
              <sitemap>
                <loc>https://www.example.com/sitemap1.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap2.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap3.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap4.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap5.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap6.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap7.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap8.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap9.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
              <sitemap>
                <loc>https://www.example.com/sitemap10.xml</loc>
                <lastmod>1970-01-01</lastmod>
              </sitemap>
            </sitemapindex>""";

    private static final String EXAMPLE = "https://www.example.com/";
    private static final W3CDateTimeFormatter ZULU = new W3CDateTimeFormatter();

    File outFile;
    SitemapIndexGenerator sig;

    @BeforeEach
    public void setUp() throws Exception {
        outFile = File.createTempFile(SitemapGeneratorTest.class.getSimpleName(), ".xml");
        outFile.deleteOnExit();
    }

    @Test
    void testTooManyUrls() {
        sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).maxUrls(10).autoValidate(true).build();
        for (int i = 0; i < 9; i++) {
            sig.addUrl(EXAMPLE + i);
        }
        sig.addUrl(EXAMPLE + "9");

        assertThrows(
                RuntimeException.class,
                () -> sig.addUrl("https://www.example.com/just-one-more"),
                "too many URLs allowed"
        );

        sig = null;
        assertTrue(outFile.delete());
        outFile = null;
    }

    @Test
    void testNoUrls() {
        sig = new SitemapIndexGenerator(EXAMPLE, outFile);

        assertThrows(RuntimeException.class, () -> sig.write(), "Allowed write with no URLs");
    }

    @Test
    void testNoUrlsEmptyIndexAllowed() {
        sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).allowEmptyIndex(true).build();
        sig.write();
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <sitemapindex xmlns="https://www.sitemaps.org/schemas/sitemap/0.9">
                </sitemapindex>""";
        String actual = TestUtil.slurpFileAndDelete(outFile);

        assertEquals(expected, actual);
        assertEquals(expected, sig.writeAsString());
    }

    @Test
    void testMaxUrls() {
        sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile)
                .autoValidate(true)
                .maxUrls(10)
                .defaultLastMod(LocalDate.EPOCH)
                .dateFormat(ZULU)
                .build();

        for (int i = 1; i <= 9; i++) {
            sig.addUrl(EXAMPLE + "sitemap" + i + ".xml");
        }

        sig.addUrl(EXAMPLE + "sitemap10.xml");
        sig.write();
        String actual = TestUtil.slurpFileAndDelete(outFile);

        assertEquals(INDEX, actual);
        assertEquals(INDEX, sig.writeAsString());
    }

    @Test
    void testOneUrl() {
        sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile).dateFormat(ZULU).autoValidate(true).build();
        SitemapIndexUrl url = new SitemapIndexUrl(EXAMPLE + "index.html", LocalDate.EPOCH);
        sig.addUrl(url);
        sig.write();
        String actual = TestUtil.slurpFileAndDelete(outFile);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <sitemapindex xmlns="https://www.sitemaps.org/schemas/sitemap/0.9">
                  <sitemap>
                    <loc>https://www.example.com/index.html</loc>
                    <lastmod>1970-01-01</lastmod>
                  </sitemap>
                </sitemapindex>""";

        assertEquals(expected, actual);
        assertEquals(expected, sig.writeAsString());
    }

    @Test
    void testAddByPrefix() {
        sig = new SitemapIndexGenerator.Options(EXAMPLE, outFile)
                .autoValidate(true)
                .defaultLastMod(LocalDate.EPOCH)
                .dateFormat(ZULU)
                .build();

        sig.addUrls("sitemap", ".xml", 10);
        sig.write();
        String actual = TestUtil.slurpFileAndDelete(outFile);

        assertEquals(INDEX, actual);
        assertEquals(INDEX, sig.writeAsString());
    }
}
