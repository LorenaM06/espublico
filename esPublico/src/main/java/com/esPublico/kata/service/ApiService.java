package com.esPublico.kata.service;

import com.esPublico.kata.config.ConfigLoader;
import com.esPublico.kata.model.PageOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Servicio para interactuar con la API externa de pedidos.
 * Implementa el patrón Singleton para asegurar una única instancia durante la ejecución.
 *
 * Utiliza un HttpClientService para realizar peticiones HTTP y Jackson ObjectMapper para deserializar JSON.
 */
public class ApiService {

    /**
     * Instancia única de ApiService (Singleton).
     */
    private static final ApiService INSTANCE = new ApiService();

    /**
     * Obtiene la instancia única de ApiService.
     *
     * @return instancia singleton de ApiService
     */
    public static ApiService getInstance(){
        return INSTANCE;
    }

    /**
     * Cliente HTTP utilizado para realizar peticiones.
     */
    private static final HttpClientService client = HttpClientService.getInstance();

    /**
     * URL base de la API, cargada desde configuración externa.
     */
    private static final String baseUrl = ConfigLoader.getApiBaseUrl();

    /**
     * ObjectMapper configurado para convertir JSON con nombres en snake_case.
     */
    private static final ObjectMapper mapper = (new ObjectMapper()).setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    /**
     * Obtiene una página de pedidos usando parámetros de paginación.
     * Construye la URL con los parámetros y llama al método que hace la petición.
     *
     * @param page       número de página a solicitar
     * @param maxPerPage cantidad máxima de pedidos por página
     * @return objeto {@link PageOrder} con los pedidos de la página solicitada
     * @throws IOException           si ocurre un error de entrada/salida
     * @throws InterruptedException  si la petición HTTP es interrumpida
     * @throws URISyntaxException    si la URL construida es inválida
     */
    public PageOrder getOrders(String page, String maxPerPage) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URIBuilder(baseUrl + "/orders")
                .addParameter("page", page)
                .addParameter("max-per-page", maxPerPage)
                .build();

        String url = uri.toString();

        return getOrders(url);
    }

    /**
     * Obtiene una página de pedidos a partir de una URL completa.
     * Realiza la petición HTTP GET y deserializa la respuesta JSON.
     *
     * @param url URL completa para obtener la página de pedidos
     * @return objeto {@link PageOrder} con los pedidos obtenidos
     * @throws IOException          si ocurre un error de entrada/salida
     * @throws InterruptedException si la petición HTTP es interrumpida
     */
    public PageOrder getOrders(String url) throws IOException, InterruptedException {
        String response = client.get(url,
                Map.of("Accept", "application/json",
                        "Accept-Encoding", "gzip"));

        return mapper.readValue(response, PageOrder.class);
    }
}
