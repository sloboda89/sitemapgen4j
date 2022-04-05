package org.psloboda.sitemapgenerator.generators;

import org.psloboda.sitemapgenerator.exceptions.SitemapGeneratorException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Validates sitemaps and sitemap indexes
 *
 * @author Dan Fabulich
 */
public class SitemapValidator {

    private SitemapValidator() {
    }

    //TODO support gzip
    //TODO confirm < 10MB
    //TODO confirm single host
    //TODO confirm correct host
    //TODO confirm UTF-8

    //TODO support Video/News (sitemap.xsd doesn't support them)
    //TODO confirm news restrictions: 3 days, 1000 URLs
    //TODO video restrictions: title, player_loc/content_loc, no non-video urls
    //IMO news should have no non-news urls, geo should have no non-geo urls, code should have no non-code urls

    private static Schema sitemapSchema;
    private static Schema sitemapIndexSchema;

    private static synchronized void lazyLoad() {
        if (sitemapSchema != null) {
            return;
        }

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            sitemapSchema = lazyLoad(factory, "../sitemap.xsd");
            sitemapIndexSchema = lazyLoad(factory, "../siteindex.xsd");
        } catch (Exception e) {
            throw new SitemapGeneratorException("BUG", e);
        }
    }

    private static synchronized Schema lazyLoad(SchemaFactory factory, String resource) throws IOException, SAXException {
        try (InputStream stream = SitemapValidator.class.getResourceAsStream(resource)) {

            if (stream == null)
                throw new SitemapGeneratorException("BUG Couldn't load " + resource);

            StreamSource source = new StreamSource(stream);

            return factory.newSchema(source);
        }
    }

    /**
     * Validates an ordinary web sitemap file (NOT a Google-specific sitemap)
     */
    public static void validateWebSitemap(File sitemap) throws SAXException {
        lazyLoad();
        validateXml(sitemap, sitemapSchema);
    }

    /**
     * Validates a sitemap index file
     */
    public static void validateSitemapIndex(File sitemap) throws SAXException {
        lazyLoad();
        validateXml(sitemap, sitemapIndexSchema);
    }

    private static void validateXml(File sitemap, Schema schema) throws SAXException {
        Validator validator = schema.newValidator();

        try (FileReader reader = new FileReader(sitemap)) {
            SAXSource source = new SAXSource(new InputSource(reader));
            validator.validate(source);
        } catch (IOException e) {
            throw new SitemapGeneratorException(e);
        }
    }
}
