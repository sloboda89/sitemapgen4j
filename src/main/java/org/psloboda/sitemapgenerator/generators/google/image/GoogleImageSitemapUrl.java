package org.psloboda.sitemapgenerator.generators.google.image;

import org.psloboda.sitemapgenerator.exceptions.SitemapGeneratorException;
import org.psloboda.sitemapgenerator.generators.web.WebSitemapUrl;
import org.psloboda.sitemapgenerator.generators.AbstractSitemapUrlOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * One configurable Google Image Search URL.  To configure, use {@link Options}
 *
 * @see Options
 * @see <a href="http://www.google.com/support/webmasters/bin/answer.py?answer=183668">Creating Image Sitemaps</a>
 */
public class GoogleImageSitemapUrl extends WebSitemapUrl {
    private static final String TOO_MANY_TAGS = "A URL cannot have more than 1000 image tags";

    private final List<Image> images;

    public GoogleImageSitemapUrl(String url) {
        this(new Options(url));
    }

    public GoogleImageSitemapUrl(URL url) {
        this(new Options(url));
    }

    public GoogleImageSitemapUrl(Options options) {
        super(options);
        this.images = options.images;
    }

    public void addImage(Image image) {
        this.images.add(image);
        if (this.images.size() > 1000) {
            throw new SitemapGeneratorException(TOO_MANY_TAGS);
        }
    }

    /**
     * Options to configure Google Extension URLs
     */
    public static class Options extends AbstractSitemapUrlOptions<GoogleImageSitemapUrl, Options> {
        private List<Image> images;


        public Options(URL url) {
            super(url, GoogleImageSitemapUrl.class);
            images = new ArrayList<>();
        }

        public Options(String url) {
            super(url, GoogleImageSitemapUrl.class);
            images = new ArrayList<>();
        }

        public Options images(List<Image> images) {
            if (images != null && images.size() > 1000) {
                throw new SitemapGeneratorException(TOO_MANY_TAGS);
            }
            this.images = images;
            return this;
        }

        public Options images(Image... images) {
            if (images.length > 1000) {
                throw new SitemapGeneratorException(TOO_MANY_TAGS);
            }
            return images(Arrays.asList(images));

        }
    }

    /**
     * Retrieves list of images
     */
    public List<Image> getImages() {
        return this.images;
    }
}
