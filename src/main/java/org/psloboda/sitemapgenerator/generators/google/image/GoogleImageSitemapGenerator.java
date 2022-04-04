package org.psloboda.sitemapgenerator.generators.google.image;

import com.psloboda.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.utils.Constants;
import org.psloboda.sitemapgenerator.utils.UrlUtils;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import com.psv.sitemapgenerator.generators.*;

import java.io.File;
import java.net.URL;

/**
 * Builds a sitemap for Google Image search. To configure options use {@link #builder(URL, File)}
 *
 * @see <a href="https://support.google.com/webmasters/answer/183668">Manage your sitemaps</a>
 */
public class GoogleImageSitemapGenerator extends SitemapGenerator<GoogleImageSitemapUrl, GoogleImageSitemapGenerator> {

    public GoogleImageSitemapGenerator(AbstractSitemapGeneratorOptions<?> options) {
        super(options, new GoogleImageSitemapGenerator.Renderer());
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public GoogleImageSitemapGenerator(String baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public GoogleImageSitemapGenerator(URL baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public GoogleImageSitemapGenerator(String baseUrl) {
        this(new SitemapGeneratorOptions(UrlUtils.toUrl(baseUrl)));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public GoogleImageSitemapGenerator(URL baseUrl) {
        this(new SitemapGeneratorOptions(baseUrl));
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<GoogleImageSitemapGenerator> builder(URL baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleImageSitemapGenerator.class);
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<GoogleImageSitemapGenerator> builder(String baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleImageSitemapGenerator.class);
    }

    private static class Renderer implements ISitemapUrlRenderer<GoogleImageSitemapUrl> {

        public Class<GoogleImageSitemapUrl> getUrlClass() {
            return GoogleImageSitemapUrl.class;
        }

        public String getXmlNamespaces() {
            return "xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\"";
        }

        public void render(GoogleImageSitemapUrl url, StringBuilder sb, W3CDateTimeFormatter dateTimeFormatter) {
            StringBuilder tagSb = new StringBuilder();

            for (Image image : url.getImages()) {
                tagSb.append("    <image:image>\n");
                renderTag(tagSb, Constants.IMAGE_NAMESPACE, "loc", image.getUrl());
                renderTag(tagSb, Constants.IMAGE_NAMESPACE, "caption", image.getCaption());
                renderTag(tagSb, Constants.IMAGE_NAMESPACE, "title", image.getTitle());
                renderTag(tagSb, Constants.IMAGE_NAMESPACE, "geo_location", image.getGeoLocation());
                renderTag(tagSb, Constants.IMAGE_NAMESPACE, "license", image.getLicense());
                tagSb.append("    </image:image>\n");
            }

            render(url, sb, dateTimeFormatter, tagSb.toString());
        }
    }
}
