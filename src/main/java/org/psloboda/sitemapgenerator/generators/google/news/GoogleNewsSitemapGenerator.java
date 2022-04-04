package org.psloboda.sitemapgenerator.generators.google.news;

import com.psloboda.sitemapgenerator.generators.*;
import com.psv.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.exceptions.SitemapGeneratorException;
import org.psloboda.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;

import java.io.File;
import java.net.URL;

import static org.psloboda.sitemapgenerator.utils.UrlUtils.toUrl;

/**
 * Builds a sitemap for Google News.  To configure options, use {@link #builder(URL, File)}
 *
 * @author Dan Fabulich
 * @see <a href="http://www.google.com/support/news_pub/bin/answer.py?answer=74288">Creating a News Sitemap</a>
 */
public class GoogleNewsSitemapGenerator extends SitemapGenerator<GoogleNewsSitemapUrl, GoogleNewsSitemapGenerator> {

    /**
     * 1000 URLs max in a Google News sitemap.
     */
    public static final int MAX_URLS_PER_SITEMAP = 1000;

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<GoogleNewsSitemapGenerator> builder(URL baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleNewsSitemapGenerator.class)
                .maxUrls(1000);
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<GoogleNewsSitemapGenerator> builder(String baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, GoogleNewsSitemapGenerator.class)
                .maxUrls(GoogleNewsSitemapGenerator.MAX_URLS_PER_SITEMAP);
    }

    public GoogleNewsSitemapGenerator(AbstractSitemapGeneratorOptions<?> options) {
        super(options, new Renderer());
        if (options.getMaxUrls() > GoogleNewsSitemapGenerator.MAX_URLS_PER_SITEMAP) {
            throw new SitemapGeneratorException("Google News sitemaps can have only 1000 URLs per sitemap: " +
                    options.getMaxUrls());
        }
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public GoogleNewsSitemapGenerator(String baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public GoogleNewsSitemapGenerator(URL baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public GoogleNewsSitemapGenerator(String baseUrl) {
        this(new SitemapGeneratorOptions(toUrl(baseUrl)));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public GoogleNewsSitemapGenerator(URL baseUrl) {
        this(new SitemapGeneratorOptions(baseUrl));
    }

    private static class Renderer implements ISitemapUrlRenderer<GoogleNewsSitemapUrl> {

        public Class<GoogleNewsSitemapUrl> getUrlClass() {
            return GoogleNewsSitemapUrl.class;
        }

        public String getXmlNamespaces() {
            return "xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\"";
        }

        public void render(GoogleNewsSitemapUrl url, StringBuilder sb, W3CDateTimeFormatter dateTimeFormatter) {
            StringBuilder tagSb = new StringBuilder();
            tagSb.append("    <news:news>\n");
            tagSb.append("      <news:publication>\n");
            renderSubTag(tagSb, "news", "name", url.getPublication().getName());
            renderSubTag(tagSb, "news", "language", url.getPublication().getLanguage());
            tagSb.append("      </news:publication>\n");
            renderTag(tagSb, "news", "genres", url.getGenres());
            renderTag(tagSb, "news", "publication_date", dateTimeFormatter.format(url.getPublicationDate()));
            renderTag(tagSb, "news", "title", url.getTitle());
            renderTag(tagSb, "news", "keywords", url.getKeywords());
            tagSb.append("    </news:news>\n");
            render(url, sb, dateTimeFormatter, tagSb.toString());
        }

    }

}
