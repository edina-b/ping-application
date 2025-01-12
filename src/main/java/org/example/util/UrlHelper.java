package org.example.util;

public class UrlHelper {
    public static String fixUrlPrefixes(String url) {
        if (!url.contains("www.")) {
            if (url.startsWith("http:")) {
                url = url.replaceFirst("http", "https");
            }
            if (url.startsWith("https:")) {
                url = url.replaceFirst("://", "://www.");
            } else {
                url = "https://www." + url;
            }
        } else {
            if (url.startsWith("http:")) {
                url = url.replaceFirst("http", "https");
            }
            if (url.startsWith("www")) {
                url = "https://" + url;
            }
        }
        return url;
    }
}
