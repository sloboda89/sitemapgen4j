package org.psloboda.sitemapgenerator.generators;

import org.psloboda.sitemapgenerator.utils.UrlUtils;

import java.io.File;
import java.net.URL;

public class SitemapGeneratorOptions extends AbstractSitemapGeneratorOptions<SitemapGeneratorOptions> {

    public SitemapGeneratorOptions(URL baseUrl, File baseDir) {
        super(baseUrl, baseDir);
    }

    public SitemapGeneratorOptions(String baseUrl, File baseDir) {
        this(UrlUtils.toUrl(baseUrl), baseDir);
    }

    public SitemapGeneratorOptions(URL baseUrl) {
        super(baseUrl);
    }

    public SitemapGeneratorOptions(String baseUrl) {
        super(UrlUtils.toUrl(baseUrl));
    }

}
