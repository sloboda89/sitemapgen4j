package org.psloboda.sitemapgenerator;

import org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.Temporal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class W3CDateTimeFormatterTest {

    @Test
    void testFormatEpoch() {
        ZonedDateTime epoch = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).atZone(W3CDateTimeFormatter.ZULU);
        verifyPatternFormat(epoch, W3CDateTimeFormatter.Pattern.MILLISECOND, "1970-01-01T00:00:00.000Z");
        verifyPatternFormat(epoch, W3CDateTimeFormatter.Pattern.SECOND, "1970-01-01T00:00:00Z");
        verifyPatternFormat(epoch, W3CDateTimeFormatter.Pattern.MINUTE, "1970-01-01T00:00Z");
        verifyPatternFormat(epoch, W3CDateTimeFormatter.Pattern.DAY, "1970-01-01");
        verifyPatternFormat(epoch, W3CDateTimeFormatter.Pattern.MONTH, "1970-01");
        verifyPatternFormat(epoch, W3CDateTimeFormatter.Pattern.YEAR, "1970");
        verifyPatternFormat(epoch, "1970-01-01T00:00:00.000Z");
    }

    @Test
    void testAutoFormat() {
        ZonedDateTime zonedDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).atZone(W3CDateTimeFormatter.ZULU);
        verifyPatternFormat(zonedDateTime, "1970-01-01T00:00:00.000Z");
        Instant instant = Instant.ofEpochSecond(0);
        verifyPatternFormat(instant, "1970-01-01T00:00:00.000Z");
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
        verifyPatternFormat(localDateTime, "1970-01-01T00:00:00.000");
        LocalDate localDate = LocalDate.ofEpochDay(0);
        verifyPatternFormat(localDate, "1970-01-01");
    }

    @Test
    void testFormatTimeZone() {
        ZoneId zoneId = ZoneId.of(ZoneId.SHORT_IDS.get("PST"));
        ZonedDateTime epoch = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.of("-08:00")).atZone(zoneId);
        verifyPatternFormat(epoch, W3CDateTimeFormatter.Pattern.MILLISECOND, "1969-12-31T16:00:00.000-08:00", zoneId);
        verifyPatternFormat(epoch, "1969-12-31T16:00:00.000-08:00", zoneId);
    }

    @Test
    void testParseEpoch() {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.EPOCH, W3CDateTimeFormatter.ZULU);
        verifyPatternParse("1970-01-01T00:00:00.000Z", W3CDateTimeFormatter.Pattern.MILLISECOND, date);
        verifyPatternParse("1970-01-01T00:00:00Z", W3CDateTimeFormatter.Pattern.SECOND, date);
        verifyPatternParse("1970-01-01T00:00Z", W3CDateTimeFormatter.Pattern.MINUTE, date);
        verifyPatternParse("1970-01-01", W3CDateTimeFormatter.Pattern.DAY, date.toLocalDate());
    }

    @Test
    void testAutoParse() {
        ZonedDateTime date = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).atZone(W3CDateTimeFormatter.ZULU);
        verifyPatternParse("1970-01-01T00:00:00.000Z", date);
        verifyPatternParse("1970-01-01T00:00:00.000Z", date.toLocalDateTime());
        verifyPatternParse("1970-01-01T00:00:00.000Z", date.toInstant());
        verifyPatternParse("1970-01-01", date.toLocalDate());
    }

    @Test
    void testParseTimeZone() {
        ZonedDateTime epoch = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).atZone(W3CDateTimeFormatter.ZULU);
        verifyPatternParse("1969-12-31T16:00:00.000-08:00", W3CDateTimeFormatter.Pattern.MILLISECOND, epoch);
        verifyPatternParse("1969-12-31T16:00:00.000-08:00", epoch);
    }

    private void verifyPatternFormat(Temporal temporal, W3CDateTimeFormatter.Pattern pattern, String expected) {
        verifyPatternFormat(temporal, pattern, expected, W3CDateTimeFormatter.ZULU);
    }

    private void verifyPatternFormat(Temporal temporal, W3CDateTimeFormatter.Pattern pattern, String expected, ZoneId zoneId) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter(pattern, zoneId);
        assertEquals(expected, format.format(temporal), temporal + " " + pattern);
    }

    private void verifyPatternFormat(Temporal temporal, String expected, ZoneId zoneId) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter(zoneId);
        assertEquals(expected, format.format(temporal), temporal + " " + format);
    }

    private void verifyPatternFormat(Temporal temporal, String expected) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter();
        assertEquals(expected, format.format(temporal), temporal + " " + format);
    }

    private void verifyPatternParse(String source, W3CDateTimeFormatter.Pattern pattern, ZonedDateTime expected) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter(pattern);
        ZonedDateTime actual = format.parseZonedDateTime(source);
        assertEquals(expected, actual, source + " " + pattern);
    }

    private void verifyPatternParse(String source, W3CDateTimeFormatter.Pattern pattern, LocalDate expected) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter(pattern);
        LocalDate actual = format.parseLocalDate(source);
        assertEquals(expected, actual, source + " " + pattern);
    }

    private void verifyPatternParse(String source, ZonedDateTime expected) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter();
        ZonedDateTime actual = format.parseZonedDateTime(source);
        assertEquals(expected, actual, source + " " + format);
    }

    private void verifyPatternParse(String source, LocalDateTime expected) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter();
        LocalDateTime actual = format.parseLocalDateTime(source);
        assertEquals(expected, actual, source + " " + format);
    }

    private void verifyPatternParse(String source, Instant expected) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter();
        Instant actual = format.parseInstant(source);
        assertEquals(expected, actual, source + " " + format);
    }

    private void verifyPatternParse(String source, LocalDate expected) {
        W3CDateTimeFormatter format = new W3CDateTimeFormatter();
        LocalDate actual = format.parseLocalDate(source);
        assertEquals(expected, actual, source + " " + format);
    }

}
