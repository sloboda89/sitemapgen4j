package org.psloboda.sitemapgenerator.utils;

import org.psloboda.sitemapgenerator.exceptions.SitemapGeneratorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
    private static final Map<String, String> ENTITIES = new HashMap<>();
    private static final Pattern PATTERN = Pattern.compile("([&'\"><])");


    static {
        ENTITIES.put("&", "&amp;");
        ENTITIES.put("'", "&apos;");
        ENTITIES.put("\"", "&quot;");
        ENTITIES.put(">", "&gt;");
        ENTITIES.put("<", "&lt;");
    }

    private UrlUtils() {
    }

    public static String escapeXml(String string) {
        Matcher matcher = PATTERN.matcher(string);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ENTITIES.get(matcher.group(1)));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static void checkUrl(URL url, URL baseUrl) {
        // Is there a better test to use here?

        if (baseUrl.getHost() == null) {
            throw new SitemapGeneratorException("base URL is null");
        }

        if (!baseUrl.getHost().equalsIgnoreCase(url.getHost())) {
            throw new SitemapGeneratorException("Domain of URL " + url + " doesn't match base URL " + baseUrl);
        }
    }

    public static URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new SitemapGeneratorException(e.getMessage(), e);
        }
    }

    public static URI toUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new SitemapGeneratorException(e.getMessage(), e);
        }
    }

}
