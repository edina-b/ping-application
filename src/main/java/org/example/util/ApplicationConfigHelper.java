package org.example.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ApplicationConfigHelper {

    public static Properties getApplicationConfig(){
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        } catch (IOException e) {
            log.error("Unexpected error during loading application properties: "+e.getClass().getName() +" "+e.getMessage());
            throw new RuntimeException(e);
        }
        return appProps;
    }
}
