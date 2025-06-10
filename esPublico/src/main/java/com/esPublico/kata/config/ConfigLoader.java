package com.esPublico.kata.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("No se pudo encontrar config.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error cargando config.properties", e);
        }
    }

    // PROPIEDADES BBDD

    public static String getDdbbUrl() {
        return properties.getProperty("jdbc.url");
    }

    public static String getDdbbUsername() {
        return properties.getProperty("jdbc.username");
    }

    public static String getDdbbPassword() {
        return properties.getProperty("jdbc.password");
    }

    public static int getDdbbMaximumPoolSize() {
        return Integer.parseInt(properties.getProperty("hikari.maximumPoolSize"));
    }

    public static int getDdbbMinimumIdle() {
        return Integer.parseInt(properties.getProperty("hikari.minimumIdle"));
    }

    // PROPIEDADES API

    public static String getApiBaseUrl() {
        return properties.getProperty("api.baseUrl");
    }
}
