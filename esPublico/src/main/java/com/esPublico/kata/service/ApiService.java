package com.esPublico.kata.service;

import com.esPublico.kata.model.Order;
import com.esPublico.kata.model.PageOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiService {

    private static final HttpClientService client = new HttpClientService();;
    private static final String baseUrl = "https://kata-espublicotech.g3stiona.com/v1/orders";
    private static final ObjectMapper mapper = (new ObjectMapper()).setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public PageOrder getOrders(String page, String maxPerPage) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URIBuilder(baseUrl)
                .addParameter("page", page)
                .addParameter("max-per-page", maxPerPage)
                .build();

        String url = uri.toString();

        return getOrders(url);
    }

    public PageOrder getOrders(String url) throws IOException, InterruptedException {
        String response = client.get(url,
                Map.of("Accept", "application/json",
                        "Accept-Encoding", "gzip"));

        return mapper.readValue(response, PageOrder.class);
    }
}
