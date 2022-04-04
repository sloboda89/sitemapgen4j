package org.psloboda.sitemapgenerator;

import org.psloboda.sitemapgenerator.enums.ChangeFreq;
import org.psloboda.sitemapgenerator.generators.index.SitemapIndexGenerator;
import org.psloboda.sitemapgenerator.generators.web.WebSitemapGenerator;
import org.psloboda.sitemapgenerator.generators.web.WebSitemapUrl;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TutorialExampleTest {

    File myDir;
    File myFile;

    @BeforeEach
    public void setUp() throws Exception {
        myDir = File.createTempFile(TutorialExampleTest.class.getSimpleName(), "");
        assertTrue(myDir.delete());
        assertTrue(myDir.mkdir());
        myDir.deleteOnExit();
        myFile = new File(myDir, "sitemap_index.xml");
    }

    @AfterEach
    public void tearDown() {
        Optional.ofNullable(myDir).map(File::listFiles)
                .stream()
                .flatMap(Arrays::stream)
                .forEach(file -> {
                    file.deleteOnExit();
                    assertTrue(file.delete());
                });
        assertTrue(myDir.delete());
        myDir = null;
    }

    @Test
    void testGettingStarted() {
        WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
        wsg.addUrl("https://www.example.com/index.html"); // repeat multiple times

        assertNotNull(wsg.write());
    }

    @Test
    void testConfiguringWsgOptions() {
        WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
                .gzip(true).build(); // enable gzipped output
        wsg.addUrl("https://www.example.com/index.html");

        assertNotNull(wsg.write());
    }

    @Test
    void testConfiguringUrlOptions() {
        WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
        WebSitemapUrl url = new WebSitemapUrl.Options("https://www.example.com/index.html")
                .lastMod(LocalDate.now())
                .priority(1.0)
                .changeFreq(ChangeFreq.HOURLY)
                .build();

        // this will configure the URL with lastmod=now, priority=1.0, changefreq=hourly
        wsg.addUrl(url);

        assertNotNull(wsg.write());
    }

    @Test
    void testConfiguringDateFormat() {
        W3CDateTimeFormatter dateTimeFormatter = new W3CDateTimeFormatter(W3CDateTimeFormatter.Pattern.DAY); // e.g. 2008-01-29
        WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
                .dateFormat(dateTimeFormatter).build(); // actually use the configured dateTimeFormatter
        wsg.addUrl("https://www.example.com/index.html");

        assertNotNull(wsg.write());
    }

    @Test
    void testLotsOfUrlsWsg() {
        WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.example.com", myDir);
        for (int i = 0; i < 60000; i++) wsg.addUrl("https://www.example.com/index.html");

        assertNotNull(wsg.write());
        assertNotNull(wsg.writeSitemapsWithIndex()); // generate the sitemap_index.xml
    }

    @Test
    void testLotsOfUrlsSig() {
        WebSitemapGenerator wsg;
        // generate foo sitemap
        wsg = WebSitemapGenerator.builder("https://www.example.com", myDir).fileNamePrefix("foo").build();
        for (int i = 0; i < 5; i++) wsg.addUrl("https://www.example.com/foo" + i + ".html");
        wsg.write();
        // generate bar sitemap
        wsg = WebSitemapGenerator.builder("https://www.example.com", myDir).fileNamePrefix("bar").build();
        for (int i = 0; i < 5; i++) wsg.addUrl("https://www.example.com/bar" + i + ".html");

        assertNotNull(wsg.write());

        // generate sitemap index for foo + bar
        SitemapIndexGenerator sig = new SitemapIndexGenerator("https://www.example.com", myFile);
        sig.addUrl("https://www.example.com/foo.html");
        sig.addUrl("https://www.example.com/bar.html");
        sig.write();
    }

    @Test
    void testAutoValidate() {
        WebSitemapGenerator wsg = WebSitemapGenerator.builder("https://www.example.com", myDir)
                .autoValidate(true).build(); // validate the sitemap after writing
        wsg.addUrl("https://www.example.com/index.html");

        assertNotNull(wsg.write());
    }
}
