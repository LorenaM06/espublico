package com.esPublico.kata.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configura y proporciona una instancia única de {@link HikariDataSource}
 * para gestionar el pool de conexiones a la base de datos.
 * <p>
 * Esta clase utiliza un bloque estático para inicializar el pool de conexiones
 * mediante {@link HikariCP}, utilizando los parámetros definidos en {@link ConfigLoader}.
 * <p>
 * El pool se configura con valores obtenidos de {@link ConfigLoader} como número máximo de conexiones,
 * conexiones mínimas en reposo, url de conexión, usuario y contraseña.
 *
 * <p>Ejemplo de uso:
 * <pre>{@code
 * Connection conn = DataSourceConfig.ds.getConnection();
 * }</pre>
 *
 * <p>Es un singleton, garantizando que solo se crea una instancia del pool durante
 * el ciclo de vida de la aplicación.
 *
 * @see HikariDataSource
 * @see ConfigLoader
 */
public class DataSourceConfig {

    /**
     * Instancia única y compartida de {@link HikariDataSource} utilizada en toda la aplicación.
     * Se configura automáticamente con los valores proporcionados por {@link ConfigLoader}.
     */
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
