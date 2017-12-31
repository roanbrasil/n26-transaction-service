package com.n26.infrastructure.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Util.java
 * Utilities to used in the code to avoid repeat code.
 *
 * @author roanbrasil
 * @version 1.0
 * @since 12-30-2017
 */
public class Util {
    public static long timestampGenerator() {
        return Timestamp.valueOf(Instant.ofEpochMilli(System.currentTimeMillis())
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime()).getTime();
    }
    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
