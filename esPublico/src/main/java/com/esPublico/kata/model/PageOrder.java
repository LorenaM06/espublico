package com.esPublico.kata.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>Representa una página de resultados que contiene una lista de pedidos (Order).
 * Implementa Serializable para permitir la serialización del objeto.</p>
 *
 * <p>Contiene información sobre la página actual, el contenido (lista de pedidos)
 * y un conjunto de enlaces relacionados, que pueden ser usados para navegación o referencia.</p>
 *
 */
@Getter
@Setter
public class PageOrder implements Serializable {

    /**
     * Número de la página actual.
     */
    private int page;

    /**
     * Lista de pedidos contenidos en esta página.
     */
    private List<Order> content;

    /**
     * Mapa de enlaces relacionados con esta página (por ejemplo, enlaces de navegación).
     */
    private Map<String, String> links;
}
