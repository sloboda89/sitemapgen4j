package org.psloboda.sitemapgenerator.generators.index;

import org.psloboda.sitemapgenerator.utils.UrlUtils;

import java.net.URL;
import java.time.temporal.Temporal;

/**
 * Represents a single sitemap for inclusion in a sitemap index.
 *
 * @author Dan Fabulich
 */
public class SitemapIndexUrl {
    final URL url;
    final Temporal lastMod;

    /**
     * Configures the sitemap URL with a specified lastMod
     */
    public SitemapIndexUrl(URL url, Temporal lastMod) {
        this.url = url;
        this.lastMod = lastMod;
    }

    /**
     * Configures the sitemap URL with a specified lastMod
     */
    public SitemapIndexUrl(String url, Temporal lastMod) {
        this(UrlUtils.toUrl(url), lastMod);
    }

    /**
     * Configures the sitemap URL with no specified lastMod; we'll use {@link SitemapIndexGenerator.Options#defaultLastMod(Temporal)} or leave it blank if no default is specified
     */
    public SitemapIndexUrl(URL url) {
        this(url, null);
    }

    /**
     * Configures the sitemap URL with no specified lastMod; we'll use {@link SitemapIndexGenerator.Options#defaultLastMod(Temporal)} or leave it blank if no default is specified
     */
    public SitemapIndexUrl(String url) {
        this(UrlUtils.toUrl(url));
    }
}
