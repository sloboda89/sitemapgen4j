package org.psloboda.sitemapgenerator.generators;

import org.psloboda.sitemapgenerator.generators.web.WebSitemapUrl;
import org.psloboda.sitemapgenerator.utils.UrlUtils;
import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;

public interface ISitemapUrlRenderer<T extends ISitemapUrl> {

    Class<T> getUrlClass();

    String getXmlNamespaces();

    void render(T url, StringBuilder sb, W3CDateTimeFormatter dateTimeFormatter);

    default void render(WebSitemapUrl url, StringBuilder sb, W3CDateTimeFormatter dateTimeFormatter, String additionalData) {
        sb.append("  <url>\n");
        sb.append("    <loc>");
        sb.append(UrlUtils.escapeXml(url.getUrl().toString()));
        sb.append("</loc>\n");
        if (url.getLastMod() != null) {
            sb.append("    <lastmod>");
            sb.append(dateTimeFormatter.format(url.getLastMod()));
            sb.append("</lastmod>\n");
        }
        if (url.getChangeFreq() != null) {
            sb.append("    <changefreq>");
            sb.append(url.getChangeFreq().toString());
            sb.append("</changefreq>\n");
        }
        if (url.getPriority() != null) {
            sb.append("    <priority>");
            sb.append(url.getPriority().toString());
            sb.append("</priority>\n");
        }
        if (additionalData != null) {
            sb.append(additionalData);
        }
        sb.append("  </url>\n");
    }

    default void renderTag(StringBuilder sb, String namespace, String tagName, Object value) {
        if (value == null) return;
        sb.append("      <");
        sb.append(namespace);
        sb.append(':');
        sb.append(tagName);
        sb.append('>');
        sb.append(UrlUtils.escapeXml(value.toString()));
        sb.append("</");
        sb.append(namespace);
        sb.append(':');
        sb.append(tagName);
        sb.append(">\n");
    }

    default void renderSubTag(StringBuilder sb, String namespace, String tagName, Object value) {
        if (value == null) return;
        sb.append("  ");
        renderTag(sb, namespace, tagName, value);
    }

}
