package org.example.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampHelper {

    public static String addCurrentTimeStampPrefix(String text) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss").format(ZonedDateTime.now()) + " " + text;
    }
}