package org.psloboda.sitemapgenerator.generators;

import org.psloboda.sitemapgenerator.enums.ChangeFreq;
import org.psloboda.sitemapgenerator.exceptions.SitemapGeneratorException;
import org.psloboda.sitemapgenerator.generators.web.WebSitemapUrl;
import org.psloboda.sitemapgenerator.utils.UrlUtils;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;

import java.net.URL;
import java.time.temporal.Temporal;

/**
 * Container for optional URL parameters
 */
//that weird thing with generics is so sub-classed objects will return themselves
//It makes sense, I swear! http://madbean.com/2004/mb2004-3/
public abstract class AbstractSitemapUrlOptions<U extends WebSitemapUrl, T extends AbstractSitemapUrlOptions<U, T>> {
    protected Temporal lastMod;
    protected ChangeFreq changeFreq;
    protected Double priority;
    protected URL url;
    protected Class<U> clazz;

    protected AbstractSitemapUrlOptions(String url, Class<U> clazz) {
        this(UrlUtils.toUrl(url), clazz);
    }

    protected AbstractSitemapUrlOptions(URL url, Class<U> clazz) {
        if (url == null) throw new NullPointerException("URL may not be null");
        this.url = url;
        this.clazz = clazz;
    }

    /**
     * The date of last modification of the file. Note that this tag is
     * separate from the If-Modified-Since (304) header the server can
     * return, and search engines may use the information from both sources
     * differently.
     */
    public T lastMod(Temporal lastMod) {
        this.lastMod = lastMod;
        return getThis();
    }

    /**
     * The date of last modification of the file. Note that this tag is
     * separate from the If-Modified-Since (304) header the server can
     * return, and search engines may use the information from both sources
     * differently.
     *
     * @see W3CDateTimeFormatter
     */
    public T lastMod(String lastMod) {
        if (lastMod.length() == 10) {
            this.lastMod = new W3CDateTimeFormatter().parseLocalDate(lastMod);
        } else {
            if (lastMod.endsWith("Z")) {
                this.lastMod = new W3CDateTimeFormatter().parseInstant(lastMod);
            } else if (lastMod.matches("-\\d{2}:\\d{2}")) {
                this.lastMod = new W3CDateTimeFormatter().parseZonedDateTime(lastMod);
            } else {
                this.lastMod = new W3CDateTimeFormatter().parseLocalDateTime(lastMod);
            }
        }

        return getThis();
    }

    /**
     * How frequently the page is likely to change. This value provides
     * general information to search engines and may not correlate exactly
     * to how often they crawl the page. The value {@link ChangeFreq#ALWAYS} should be used to
     * describe documents that change each time they are accessed. The value
     * {@link ChangeFreq#NEVER} should be used to describe archived URLs.
     *
     * <p>Please note that the
     * value of this tag is considered a <em>hint</em> and not a command. Even though
     * search engine crawlers may consider this information when making
     * decisions, they may crawl pages marked {@link ChangeFreq#HOURLY} less frequently than
     * that, and they may crawl pages marked {@link ChangeFreq#YEARLY} more frequently than
     * that. Crawlers may periodically crawl pages marked {@link ChangeFreq#NEVER} so that
     * they can handle unexpected changes to those pages.</p>
     */
    public T changeFreq(ChangeFreq changeFreq) {
        this.changeFreq = changeFreq;
        return getThis();
    }

    /**
     * <p>The priority of this URL relative to other URLs on your site. Valid
     * values range from 0.0 to 1.0. This value does not affect how your
     * pages are compared to pages on other sites-it only lets the search
     * engines know which pages you deem most important for the crawlers.</p>
     *
     * <p>The default priority of a page is 0.5.</p>
     *
     * <p>Please note that the priority you assign to a page is not likely to
     * influence the position of your URLs in a search engine's result
     * pages. Search engines may use this information when selecting between
     * URLs on the same site, so you can use this tag to increase the
     * likelihood that your most important pages are present in a search
     * index.</p>
     *
     * <p>Also, please note that assigning a high priority to all of the URLs
     * on your site is not likely to help you. Since the priority is
     * relative, it is only used to select between URLs on your site.</p>
     */
    public T priority(Double priority) {
        if (priority > 1.0) throw new IllegalArgumentException("Priority may not be greater than 1.0: " + priority);
        if (priority < 0) throw new IllegalArgumentException("Priority may not be less than 0: " + priority);
        this.priority = priority;
        return getThis();
    }

    @SuppressWarnings("unchecked")
    T getThis() {
        return (T) this;
    }

    /**
     * Return an URL based on these settings
     */
    public U build() {
        try {
            return clazz.getConstructor(getClass()).newInstance(this);
        } catch (Exception e) {
            throw new SitemapGeneratorException(e);
        }
    }

    public URL getUrl() {
        return url;
    }

    public Temporal getLastMod() {
        return lastMod;
    }

    public ChangeFreq getChangeFreq() {
        return changeFreq;
    }

    public Double getPriority() {
        return priority;
    }
}
