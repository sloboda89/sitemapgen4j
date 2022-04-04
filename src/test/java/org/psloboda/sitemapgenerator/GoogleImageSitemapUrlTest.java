package org.psloboda.sitemapgenerator;

import org.psloboda.sitemapgenerator.enums.ChangeFreq;
import org.psloboda.sitemapgenerator.generators.google.image.GoogleImageSitemapGenerator;
import org.psloboda.sitemapgenerator.generators.google.image.GoogleImageSitemapUrl;
import org.psloboda.sitemapgenerator.generators.google.image.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class GoogleImageSitemapUrlTest {

    private static final URL LANDING_URL = newURL("https://www.example.com/index.html");

    File dir;
    GoogleImageSitemapGenerator wsg;

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
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        GoogleImageSitemapUrl url = new GoogleImageSitemapUrl(LANDING_URL);
        url.addImage(new Image(newURL("https://cdn.example.com/image1.jpg")));
        url.addImage(new Image(newURL("https://cdn.example.com/image2.jpg")));
        wsg.addUrl(url);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:image="http://www.google.com/schemas/sitemap-image/1.1" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <image:image>
                      <image:loc>https://cdn.example.com/image1.jpg</image:loc>
                    </image:image>
                    <image:image>
                      <image:loc>https://cdn.example.com/image2.jpg</image:loc>
                    </image:image>
                  </url>
                </urlset>""";
        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testBaseOptions() {
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        GoogleImageSitemapUrl url = new GoogleImageSitemapUrl.Options(LANDING_URL)
                .images(
                        new Image(newURL("https://cdn.example.com/image1.jpg")),
                        new Image(newURL("https://cdn.example.com/image2.jpg"))
                )
                .priority(0.5)
                .changeFreq(ChangeFreq.WEEKLY)
                .build();
        wsg.addUrl(url);

        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:image="http://www.google.com/schemas/sitemap-image/1.1" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <changefreq>weekly</changefreq>
                    <priority>0.5</priority>
                    <image:image>
                      <image:loc>https://cdn.example.com/image1.jpg</image:loc>
                    </image:image>
                    <image:image>
                      <image:loc>https://cdn.example.com/image2.jpg</image:loc>
                    </image:image>
                  </url>
                </urlset>""";

        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testImageOptions() {
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        GoogleImageSitemapUrl url = new GoogleImageSitemapUrl.Options(LANDING_URL)
                .images(new Image.ImageBuilder("https://cdn.example.com/image1.jpg")
                                .title("image1.jpg")
                                .caption("An image of the number 1")
                                .geoLocation("Pyongyang, North Korea")
                                .license("https://cdn.example.com/licenses/imagelicense.txt")
                                .build(),
                        new Image.ImageBuilder("https://cdn.example.com/image2.jpg")
                                .title("image2.jpg")
                                .caption("An image of the number 2")
                                .geoLocation("Pyongyang, North Korea")
                                .license("https://cdn.example.com/licenses/imagelicense.txt")
                                .build())
                .priority(0.5)
                .changeFreq(ChangeFreq.WEEKLY)
                .build();
        wsg.addUrl(url);

        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9" xmlns:image="http://www.google.com/schemas/sitemap-image/1.1" >
                  <url>
                    <loc>https://www.example.com/index.html</loc>
                    <changefreq>weekly</changefreq>
                    <priority>0.5</priority>
                    <image:image>
                      <image:loc>https://cdn.example.com/image1.jpg</image:loc>
                      <image:caption>An image of the number 1</image:caption>
                      <image:title>image1.jpg</image:title>
                      <image:geo_location>Pyongyang, North Korea</image:geo_location>
                      <image:license>https://cdn.example.com/licenses/imagelicense.txt</image:license>
                    </image:image>
                    <image:image>
                      <image:loc>https://cdn.example.com/image2.jpg</image:loc>
                      <image:caption>An image of the number 2</image:caption>
                      <image:title>image2.jpg</image:title>
                      <image:geo_location>Pyongyang, North Korea</image:geo_location>
                      <image:license>https://cdn.example.com/licenses/imagelicense.txt</image:license>
                    </image:image>
                  </url>
                </urlset>""";

        String sitemap = writeSingleSiteMap(wsg);
        assertEquals(expected, sitemap);
    }

    @Test
    void testTooManyImages() {
        wsg = new GoogleImageSitemapGenerator("https://www.example.com", dir);
        List<Image> images = IntStream.rangeClosed(1, 1001)
                .mapToObj(i -> new Image(newURL("https://cdn.example.com/image" + i + ".jpg")))
                .toList();

        GoogleImageSitemapUrl.Options options = new GoogleImageSitemapUrl.Options(LANDING_URL);

        assertThrows(RuntimeException.class, () -> options.images(images), "Too many images allowed");
    }

    private String writeSingleSiteMap(GoogleImageSitemapGenerator wsg) {
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
