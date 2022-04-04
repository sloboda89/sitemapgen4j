package org.psloboda.sitemapgenerator;

import org.psloboda.sitemapgenerator.generators.google.video.GoogleVideoSitemapGenerator;
import org.psloboda.sitemapgenerator.generators.google.video.GoogleVideoSitemapUrl;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GoogleVideoSitemapUrlTest {

    private static final URL LANDING_URL = newURL("https://www.example.com/index.html");
    private static final URL CONTENT_URL = newURL("https://www.example.com/index.flv");
    File dir;
    GoogleVideoSitemapGenerator wsg;

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
        wsg = new GoogleVideoSitemapGenerator("https://www.example.com", dir);
        GoogleVideoSitemapUrl url = new GoogleVideoSitemapUrl(LANDING_URL, CONTENT_URL);
        wsg.addUrl(url);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:video="http://www.google.com/schemas/sitemap-video/1.1" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <video:video>
                      <video:content_loc>https://www.example.com/index.flv</video:content_loc>
                    </video:video>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testOptions() throws Exception {
        W3CDateTimeFormatter dateTimeFormatter = new W3CDateTimeFormatter();
        wsg = GoogleVideoSitemapGenerator.builder("https://www.example.com", dir)
                .dateFormat(dateTimeFormatter).build();

        GoogleVideoSitemapUrl url = new GoogleVideoSitemapUrl.Options(LANDING_URL, CONTENT_URL)
                .playerUrl(new URL("https://www.example.com/index.swf"), true)
                .thumbnailUrl(new URL("https://www.example.com/thumbnail.jpg"))
                .title("This is a video!")
                .description("A great video about dinosaurs")
                .rating(5.0)
                .viewCount(500000)
                .publicationDate(LocalDate.EPOCH)
                .tags("dinosaurs", "example", "awesome")
                .category("example")
                .familyFriendly(false)
                .durationInSeconds(60 * 30)
                .build();

        wsg.addUrl(url);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:video="http://www.google.com/schemas/sitemap-video/1.1" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <video:video>
                      <video:content_loc>https://www.example.com/index.flv</video:content_loc>
                      <video:player_loc allow_embed="Yes">https://www.example.com/index.swf</video:player_loc>
                      <video:thumbnail_loc>https://www.example.com/thumbnail.jpg</video:thumbnail_loc>
                      <video:title>This is a video!</video:title>
                      <video:description>A great video about dinosaurs</video:description>
                      <video:rating>5.0</video:rating>
                      <video:view_count>500000</video:view_count>
                      <video:publication_date>1970-01-01</video:publication_date>
                      <video:tag>dinosaurs</video:tag>
                      <video:tag>example</video:tag>
                      <video:tag>awesome</video:tag>
                      <video:category>example</video:category>
                      <video:family_friendly>No</video:family_friendly>
                      <video:duration>1800</video:duration>
                    </video:video>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testLongTitle() {
        GoogleVideoSitemapUrl.Options options = new GoogleVideoSitemapUrl.Options(LANDING_URL, CONTENT_URL);

        assertThrows(
                RuntimeException.class,
                () -> options.title("Unfortunately, this title is far longer than 100 characters by virtue of having a" +
                        " great deal to say but not much content."),
                "Long title inappropriately allowed"
        );
    }

    @Test
    void testLongDescription() {
        GoogleVideoSitemapUrl.Options options = new GoogleVideoSitemapUrl.Options(LANDING_URL, CONTENT_URL);
        String description = "x".repeat(2049);

        assertThrows(
                RuntimeException.class,
                () -> options.description(description),
                "Long description inappropriately allowed"
        );
    }

    @Test
    void testWrongRating() {
        GoogleVideoSitemapUrl.Options o = new GoogleVideoSitemapUrl.Options(LANDING_URL, CONTENT_URL);

        assertThrows(RuntimeException.class, () -> o.rating(-1.0), "Negative rating allowed");
        assertThrows(RuntimeException.class, () -> o.rating(10.0), ">5 rating allowed");
    }

    @Test
    void testTooManyTags() {
        int maxTags = 32;
        String[] tags = new String[maxTags + 1];
        for (int i = 0; i < maxTags + 1; i++) {
            tags[i] = "tag" + i;
        }
        GoogleVideoSitemapUrl.Options options = new GoogleVideoSitemapUrl.Options(LANDING_URL, CONTENT_URL).tags(tags);

        Assertions.assertThrows(
                RuntimeException.class,
                options::build,
                "Too many tags allowed"
        );
    }

    @Test
    void testLongCategory() {
        GoogleVideoSitemapUrl.Options options = new GoogleVideoSitemapUrl.Options(LANDING_URL, CONTENT_URL);
        String category = "x".repeat(257);

        assertThrows(
                RuntimeException.class,
                () -> options.category(category),
                "Long category inappropriately allowed"
        );
    }

    @Test
    void testWrongDuration() {
        GoogleVideoSitemapUrl.Options o = new GoogleVideoSitemapUrl.Options(LANDING_URL, CONTENT_URL);

        assertThrows(RuntimeException.class, () -> o.durationInSeconds(-1), "Negative duration allowed");
        assertThrows(RuntimeException.class, () -> o.durationInSeconds(Integer.MAX_VALUE), ">8hr duration allowed");
    }

    private String writeSingleSiteMap(GoogleVideoSitemapGenerator wsg) {
        List<File> files = wsg.write();
        assertEquals(1, files.size(), "Too many files: " + files);
        assertEquals("sitemap.xml", files.get(0).getName(), "Sitemap misnamed");
        return TestUtil.slurpFileAndDelete(files.get(0));
    }

    private static URL newURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        }
        return null;
    }
}
