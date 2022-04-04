package org.psloboda.sitemapgenerator.utils;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter.Pattern.DAY;
import static org.psloboda.sitemapgenerator.utils.W3CDateTimeFormatter.Pattern.MILLISECOND;

public class W3CDateTimeFormatter implements Serializable {
    /**
     * The GMT ("zulu") time zone, for your convenience
     */
    public static final ZoneId ZULU = ZoneId.of("GMT");

    private final transient DateTimeFormatter defaultFormatter;
    private final ZoneId zoneId;
    private static final Map<Class<? extends Temporal>, DateTimeFormatter> FORMATTERS = new HashMap<>();

    static {
        FORMATTERS.put(LocalDate.class, DAY.getFormatter());
        FORMATTERS.put(LocalDateTime.class, MILLISECOND.getFormatter());
        FORMATTERS.put(Instant.class, MILLISECOND.getFormatter().withZone(ZULU));
        FORMATTERS.put(ZonedDateTime.class, MILLISECOND.getFormatter().withZone(ZULU));
    }

    public W3CDateTimeFormatter() {
        this(ZULU);
    }

    public W3CDateTimeFormatter(ZoneId zoneId) {
        this.zoneId = zoneId;
        this.defaultFormatter = null;
    }

    public W3CDateTimeFormatter(Pattern pattern, ZoneId zoneId) {
        this.zoneId = zoneId;
        defaultFormatter = pattern.getFormatter();
    }

    public W3CDateTimeFormatter(Pattern pattern) {
        this(pattern, ZULU);
    }

    public ZonedDateTime parseZonedDateTime(String data) {
        DateTimeFormatter formatter = Optional.ofNullable(defaultFormatter)
                .orElseGet(() -> FORMATTERS.get(ZonedDateTime.class))
                .withZone(zoneId);
        return ZonedDateTime.parse(data, formatter);
    }

    public LocalDateTime parseLocalDateTime(String data) {
        DateTimeFormatter formatter = Optional.ofNullable(defaultFormatter)
                .orElseGet(() -> FORMATTERS.get(LocalDateTime.class));
        return LocalDateTime.parse(data, formatter);
    }

    public Instant parseInstant(String data) {
        DateTimeFormatter formatter = Optional.ofNullable(defaultFormatter)
                .orElseGet(() -> FORMATTERS.get(Instant.class));
        return formatter.parse(data, Instant::from);
    }

    public LocalDate parseLocalDate(String data) {
        DateTimeFormatter formatter = Optional.ofNullable(defaultFormatter)
                .orElseGet(() -> FORMATTERS.get(LocalDate.class));
        return LocalDate.parse(data, formatter);
    }

    public String format(Temporal temporal) {
        return Optional.ofNullable(defaultFormatter)
                .orElseGet(() -> FORMATTERS.get(temporal.getClass()))
                .withZone(zoneId)
                .format(temporal);
    }

    public enum Pattern {
        /**
         * "yyyy-MM-ddTHH:mm:ss.SSSZ"
         */
        MILLISECOND("yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]"),
        /**
         * "yyyy-MM-ddTHH:mm:ssZ"
         */
        SECOND("yyyy-MM-dd'T'HH:mm:ss[XXX]"),
        /**
         * "yyyy-MM-ddTHH:mmZ"
         */
        MINUTE("yyyy-MM-dd'T'HH:mm[XXX]"),
        /**
         * "yyyy-MM-dd"
         */
        DAY("yyyy-MM-dd"),
        /**
         * "yyyy-MM"
         */
        MONTH("yyyy-MM"),
        /**
         * "yyyy"
         */
        YEAR("yyyy");

        private final DateTimeFormatter formatter;

        Pattern(String pattern) {
            this.formatter = DateTimeFormatter.ofPattern(pattern);
        }

        public DateTimeFormatter getFormatter() {
            return formatter;
        }
    }
}
