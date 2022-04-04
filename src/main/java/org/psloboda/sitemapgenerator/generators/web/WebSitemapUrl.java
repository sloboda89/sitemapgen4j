package org.psloboda.sitemapgenerator.generators.web;

import org.psloboda.sitemapgenerator.utils.UrlUtils;
import org.psloboda.sitemapgenerator.enums.ChangeFreq;
import org.psloboda.sitemapgenerator.generators.AbstractSitemapUrlOptions;
import org.psloboda.sitemapgenerator.generators.ISitemapUrl;

import java.net.URL;
import java.time.temporal.Temporal;

/**
 * Encapsulates a single URL to be inserted into a Web sitemap (as opposed to a Geo sitemap, a Mobile sitemap, a Video sitemap, etc which are Google specific).
 * Specifying a lastMod, changeFreq, or priority is optional; you specify those by using an Options object.
 *
 * @author Dan Fabulich
 * @see Options
 */
public class WebSitemapUrl implements ISitemapUrl {
    private final URL url;
    private final Temporal lastMod;
    private final ChangeFreq changeFreq;
    private final Double priority;

    /**
     * Encapsulates a single simple URL
     */
    public WebSitemapUrl(String url) {
        this(UrlUtils.toUrl(url));
    }

    /**
     * Encapsulates a single simple URL
     */
    public WebSitemapUrl(URL url) {
        this.url = url;
        this.lastMod = null;
        this.changeFreq = null;
        this.priority = null;
    }

    /**
     * Creates an URL with configured options
     */
    public WebSitemapUrl(Options options) {
        this((AbstractSitemapUrlOptions<?, ?>) options);
    }

    protected WebSitemapUrl(AbstractSitemapUrlOptions<?, ?> options) {
        this.url = options.getUrl();
        this.lastMod = options.getLastMod();
        this.changeFreq = options.getChangeFreq();
        this.priority = options.getPriority();
    }

    /**
     * Retrieves the {@link Options#lastMod(Temporal)}
     */
    public Temporal getLastMod() {
        return lastMod;
    }

    /**
     * Retrieves the {@link Options#changeFreq(ChangeFreq)}
     */
    public ChangeFreq getChangeFreq() {
        return changeFreq;
    }

    /**
     * Retrieves the {@link Options#priority(Double)}
     */
    public Double getPriority() {
        return priority;
    }

    /**
     * Retrieves the url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Options to configure web sitemap URLs
     */
    public static class Options extends AbstractSitemapUrlOptions<WebSitemapUrl, Options> {

        /**
         * Configure this URL
         */
        public Options(String url) {
            this(UrlUtils.toUrl(url));
        }

        /**
         * Configure this URL
         */
        public Options(URL url) {
            super(url, WebSitemapUrl.class);
        }

    }
}
