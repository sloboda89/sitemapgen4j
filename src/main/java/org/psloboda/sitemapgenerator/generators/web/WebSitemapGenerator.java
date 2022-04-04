package org.psloboda.sitemapgenerator.generators.web;

import com.psloboda.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.utils.UrlUtils;
import com.psv.sitemapgenerator.generators.*;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;

import java.io.File;
import java.net.URL;

/**
 * Generates a regular old sitemap (USE THIS CLASS FIRST).  To configure options, use {@link #builder(URL, File)}
 *
 * @author Dan Fabulich
 */
public class WebSitemapGenerator extends SitemapGenerator<WebSitemapUrl, WebSitemapGenerator> {

    public WebSitemapGenerator(AbstractSitemapGeneratorOptions<?> options) {
        super(options, new Renderer());
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<WebSitemapGenerator> builder(URL baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, WebSitemapGenerator.class);
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<WebSitemapGenerator> builder(String baseUrl, File baseDir) {
        return new SitemapGeneratorBuilder<>(baseUrl, baseDir, WebSitemapGenerator.class);
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public WebSitemapGenerator(String baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(UrlUtils.toUrl(baseUrl), baseDir));
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public WebSitemapGenerator(URL baseUrl, File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public WebSitemapGenerator(String baseUrl) {
        this(new SitemapGeneratorOptions(UrlUtils.toUrl(baseUrl)));
    }

    /**
     * Configures the generator with a base URL and a null directory. The object constructed
     * is not intended to be used to write to files. Rather, it is intended to be used to obtain
     * XML-formatted strings that represent sitemaps.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     */
    public WebSitemapGenerator(URL baseUrl) {
        this(new SitemapGeneratorOptions(baseUrl));
    }

    private static class Renderer implements ISitemapUrlRenderer<WebSitemapUrl> {

        public Class<WebSitemapUrl> getUrlClass() {
            return WebSitemapUrl.class;
        }

        public String getXmlNamespaces() {
            return null;
        }

        public void render(WebSitemapUrl url, StringBuilder sb, W3CDateTimeFormatter dateTimeFormatter) {
            render(url, sb, dateTimeFormatter, null);
        }

    }
}
