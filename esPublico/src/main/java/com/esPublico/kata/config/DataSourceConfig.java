package com.esPublico.kata.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceConfig {

    public static final HikariDataSource ds;

    static {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigLoader.getDdbbUrl());
            config.setUsername(ConfigLoader.getDdbbUsername());
            config.setPassword(ConfigLoader.getDdbbPassword());

            // Opcionales pero recomendados
            config.setMaximumPoolSize(ConfigLoader.getDdbbMaximumPoolSize()); // Número máximo de conexiones simultáneas
            config.setMinimumIdle(ConfigLoader.getDdbbMinimumIdle());      // Conexiones mínimas en reposo
            config.setIdleTimeout(30000);  // 30 segundos
            config.setMaxLifetime(600000); // 10 minutos
            config.setConnectionTimeout(10000); // 10 segundos de espera máxima
            ds = new HikariDataSource(config);
    }
}
