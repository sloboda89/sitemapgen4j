package org.psloboda.sitemapgenerator.generators.index;

import org.psloboda.sitemapgenerator.utils.UrlUtils;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import org.psloboda.sitemapgenerator.exceptions.SitemapGeneratorException;
import org.psloboda.sitemapgenerator.generators.SitemapValidator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.ArrayList;

/**
 * Builds a sitemap index, which points only to other sitemaps.
 *
 * @author Dan Fabulich
 */
public class SitemapIndexGenerator {
    private final URL baseUrl;
    private final File outFile;
    private final boolean allowEmptyIndex;
    private final ArrayList<SitemapIndexUrl> urls = new ArrayList<>();
    private final int maxUrls;
    private final W3CDateTimeFormatter dateTimeFormatter;
    private final Temporal defaultLastMod;
    private final boolean autoValidate;
    /**
     * Maximum 50,000 sitemaps per index allowed
     */
    public static final int MAX_SITEMAPS_PER_INDEX = 50000;

    /**
     * Options to configure sitemap index generation
     */
    public static class Options {
        private URL baseUrl;
        private File outFile;
        private W3CDateTimeFormatter dateTimeFormatter = null;
        private boolean allowEmptyIndex = false;
        private int maxUrls = MAX_SITEMAPS_PER_INDEX;
        private Temporal defaultLastMod = LocalDate.now();
        private boolean autoValidate = false;
        // TODO GZIP?  Is that legal for a sitemap index?

        /**
         * Configures the generator with a base URL and destination to write the sitemap index file.
         *
         * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
         * @param outFile The sitemap index will be written out at this location
         */
        public Options(URL baseUrl, File outFile) {
            this.baseUrl = baseUrl;
            this.outFile = outFile;
        }

        /**
         * Configures the generator with a base URL and destination to write the sitemap index file.
         *
         * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
         * @param outFile The sitemap index will be written out at this location
         */
        public Options(String baseUrl, File outFile) {
            this(UrlUtils.toUrl(baseUrl), outFile);
        }

        /**
         * The date formatter, typically configured with a {@link W3CDateTimeFormatter.Pattern} and/or a time zone
         */
        public Options dateFormat(W3CDateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
            return this;
        }

        /**
         * Permit writing an index that contains no URLs.
         *
         * @param allowEmptyIndex {@code true} if an empty index is permissible
         * @return this instance, for chaining
         */
        public Options allowEmptyIndex(boolean allowEmptyIndex) {
            this.allowEmptyIndex = allowEmptyIndex;
            return this;
        }

        /**
         * The maximum number of sitemaps to allow per sitemap index; the default is the
         * maximum allowed (1,000), but you can decrease it if you wish (for testing)
         */
        public Options maxUrls(int maxUrls) {
            if (maxUrls > MAX_SITEMAPS_PER_INDEX) {
                throw new SitemapGeneratorException("You can't have more than 1000 sitemaps per index");
            }
            this.maxUrls = maxUrls;
            return this;
        }

        /**
         * The default lastMod date for sitemap indexes; the default default is
         * now, but you can pass in null to omit a lastMod entirely. We don't
         * recommend this; Google may not like you as much.
         */
        public Options defaultLastMod(Temporal defaultLastMod) {
            this.defaultLastMod = defaultLastMod;
            return this;
        }

        /**
         * Validate the sitemap index automatically after writing it; this takes
         * time
         */
        public Options autoValidate(boolean autoValidate) {
            this.autoValidate = autoValidate;
            return this;
        }

        /**
         * Constructs a sitemap index generator configured with the options you specified
         */
        public SitemapIndexGenerator build() {
            return new SitemapIndexGenerator(this);
        }
    }

    /**
     * Configures the generator with a base URL and destination to write the sitemap index file.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param outFile The sitemap index will be written out at this location
     */
    public SitemapIndexGenerator(URL baseUrl, File outFile) {
        this(new Options(baseUrl, outFile));
    }

    /**
     * Configures the generator with a base URL and destination to write the sitemap index file.
     *
     * @param baseUrl All URLs in the generated sitemap(s) should appear under this base URL
     * @param outFile The sitemap index will be written out at this location
     */
    public SitemapIndexGenerator(String baseUrl, File outFile) {
        this(new Options(baseUrl, outFile));
    }

    private SitemapIndexGenerator(Options options) {
        this.baseUrl = options.baseUrl;
        this.outFile = options.outFile;
        this.allowEmptyIndex = options.allowEmptyIndex;
        this.maxUrls = options.maxUrls;

        W3CDateTimeFormatter formatter = options.dateTimeFormatter;

        if (formatter == null) {
            formatter = new W3CDateTimeFormatter();
        }

        this.dateTimeFormatter = formatter;
        this.defaultLastMod = options.defaultLastMod;
        this.autoValidate = options.autoValidate;
    }

    /**
     * Adds a single sitemap to the index
     */
    public SitemapIndexGenerator addUrl(SitemapIndexUrl url) {
        UrlUtils.checkUrl(url.url, baseUrl);
        if (urls.size() >= maxUrls) {
            throw new SitemapGeneratorException("More than " + maxUrls + " urls");
        }
        urls.add(url);
        return this;
    }

    /**
     * Add multiple sitemaps to the index
     */
    public SitemapIndexGenerator addUrls(Iterable<? extends SitemapIndexUrl> urls) {
        for (SitemapIndexUrl url : urls) addUrl(url);
        return this;
    }

    /**
     * Add multiple sitemaps to the index
     */
    public SitemapIndexGenerator addUrls(SitemapIndexUrl... urls) {
        for (SitemapIndexUrl url : urls) addUrl(url);
        return this;
    }

    /**
     * Add multiple sitemaps to the index
     */
    public SitemapIndexGenerator addUrls(String... urls) {
        for (String url : urls) addUrl(url);
        return this;
    }

    /**
     * Adds a single sitemap to the index
     */
    public SitemapIndexGenerator addUrl(String url) {
        return addUrl(new SitemapIndexUrl(url));
    }

    /**
     * Add multiple sitemaps to the index
     */
    public SitemapIndexGenerator addUrls(URL... urls) {
        for (URL url : urls) addUrl(url);
        return this;
    }

    /**
     * Adds a single sitemap to the index
     */
    public SitemapIndexGenerator addUrl(URL url) {
        return addUrl(new SitemapIndexUrl(url));
    }

    /**
     * Adds a single sitemap to the index
     */
    public SitemapIndexGenerator addUrl(URL url, Temporal lastMod) {
        return addUrl(new SitemapIndexUrl(url, lastMod));
    }

    /**
     * Adds a single sitemap to the index
     */
    public SitemapIndexGenerator addUrl(String url, Temporal lastMod) {
        return addUrl(new SitemapIndexUrl(url, lastMod));
    }

    /**
     * Add a numbered list of sitemaps to the index, e.g. "sitemap1.xml" "sitemap2.xml" "sitemap3.xml" etc.
     *
     * @param prefix the first part of the filename e.g. "sitemap"
     * @param suffix the last part of the filename e.g. ".xml" or ".xml.gz"
     * @param count  the number of sitemaps (1-based)
     */
    public SitemapIndexGenerator addUrls(String prefix, String suffix, int count) {
        if (count == 0) {
            try {
                addUrl(new URL(baseUrl, prefix + suffix));
            } catch (MalformedURLException e) {
                throw new SitemapGeneratorException(e);
            }
        } else {
            for (int i = 1; i <= count; i++) {
                String fileName = prefix + i + suffix;
                try {
                    addUrl(new URL(baseUrl, fileName));
                } catch (MalformedURLException e) {
                    throw new SitemapGeneratorException(e);
                }
            }
        }
        return this;
    }

    /**
     * Writes out the sitemap index
     */
    public void write() {
        try {
            // TODO gzip? is that legal for a sitemap index?
            write(new FileWriter(outFile));
        } catch (IOException e) {
            throw new SitemapGeneratorException("Problem writing sitemap index file " + outFile, e);
        }
    }

    private void write(OutputStreamWriter out) {
        if (!allowEmptyIndex && urls.isEmpty())
            throw new SitemapGeneratorException("No URLs added, sitemap index would be empty; you must add some URLs with addUrls");

        try (out) {
            writeSiteMap(out);
            out.flush();
            if (autoValidate) SitemapValidator.validateSitemapIndex(outFile);
        } catch (SAXException e) {
            throw new SitemapGeneratorException("Problem validating sitemap index file (bug?)", e);
        } catch (IOException ex) {
            throw new SitemapGeneratorException("Writing of stream has failed.", ex);
        }
    }

    public String writeAsString() {
        StringBuilder sb = new StringBuilder();
        writeAsString(sb);
        return sb.toString();
    }

    private void writeAsString(StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<sitemapindex xmlns=\"https://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (SitemapIndexUrl url : urls) {
            sb.append("  <sitemap>\n");
            sb.append("    <loc>");
            sb.append(UrlUtils.escapeXml(url.url.toString()));
            sb.append("</loc>\n");
            Temporal lastMod = url.lastMod;

            if (lastMod == null) lastMod = defaultLastMod;

            if (lastMod != null) {
                sb.append("    <lastmod>");
                sb.append(dateTimeFormatter.format(lastMod));
                sb.append("</lastmod>\n");
            }
            sb.append("  </sitemap>\n");
        }
        sb.append("</sitemapindex>");
    }

    private void writeSiteMap(OutputStreamWriter out) throws IOException {
        StringBuilder sb = new StringBuilder();
        writeAsString(sb);
        out.write(sb.toString());
    }

}
