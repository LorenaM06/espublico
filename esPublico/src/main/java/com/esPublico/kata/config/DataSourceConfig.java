package com.esPublico.kata.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceConfig {

    public static final HikariDataSource ds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/espublico");
        config.setUsername("root");
        config.setPassword("root");

        // Opcionales pero recomendados
        config.setMaximumPoolSize(200); // Número máximo de conexiones simultáneas
        config.setMinimumIdle(200);      // Conexiones mínimas en reposo
        config.setIdleTimeout(30000);  // 30 segundos
        config.setMaxLifetime(600000); // 10 minutos
        config.setConnectionTimeout(10000); // 10 segundos de espera máxima
        ds = new HikariDataSource(config);
    }
}
