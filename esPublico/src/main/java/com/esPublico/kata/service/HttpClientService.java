package com.esPublico.kata.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Servicio singleton para realizar peticiones HTTP GET genéricas.
 * <p>
 * Utiliza la API estándar de {@link HttpClient} de Java 11+ para ejecutar
 * solicitudes HTTP con un timeout de conexión configurable (actualmente 10 segundos).
 * <p>
 * Soporta la inclusión de cabeceras HTTP personalizadas y manejo básico de respuestas,
 * incluyendo descompresión automática de respuestas GZIP.
 */
public class HttpClientService {

    private static final HttpClientService INSTANCE = new HttpClientService();

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Obtiene la instancia singleton del servicio.
     *
     * @return instancia única de {@code HttpClientService}.
     */
    public static HttpClientService getInstance() {
        return INSTANCE;
    }

    /**
     * Realiza una petición HTTP GET a la URL especificada con las cabeceras indicadas.
     *
     * @param url     URL de destino de la petición HTTP GET.
     * @param headers mapa con las cabeceras HTTP que se desean incluir en la petición.
     * @return el cuerpo de la respuesta HTTP como cadena de texto, descomprimido si está en formato GZIP.
     * @throws IOException          si ocurre un error durante la conexión o lectura de la respuesta.
     * @throws InterruptedException si la operación es interrumpida mientras espera la respuesta.
     */
    public String get(String url, Map<String, String> headers) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        headers.forEach(requestBuilder::header);

        HttpRequest request = requestBuilder.build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        return handleResponse(response);
    }

    /**
     * Maneja la respuesta HTTP recibida.
     * <p>
     * Si el código de estado HTTP es exitoso (2xx), descomprime la respuesta en GZIP y
     * devuelve el cuerpo como texto UTF-8. Si no, lanza una excepción indicando el error HTTP.
     *
     * @param response la respuesta HTTP recibida.
     * @return cuerpo de la respuesta HTTP como texto.
     * @throws IOException si el código HTTP no indica éxito o si hay error leyendo el cuerpo.
     */
    private String handleResponse(HttpResponse<InputStream> response) throws IOException {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            InputStream bodyStream = new GZIPInputStream(response.body());
            return new String(bodyStream.readAllBytes(), "UTF-8");
        } else {
            throw new IOException("HTTP error: " + status + " - " + response.body());
        }
    }
}

