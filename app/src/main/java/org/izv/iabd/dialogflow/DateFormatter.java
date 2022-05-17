package org.izv.iabd.dialogflow;

import android.os.Build;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static String getDateFormated(String fecha, String dateTimeFormatter) {
        String date = "N/A";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime ldate = LocalDateTime.parse(fecha, DateTimeFormatter.ISO_DATE_TIME);
            date = ldate.format(DateTimeFormatter.ofPattern(dateTimeFormatter));
        }

        return date;
    }

    public static long getMiliseconds(String fecha) {
        long begin = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime ldate = LocalDateTime.parse(fecha, DateTimeFormatter.ISO_DATE_TIME);
            Instant instant = ldate.atZone(ZoneId.of("UTC+2")).toInstant();
            begin = instant.toEpochMilli();
        }

        return begin;
    }

    public static String getTimeFormat() {
        return "HH:mm";
    }

    public static String getDateFormat() {

        return "YYYY-MM-dd";
    }

    public static String getDayFormat() {

        return "dd";
    }

    public static String getMonthFormat() {
        return "MMMM";
    }

    public static String getHourFormat() {
        return "HH";
    }
}