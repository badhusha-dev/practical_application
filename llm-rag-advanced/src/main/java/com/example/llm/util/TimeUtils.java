package com.example.llm.util;

import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
public class TimeUtils {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
    public static String formatTimestamp(OffsetDateTime timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.format(ISO_FORMATTER);
    }
    
    public static OffsetDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(timestamp, ISO_FORMATTER);
        } catch (Exception e) {
            log.warn("Failed to parse timestamp: {}", timestamp, e);
            return null;
        }
    }
    
    public static String getRelativeTime(OffsetDateTime timestamp) {
        if (timestamp == null) {
            return "Unknown";
        }
        
        OffsetDateTime now = OffsetDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(timestamp, now);
        long hours = ChronoUnit.HOURS.between(timestamp, now);
        long days = ChronoUnit.DAYS.between(timestamp, now);
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (days < 7) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else {
            return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        }
    }
    
    public static long getElapsedMillis(OffsetDateTime start) {
        if (start == null) {
            return 0;
        }
        return ChronoUnit.MILLIS.between(start, OffsetDateTime.now());
    }
}
