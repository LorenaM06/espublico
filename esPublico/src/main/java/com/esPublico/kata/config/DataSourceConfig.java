package com.esPublico.kata.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceConfig {

    public static final HikariDataSource ds;
    public static final int maxConnections;

    static {
        try (InputStream input = DataSourceConfig.class.getClassLoader().getResourceAsStream("local.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("jdbc.url"));
            config.setUsername(properties.getProperty("jdbc.username"));
            config.setPassword(properties.getProperty("jdbc.password"));

            // Opcionales pero recomendados
            maxConnections = Integer.parseInt(properties.getProperty("hikari.maximumPoolSize"));
            config.setMaximumPoolSize(maxConnections); // Número máximo de conexiones simultáneas
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("hikari.minimumIdle")));      // Conexiones mínimas en reposo
            config.setIdleTimeout(30000);  // 30 segundos
            config.setMaxLifetime(600000); // 10 minutos
            config.setConnectionTimeout(10000); // 10 segundos de espera máxima
            ds = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
