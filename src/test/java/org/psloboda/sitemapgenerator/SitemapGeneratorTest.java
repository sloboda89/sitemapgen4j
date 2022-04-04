package org.psloboda.sitemapgenerator;

import org.psloboda.sitemapgenerator.enums.ChangeFreq;
import org.psloboda.sitemapgenerator.generators.SitemapGenerator;
import org.psloboda.sitemapgenerator.generators.web.WebSitemapGenerator;
import org.psloboda.sitemapgenerator.generators.web.WebSitemapUrl;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

class SitemapGeneratorTest {
    private static final String SITEMAP_PLUS_ONE = """
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
              <url>
                <loc>https://www.example.com/just-one-more</loc>
              </url>
            </urlset>""";
    private static final String SITEMAP1 = """
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
              <url>
                <loc>https://www.example.com/0</loc>
              </url>
              <url>
                <loc>https://www.example.com/1</loc>
              </url>
              <url>
                <loc>https://www.example.com/2</loc>
              </url>
              <url>
                <loc>https://www.example.com/3</loc>
              </url>
              <url>
                <loc>https://www.example.com/4</loc>
              </url>
              <url>
                <loc>https://www.example.com/5</loc>
              </url>
              <url>
                <loc>https://www.example.com/6</loc>
              </url>
              <url>
                <loc>https://www.example.com/7</loc>
              </url>
              <url>
                <loc>https://www.example.com/8</loc>
              </url>
              <url>
                <loc>https://www.example.com/9</loc>
              </url>
            </urlset>""";
    private static final String SITEMAP2 = """
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
              <url>
                <loc>https://www.example.com/10</loc>
              </url>
              <url>
                <loc>https://www.example.com/11</loc>
              </url>
              <url>
                <loc>https://www.example.com/12</loc>
              </url>
              <url>
                <loc>https://www.example.com/13</loc>
              </url>
              <url>
                <loc>https://www.example.com/14</loc>
              </url>
              <url>
                <loc>https://www.example.com/15</loc>
              </url>
              <url>
                <loc>https://www.example.com/16</loc>
              </url>
              <url>
                <loc>https://www.example.com/17</loc>
              </url>
              <url>
                <loc>https://www.example.com/18</loc>
              </url>
              <url>
                <loc>https://www.example.com/19</loc>
              </url>
            </urlset>""";

    File dir;
    WebSitemapGenerator wsg;

    @BeforeEach
    public void setUp() throws Exception {
        dir = File.createTempFile(this.getClass().getSimpleName(), "");
        assertTrue(dir.delete());
        assertTrue(dir.mkdir());
        dir.deleteOnExit();
    }

    @AfterEach
    public void tearDown() {
        wsg = null;

        Optional.ofNullable(dir).map(File::listFiles)
                .stream()
                .flatMap(Arrays::stream)
                .forEach(file -> {
                    file.deleteOnExit();
                    assertTrue(file.delete());
                });

        assertTrue(dir.delete());
        dir = null;
    }

    @Test
    void testSimpleUrl() {
        wsg = new WebSitemapGenerator("https://www.example.com", dir);
        wsg.addUrl("https://www.example.com/index.html");
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testTwoUrl() {
        wsg = new WebSitemapGenerator("https://www.example.com", dir);
        wsg.addUrls("https://www.example.com/index.html", "https://www.example.com/index2.html");
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                  </url>
                  <url>
                    <loc>https://www.example.com/index2.html</loc>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testAllUrlOptions() {
        W3CDateTimeFormatter dateTimeFormatter = new W3CDateTimeFormatter();
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).dateFormat(dateTimeFormatter).autoValidate(true).build();
        WebSitemapUrl url = new WebSitemapUrl.Options("https://www.example.com/index.html")
                .changeFreq(ChangeFreq.DAILY)
                .lastMod(LocalDate.EPOCH)
                .priority(1.0)
                .build();

        wsg.addUrl(url);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <lastmod>1970-01-01</lastmod>
                    <changefreq>daily</changefreq>
                    <priority>1.0</priority>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testBadUrl() {
        wsg = new WebSitemapGenerator("https://www.example.com", dir);

        assertThrows(
                RuntimeException.class,
                () -> wsg.addUrl("https://example.com/index.html"),
                "wrong domain allowed to be added"
        );
    }

    @Test
    void testSameDomainDifferentSchemeOK() {
        wsg = new WebSitemapGenerator("https://www.example.com", dir);

        wsg.addUrl("https://www.example.com/index.html");

        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testDoubleWrite() {
        testSimpleUrl();

        assertThrows(RuntimeException.class, () -> wsg.write(), "Double-write is not allowed");
    }

    @Test
    void testEmptyWrite() {
        wsg = new WebSitemapGenerator("https://www.example.com", dir);

        assertThrows(RuntimeException.class, () -> wsg.write(), "Empty write is not allowed");
    }

    @Test
    void testSuffixPresent() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).suffixStringPattern("01").build();
        wsg.addUrl("https://www.example.com/url1");
        wsg.addUrl("https://www.example.com/url2");
        List<File> files = wsg.write();

        assertEquals("sitemap01.xml", files.get(0).getName(), "Sitemap has a suffix now");
    }

    @Test
    void testNullSuffixPassed() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).suffixStringPattern("").build();
        wsg.addUrl("https://www.example.com/url1");
        wsg.addUrl("https://www.example.com/url2");
        List<File> files = wsg.write();

        assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap has a suffix now");
    }

    @Test
    void testTooManyUrls() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).allowMultipleSitemaps(false).build();
        for (int i = 0; i < SitemapGenerator.MAX_URLS_PER_SITEMAP; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }

        assertThrows(
                RuntimeException.class,
                () -> wsg.addUrl("https://www.example.com/just-one-more"),
                "too many URLs allowed"
        );
    }

    @Test
    void testMaxUrlsPlusOne() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).autoValidate(true).maxUrls(10).build();

        for (int i = 0; i < 9; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }

        wsg.addUrl("https://www.example.com/9");
        wsg.addUrl("https://www.example.com/just-one-more");

        String actual = TestUtil.slurpFileAndDelete(new File(dir, "sitemap1.xml"));

        assertEquals(SITEMAP1, actual, "sitemap1 didn't match");

        List<File> files = wsg.write();

        assertEquals(2, files.size());
        assertEquals("sitemap1.xml", files.get(0).getName(), "First sitemap was misnamed");
        assertEquals("sitemap2.xml", files.get(1).getName(), "Second sitemap was misnamed");

        actual = TestUtil.slurpFileAndDelete(files.get(1));

        assertEquals(SITEMAP_PLUS_ONE, actual, "sitemap2 didn't match");
    }

    @Test
    void testMaxUrls() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).autoValidate(true).maxUrls(10).build();
        for (int i = 0; i < 9; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }
        wsg.addUrl("https://www.example.com/9");
        String actual = writeSingleSiteMap(wsg);

        assertEquals(SITEMAP1, actual, "sitemap didn't match");
    }

    @Test
    void testMaxUrlsTimesTwo() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).autoValidate(true).maxUrls(10).build();
        for (int i = 0; i < 19; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }
        wsg.addUrl("https://www.example.com/19");
        List<File> files = wsg.write();

        assertEquals(2, files.size());
        assertEquals("sitemap1.xml", files.get(0).getName(), "First sitemap was misnamed");
        assertEquals("sitemap2.xml", files.get(1).getName(), "Second sitemap was misnamed");

        String actual = TestUtil.slurpFileAndDelete(files.get(0));
        assertEquals(SITEMAP1, actual, "sitemap1 didn't match");

        actual = TestUtil.slurpFileAndDelete(files.get(1));
        assertEquals(SITEMAP2, actual, "sitemap2 didn't match");
    }

    @Test
    void testMaxUrlsTimesTwoPlusOne() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir).autoValidate(true).maxUrls(10).build();
        for (int i = 0; i < 19; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }
        wsg.addUrl("https://www.example.com/19");
        wsg.addUrl("https://www.example.com/just-one-more");
        List<File> files = wsg.write();

        assertEquals(3, files.size());
        assertEquals("sitemap1.xml", files.get(0).getName(), "First sitemap was misnamed");
        assertEquals("sitemap2.xml", files.get(1).getName(), "Second sitemap was misnamed");
        assertEquals("sitemap3.xml", files.get(2).getName(), "Third sitemap was misnamed");

        String expected = SITEMAP1;
        String actual = TestUtil.slurpFileAndDelete(files.get(0));
        assertEquals(expected, actual, "sitemap1 didn't match");

        expected = SITEMAP2;
        actual = TestUtil.slurpFileAndDelete(files.get(1));
        assertEquals(expected, actual, "sitemap2 didn't match");

        expected = SITEMAP_PLUS_ONE;
        actual = TestUtil.slurpFileAndDelete(files.get(2));
        assertEquals(expected, actual, "sitemap3 didn't match");
    }

    @Test
    void testGzip() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir)
                .gzip(true).build();
        for (int i = 0; i < 9; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }
        wsg.addUrl("https://www.example.com/9");
        List<File> files = wsg.write();

        assertEquals(1, files.size(), "Too many files: " + files);
        assertEquals("sitemap.xml.gz", files.get(0).getName(), "Sitemap misnamed");

        File file = files.get(0);
        file.deleteOnExit();
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fileStream = new FileInputStream(file);
            GZIPInputStream gzipStream = new GZIPInputStream(fileStream);
            InputStreamReader reader = new InputStreamReader(gzipStream);
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(file.delete());

        String actual = sb.toString();

        assertEquals(SITEMAP1, actual, "sitemap didn't match");
    }

    @Test
    void testBaseDirIsNullThrowsNullPointerException() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", null)
                .autoValidate(true)
                .maxUrls(10)
                .build();

        wsg.addUrl("https://www.example.com/index.html");

        String message = assertThrows(
                NullPointerException.class,
                () -> wsg.write(),
                "To write to files, baseDir must not be null"
        ).getMessage();

        assertEquals("To write to files, baseDir must not be null", message, "Correct exception was not thrown");
    }

    @Test
    void testWriteAsStringsMoreThanOneString() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", null)
                .autoValidate(true)
                .maxUrls(10)
                .build();

        for (int i = 0; i < 9; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }
        wsg.addUrl("https://www.example.com/9");
        wsg.addUrl("https://www.example.com/just-one-more");
        List<String> siteMapsAsStrings = wsg.writeAsStrings();

        assertEquals(SITEMAP1, siteMapsAsStrings.get(0), "First string didn't match");
        assertEquals(SITEMAP_PLUS_ONE, siteMapsAsStrings.get(1), "Second string didn't match");
    }

    @Test
    void testWriteEmptySitemap() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir)
                .allowEmptySitemap(true)
                .build();

        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" >
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);

        assertEquals(expected, sitemap);
    }

    @Test
    void testMaxUrlsAllowingEmptyDoesNotWriteExtraSitemap() {
        wsg = WebSitemapGenerator.builder("https://www.example.com", dir)
                .allowEmptySitemap(true)
                .maxUrls(10)
                .build();

        for (int i = 0; i < 10; i++) {
            wsg.addUrl("https://www.example.com/" + i);
        }

        String sitemap = writeSingleSiteMap(wsg);

        assertEquals(SITEMAP1, sitemap);
    }

    private String writeSingleSiteMap(WebSitemapGenerator wsg) {
        List<File> files = wsg.write();
        assertEquals(1, files.size(), "Too many files: " + files);
        assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
        return TestUtil.slurpFileAndDelete(files.get(0));
    }
}
