package com.n26.infrastructure.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;

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
