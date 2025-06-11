package com.esPublico.kata.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigLoader es una clase utilitaria que carga las propiedades de configuración desde
 * un archivo `config.properties` ubicado en la carpeta `resources`.
 *
 * <p>Este archivo contiene parámetros como los datos de conexión a la base de datos
 * y la URL base de la API de pedidos.</p>
 *
 * <p>La clase está diseñada como un singleton implícito: todas las propiedades se cargan una sola vez
 * al inicializar la clase y se accede a ellas mediante métodos estáticos.</p>
 *
 * <p>Ejemplo de uso:
 * <pre>{@code
 * String dbUrl = ConfigLoader.getDdbbUrl();
 * String apiUrl = ConfigLoader.getApiBaseUrl();
 * }</pre>
 * </p>
 *
 */
public class ConfigLoader {

    private static final Properties properties = new Properties();

    // Bloque de inicialización estático para cargar el archivo de propiedades
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

    /**
     * Recupera el valor de una propiedad obligatoria a partir de su clave.
     * <p>
     * Si la propiedad no existe o está vacía, lanza una excepción indicando
     * que la propiedad requerida no está presente en el archivo de configuración.
     * </p>
     *
     * @param key la clave de la propiedad requerida.
     * @return el valor asociado a la clave especificada.
     * @throws IllegalArgumentException si la propiedad no está definida o su valor es vacío.
     */
    private static String requireProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Falta la propiedad requerida: " + key);
        }
        return value;
    }

    // ==============================
    // Propiedades de Base de Datos
    // ==============================

    /**
     * Obtiene la URL de conexión a la base de datos.
     *
     * @return la URL JDBC definida en `jdbc.url`.
     */
    public static String getDdbbUrl() {
        return requireProperty("jdbc.url");
    }

    /**
     * Obtiene el nombre de usuario para la base de datos.
     *
     * @return el nombre de usuario definido en `jdbc.username`.
     */
    public static String getDdbbUsername() {
        return requireProperty("jdbc.username");
    }

    /**
     * Obtiene el nombre de usuario para la base de datos.
     *
     * @return el nombre de usuario definido en `jdbc.username`.
     */
    public static String getDdbbPassword() {
        return requireProperty("jdbc.password");
    }

    /**
     * Obtiene el número máximo de conexiones en el pool de HikariCP.
     *
     * @return el valor definido en `hikari.maximumPoolSize`.
     * @throws NumberFormatException si la propiedad no es un número válido.
     */
    public static int getDdbbMaximumPoolSize() {
        return Integer.parseInt(requireProperty("hikari.maximumPoolSize"));
    }

    /**
     * Obtiene el número mínimo de conexiones inactivas en el pool de HikariCP.
     *
     * @return el valor definido en `hikari.minimumIdle`.
     * @throws NumberFormatException si la propiedad no es un número válido.
     */
    public static int getDdbbMinimumIdle() {
        return Integer.parseInt(requireProperty("hikari.minimumIdle"));
    }

    // ==============================
    // Propiedades de API
    // ==============================

    /**
     * Obtiene la URL base de la API que consume la aplicación.
     *
     * @return la URL definida en `api.baseUrl`.
     */
    public static String getApiBaseUrl() {
        return requireProperty("api.baseUrl");
    }

    /**
     * Obtiene el número de registros obtenidos en las peticiones a la API.
     *
     * @return el valor definido en `api.maxPerPage`.
     */
    public static String getApiMaxPerPage() { return requireProperty("api.maxPerPage"); }
}
