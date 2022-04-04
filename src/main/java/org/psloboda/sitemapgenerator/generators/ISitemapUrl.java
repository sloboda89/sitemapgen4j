package org.psloboda.sitemapgenerator.generators;

import java.net.URL;
import java.time.temporal.Temporal;

public interface ISitemapUrl {

    Temporal getLastMod();

    URL getUrl();

}
