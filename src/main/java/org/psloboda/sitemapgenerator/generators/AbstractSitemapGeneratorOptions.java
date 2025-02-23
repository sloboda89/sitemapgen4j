package org.psloboda.sitemapgenerator.generators;

import org.psloboda.sitemapgenerator.exceptions.SitemapGeneratorException;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;

import java.io.File;
import java.net.URL;

// that weird thing with generics is so sub-classed objects will return themselves
// It makes sense, I swear! http://madbean.com/2004/mb2004-3/
public abstract class AbstractSitemapGeneratorOptions<T extends AbstractSitemapGeneratorOptions<T>> {
    protected File baseDir;
    protected URL baseUrl;
    protected String fileNamePrefix = "sitemap";
    protected boolean allowEmptySitemap = false;
    protected boolean allowMultipleSitemaps = true;
    protected String suffixStringPattern; // this will store some type of string pattern suitable per needs.
    protected W3CDateTimeFormatter dateTimeFormatter;
    protected int maxUrls = SitemapGenerator.MAX_URLS_PER_SITEMAP;
    protected boolean autoValidate = false;
    protected boolean gzip = false;

    AbstractSitemapGeneratorOptions(URL baseUrl, File baseDir) {
        if (baseUrl == null) throw new NullPointerException("baseUrl may not be null");
        this.baseDir = baseDir;
        this.baseUrl = baseUrl;
    }

    AbstractSitemapGeneratorOptions(URL baseUrl) {
        this(baseUrl, null);
    }

    /**
     * The prefix of the name of the sitemaps we'll create; by default this is "sitemap"
     */
    public T fileNamePrefix(String fileNamePrefix) {
        if (fileNamePrefix == null) throw new NullPointerException("fileNamePrefix may not be null");
        this.fileNamePrefix = fileNamePrefix;
        return getThis();
    }

    public T suffixStringPattern(String pattern) {
        this.suffixStringPattern = pattern;
        return getThis();
    }

    /**
     * Permit writing a sitemap that contains no URLs.
     *
     * @param allowEmpty {@code true} if an empty sitemap is permissible
     * @return this instance, for chaining
     */
    public T allowEmptySitemap(boolean allowEmpty) {
        this.allowEmptySitemap = allowEmpty;
        return getThis();
    }

    /**
     * When more than the maximum number of URLs are passed in, should we split into multiple sitemaps automatically, or just throw an exception?
     */
    public T allowMultipleSitemaps(boolean allowMultipleSitemaps) {
        this.allowMultipleSitemaps = allowMultipleSitemaps;
        return getThis();
    }

    /**
     * The date formatter, typically configured with a {@link W3CDateTimeFormatter.Pattern} and/or a time zone
     */
    public T dateFormat(W3CDateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        return getThis();
    }

    /**
     * The maximum number of URLs to allow per sitemap; the default is the
     * maximum allowed (50,000), but you can decrease it if you wish (to make
     * your auto-generated sitemaps smaller)
     */
    public T maxUrls(int maxUrls) {
        if (maxUrls > SitemapGenerator.MAX_URLS_PER_SITEMAP) {
            throw new SitemapGeneratorException("You can only have 50,000 URLs per sitemap; to use more, allowMultipleSitemaps and generate a sitemap index. You asked for " + maxUrls);
        }
        this.maxUrls = maxUrls;
        return getThis();
    }

    /**
     * Validate the sitemaps automatically after writing them; this takes time (and may fail for Google-specific sitemaps)
     */
    public T autoValidate(boolean autoValidate) {
        this.autoValidate = autoValidate;
        return getThis();
    }

    /**
     * Gzip the sitemaps after they are written to disk
     */
    public T gzip(boolean gzip) {
        this.gzip = gzip;
        return getThis();
    }

    @SuppressWarnings("unchecked")
    T getThis() {
        return (T) this;
    }

    public int getMaxUrls() {
        return maxUrls;
    }
}
