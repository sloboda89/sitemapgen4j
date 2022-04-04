package org.psloboda.sitemapgenerator;

import org.psloboda.sitemapgenerator.generators.google.news.GoogleNewsSitemapGenerator;
import org.psloboda.sitemapgenerator.generators.google.news.GoogleNewsSitemapUrl;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleNewsSitemapUrlTest {

    File dir;
    GoogleNewsSitemapGenerator wsg;

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
        W3CDateTimeFormatter dateTimeFormatter = new W3CDateTimeFormatter(W3CDateTimeFormatter.Pattern.SECOND);
        wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", dir)
                .dateFormat(dateTimeFormatter).build();
        GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl("https://www.example.com/index.html", Instant.EPOCH, "Example Title", "The Example Times", "en");
        wsg.addUrl(url);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:news="http://www.google.com/schemas/sitemap-news/0.9" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <news:news>
                      <news:publication>
                        <news:name>The Example Times</news:name>
                        <news:language>en</news:language>
                      </news:publication>
                      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>
                      <news:title>Example Title</news:title>
                    </news:news>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testKeywords() {
        W3CDateTimeFormatter dateTimeFormatter = new W3CDateTimeFormatter(W3CDateTimeFormatter.Pattern.SECOND);
        wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", dir)
                .dateFormat(dateTimeFormatter).build();
        GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl.Options("https://www.example.com/index.html", Instant.EPOCH, "Example Title", "The Example Times", "en")
                .keywords("Klaatu", "Barrata", "Nicto")
                .build();
        wsg.addUrl(url);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:news="http://www.google.com/schemas/sitemap-news/0.9" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <news:news>
                      <news:publication>
                        <news:name>The Example Times</news:name>
                        <news:language>en</news:language>
                      </news:publication>
                      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>
                      <news:title>Example Title</news:title>
                      <news:keywords>Klaatu, Barrata, Nicto</news:keywords>
                    </news:news>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testGenres() {
        W3CDateTimeFormatter dateTimeFormatter = new W3CDateTimeFormatter(W3CDateTimeFormatter.Pattern.SECOND);
        wsg = GoogleNewsSitemapGenerator.builder("https://www.example.com", dir)
                .dateFormat(dateTimeFormatter).build();
        GoogleNewsSitemapUrl url = new GoogleNewsSitemapUrl.Options("https://www.example.com/index.html", Instant.EPOCH, "Example Title", "The Example Times", "en")
                .genres("persbericht")
                .build();
        wsg.addUrl(url);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:news="http://www.google.com/schemas/sitemap-news/0.9" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <news:news>
                      <news:publication>
                        <news:name>The Example Times</news:name>
                        <news:language>en</news:language>
                      </news:publication>
                      <news:genres>persbericht</news:genres>
                      <news:publication_date>1970-01-01T00:00:00Z</news:publication_date>
                      <news:title>Example Title</news:title>
                    </news:news>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    private String writeSingleSiteMap(GoogleNewsSitemapGenerator wsg) {
        List<File> files = wsg.write();
        assertEquals(1, files.size(), "Too many files: " + files);
        assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
        return TestUtil.slurpFileAndDelete(files.get(0));
    }
}
