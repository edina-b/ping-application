package org.example.util;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ApplicationConfigHelper {

    public static Properties getApplicationConfig() {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;

        } catch (Exception e) {
            log.error("Unexpected error during loading application properties: " + e.getClass().getName() + " " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
