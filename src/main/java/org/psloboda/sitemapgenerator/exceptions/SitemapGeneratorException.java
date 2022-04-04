package org.psloboda.sitemapgenerator.exceptions;

public class SitemapGeneratorException extends RuntimeException {
    public SitemapGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SitemapGeneratorException(Throwable cause) {
        super(cause);
    }

    public SitemapGeneratorException(String message) {
        super(message);
    }
}
