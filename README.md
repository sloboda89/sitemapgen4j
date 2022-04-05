sitemapgen4j
============

SitemapGen4j is a library to generate XML sitemaps in Java.

Forked from [dfabulich/sitemapgen4j](https://github.com/dfabulich/sitemapgen4j)

## What was changed:

- Migrated from Java 1.5 to Java 17
- Maven plugins were updated to latest version (05-04-2022)
- Unit test was rewrote from JUnit 3 to JUnit 5
- Removed unsupported types of Google sitemaps (Code/Geo/Link/Mobile)
- Package structure was reorganized

## What's an XML sitemap?

Quoting from [sitemaps.org](http://sitemaps.org/index.php):

> Sitemaps are an easy way for webmasters to inform search engines about pages on their sites that are available for crawling. In its simplest form, a Sitemap is an XML file that lists URLs for a site along with additional metadata about each URL (when it was last updated, how often it usually changes, and how important it is, relative to other URLs in the site) so that search engines can more intelligently crawl the site.
>
> Web crawlers usually discover pages from links within the site and from other sites. Sitemaps supplement this data to allow crawlers that support Sitemaps to pick up all URLs in the Sitemap and learn about those URLs using the associated metadata. Using the Sitemap protocol does not guarantee that web pages are included in search engines, but provides hints for web crawlers to do a better job of crawling your site.
>
> Sitemap 0.90 is offered under the terms of the Attribution-ShareAlike Creative Commons License and has wide adoption, including support from Google, Yahoo!, and Microsoft.

## Getting started

The easiest way to get started is to just use the WebSitemapGenerator class, like this:

```java
WebSitemapGenerator wsg = new WebSitemapGenerator("http://www.example.com", myDir);
wsg.addUrl("http://www.example.com/index.html"); // repeat multiple times
wsg.write();
```

## Configuring options

But there are a lot of nifty options available for URLs and for the generator as a whole.  To configure the generator, use a builder:

```java
WebSitemapGenerator wsg = WebSitemapGenerator.builder("http://www.example.com", myDir)
        .gzip(true)
        .build(); // enable gzipped output
        
wsg.addUrl("http://www.example.com/index.html");
wsg.write();
```

To configure the URLs, construct a WebSitemapUrl with WebSitemapUrl.Options.

```java
WebSitemapGenerator wsg = new WebSitemapGenerator("http://www.example.com", myDir);
WebSitemapUrl url = new WebSitemapUrl.Options("http://www.example.com/index.html")
        .lastMod(LocalDate.now())
        .priority(1.0)
        .changeFreq(ChangeFreq.HOURLY)
        .build();

// this will configure the URL with lastmod=now, priority=1.0, changefreq=hourly 
wsg.addUrl(url);
wsg.write();
```

## Configuring the date format

One important configuration option for the sitemap generator is the date format. The [W3C datetime standard](http://www.w3.org/TR/NOTE-datetime) allows you to choose the precision of your datetime (anything from just specifying the date like "1997-01-01" to specifying the fraction of the second like "1997-07-16T19:20:30.45+01:00"); if you don't specify one, we'll try to guess which one you want, and we'll use GMT as default timezone, which might not be what you prefer.

```java
// Use DAY pattern (2009-02-07), Greenwich Mean Time timezone
W3CDateTimeFormatter dateTimeFormatter = new W3CDateTimeFormatter(Pattern.DAY); 
WebSitemapGenerator wsg = WebSitemapGenerator.builder("http://www.example.com", myDir)
        .dateFormat(dateTimeFormatter)
        .build(); // actually use the configured dateTimeFormat
        
wsg.addUrl("http://www.example.com/index.html");
wsg.write();
```

## Lots of URLs: a sitemap index file

One sitemap can contain a maximum of 50,000 URLs.  (Some sitemaps, like Google News sitemaps, can contain only 1,000 URLs.) If you need to put more URLs than that in a sitemap, you'll have to use a sitemap index file.  Fortunately,  WebSitemapGenerator can manage the whole thing for you. 

```java
WebSitemapGenerator wsg = new WebSitemapGenerator("http://www.example.com", myDir);

for (int i = 0; i < 60000; i++) {
    wsg.addUrl("http://www.example.com/doc" + i + ".html");
}

wsg.write();
wsg.writeSitemapsWithIndex(); // generate the sitemap_index.xml

```

That will generate two sitemaps for 60K URLs: sitemap1.xml (with 50K urls) and sitemap2.xml (with the remaining 10K), and then generate a sitemap_index.xml file describing the two.

It's also possible to carefully organize your sub-sitemaps.  For example, it's recommended to group URLs with the same changeFreq together (have one sitemap for changeFreq "daily" and another for changeFreq "yearly"), so you can modify the lastMod of the daily sitemap without modifying the lastMod of the yearly sitemap.  To do that, just construct your sitemaps one at a time using  the WebSitemapGenerator, then use the SitemapIndexGenerator to create a single index for all of them. 

```java
// generate foo sitemap
WebSitemapGenerator fooMsg = WebSitemapGenerator.builder("http://www.example.com", myDir)
        .fileNamePrefix("foo")
        .build();

for (int i = 0; i < 5; i++) {
    fooMsg.addUrl("http://www.example.com/foo"+i+".html");
}

fooMsg.write();

// generate bar sitemap
WebSitemapGenerator barMsg = WebSitemapGenerator.builder("http://www.example.com", myDir)
        .fileNamePrefix("bar")
        .build();

for (int i = 0; i < 5; i++) {
    barMsg.addUrl("http://www.example.com/bar"+i+".html");
}

barMsg.write();

// generate sitemap index for foo + bar 
SitemapIndexGenerator sig = new SitemapIndexGenerator("http://www.example.com", myFile);
sig.addUrl("http://www.example.com/foo.xml");
sig.addUrl("http://www.example.com/bar.xml");
sig.write();
```

You could also use the SitemapIndexGenerator to incorporate sitemaps generated by other tools.  For example, you might use Google's official Python sitemap generator to generate some sitemaps, and use WebSitemapGenerator to generate some sitemaps, and use SitemapIndexGenerator to make an index of all of them. 

## Validate your sitemaps

SitemapGen4j can also validate your sitemaps using the official XML Schema Definition (XSD).  If you used SitemapGen4j to make the sitemaps, you shouldn't need to do this unless there's a bug in our code.  But you can use it to validate sitemaps generated by other tools, and it provides an extra level of safety.

It's easy to configure the WebSitemapGenerator to automatically validate your sitemaps right after you write them (but this does slow things down, naturally). 

```java
WebSitemapGenerator wsg = WebSitemapGenerator.builder("http://www.example.com", myDir)
        .autoValidate(true)
        .build(); // validate the sitemap after writing
        
wsg.addUrl("http://www.example.com/index.html");
wsg.write();
```

You can also use the SitemapValidator directly to manage sitemaps.  It has two methods: validateWebSitemap(File f) and validateSitemapIndex(File f).

## Google-specific sitemaps

Google can understand a wide variety of custom sitemap formats that they made up, including Google Images sitemaps, News sitemaps, and Video sitemaps.  SitemapGen4j can generate any/all of these different types of sitemaps.

To generate a special type of sitemap, just use GoogleNewsSitemapGenerator or GoogleVideoSitemapGenerator instead of WebSitemapGenerator.

You can't mix-and-match regular URLs with Google-specific sitemaps, so you'll also have to use a GoogleNewsSitemapUrl or GoogleVideoSitemapUrl instead of a WebSitemapUrl.  Each of them has unique configurable options not available to regular web URLs.  
