package org.psloboda.sitemapgenerator.generators.google.video;

import com.psloboda.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.utils.Constants;
import org.psloboda.sitemapgenerator.utils.UrlUtils;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import com.psv.sitemapgenerator.generators.*;

import java.io.File;
import java.net.URL;

/**
 * Builds a sitemap for Google Video search.  To configure options, use {@link #builder(URL, File)}
 *
 * @author Dan Fabulich
 * @see <a href="http://www.google.com/support/webmasters/bin/answer.py?answer=80472">Creating Video Sitemaps</a>
 */
public class GoogleVideoSitemapGenerator extends SitemapGenerator<GoogleVideoSitemapUrl, GoogleVideoSitemapGenerator> {

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<GoogleVideoSitemapGenerator> builder(URL baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleVideoSitemapGenerator.class);
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<GoogleVideoSitemapGenerator> builder(String baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleVideoSitemapGenerator.class);
    }

    public GoogleVideoSitemapGenerator(AbstractSitemapGeneratorOptions<?> options) {
        super(options, new Renderer());
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public GoogleVideoSitemapGenerator(String baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public GoogleVideoSitemapGenerator(URL baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public GoogleVideoSitemapGenerator(String baseUrl) {
        this(new SitemapGeneratorOptions(UrlUtils.toUrl(baseUrl)));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public GoogleVideoSitemapGenerator(URL baseUrl) {
        this(new SitemapGeneratorOptions(baseUrl));
    }

    private static class Renderer implements ISitemapUrlRenderer<GoogleVideoSitemapUrl> {

        public Class<GoogleVideoSitemapUrl> getUrlClass() {
            return GoogleVideoSitemapUrl.class;
        }

        public String getXmlNamespaces() {
            return "xmlns:video=\"http://www.google.com/schemas/sitemap-video/1.1\"";
        }

        public void render(GoogleVideoSitemapUrl url, StringBuilder sb, W3CDateTimeFormatter dateTimeFormatter) {
            StringBuilder tagSb = new StringBuilder();
            tagSb.append("    <video:video>\n");
            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "content_loc", url.getContentUrl());

            if (url.getPlayerUrl() != null) {
                tagSb.append("      <video:player_loc allow_embed=\"");
                tagSb.append(url.getAllowEmbed());
                tagSb.append("\">");
                tagSb.append(url.getPlayerUrl());
                tagSb.append("</video:player_loc>\n");
            }

            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "thumbnail_loc", url.getThumbnailUrl());
            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "title", url.getTitle());
            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "description", url.getDescription());
            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "rating", url.getRating());
            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "view_count", url.getViewCount());

            if (url.getPublicationDate() != null) {
                renderTag(tagSb, Constants.VIDEO_NAMESPACE, "publication_date", dateTimeFormatter.format(url.getPublicationDate()));
            }

            if (url.getTags() != null) {
                for (String tag : url.getTags()) {
                    renderTag(tagSb, Constants.VIDEO_NAMESPACE, "tag", tag);
                }
            }

            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "category", url.getCategory());
            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "family_friendly", url.getFamilyFriendly());
            renderTag(tagSb, Constants.VIDEO_NAMESPACE, "duration", url.getDurationInSeconds());
            tagSb.append("    </video:video>\n");

            render(url, sb, dateTimeFormatter, tagSb.toString());
        }
    }
}
