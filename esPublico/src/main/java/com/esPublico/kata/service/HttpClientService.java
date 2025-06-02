package com.esPublico.kata.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpClientService {

    private static final HttpClientService INSTANCE = new HttpClientService();

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static HttpClientService getInstance() {
        return INSTANCE;
    }

    // Método GET genérico
    public String get(String url, Map<String, String> headers) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        headers.forEach(requestBuilder::header);

        HttpRequest request = requestBuilder.build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        return handleResponse(response);
    }

    // Manejo básico de errores HTTP
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

